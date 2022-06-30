package com.xeloklox.dungeons.unleashed.utils.block.entity;

import net.minecraft.item.*;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.screen.*;
import net.minecraft.util.collection.*;

public class HiddenInventory implements ImplementedInventory{
    private final ScreenHandler handler;
    DefaultedList<ItemStack> items;
    public int ids[];
    public HiddenInventory(ScreenHandler handler, int slots) {
        this.handler=handler;
        items =  DefaultedList.ofSize(slots,ItemStack.EMPTY);
        ids = new int[slots];
    }
    @Override
    public DefaultedList<ItemStack> getItems(){
        return items;
    }

    @Override
    public void onChange(int slot){
        this.handler.onContentChanged(this);
    }
    public int occupiedSlot(){
        for(int i=0;i<size();i++){
           if(!getStack(i).isEmpty()){
               return ids[i];
           }
        }
        return -1;
    }
    public int freeSlot(){
        for(int i=0;i<size();i++){
            if(getStack(i).isEmpty()){
                return ids[i];
            }
        }
        return -1;
    }
}
