package com.xeloklox.dungeons.unleashed.gen;

import com.xeloklox.dungeons.unleashed.utils.lambda.*;
import net.minecraft.state.property.*;
import org.json.*;
import org.mini2Dx.gdx.utils.*;

public class BlockStateBuilder{
    // this is not a multipart....
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


    //am i doin it right, i don feel like this is good programmin
    public static abstract class ModelList{
        Array<ModelVariant> list = new Array<>();
        public abstract void eachModelVariant(Cons<ModelVariant> c);
    }
    public static class ModelMultipart extends ModelList{
        public ModelVariant apply;
        public boolean or=false;
        Array<PartConditionSet> conditions = new Array<>();
        @Override
        public void eachModelVariant(Cons<ModelVariant> c){
            c.get(apply);
        }
        public ModelMultipart setModel(Func<ModelVariant, ModelVariant> func){
            apply = (func.get(new ModelVariant()));
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
        public ModelVariantList addModel(String modelstr,int y){
            addModel(model->model.setModel(modelstr).setY(y));
            return this;
        }

        public void eachModelVariant(Cons<ModelVariant> c){
            for(ModelVariant d:list){
                c.get(d);
            }
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
                data.put("y", y);
            }catch(JSONException e){
            }
            return this;
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
