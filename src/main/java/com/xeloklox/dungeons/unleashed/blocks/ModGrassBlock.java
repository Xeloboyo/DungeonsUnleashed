package com.xeloklox.dungeons.unleashed.blocks;

import com.xeloklox.dungeons.unleashed.utils.*;
import com.xeloklox.dungeons.unleashed.utils.block.*;
import com.xeloklox.dungeons.unleashed.utils.lambda.*;
import net.fabricmc.fabric.api.object.builder.v1.block.*;
import net.minecraft.block.*;
import net.minecraft.server.world.*;
import net.minecraft.util.math.*;

public class ModGrassBlock extends SpreadingBlock{
    RegisteredBlock[] stages;
    int minlightLevel, growlightLevel;
    float growchance = 0.3f;

    public ModGrassBlock(Material material, RegisteredBlock[] stages, int minlightLevel, int growlightLevel, Func<FabricBlockSettings, FabricBlockSettings> func){
        super(material, func);
        this.stages = stages;
        this.minlightLevel = minlightLevel;
        this.growlightLevel=growlightLevel;
    }

    private int stage = -1;
    public int getStage(){
        if(stage==-1){
            for(int i =0 ;i<stages.length;i++){
                if(stages[i].get().equals(this)){
                    stage =  i;
                }
            }
        }
        return stage;
    }

    @Override
    public void decay(BlockState state, ServerWorld world, BlockPos pos){
        if(getStage()>0){
            world.setBlockState(pos, stages[getStage()-1].get().getDefaultState());
        }
    }

    @Override
    public boolean surviveCheck(BlockState state, ServerWorld world, BlockPos pos){
        return super.surviveCheck(state, world, pos) && world.getLightLevel(pos.up())>=minlightLevel;
    }

    @Override
    public boolean canGrow(BlockState state, ServerWorld world, BlockPos pos){
        return (world.getLightLevel(pos.up()) >= growlightLevel);
    }

    @Override
    public boolean spreadTo(BlockState state, ServerWorld world, BlockPos from, BlockPos to){
        if (world.getBlockState(to).isOf(stages[0].get()) && surviveCheck(state,world,to)) {
            world.setBlockState(to, stages[1].get().getDefaultState());
        }
        return false;
    }

    @Override
    public boolean grow(BlockState state, ServerWorld world, BlockPos pos){
        if(getStage()<stages.length-1 && world.random.nextFloat()<growchance){
            world.setBlockState(pos, stages[getStage()+1].get().getDefaultState());
        }
        return true;
    }


}
