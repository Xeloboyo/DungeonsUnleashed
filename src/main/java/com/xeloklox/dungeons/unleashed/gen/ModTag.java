package com.xeloklox.dungeons.unleashed.gen;

import com.xeloklox.dungeons.unleashed.*;
import com.xeloklox.dungeons.unleashed.utils.*;
import org.json.*;
import org.lwjgl.system.CallbackI.*;
import org.mini2Dx.gdx.utils.*;

import static com.xeloklox.dungeons.unleashed.DungeonsUnleashed.MODID;

public class ModTag<T> extends JsonConfiguration{
    TagDomain domain;
    String name;
    TagCategory category;
    Array<Registerable<T>> values = new Array<>();
    /// TagRegistry.block(new Identifier("examplemod", "example_ores"));

    public void add(Registerable<T> t){
        values.add(t);
    }

    private static String getPath(TagDomain domain, String name, TagCategory category){
        return Paths.data+domain.path+"/"+"tags/"+category.name()+"/"+name+".json";
    }

    public ModTag(TagDomain domain, String name, TagCategory category){
        super(getPath(domain,name,category), new JSONObject());
        this.domain = domain;
        this.name = name;
        this.category = category;
    }

    @Override
    public void fillJSONObj(){
        try{
            json.put("replace",false);
            JSONArray valuesJson = new JSONArray();
            for(Registerable<T> r:values){
                valuesJson.put(r.getJSONID());
            }
            json.put("values",valuesJson);
        }catch(JSONException e){
            e.printStackTrace();
        }
    }
    public enum TagDomain{
        common("c"),minecraft("minecraft"),mod(MODID);
        String path;
        TagDomain(String path){
            this.path = path;
        }
    }
    public enum TagCategory{
        blocks,items,fluids;
    }
}
