package com.xeloklox.dungeons.unleashed.blocks;

import net.minecraft.util.math.*;
import net.minecraft.world.*;

public interface IChargeAccessor{
    int getCharge(World world, BlockPos pos);
    int maxCharge(World world, BlockPos pos);
    void setCharge(World world, BlockPos pos, int charge);
    default boolean drainCharge(World world, BlockPos pos){
        int c = getCharge(world,pos);
        if(c<=0){
            return false;
        }
        setCharge(world,pos,c-1);
        return true;
    }
    default boolean addCharge(World world, BlockPos pos,int charge){
        int c = getCharge(world,pos)+charge;
        if(c>maxCharge(world,pos)){
            return false;
        }
        setCharge(world,pos,c);
        return true;
    }
}
