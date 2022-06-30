package com.xeloklox.dungeons.unleashed.utils.ui;

public class RegionThreeSlice{
    public TextureRegion baseSlice;
    TextureRegion[] slices;
    public int h0,h1,h2;
    public int insetX=0,insetY=0;
    boolean vertical;
    RegionThreeSlice(TextureRegion baseSlice, int h0, int h1, int h2){
        this.baseSlice = baseSlice;
        this.h0 = h0;
        this.h1 = h1;
        this.h2 = h2;
        slices = new TextureRegion[3];
        if(h0+h1+h2>baseSlice.w){
            vertical = true;
            slices[0] = baseSlice.subRegion(0, 0, (int)baseSlice.w, h0);
            slices[1] = baseSlice.subRegion(0, h0, (int)baseSlice.w, h1);
            slices[2] = baseSlice.subRegion(0, h0 + h1, (int)baseSlice.w, h2);
        }else{
            slices[0] = baseSlice.subRegion(0, 0, h0, (int)baseSlice.h);
            slices[1] = baseSlice.subRegion(h0, 0, h1, (int)baseSlice.h);
            slices[2] = baseSlice.subRegion(h0 + h1, 0, h2, (int)baseSlice.h);
        }
    }
    RegionThreeSlice(TextureRegion baseSlice,int cornersize){
        this(baseSlice,cornersize, (int)(baseSlice.w-cornersize*2),cornersize);
    }
    public void drawInnerTiled(SpriteDrawer sb, float x, float y, float l){
        float w2 = l-(h0+h2);
        if(vertical){
            sb.draw(slices[0], x, y, baseSlice.w, h0);
            for(float i =0;i<w2;i+=h1){
                sb.draw(slices[1], x, y + h0+i, baseSlice.w, h1);
            }
            float lo = w2%h1;
            sb.drawSection(slices[1], x, y + w2 + h0 - lo,0,0, baseSlice.w, lo);
            sb.draw(slices[2], x, y + w2+ h0, baseSlice.w, h2);
        }else{
            sb.draw(slices[0], x, y, h0, baseSlice.h);
            for(float i =0;i<w2;i+=h1){
                sb.draw(slices[1], x+ h0+i, y , h1, baseSlice.h);
            }
            float lo = w2%h1;
            sb.drawSection(slices[1], x+ w2 + h0 - lo, y , 0,0,lo, baseSlice.h);
            sb.draw(slices[2], x + w2+ h0, y, h2, baseSlice.h);
        }
    }
    public void drawInner(SpriteDrawer sb, float x, float y, float l){
        float w2 = l-(h0+h2);
        if(vertical){
            sb.draw(slices[0], x, y, baseSlice.w, h0);
            sb.draw(slices[1], x, y + h0, baseSlice.w, w2);
            sb.draw(slices[2], x, y + w2+ h0, baseSlice.w, h2);
        }else{
            sb.draw(slices[0], x, y, h0, baseSlice.h);
            sb.draw(slices[1], x + h0, y, w2, baseSlice.h);
            sb.draw(slices[2], x + w2+ h0, y, h2, baseSlice.h);
        }
    }
}
