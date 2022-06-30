package com.xeloklox.dungeons.unleashed.utils.ui.components;

import com.xeloklox.dungeons.unleashed.blockentity.screens.*;
import com.xeloklox.dungeons.unleashed.utils.ui.*;
import org.mini2Dx.gdx.utils.*;

public class UIFlexContainer extends UIContainer{
    public FlexItemAlign itemAlign = FlexItemAlign.STRETCH;
    public FlexDirection direction = FlexDirection.ROW;
    public FlexJustify justify = FlexJustify.START;
    Array<FlexItem> items = new Array<>();
    public UIFlexContainer(String id, AnimatedScreen screen){
        super(id, screen);
    }

    public static UIFlexContainer create(String body, AnimatedScreen researchMapScreen){
        return new UIFlexContainer(body,researchMapScreen);
    }

    public UIFlexContainer setDirection(FlexDirection direction){
        this.direction = direction;
        return this;
    }

    public UIFlexContainer setItemAlign(FlexItemAlign itemAlign){
        this.itemAlign = itemAlign;
        return this;
    }

    public void onRemove(UIComponent u){
        for(int i=0;i<items.size;i++){
            if(items.get(i).component.equals(u)){
                items.removeIndex(i);
                rebuild();
                return;
            }
        }
    }

    @Override
    public void resetMinSize(){
        super.resetMinSize();
        float t = direction.equals(FlexDirection.ROW)?(padl+padr):(padt+padb);
        for(var i:items){
            t += direction.equals(FlexDirection.ROW)?i.component.minw+i.component.marginl+i.component.marginr : i.component.minh+i.component.margint+i.component.marginb;
        }
        if(direction.equals(FlexDirection.ROW)){
            minw = Math.max(minw,t);
        }else{
            minh = Math.max(minh,t);
        }
    }
    public void resetIdealSize(){
        float t = direction.equals(FlexDirection.ROW)?(padl+padr):(padt+padb);
        float t2 = direction.equals(FlexDirection.COLUMN)?(padl+padr):(padt+padb);
        for(var i:items){
            float l = i.component.idealw+i.component.marginl+i.component.marginr;
            float l2 = i.component.idealh+i.component.margint+i.component.marginb;
            t += direction.equals(FlexDirection.ROW)?l :l2;
            t2 = Math.max(t2,direction.equals(FlexDirection.COLUMN)?l :l2);
        }
        if(direction.equals(FlexDirection.ROW)){
            idealw = Math.max(t,setw);
            idealh = Math.max(t2,seth);
        }else{
            idealw = Math.max(t2,setw);
            idealh = Math.max(t,seth);
        }
    }

    @Override
    public void add(UIComponent u){
        items.add(FlexItem.createFlexItem(this,u));
        super.add(u);

    }
    public FlexItem addFlex(UIComponent u){
        var f = FlexItem.createFlexItem(this,u);
        items.add(f);
        super.add(u);
        return f;
    }

    @Override
    public void rebuild(){
        //set lengths
        float totallength = 0;
        for(var i:items){
            i.setLength(i.basis.getInitialLength(i));
            totallength += i.getLength();
        }
        float leftover = getLength()-totallength;
        float justifyoffset = 0;
        if(totallength<getLength()){
            float totalgrow = 0;
            for(var i:items){
                totalgrow+=i.flexgrow;
            }
            if(totalgrow==0){
                switch(justify){
                    case CENTER:
                        justifyoffset += leftover*0.5f;
                        break;
                    case END:
                        justifyoffset+=leftover;
                        break;
                    default:
                }
            }
            for(var i:items){
                if(totalgrow>0 && i.flexgrow!=0){
                    i.setLength(i.getLength() + leftover*(i.flexgrow/totalgrow));
                }
            }
        }else{
            float totalshrink = 0;
            for(var i:items){
                totalshrink+=i.flexshrink;
            }
            for(var i:items){
                if(totalshrink>0 && i.flexshrink!=0){
                    i.setLength(i.getLength() + leftover*(i.flexshrink/totalshrink));
                }
            }
        }
        float offset = justifyoffset + (direction.equals(FlexDirection.ROW)?padl:padt);
        for(var i:items){
            i.setLengthOffset(offset);
            offset+=i.getLength();
        }
        //set thickness
        for(var i:items){
            FlexItemAlign align = i.align==null?itemAlign:i.align;
            switch(align){
                case CENTER:
                    i.setThickOffset((getThickness()-getThickness(i.component))*0.5f + (direction.equals(FlexDirection.ROW)?padt:padr));
                    i.setThickness(Math.min(getThickness(),getThickness(i.component)));
                break;
                case END:
                    i.setThickOffset(getThickness()-getThickness(i.component) + (direction.equals(FlexDirection.ROW)?padt:padr));
                    i.setThickness(Math.min(getThickness(),getThickness(i.component)));
                break;
                case START:
                    i.setThickOffset(direction.equals(FlexDirection.ROW)?padt:padr);
                    i.setThickness(Math.min(getThickness(),getThickness(i.component)));
                break;
                case STRETCH:
                default:
                    i.setThickOffset(direction.equals(FlexDirection.ROW)?padt:padr);
                    i.setThickness(getThickness());
            }
        }
        for(var i:items){
            i.component.fitInto(i.x,i.y,i.w,i.h);
        }

        super.rebuild();
    }


