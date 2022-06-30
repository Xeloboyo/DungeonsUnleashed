package com.xeloklox.dungeons.unleashed.utils.ui.components;

import com.mojang.blaze3d.systems.*;
import com.xeloklox.dungeons.unleashed.utils.animation.Interpolations.*;
import com.xeloklox.dungeons.unleashed.utils.lambda.*;
import com.xeloklox.dungeons.unleashed.utils.ui.*;
import net.minecraft.item.*;
import net.minecraft.screen.*;
import net.minecraft.text.*;
import org.mini2Dx.gdx.utils.*;

public class UIHandler{
    public Array<UIComponent> components = new Array<>();
    public Array<UIContainer> containers = new Array<>();
    public ObjectMap<String,UIComponent> uiComponentMap = new ObjectMap<>();
    IntMap<KeyPress> keyPresses = new IntMap<>();
    Cons<UIComponent> onRemove = (c)->{};
    AnimatedScreen screen;

    UIComponent focus = null;

    public UIHandler(AnimatedScreen screen){
        this.screen = screen;
    }

    public void clear(){
        containers.clear();
        components.clear();
        uiComponentMap.clear();
        keyPresses.clear();
    }

    public void add(UIComponent comp){
        if(uiComponentMap.containsKey(comp.id)){
            throw new IllegalArgumentException("already added");
        }
        components.add(comp);
        comp.handler=this;
        comp.onAdd();
        uiComponentMap.put(comp.id,comp);
        if(comp instanceof UIContainer u){
            containers.add(u);
        }
        Zchanged();

    }

    public boolean remove(UIComponent comp){
        if(uiComponentMap.containsKey(comp.id)){
            onRemove.get(comp);
            uiComponentMap.remove(comp.id);
            components.removeValue(comp,true);
            if(comp instanceof UIContainer u){
                containers.removeValue(u,true);
            }
            return true;
        }
        return false;
    }

    public <T extends UIComponent> T get(Class<T> c, String id){
        if(uiComponentMap.containsKey(id)){
            return (T)uiComponentMap.get(id);
        }
        for(int i=0;i<containers.size;i++){
            T t =containers.get(i).children.get(c,id);
            if(t!=null){
                return t;
            }
        }
        return null;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for(int i=components.size-1;i>=0;i--){
            if(components.get(i).mouseReleased(mouseX,mouseY,button)){
                return true;
            }
        }
        return false;
    }
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if(focus!=null && focus.mouseDragged(mouseX,mouseY,button,deltaX,deltaY)){
            return true;
        }
        for(int i=components.size-1;i>=0;i--){
            if(components.get(i).mouseDragged(mouseX,mouseY,button,deltaX,deltaY)){
                return true;
            }
        }
        return false;
    }
    public boolean mouseClicked(float mouseX, float mouseY, int button) {
        for(int i=components.size-1;i>=0;i--){
            if(!components.get(i).disabled && components.get(i).mouseClicked(mouseX,mouseY,button)){
                return true;
            }
        }
        return false;
    }
    public void mouseScrolled(double mouseX, double mouseY, double amount){

    }
    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        keyPresses.put(scanCode,new KeyPress(scanCode));
    }
    public void keyReleased(int keyCode, int scanCode, int modifiers){
        keyPresses.remove(scanCode);
    }
    public void charTyped(int keyCode, int modifiers){

    }

    public void draw(){
        for(int i=0;i<components.size;i++){
            UIComponent c = components.get(i);
            if(c.disabled){continue;}
            c.draw(screen.spriteDrawer);
        }
        for(int i=0;i<components.size;i++){
            if(components.get(i).disabled){continue;}
            components.get(i).drawAbove(screen.spriteDrawer);
        }
    }

    public void update(){
        for(int i=0;i<components.size;i++){
            if(components.get(i).disabled){continue;}
            components.get(i).update();
            if(components.get(i).remove){
                remove(components.get(i));
                i--;
            }
        }
        for(int i=0;i<animations.size;i++){
            animations.get(i).ui.animate(animations.get(i).se.interpolate(0,1,animations.get(i).t/animations.get(i).time,0.2f));
            animations.get(i).t++;
            if(animations.get(i).t>animations.get(i).time){
                animations.get(i).ui.animate(1);
                animations.removeIndex(i);
                i--;
            }
        }
    }

    public void Zchanged(){
        components.sort((a,b)->Float.compare(a.getZ(),b.getZ()));
    }




    class KeyPress{
        int keycode;
        int duration=0;

        public KeyPress(int keycode){
            this.keycode = keycode;
        }
    }
    public ItemStack getCursorStack(){
        return screen.getScreenHandler().getCursorStack();
    }
    public void setCursorStack(ItemStack u){
        this.screen.getScreenHandler().onContentChanged(screen.getInvScreenHandler().pl);
        screen.getScreenHandler().setCursorStack(u);
    }
    public ScreenHandler getScreenHandler(){
        return this.screen.getScreenHandler();
    }

    //text drawin
    public void wrapLines(Text text, float width, Cons<OrderedText> cons){
        this.screen.getTextRenderer().wrapLines(text,(int)width).forEach(cons::get);
    }
    public void drawText(Text text, float x, float y, int color){
        screen.spriteDrawer.end();
        this.screen.getTextRenderer().draw(screen.spriteDrawer.getMatrixStack(), text, x , y , color);
        screen.spriteDrawer.resetTex();
        screen.spriteDrawer.begin();
        RenderSystem.enableBlend();
    }
    public void drawText(OrderedText text, float x, float y, int color){
        screen.spriteDrawer.end();
        this.screen.getTextRenderer().draw(screen.spriteDrawer.getMatrixStack(), text, x , y , color);
        screen.spriteDrawer.resetTex();
        screen.spriteDrawer.begin();
        RenderSystem.enableBlend();
    }
    public float textWidth(Text text){
        return this.screen.getTextRenderer().getWidth(text);
    }

    Array<UIAnimator> animations = new Array<>();

    public void addAnimation(float time, SingularInterpolateType se, UIAnimationI ui){
        animations.add(new UIAnimator(time,se,ui));
    }

    public static class UIAnimator{
        float time;
        float t=0;
        UIAnimationI ui;
        SingularInterpolateType se;

        public UIAnimator(float time,SingularInterpolateType se, UIAnimationI ui){
            this.time = time;
            this.ui = ui;
            this.se=se;
        }

    }

    public interface UIAnimationI{
        public void animate(float trans);
    }


}
