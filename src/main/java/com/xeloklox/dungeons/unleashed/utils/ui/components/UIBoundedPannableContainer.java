package com.xeloklox.dungeons.unleashed.utils.ui.components;

import com.xeloklox.dungeons.unleashed.utils.ui.*;

public class UIBoundedPannableContainer extends UIPannableContainer{
    float actualh = 0,actualw = 0;
    boolean lockWidth = true, lockHeight = false;
    public UIBoundedPannableContainer(String id, AnimatedScreen screen){
        super(id, screen);
        pad(0);
    }

    public static UIBoundedPannableContainer create(String id, AnimatedScreen screen){
        return new UIBoundedPannableContainer(id,screen);
    }

    @Override
    public void rebuild(){
        actualh = 0;
        actualw = 0;
        for(UIComponent u:children.components){
            u.fitInto(padl, padt, lockWidth?w-padl-padr:u.idealw+u.marginl+u.marginr, lockHeight?h-padt-padb:u.idealh+u.marginb+u.margint);
            actualh = Math.max(actualh,u.h+u.marginb+u.margint);
            actualw = Math.max(actualw,u.w+u.marginl+u.marginr);
        }
        super.rebuild();
    }

    @Override
    public void resetMinSize(){
        float pw= minw;
        float ph = minh;
        super.resetMinSize();
        if(!lockHeight){minh=ph;}
        if(!lockWidth){pw=ph;}
    }

    public UIBoundedPannableContainer useSliderVert(UISlider slider){
        slider.onValueChanged((slider1, t2) -> {
            pany = -t2*(actualh-h);
        });
        return this;
    }
    public UIBoundedPannableContainer useSliderHorz(UISlider slider){
        slider.onValueChanged((slider1, t2) -> {
            panx = -t2*(actualw-w);
        });
        return this;
    }

}
