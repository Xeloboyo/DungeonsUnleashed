package com.xeloklox.dungeons.unleashed.utils;

import com.xeloklox.dungeons.unleashed.utils.lambda.*;
import net.fabricmc.fabric.api.client.rendereregistry.v1.*;
import net.minecraft.block.entity.*;
import net.minecraft.client.render.block.entity.*;

public class RegisteredBlockEntityRenderer <U extends BlockEntity> extends Registerable<BlockEntityRendererFactory<U>> {
    Prov<BlockEntityType<U>> etype;
    public RegisteredBlockEntityRenderer(Prov<BlockEntityType<U>> entitytype, BlockEntityRendererFactory<U> blockEntityRendererFactory){
        super(entitytype.toString()+"renderer", blockEntityRendererFactory, null, RegisterEnvironment.CLIENT);
        this.etype=entitytype;

    }
    @Override
    public void register(){
        BlockEntityRendererRegistry.INSTANCE.register(etype.get(), get());
    }
}
