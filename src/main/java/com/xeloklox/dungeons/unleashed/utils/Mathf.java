package com.xeloklox.dungeons.unleashed.utils;

import com.xeloklox.dungeons.unleashed.utils.Cons.*;
import net.minecraft.util.math.*;

import java.util.*;

import static net.minecraft.util.math.MathHelper.*;

public class Mathf{
    static Random rand = new Random();
    static {

    }
    public static Vec3f rand(){
        float t  = rand.nextFloat()*MathConstants.PI*2.0f;
        float z =  rand.nextFloat()*2-1;
        float s1z = sqrt(1-z*z);
        return new Vec3f(s1z* cos(t),s1z* sin(t),z);
    }
    public static void rand(Cons3<Float> cons){
        float t  = rand.nextFloat()*MathConstants.PI*2.0f;
        float z =  rand.nextFloat()*2-1;
        float s1z = sqrt(1-z*z);
        cons.get(s1z* cos(t),s1z* sin(t),z);
    }
}
