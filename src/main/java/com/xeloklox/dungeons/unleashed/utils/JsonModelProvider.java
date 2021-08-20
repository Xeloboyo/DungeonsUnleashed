package com.xeloklox.dungeons.unleashed.utils;

import net.fabricmc.fabric.api.client.model.*;
import net.minecraft.client.render.model.*;
import net.minecraft.util.*;
import net.minecraft.util.registry.*;
import org.jetbrains.annotations.*;
import org.mini2Dx.gdx.utils.*;

public class JsonModelProvider extends Registerable<JsonModelProvider> implements ModelResourceProvider{
    static ObjectMap<String,JsonModelWrapper> models = new ObjectMap<>();

    public JsonModelProvider(){
        super("JsonModelProvider", null, null, RegisterEnvironment.CLIENT);
        registration=this;
    }

    @Override
    public void register(){
        ModelLoadingRegistry.INSTANCE.registerResourceProvider(rm -> new JsonModelProvider());
    }

    public static void add(JsonModelWrapper wrapper){
        models.put(wrapper.getIdentifier().toString(),wrapper);
    }

    @Override
    public @Nullable UnbakedModel loadModelResource(Identifier resourceId, ModelProviderContext context) throws ModelProviderException{
        if(models.containsKey(resourceId.toString())){

        }
        return models.get(resourceId.toString());
    }
}
