package com.xeloklox.dungeons.unleashed.utils;

import org.json.*;
import org.mini2Dx.gdx.utils.*;

import java.io.*;

public interface IHasName{
    public static final Array<IHasName> names= new Array<>();
    public static void generateLang(String path){
        File  f = new File(path);

        try{
            if(!f.exists()){
                f.createNewFile();
            }
            FileWriter fw =new FileWriter(f);
            JSONObject jsonObject = new JSONObject();
            for(IHasName name:names){
                jsonObject.put(name.getNameID(),name.getName());
            }
            fw.write(jsonObject.toString(4));
            fw.flush();
            fw.close();
        }catch(IOException | JSONException e){
            e.printStackTrace();
        }
    }


    String getName();
    String getNameID();
}
