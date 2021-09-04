package com.xeloklox.dungeons.unleashed.blockentity.renderer;

import com.xeloklox.dungeons.unleashed.blockentity.*;
import com.xeloklox.dungeons.unleashed.blocks.graph.charge.*;
import com.xeloklox.dungeons.unleashed.gen.*;
import com.xeloklox.dungeons.unleashed.utils.*;
import com.xeloklox.dungeons.unleashed.utils.animation.*;
import com.xeloklox.dungeons.unleashed.utils.models.*;
import net.minecraft.client.model.*;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory.*;
import net.minecraft.client.util.math.*;
import net.minecraft.util.math.*;

import static com.xeloklox.dungeons.unleashed.utils.Utils.pixels;

public class ChargeTankRenderer extends BlockEntityRendererBase<ChargeStorageTankEntity>{
    public static RenderableModel modelBottom;
    public static RenderableModel modelTop;
    public static RenderableModel glowMiddle;
    public static RenderableModel glowTop;
    public static RenderableModel glowBottom;
    public static RenderableModel glowUnconnect;

    public static BoneAnimationGroup model_animation;
    public static BoneAnimation contract_base;
    public static BoneAnimationGroup model_animation_top;
    public static BoneAnimation contract_top;
    static {
        //connections
        ModelJson modelJson =ModelJson.getModel("entity/charge_tank_bottom","block/custom/charge_cell_tank");
        modelBottom = new RenderableModel(modelJson, ModelTransform.pivot(0,0,0));
        model_animation = new BoneAnimationGroup(BoneAnimation.getAnimation("block/charge_tank_bottom"), modelBottom);
        contract_base = model_animation.get("connectbottom");

        ModelJson modelJsonTop =ModelJson.getModel("entity/charge_tank_top","block/custom/charge_cell_tank");
        modelTop = new RenderableModel(modelJsonTop, ModelTransform.pivot(0,0,0));
        model_animation_top = new BoneAnimationGroup(BoneAnimation.getAnimation("block/charge_tank_top"), modelTop);
        contract_top = model_animation_top.get("connecttop");
        //glow
        glowMiddle = new RenderableModel(ModelJson.getModel("entity/glow_middle","block/custom/glow"), ModelTransform.pivot(0,0,0), ModRenderLayers.ADDTIVE_SPRITE);
        glowTop = new RenderableModel(ModelJson.getModel("entity/glow_top","block/custom/glow"), ModelTransform.pivot(0,0,0), ModRenderLayers.ADDTIVE_SPRITE);
        glowBottom = new RenderableModel(ModelJson.getModel("entity/glow_bottom","block/custom/glow"), ModelTransform.pivot(0,0,0), ModRenderLayers.ADDTIVE_SPRITE);
        glowUnconnect = new RenderableModel(ModelJson.getModel("entity/glow_unconnected","block/custom/glow"), ModelTransform.pivot(0,0,0), ModRenderLayers.ADDTIVE_SPRITE);
    }

    public ChargeTankRenderer(Context ctx){
        super(ctx);
    }

    @Override
    public void render(ChargeStorageTankEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay){
        boolean up = entity.getCachedState().get(ChargeStorageTankBlock.UP);
        boolean down = entity.getCachedState().get(ChargeStorageTankBlock.DOWN);
        if(up && !down){
            if(entity.animationParamsBottom == null){
                entity.animationParamsBottom = modelBottom.getParamMap();
            }
            contract_base.animate(entity.contractBase*contract_base.getLength(),entity.animationParamsBottom);
            matrices.push();
            matrices.translate(pixels(8),0,pixels(8));
            modelBottom.render(matrices,vertexConsumers,light  ,overlay,entity.animationParamsBottom);
            matrices.pop();
        }

        if(!up && down){
            if(entity.animationParamsTop == null){
                entity.animationParamsTop = modelTop.getParamMap();
            }
            contract_top.animate(entity.contractTop*contract_top.getLength(),entity.animationParamsTop);
            matrices.push();
            matrices.translate(pixels(8),0,pixels(8));
            modelTop.render(matrices,vertexConsumers,light  ,overlay,entity.animationParamsTop);
            matrices.pop();
        }

        float time = entity.getWorld().getTime()+tickDelta;
        float pulse = Mathf.map(MathHelper.sin(time*0.05f),-1,1,0.8f,1.0f);
        RenderableModel glow = glowMiddle;
        if(up && !down){
            glow = glowBottom;
        }
        if(!up && down){
            glow = glowTop;
        }
        if(!up && !down){
            glow = glowUnconnect;
        }
        glow.setAlpha(entity.chargePortion*pulse);
        glow.render(matrices,vertexConsumers,15728880,overlay);
    }
}
