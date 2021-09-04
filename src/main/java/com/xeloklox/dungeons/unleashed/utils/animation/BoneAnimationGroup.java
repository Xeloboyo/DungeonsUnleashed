package com.xeloklox.dungeons.unleashed.utils.animation;

import com.xeloklox.dungeons.unleashed.utils.models.*;
import org.json.*;
import org.mini2Dx.gdx.utils.*;

public class BoneAnimationGroup{
    public ObjectMap<String, BoneAnimation> animations = new ObjectMap<>();

    public BoneAnimationGroup(JSONObject jsonObject, RenderableModel model){
        try{
            JSONObject animationsjson = jsonObject.getJSONObject("animations");
            animationsjson.keys().forEachRemaining(k -> {
                String key = (String)k;
                try{
                    animations.put(key, new BoneAnimation(key, animationsjson.getJSONObject(key), model));
                }catch(JSONException e){
                    e.printStackTrace();
                }
            });
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    public BoneAnimation get(String name){
        return animations.get(name);
    }

}
