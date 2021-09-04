package com.xeloklox.dungeons.unleashed.utils.animation;

import com.xeloklox.dungeons.unleashed.utils.animation.Interpolations.*;
import org.mini2Dx.gdx.utils.*;

public class ParameterMap{
    Array<Interpolator> interops = new Array<>();
    ObjectMap<String, Wrapper> vals = new ObjectMap<>();
    public String key;

    public Object o(String k){
        return vals.get(k).val;
    }

    public float f(String k){
        return (Float)vals.get(k).val;
    }

    public void f(String k, float d){
        vals.get(k).val = d;
    }

    public int i(String k){
        return (Integer)vals.get(k).val;
    }

    public String s(String k){
        return vals.get(k).val.toString();
    }

    public void s(String k, String d){
        vals.get(k).val = d;
    }

    public int[] ia(String k){
        return (int[])vals.get(k).val;
    }

    public <T> void add(String name, T value){
        vals.put(name, new Wrapper<T>(value));
    }

    public <T> void addInterpolator(SingularInterpolateType type, float w, String key, Interpolate<T> interp, T target){
        interops.add(new Interpolator<T>(this, type, w, key, interp, target));
    }

    public<T> void addChainedInterpolator(SingularInterpolateType type, float w, String key, Interpolate<T> interp, T... target){
        FrameState<T>[] fs = new FrameState[target.length];
        for(int i = 0; i < target.length; i++){
            fs[i] = new FrameState<>(target[i]);
        }
        interops.add(new ChainedInterpolator<T>(this, type, w, key, interp, fs));
    }
    public<T> void addChainedInterpolator(SingularInterpolateType type, float w, String key, Interpolate<T> interp, FrameState<T>... fs){
       interops.add(new ChainedInterpolator<>(this, type, w, key, interp, fs));
   }

    public void removeInterops(){
        for(Interpolator i : interops){
            i.finalise();
        }
        interops.clear();
    }

    public void update(float t){
        for(Interpolator i : interops){
            i.update(t);
        }
    }

    public void initAnimations(){ }

    public void preStep(){ }

    ParameterMap getElement(){
        return this;
    }
}
