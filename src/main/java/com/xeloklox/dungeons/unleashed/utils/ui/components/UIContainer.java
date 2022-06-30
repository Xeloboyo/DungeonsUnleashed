package com.xeloklox.dungeons.unleashed.utils.ui.components;

import com.xeloklox.dungeons.unleashed.utils.ui.*;
import net.minecraft.util.math.*;

public abstract class UIContainer extends UIComponent{
    public static boolean debugDraw = false;
    UIHandler children;
    public RegionNineSlice frame = null;
    public RegionNineSlice bg = null;
    Vector4f bgcolor = new Vector4f(1,1,1,1);
    public boolean isInherentClickConsumer = false;
    public boolean isInherentReleaseConsumer = false;


    public UIContainer( String id, AnimatedScreen screen){
        super( id);
        children = new UIHandler(screen);
        children.onRemove= this::onRemove;
    }

    public void onRemove(UIComponent u){

    }

    public void add(UIComponent u){
        children.add(u);
        u.parent = this;
        rebuild();

    }

    public void remove(UIComponent u){
        children.remove(u);
        rebuild();
    }

    public void resetMinSize(){
        minw = 0;
        minh = 0;
        for(UIComponent u:children.components){
            minw = Math.max(u.minw+u.marginl+u.marginr,minw);
            minh = Math.max(u.minh+u.margint+u.marginb,minh);
        }
        minw+=padl+padr;
        minh+=padt+padb;
    }
    public void resetIdealSize(){
        idealw = 0;
        idealh = 0;
        for(UIComponent u:children.components){
            idealw = Math.max(u.idealw+u.marginl+u.marginr,minw);
            idealh = Math.max(u.idealh+u.margint+u.marginb,minh);
        }
        idealw+=padl+padr;
        idealh+=padt+padb;
    }

    public UIContainer setBg(RegionNineSlice bg){
        this.bg = bg;
        return this;
    }
    public UIContainer setFrame(RegionNineSlice frame){
        this.frame = frame;
        return this;
    }
    public void drawChildren(SpriteDrawer sb){
        sb.getMatrixStack().push();
        sb.getMatrixStack().translate(x,y,0);
        for(UIComponent c:children.components){
            if(c.disabled || c.h<0 || c.w<0){continue;}
            c.draw(sb);
            drawDebug(sb,c);
        }
        sb.getMatrixStack().pop();
    }
    @Override
    public void draw(SpriteDrawer sb){
        if(getZ()>0){
            sb.getMatrixStack().push();
            sb.getMatrixStack().translate(0,0,getZ());
        }
        if(bg!=null){
            sb.setColor(bgcolor);
            bg.drawInner(sb,x,y,w,h);
            sb.resetColor();
        }
        drawChildren(sb);
        if(frame!=null){
            frame.drawInner(sb,x,y,w,h);
        }
        if(getZ()>0){
            sb.getMatrixStack().pop();
        }
    }

    public void drawDebug(SpriteDrawer sb, UIComponent c){
        if(debugDraw){
            sb.setColor(0, 0, 1);
            sb.drawRect(c.x, c.y, c.w, c.h);
            sb.setColor(1, 0, 0);
            sb.drawRect(c.x - c.marginl, c.y - c.margint, c.w + c.marginl + c.marginr, c.h + c.margint + c.marginb);
            sb.resetColor();
        }
    }

    @Override
    public void drawAbove(SpriteDrawer sb){
        sb.getMatrixStack().push();
        sb.getMatrixStack().translate(x,y,0);
        for(UIComponent u:children.components){
            if(u.disabled){continue;}
            u.drawAbove(sb);
        }
        sb.getMatrixStack().pop();
    }

    @Override
    public void rebuild(){
        if(disabled){return;}
        super.rebuild();
        for(UIComponent u:children.components){
            u.rebuild();
        }
        recalcSize();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers){
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button){
        return children.mouseReleased(mouseX-x,mouseY-y,button) || (isInside((float)mouseX,(float)mouseY)&&isInherentReleaseConsumer);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY){
        return children.mouseDragged(mouseX-x,mouseY-y,button, deltaX, deltaY) ;
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, int button){
        return children.mouseClicked(mouseX-x,mouseY-y,button) || (isInside(mouseX,mouseY)&& isInherentClickConsumer);
    }

    @Override
    public boolean charTyped(int keyCode, int scanCode, int modifiers){
        return false;
    }
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount){
        return false;
    }

    public UIHandler getChildHandler(){
        return children;
    }

    public UIContainer setBgcolor(Vector4f bgcolor){
        this.bgcolor = bgcolor;
        return this;
    }

    @Override
    public void update(){
        children.update();

    }

    @Override
    public void recalcSize(){
        for(UIComponent u:children.components){
            u.recalcSize();
        }
        super.recalcSize();
    }


}
