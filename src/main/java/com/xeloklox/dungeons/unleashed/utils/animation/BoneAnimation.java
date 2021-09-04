package com.xeloklox.dungeons.unleashed.utils.animation;

import com.xeloklox.dungeons.unleashed.*;
import com.xeloklox.dungeons.unleashed.utils.*;
import com.xeloklox.dungeons.unleashed.utils.animation.Interpolations.*;
import com.xeloklox.dungeons.unleashed.utils.animation.TimeLine.*;
import com.xeloklox.dungeons.unleashed.utils.models.*;
import com.xeloklox.dungeons.unleashed.utils.models.RenderableModel.*;
import net.minecraft.util.math.*;
import org.json.*;
import org.mini2Dx.gdx.utils.*;

public class BoneAnimation{
    String name;
    float length=1;
    RenderableModel model;
    public boolean loops = false;
    Array<BoneAnimator> animators = new Array<>();

    public static JSONObject getAnimation(String name){
        String s = Strings.resourceAsString(Paths.animations+name+".json");
        try{
            return new JSONObject(s);
        }catch(JSONException e){
            e.printStackTrace();
        }
        return null;
    }
    public BoneAnimation(String name,JSONObject object, RenderableModel model) throws JSONException{

        this.name=name;
        if(object.has("loop")){
            loops = object.getBoolean("loop");
        }
        length = (float)object.getDouble("animation_length");
        this.model=model;
        JSONObject bones = object.getJSONObject("bones");
        bones.keys().forEachRemaining(k->{
            String key = (String)k;
            BoneAnimator animator = new BoneAnimator();
            animator.boneName = key;
            try{
                JSONObject bone = bones.getJSONObject(key);
                if(bone.has("scale") && bone.get("scale") instanceof JSONObject){
                    JSONObject scale = bone.getJSONObject("scale");
                    loadTimeLine(animator.scale,scale);
                }
                if(bone.has("position")){
                    JSONObject position = bone.getJSONObject("position");
                    loadTimeLine(animator.position,position);
                    for(var f:animator.position.keyframes){
                        f.value.scale(1/16f);
                    }
                }
                if(bone.has("rotation")){
                    JSONObject rotation = bone.getJSONObject("rotation");
                    loadTimeLine(animator.rotation,rotation);
                    for(var f:animator.rotation.keyframes){
                        f.value.multiplyComponentwise(1,-1,-1);
                    }
                }
            }catch(JSONException e){
                e.printStackTrace();
            }
            animators.add(animator);

        });
    }

    public static void loadTimeLine(TimeLine<Vec3f> line, JSONObject linejson){
        linejson.keys().forEachRemaining(t->{
            String timestr = (String)t;
            float time = Float.parseFloat(timestr);
            try{
                if(linejson.get(timestr) instanceof JSONArray){
                    float[] keyf = Utils.floatArray(linejson.getJSONArray(timestr));
                    line.add(new Vec3f(keyf[0],keyf[1],keyf[2]),time);
                }else {
                    JSONObject keyfjson = linejson.getJSONObject(timestr);
                    float[] keyf = Utils.floatArray(keyfjson.getJSONArray("post"));
                    var keyfobj = line.add(new Vec3f(keyf[0],keyf[1],keyf[2]),time);
                    keyfobj.interpolateType = keyfjson.get("lerp_mode").equals("catmullrom")? WeightedInterpolateType.CATMULL_SPLINE:SingularInterpolateType.LINEAR;
                }
            }catch(JSONException e){
                e.printStackTrace();
            }
        });

    }

    public void animate(float t,ObjectMap<String,BoneTranslationParameters> parameters){
        if(loops){
            t %= length;
        }
        for(BoneAnimator bone:animators){
            bone.get(t,parameters);
        }
    }

    public float getLength(){
        return length;
    }

    public String getName(){
        return name;
    }

    class BoneAnimator{
        String boneName;
        Vec3fTimeLine scale = new Vec3fTimeLine();
        Vec3fTimeLine position = new Vec3fTimeLine();
        Vec3fTimeLine rotation = new Vec3fTimeLine();

        public void get(float t,ObjectMap<String,BoneTranslationParameters> parameters){
            if(!parameters.containsKey(boneName)){return;}
            if(!scale.empty){
                parameters.get(boneName).scale.set(scale.get(t));
            }
            if(!position.empty){
                parameters.get(boneName).offset.set(position.get(t));
            }
            if(!rotation.empty){
                parameters.get(boneName).rotation.set(rotation.get(t));
            }
        }
    }


}
