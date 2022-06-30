package com.xeloklox.dungeons.unleashed.utils.ui.components;

import com.xeloklox.dungeons.unleashed.utils.ui.*;

public class UIPannableContainer extends UIContainer{
    public float panx,pany;
    public UIPannableContainer(String id, AnimatedScreen screen){
        super(id, screen);
    }
    @Override
    public boolean mouseClicked(float mouseX, float mouseY, int button){
        return super.mouseClicked(mouseX-panx, mouseY-pany, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY){
        return super.mouseDragged(mouseX-panx, mouseY-pany, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button){
        return super.mouseReleased(mouseX-panx, mouseY-pany, button);

    }

    public void drawPannedBg(SpriteDrawer sb){ }

    @Override
    public void drawChildren(SpriteDrawer sb){
        if(w<0||h<0){
            return;
        }
        sb.clipRelative(x,y,w,h);
        sb.getMatrixStack().push();
        sb.getMatrixStack().translate(x+panx,y+pany,0);
        drawPannedBg(sb);
        for(UIComponent c:children.components){
            if(c.disabled|| c.h<0 || c.w<0){continue;}
            if(c.y+c.h<-pany-50 || c.y>-pany+h+50){continue;}
            c.draw(sb);
            drawDebug(sb,c);
        }
        drawAboveChildren(sb);
        sb.getMatrixStack().pop();
        sb.unclip();
    }



    @Override
    public void drawAbove(SpriteDrawer sb){
        if(w<0||h<0){
            return;
        }
        sb.clipRelative(x,y,w,h);
        sb.getMatrixStack().push();
        sb.getMatrixStack().translate(x+panx,y+pany,0);
        for(UIComponent c:children.components){
            if(c.disabled){continue;}
            c.drawAbove(sb);
        }
        sb.getMatrixStack().pop();
        sb.unclip();
    }

    public void drawAboveChildren(SpriteDrawer sb){}

}
