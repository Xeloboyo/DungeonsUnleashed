package com.xeloklox.dungeons.unleashed.utils.ui;

import com.mojang.blaze3d.systems.*;
import com.xeloklox.dungeons.unleashed.utils.*;
import net.minecraft.client.*;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.*;
import net.minecraft.client.util.math.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;

public class TextureRegion{
    public float u,u2,v,v2,w,h;
    public Identifier texture;
    private int texid=-1;
    public AbstractTexture rawTex;

    public TextureRegion(float u, float u2, float v, float v2,float w, float h, Identifier texture){
        this.u = u;
        this.u2 = u2;
        this.v = v;
        this.v2 = v2;
        this.texture = texture;
        this.w=w;
        this.h=h;
    }
    public TextureRegion(float w, float h, Identifier texture){
        this.u = 0;
        this.u2 = 1;
        this.v = 0;
        this.v2 = 1;
        this.texture = texture;
        this.w=w;
        this.h=h;
    }
    public TextureRegion(int x, int y, int w, int h, int tw, int th, Identifier texture){
        this((float)x/(float)tw,(float)(x+w)/(float)tw,((float)y/(float)th),((y+(float)h)/(float)th), w, h, texture);
    }
    public TextureRegion(int x, int y, int w, int h, Identifier texture){
        this(x,y,w,h,256,256, texture);
    }

    public TextureRegion subRegion(int x, int y, int w, int h){
        return new TextureRegion(
            Mathf.map(x,0,this.w,u,u2),Mathf.map(x+w,0,this.w,u,u2),
            Mathf.map(y,0,this.h,v,v2),Mathf.map(y+h,0,this.h,v,v2),w,h,this.texture);
    }

    public int getTexid(){
        if(rawTex==null){
            rawTex= MinecraftClient.getInstance().getTextureManager().getTexture(texture);
        }
        return rawTex.getGlId();
    }

    @Override
    public String toString(){
        return "TextureRegion{" +
        "u=" + u +
        ", u2=" + u2 +
        ", v=" + v +
        ", v2=" + v2 +
        ", w=" + w +
        ", h=" + h +
        ", texture=" + texture +
        ", texid=" + texid +
        '}';
    }
}