    public static class FlexItem{
        public float flexgrow=0, flexshrink =1;
        public FlexItemBasis basis = FlexItemBasis.auto();
        public FlexItemAlign align=null;
        public UIFlexContainer container;
        public UIComponent component;

        float x,y,w,h;

        public FlexItem(UIFlexContainer con,UIComponent component){
            this.component = component;
            this.container=con;
        }

        public FlexItem setFlexgrow(float flexgrow){
            this.flexgrow = Math.max(0,flexgrow);
            return this;
        }

        public FlexItem setFlexshrink(float flexshrink){
            this.flexshrink = Math.max(0, flexshrink);
            return this;
        }

        public FlexItem setBasis(FlexItemBasis basis){
            this.basis = basis;
            return this;
        }

        public FlexItem setAlign(FlexItemAlign align){
            this.align = align;
            return this;
        }

        public static FlexItem createFlexItem(UIFlexContainer con,UIComponent component){
            return new FlexItem(con, component);
        }
        void setLengthOffset(float l){
            if(container.direction.equals(FlexDirection.ROW)){
                x = l;
            }else{
                y = l;
            }
        }
        void setThickOffset(float l){
            if(container.direction.equals(FlexDirection.ROW)){
                y = l;
            }else{
                x = l;
            }
        }
        void setLength(float l){
            if(container.direction.equals(FlexDirection.ROW)){
                w = l;
            }else{
                h = l;
            }
        }
        float getLength(){
            if(container.direction.equals(FlexDirection.ROW)){
                return w;
            }else{
                return h;
            }
        }
        void setThickness(float l){
            if(container.direction.equals(FlexDirection.ROW)){
                h = l;
            }else{
                w = l;
            }
        }
    }

    float getLength(){
        return direction.equals(FlexDirection.ROW)?w-padl-padr:h-padt-padb;
    }
    float getThickness(){
        return direction.equals(FlexDirection.ROW)?h-padt-padb:w-padl-padr;
    }
    float getLength(UIComponent c){
        return direction.equals(FlexDirection.ROW)?c.idealw+c.marginl+c.marginr:c.idealh+c.margint+c.marginb;
    }
    float getThickness(UIComponent c){
        return direction.equals(FlexDirection.ROW)?c.idealh+c.margint+c.marginb:c.idealw+c.marginl+c.marginr;
    }

    public static class FlexItemBasis{
        boolean auto;
        float value;
        boolean percent;

        private FlexItemBasis(boolean auto, float value, boolean percent){
            this.auto = auto;
            this.value = value;
            this.percent = percent;
        }

        public static FlexItemBasis auto(){
            return new FlexItemBasis(true,0,false);
        }
        public static FlexItemBasis length(float length){
            return new FlexItemBasis(false,length,false);
        }
        public static FlexItemBasis percent(float per){
            return new FlexItemBasis(false,per,true);
        }

        float getInitialLength(FlexItem e){
            if(auto){
                return e.container.getLength(e.component);
            }
            return percent?e.container.getLength()*value:value;
        }
    }

    public enum FlexItemAlign{
        CENTER,START,END, STRETCH;
    }
    public enum FlexJustify{
        CENTER,START,END;
    }
    public enum FlexDirection{
        ROW,COLUMN;
    }

}
