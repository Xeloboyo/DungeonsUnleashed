package com.xeloklox.dungeons.unleashed.blocks;

import com.xeloklox.dungeons.unleashed.utils.lambda.*;
import net.fabricmc.fabric.api.object.builder.v1.block.*;
import net.minecraft.block.*;
import net.minecraft.server.world.*;
import net.minecraft.util.math.*;

public class ModGrassBlock extends SpreadingBlock{
    Block dirt;
    int minlightLevel;

    public ModGrassBlock(Material material,  Block dirt, int minlightLevel, Func<FabricBlockSettings, FabricBlockSettings> func){
        super(material, func);
        this.dirt = dirt;
        this.minlightLevel = minlightLevel;
    }


    @Override
    public void decay(BlockState state, ServerWorld world, BlockPos pos){
        world.setBlockState(pos, dirt.getDefaultState());
    }

    @Override
    public boolean canGrow(BlockState state, ServerWorld world, BlockPos pos){
        return (world.getLightLevel(pos.up()) >= minlightLevel);
    }

    @Override
    public boolean spreadTo(BlockState state, ServerWorld world, BlockPos from, BlockPos to){

        if (world.getBlockState(to).isOf(dirt) && surviveCheck.get(state,world,to)) {
            world.setBlockState(to, getDefaultState());
        }
        return false;
    }
}
