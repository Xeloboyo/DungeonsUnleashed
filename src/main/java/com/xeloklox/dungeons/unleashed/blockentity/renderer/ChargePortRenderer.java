package com.xeloklox.dungeons.unleashed.blockentity.renderer;

import com.xeloklox.dungeons.unleashed.blockentity.*;
import com.xeloklox.dungeons.unleashed.gen.*;
import com.xeloklox.dungeons.unleashed.utils.animation.*;
import com.xeloklox.dungeons.unleashed.utils.models.*;
import net.minecraft.client.model.*;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory.*;
import net.minecraft.client.util.math.*;

import static com.xeloklox.dungeons.unleashed.utils.Utils.pixels;

public class ChargePortRenderer extends BlockEntityRendererBase<ChargeStoragePortEntity>{
    public static RenderableModel model;
    public static BoneAnimationGroup model_animation;
    public static BoneAnimation contract_base;
    static {
        ModelJson modelJson =ModelJson.getModel("entity/charge_port","block/custom/charge_cell_port");
        model = new RenderableModel(modelJson, ModelTransform.pivot(0,0,0));
        model_animation = new BoneAnimationGroup(BoneAnimation.getAnimation("block/charge_port"),model);
        contract_base = model_animation.get("connectunderneath");
    }
    public ChargePortRenderer(Context ctx){
        super(ctx);
    }

    @Override
    public void render(ChargeStoragePortEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay){
        if(entity.animationParams == null){
            entity.animationParams = model.getParamMap();
        }
        contract_base.animate(entity.contractBase*contract_base.getLength(),entity.animationParams);
        matrices.push();
        matrices.translate(pixels(8),0,pixels(8));
        model.render(matrices,vertexConsumers,light  ,overlay,entity.animationParams);
        matrices.pop();
    }
}
