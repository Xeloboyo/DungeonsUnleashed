package com.xeloklox.dungeons.unleashed.utils.models;

import com.xeloklox.dungeons.unleashed.utils.*;
import net.fabricmc.fabric.api.client.model.*;
import net.minecraft.client.render.model.*;
import net.minecraft.util.*;
import org.jetbrains.annotations.*;
import org.mini2Dx.gdx.utils.*;

import static com.xeloklox.dungeons.unleashed.ModInitClientServer.MODID;

public class ModelProvider  implements ModelResourceProvider{
    static ObjectMap<String,GeneratedModel> models = new ObjectMap<>();

    public ModelProvider(){
    }


    public static boolean hasModel(String id){
        return models.containsKey(MODID+":"+id);
    }

    public static void add(GeneratedModel wrapper){
        models.put(MODID+":block/"+wrapper.name,wrapper);
        models.put(MODID+":item/"+wrapper.name,wrapper);
    }

    @Override
    public @Nullable UnbakedModel loadModelResource(Identifier resourceId, ModelProviderContext context) throws ModelProviderException{
        if(models.containsKey(resourceId.toString())){
            return models.get(resourceId.toString());
        }
        return null;
    }

    public static class RegisteredModelProvider extends Registerable<ModelProvider>{
        public RegisteredModelProvider(String id, ModelProvider registration){
            super(id, registration, null, RegisterEnvironment.CLIENT);
        }

        @Override
          public void register(){
              ModelLoadingRegistry.INSTANCE.registerResourceProvider(rm -> new ModelProvider());
          }
    }
}
