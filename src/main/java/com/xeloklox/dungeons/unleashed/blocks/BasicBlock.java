package com.xeloklox.dungeons.unleashed.blocks;

import com.xeloklox.dungeons.unleashed.utils.*;
import net.fabricmc.fabric.api.object.builder.v1.block.*;
import net.minecraft.block.*;
import net.minecraft.state.*;
import net.minecraft.state.StateManager.*;
import net.minecraft.state.property.*;

public class BasicBlock extends Block{
    public BasicBlock(Material material, Func<FabricBlockSettings,FabricBlockSettings> func){
        super(func.get(FabricBlockSettings.of(material)));

    }

    @Override
    protected void appendProperties(Builder<Block, BlockState> builder){
        super.appendProperties(builder);
    }
}
