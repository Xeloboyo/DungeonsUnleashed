package com.xeloklox.dungeons.unleashed.gen;

import com.xeloklox.dungeons.unleashed.utils.*;
import com.xeloklox.dungeons.unleashed.gen.BlockStateBuilder.*;
import net.minecraft.state.property.*;
import org.json.*;
import org.mini2Dx.gdx.utils.*;

public class BlockStateBuilder{
    // this is not a multipart....
    public ObjectMap<String, ModelVariantList> map = new ObjectMap<>();
    boolean noState = false;

    public static BlockStateBuilder create(){
        return new BlockStateBuilder();
    }

    public <T extends Comparable<T>> BlockStateBuilder addStateVariant(Property<T> prop, T value, Func<ModelVariantList, ModelVariantList> func) throws IllegalBlockStateException{
        if(noState){
            throw new IllegalBlockStateException("Blocks with no BlockState cannot suddenly have a state variant.");
        }
        String key = prop.getName() + "=" + value;
        ModelVariantList ml = new ModelVariantList();
        map.put(key, func.get(ml));
        return this;
    }

    public BlockStateBuilder noState(Func<ModelVariantList, ModelVariantList> func){
        ModelVariantList ml = new ModelVariantList();
        map.put("", func.get(ml));
        noState = true;
        return this;
    }

    public ObjectMap<String, ModelVariantList> build(){
        return map;
    }

    public static class IllegalBlockStateException extends Exception{
        public IllegalBlockStateException(String message){
            super(message);
        }
    }

    //am i doin it right, i don feel like this is good programmin
    public static class ModelVariantList{
        Array<ModelVariant> list = new Array<>();

        public ModelVariantList addModel(Func<ModelVariant, ModelVariant> func){
            list.add(func.get(new ModelVariant()));
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
