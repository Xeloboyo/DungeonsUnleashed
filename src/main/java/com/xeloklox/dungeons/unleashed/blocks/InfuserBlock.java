package com.xeloklox.dungeons.unleashed.blocks;

import com.xeloklox.dungeons.unleashed.*;
import com.xeloklox.dungeons.unleashed.blockentity.*;
import com.xeloklox.dungeons.unleashed.utils.block.*;
import com.xeloklox.dungeons.unleashed.utils.lambda.*;
import net.fabricmc.fabric.api.object.builder.v1.block.*;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import org.jetbrains.annotations.*;

public class InfuserBlock extends ChargeConnectorBlock implements BlockEntityProvider{
    public static Vec3f[] infuserConnectionPoints = {new Vec3f(-1,0,0),new Vec3f(0,0,1),new Vec3f(1,0,0)};
    public InfuserBlock(Material material, Func<FabricBlockSettings, FabricBlockSettings> func){
        super(material, func, basicBlock -> {
            basicBlock.setPlacementConfig(BasicBlock.HORIZONTAL_FACING_PLAYER_PLACEMENT);
        }, infuserConnectionPoints);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state){
        return new InfuserEntity(pos,state);
    }

    @Override
    public <T extends BlockEntity> Class<T> getEntityClass(){
        return (Class<T>)InfuserEntity.class;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlocks.INFUSER_ENTITY.get(), (world1, pos, state1, be) -> be.tick(world1, pos, state1, be));
    }

}
