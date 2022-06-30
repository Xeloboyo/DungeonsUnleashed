package com.xeloklox.dungeons.unleashed.utils.ui.components;

import com.xeloklox.dungeons.unleashed.utils.ui.*;
import net.minecraft.text.*;
import org.mini2Dx.gdx.utils.*;

public class UIMultilineText extends UIText{
    Array<OrderedText> lines = new Array<>();
    public UIMultilineText(String id, Text text){
        super(id, text);
    }

    public static UIMultilineText create(String id, Text text){
        return new UIMultilineText(id,text);
    }

    float texth = 0;
    @Override
    protected UIComponent setSize(float w, float h){
        lines.clear();
        handler.wrapLines(text,(w-padl-padr)/scale,lines::add);
        texth = lines.size*(14*scale);
        return super.setSize(w, h);
    }

    @Override
    public void resetIdealSize(){
        idealw=Math.max(padl+padr,setw);
        idealh=Math.max(seth,texth+padt+padb);
    }

    @Override
    public void drawText(float x, float y){
        if(h<14){return;}
        for(int i =0;i<lines.size;i++){
            if(14 + y + i*14>=h/scale && i!=lines.size-1){
                handler.drawText(new LiteralText("..."), x , y + i*14, color);
                break;
            }
            handler.drawText(lines.get(i), x , y + i*14, color);
        }
    }

}
