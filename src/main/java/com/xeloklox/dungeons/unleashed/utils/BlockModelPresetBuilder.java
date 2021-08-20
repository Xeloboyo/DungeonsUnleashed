package com.xeloklox.dungeons.unleashed.utils;

import com.xeloklox.dungeons.unleashed.*;
import org.json.*;
import org.mini2Dx.gdx.utils.*;

import java.io.*;
import java.util.stream.*;

import static com.xeloklox.dungeons.unleashed.DungeonsUnleashed.MODID;

public class BlockModelPresetBuilder{
    public static String allSidesSame(String name, String texture){
        JSONObject jo = new JSONObject();
        try{
            jo.put("template","block/cube_all");
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
            jo.put("template","block/cube_capped");
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
            jo.put("template","block/cube_bottom_top");
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
        return "@@"+customTemplateObj(template,name,tex).toString();
    }
    public static JSONObject customTemplateObj(String template,String name,String tex){
        JSONObject jo = new JSONObject();
        try{
            jo.put("template",template);
            jo.put("tex_particle",tex);
            jo.put("tex_0",tex);
            jo.put("name",name);
        }catch(JSONException e){
            e.printStackTrace();
        }
        return jo;
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
        String s = Strings.resourceAsString(Paths.models+name+".json");

        try{
            templateMap.put(name,s);
            return new JSONObject(s);
        }catch(JSONException e){
            e.printStackTrace();
        }
        return null;
    }
}
