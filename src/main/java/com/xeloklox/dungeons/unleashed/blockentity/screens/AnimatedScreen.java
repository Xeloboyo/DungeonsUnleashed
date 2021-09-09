package com.xeloklox.dungeons.unleashed.blockentity.screens;

import com.mojang.blaze3d.platform.GlStateManager.*;
import com.mojang.blaze3d.systems.*;
import com.xeloklox.dungeons.unleashed.blockentity.screens.particles.*;
import com.xeloklox.dungeons.unleashed.utils.*;
import com.xeloklox.dungeons.unleashed.utils.animation.*;
import com.xeloklox.dungeons.unleashed.utils.lambda.*;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.screen.*;
import net.minecraft.text.*;
import net.minecraft.util.math.*;

public abstract class AnimatedScreen<T extends ScreenHandler> extends HandledScreen<T>{
    float tick=0;
    float duration=100;
    StateMap stateMap;
    UIParticleSystem particles;

    public AnimatedScreen(T handler, PlayerInventory inventory, Text title, float duration, Cons<ParameterMap> cons){
        super(handler, inventory, title);
        this.duration=duration;
        stateMap = new StateMap((a)->null,pmap -> {
            pmap.add("x_offset",0f);
            pmap.add("y_offset", 0f);
            cons.get(pmap);
        });
        particles = new UIParticleSystem();
    }

    public abstract void updateLogic();

    int ox,oy;

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta){
        if(ox==0||oy==0){
            ox=x;oy=y;
        }
        updateLogic();
        stateMap.update();
        x = ox+(int)stateMap.f("x_offset");
        y = oy+(int)stateMap.f("y_offset");
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }
    //fuck you handled screen
    public void drawItem(ItemStack stack, float x, float y, String amountText) {
       this.setZOffset(200);
       this.itemRenderer.zOffset = 200.0F;
       this.itemRenderer.renderInGuiWithOverrides(stack, (int)x, (int)y);
       this.itemRenderer.renderGuiItemOverlay(this.textRenderer, stack, (int)x, (int)y, amountText);
       this.setZOffset(0);
       this.itemRenderer.zOffset = 0.0F;
    }
    public void resetBlendMode(){
        RenderSystem.blendFunc(SrcFactor.SRC_ALPHA, DstFactor.ONE_MINUS_SRC_ALPHA);
    }
    public void additiveBlendMode(){
        RenderSystem.blendFunc(SrcFactor.SRC_ALPHA, DstFactor.ONE);
    }

    public void alpha(float alpha){
        float[] c = RenderSystem.getShaderColor();
        RenderSystem.setShaderColor(c[0], c[1], c[2], alpha);
    }

    public void rotate(MatrixStack matrices, float angle){
        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(angle));
    }
    public void drawSection(MatrixStack matrices,float x,float y,float u,float v,float uw,float vh, float sx0, float sx1, float sy0, float sy1){
        sx0 = MathHelper.clamp(sx0,0,1);
        sx1 = MathHelper.clamp(sx1,0,1);
        sy0 = MathHelper.clamp(sy0,0,1);
        sy1 = MathHelper.clamp(sy1,0,1);
        drawTexture(matrices, (int)(x+sx0*uw), (int)(y+sy0*vh), (int)(u+sx0*uw), (int)(v+sy0*vh), (int)(uw * (sx1-sx0)), (int)(vh * (sy1-sy0)));
    }

    public void drawTexturedQuadWH(MatrixStack matrices, float[][] pos, float u, float v, float regionWidth, float regionHeight) {
        drawTexturedQuadWH(matrices,pos,u,v,regionWidth,regionHeight,256,256);
    }
    public void drawTexturedQuadWH(MatrixStack matrices, float[][] pos, float u, float v, float regionWidth, float regionHeight, float tw, float th) {
        drawTexturedQuad(matrices.peek().getModel(), pos,u/tw,(u+regionWidth)/tw,v/th,(v+regionHeight)/th);
    }
    public void drawTexturedQuad(Matrix4f matrices, float[][] pos, float u0, float u1, float v0, float v1) {
       RenderSystem.setShader(GameRenderer::getPositionTexShader);
       BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
       bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
       bufferBuilder.vertex(matrices, pos[0][0], pos[0][1], 0).texture(u0, v1).next();
       bufferBuilder.vertex(matrices, pos[1][0], pos[1][1], 0).texture(u1, v1).next();
       bufferBuilder.vertex(matrices, pos[2][0], pos[2][1], 0).texture(u1, v0).next();
       bufferBuilder.vertex(matrices, pos[3][0], pos[3][1], 0).texture(u0, v0).next();
       bufferBuilder.end();
       BufferRenderer.draw(bufferBuilder);
    }
    public void drawArc(MatrixStack matrices, float x,float y,float r,float t,float fromA,float toA, float u, float v, float regionWidth, float regionHeight) {
        drawArc(matrices.peek().getModel(), x,y,r,t,fromA,toA, 180f/r,u/256f,(u+regionWidth)/256f,v/256f,(v+regionHeight)/256f );
    }
    public void drawArc(Matrix4f matrices, float x,float y,float r,float t,float fromA,float toA, float segsize,float u0, float u1, float v0, float v1) {
       RenderSystem.setShader(GameRenderer::getPositionTexShader);
       BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
       bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
       float[] prevOuter = new float[2],prevInner = new float[2];
       float innerR = r-t;
       prevOuter[0] = Mathf.sinDeg(fromA)*r+x;
       prevOuter[1] = Mathf.cosDeg(fromA)*r+y;
       prevInner[0] = Mathf.sinDeg(fromA)*innerR+x;
       prevInner[1] = Mathf.cosDeg(fromA)*innerR+y;
       for(float i = fromA;i<toA;i+=segsize){
           float fa = Math.min(toA,i+segsize);
           float s = Mathf.sinDeg(fa);
           float c = Mathf.cosDeg(fa);
           bufferBuilder.vertex(matrices, prevOuter[0], prevOuter[1], 0).texture(u0, v1).next();
           bufferBuilder.vertex(matrices, s*r+x,        c*r+y,        0).texture(u1, v1).next();
           bufferBuilder.vertex(matrices, s*innerR+x,   c*innerR+y,   0).texture(u1, v0).next();
           bufferBuilder.vertex(matrices, prevInner[0], prevInner[1], 0).texture(u0, v0).next();

           prevOuter[0] = s*r+x;
           prevOuter[1] = c*r+y;
           prevInner[0] = s*innerR+x;
           prevInner[1] = c*innerR+y;

       }
       bufferBuilder.end();
       BufferRenderer.draw(bufferBuilder);
    }


}
