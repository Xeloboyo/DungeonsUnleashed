package com.xeloklox.dungeons.unleashed.utils.animation;

import com.xeloklox.dungeons.unleashed.utils.*;

import static java.lang.Math.*;
import static net.minecraft.util.math.MathHelper.cos;

public class Interpolations{
    interface Interpolate<T> {
      T get(T x1, T x2, InterpolateType type, float w, float t);
    }

    public static class NoInterpolate implements Interpolate{
        public Object get(Object x0, Object x1, InterpolateType type, float w, float t){
            return t > 0.5 ? x1 : x0;
        }
    }

    public static class FloatInterpolate implements Interpolate<Float>{
        public Float get(Float x0, Float x1, InterpolateType type, float w, float t){
            return type.interpolate(x0, x1, t,  w);
        }
    }
    public static class IntInterpolate implements Interpolate<Integer>{
        public Integer get(Integer x0, Integer x1, InterpolateType type, float w, float t){
            return (int)type.interpolate(x0, x1, t,  w);
        }
    }


    public static class StringInterpolate implements Interpolate<String>{
        public String get(String x0, String x1, InterpolateType type, float w, float t){
            float l = type.interpolate(0, 1, t, w);
            int charc = (int)(Math.max(x0.length(), x1.length()) * l);
            int mt = charc - x1.length();
            StringBuilder spaces = new StringBuilder();
            for(int i = 0; i < mt; i++){
                spaces.append(" ");
            }
            return x1.substring(0, min(charc + 1, x1.length())) + spaces.toString() + "_" + x0.substring(min(charc + 1, x0.length()), x0.length());
        }
    }
    public interface InterpolateType{
        default float interpolate(float p,float pt,float a,float at,float t,float w){
            return interpolate(p,pt,0,1,a,at,t,w);
        }
        public float interpolate(float p,float pt,float x,float x2,float a,float at,float t,float w);
        default float interpolate(float x,float x2,float t,float w){
            return interpolate(x,-1,x,x2,x2,2,t,w);
        }
    }
    //has addtional control points
    public enum WeightedInterpolateType implements InterpolateType{
        CATMULL_SPLINE((p, pt, x,x2,a, at, t, w) -> {
            return Mathf.catmull(t,x,x2,(p-x)/pt,(a-x2)/(at-1));
        });
        InterpolateType interpol;

        WeightedInterpolateType(InterpolateType interpol){
            this.interpol = interpol;
        }
        @Override
        public float interpolate(float p, float pt,float x,float x2, float a, float at, float t, float w){
            return this.interpol.interpolate(p,pt,x,x2,a,at,t,w);
        }
    }

    //is not influenced by additional control points
    public enum SingularInterpolateType implements InterpolateType{
        LINEAR((p, pt, x,x2,a, at, t, w) -> {
            return t*(x2-x)+x;
        }),
        EXPONENTIAL((p, pt,x,x2, a, at, t, w) -> {
            return (float)(1-exp(-t*w)*(1-t))*(x2-x)+x;
        }),
        EXPONENTIAL2((p, pt,x,x2, a, at, t, w) -> {
            return (float)(1-(1.0/(1+exp((t*2-1)/w))))*(x2-x)+x;

        }), DISCRETE((p, pt, x,x2,a, at, t, w) -> {
            return t>w?x2:x;
        }), WOBBLE((p, pt,x,x2, a, at, t, w) -> {
            return (1-(1/(10*t+t))*min(1,10*(1-t))*cos(t/w))*(x2-x)+x;
        });
        InterpolateType interpol;

        SingularInterpolateType(InterpolateType interpol){
            this.interpol = interpol;
        }

        @Override
        public float interpolate(float p, float pt,float x,float x2, float a, float at, float t, float w){
            return this.interpol.interpolate(p,pt,x,x2,a,at,t,w);
        }
    }

    public static class Wrapper<T> {
        public T val;
        public Wrapper(T t) {
            val = t;
        }
    }
}
