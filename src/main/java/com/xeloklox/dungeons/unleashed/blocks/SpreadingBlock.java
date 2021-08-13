package com.xeloklox.dungeons.unleashed.blocks;

import com.xeloklox.dungeons.unleashed.utils.*;
import net.fabricmc.fabric.api.object.builder.v1.block.*;
import net.minecraft.block.*;
import net.minecraft.server.world.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraft.world.chunk.light.*;

import java.util.*;

public abstract class SpreadingBlock extends BasicBlock{

    //if block is covered up, die.
    protected BlockCons<Boolean> surviveCheck = (state, world, pos) -> {
        BlockPos blockPos = pos.up();
        BlockState blockState = world.getBlockState(blockPos);
        int i = ChunkLightProvider.getRealisticOpacity(world, state, pos, blockState, blockPos, Direction.UP, blockState.getOpacity(world, blockPos));
        return i < world.getMaxLightLevel();
    };
    public int spreadattempts = 4;
    public int spreadX = 1,spreadY = 2,spreadZ = 1;

    public SpreadingBlock(Material material, Func<FabricBlockSettings, FabricBlockSettings> func){
        super(material, func);
    }

    public abstract void decay(BlockState state, ServerWorld world, BlockPos pos);
    public abstract boolean canGrow(BlockState state, ServerWorld world, BlockPos pos);
    public abstract boolean spreadTo(BlockState state, ServerWorld world, BlockPos from, BlockPos to);


    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if(!surviveCheck.get(state,world,pos)){
            decay(state,world,pos);
            return;
        }
        if(canGrow(state,world,pos)){
            for(int i = 0;i<spreadattempts;i++){
                BlockPos blockPos = pos.add(random.nextInt(spreadX*2+1) - spreadX, random.nextInt(spreadY*2+1) - spreadY, random.nextInt(spreadZ*2+1) - spreadZ);
                spreadTo(state, world, pos, blockPos);
            }
        }
    }
}
