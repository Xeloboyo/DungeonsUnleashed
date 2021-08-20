package com.xeloklox.dungeons.unleashed.blockentity.screens;

import com.xeloklox.dungeons.unleashed.utils.lambda.*;
import net.minecraft.client.util.math.*;
import net.minecraft.util.math.*;

public class DistortableParticle{
    float[][] relativePos = new float[4][2];
    float[][] distortPos = new float[4][2];
    Funcf2 distort;
    float u0,u1,v0,v1;
    float x,y,vx,vy;
    float life = 0;

    DistortableParticle(float x,float y,float size,float vx,float vy,float u,float v,float texW,float texH, Funcf2 distort){
        u0 = u/texW;
        u1 = (u+size)/texW;
        v0 = v/texH;
        v1 = (v+size)/texH;
        float s2 = size*0.5f;
        relativePos = new float[][]{
            {-s2,s2},
            {s2,s2},
            {s2,-s2},
            {-s2,-s2},
        };
        this.distort=distort;
        this.x=x;
        this.y=y;
        this.vx=vx;
        this.vy=vy;
    }

    public void draw(Matrix4f matrices, AnimatedScreen at){
        x+=vx;
        y+=vy;
        //when too lazy to for loop
        distort.get(relativePos[0][0]+x,relativePos[0][1]+y,distortPos[0]);
        distort.get(relativePos[1][0]+x,relativePos[1][1]+y,distortPos[1]);
        distort.get(relativePos[2][0]+x,relativePos[2][1]+y,distortPos[2]);
        distort.get(relativePos[3][0]+x,relativePos[3][1]+y,distortPos[3]);

        at.drawTexturedQuad(matrices,distortPos,u0,u1,v0,v1);
        life++;
    }
}
