package com.xeloklox.dungeons.unleashed.utils;

import net.minecraft.client.texture.*;
import net.minecraft.client.util.*;
import net.minecraft.util.*;

import static com.xeloklox.dungeons.unleashed.DungeonsUnleashed.MODID;

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

    public static SpriteIdentifier getSprite(String name){
        return new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier(MODID+":"+name));
    }
}
