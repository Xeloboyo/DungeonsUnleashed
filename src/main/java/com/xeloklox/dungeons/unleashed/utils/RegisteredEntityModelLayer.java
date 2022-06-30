package com.xeloklox.dungeons.unleashed.utils;

import net.fabricmc.fabric.api.client.rendereregistry.v1.*;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityModelLayerRegistry.*;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.util.*;

import static com.xeloklox.dungeons.unleashed.ModInitClientServer.MODID;

public class RegisteredEntityModelLayer extends Registerable<EntityModelLayer>{
    TexturedModelDataProvider prov;
    public RegisteredEntityModelLayer(String id, TexturedModelDataProvider prov){
        super(id, new EntityModelLayer(new Identifier(MODID,id),"main"), null, RegisterEnvironment.CLIENT);
        this.prov = prov;
    }

    @Override
    public void register(){
        EntityModelLayerRegistry.registerModelLayer(get(), prov);
    }
}
