package com.xeloklox.dungeons.unleashed.utils.ui.components;

import com.xeloklox.dungeons.unleashed.utils.ui.*;
import net.minecraft.item.*;
import net.minecraft.screen.slot.*;

import static com.xeloklox.dungeons.unleashed.utils.ui.UITextures.dropShadowItem;

public class UILooseItem extends UIComponent{
    ItemStack stack;
    float bounce = 15,bv = 0;
    int id;

    public UILooseItem(String id, ItemStack stack,float bounce, int slotid){
        super(id);
        this.stack=stack;
        this.bounce=bounce;
        this.resize(16,16);
        this.setMinimumSize(16,16);
        this.id=slotid;
    }

    @Override
    public void draw(SpriteDrawer sb){
        sb.drawCentered(dropShadowItem,x+8,y+10);
        sb.drawItem(stack,x+8,y+8-bounce,getZ());

    }

    @Override
    public void update(){
        Slot s = handler.getScreenHandler().getSlot(id);
        if(!s.getStack().isEmpty()){
            stack = s.getStack();
        }
        bv -=0.5;
        bounce +=bv;
        if(bounce<0){
            bounce = 0;
            bv*=-0.5;
        }
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, int button){
        if(isInside(mouseX,mouseY)&&handler.getCursorStack().isEmpty()){
            if(button==0 || stack.getCount()==1){
                handler.setCursorStack(stack);
                remove = true;
            }else if(button==1){
                handler.setCursorStack(stack.split(stack.getCount()/2));
            }
            handler.screen.getClient().interactionManager.clickSlot(handler.getScreenHandler().syncId, id, button, SlotActionType.PICKUP, handler.screen.getClient().player);
            return true;
        }
        return false;
    }

    public void directRemove(){
        remove=true;
    }


}
