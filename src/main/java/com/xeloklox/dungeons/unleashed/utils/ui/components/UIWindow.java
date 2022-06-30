package com.xeloklox.dungeons.unleashed.utils.ui.components;

import com.xeloklox.dungeons.unleashed.utils.lambda.*;
import com.xeloklox.dungeons.unleashed.utils.ui.*;
import net.minecraft.util.math.*;

//can be dragged and minimised.
public class UIWindow extends UIContainer{
    public boolean minimised=false;
    public boolean closing=false;
    UIButton minimise;
    UIButton close;

    Cons<UIWindow> onMinimise = (b)->{};
    Cons<UIWindow> onClose = (b)->{};

    public UIWindow(String id, AnimatedScreen screen){
        super(id, screen);
        minimise = (UIButton)UIButton.create("minimise")
            .setIcon(UITextures.minimiseSmall).
            setOnClicked((b)->{minimised=!minimised; onMinimise.get(this);})
            .setPositioning(DISABLE,18,-12,DISABLE)
            .resize(12,12);
        close = (UIButton)UIButton.create("close")
            .setIcon(UITextures.crossSmall).
            setOnClicked((b)->{closing=true; onClose.get(this);})
            .setPositioning(DISABLE,0,-12,DISABLE)
            .resize(12,12);
        add(minimise);
        add(close);
        setBg(UITextures.card);
        isInherentClickConsumer = true;
    }
    @Override
    public UIComponent resize(float w, float h){
        return super.resize(w, h);
    }



    public void initialSize(){
        w=0;h=0;
    }

    @Override
    public void rebuild(){
        for(UIComponent u:children.components){
            u.fitInto(padl,padt,w-padl-padr,h-padt-padb);
        }
        super.rebuild();
    }

    @Override
    public void update(){
        super.update();
        float targetw = idealw;
        float targeth = idealh;

        if(minimised){
            targeth = 32;
        }
        if(closing){
            targetw = 0;
            targeth = 0;
        }
        if(Math.abs(w-targetw)<0.5 && Math.abs(h-targeth)<0.5){
            return;
        }
        setSize(w+(targetw-w)*0.1f,h+(targeth-h)*0.1f);
        if(closing && w<10){
            disabled=true;
        }
        rebuild();
    }

    @Override
    public void drawChildren(SpriteDrawer sb){
        sb.clipRelative(x, y-15, w,h+15);
        if(w>padl+padr && h>padt+padb){
            super.drawChildren(sb);
        }
        sb.unclip();
    }

    public UIWindow setOnMinimise(Cons<UIWindow> onMinimise){
        this.onMinimise = onMinimise;
        return this;
    }

    boolean dragging = false;
    float imx,imy,ix,iy;

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, int button){
        if(children.mouseClicked(mouseX-x,mouseY-y,button)){
            return true;
        }
        if(isInside(mouseX,mouseY) && isInherentClickConsumer){
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
