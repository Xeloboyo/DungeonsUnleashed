package com.xeloklox.dungeons.unleashed.blockentity.screens;

import com.mojang.blaze3d.systems.*;
import com.xeloklox.dungeons.unleashed.blockentity.*;
import com.xeloklox.dungeons.unleashed.utils.*;
import com.xeloklox.dungeons.unleashed.utils.animation.*;
import com.xeloklox.dungeons.unleashed.utils.animation.Interpolations.*;
import com.xeloklox.dungeons.unleashed.utils.animation.StateMap.*;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.*;
import net.minecraft.entity.player.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import org.mini2Dx.gdx.utils.*;

import static com.xeloklox.dungeons.unleashed.DungeonsUnleashed.MODID;

public class InfuserScreen extends AnimatedScreen<InfuserScreenHandler>{
    private static final Identifier TEXTURE = new Identifier(MODID, "textures/gui/infuser_ui.png");
    private static final Identifier RING = new Identifier(MODID, "textures/gui/infuser_ring.png");

    public static final String STATE_OPENING = "opening";
    public static final String STATE_IDLE_POWERED = "idle_powered";
    public static final String STATE_IDLE_POWERINGUP = "idle_poweringup";
    public static final String STATE_IDLE_UNPOWERED = "idle_unpowered";
    public static final String STATE_IDLE_POWERINGDOWN = "idle_poweringdown";
    public static final String STATE_WINDINGUP = "workwindup";
    public static final String STATE_WINDINGDOWN = "workwinddown";
    public static final String STATE_WORKING = "work";

    float ringangle = 0;
    final float centerX = 101,centerY = 54;
    Array<DistortableParticle> particles = new Array<>();

