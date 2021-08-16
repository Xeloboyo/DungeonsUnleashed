package com.xeloklox.dungeons.unleashed.utils;

import com.xeloklox.dungeons.unleashed.*;
import org.json.*;
import org.mini2Dx.gdx.utils.*;

import java.io.*;
import java.util.stream.*;

import static com.xeloklox.dungeons.unleashed.DungeonsUnleashed.MODID;

public class BlockModel{
    public static String allSidesSame(String name, String texture){
        JSONObject jo = new JSONObject();
        try{
            jo.put("template","cube_all");
            jo.put("tex_all",texture);
            jo.put("name",name);
        }catch(JSONException e){
            e.printStackTrace();
        }

        return "@@"+jo.toString();
    }
    public static String cappedTopBottom(String name, String texture){
        JSONObject jo = new JSONObject();
        try{
            jo.put("template","cube_capped");
            jo.put("tex_0",texture);
            jo.put("tex_particle",texture);
            jo.put("name",name);
        }catch(JSONException e){
            e.printStackTrace();
        }
        return "@@"+jo.toString();
    }
    public static String TopBottomSide(String name, String top, String side, String bottom){
        JSONObject jo = new JSONObject();
        try{
            jo.put("template","cube_bottom_top");
            jo.put("tex_top",top);
            jo.put("tex_bottom",bottom);
            jo.put("tex_side",side);
            jo.put("name",name);
        }catch(JSONException e){
            e.printStackTrace();
        }
        return "@@"+jo.toString();
    }

    //lmao idk
    public static String custom(String name){
        return name;
    }

    public static String customTemplate(String template,String name,String tex){
        JSONObject jo = new JSONObject();
        try{
            jo.put("template",template);
            jo.put("tex_particle",tex);
            jo.put("tex_0",tex);
            jo.put("name",name);
        }catch(JSONException e){
            e.printStackTrace();
        }
        return "@@"+jo.toString();
    }


    static ObjectMap<String,String> templateMap = new ObjectMap<>();
    public static JSONObject getTemplate(String name) {
        if(templateMap.containsKey(name)){
            try{
                return new JSONObject(templateMap.get(name));
            }catch(JSONException e){
                e.printStackTrace();
            }
        }
        File f = new File(Paths.blockModel+name+".json");
        try{
            BufferedReader fr = new BufferedReader(new FileReader(f));
            String s = fr.lines().collect(Collectors.joining());
            templateMap.put(name,s);
            return new JSONObject(s);
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }catch(JSONException e){
            e.printStackTrace();
        }
        return null;
    }
}
