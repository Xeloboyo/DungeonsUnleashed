package com.xeloklox.dungeons.unleashed.utils;

import com.mojang.blaze3d.platform.*;
import com.mojang.blaze3d.systems.*;
import com.xeloklox.dungeons.unleashed.mixin.*;
import com.xeloklox.dungeons.unleashed.utils.lambda.*;
import net.fabricmc.fabric.api.renderer.v1.*;
import net.minecraft.client.render.*;
import net.minecraft.util.*;

import java.util.function.*;

public class ModRenderLayers{
    protected static final RenderPhase.Transparency ADDITIVE_TRANSPARENCY = new RenderPhase.Transparency("additive_transparency", () -> {
         RenderSystem.enableBlend();
         RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE);
      }, () -> {
         RenderSystem.disableBlend();
         RenderSystem.defaultBlendFunc();
      });


    public static Func<Identifier, RenderLayer> ADDTIVE_SPRITE;

    static {
        ADDTIVE_SPRITE = Func.fromFunction(Util.memoize((texture) -> RenderLayerMixin.of(
        "additive_blend",
        VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
        VertexFormat.DrawMode.QUADS,
        256,
        false,
        true,
        RenderLayer.MultiPhaseParameters.builder()
        .shader(RenderPhaseMixin.ENTITY_CUTOUT_SHADER())
        .texture(new RenderPhase.Texture(texture, false, false))
        .transparency(ADDITIVE_TRANSPARENCY)
        .lightmap(RenderPhaseMixin.ENABLE_LIGHTMAP())
        .overlay(RenderPhaseMixin.ENABLE_OVERLAY_COLOR())
        .build(false))));
    }



}
