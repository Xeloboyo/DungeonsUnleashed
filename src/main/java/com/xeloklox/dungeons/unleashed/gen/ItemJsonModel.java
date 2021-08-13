package com.xeloklox.dungeons.unleashed.gen;

import com.xeloklox.dungeons.unleashed.*;
import com.xeloklox.dungeons.unleashed.utils.*;
import com.xeloklox.dungeons.unleashed.gen.ItemJsonModel.*;
import net.minecraft.item.*;
import org.apache.commons.io.*;
import org.json.*;

import java.io.*;

import static com.xeloklox.dungeons.unleashed.DungeonsUnleashed.MODID;

// this is only run in preprocessing.
public class ItemJsonModel extends JsonConfiguration{
    RegisteredItem item;
    public ModelParent modelParent = ModelParent.ITEM_GENERATED;
    public String[] textureLayers;

    public ItemJsonModel(RegisteredItem item){
        super(Paths.itemModel + item.id + ".json", new JSONObject());
        this.item = item;
        if(item.get() instanceof BlockItem){
            modelParent = ModelParent.BLOCK;
            textureLayers = new String[]{};
        }else{
            textureLayers = new String[]{"item/" + item.id};
        }
    }

    @Override
    public void fillJSONObj(){
        try{
            json.put("parent", format(modelParent.key));
            if(textureLayers.length > 0){
                JSONObject models = new JSONObject();
                for(int i = 0; i < textureLayers.length; i++){
                    models.put("layer" + i, MODID + ":" + textureLayers[i]);
                }
                json.put("textures", models);
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    public String format(String key){

        key = key.replace("@ID@", item.id);
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
    }

    public RegisteredItem getItem(){
        return item;
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
