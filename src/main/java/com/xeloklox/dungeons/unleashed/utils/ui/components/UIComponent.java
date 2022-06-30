package com.xeloklox.dungeons.unleashed.utils.ui.components;

import com.xeloklox.dungeons.unleashed.utils.ui.*;
import net.minecraft.util.math.*;

public abstract class UIComponent{
    protected float x,y,w,h;
    //dimensions set externally
    public float setw,seth;
    //ideal dimensions
    public float idealw,idealh;

    //minimum dimensions set externally
    public float msetw,mseth;
    //minimum dimensions
    public float minw=16,minh=16;
    public float left=0,right=0,bottom=0,top=0;
    public static float DISABLE = -999999;

    public String id;
    private float z;
    public UIHandler handler;
    public UIComponent parent;
    boolean fixedSize = false;
    public boolean disabled;
    boolean remove=false;

    public float padl=5,padr=5,padt=5,padb=5;
    public float marginl=0,marginr=0,margint=0,marginb=0;

    public UIComponent(String id){
        this.id = id;
    }

    public abstract void draw(SpriteDrawer sb);
    public void drawAbove(SpriteDrawer sb){}
    public abstract void update();

    public boolean mouseReleased(double mouseX, double mouseY, int button) {return false;}
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {return false;}
    public boolean mouseClicked(float mouseX, float mouseY, int button) {return false;}
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {return false;}
    public boolean charTyped(int keyCode, int scanCode, int modifiers){return false;}
    public boolean mouseScrolled(double mouseX, double mouseY, double amount){return false;}
    public void rebuild(){
        if(parent==null){
            recalcSize();
            w = Math.max(idealw,setw);
            h = Math.max(idealh,seth);
            x = marginl + left;
            y = margint + top;
        }
    }
    //should not be called directly.
    public void resetMinSize(){
        minw=padl+padr;
        minh=padt+padb;
    }
    //should not be called directly.
    public void resetIdealSize(){
        idealw=minw;
        idealh=minh;
    }

    public void onAdd(){}

    public Vec2f getAbsolutePos(){
        return getAbsolutePos(true);
    }
    public Vec2f getAbsolutePos(boolean pan){
        if(parent==null){
            return new Vec2f(x,y);
        }
        Vec2f v =  parent.getAbsolutePos(pan).add(new Vec2f(x,y));
        if(pan && parent instanceof UIPannableContainer p){
            v = v.add(new Vec2f(p.panx,p.pany));
        }
        return v;
    }

    public UIComponent setZ(float z){
        this.z = z;
        if(handler!=null){
            handler.Zchanged();
        }
        return this;
    }

    public float getZ(){
        return z;
    }
    public UIComponent fitToMinSize(){
        this.idealw = minw;
        this.idealh = minh;
        return this;
    }
    public UIComponent resize(float w, float h){
        setw = w;
        seth = h;
        if(!fixedSize){
            this.idealw = Math.max(idealw,setw);
            this.idealh = Math.max(idealh,seth);
            idealw = Math.max(idealw,minw);
            idealh = Math.max(idealh,minh);
            rebuild();
        }
        return this;
    }
    protected UIComponent setSize(float w, float h){
        this.w = w;
        this.h = h;
        return this;
    }

    UIComponent fitInto(float x, float y, float w, float h){
        x+=marginl; w-=marginr+marginl;
        y+=margint; h-=marginb+margint;
        float aw = Math.min(w,idealw);
        if(left>DISABLE && right>DISABLE){
            aw = w;
        }else if(right>DISABLE){
            x += w-aw;
            x-= right;
        }else{
            x += left;
        }
        float ah = Math.min(h,idealh);
        if(top>DISABLE && bottom>DISABLE){
            ah = h;
        }else if(bottom>DISABLE){
            y += h-ah;
            y -= bottom;
        }else{
            y += top;
        }
        setSize(aw,ah);
        setPos(x,y);
        return this;
    }

    public UIComponent setFixedSize(boolean fixedSize){
        this.fixedSize = fixedSize;
        if(fixedSize){
            setMinimumSize(w,h);
        }
        return this;
    }

    public UIComponent setMinimumSize(float w, float h){
        this.msetw=w;
        this.mseth=h;
        recalcSize();
        return this;
    }
    public void recalcSize(){
        resetMinSize();
        minw = Math.max(msetw,minw);
        minh = Math.max(mseth,minh);
        resetIdealSize();
        idealw=Math.max(idealw,setw);
        idealh=Math.max(idealh,seth);
    }

    UIComponent setPos(float x, float y){
        this.x=x;
        this.y=y;
        return this;
    }
    public UIComponent setPositioning(float left, float right, float top, float bottom){
        this.left=left;
        this.right=right;
        this.top=top;
        this.bottom=bottom;
        rebuild();
        return this;
    }
    public UIComponent setPositioning(float left,float top){
        this.left=left;
        this.right=DISABLE;
        this.top=top;
        this.bottom=DISABLE;
        rebuild();
        return this;
    }

    public UIComponent pad(float p){
        padl = p;
        padr = p;
        padt = p;
        padb = p;
        recalcSize();
        rebuild();

        return this;
    }
    public UIComponent padTop(float p){
        padt = p;
        recalcSize();
        rebuild();

        return this;
    }
    public UIComponent padBottom(float p){
        padb = p;
        recalcSize();
        rebuild();
        return this;
    }
    public UIComponent margin(float p){
        marginl = p;
        marginr = p;
        margint = p;
        marginb = p;
        if(parent!=null){
            parent.rebuild();
        }
        return this;
    }
    public UIComponent marginBottom(float p){
        marginb = p;
        if(parent!=null){
            parent.rebuild();
        }
        return this;
    }
    public UIComponent marginRight(float p){
        marginr = p;
        if(parent!=null){
            parent.rebuild();
        }
        return this;
    }
    public UIComponent marginLeft(float p){
        marginl = p;
        if(parent!=null){
            parent.rebuild();
        }
        return this;
    }
    public UIComponent marginTop(float p){
        margint = p;
        if(parent!=null){
            parent.rebuild();
        }
        return this;
    }

    public boolean isInside(float px,float py){
        return px>=x && py>=y && px<=x+w && py<=y+h;
    }

    public float getX(){
        return x;
    }

    public float getY(){
        return y;
    }

    public void setFocus(){
        this.handler.focus = this;
        if(this.parent!=null){
            this.parent.onChildFocused(this);
            this.parent.setFocus();
        }
    }
    public void unsetFocus(){
        this.handler.focus = null;
        if(this.parent!=null){
            this.parent.onChildUnfocused(this);
            this.parent.unsetFocus();
        }
    }

    public void onChildFocused(UIComponent c){}
    public void onChildUnfocused(UIComponent c){}

    public float mx(){return x+w*0.5f;}
    public float my(){return y+h*0.5f;}

    public float getW(){
        return w;
    }

    public float getH(){
        return h;
    }
}
