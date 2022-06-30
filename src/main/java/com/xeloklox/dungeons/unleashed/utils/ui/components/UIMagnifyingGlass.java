package com.xeloklox.dungeons.unleashed.utils.ui.components;

import com.xeloklox.dungeons.unleashed.utils.*;
import com.xeloklox.dungeons.unleashed.utils.ui.*;
import net.minecraft.util.math.*;

public class UIMagnifyingGlass extends UIDraggable{
    UIComponent toDraw;
    TextureRegion glass = UITextures.magGlassDefault;
    float offx = 9, offy = 9;
    float sx = 96, sy = 96;

    //57,108
    public UIMagnifyingGlass(String id, UIComponent component){
        super(id);
        toDraw = component;
    }

    @Override
    public void resetMinSize(){
        minw = glass.w;
        minh = glass.h;
    }

    @Override
    public void draw(SpriteDrawer sb){
        if(toDraw!=null){

            Vec2f abspos = getAbsolutePos();
            sb.clip(abspos.x+offx,abspos.y+offy,sx,sy);
            sb.getMatrixStack().push();
            sb.getMatrixStack().translate(0,0,getZ());
            sb.getMatrixStack().push();
            sb.getMatrixStack().translate(-(x+offx+sx*0.5),-(y+offy+sy*0.5),0);
            sb.getMatrixStack().scale(2,2,1);
            toDraw.draw(sb);
            sb.getMatrixStack().pop();
            sb.unclip();

            sb.setColor(1);
            sb.draw(glass,x,y,w,h);
            sb.getMatrixStack().pop();
        }
    }

    @Override
    public void update(){

    }

    @Override
    public boolean consumeClickBeforeDrag(float mouseX, float mouseY, int button){
        float dst = Mathf.dst2(mouseX-(x+57),mouseY-(y+108));
        return dst>22*22;
    }

    @Override
    public boolean draggable(){
        return true;
    }
}
