package com.xeloklox.dungeons.unleashed.gen;

import com.xeloklox.dungeons.unleashed.*;
import com.xeloklox.dungeons.unleashed.utils.*;
import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.*;
import net.minecraft.util.*;
import org.apache.commons.io.*;
import org.json.*;
import org.mini2Dx.gdx.utils.*;

import java.io.*;

import static com.xeloklox.dungeons.unleashed.DungeonsUnleashed.MODID;

public class ModelJson extends JsonConfiguration{
    public JSONObject config;
    public Array<String> textureList = new Array<>();
    public Identifier id;
    JsonUnbakedModel jsonUnbakedModel;
    BakedModel model;
    String name;
    public boolean isBedrock = false;

    public ModelJson(String name, JSONObject template, JSONObject config){
        super(Paths.models + name + ".json", template);
        this.name=name;
        id = new Identifier(MODID+":"+name);
        this.config = config;
    }


    public static ModelJson getModel(String name, String tex){
        return new ModelJson(
            name,
            BlockModelPresetBuilder.getTemplate(name),
            BlockModelPresetBuilder.customTemplateObj(name,"",tex)
        );
    }



    @Override
    public void fillJSONObj(){
        try{
            if(json.has("minecraft:geometry")){
                //bedrock model.
                isBedrock = true;
                json.put("textures",config);
                return;
            }
            JSONObject textures = json.getJSONObject("textures");
            Array<String> keys = new Array<>();
            textures.keys().forEachRemaining(c -> {
                keys.add((String)c);
            });
            for(String key : keys){
                if(config.has("tex_" + key)){
                    textureList.add(config.getString("tex_" + key));
                    textures.put(key, MODID + ":" + config.getString("tex_" + key));
                }
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public void postGenerate(){
        File defaultTex = new File(Paths.blockTexture_DEFAULT);
        for(int i = 0; i < textureList.size; i++){
            File tex = new File(Paths.texture + textureList.get(i) + ".png");
            if(!tex.exists()){
                try{
                    System.out.println("[WARNING] Unable to find " + tex + ", replacing with placeholder...");
                    FileUtils.copyFile(defaultTex, tex);
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    public String getName(){
        return name;
    }
}
