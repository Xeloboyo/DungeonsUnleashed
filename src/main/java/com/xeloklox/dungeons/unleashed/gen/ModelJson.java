package com.xeloklox.dungeons.unleashed.gen;

import com.xeloklox.dungeons.unleashed.*;
import com.xeloklox.dungeons.unleashed.utils.*;
import net.minecraft.client.model.*;
import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.*;
import net.minecraft.util.*;
import org.apache.commons.io.*;
import org.json.*;
import org.mini2Dx.gdx.utils.*;

import java.io.*;

import static com.xeloklox.dungeons.unleashed.DungeonsUnleashed.MODID;

public class ModelJson extends JsonConfiguration implements JsonModelWrapper{
    public JSONObject config;
    public Array<String> textureList = new Array<>();
    public Identifier id;
    JsonUnbakedModel jsonUnbakedModel;
    BakedModel model;
    String name;

    public ModelJson(String name, JSONObject template, JSONObject config){
        super(Paths.models + name + ".json", template);
        this.name=name;
        id = new Identifier(MODID+":"+name);
        this.config = config;
    }



    @Override
    public void fillJSONObj(){
        try{
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

    @Override
    public Identifier getIdentifier(){
        return id;
    }

    @Override
    public void setIdentifier(Identifier i){
        id=i;
    }

    @Override
    public JsonUnbakedModel getUnbaked(){
        return jsonUnbakedModel;
    }

    @Override
    public void setUnbaked(JsonUnbakedModel i){
        jsonUnbakedModel=i;
    }

    @Override
    public BakedModel getBaked(){
        return model;
    }

    @Override
    public void setBaked(BakedModel i){
        model=i;
    }

    public String getName(){
        return name;
    }
}
