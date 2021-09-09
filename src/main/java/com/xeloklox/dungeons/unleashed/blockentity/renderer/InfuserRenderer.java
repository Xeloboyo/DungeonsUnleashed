package com.xeloklox.dungeons.unleashed.blockentity.renderer;

import com.xeloklox.dungeons.unleashed.blockentity.*;
import com.xeloklox.dungeons.unleashed.blocks.*;
import com.xeloklox.dungeons.unleashed.gen.*;
import com.xeloklox.dungeons.unleashed.utils.*;
import com.xeloklox.dungeons.unleashed.utils.animation.*;
import com.xeloklox.dungeons.unleashed.utils.models.*;
import net.minecraft.block.*;
import net.minecraft.client.*;
import net.minecraft.client.model.*;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory.*;
import net.minecraft.client.render.model.json.*;
import net.minecraft.client.util.math.*;
import net.minecraft.item.*;
import net.minecraft.state.property.*;
import net.minecraft.util.math.*;

public class InfuserRenderer extends ChargeConnectingRenderer<InfuserEntity>{
    public static RenderableModel ring;
    static{
        ModelJson modelJson2 =ModelJson.getModel("entity/infuser_ring","entity/infuser_ring");
        ring = new RenderableModel(modelJson2,ModelTransform.pivot(8,0,8));
    }

    public InfuserRenderer(Context ctx){
        super(ctx);
    }

    @Override
    public void render(InfuserEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay){
        super.render(entity,tickDelta,matrices,vertexConsumers,light,overlay);
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


