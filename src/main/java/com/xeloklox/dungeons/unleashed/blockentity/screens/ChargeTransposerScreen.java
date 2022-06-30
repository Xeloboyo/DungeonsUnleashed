package com.xeloklox.dungeons.unleashed.blockentity.screens;

import com.mojang.blaze3d.systems.*;
import com.xeloklox.dungeons.unleashed.*;
import com.xeloklox.dungeons.unleashed.blockentity.*;
import com.xeloklox.dungeons.unleashed.utils.ui.particles.*;
import com.xeloklox.dungeons.unleashed.utils.ui.particles.UIParticle.*;
import com.xeloklox.dungeons.unleashed.gen.*;
import com.xeloklox.dungeons.unleashed.utils.*;
import com.xeloklox.dungeons.unleashed.utils.animation.*;
import com.xeloklox.dungeons.unleashed.utils.models.*;
import com.xeloklox.dungeons.unleashed.utils.models.RenderableModel.*;
import com.xeloklox.dungeons.unleashed.utils.ui.*;
import net.minecraft.client.model.*;
import net.minecraft.client.util.math.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import org.mini2Dx.gdx.utils.*;

import static com.xeloklox.dungeons.unleashed.ModInitClientServer.MODID;

public class ChargeTransposerScreen extends AnimatedScreen<ChargeTransposerScreenHandler>{
    private static final Identifier TEXTURE = new Identifier(MODID, "textures/gui/transposer_ui.png");
    public static RenderableModel model;
    public static BoneAnimationGroup model_animation;
    public static BoneAnimation switchAnimation;

    ObjectMap<String, BoneTranslationParameters> params;
    static {
        ModelJson modelJson =ModelJson.getModel("ui/transposer_handle","gui/transposer_ui");
        model = new RenderableModel(modelJson, ModelTransform.pivot(0,0,0));
        model_animation = new BoneAnimationGroup(BoneAnimation.getAnimation("ui/transposer_handle"),model);
        switchAnimation = model_animation.get("switch");
    }

