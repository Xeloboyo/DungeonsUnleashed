package com.xeloklox.dungeons.unleashed.utils.ui.components;

public abstract class UIDraggable extends UIComponent{
    boolean dragging = false;
    float imx,imy,ix,iy;
    public UIDraggable(String id){
        super(id);
    }

    public abstract boolean consumeClickBeforeDrag(float mouseX, float mouseY, int button);
    public abstract boolean draggable();
    @Override
    public boolean mouseClicked(float mouseX, float mouseY, int button){
        if(consumeClickBeforeDrag(mouseX,mouseY,button)){
            return false;
        }
        if(isInside(mouseX,mouseY) && draggable()){
            dragging = true;
            setFocus();
            imx = mouseX;
            imy = mouseY;
            ix = left;
            iy=top;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY){
        if(dragging){
            setPositioning((float)(ix+(mouseX-imx)),(float)(iy+(mouseY-imy)));
            x = left+marginl;
            y = top+margint;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button){
        if(dragging){
            unsetFocus();
            dragging = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }
}
