package com.xeloklox.dungeons.unleashed.items;

import net.minecraft.entity.*;
import net.minecraft.entity.damage.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.world.*;

public abstract class ItemEntityHook{
    protected ItemEntityWrapper wrapper;
    public boolean toRemove=false;

    public abstract void onTick();
    public abstract void onRemove();
    public abstract void onDamage(DamageSource source, float amount);

    public World world(){
        return wrapper.getEntity().world;
    }
    public ItemEntity itemEntity(){
        return wrapper.getEntity();
    }
    public ItemStack itemstack(){
        return wrapper.getEntity().getStack();
    }
    public NbtCompound getNBT(){
        return itemEntity().getStack().getOrCreateNbt();
    }
    public float getOrCreateTag(String tag,float defaultvalue){
        NbtCompound nbt = getNBT();
        if(!nbt.contains(tag)){
            nbt.putFloat(tag,defaultvalue);
        }
        return nbt.getFloat(tag);
    }
    public int getOrCreateTag(String tag,int defaultvalue){
        NbtCompound nbt = getNBT();
        if(!nbt.contains(tag)){
            nbt.putInt(tag,defaultvalue);
        }
        return nbt.getInt(tag);
    }
    public boolean hasTag(String tag){
        if(!itemEntity().getStack().hasNbt()){return false;}
        return getNBT().contains(tag);
    }


}
