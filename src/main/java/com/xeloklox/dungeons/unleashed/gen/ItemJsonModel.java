package com.xeloklox.dungeons.unleashed.gen;

import com.xeloklox.dungeons.unleashed.*;
import com.xeloklox.dungeons.unleashed.utils.*;
import com.xeloklox.dungeons.unleashed.utils.lambda.*;
import com.xeloklox.dungeons.unleashed.utils.models.*;
import net.minecraft.item.*;
import org.apache.commons.io.*;
import org.json.*;
import org.mini2Dx.gdx.utils.*;

import java.io.*;

import static com.xeloklox.dungeons.unleashed.DungeonsUnleashed.MODID;

// this is only run in preprocessing.
public class ItemJsonModel extends JsonConfiguration{
    RegisteredItem item;
    public ModelParent modelParent = ModelParent.ITEM_GENERATED;
    public String[] textureLayers;
    public Array<ItemModelOverride> overrides = new Array<>();


    public ItemJsonModel(RegisteredItem item){
        this(item,"item/"+item.id,new String[]{});
        if(item.get() instanceof BlockItem){
            modelParent = ModelParent.BLOCK;
            textureLayers = new String[]{};
        }else{
            textureLayers = new String[]{"item/" + item.id};
        }
    }

    public ItemJsonModel(RegisteredItem item,String name,String[] textureLayers){
        super(Paths.models + name + ".json", new JSONObject());
        this.item = item;
        this.textureLayers=textureLayers;
    }

    @Override
    public void fillJSONObj(){
        try{
            json.put("parent", format(modelParent.key,item.id));
            if(item.get() instanceof BlockItem){
                String modelpath = Paths.blockModel+item.id+".json";
                if(!AssetGenerator.isQueued(modelpath) && !ModelProvider.hasModel("block/"+item.id)){
                    File cust = new File(Paths.blockModel+"custom/"+item.id+".json");
                    if(!cust.exists()){
                        System.out.println("[WARNING] Block model for BlockItem "+item.id+" was not found!");
                        JSONObject config = new JSONObject(BlockModelPresetBuilder.allSidesSame(item.id,"block/default").substring(2));
                        JSONObject template = BlockModelPresetBuilder.getTemplate(config.getString("template"));
                        new ModelJson("block/"+item.id,template, config);
                    }else{
                        json.put("parent", format(modelParent.key,"/custom/"+item.id));
                    }
                }

            }


            if(textureLayers.length > 0){
                JSONObject models = new JSONObject();
                for(int i = 0; i < textureLayers.length; i++){
                    models.put("layer" + i, MODID + ":" + textureLayers[i]);
                }
                json.put("textures", models);
            }
            if(overrides.size>0){
                JSONArray ojson = new JSONArray();
                for(ItemModelOverride override:overrides){
                    ojson.put(override.getObj());
                }
                json.put("overrides", ojson);
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    public String format(String key,String id){

        key = key.replace("@ID@", id);
        return key.replace("@MODID@", MODID);
    }

    //if theres no texture, replace it with the default texture.
    @Override
    public void postGenerate(){
        File defaultTex = new File(Paths.itemTexture_DEFAULT);
        for(int i = 0; i < textureLayers.length; i++){
            File tex = new File(Paths.texture + textureLayers[i] + ".png");
            if(!tex.exists()){
                try{
                    System.out.println("[WARNING] Unable to find "+tex+", replacing with placeholder...");
                    FileUtils.copyFile(defaultTex, tex);
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
        for(ItemModelOverride override:overrides){
            if(!AssetGenerator.isQueued(Paths.models+override.model)){
                new ItemJsonModel(item,override.model,new String[]{override.model});
            }
        }
    }

    public ItemJsonModel setTextureLayers(String... textureLayers){
        this.textureLayers = textureLayers;
        return this;
    }

    public ItemJsonModel setModelParent(ModelParent modelParent){
        this.modelParent = modelParent;
        return this;
    }

    public RegisteredItem getItem(){
        return item;
    }

    public ItemJsonModel addOverride(Func<ItemModelOverride,ItemModelOverride> func){
        overrides.add(func.get(new ItemModelOverride()));
        return this;
    }

    public static class ItemModelOverride{
        public ObjectMap<String,Float> predicates = new ObjectMap<>();
        public String model;
        public ItemModelOverride addModelPredicate(String name, float value){
            predicates.put(name,value);
            return this;
        }

        public ItemModelOverride setModel(String model){
            this.model = model;
            return this;
        }

        public JSONObject getObj() throws JSONException{
            JSONObject obj = new  JSONObject();
            obj.put("model",MODID+":"+model);
            JSONObject predicate = new  JSONObject();
            predicates.forEach(entry -> {
                try{predicate.put(entry.key,entry.value);}catch(JSONException ignored){}
            });
            obj.put("predicate",predicate);
            return obj;
        }
    }

    public enum ModelParent{
        /**
         * held like a normal item
         */
        ITEM_GENERATED("item/generated"),
        /**
         * held like a tool
         **/
        ITEM_HANDHELD("item/handheld"),
        /**
         * for block items
         **/
        BLOCK("@MODID@:block/@ID@");
        String key;

        ModelParent(String key){
            this.key = key;
        }
    }
}
