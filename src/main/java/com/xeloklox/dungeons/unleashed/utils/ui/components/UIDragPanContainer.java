package com.xeloklox.dungeons.unleashed.utils.ui.components;

import com.xeloklox.dungeons.unleashed.utils.ui.*;

public class UIDragPanContainer extends UIPannableContainer{

    public UIDragPanContainer(String id, AnimatedScreen screen){
        super(id, screen);
        isInherentClickConsumer = true;
    }

    public static UIDragPanContainer create(String test, AnimatedScreen screen){
        return new UIDragPanContainer(test,screen);
    }

    boolean panning=false;
    float dragFromX,dragFromY;
    float mouseDragFromX,mouseDragFromY;

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, int button){
        if(isInside(mouseX,mouseY)){
            if(! children.mouseClicked(mouseX-panx-x,mouseY-pany-y,button)){
                if(button==0){
                    setFocus();
                    dragFromX = panx;
                    dragFromY = pany;
                    mouseDragFromX = mouseX;
                    mouseDragFromY = mouseY;
                    panning = true;
                }else{
                    onClickSurface(mouseX,mouseY,button);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY){
        if(panning){
            panx = dragFromX + (float)(mouseX-mouseDragFromX);
            pany = dragFromY + (float)(mouseY-mouseDragFromY);
            return true;
        }else{
            return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button){
        if(panning){
            panning=false;
            unsetFocus();
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }


    public void onClickSurface(float mouseX, float mouseY, int button){

    }

    @Override
    public void rebuild(){
        for(UIComponent c:children.components){
            c.x = c.left;
            c.y = c.top;
        }
        super.rebuild();
    }
}
