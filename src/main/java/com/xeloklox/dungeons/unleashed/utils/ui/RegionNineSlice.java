package com.xeloklox.dungeons.unleashed.utils.ui;

import com.xeloklox.dungeons.unleashed.utils.*;

public class RegionNineSlice{
    public TextureRegion baseSlice;
    TextureRegion[] slices;
    public int h0,h1,h2,v0,v1,v2;
    public int insetX=0,insetY=0;

    public RegionNineSlice(TextureRegion baseSlice, int h0, int h1, int h2, int v0, int v1, int v2){
        this.baseSlice = baseSlice;
        this.h0 = h0;
        this.h1 = h1;
        this.h2 = h2;
        this.v0 = v0;
        this.v1 = v1;
        this.v2 = v2;
        slices = new TextureRegion[9];
        slices[0] = baseSlice.subRegion(0,0,h0,v0);
        slices[1] = baseSlice.subRegion(h0,0,h1,v0);
        slices[2] = baseSlice.subRegion(h0+h1,0,h2,v0);
        slices[3] = baseSlice.subRegion(0,v0,h0,v1);
        slices[4] = baseSlice.subRegion(h0,v0,h1,v1);
        slices[5] = baseSlice.subRegion(h0+h1,v0,h2,v1);
        slices[6] = baseSlice.subRegion(0,v0+v1,h0,v2);
        slices[7] = baseSlice.subRegion(h0,v0+v1,h1,v2);
        slices[8] = baseSlice.subRegion(h0+h1,v0+v1,h2,v2);
    }
    public RegionNineSlice(TextureRegion baseSlice, int cornersize, int middlesize){
        this(baseSlice,cornersize,middlesize,cornersize,cornersize,middlesize,cornersize);
    }
    public RegionNineSlice(TextureRegion baseSlice, int cornersize){
        this(baseSlice,cornersize,(int)(baseSlice.w-(cornersize*2)),cornersize,cornersize,(int)(baseSlice.h-(cornersize*2)),cornersize);
    }

    public void getUV(float x, float y, float w, float h, float[] out){
        if(x<=h0){
            out[0] = Mathf.lerp(x/h0,slices[0].u,slices[0].u2);
        }else if(x<=w-h2){
            out[0] = Mathf.lerp((x-h0)/(w-h0-h2),slices[1].u,slices[1].u2);
        }else{
            out[0] = Mathf.lerp(1f-((w-x)/h2),slices[2].u,slices[2].u2);
        }
        if(y<=v0){
            out[1] = Mathf.lerp(y/v0,slices[0].v,slices[0].v2);
        }else if(y<=h-v2){
            out[1] = Mathf.lerp((y-v0)/(h-v0-v2),slices[3].v,slices[3].v2);
        }else{
            out[1] = Mathf.lerp(1f-((h-y)/v2),slices[6].v,slices[6].v2);
        }
    }

    public void drawInner(SpriteDrawer sb, float x, float y, float w, float h){
        x-=insetX;
        y-=insetY;
        w+=2*insetX;
        h+=2*insetY;
        float mw = w-(h2+h0);
        float mh = h-(v0+v2);
        int ah0 = h0;
        int ah2 = h2;
        if(mw<0){
            h0+=mw/2;
            h2+=mw/2;
            mw=0;
        }
        int av0 = v0;
        int av2 = v2;
        if(mh<0){
            v0+=mh/2;
            v2+=mh/2;
            mh=0;
        }
        sb.draw(slices[0],x,y,h0,v0);
        sb.draw(slices[1],x+h0,y,mw,v0);
        sb.draw(slices[2],x+h0+mw,y,h2,v0);
        sb.draw(slices[3],x,y+v0,h0,mh);
        sb.draw(slices[4],x+h0,y+v0,mw,mh);
        sb.draw(slices[5],x+h0+mw,y+v0,h2,mh);
        sb.draw(slices[6],x,y+v0+mh,h0,v2);
        sb.draw(slices[7],x+h0,y+v0+mh,mw,v2);
        sb.draw( slices[8],x+h0+mw,y+v0+mh,h2,v2);
        h0=ah0;
        h2=ah2;
        v0=av0;
        v2=av2;

    }
    public void drawOuter(SpriteDrawer sb, float x, float y, float w, float h){
        x-=insetX;
        y-=insetY;
        w+=2*insetX;
        h+=2*insetY;
        sb.draw(slices[0],x-h0,y-v0,h0,v0);
        sb.draw(slices[1],x,y-v0,w,v0);
        sb.draw(slices[2],x+w,y-v0,h2,v0);
        sb.draw(slices[3],x-h0,y,h0,h);
        sb.draw(slices[4],x,y,w,h);
        sb.draw(slices[5],x+w,y,h2,h);
        sb.draw(slices[6],x-h0,y+h,h0,v2);
        sb.draw(slices[7],x,y+h,w,v2);
        sb.draw(slices[8],x+w,y+h,h2,v2);
    }

    public RegionNineSlice setInset(int insetX,int insetY){
        this.insetX = insetX;
        this.insetY = insetY;
        return this;
    }
}
