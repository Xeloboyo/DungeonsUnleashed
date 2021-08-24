package com.xeloklox.dungeons.unleashed.utils.models;

import com.mojang.datafixers.util.Pair;
import com.xeloklox.dungeons.unleashed.gen.*;
import net.fabricmc.fabric.api.renderer.v1.*;
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

public abstract class GeneratedModel implements UnbakedModel, BakedModel, FabricBakedModel{
    public Sprite[] sprites ;
    public SpriteIdentifier[] spriteIDs ;
    public String name;
    Mesh mesh;
    ModelTransformation transformation;

    private static final Identifier DEFAULT_BLOCK_MODEL = new Identifier("minecraft:block/block");

    public GeneratedModel(SpriteIdentifier[] spriteIDs, String name){
        this.spriteIDs = spriteIDs;
        this.name = name;
        sprites= new Sprite[spriteIDs.length];
        ModelProvider.add(this);
    }

    @Override
    public boolean isVanillaAdapter(){
        return false;
    }

    @Override
    public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context){
        context.meshConsumer().accept(mesh);
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context){
        context.meshConsumer().accept(mesh);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random){
        return null;
    }

    @Override
    public boolean useAmbientOcclusion(){
        return true;
    }

    @Override
    public boolean hasDepth(){
        return false;
    }

    @Override
    public boolean isSideLit(){
        return false;
    }

    @Override
    public boolean isBuiltin(){
        return false;
    }

    @Override
    public Sprite getParticleSprite(){
        return sprites[0];
    }

    @Override
    public ModelTransformation getTransformation(){
        return transformation;
    }

    @Override
    public ModelOverrideList getOverrides(){
        return ModelOverrideList.EMPTY;
    }


    @Override
    public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<Pair<String, String>> unresolvedTextureReferences){
        return Arrays.asList(spriteIDs);
    }

    @Nullable
    @Override
    public BakedModel bake(ModelLoader loader, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId){
        // Load the default block model
        JsonUnbakedModel defaultBlockModel = (JsonUnbakedModel) loader.getOrLoadModel(DEFAULT_BLOCK_MODEL);
        // Get its ModelTransformation
        transformation = defaultBlockModel.getTransformations();

        for(int i = 0; i < sprites.length; ++i) {
            sprites[i] = textureGetter.apply(spriteIDs[i]);
        }
        // Build the mesh using the Renderer API
        Renderer renderer = RendererAccess.INSTANCE.getRenderer();
        MeshBuilder builder = renderer.meshBuilder();
        QuadEmitter emitter = builder.getEmitter();
        bake(emitter,loader,rotationContainer,modelId);
        mesh = builder.build();
        return this;
    }

    public abstract void bake(QuadEmitter emitter,ModelLoader loader, ModelBakeSettings rotationContainer, Identifier modelId);

    public Collection<Identifier> getModelDependencies() {
        return Arrays.asList(DEFAULT_BLOCK_MODEL);
    }
}
