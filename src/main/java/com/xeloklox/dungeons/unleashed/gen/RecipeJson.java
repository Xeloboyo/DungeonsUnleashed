package com.xeloklox.dungeons.unleashed.gen;

import com.xeloklox.dungeons.unleashed.*;
import com.xeloklox.dungeons.unleashed.utils.*;
import net.minecraft.data.server.recipe.*;
import net.minecraft.recipe.*;
import org.json.*;
import org.mini2Dx.gdx.utils.*;

public abstract class RecipeJson extends JsonConfiguration{
    RecipeType type;
    String result;
    int resultam=1;

    public RecipeJson(RecipeType type, String path){
        super(Paths.recipes+path+".json", new JSONObject());
        this.type=type;
    }
    public RecipeJson setResult(String item, int am){
        resultam = am;
        result=item;
        return this;
    }
    public RecipeJson setResult(RegisteredItem item, int am){
        resultam = am;
        result=item.getJSONID();
        return this;
    }
    public RecipeJson setResult(RegisteredBlock item, int am){
        resultam = am;
        result=item.blockitem.getJSONID();
        return this;
    }

    @Override
    public void fillJSONObj(){
        try{
            json.put("type","minecraft:"+type.name());
            JSONObject res = new JSONObject();
            res.put("item",result);
            res.put("count",resultam);
            json.put("result",res);
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    enum RecipeType{
        crafting_shaped, crafting_shapeless, smelting
    }


    public static class ShapelessRecipeJson extends RecipeJson{
        String[] ingredients;
        public ShapelessRecipeJson( String path){
            super(RecipeType.crafting_shapeless, path);
        }
        public ShapelessRecipeJson setPattern(String... ingredients){
            this.ingredients=ingredients;
            return this;
        }
        @Override
        public void fillJSONObj(){
            super.fillJSONObj();
            try{
                JSONArray pat = new JSONArray();
                for(String ingredient : ingredients) pat.put(ingredient);
                json.put("ingredients", pat);
            }catch(JSONException e){
                e.printStackTrace();
            }
        }

    }

    public static class ShapedRecipeJson extends RecipeJson{
        String[] pattern;
        ObjectMap<String,String> keys = new ObjectMap<>();
        public ShapedRecipeJson(String path){
            super(RecipeType.crafting_shaped, path);
        }
        public ShapedRecipeJson setKeys(String... keyPairs){
            for(String s:keyPairs){
                String[] s2 = s.split("->");
                keys.put(s2[1].trim(),s2[0].trim());
            }
            return this;
        }
        public ShapedRecipeJson setPattern(String... pattern){
            this.pattern=pattern;
            return this;
        }

        @Override
        public void fillJSONObj(){
            super.fillJSONObj();
            try{
                JSONArray pat = new JSONArray();
                for(int i = 0; i < pattern.length; i++)
                    pat.put(pattern[i]);
                json.put("pattern", pat);

                JSONObject keyjson = new JSONObject();
                keys.forEach(entry->{
                    try{
                        JSONObject key = new JSONObject();
                        key.put("item",entry.value);
                        keyjson.put(entry.key,key);
                    }catch(JSONException ignored){}
                });
                json.put("key", keyjson);

            }catch(JSONException e){
                e.printStackTrace();
            }
        }
    }
}
