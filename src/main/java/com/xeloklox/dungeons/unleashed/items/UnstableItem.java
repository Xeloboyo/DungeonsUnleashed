package com.xeloklox.dungeons.unleashed.items;

import com.xeloklox.dungeons.unleashed.items.hooks.*;
import com.xeloklox.dungeons.unleashed.utils.lambda.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.entity.projectile.thrown.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraft.world.explosion.*;

public class UnstableItem extends Item{
    public float explosionDelay = 250f;
    public UnstableItem(Settings settings, Func<UnstableExplosiveItemHook,UnstableExplosiveItemHook> explosionSettings){
        super(settings);
        ItemEntityWrapper.addHookSelector((item)->{
            if(item.getStack().isOf(this)){
                return explosionSettings.get(new UnstableExplosiveItemHook());
            }
            return null;
        });
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand){
        //throw an normal ende pearl the other 50% times
        ItemStack itemStack = user.getStackInHand(hand);
        if(!world.isClient && Math.random()>0.5){
            world.createExplosion(user,user.getX(),user.getY(),user.getZ(), (float)(0.8f+Math.random()*0.8),true, Explosion.DestructionType.DESTROY);
            itemStack.decrement(1);
        }else
        if(!world.isClient){
            EnderPearlEntity enderPearlEntity = new EnderPearlEntity(world, user);
            enderPearlEntity.setItem(itemStack);
            enderPearlEntity.setProperties(user, user.getPitch(), user.getYaw(), 0.0F, 1.5F, 1.0F);
            world.spawnEntity(enderPearlEntity);
        }

        return  TypedActionResult.success(itemStack, world.isClient());
    }

    public static float getExplosionCharge(ItemStack is){
        if(is.getItem() instanceof UnstableItem unstableItem){
            var nbt = is.getNbt();
            if(nbt==null){
                return 0;
            }
            return nbt.getInt(UnstableExplosiveItemHook.EXPLOSION_CHARGE_TAG)/unstableItem.explosionDelay;
        }
        return 0;
    }



}
