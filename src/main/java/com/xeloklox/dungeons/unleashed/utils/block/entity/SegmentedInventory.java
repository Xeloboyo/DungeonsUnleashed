package com.xeloklox.dungeons.unleashed.utils.block.entity;

import com.xeloklox.dungeons.unleashed.utils.lambda.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.util.math.*;
import org.jetbrains.annotations.*;
import org.mini2Dx.gdx.utils.*;

public interface SegmentedInventory extends SidedInventory,ImplementedInventory{

    Array<InventorySegment> getConfig();
    void setConfig(Array<InventorySegment> s);

    default void addSegment(InventorySegment is){
        if(getConfig()==null){
            setConfig(new Array<>());
        }
        getConfig().add(is);
        is.inventory=this;
    }

    default InventorySegment getSegment(String name){
        for(InventorySegment segment:getConfig()){
            if(segment.name.equals(name)){
                return segment;
            }
        }
        return null;
    }
    default InventorySegment getSegment(int slot){
        for(InventorySegment segment:getConfig()){
            for(Integer i:segment.slots){
                if(i==slot)
                    return segment;
            }
        }
        return null;
    }

    @Override
    default int[] getAvailableSlots(Direction side){
        if(getConfig()==null){ return new int[]{};}
        Array<Integer> got = new Array<>();
        for(InventorySegment segment:getConfig()){
            if(segment.insertFrom[side.getId()] || segment.extractFrom[side.getId()]){
                for(Integer i:segment.slots)
                    got.add(i);
            }
        }
        int[] a = new int[got.size];
        for(int i =0;i<got.size;i++){
            a[i]=got.get(i);
        }
        return a;
    }

    @Override
    default boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir){
        for(InventorySegment segment:getConfig()){
            if(dir==null || segment.insertFrom[dir.getId()]){
                for(Integer i:segment.slots)
                    if(i==slot)
                        return true;
            }
        }
        return false;
    }

    @Override
    default boolean canExtract(int slot, ItemStack stack, Direction dir){
        for(InventorySegment segment:getConfig()){
            if(dir==null || segment.extractFrom[dir.getId()]){
                for(Integer i:segment.slots)
                    if(i==slot)
                        return true;
            }
        }
        return false;
    }

    default int tryInsertIntoSegment(InventorySegment segment, ItemStack stack){
        ItemStack stackcpy = stack.copy();
        for(int i:segment.slots){
            int left = tryInsert(i,stackcpy);
            stackcpy.setCount(left);
            if(left==0){
                break;
            }
        }
        return stackcpy.getCount();
    }
    default void tryTransferIntoSegment(InventorySegment segment, int slot){
        ItemStack is = getItems().get(slot);
        int left = tryInsertIntoSegment(segment,is);
        is.setCount(left);
        if(getSegment(slot).syncOnChange){
            onChange(slot);
        }
    }

    @Override
    default void setStack(int slot, ItemStack stack){
        ImplementedInventory.super.setStack(slot, stack);
        if(getSegment(slot).syncOnChange){
            onChange(slot);
        }
    }

    @Override
    default int tryInsert(int slotindex, ItemStack stack){
        int o = ImplementedInventory.super.tryInsert(slotindex, stack);;
        if(o!=stack.getCount() && getSegment(slotindex).syncOnChange){
            onChange(slotindex);
        }
        return o;
    }

    default void onChange(int slot){}

    @Override
    default int getMaxCount(int slotindex){
        for(InventorySegment segment:getConfig()){
            for(Integer i:segment.slots)
                if(slotindex==i){
                    return segment.maxItems;
                }
        }
        return 64;
    }


    class InventorySegment{
        SegmentedInventory inventory;
        public String name;
        public int[] slots;
        boolean[] insertFrom = new boolean[6],extractFrom = new boolean[6];
        int maxItems=64;
        boolean syncOnChange=false;
        public static InventorySegment of(String name,int... slots){
            InventorySegment is = new InventorySegment();
            is.name=name;
            is.slots=slots;
            return is;
        }
        public int getFirstSlot(Boolf<ItemStack> cond){
            for(int i=0;i<slots.length;i++){
                ItemStack stack = inventory.getStack(slots[i]);
                if(!stack.isEmpty() && cond.get(stack)){
                    return slots[i];
                }
            }
            return -1;
        }

        public int getAnyFilledSlot(){return getFirstSlot(i->true);}


        public InventorySegment maxItems(int maxItems){
            this.maxItems=maxItems;
            return this;
        }

        public InventorySegment insertableFrom(Direction...directions){
            for(Direction d:directions){
                insertFrom[d.getId()]=true;
            }
            return this;
        }
        public InventorySegment universallyInsertable(){
            for(int i =0;i<6;i++){
                insertFrom[i]=true;
            }
            return this;
        }
        public InventorySegment extractableFrom(Direction...directions){
            for(Direction d:directions){
                extractFrom[d.getId()]=true;
            }
            return this;
        }
        public InventorySegment universallyExtractable(){
            for(int i =0;i<6;i++){
                extractFrom[i]=true;
            }
            return this;
        }

        public InventorySegment syncOnChange(boolean b){
            this.syncOnChange=b;
            return this;
        }
    }
}
