package com.xeloklox.dungeons.unleashed.utils.ui;

import com.mojang.blaze3d.platform.*;
import com.mojang.blaze3d.platform.GlStateManager.*;
import com.mojang.blaze3d.systems.*;
import com.xeloklox.dungeons.unleashed.blockentity.screens.*;
import com.xeloklox.dungeons.unleashed.utils.ui.components.*;
import com.xeloklox.dungeons.unleashed.utils.ui.particles.*;
import com.xeloklox.dungeons.unleashed.utils.*;
import com.xeloklox.dungeons.unleashed.utils.animation.*;
import com.xeloklox.dungeons.unleashed.utils.lambda.*;
import net.minecraft.client.*;
import net.minecraft.client.font.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.render.*;
import net.minecraft.client.render.model.json.*;
import net.minecraft.client.texture.*;
import net.minecraft.client.util.math.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

import java.util.*;

public abstract class AnimatedScreen<T extends ScreenHandler> extends HandledScreen<T>{
    float tick=0;
    public float duration=100;
    public StateMap stateMap;
    public UIParticleSystem particles;
    public UIHandler ui;
    public SpriteDrawer spriteDrawer;

    public AnimatedScreen(T handler, PlayerInventory inventory, Text title, float duration, Cons<ParameterMap> cons){
        super(handler, inventory, title);
        this.duration=duration;
        stateMap = new StateMap((a)->null,pmap -> {
            pmap.add("x_offset",0f);
            pmap.add("y_offset", 0f);
            cons.get(pmap);
        });
        particles = new UIParticleSystem();
        ui = new UIHandler(this);
        spriteDrawer = new SpriteDrawer(new MatrixStack(),this);
    }

    public abstract void updateLogic();

    int ox,oy;

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta){
        spriteDrawer.reset(matrices);
        if(ox==0||oy==0){
            ox=x;oy=y;
        }
        particles.update();
        ui.update();
        updateLogic();
        stateMap.update();
        x = ox+(int)stateMap.f("x_offset");
        y = oy+(int)stateMap.f("y_offset");

        renderBackground(matrices);
        spriteDrawer.resetColor();
        spriteDrawer.resetTex();
        matrices.push();
        matrices.translate(x+stateMap.f("x_offset"),y,0);
        spriteDrawer.reset(matrices);
        spriteDrawer.begin();
        RenderSystem.enableBlend();
        RenderSystem.setShaderTexture(0, UITextures.TEXTURE);
        ui.draw();
        spriteDrawer.end();
        matrices.pop();

        super.render(matrices, mouseX, mouseY, delta);



        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }
    //fuck you handled screen
    public void drawItem(ItemStack stack, float x, float y, String amountText) {
        drawItem(stack,x,y,200,amountText);
    }
    //this.renderItem(stack, ModelTransformation.Mode.GUI, false, matrixStack2, immediate, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, model);
    public void drawItem(ItemStack stack, float x, float y, float z, String amountText) {
       this.setZOffset((int)z);
       this.itemRenderer.zOffset = z;
       this.itemRenderer.renderInGuiWithOverrides(stack, (int)x, (int)y);
       this.itemRenderer.renderGuiItemOverlay(this.textRenderer, stack, (int)x, (int)y, amountText);
       this.setZOffset(0);
       this.itemRenderer.zOffset = 0.0F;
    }
    static final Vec3f topLeftLight = Util.make(new Vec3f(-1.0F, -0.7F, 0.2F), Vec3f::normalize);
    static final Vec3f topDownLight = Util.make(new Vec3f(-0.2F, -1F, 0.7F), Vec3f::normalize);

    public void drawItem(MatrixStack matrices,ItemStack stack, float x, float y, float z, String amountText) {

        RenderSystem.setShaderTexture(0, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        matrices.push();
        matrices.translate(x, y, z);
        matrices.scale(16, -16, 0.1f);
        RenderSystem.applyModelViewMatrix();
        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        var model = itemRenderer.getHeldItemModel(stack, null, null, 0);
        boolean bl = !model.isSideLit();
        if(bl){
            DiffuseLighting.disableGuiDepthLighting();
        }else{
            RenderSystem.setShaderLights(topLeftLight, topDownLight);
        }
        this.itemRenderer.renderItem(stack, ModelTransformation.Mode.GUI, false, matrices, immediate, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, model);
        immediate.draw();
        RenderSystem.enableDepthTest();
        if(bl){
            DiffuseLighting.enableGuiDepthLighting();
        }
        matrices.pop();


        if(amountText.length()>0){
            matrices.push();
            matrices.translate(0, 0, z + 1);
            this.textRenderer.drawWithShadow(matrices, amountText, x + 2 - (amountText.length() - 1) * 8, y + 1, Utils.rgb(255, 255, 255));
            matrices.pop();
        }
        //DiffuseLighting.method_34742();

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

    public MinecraftClient getClient(){
        return this.client;
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button){
        if(this.getSlotAt(mouseX, mouseY)==null){
            return ui.mouseClicked((float)mouseX - x, (float)mouseY - y, button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY){
        ui.mouseDragged(mouseX-x, mouseY-y, button, deltaX, deltaY);
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button){
        if(this.getSlotAt(mouseX, mouseY)==null){
            ui.mouseReleased(mouseX - x, mouseY - y, button);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount){
        ui.mouseScrolled(mouseX-x, mouseY-y, amount);
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers){
        ui.keyPressed(keyCode, scanCode, modifiers);
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers){
        ui.keyReleased(keyCode, scanCode, modifiers);
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers){
        ui.charTyped(chr, modifiers);
        return super.charTyped(chr, modifiers);
    }

    public TextRenderer getTextRenderer(){
        return this.textRenderer;
    }

    @Override
    public Optional<Element> hoveredElement(double mouseX, double mouseY){
        return super.hoveredElement(mouseX, mouseY);
    }

    private Slot getSlotAt(double x, double y) {
      for(int i = 0; i < this.handler.slots.size(); ++i) {
         Slot slot = (Slot)this.handler.slots.get(i);
         if (this.isPointOverSlot(slot, x, y) && slot.isEnabled()) {
            return slot;
         }
      }
      return null;
    }
    private boolean isPointOverSlot(Slot slot, double pointX, double pointY) {
        return this.isPointWithinBounds(slot.x, slot.y, 16, 16, pointX, pointY);
    }

    public <T extends InventoryScreenHandler> T getInvScreenHandler(){
        return (T)super.getScreenHandler();
    }
}
