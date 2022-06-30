package com.xeloklox.dungeons.unleashed.utils;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.*;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder.*;
import net.minecraft.block.entity.*;
import net.minecraft.util.registry.*;

import static com.xeloklox.dungeons.unleashed.ModInitClientServer.MODID;
import static com.xeloklox.dungeons.unleashed.Globals.bootQuery;

public class RegisteredBlockEntity <T extends BlockEntity> extends Registerable<BlockEntityType<T>>{
    Factory<T> factory;
    RegisteredBlock block;
    public RegisteredBlockEntity(String id, Factory<T> factory, RegisteredBlock block){
        super(id, null, bootQuery(() -> Registry.BLOCK_ENTITY_TYPE), RegisterEnvironment.CLIENT_AND_SERVER);
        this.factory=factory;
        this.block=block;
    }

    @Override
    public void register(){
        try{
            registration = Registry.register(Registry.BLOCK_ENTITY_TYPE, MODID+":"+id, FabricBlockEntityTypeBuilder.create(factory, block.get()).build(null));
        }catch(SecurityException e){
            e.printStackTrace();
        }
    }
}
