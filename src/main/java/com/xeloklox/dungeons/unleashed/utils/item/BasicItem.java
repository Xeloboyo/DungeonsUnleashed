package com.xeloklox.dungeons.unleashed.utils.item;

import com.xeloklox.dungeons.unleashed.utils.lambda.*;
import net.minecraft.item.*;

public class BasicItem extends Item{
    public Boolf<ItemStack> hasGlint = i->false;
    public BasicItem(Settings settings,Cons<BasicItem> extra){
        super(settings);
        extra.get(this);
    }

    @Override
    public boolean hasGlint(ItemStack stack){
        return super.hasGlint(stack) || hasGlint.get(stack);
    }



}
