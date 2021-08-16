package com.xeloklox.dungeons.unleashed.blocks;

import net.minecraft.util.math.*;
import net.minecraft.world.*;

public interface IAffectedByLightning{
    public void onStruck(World world, BlockPos pos);
}
