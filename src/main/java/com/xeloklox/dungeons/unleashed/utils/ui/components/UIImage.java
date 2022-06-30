package com.xeloklox.dungeons.unleashed.utils.ui.components;

import com.xeloklox.dungeons.unleashed.utils.ui.*;
import net.minecraft.util.math.*;

public class UIImage extends UIComponent{
    TextureRegion region;
    RegionNineSlice back=null;
    boolean subSection = true;
    Vector4f color = new Vector4f(1,1,1,1);
    public float rotation = 0;
    public boolean stickytape = false; //hehe

    public float rollup = 0;

    public UIImage(String id,TextureRegion r){
        super(id);
        region=r;
    }
    public static UIImage create(String id, TextureRegion r){
        return new UIImage(id,r);
    }

    @Override
    public void resetMinSize(){
        minw =0;
        minh =0;
    }

    @Override
    public void resetIdealSize(){
        idealw = region.w;
        idealh = region.h*(1f-rollup);
    }

    @Override
    public void draw(SpriteDrawer sb){
        if(rotation==0){
            drawImage(sb,x,y);
        }else{
            sb.getMatrixStack().push();
            sb.getMatrixStack().translate(x+w*0.5f,y+h*0.5f , 0);
            sb.rotate(rotation);
            drawImage(sb,-w*0.5f,-h*0.5f);
            sb.getMatrixStack().pop();
        }
    }

    public void drawImage(SpriteDrawer sb,float x,float y){
        float ah = h*(1-rollup);
        if(back!=null){
            back.drawInner(sb,x, y,w,ah);
        }
        sb.setColor(color);
        if(region!=null){
            if(subSection){
                sb.drawSection(region, x, y, 0,0,Math.min(region.w,w), Math.min(region.h,ah));
                if(rollup>0){
                    float rollsize = Math.min(Math.min(ah,rollup*h),16);
                    UITextures.roll.drawInner(sb,x,y+ah-rollsize,w,rollsize);
                }
            }else{
                sb.draw(region, x, y,w,ah);
            }
        }
        sb.resetColor();
        if(stickytape){
            sb.drawCentered(UITextures.stickytape, x+w*0.5f, y);
        }
    }

    public UIImage setColor(Vector4f color){
        this.color = color;
        return this;
    }

    public UIImage setBack(RegionNineSlice back){
        this.back = back;
        return this;
    }

    public UIImage setRotation(float rotation){
        this.rotation = rotation;
        return this;
    }

    public UIImage setStickytape(boolean stickytape){
        this.stickytape = stickytape;
        return this;
    }

    @Override
    public void update(){

    }
}
