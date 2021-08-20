package com.xeloklox.dungeons.unleashed.utils.animation;

import com.xeloklox.dungeons.unleashed.utils.lambda.*;
import org.mini2Dx.gdx.utils.*;

public class StateMap{
    ObjectMap<String,AnimationState> states =new ObjectMap<>();
    ParameterMap params = new ParameterMap();
    AnimationState current=null;
    AnimationState pending = null;
    float internalTick = 0;
    public Func<StateMap,String> onStateEnd;

    public StateMap(Func<StateMap, String> onStateEnd, Cons<ParameterMap> initVariables){
        this.onStateEnd = onStateEnd;
        initVariables.get(params);
    }
    public boolean isState(String as){
        if(as==null || as.length()==0){
            return current==null;
        }
        if(current==null){return false;}
        return current.name.equals(as);
    }
    public StateMap addState(AnimationState as){
        states.put(as.name,as);
        return this;
    }

    public StateMap setState(String as){
        current = states.get(as);
        params.removeInterops();
        current.init.get(params);
        internalTick=0;
        return this;
    }

    public StateMap requestState(String as){
        if(current==null || !current.needsToComplete){
            setState(as);
        }else{
            pending = states.get(as);
        }
        return this;
    }


    public void update(){
        if(current==null){
            setState(onStateEnd.get(this));
        }
        params.update(internalTick/current.duration);
        internalTick++;
        if(internalTick>=current.duration){
            internalTick=0;
            if(pending!=null){
                setState(pending.getName());
                pending=null;
            }else{
                if(!current.loops){
                    setState(onStateEnd.get(this));
                }else if(current.resetOnloop){
                    setState(current.getName());
                }
            }
        }
    }

    public AnimationState getCurrent(){
        return current;
    }

    public float f(String k){
        return params.f(k);
    }
    public int i(String k){
        return params.i(k);
    }



    public static class AnimationState{
        String name;
        boolean loops = true;
        boolean resetOnloop = true;
        Cons<ParameterMap> init;
        float duration=60;
        boolean needsToComplete=true;
        AnimationState(String name){
            this.name = name;
        }
        public AnimationState(String name, boolean loops, float duration, Cons<ParameterMap> init){
            this.name = name;
            this.loops = loops;
            this.init = init;
            this.duration = duration;
        }
        public static AnimationState get(String name){
            return new AnimationState(name);
        }
        public AnimationState loops(boolean loops){
            this.loops = loops;
            return this;
        }
        public AnimationState resetOnloop(boolean loops){
            this.resetOnloop = loops;
            return this;
        }
        public AnimationState needsToComplete(boolean needsToComplete){
            this.needsToComplete = needsToComplete;
            return this;
        }
        public AnimationState duration(float duration){
            this.duration = duration;
            return this;
        }
        public AnimationState onInit(Cons<ParameterMap> init){
            this.init = init;
            return this;
        }

        public String getName(){
            return name;
        }
    }
}
