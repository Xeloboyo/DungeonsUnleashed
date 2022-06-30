package com.xeloklox.dungeons.unleashed.utils;

import com.xeloklox.dungeons.unleashed.utils.lambda.Cons.*;
import net.minecraft.util.math.*;

import java.util.*;

import static net.minecraft.util.math.MathHelper.*;

public class Mathf{
    static Random rand = new Random();
    static {

    }
    public static Vec3f randVec3(){
        float t  = rand.nextFloat()*MathConstants.PI*2.0f;
        float z =  rand.nextFloat()*2-1;
        float s1z = sqrt(1-z*z);
        return new Vec3f(s1z* cos(t),s1z* sin(t),z);
    }
    public static Vec2f randVec2(){
        float t  = rand.nextFloat()*MathConstants.PI*2.0f;
        return new Vec2f(cos(t), sin(t));
    }
    public static Vec2f randVec2Uniform(){
        return new Vec2f(rand.nextFloat(), rand.nextFloat());
    }
    public static float randFloat(float max){
        return rand.nextFloat()*max;
    }
    public static float randInt(int exclusiveMax){
        return rand.nextInt(exclusiveMax);
    }
    public static float randFloat(float min,float max){
        return rand.nextFloat()*(max-min) + min;
    }
    public static void randVec3(Cons3<Float,Float,Float> cons){
        float t  = rand.nextFloat()*MathConstants.PI*2.0f;
        float z =  rand.nextFloat()*2-1;
        float s1z = sqrt(1-z*z);
        cons.get(s1z* cos(t),s1z* sin(t),z);
    }
    public static float sinDeg(float deg){
        return MathHelper.sin(deg*RADIANS_PER_DEGREE);
    }
    public static float cosDeg(float deg){
        return MathHelper.cos(deg*RADIANS_PER_DEGREE);
    }

    public static float getHorzAngle(Vec3f vec){
        return (float)Math.toDegrees(MathHelper.atan2(vec.getZ(),vec.getX()))+90;
    }

    public static Vec3f relativeDirectionHorzF(Direction dir, Vec3f vec){
        Vec3f dirvecZ = new Vec3f(dir.getOffsetX(),dir.getOffsetY(), dir.getOffsetZ());
        Vec3f dirvecX = new Vec3f(-dir.getOffsetZ(),dir.getOffsetY(), dir.getOffsetX());
        Vec3f dirvecY = dirvecX.copy();
        dirvecY.cross(dirvecZ);
        dirvecX.scale(vec.getX());
        dirvecY.scale(vec.getY());
        dirvecZ.scale(vec.getZ());
        dirvecX.add(dirvecY);
        dirvecX.add(dirvecZ);
        return  dirvecX;

    }
    public static Vec3i relativeDirectionHorz(Direction dir, Vec3f vec){
        Vec3f v = relativeDirectionHorzF(dir,vec);
        return new Vec3i(v.getX(),v.getY(),v.getZ());
    }
    public static Vec3i vec3i(Vec3f v){
        return new Vec3i(v.getX(),v.getY(),v.getZ());
    }
    public static float lerp(float t, float x,float x2){return t*(x2-x) + x;}
    public static float map(float r, float rmin,float rmax, float mapmin,float mapmax){
        return mapmin + (mapmax-mapmin)*(r-rmin)/(rmax-rmin);
    }
    public static float mapClamped(float r, float rmin,float rmax, float mapmin,float mapmax){
        return mapmin + (mapmax-mapmin)*MathHelper.clamp((r-rmin)/(rmax-rmin),0,1);
    }
    public static float catmull(float t, float x,float x2,float m1,float m2){
        float t2 = t*t;
        float t3 = t2*t;
        return  (2*t3 - 3*t2 + 1)*x + (t3 - 2*t2 + t)*m1 + (-2*t3 + 3*t2)*x2 + (t3-t2)*m2;
    }
    public static float catmullNorm(float t,float m1,float m2){
        float t2 = t*t;
        float t3 = t2*t;
        return  (t3 - 2*t2 + t)*m1 + (-2*t3 + 3*t2) + (t3-t2)*m2;
    }
    public static Quaternion fromEulerDegXYZ(float x,float y,float z){
        return Quaternion.method_35825(x*MathConstants.RADIANS_PER_DEGREE,y*MathConstants.RADIANS_PER_DEGREE,z*MathConstants.RADIANS_PER_DEGREE);
    }


    public static float dst2(float x,float y){
        return x*x+y*y;
    }

    public static float approach(float x, float target,float speed){
        return x+(target>=x?Math.min(speed,target-x):-Math.min(speed,x-target));
    }
    public static float lerpTowards(float x, float target,float speed){
        return x+(target-x)*speed;
    }

    public static final int lookupSize = 256;
    public static float[][] randLookup = new float[lookupSize][lookupSize];

    public static float getRandFromPoint(int x,int y){
        return randLookup[x&0xFF][y&0xFF];
    }
    static {
        for(int i = 0;i<lookupSize;i++){
            for(int j = 0;j<lookupSize;j++){
                randLookup[i][j] = Mathf.randFloat(1);
            }
        }
    }

}
