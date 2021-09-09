package com.xeloklox.dungeons.unleashed.blockentity.renderer;

import com.xeloklox.dungeons.unleashed.blockentity.*;
import com.xeloklox.dungeons.unleashed.gen.*;
import com.xeloklox.dungeons.unleashed.utils.*;
import com.xeloklox.dungeons.unleashed.utils.animation.*;
import com.xeloklox.dungeons.unleashed.utils.models.*;
import net.minecraft.block.*;
import net.minecraft.client.model.*;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory.*;
import net.minecraft.client.util.math.*;
import net.minecraft.state.property.*;
import net.minecraft.util.math.*;

public abstract class ChargeConnectingRenderer<T extends ChargeConnectingEntity> extends BlockEntityRendererBase<T>{
    public static RenderableModel connector;
    static{
       ModelJson modelJson =ModelJson.getModel("entity/infuser_connection","entity/debug");
       connector = new RenderableModel(modelJson, ModelTransform.pivot(8,2,0));
   }
    public ChargeConnectingRenderer(Context ctx){
        super(ctx);
    }

    @Override
    public void render(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay){
        BlockState bs = entity.getWorld().getBlockState(entity.getPos());
        Vec3f diroffset = new Vec3f(0,1,0);
        Direction dir = bs.get(Properties.HORIZONTAL_FACING);
        for(int i = 0; i< entity.getAnimation().length; i++){
            ParameterMap anim = entity.getAnimation()[i];
            float extend = anim.f("extend");
            float rotation = anim.f("rotation");
            matrices.push();

            diroffset = Mathf.relativeDirectionHorzF(dir,entity.block.connectionRelative[i]);

            float extendscl = 0.5f+extend;
            matrices.translate(
                diroffset.getX()*extendscl+0.5f, Utils.pixels(14)-0.01f, diroffset.getZ()*extendscl+0.5f
            );
            float angle = Mathf.getHorzAngle(diroffset)+180;
            matrices.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion( angle));
            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(  rotation ));

            int lightAround = WorldRenderer.getLightmapCoordinates(entity.getWorld(), entity.getPos().add(Mathf.vec3i(diroffset)));
            connector.render(matrices,vertexConsumers,lightAround,overlay);

            matrices.pop();
        }
    }
}
