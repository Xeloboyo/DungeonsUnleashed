package com.xeloklox.dungeons.unleashed.blockentity.screens;

import com.xeloklox.dungeons.unleashed.*;
import com.xeloklox.dungeons.unleashed.utils.*;
import com.xeloklox.dungeons.unleashed.utils.animation.Interpolations.*;
import com.xeloklox.dungeons.unleashed.utils.ui.*;
import com.xeloklox.dungeons.unleashed.utils.ui.components.*;
import com.xeloklox.dungeons.unleashed.utils.ui.components.UIFlexContainer.*;
import com.xeloklox.dungeons.unleashed.utils.ui.components.UIResearchBoard.*;
import net.fabricmc.fabric.api.client.networking.v1.*;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.client.util.math.*;
import net.minecraft.entity.player.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;

import static com.xeloklox.dungeons.unleashed.ModInitClientServer.MODID;
import static com.xeloklox.dungeons.unleashed.utils.ui.components.UIComponent.DISABLE;

public class ResearchMapScreen extends AnimatedScreen<ResearchMapScreenHandler>{

    public ResearchMapScreen(ResearchMapScreenHandler handler, PlayerInventory inventory, Text title){
        super(handler, inventory, title, 100, params->{});
    }

    boolean toggleInv = true;
    UIResearchBoard panningContainer;

    @Override
    protected void init(){
        backgroundWidth= (int)(width*0.75f);
        backgroundHeight= (int)(height*0.85f);

        super.init();
        if(!ui.components.isEmpty()){
            ui.get(UIResearchBoard.class,"map").resize(width*0.75f-40,height*0.85f);
            return;
        }
        handler.moveSlot(handler.getSlot(handler.getSlotIndex(handler.inventory,0).getAsInt()),backgroundWidth-24,8);
        MarchingSquaresBackground mbg = new MarchingSquaresBackground(new TextureRegion(1024,1024, new Identifier(MODID, "textures/gui/bgpiece1.png")),128,128);
        mbg.setOrigin(501,904);
        MarchingSquaresBackground mbg0 = new MarchingSquaresBackground(new TextureRegion(128,128, new Identifier(MODID, "textures/gui/bgpiece0.png")),32,32);
        mbg0.setOrigin(64,64);

        ResearchItemUI RESEARCH_THUNDERSTONE = new ResearchItemUI(ModResearch.RESEARCH_THUNDERSTONE);
        ResearchItemUI RESEARCH_THUNDERCORE = new ResearchItemUI(ModResearch.RESEARCH_THUNDERCORE);


        panningContainer = (UIResearchBoard)UIResearchBoard.create("map",this,handler.hiddenInventory,mbg)
            .setFrame(UITextures.largeFrame)
            .setBg(UITextures.blank).setBgcolor(new Vector4f(0.36f,0.36f,0.37f,1.0f))
            .resize(width*0.75f-40,height*0.85f);
        panningContainer.add(RESEARCH_THUNDERSTONE,0,0);
        panningContainer.addRelative(RESEARCH_THUNDERSTONE.item.name,RESEARCH_THUNDERCORE,0,-50);
        panningContainer.panx = panningContainer.getW()*0.5f;
        panningContainer.pany = panningContainer.getH()*0.5f;
        panningContainer.setDefaultbg(mbg0);
        ui.add(panningContainer);

        ui.add(UIButton.create("debug")
            .setIcon(UITextures.debug)
            .setOnClicked(b->{UIContainer.debugDraw=!UIContainer.debugDraw;})
            .setPositioning(0,DISABLE,0,DISABLE)
            .setZ(2)
            .resize(15,15));

        ui.add(new UIMagnifyingGlass("magglass",panningContainer).setPositioning(width*0.75f-96,0).setZ(90));

        ui.add(UIButton.create("magglassbutton")
            .setIcon(UITextures.magnifyingGlassIcon)
            .setOnClicked(b->{
                var glass = ui.get(UIMagnifyingGlass.class,"magglass");
                glass.disabled =!glass.disabled;
                glass.setPositioning(52,0);
            })
            .setPositioning(26,DISABLE,0,DISABLE)
            .setZ(2)
            .resize(15,15));

        UIFlexContainer inventoryHolder = (UIFlexContainer)UIFlexContainer.create("invholder",this).setPositioning(0,30).setZ(100);
        inventoryHolder.addFlex(new UISlotHolder("playerinv",this.handler,this.handler.pl)).setAlign(FlexItemAlign.START).setFlexshrink(0);
        UIFlexContainer buttonpanel =  (UIFlexContainer)UIFlexContainer.create("butpanel",this).setDirection(FlexDirection.COLUMN).pad(0);
        buttonpanel.addFlex(new UIButton("toggleinv").setIcon(UITextures.leftTriangle).setOnClicked(b->{
            toggleInv = !toggleInv;
            ui.addAnimation(40, SingularInterpolateType.EXPONENTIAL2, time->{
                if(toggleInv){
                    inventoryHolder.setPositioning((-168 - x) * time, 30);
                    inventoryHolder.getChildHandler().get(UIButton.class, "toggleinv").setIcon(UITextures.rightTriangle);
                }else{
                    inventoryHolder.setPositioning((-168 - x) * (1-time), 30);
                    inventoryHolder.getChildHandler().get(UIButton.class, "toggleinv").setIcon(UITextures.leftTriangle);
                }
            });
        }));
        buttonpanel.addFlex(new UIButton("dropinv").setIcon(UITextures.inventoryBack).setOnClicked(b->{
            panningContainer.clearItems();
            ClientPlayNetworking.send(ModResearch.moveItemsBack.getIdentifier(), PacketByteBufs.empty());
        }).marginBottom(5));
        buttonpanel.addFlex(UIImage.create("invenicon",UITextures.inventory).setMinimumSize(16,16)).setAlign(FlexItemAlign.CENTER);
        inventoryHolder.add(buttonpanel);
        inventoryHolder.setBg(UITextures.panel);
        inventoryHolder.resize(175,80);
        inventoryHolder.padr = 0;
        inventoryHolder.isInherentClickConsumer = true;
        inventoryHolder.isInherentReleaseConsumer = true;
        inventoryHolder.rebuild();
        ui.add(inventoryHolder);

        //yes
        buttonpanel.getChildHandler().get(UIButton.class,"toggleinv").triggerClick();
        ui.get(UIButton.class,"magglassbutton").triggerClick();

    }

    float t= 0;

    @Override
    public void updateLogic(){
        if(handler.tabletUpdated && panningContainer!=null){
            panningContainer.tabletUpdated(handler.inventory.getStack(0));
            handler.tabletUpdated = false;
        }
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY){
        spriteDrawer.reset(matrices);
        spriteDrawer.begin();
        spriteDrawer.getMatrixStack().push();
        spriteDrawer.getMatrixStack().translate(x,y,0);
        UITextures.SideBarResearch.drawInnerTiled(this.spriteDrawer,backgroundWidth-32,0,backgroundHeight);
        spriteDrawer.getMatrixStack().pop();
        spriteDrawer.end();
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY){ }
}
