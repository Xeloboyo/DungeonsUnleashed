package com.xeloklox.dungeons.unleashed.utils;

import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.util.registry.*;

import static com.xeloklox.dungeons.unleashed.Globals.bootQuery;

public class RegisteredBlockEntity extends Registerable<BlockEntity>{
    public RegisteredBlockEntity(String id, BlockEntity registration){
        super(id, registration, bootQuery(() -> Registry.BLOCK_ENTITY_TYPE), RegisterEnvironment.CLIENT_AND_SERVER);
    }


}
