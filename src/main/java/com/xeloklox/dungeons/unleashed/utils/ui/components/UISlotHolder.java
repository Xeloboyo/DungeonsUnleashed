package com.xeloklox.dungeons.unleashed.utils.ui.components;

import com.xeloklox.dungeons.unleashed.blockentity.screens.*;
import com.xeloklox.dungeons.unleashed.utils.*;
import com.xeloklox.dungeons.unleashed.utils.animation.Interpolations.*;
import com.xeloklox.dungeons.unleashed.utils.ui.*;
import net.minecraft.inventory.*;
import net.minecraft.screen.slot.*;
import net.minecraft.util.math.*;
import org.mini2Dx.gdx.utils.*;


public class UISlotHolder extends UIComponent{
    public TextureRegion bg;
    public boolean drawSlots = true;
    Array<SlotWrapper> slots = new Array<>();

    public UISlotHolder(String id, InventoryScreenHandler screenHandler, Inventory s){
        super(id);
        Wrapper<Integer> minx=new Wrapper<>(9999),miny=new Wrapper<>(9999);
        screenHandler.slots.forEach(c->{
            if(c.inventory==s){
                minx.val = Math.min(minx.val,c.x);
                miny.val = Math.min(miny.val,c.y);
            }
        });
        screenHandler.slots.forEach(c->{
            if(c.inventory==s){
                slots.add(new SlotWrapper(c,c.x-minx.val,c.y-miny.val));
                minw = Math.max(minw,c.x+18-minx.val);
                minh = Math.max(minh,c.y+18-miny.val);
            }
        });
        setMinimumSize(minw,minh);
    }

    @Override
    public void resetMinSize(){
        minw=0;minh=0;
        for(var v:slots){
            minw = Math.max(minw,v.ox+18);
            minh = Math.max(minh,v.oy+18);
        }
    }
    @Override
    public void draw(SpriteDrawer sb){
        if(bg!=null){
            sb.draw(bg,x,y,w,h);
        }
        if(drawSlots)
        for(var v:slots){
            sb.draw(UITextures.inventorySlot,v.ox+x,v.oy+y,18,18);
        }
    }

    @Override
    public void update(){
        Vec2f pos = getAbsolutePos();
        for(var v:slots){
            v.setPos(pos.x,pos.y);
        }
    }

    class SlotWrapper{
        Slot slot;
        float ox,oy;
        SlotWrapper(Slot s,float ox,float oy){
            this.ox=ox;
            this.oy=oy;
            slot=s;
        }

        void setPos(float rx,float ry){
            Utils.setFinalInt(slot,"x",(int)(ox+rx));
            Utils.setFinalInt(slot,"y",(int)(oy+ry));
        }
    }

}
