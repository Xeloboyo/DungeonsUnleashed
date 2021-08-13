package com.xeloklox.dungeons.unleashed.gen;

import com.xeloklox.dungeons.unleashed.*;
import org.apache.commons.io.*;
import org.json.*;
import org.mini2Dx.gdx.utils.*;

import java.io.*;

import static com.xeloklox.dungeons.unleashed.DungeonsUnleashed.MODID;

public class BlockModelJson extends JsonConfiguration{
    JSONObject config;
    Array<String> textureList = new Array<>();

    public BlockModelJson(String name, JSONObject template, JSONObject config){
        super(Paths.blockModel + name + ".json", template);
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
}
