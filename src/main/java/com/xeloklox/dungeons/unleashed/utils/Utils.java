package com.xeloklox.dungeons.unleashed.utils;

import net.minecraft.client.texture.*;
import net.minecraft.client.util.*;
import net.minecraft.util.*;
import org.json.*;

import java.lang.reflect.*;

import static com.xeloklox.dungeons.unleashed.ModInitClientServer.MODID;

public class Utils{
    public static int toIntMask(boolean[] mask){
        int out = 0;
        for(int i =0;i<mask.length;i++){
            out = out|((mask[i]?1:0)<<i);
        }
        return out;
    }
    public static void fromIntMask(int mask, boolean[] out){
        for(int i =0;i<out.length;i++){
            out[i] = (mask&(1<<i))>0;
        }
    }

    public static float pixels(int pixels){
        return pixels/16f;
    }
    public static float pixels(float pixels){
       return pixels/16f;
   }

    public static SpriteIdentifier getSprite(String name){
        return new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier(MODID+":"+name));
    }

    public static float[] floatArray(JSONArray ja) throws JSONException{
        float[] floats =new float[ja.length()];
        for(int i=0;i<floats.length;i++){
            floats[i]= (float)ja.getDouble(i);
        }
        return floats;
    }

    public static int rgb(int red,int green, int blue){
        return (red<<16) | (green<<8) | blue;
    }


    public static <T> void setFinalInt(T d,String f, int a){
        Field field = null;
        try{
            field = d.getClass().getDeclaredField(f);
        }catch(NoSuchFieldException e){
            e.printStackTrace();
        }
        field.setAccessible(true);
        try{
            field.setInt(d,a);
        }catch(IllegalAccessException e){
            e.printStackTrace();
        }
    }


}