    public InfuserScreen(InfuserScreenHandler handler, PlayerInventory inventory, Text title){
        super(handler, inventory, title,50,

        pmap -> {
            pmap.add("alpha",0f);
            pmap.add("rotation",5f);
            pmap.add("tab",0f);
            pmap.add("frame",0);
            pmap.add("glowalpha",0f);
        });

        stateMap.onStateEnd = stateMap -> {
            if(stateMap.getCurrent()==null){
                return STATE_OPENING;
            }
            switch(stateMap.getCurrent().getName()){
                case STATE_IDLE_POWERINGDOWN:
                    return STATE_IDLE_UNPOWERED;
                case STATE_WINDINGUP:
                    return STATE_WORKING;
                case STATE_IDLE_POWERINGUP:
                    return STATE_IDLE_POWERED;
                case STATE_WINDINGDOWN:
                case STATE_OPENING:
                    if(isPowered()){
                        if(handler.propertyDelegate.get(InfuserEntity.D_TOTAL)>0){
                            return STATE_WINDINGUP;
                        }else{
                            return STATE_IDLE_POWERED;
                        }
                    }
                    return STATE_IDLE_POWERINGDOWN;
                default:
                    if(isPowered()){
                        return STATE_IDLE_POWERINGUP;
                    }
                    return STATE_IDLE_POWERINGDOWN;
            }
        };

        stateMap.addState(AnimationState.get(STATE_OPENING).loops(false).onInit(pmap -> {
            pmap.f("x_offset",-50);
            pmap.addInterpolator(SingularInterpolateType.EXPONENTIAL,5f,"x_offset",new FloatInterpolate(),0f);
            pmap.addChainedInterpolator(SingularInterpolateType.EXPONENTIAL2,0.2f,"alpha",new FloatInterpolate(),1f,1f,1f);
            pmap.addInterpolator(SingularInterpolateType.EXPONENTIAL2,0.2f,"rotation",new FloatInterpolate(), 0f);
            pmap.addChainedInterpolator(SingularInterpolateType.EXPONENTIAL2,0.2f,"tab",new FloatInterpolate(),
                FrameState.get(5f,0.6f),
                    FrameState.get(14f,0.3f),
                    FrameState.get(12f,0.1f)
            );
        }));
        stateMap.addState(AnimationState.get(STATE_IDLE_UNPOWERED).loops(true).duration(1000).needsToComplete(false).onInit(pmap -> { }));
        stateMap.addState(AnimationState.get(STATE_IDLE_POWERINGDOWN).loops(false).needsToComplete(true).onInit(pmap -> {
            pmap.addInterpolator(SingularInterpolateType.EXPONENTIAL2,0.2f,"rotation",new FloatInterpolate(), 0f);
            pmap.addInterpolator(SingularInterpolateType.EXPONENTIAL2,0.2f,"glowalpha",new FloatInterpolate(), 0f);
            pmap.addInterpolator(SingularInterpolateType.EXPONENTIAL2,0.2f,"frame",new IntInterpolate(), 0);
            pmap.addChainedInterpolator(SingularInterpolateType.EXPONENTIAL2,0.2f,"tab",new FloatInterpolate(),
                FrameState.get(14f,0.1f),
                FrameState.get(5f,0.9f)
            );
        }));
        stateMap.addState(AnimationState.get(STATE_IDLE_POWERINGUP).loops(false).needsToComplete(true).onInit(pmap -> {
            pmap.addChainedInterpolator(SingularInterpolateType.EXPONENTIAL2,0.2f,"glowalpha",new FloatInterpolate(),
                FrameState.get(1f,0.1f),
                FrameState.get(0.4f,0.9f)
            );
            pmap.addChainedInterpolator(SingularInterpolateType.EXPONENTIAL2,0.2f,"tab",new FloatInterpolate(),
                FrameState.get(14f,0.1f),
                FrameState.get(12f,0.9f)
            );
        }));
        stateMap.addState(AnimationState.get(STATE_IDLE_POWERED).loops(true).duration(600).needsToComplete(false).onInit(pmap -> {
            pmap.addInterpolator(SingularInterpolateType.EXPONENTIAL2,0.2f,"tab",new FloatInterpolate(), 12f);
            pmap.addChainedInterpolator(SingularInterpolateType.EXPONENTIAL2,0.2f,"glowalpha",new FloatInterpolate(),
            FrameState.get(0.4f,0.25f),
                FrameState.get(0.2f,0.25f),
                FrameState.get(0.5f,0.25f),
                FrameState.get(0.3f,0.25f)
            );
            pmap.addChainedInterpolator(SingularInterpolateType.EXPONENTIAL2,0.2f,"rotation",new FloatInterpolate(),
            FrameState.get(0f,0.1f),
                FrameState.get(-1f,0.05f),
                FrameState.get(0f,0.12f),
                FrameState.get(1f,0.03f),
                FrameState.get(0f,0.32f),
                FrameState.get(-2f,0.07f),
                FrameState.get(0f,0.22f),
                FrameState.get(-1f,0.03f),
                FrameState.get(2f,0.07f),
                FrameState.get(0f,0.12f)
            );
        }));
        stateMap.addState(AnimationState.get(STATE_WORKING).loops(true).duration(30).needsToComplete(false).resetOnloop(false).onInit(pmap -> {
            pmap.addChainedInterpolator(SingularInterpolateType.EXPONENTIAL2,0.2f,"glowalpha",new FloatInterpolate(),
           FrameState.get(2f,0.25f),
               FrameState.get(1.5f,0.25f),
               FrameState.get(1.8f,0.25f),
               FrameState.get(1.2f,0.25f)
            );
            pmap.addInterpolator(SingularInterpolateType.EXPONENTIAL2,0.2f,"frame",new IntInterpolate(), 6);
        }));
        stateMap.addState(AnimationState.get(STATE_WINDINGUP).loops(false).duration(100).needsToComplete(true).onInit(pmap -> {
            pmap.addInterpolator(SingularInterpolateType.EXPONENTIAL2,0.2f,"rotation",new FloatInterpolate(), 10f);
            pmap.addInterpolator(SingularInterpolateType.EXPONENTIAL2,0.2f,"glowalpha",new FloatInterpolate(), 1f);
            pmap.addInterpolator(SingularInterpolateType.EXPONENTIAL2,0.2f,"frame",new IntInterpolate(), 0);
        }));
        stateMap.addState(AnimationState.get(STATE_WINDINGDOWN).loops(false).duration(100).needsToComplete(true).onInit(pmap -> {
            pmap.addInterpolator(SingularInterpolateType.EXPONENTIAL2,0.2f,"rotation",new FloatInterpolate(), 0f);
            pmap.addInterpolator(SingularInterpolateType.EXPONENTIAL2,0.2f,"glowalpha",new FloatInterpolate(), 0.4f);
            pmap.addInterpolator(SingularInterpolateType.EXPONENTIAL2,0.2f,"frame",new IntInterpolate(), 0);
        }));
        backgroundWidth=203;
        backgroundHeight=200;
        playerInventoryTitleX = 23;
        playerInventoryTitleY = 107;
        titleY = -13;

    }
    //71,20  61,70    190,105
    // 79 34    ,   0 ,200   47, 41
    //  88,39       218,48,28,31
    //93,44    238,0,18,21
    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY){
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, stateMap.f("alpha"));
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        matrices.push();
        matrices.translate(x+stateMap.f("x_offset"),y,0);
        drawTexture(matrices, 0, 0, 0, 0, backgroundWidth, backgroundHeight);
        //RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, );
        drawTexture(matrices, 142, 86, 228, getPowerStatusIndex()*8, 8, 8);
        drawTexture(matrices, 44, 1, 216-(int)stateMap.f("tab"), 1, (int)stateMap.f("tab"), 103);
        if(stateMap.i("frame")>0 && stateMap.i("frame")<6){
            drawTexture(matrices, 79, 34, 47 * (stateMap.i("frame")-1), 200, 47, 41);
        }
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, stateMap.f("glowalpha"));
        drawTexture(matrices, 71, 20, 190, 105, 61, 70);
        if(stateMap.f("glowalpha")>1){
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, stateMap.f("glowalpha")-1.0f);
            drawTexture(matrices, 88, 39, 218, 48, 28, 31);
        }
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1f);
        drawArc(matrices,146,90,8,2,0,360f*getEnergy(),240,29,4,2);

        RenderSystem.setShaderTexture(0, RING);
        matrices.push();
        matrices.translate(102,55,0);
        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(ringangle));
        drawTexture(matrices, -64, -64, 0, 0, 128, 128,128,128);
        matrices.pop();

        RenderSystem.setShaderTexture(0, TEXTURE);
        for(int i=0;i<particles.size;i++){
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, Math.min(1.0f,particles.get(i).life*0.1f));
            particles.get(i).draw(matrices.peek().getModel(), this);
        }
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1f);
        drawTexture(matrices, 93,44 , 238,0, 18, (int)(21*getProgress()));
        matrices.pop();
        RenderSystem.disableBlend();
    }

    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
      this.textRenderer.draw(matrices, this.title, (float)this.titleX, (float)this.titleY, 0xffffff);
      this.textRenderer.draw(matrices, this.playerInventoryTitle, (float)this.playerInventoryTitleX, (float)this.playerInventoryTitleY, 4210752);
    }



    @Override
    public void updateLogic(){
        if(isPowered()){
            if(stateMap.isState(STATE_IDLE_POWERINGDOWN)||stateMap.isState(STATE_IDLE_UNPOWERED)){
                stateMap.requestState(STATE_IDLE_POWERINGUP);
            }
            boolean working =  this.getScreenHandler().getSyncedInt(InfuserEntity.D_TOTAL)>0;
            if(stateMap.isState(STATE_IDLE_POWERED) && working){
                stateMap.requestState(STATE_WINDINGUP);
            }else if(stateMap.isState(STATE_WORKING)  && !working){
                stateMap.requestState(STATE_WINDINGDOWN);
            }
        }else{
            if(stateMap.isState(STATE_IDLE_POWERINGUP)||stateMap.isState(STATE_IDLE_POWERED)){
                stateMap.requestState(STATE_IDLE_POWERINGDOWN);
            }
            if(stateMap.isState(STATE_WORKING)||stateMap.isState(STATE_WINDINGUP)){
                stateMap.requestState(STATE_WINDINGDOWN);
            }
        }
        ringangle += stateMap.f("rotation");
        if(stateMap.isState(STATE_WORKING) && Mathf.randFloat(10)<1){
            Vec2f v = Mathf.randVec2().multiply(65);
            addParticle(v.x+centerX,v.y+centerY);
        }
        for(int i=0;i<particles.size;i++){
            float dx = centerX-particles.get(i).x;
            float dy = centerY-particles.get(i).y;
            float d =  MathHelper.sqrt(dx*dx+dy*dy);

            particles.get(i).vx+=0.1f*dx/d;
            particles.get(i).vy+=0.1f*dy/d;
            if(d<16){
                particles.get(i).vx*=0.8;
                particles.get(i).vy*=0.8;
            }
            if(d<10){
                particles.removeIndex(i);
            }
        }

    }

    int get(int index){
        return this.getScreenHandler().getSyncedInt(index);
    }

    boolean isPowered(){
        return get(InfuserEntity.D_POWERSTATUS)>0||get(InfuserEntity.D_CHARGELEVEL)>0;
    }
    float getEnergy(){
        return get(InfuserEntity.D_CHARGELEVEL)/100f;
    }

    float getProgress(){
        int t = get(InfuserEntity.D_TOTAL);
        if(t==0){return 0;}
        return get(InfuserEntity.D_PROGRESS) / (float)get(InfuserEntity.D_TOTAL);
    }
    int getPowerStatusIndex(){
        int p = get(InfuserEntity.D_POWERSTATUS);
        if(p==0){return 0;}
        if(p==1){return 1;}
        if(p<5){return 2;}
        if(p<8){return 3;}
        if(p<12){return 4;}
        return 5;
    }

    void addParticle(float x,float y){
        //101,54
        float dpx = centerX-x;
        float dpy = centerY-y;
        float dp = MathHelper.sqrt(dpx*dpx+dpy*dpy);
        particles.add(new DistortableParticle(x,y,10,0.3f*dpx/dp,0.3f*dpy/dp,3+16*Mathf.randInt(16),246,256,256,(px, py, out) -> {
            float dx = centerX-px,dy=centerY-py;
            float d = MathHelper.sqrt(dx*dx+dy*dy);
            float shift = 30f/(1f+d*0.1f);
            if(d<16){shift=d-16;}
            float d1 = 1f/d;
            out[0]= px+(dx*d1)*shift;
            out[1]= py+(dy*d1)*shift;
        }));
    }
}
