package com.xeloklox.dungeons.unleashed.utils;

import com.xeloklox.dungeons.unleashed.*;
import com.xeloklox.dungeons.unleashed.gen.*;
import com.xeloklox.dungeons.unleashed.utils.models.*;
import org.json.*;
import org.mini2Dx.gdx.utils.*;

import java.io.*;
import java.util.stream.*;

import static com.xeloklox.dungeons.unleashed.DungeonsUnleashed.MODID;

public class BlockModelPresetBuilder{

    public static String generated(GeneratedModel generatedModel){
        JSONObject jo = new JSONObject();
        try{
            jo.put("template","dynamic");
            jo.put("name",generatedModel.name);
        }catch(JSONException e){
            e.printStackTrace();
        }

        return "@@"+jo.toString();
    }

    public static String allSidesSame(String name, String texture){
        JSONObject jo = new JSONObject();
        try{
            jo.put("template","block/templates/cube_all");
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
            jo.put("template","block/templates/cube_capped");
            jo.put("tex_0",texture);
            jo.put("tex_particle",texture);
            jo.put("name",name);
        }catch(JSONException e){
            e.printStackTrace();
        }
        return "@@"+jo.toString();
    }
    public static String directional(String name, String texture){
        JSONObject jo = new JSONObject();
        try{
            jo.put("template","block/templates/cube_directional");
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
            jo.put("template","block/templates/cube_bottom_top");
            jo.put("tex_top",top);
            jo.put("tex_bottom",bottom);
            jo.put("tex_side",side);
            jo.put("name",name);
        }catch(JSONException e){
            e.printStackTrace();
        }
        return "@@"+jo.toString();
    }

    private static String TopBottomSideTemplate(String template, String name, String top, String side, String bottom){
        JSONObject jo = new JSONObject();
        try{
            jo.put("template","block/templates/"+template);
            jo.put("tex_top",top);
            jo.put("tex_bottom",bottom);
            jo.put("tex_side",side);
            jo.put("name",name);
        }catch(JSONException e){
            e.printStackTrace();
        }
        return "@@"+jo.toString();
    }

    private static String WallTemplate(String template, String name, String tex){
        JSONObject jo = new JSONObject();
        try{
            jo.put("template","block/templates/"+template);
            jo.put("tex_wall",tex);
            jo.put("name",name);
        }catch(JSONException e){
            e.printStackTrace();
        }
        return "@@"+jo.toString();
    }
    private static String FenceTemplate(String template, String name, String tex){
        JSONObject jo = new JSONObject();
        try{
            jo.put("template","block/templates/"+template);
            jo.put("tex_texture",tex);
            jo.put("name",name);
        }catch(JSONException e){
            e.printStackTrace();
        }
        return "@@"+jo.toString();
    }

    public static String SlabTop(String name, String top, String side, String bottom){
        return TopBottomSideTemplate("slab_top",name,top,side,bottom);
    }
    public static String SlabBottom(String name, String top, String side, String bottom){
        return TopBottomSideTemplate("slab",name,top,side,bottom);
    }
    public static String Stairs(String name, String top, String side, String bottom){
        return TopBottomSideTemplate("stairs",name,top,side,bottom);
    }
    public static String StairsInner(String name, String top, String side, String bottom){
        return TopBottomSideTemplate("stairs_inner",name,top,side,bottom);
    }
    public static String StairsOuter(String name, String top, String side, String bottom){
        return TopBottomSideTemplate("stairs_outer",name,top,side,bottom);
    }
    public static String WallPost(String name, String tex){
        return WallTemplate("wall_post",name,tex);
    }
    public static String WallSide(String name, String tex){
        return WallTemplate("wall_side",name,tex);
    }
    public static String WallSideTall(String name, String tex){
        return WallTemplate("wall_side_tall",name,tex);
    }
    public static String WallInventory(String name, String tex){
        return WallTemplate("wall_inventory",name,tex);
    }
    public static String FencePost(String name, String tex){
           return FenceTemplate("fence_post",name,tex);
       }
    public static String FenceSide(String name, String tex){
       return FenceTemplate("fence_side",name,tex);
   }
    public static String FenceInventory(String name, String tex){
            return FenceTemplate("fence_inventory",name,tex);
        }

    //lmao idk
    public static String custom(String name){
        return name;
    }
    public static ModelJson getModelJson(String modelstr){
        try{
            JSONObject jo = new JSONObject(modelstr.substring(2));
            return new ModelJson(jo.getString("name"),getTemplate(jo.getString("template")),jo);
        }catch(JSONException e){
            e.printStackTrace();
        }
        return null;
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
        if(s==null){
            s = Strings.resourceAsString(Paths.models+name+".model");
        }

        try{
            templateMap.put(name,s);
            return new JSONObject(s);
        }catch(JSONException e){
            e.printStackTrace();
        }
        return null;
    }
}
