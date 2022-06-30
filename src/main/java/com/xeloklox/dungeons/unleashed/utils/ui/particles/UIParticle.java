package com.xeloklox.dungeons.unleashed.utils.ui.particles;

import com.mojang.blaze3d.platform.GlStateManager.*;
import com.mojang.blaze3d.systems.*;
import com.xeloklox.dungeons.unleashed.utils.lambda.Cons.*;
import com.xeloklox.dungeons.unleashed.utils.ui.*;
import net.minecraft.client.util.math.*;

public abstract class UIParticle{
    public float x,y,vx,vy;
    public float life = 0;
    public RenderAffectors[] renderAffectors = {};
    public boolean cantDie = false;
    public long layer = 1;
    public UIParticle(float x, float y){
        this.x = x;
        this.y = y;
    }

    public void update(){
        x+=vx;
        y+=vy;
        life++;
    }
    public abstract void draw(MatrixStack matrices, AnimatedScreen at);

    public void forceKill(){
        life=-99;
        cantDie=false;
    }

    public UIParticle setLayer(int layer,boolean enabled){
        this.layer = (this.layer-(this.layer&(1L<<layer)))|(enabled?1L:0L)<<layer;
        return this;
    }

    public UIParticle setRenderAffectors(RenderAffectors... renderAffectors){
        this.renderAffectors = renderAffectors;
        return this;
    }

    public static class RenderAffectors{
        Cons3<MatrixStack, AnimatedScreen, UIParticle> effect = (a,b,c)->{};

        public RenderAffectors(Cons3<MatrixStack, AnimatedScreen, UIParticle> effect){
            this.effect = effect;
        }



        public static RenderAffectors FADE_IN  = new RenderAffectors((a,b,p)-> {
            float[] c = RenderSystem.getShaderColor();
            RenderSystem.setShaderColor(c[0], c[1], c[2], Math.min(1.0f,p.life*0.1f));
        });
        public static RenderAffectors ADD_BLEND  = new RenderAffectors((a,b,p)-> RenderSystem.blendFunc(SrcFactor.ONE,DstFactor.ONE));
        public static RenderAffectors NORM_BLEND  = new RenderAffectors((a,b,p)-> RenderSystem.blendFunc(SrcFactor.SRC_ALPHA, DstFactor.ONE_MINUS_SRC_ALPHA));
    }
}
