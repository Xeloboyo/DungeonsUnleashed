package com.xeloklox.dungeons.unleashed.gen;

import org.json.*;

import java.io.*;
import java.util.*;

public abstract class JsonConfiguration extends Generator{
    public static HashSet<String> generated = new HashSet<>();
    protected String path;
    protected JSONObject json;

    public JsonConfiguration(String path, JSONObject object){
        super();
        this.path = path;
        this.json = object;
    }
    //fills the JSONObject with data to be written to a file
    public abstract void fillJSONObj();
    public void postGenerate(){ }

    public void generateFile() throws IOException{
        if(generated.contains(path)){
            return;
        }
        System.out.println("Generating JSON: "+this.path );
        fillJSONObj();
        File f = new File(path);
        if(!f.exists()){
            f.createNewFile();
        }
        PrintWriter writer = new PrintWriter(f);
        try{
            writer.write(json.toString(4));
        }catch(JSONException e){
            e.printStackTrace();
        }
        writer.flush();
        writer.close();
        postGenerate();
        generated.add(path);
    }


}
