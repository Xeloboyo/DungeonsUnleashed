package com.xeloklox.dungeons.unleashed.utils.ui.components;

import com.xeloklox.dungeons.unleashed.utils.*;
import com.xeloklox.dungeons.unleashed.utils.ui.*;

public class UIResearchProgressBar extends UIComponent{
    RegionThreeSlice bgslice,bar;
    public float progress =0;
    public int stage=0,totalstages;
    int insetx,insety;
    TextureRegion regularNode = UITextures.researchNodeNormal;
    TextureRegion finalNode = UITextures.researchNodeFinal;
    TextureRegion nodeActivated = UITextures.researchNodeActivated;

    public UIResearchProgressBar(RegionThreeSlice bgslice,RegionThreeSlice bar,int stages, String id){
        super(id);
        this.bgslice = bgslice;
        this.bar=bar;
        totalstages=stages;
    }

    public UIResearchProgressBar( String id, int stages){
        this(UITextures.progressBar,UITextures.progressBarInside,stages,id);
        insetx=2;
        insety=2;
    }

    @Override
    public void resetMinSize(){
        super.resetMinSize();
        minh += Math.max(bar.baseSlice.h,finalNode.h);
        minw += finalNode.w;
    }

    @Override
    public void draw(SpriteDrawer sb){
        float ay = y+h*0.5f;
        float aw = w-8;
        bgslice.drawInner(sb,x+padl,ay-bgslice.baseSlice.h*0.5f,aw);
        float awb = aw-insetx*2;
        bar.drawInner(sb,x+padl+insetx,ay-bar.baseSlice.h*0.5f,awb*progress);
        for(int i = 1;i<=totalstages;i++){
            float nx = ((float)i/totalstages)*awb + insetx + x;
            if(i!=totalstages){
                sb.drawCentered(regularNode,nx,ay);
            }else{
                sb.drawCentered(finalNode,nx,ay);
            }
            if(i<=stage){
                sb.drawCentered(nodeActivated,nx,ay);
            }
        }
    }

    @Override
    public void update(){
        progress = Mathf.lerpTowards(progress,stage/(float)totalstages,0.1f);
    }
}