    public ChargeTransposerScreen(ChargeTransposerScreenHandler handler, PlayerInventory inventory, Text title){
        super(handler, inventory, title, 50, pmap->{


        });
        params = model.getParamMap();

        backgroundWidth = 176;
        backgroundHeight = 200;
        playerInventoryTitleX = 23;
        playerInventoryTitleY = 107;
        titleY = -13;
        attractionPoint = (ParticleAttractor)new ParticleAttractor(200,89,0,0.15f).setStartTime(50).setToLayer(1);
        repulsionPoint = (ParticleAttractor)new ParticleAttractor(40,89,50,-0.1f).setEndTime(50).setToLayer(1);
        particles.addAffector(attractionPoint);
        particles.addAffector(ParticleDeleter.create(60,89,50).setInvert(true).setToLayer(1));
        particles.addAffector(repulsionPoint);
        particles.addAffector(new ParticleAccelerator(100,89,50,0.90f).setEndTime(50).setToLayer(1));
        particles.addAffector(new ParticleAttractor(100,89,50,0.15f));
        particles.addAffector(new ParticleAccelerator(100,89,50,0.97f));
    }
    float t = 0;
    boolean rotate= false;
    float bgMovePos, pastProgression, progression;
    boolean item = false;
    UIParticle itemParticle = null;
    ParticleAttractor attractionPoint;
    ParticleAttractor repulsionPoint;
    @Override
    public void updateLogic(){
        int flip = this.getScreenHandler().propertyDelegate.get(ChargeTransposerEntity.D_FLIP);
        rotate = flip==0;
        t = Mathf.approach(t,flip,0.01f);

        int delayProg = this.getScreenHandler().propertyDelegate.get(ChargeTransposerEntity.D_DELAY);
        progression = delayProg/(float)ChargeTransposerEntity.delay;
        if(pastProgression > progression && itemParticle!=null){
            for(int i = 0;i<20;i++){
                Vec2f randp = Mathf.randVec2().multiply(4);
                float spd = Mathf.randFloat(0.5f,1);
                particles.add(new DistortableUIParticle(itemParticle.x + randp.x, itemParticle.y + randp.y, 8, randp.x*spd, randp.y*spd, 248, 8 * Mathf.randInt(4), 256, 256,
                DistortableUIParticle.distortTowardsPoint(89, 50, 1,60))).setLayer(0, false).setLayer(1, true).renderAffectors = new RenderAffectors[]{RenderAffectors.FADE_IN, RenderAffectors.ADD_BLEND};
            }
            itemParticle.forceKill();
            itemParticle=null;
        }
        pastProgression = progression;
        bgMovePos = progression;
        if(!rotate){
            bgMovePos =1- bgMovePos;
        }
        item = this.getScreenHandler().propertyDelegate.get(ChargeTransposerEntity.D_ITEM)==1;
        if(item){
            if(itemParticle==null){
                itemParticle = particles.add(new ItemParticle(22,51,new ItemStack(ModItems.THUNDERSTONE.get(),1)));
                Vec2f randVel = Mathf.randVec2();
                if(progression>0.2){
                    itemParticle.x = 89+randVel.x;
                    itemParticle.y = 50+randVel.y;
                }
                itemParticle.vx = randVel.x;  itemParticle.vy = randVel.y;
                itemParticle.setRenderAffectors(RenderAffectors.FADE_IN);
                itemParticle.cantDie = true;
            }
            repulsionPoint.x = itemParticle.x;
            repulsionPoint.y = itemParticle.y;
            if(Mathf.randFloat(10)<progression){
                Vec2f randp = Mathf.randVec2().multiply(3);
                particles.add(new DistortableUIParticle(itemParticle.x + randp.x, itemParticle.y + randp.y, 8, randp.x, randp.y, 248, 8 * Mathf.randInt(4), 256, 256,
                DistortableUIParticle.distortTowardsPoint(89, 50, 1,60))).setLayer(0, false).setLayer(1, true).renderAffectors = new RenderAffectors[]{RenderAffectors.FADE_IN, RenderAffectors.ADD_BLEND};
            }
        }else{
            if(itemParticle!=null){
                itemParticle.forceKill();
                itemParticle=null;
            }
        }
        attractionPoint.y = rotate?114:-10;
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY){
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        matrices.push();
            matrices.translate(x+stateMap.f("x_offset"),y,0);
            drawTexture(matrices, 0, 0, 0, 0, backgroundWidth, backgroundHeight);
            drawSection(matrices, 60, 1,176,0,53,103,0,1, bgMovePos, bgMovePos +0.3f);
            drawSection(matrices, 60, 1,176,0,53,103,0,1, bgMovePos -1, bgMovePos +0.3f-1);
            RenderSystem.enableBlend();
            if(itemParticle!=null){
                additiveBlendMode();
                alpha(progression);
                matrices.push();
                    matrices.translate(itemParticle.x,itemParticle.y,0);
                    rotate(matrices, progression *360);
                    drawTexture(matrices, -16, -16, 208, 112, 32, 32);
                    rotate(matrices, progression *120+90);
                    drawTexture(matrices, -16, -16, 208, 112, 32, 32);
                matrices.pop();
            }
            particles.draw(matrices, this);
            additiveBlendMode();
            if(itemParticle!=null){
                alpha(progression);
                drawTexture(matrices, (int)(itemParticle.x-16), (int)(itemParticle.y-16), 208, 144, 32, 32);
            }
            resetBlendMode();
            matrices.push();
                matrices.translate(89,50,0);
                rotate(matrices,rotate?180:0);
                switchAnimation.animate(rotate?1-t:t,params);
                model.render(matrices,params);
            matrices.pop();

        matrices.pop();
    }


    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
      this.textRenderer.draw(matrices, this.title, (float)this.titleX, (float)this.titleY, 0xffffff);
      this.textRenderer.draw(matrices, this.playerInventoryTitle, (float)this.playerInventoryTitleX, (float)this.playerInventoryTitleY, 4210752);
    }
}
