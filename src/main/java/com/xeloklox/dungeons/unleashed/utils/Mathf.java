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
    public static float randFloat(float max){
        return rand.nextFloat()*max;
    }
    public static float randInt(int exclusiveMax){
        return rand.nextInt(exclusiveMax);
    }
    public static float randFloat(float min,float max){
        return rand.nextFloat()*(max-min) + min;
    }
    public static void randVec3(Cons3<Float> cons){
        float t  = rand.nextFloat()*MathConstants.PI*2.0f;
        float z =  rand.nextFloat()*2-1;
        float s1z = sqrt(1-z*z);
        cons.get(s1z* cos(t),s1z* sin(t),z);
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
    public static float map(float r, float rmin,float rmax, float mapmin,float mapmax){
        return mapmin + (mapmax-mapmin)*(r-rmin)/(rmax-rmin);
    }

}
