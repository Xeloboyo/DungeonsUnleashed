package com.xeloklox.dungeons.unleashed.mixin;

import com.xeloklox.dungeons.unleashed.utils.item.*;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.*;
import net.minecraft.item.*;
import net.minecraft.world.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(ItemEntity.class)
public class ItemEntityMixin{
    @Inject(method = "<init>(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/World;)V", at = @At("RETURN"))
    private void init(EntityType<? extends ItemEntity> entityType, World world, CallbackInfo ci){
        ItemEntity iec = (ItemEntity)(Object)this;
        ItemEntityWrapper.onEntitySpawn(iec);
    }

    @Inject(method = "<init>(Lnet/minecraft/world/World;DDDLnet/minecraft/item/ItemStack;DDD)V" , at = @At("RETURN"))
    private void init(World world, double x, double y, double z, ItemStack stack, double velocityX, double velocityY, double velocityZ, CallbackInfo ci){
        ItemEntity iec = (ItemEntity)(Object)this;
        ItemEntityWrapper.onItemChanged(iec);
    }

    @Inject(method = "<init>(Lnet/minecraft/entity/ItemEntity;)V", at = @At("RETURN"))
    private void init(ItemEntity entity, CallbackInfo ci){

    }

    @Inject(method = "tick",at=@At("RETURN"))
    private void tick(CallbackInfo ci){
        ItemEntity iec = (ItemEntity)(Object)this;
        ItemEntityWrapper wrap = ItemEntityWrapper.entityWrappers.get(iec);
        if(wrap!=null){
            if(iec.isRemoved()){
                wrap.remove();
            }else{
                wrap.tick();
                if(iec.isRemoved()){
                    wrap.remove();
                }
            }
        }
    }

    @Inject(method = "damage",at = @At("RETURN"))
    private void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir){
        ItemEntity iec = (ItemEntity)(Object)this;
        ItemEntityWrapper wrap = ItemEntityWrapper.entityWrappers.get(iec);
        if(wrap!=null){
            if(iec.isRemoved()){
                wrap.remove();
            }else{
                wrap.damage(source, amount);
            }
        }
    }


}
