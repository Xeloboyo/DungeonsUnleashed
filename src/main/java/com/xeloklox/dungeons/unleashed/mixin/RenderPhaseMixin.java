package com.xeloklox.dungeons.unleashed.mixin;

import net.minecraft.client.render.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.*;

@Mixin(RenderPhase.class)
public interface RenderPhaseMixin{
    @Accessor("ENTITY_CUTOUT_SHADER")
    public static RenderPhase.Shader ENTITY_CUTOUT_SHADER(){
        throw new AssertionError();
    }
    @Accessor("ADDITIVE_TRANSPARENCY")
    public static RenderPhase.Transparency ADDITIVE_TRANSPARENCY(){
        throw new AssertionError();
    }
    @Accessor("ENABLE_OVERLAY_COLOR")
    public static RenderPhase.Overlay ENABLE_OVERLAY_COLOR(){
        throw new AssertionError();
    }
    @Accessor("ENABLE_LIGHTMAP")
    public static RenderPhase.Lightmap ENABLE_LIGHTMAP(){
        throw new AssertionError();
    }


}
