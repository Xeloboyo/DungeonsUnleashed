package com.xeloklox.dungeons.unleashed.utils;

import net.minecraft.block.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

public interface BlockCons<T>{
    public T get(BlockState state, WorldView world, BlockPos pos);
}
