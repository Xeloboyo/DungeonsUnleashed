package com.xeloklox.dungeons.unleashed.items;

import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.screen.slot.*;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraft.world.explosion.*;

public class UnstableItem extends Item{
    public UnstableItem(Settings settings){
        super(settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected){
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand){
        //throw an normal ende pearl the other 50% times
        ItemStack itemStack = user.getStackInHand(hand);
        if(!world.isClient && Math.random()>0.5){
            world.createExplosion(user,user.getX(),user.getY(),user.getZ(), (float)(0.8f+Math.random()*0.8),true, Explosion.DestructionType.DESTROY);
            itemStack.decrement(1);
        }
        return  TypedActionResult.success(itemStack, world.isClient());
    }



}
