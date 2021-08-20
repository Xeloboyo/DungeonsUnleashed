package com.xeloklox.dungeons.unleashed.utils.animation;

public class FrameState<T>{
    T val;
    float weight;

    public FrameState(T val, float weight){
        this.val = val;
        this.weight = weight;
    }

    public FrameState(T val){
        this.val = val;
        this.weight = 1;
    }

    public static <T> FrameState<T> get(T val, float weight){
        return new FrameState<>(val,weight);
    }
}
