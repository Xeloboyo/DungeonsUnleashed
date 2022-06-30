package com.xeloklox.dungeons.unleashed.utils.ui;

import net.minecraft.util.*;

import static com.xeloklox.dungeons.unleashed.ModInitClientServer.MODID;

public class UITextures{
    public static final Identifier TEXTURE = new Identifier(MODID, "textures/gui/ui.png");
    public static RegionNineSlice largeFrame = new RegionNineSlice(new TextureRegion(0,0,33,33,TEXTURE),16,1).setInset(9,9);
    public static RegionNineSlice panel = new RegionNineSlice(new TextureRegion(33,0,23,23,TEXTURE),10,2).setInset(4,4);
    public static RegionNineSlice upTab = new RegionNineSlice(new TextureRegion(48,32,16,16,TEXTURE),4,8).setInset(4,4);
    public static RegionNineSlice card = new RegionNineSlice(new TextureRegion(0,33,48,48,TEXTURE),16,16).setInset(9,9);
    public static RegionNineSlice blank = new RegionNineSlice(new TextureRegion(0,253,3,3,TEXTURE),1,1);
    public static RegionNineSlice defaultButtonUp = new RegionNineSlice(new TextureRegion(0,96,15,15,TEXTURE),5,5).setInset(2,2);
    public static RegionNineSlice defaultButtonDown = new RegionNineSlice(new TextureRegion(16,96,15,15,TEXTURE),5,5).setInset(2,2);
    public static RegionNineSlice fancyButtonUp = new RegionNineSlice(new TextureRegion(134,0,19,19,TEXTURE),9).setInset(2,2);
    public static RegionNineSlice fancyButtonDown = new RegionNineSlice(new TextureRegion(155,2,15,15,TEXTURE),7);
    public static RegionNineSlice insetShadow = new RegionNineSlice(new TextureRegion(16,112,16,16,TEXTURE),2);
    public static RegionNineSlice dropShadow = new RegionNineSlice(new TextureRegion(112,0,19,19,TEXTURE),4).setInset(2,2);
    public static RegionNineSlice sliderVertical = new RegionNineSlice(new TextureRegion(64,80,11,15,TEXTURE),5).setInset(1,2);
    public static RegionNineSlice selectionInset = new RegionNineSlice(new TextureRegion(81,65,14,14,TEXTURE),3);
    public static RegionNineSlice roll = new RegionNineSlice(new TextureRegion(97,80,14,13,TEXTURE),3,8,3,3,4,6).setInset(1,2);
    public static RegionNineSlice parchment = new RegionNineSlice(new TextureRegion(64,96,27,14,TEXTURE),10,7,10,4,6,4).setInset(10,2);
    public static RegionNineSlice beveled = new RegionNineSlice(new TextureRegion(64,112,16,16,TEXTURE),5);

    public static RegionThreeSlice progressBar = new RegionThreeSlice(new TextureRegion(64,160,16,7,TEXTURE),3);
    public static RegionThreeSlice progressBarInside = new RegionThreeSlice(new TextureRegion(64,167,6,3,TEXTURE),1);
    public static RegionThreeSlice SideBarResearch = new RegionThreeSlice(new TextureRegion(224,0,32,80,TEXTURE),48,16,16);

    public static TextureRegion whitebar = new TextureRegion(113,24,6,8,TEXTURE);
    public static TextureRegion magGlassDefault = new TextureRegion(142,137,114,119,TEXTURE);
    public static TextureRegion stickytape = new TextureRegion(81,80,10,15,TEXTURE);
    public static TextureRegion inventory = new TextureRegion(64,48,16,16,TEXTURE);
    public static TextureRegion inventoryBack = new TextureRegion(96,48,16,16,TEXTURE);
    public static TextureRegion debug = new TextureRegion(112,48,16,16,TEXTURE);
    public static TextureRegion magnifyingGlassIcon = new TextureRegion(128,48,16,16,TEXTURE);
    public static TextureRegion inventorySlot = new TextureRegion(0,144,18,18,TEXTURE);
    public static TextureRegion cross = new TextureRegion(64,32,16,16,TEXTURE);
    public static TextureRegion ellipsis = new TextureRegion(96,32,16,16,TEXTURE);
    public static TextureRegion check = new TextureRegion(112,32,16,16,TEXTURE);
    public static TextureRegion crossSmall = new TextureRegion(32,96,6,6,TEXTURE);
    public static TextureRegion minimiseSmall = new TextureRegion(38,96,6,6,TEXTURE);
    public static TextureRegion completeCheck = new TextureRegion(64,16,16,16,TEXTURE);
    public static TextureRegion dropShadowItem = new TextureRegion(64,0,16,16,TEXTURE);
    public static TextureRegion researchNodeNormal = new TextureRegion(64,128,16,16,TEXTURE);
    public static TextureRegion researchNodeFinal = new TextureRegion(80,128,16,16,TEXTURE);
    public static TextureRegion researchNodeActivated = new TextureRegion(64,144,16,16,TEXTURE);

    public static TextureRegion rightTriangle = new TextureRegion(80,32,16,16,TEXTURE);
    public static TextureRegion leftTriangle = new TextureRegion(80,16,16,16,TEXTURE);

    public static TextureRegion[] insetrunes = new TextureRegion[3];

    public static TextureRegion blanksquare = new TextureRegion(0,253,3,3,TEXTURE);
    public static TextureRegion entireTexture = new TextureRegion(0f,1,0,1f,256,256,TEXTURE);

    static {
        for(int i = 0;i<insetrunes.length;i++){
            insetrunes[i] = new TextureRegion(i*8,128,7,7,TEXTURE);
        }
    }
}
