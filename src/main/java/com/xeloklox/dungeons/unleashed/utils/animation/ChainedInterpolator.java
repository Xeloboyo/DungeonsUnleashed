package com.xeloklox.dungeons.unleashed.utils.animation;

import com.xeloklox.dungeons.unleashed.utils.*;
import com.xeloklox.dungeons.unleashed.utils.animation.Interpolations.*;
import org.mini2Dx.gdx.utils.compat.*;

public class ChainedInterpolator<T> extends Interpolator<T>{
    FrameState<T>[] states;
    float totalWeight;

    ChainedInterpolator(ParameterMap ae, InterpolateType type, float w, String key, Interpolate<T> interp, FrameState<T>... states){
        super(ae, type, w, key, interp);
        this.states = states;
        initOrigin();
        totalWeight = 0;
        for(FrameState<T> f : states){
            if(f.val == null){
                f.val = o1;
            }
            totalWeight += f.weight;
        }
    }

    public void update(float t){
        float cw = 0, pcw = 0;
        for(int i = 0; i < states.length; i++){
            pcw = cw;
            cw += states[i].weight / totalWeight;
            if(cw >= t){
                ae.vals.get(key).val = interp.get(i == 0 ? o1 : states[i - 1].val, states[i].val, type, w, Mathf.map(t, pcw, cw, 0, 1));
                break;
            }
        }
    }

    public void finalise(){
        ae.vals.get(key).val = states[states.length - 1].val;
    }
}
