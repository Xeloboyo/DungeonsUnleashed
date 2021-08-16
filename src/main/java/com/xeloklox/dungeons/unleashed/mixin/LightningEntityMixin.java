package com.xeloklox.dungeons.unleashed.mixin;

import com.xeloklox.dungeons.unleashed.blocks.*;
import net.minecraft.entity.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.util.*;

@Mixin(LightningEntity.class)
public class LightningEntityMixin{
    @Inject(method = "cleanOxidizationAround(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)Ljava/util/Optional;", at = @At("RETURN"), locals=LocalCapture.CAPTURE_FAILSOFT)
    private static void clean(World world, BlockPos pos, CallbackInfoReturnable<Optional<BlockPos>> cir, Iterator var2, BlockPos blockPos){
        if(world.getBlockState(blockPos).getBlock() instanceof IAffectedByLightning block){
            block.onStruck(world,blockPos);
        }
    }
}
