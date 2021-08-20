package com.xeloklox.dungeons.unleashed.blockentity.renderer;

import com.xeloklox.dungeons.unleashed.blockentity.*;
import com.xeloklox.dungeons.unleashed.blocks.*;
import com.xeloklox.dungeons.unleashed.gen.*;
import com.xeloklox.dungeons.unleashed.utils.*;
import com.xeloklox.dungeons.unleashed.utils.animation.*;
import net.minecraft.block.*;
import net.minecraft.client.*;
import net.minecraft.client.model.*;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory.*;
import net.minecraft.client.render.model.json.*;
import net.minecraft.client.util.math.*;
import net.minecraft.item.*;
import net.minecraft.particle.*;
import net.minecraft.state.property.*;
import net.minecraft.util.math.*;

public class InfuserRenderer extends BlockEntityRendererBase<InfuserEntity>{
    public static RenderableModel connector;
    public static RenderableModel ring;
    static{
        String modelfile = "entity/infuser_connection";
        ModelJson modelJson = new ModelJson(
            "entity/infuser_con",
            BlockModelPresetBuilder.getTemplate(modelfile),
            BlockModelPresetBuilder.customTemplateObj(modelfile,"","entity/debug")
        );
        connector = new RenderableModel(modelJson,ModelTransform.pivot(8,2,0));
        String ringmodelfile = "entity/infuser_ring";
        ModelJson modelJson2 = new ModelJson(
            "entity/infuser_ring",
            BlockModelPresetBuilder.getTemplate(ringmodelfile),
            BlockModelPresetBuilder.customTemplateObj(ringmodelfile,"","entity/infuser_ring")
        );
        ring = new RenderableModel(modelJson2,ModelTransform.pivot(8,0,8));
    }
    ModelPart modelPart;

    public InfuserRenderer(Context ctx){
        super(ctx);
        connector.init(ctx);
        ring.init(ctx);
    }



    @Override
    public void render(InfuserEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay){

        BlockState bs = entity.getWorld().getBlockState(entity.getPos());
        Vec3f diroffset = new Vec3f(0,1,0);

        if(bs.getBlock() instanceof InfuserBlock){
            Direction dir = bs.get(Properties.HORIZONTAL_FACING);
            for(int i=0;i<InfuserBlock.connectionRelative.length;i++){
                ParameterMap anim = entity.getAnimation()[i];
                float extend = anim.f("extend");
                float rotation = anim.f("rotation");
                matrices.push();

                diroffset = Mathf.relativeDirectionHorzF(dir,InfuserBlock.connectionRelative[i]);

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

            matrices.push();
            matrices.translate(
            0.5f, 1f+entity.rise, 0.5f
            );
            matrices.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion( entity.spin));
            int lightAbove = WorldRenderer.getLightmapCoordinates(entity.getWorld(), entity.getPos().add(0,1,0));
            ring.render(matrices,vertexConsumers,lightAbove,overlay);
            matrices.pop();

            if(!entity.processingStack.isEmpty()){

                entity.spin+=entity.working?5.3:0.3f;
                ItemStack is =entity.processingStack;
                matrices.push();
                matrices.translate(
                0.5f, 1.2+entity.rise, 0.5f
                );
                matrices.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion( -0.5f*entity.spin));
                MinecraftClient.getInstance().getItemRenderer().renderItem(is, ModelTransformation.Mode.GROUND, lightAbove, overlay, matrices, vertexConsumers,0);
                matrices.pop();

            }
        }
    }
}

// MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Mode.GROUND, lightAbove, overlay, matrices, vertexConsumers,0);

