package com.xeloklox.dungeons.unleashed.gen;

import com.xeloklox.dungeons.unleashed.utils.animation.Interpolations.*;
import com.xeloklox.dungeons.unleashed.utils.lambda.*;
import com.xeloklox.dungeons.unleashed.utils.lambda.Cons.*;
import net.minecraft.state.property.*;
import org.json.*;
import org.mini2Dx.gdx.utils.*;

import java.util.*;

public class BlockStateBuilder{
    public ObjectMap<String, ModelList> map = new ObjectMap<>();
    boolean noState = false;
    boolean multipart = false;


    public static BlockStateBuilder create(){
        return new BlockStateBuilder();
    }
    public static BlockStateBuilder createMultipart(){
        var b =  new BlockStateBuilder();
        b.multipart=true;
        return b;
    }

    public BlockStateBuilder stateCombinations(Cons2<PropertyCombinator,ModelVariantList> cons, Property...properties){
        PropertyCombinator comb = new PropertyCombinator(properties);
        for(int i=0;i<comb.max;i++){
            ModelVariantList ml = new ModelVariantList();
            cons.get(comb,ml);
            map.put(comb.getKey(), ml);
            comb.next();
        }
        return this;
    }

    public <T extends Comparable<T>> BlockStateBuilder addStateCombination(Property<T> property, Cons2<T, ModelVariantList> cons){
        if(multipart){
            throw new IllegalStateException("Multiparts dont have variant states");
        }
        ObjectMap<String, ModelList> newmap = new ObjectMap<>();
        property.getValues().forEach(p->{
            if(map.isEmpty()){
                ModelVariantList ml = new ModelVariantList();
                cons.get(p,ml);
                newmap.put(property.getName()+"="+p, ml);
            }else{
                map.forEach(entry->{
                    ModelVariantList ml = (ModelVariantList)entry.value.clone();
                    cons.get(p,ml);
                    newmap.put(property.getName()+"="+p+","+entry.key, ml);
                });
            }
        });
        map = newmap;
        return this;
    }

    public <T extends Comparable<T>> BlockStateBuilder addStateVariant(Property<T> prop, T value, Func<ModelVariantList, ModelVariantList> func) {
        if(noState){
            throw new IllegalStateException("Blocks with no BlockState cannot suddenly have a state variant.");
        }
        if(multipart){
            throw new IllegalStateException("Multiparts dont have variant states");
        }
        String key = prop.getName() + "=" + value;
        ModelVariantList ml = new ModelVariantList();
        map.put(key, func.get(ml));
        return this;
    }

    public BlockStateBuilder noState(Func<ModelVariantList, ModelVariantList> func){
        if(!map.isEmpty()){
            throw new IllegalStateException("Blocks with a BlockState cannot suddenly have no state");
        }
        ModelVariantList ml = new ModelVariantList();
        map.put("", func.get(ml));
        noState = true;
        return this;
    }

    public <T extends Comparable<T>> BlockStateBuilder addPart(Func<ModelMultipart, ModelMultipart> func) {
        if(!multipart){
            throw new IllegalStateException("Block needs to be multipart");
        }
        if(noState){
            throw new IllegalStateException("Blocks with no BlockState cannot suddenly have a state.");
        }

        String key = ""+Math.random();
        ModelMultipart ml = new ModelMultipart();
        map.put(key, func.get(ml));
        return this;
    }

    public ObjectMap<String, ModelList> build(){
        return map;
    }


    public static class PropertyCombinator{
        ObjectMap<Property,PropertyCombinatorEntry> map = new ObjectMap<>();
        Array<PropertyCombinatorEntry> entries = new Array<>();
        int current=0;
        int max = 1;
        PropertyCombinator(Property...props){
            for(Property p:props){
                var pce = new PropertyCombinatorEntry(p);
                map.put(p,pce);
                entries.add(pce);
                max*=pce.array.size;
            }
        }

        public <T extends Comparable<T>> T get(Property<T> property){
            return (T)map.get(property).get();
        }
        //facing=east,half=bottom,shape=outer_right
        String getKey(){
            final StringBuilder output =new StringBuilder();
            map.forEach(e->{
                output.append((output.length()==0?"":",")+e.key.getName()+"="+e.value.get().toString().toLowerCase(Locale.ROOT));
            });
            return output.toString();
        }

        void next(){
            current++;
            int relative= current;
            for(PropertyCombinatorEntry p:entries){
                p.current = relative%p.array.size;
                relative/=p.array.size;
            }
        }


        static class PropertyCombinatorEntry<T extends Comparable<T>>{
            Array<T> array;
            int current = 0;
            PropertyCombinatorEntry(Property<T> p){
                array = new Array<>();
                p.getValues().forEach(a->array.add(a));
            }
            T get(){ return array.get(current);}

        }
    }


