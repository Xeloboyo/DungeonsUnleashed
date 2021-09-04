package com.xeloklox.dungeons.unleashed.mixin;

import net.minecraft.client.render.*;
import net.minecraft.client.render.RenderLayer.*;
import net.minecraft.client.render.VertexFormat.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.*;

@Mixin(RenderLayer.class)
public interface RenderLayerMixin{
    //return of("energy_swirl", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 256, false, true, RenderLayer.MultiPhaseParameters.builder().shader(ENERGY_SWIRL_SHADER).texture(new RenderPhase.Texture(texture, false, false)).texturing(new RenderPhase.OffsetTexturing(x, y)).transparency(ADDITIVE_TRANSPARENCY).cull(DISABLE_CULLING).lightmap(ENABLE_LIGHTMAP).overlay(ENABLE_OVERLAY_COLOR).build(false));
    @Invoker("of")
    public static RenderLayer.MultiPhase of(String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, RenderLayer.MultiPhaseParameters phases){
        throw  new AssertionError();
    }

}
