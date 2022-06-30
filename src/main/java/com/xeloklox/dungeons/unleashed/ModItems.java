package com.xeloklox.dungeons.unleashed;

import com.xeloklox.dungeons.unleashed.items.*;
import com.xeloklox.dungeons.unleashed.utils.*;
import com.xeloklox.dungeons.unleashed.utils.item.*;
import com.xeloklox.dungeons.unleashed.utils.lambda.*;
import net.minecraft.item.*;
import net.minecraft.item.Item.*;
import net.minecraft.util.*;

public class ModItems{
    public static final RegisteredItem UNSTABLE_ENDER_PEARL;
    public static final RegisteredItem THUNDERSTONE,THUNDER_CORE;
    public static final RegisteredItem BLANK_TABLET,TABLET_OF_KNOWLEDGE;
    public static final RegisteredItem SALT,CRYSTALLISED_IGNIS;
    static{
        //region ITEMS
        UNSTABLE_ENDER_PEARL  = new RegisteredItem(
        "unstable_ender_pearl",
        new UnstableItem(
            getSettings(s->s.group(ItemGroup.MISC).maxCount(16).rarity(Rarity.UNCOMMON)),
            explosion->
                    explosion.setDelay(250)
                    .setChancePerTick(0.005f)
            )
        );
        UNSTABLE_ENDER_PEARL.setModel(model ->
            model.addOverride(override->
                override.setModel("item/unstable_ender_pearl2").addModelPredicate("explosion",0.01f)
            ).addOverride(override->
                override.setModel("item/unstable_ender_pearl3").addModelPredicate("explosion",0.5f)
            ).addOverride(override->
                override.setModel("item/unstable_ender_pearl4").addModelPredicate("explosion",0.75f)
            )
        );
        UNSTABLE_ENDER_PEARL.addPredicate("explosion",(itemstack, world, entity, seed) -> UnstableItem.getExplosionCharge(itemstack));
        UNSTABLE_ENDER_PEARL.finalise();

        THUNDERSTONE = new RegisteredItem("thunderstone", getSettings(s->s.group(ItemGroup.MISC))); //ItemGroup.MISC,Rarity.COMMON,64
        THUNDERSTONE.finalise();

        THUNDER_CORE = new RegisteredItem("thunder_core", getSettings(s->s.group(ItemGroup.MISC).maxCount(16)));
        THUNDER_CORE.finalise();

        BLANK_TABLET = new RegisteredItem("blank_tablet", getSettings(s->s.group(ItemGroup.MISC)));
        BLANK_TABLET.finalise();

        TABLET_OF_KNOWLEDGE = new RegisteredItem("knowledge_tablet",
            new BasicItem(
                getSettings(s->s.group(ItemGroup.MISC).maxCount(1)),
                item->{
                    item.hasGlint = ItemStack::hasNbt;
                }
            )
        );
        TABLET_OF_KNOWLEDGE.setName("Tablet of Knowledge");
        TABLET_OF_KNOWLEDGE.finalise();

        CRYSTALLISED_IGNIS = new RegisteredItem("crystallised_ignis", getSettings(s->s.group(ItemGroup.MISC).fireproof()));
        CRYSTALLISED_IGNIS.finalise();

        SALT = new RegisteredItem("salt", getSettings(s->s.group(ItemGroup.MISC)));
        SALT.finalise();


        //endregion
        //region HOOKS
        //endregion
    }




    public static Settings getSettings(Cons<Settings> settings){
        if(!Globals.bootStrapped){
            return new Settings();
        }
        Settings sett = new Settings();
        settings.get(sett);
        return sett;
    }
}



