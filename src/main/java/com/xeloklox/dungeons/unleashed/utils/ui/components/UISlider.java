package com.xeloklox.dungeons.unleashed.utils.ui.components;

import com.xeloklox.dungeons.unleashed.utils.*;
import com.xeloklox.dungeons.unleashed.utils.lambda.Cons.*;
import com.xeloklox.dungeons.unleashed.utils.ui.*;
import net.minecraft.util.math.*;

public class UISlider extends UIComponent{
    float value ,min ,max;
    float sliderpos = 0;
    Cons2<UISlider,Float> valueChanged = (a,b)->{};
    RegionNineSlice bg = UITextures.insetShadow;
    RegionNineSlice sliderVertical = UITextures.sliderVertical;

    public UISlider(String id){
        super(id);
    }
    public static UISlider create(String id){
        return new UISlider(id);
    }

    @Override
    public void resetMinSize(){
        super.resetMinSize();
        minw = Math.max(10,minw);
        minh = Math.max(10,minh);
    }

    public UISlider setValues(float value, float min, float max){
        value = MathHelper.clamp(value,min,max);
        this.value=value;
        this.max=max;
        this.min=min;
        return this;
    }

    @Override
    public void draw(SpriteDrawer sb){
        if(w<0||h<0){
            return;
        }
        bg.drawInner(sb,x,y,w,h);
        if(h>=w){
            if(dragging){
                sb.setColor(0.7f);
            }
            sliderVertical.drawInner(sb,x,sliderpos,w,10);
            sb.resetColor();
        }
    }

    public UISlider onValueChanged(Cons2<UISlider, Float> valueChanged){
        this.valueChanged = valueChanged;
        return this;
    }

    @Override
    public void update(){
        sliderpos = Mathf.map(value,min,max,5,h>w?h-5:w-5);
    }

    boolean dragging = false;



    @Override
    public boolean mouseClicked(float mouseX, float mouseY, int button){
        if(isInside(mouseX,mouseY)){
            if(h>=w && Math.abs((mouseY-y)-sliderpos)<=5){
                dragging = true;
                setFocus();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY){
        if(dragging){
            if(h>=w){
                value = Mathf.mapClamped((float)mouseY,y+5,y+h-5,min,max);
            }else{
                value = Mathf.mapClamped((float)mouseX,x+5,x+w-5,min,max);
            }
            valueChanged.get(this,value);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button){
        if(dragging){
            dragging = false;
            unsetFocus();
        }
        return false;
    }
}
