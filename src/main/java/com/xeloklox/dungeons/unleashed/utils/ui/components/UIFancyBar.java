package com.xeloklox.dungeons.unleashed.utils.ui.components;

import com.xeloklox.dungeons.unleashed.utils.*;
import com.xeloklox.dungeons.unleashed.utils.ui.*;
import net.minecraft.util.math.*;

public class UIFancyBar extends UIComponent{
    MarchingSquaresBackground msb;
    RegionNineSlice slice;
    private float progress = 0;
    boolean fadeIn = false;
    float[] color = new float[4];
    public UIFancyBar(RegionNineSlice slice, String id){
        super(id);
        this.slice=slice;
        msb = new MarchingSquaresBackground(slice,32,32,8,8);
    }

    @Override
    public void draw(SpriteDrawer sb){
        if(progress<=0){
            return;
        }
        sb.setColor(color[0],color[1],color[2],color[3]*(fadeIn? MathHelper.clamp(progress,0,1):1));
        if(progress>=1){
            slice.drawInner(sb,x,y,w,h);
            return;
        }
        sb.getMatrixStack().push();
        sb.getMatrixStack().translate(x,y,0);
        sb.clipRelative(0,0,w,h);
        msb.draw(sb,0,0,w,h);
        sb.unclip();
        sb.getMatrixStack().pop();
    }

    @Override
    public void update(){

    }

    public void setProgress(float progress){
        msb.affectAll((rx, ry, original) -> {
            float v = Mathf.getRandFromPoint((int)rx,(int)ry)*0.9f;
            float am = Math.max(0,1f+(progress-(rx*msb.getGridWidth()/(msb.getTotalWidth()))) * 10f);
            return am+v;
        },0,0);
        this.progress = progress;
    }

    public float getProgress(){
        return progress;
    }

    @Override
    protected UIComponent setSize(float w, float h){
        msb.resizeLarger(w,h);
        return super.setSize(w, h);
    }

    public void setColor(float r, float g,float b,float a){
        this.color[0]=r;
        this.color[1]=g;
        this.color[2]=b;
        this.color[3]=a;
    }
}
