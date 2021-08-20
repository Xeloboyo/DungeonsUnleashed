package com.xeloklox.dungeons.unleashed.blockentity.renderer;

import com.xeloklox.dungeons.unleashed.utils.*;
import net.fabricmc.fabric.api.client.rendereregistry.v1.*;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.client.*;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.*;
import net.minecraft.client.render.model.*;
import net.minecraft.client.util.*;
import net.minecraft.client.util.math.*;
import net.minecraft.util.registry.*;
import net.minecraft.world.*;

import javax.swing.text.html.parser.*;

public abstract class BlockEntityRendererBase <U extends BlockEntity>implements BlockEntityRenderer<U>{
    public BlockEntityRendererBase(BlockEntityRendererFactory.Context ctx) {

    }
    /*
    public void drawModel(BlockEntity e, BakedModel model, BlockState state,MatrixStack matrices, VertexConsumer vertexConsumers, int overlay){
        MinecraftClient.getInstance().getBakedModelManager().getModel(new ModelIdentifier(""));
        MinecraftClient.getInstance().getBlockRenderManager().getModelRenderer().renderFlat(e.getWorld(),model,state,e.getPos(),matrices,vertexConsumers,true,e.getWorld().random,0,overlay);
    }*/

    //VertexConsumer vertexConsumer = spriteIdentifier.getVertexConsumer(vertexConsumers, RenderLayer::getEntityCutout);



}