    //am i doin it right, i don feel like this is good programmin
    public static abstract class ModelList{
        Array<ModelVariant> list = new Array<>();
        public abstract void eachModelVariant(Cons<ModelVariant> c);
        public ModelVariant getFirst(){
            final Wrapper<ModelVariant> wrapper = new Wrapper<>(null);
            eachModelVariant(c->{
                if(wrapper.val==null){
                    wrapper.val = c;
                }
            });
            return wrapper.val;
        }
        public abstract ModelList clone();
    }
    public static class ModelMultipart extends ModelList{
        public ModelVariant apply;
        public boolean or=false;
        Array<PartConditionSet> conditions = new Array<>();
        @Override
        public void eachModelVariant(Cons<ModelVariant> c){
            c.get(apply);
        }

        @Override
        public ModelList clone(){
            ModelMultipart m = new ModelMultipart();
            m.conditions.addAll(conditions);
            m.or=or;
            return m;
        }

        public ModelMultipart setModel(Func<ModelVariant, ModelVariant> func){
            apply = (func.get(new ModelVariant()));
            return this;
        }
        public ModelMultipart setModel(String model){
            apply = ((new ModelVariant()).setModel(model));
            return this;
        }
        public ModelMultipart OR(){
            or=true;
            return this;
        }
        public<T extends Comparable<T>> ModelMultipart addConditions(Func<PartConditionSet,PartConditionSet> func){
            conditions.add(func.get(new PartConditionSet()));
            return this;
        }
        public static class PartConditionSet{
            Array<PartCondition> conditions = new Array<>();
            public<T extends Comparable<T>> PartConditionSet set(Property<T> prop, T...values){
               conditions.add(new PartCondition<>(prop, values));
               return this;
            }
            public JSONObject getJson() throws JSONException{
                JSONObject obj = new JSONObject();
                for(var cond:conditions){
                    StringBuilder val = new StringBuilder();
                    for(var v:cond.values){
                        val.append(val.length() == 0 ? "" : "|").append(v);
                    }
                    obj.put(cond.property.getName(), val.toString());
                }
                return obj;
            }
        }

        public static class PartCondition<T extends Comparable<T>>{
            Property<T> property;
            T[] values;

            PartCondition(Property<T> property,T...values){
                this.property=property;
                this.values=values;
            }


        }

    }
    public static class ModelVariantList extends ModelList{
        Array<ModelVariant> list = new Array<>();

        public ModelVariantList addModel(Func<ModelVariant, ModelVariant> func){
            list.add(func.get(new ModelVariant()));
            return this;
        }
        public ModelVariantList addModel(String modelstr){
            addModel(model->model.setModel(modelstr));
            return this;
        }
        public ModelVariantList addModel(String modelstr,int y){
            addModel(model->model.setModel(modelstr).setY(y));
            return this;
        }
        public ModelVariantList addModel(String modelstr,int x,int y){
            addModel(model->model.setModel(modelstr).setX(x).setY(y));
            return this;
        }
        public void eachModelVariant(Cons<ModelVariant> c){
            for(ModelVariant d:list){
                c.get(d);
            }
        }

        @Override
        public ModelList clone(){
            ModelVariantList m = new ModelVariantList();
            list.forEach(mv->{
                try{
                    m.list.add(new ModelVariant(new JSONObject(mv.data.toString())));
                }catch(JSONException e){
                    e.printStackTrace();
                }
            });
            return m;
        }

        @Override
        public String toString(){
            return "ModelVariantList{" +
            "list=" + list +
            '}';
        }
    }

    public static class ModelVariant{
        public JSONObject data = new JSONObject();

        public ModelVariant(){
        }

        public ModelVariant(JSONObject data){
            this.data = data;
        }

        public String getModel(){
            try{
                return data.getString("model");
            }catch(JSONException e){
            }
            return null;
        }

        public ModelVariant setModel(String model){
            try{
                data.put("model", model);
            }catch(JSONException e){
            }
            return this;
        }

        public ModelVariant setX(int x){
            try{
                data.put("x", x);
            }catch(JSONException e){
            }
            return this;
        }

        public ModelVariant setY(int y){
            try{
                data.put("y", y%360);
            }catch(JSONException e){
            }
            return this;
        }
        public int getY(){
            if(!data.has("y")){return 0;}
            try{
                return data.getInt("y");
            }catch(JSONException e){
            }
            return 0;
        }


        public ModelVariant setZ(int z){
            try{
                data.put("z", z);
            }catch(JSONException e){
            }
            return this;
        }

        public ModelVariant setUVLock(boolean z){
            try{
                data.put("uvlock", z);
            }catch(JSONException e){
            }
            return this;
        }
    }


}
