package com.xeloklox.dungeons.unleashed.utils.ui.components;

import com.xeloklox.dungeons.unleashed.utils.*;
import com.xeloklox.dungeons.unleashed.utils.lambda.*;
import com.xeloklox.dungeons.unleashed.utils.ui.*;
import net.minecraft.text.*;

//haha fuck default ui
public class UIButton extends UIComponent{
    public RegionNineSlice upTexture = UITextures.defaultButtonUp;
    public RegionNineSlice downTexture = UITextures.defaultButtonDown;
    Cons<UIButton> onClicked = b->{};
    boolean clicked = false;
    Text text = null;
    TextureRegion icon = null;
    int color = Utils.rgb(50,50,50);
    //animation
    public float shrink = 0;

    public UIButton(String id){
        super( id);
    }
    public static UIButton create(String id){
        return new UIButton(id);
    }

    public UIButton setText( Text text){
        this.text=text;
        recalcSize();
        return this;
    }
    public UIButton setIcon( TextureRegion icon){
        this.icon=icon;
        recalcSize();
        return this;
    }

    public UIButton setOnClicked(Cons<UIButton> onClicked){
        this.onClicked = onClicked;
        return this;
    }

    @Override
    public void resetMinSize(){
        super.resetMinSize();
        if(icon!=null){
            minw += icon.w;
            minh += icon.h;
        }
        if(text!=null){
            minw+=textw;
        }
    }


    @Override
    public void draw(SpriteDrawer sb){
        float g = 1-shrink;
        float inx = shrink*w*0.5f;
        float iny = shrink*h*0.5f;
        float ox = x+inx;
        float oy = y+iny;
        float aw = w-inx*2;
        float ah = h-iny*2;;
        if(clicked){
            downTexture.drawInner(sb,ox,oy,aw,ah);
        }else{
            upTexture.drawInner(sb,ox,oy,aw,ah);
        }
        if(icon!=null){
            sb.draw(icon,ox+padl*g,oy+padt*g, icon.w*g, icon.h*g);
        }
        float xoffset = padl;
        if(icon!=null){
            xoffset += icon.w+padl;
        }
        xoffset*=g;
        if(text!=null){
            this.handler.drawText(text, ox + xoffset, oy + padt*g, color);
        }
    }

    float textw = 0;

    @Override
    public void onAdd(){
        if(text!=null){
            textw = handler.textWidth(text);
        }
        resetMinSize();
        resetIdealSize();
    }

    @Override
    public void update(){

    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, int button){
        if(isInside(mouseX,mouseY)){
            clicked=true;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button){
        if(clicked){
            clicked=false;
            onClicked.get(this);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    public void triggerClick(){
        onClicked.get(this);
    }
}
