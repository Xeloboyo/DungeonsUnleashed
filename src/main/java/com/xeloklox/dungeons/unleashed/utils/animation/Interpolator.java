package com.xeloklox.dungeons.unleashed.utils.animation;

import com.xeloklox.dungeons.unleashed.utils.animation.Interpolations.*;

public class Interpolator<T>{
    ParameterMap ae;
    SingularInterpolateType type;
    float w;
    String key;
    Interpolate<T> interp;
    T o1, o2;

    public Interpolator(ParameterMap ae, SingularInterpolateType type, float w, String key, Interpolate<T> interp, T targ){
        this.ae = ae;
        this.type = type;
        this.w = w;
        this.key = key;
        this.interp = interp;
        initOrigin();
        o2 = targ;
    }

    protected Interpolator(ParameterMap ae, SingularInterpolateType type, float w, String key, Interpolate<T> interp){
        this.ae = ae;
        this.type = type;
        this.w = w;
        this.key = key;
        this.interp = interp;
    }

    public void update(float t){
        ae.vals.get(key).val = interp.get(o1, o2, type, w, t);
    }

    public void finalise(){
        ae.vals.get(key).val = o2;
    }

    public void initOrigin(){
        o1 = (T)ae.vals.get(key).val;
    }
}
