package com.xeloklox.dungeons.unleashed.utils.ui.particles;

import com.mojang.blaze3d.systems.*;
import com.xeloklox.dungeons.unleashed.utils.ui.*;
import net.minecraft.client.util.math.*;
import net.minecraft.item.*;
import net.minecraft.util.math.*;

public class ItemParticle extends UIParticle{
    ItemStack stack;
    public ItemParticle(float x, float y,ItemStack stack){
        super(x, y);
        this.stack=stack;
    }

    @Override
    public void draw(MatrixStack matrices, AnimatedScreen at){
        int tex = RenderSystem.getShaderTexture(0);
        Vector4f p = new Vector4f(x-8,y-8,0,1);
        p.transform(matrices.peek().getModel());
        at.drawItem(stack,p.getX(),p.getY(),stack.getCount()>1?stack.getCount()+"":"");
        RenderSystem.setShaderTexture(0,tex);
        RenderSystem.enableBlend();
    }
}
