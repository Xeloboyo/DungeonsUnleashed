package com.xeloklox.dungeons.unleashed.utils.ui.components;

import com.xeloklox.dungeons.unleashed.utils.ui.*;
import net.minecraft.item.*;

public class UIItemIcon extends UIComponent{
    public RegionNineSlice bg;
    public ItemStack stack;

    public UIItemIcon(String id,ItemStack itemstack){
        super(id);
        this.stack=itemstack;
    }

    public UIItemIcon setBg(RegionNineSlice bg){
        this.bg = bg;
        return this;
    }

    @Override
    public void draw(SpriteDrawer sb){
        sb.drawItem(stack,x+8+padl,y+8+padr,getZ());
    }

    @Override
    public void resetMinSize(){
        super.resetMinSize();
        minw+=16;
        minh+=16;
    }

    @Override
    public void update(){

    }

}
