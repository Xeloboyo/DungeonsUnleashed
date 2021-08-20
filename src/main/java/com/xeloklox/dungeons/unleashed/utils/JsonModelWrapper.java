package com.xeloklox.dungeons.unleashed.utils;

import com.mojang.datafixers.util.Pair;
import com.xeloklox.dungeons.unleashed.gen.*;
import net.fabricmc.fabric.api.renderer.v1.mesh.*;
import net.fabricmc.fabric.api.renderer.v1.model.*;
import net.fabricmc.fabric.api.renderer.v1.render.*;
import net.minecraft.block.*;
import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.*;
import net.minecraft.client.texture.*;
import net.minecraft.client.util.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.function.*;

import static com.xeloklox.dungeons.unleashed.DungeonsUnleashed.MODID;

public interface JsonModelWrapper extends UnbakedModel{

    Identifier getIdentifier();
    void setIdentifier(Identifier i);

    JsonUnbakedModel getUnbaked();
    void setUnbaked(JsonUnbakedModel i);

    BakedModel getBaked();
    void setBaked(BakedModel i);


    @Override
    default Collection<Identifier> getModelDependencies(){
        return Arrays.asList(getIdentifier());
    }

    @Override
    default Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<Pair<String, String>> unresolvedTextureReferences){
        return getUnbaked().getTextureDependencies(unbakedModelGetter,unresolvedTextureReferences);
    }

    @Nullable
    @Override
    default BakedModel bake(ModelLoader loader, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId){
        setUnbaked((JsonUnbakedModel) loader.getOrLoadModel(getIdentifier()));
        setBaked(getUnbaked().bake(loader,textureGetter,rotationContainer,modelId));
        return getBaked();
    }
}
