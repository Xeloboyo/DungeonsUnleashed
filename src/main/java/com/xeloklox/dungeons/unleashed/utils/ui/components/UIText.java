package com.xeloklox.dungeons.unleashed.utils.ui.components;

import com.xeloklox.dungeons.unleashed.utils.*;
import com.xeloklox.dungeons.unleashed.utils.ui.*;
import net.minecraft.text.*;

import java.time.format.*;

public class UIText extends UIComponent{
    Text text;

    int color = Utils.rgb(48,48,48);
    float scale = 1;
    public UIText(String id, Text text){
        super(id);
        this.text=text;
    }

    @Override
    public void resetMinSize(){
        super.resetMinSize();
        minh+=scale*15;
    }

    @Override
    public void resetIdealSize(){
        super.resetIdealSize();
        idealw = Math.max(idealw,(handler!=null?handler.textWidth(text)*scale:scale*15*text.asString().length())+padl+padr);
    }

    public static UIText create(String id, Text text){
        return new UIText(id,text);
    }

    @Override
    public void draw(SpriteDrawer sb){
        if(scale==1){
            drawText( x + padl, y + padt);
        }else{
            sb.getMatrixStack().push();
            sb.getMatrixStack().translate(x + padl, y + padt,0);
            sb.getMatrixStack().scale(scale,scale,1);
            drawText(0,0);
            sb.getMatrixStack().pop();
        }
    }

    public void drawText(float x,float y){
        handler.drawText(text, x,y, color);
    }

    @Override
    public void drawAbove(SpriteDrawer sb){ }

    public UIText setColor(int color){
        this.color = color;
        return this;
    }

    public UIText setScale(float scale){
        this.scale = scale;
        return this;
    }

    public UIText setText(Text text){
        this.text = text;
        recalcSize();
        return this;
    }

    @Override
    public void update(){ }
}
