package com.xeloklox.dungeons.unleashed.utils.ui.components;

import com.xeloklox.dungeons.unleashed.utils.ui.*;

//lmao
public class UISimpleContainer extends UIContainer{
    public UISimpleContainer(String id, AnimatedScreen screen){
        super(id, screen);
    }
    public static UISimpleContainer create(String id, AnimatedScreen screen){
        return new UISimpleContainer(id,screen);
    }
    @Override
    public void rebuild(){
        for(UIComponent u:children.components){
            u.fitInto(padl,padt,w-padl-padr,h-padt-padb);
        }
        super.rebuild();
    }
}
