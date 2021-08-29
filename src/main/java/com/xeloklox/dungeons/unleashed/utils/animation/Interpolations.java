package com.xeloklox.dungeons.unleashed.utils.animation;

import static java.lang.Math.*;
import static net.minecraft.util.math.MathHelper.cos;

public class Interpolations{
    interface Interpolate<T> {
      T get(T x1, T x2, InterpolateType type, float w, float t);
    }

    public static class NoInterpolate implements Interpolate{
        public Object get(Object x0, Object x1, InterpolateType type, float w, float t){
            return getInterpolate(0, 1, t, InterpolateType.DISCRETE, w) > 0.5 ? x1 : x0;
        }
    }

    public static class FloatInterpolate implements Interpolate<Float>{
        public Float get(Float x0, Float x1, InterpolateType type, float w, float t){
            return getInterpolate(x0, x1, t, type, w);
        }
    }
    public static class IntInterpolate implements Interpolate<Integer>{
        public Integer get(Integer x0, Integer x1, InterpolateType type, float w, float t){
            return (int)getInterpolate(x0, x1, t, type, w);
        }
    }


    public static class StringInterpolate implements Interpolate<String>{
        public String get(String x0, String x1, InterpolateType type, float w, float t){
            float l = getInterpolate(0, 1, t, type, w);
            int charc = (int)(Math.max(x0.length(), x1.length()) * l);
            int mt = charc - x1.length();
            StringBuilder spaces = new StringBuilder();
            for(int i = 0; i < mt; i++){
                spaces.append(" ");
            }
            return x1.substring(0, min(charc + 1, x1.length())) + spaces.toString() + "_" + x0.substring(min(charc + 1, x0.length()), x0.length());
        }
    }

    public enum InterpolateType {
      LINEAR, EXPONENTIAL, EXPONENTIAL2, DISCRETE,WOBBLE
    }

    public static float getInterpolate(float x0, float x1, float t, InterpolateType it, float w) {
      float sig = 0;
      switch(it) {
      case LINEAR:
        sig = t;
        break;
      case WOBBLE:
        sig = 1-(1/(10*t+t))*min(1,10*(1-t))*cos(t/w);
        break;
      case EXPONENTIAL:
        sig = (float)(1-exp(-t*w)*(1-t));
        break;
      case EXPONENTIAL2:
        sig = (float)(1-(1.0/(1+exp((t*2-1)/w))));
        break;
      case DISCRETE:
        sig = t>w?1:0;
        break;
      }
      return x0 + (x1-x0)*sig;
    }
    public static class Wrapper<T> {
        public T val;
        public Wrapper(T t) {
            val = t;
        }
    }
}
