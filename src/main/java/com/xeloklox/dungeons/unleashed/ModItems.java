package com.xeloklox.dungeons.unleashed;

import com.xeloklox.dungeons.unleashed.items.*;
import com.xeloklox.dungeons.unleashed.utils.*;
import com.xeloklox.dungeons.unleashed.utils.lambda.*;
import net.minecraft.item.*;
import net.minecraft.item.Item.*;
import net.minecraft.util.*;

public class ModItems{
    public static final RegisteredItem UNSTABLE_ENDER_PEARL;

    static{
        //region ITEMS
        UNSTABLE_ENDER_PEARL  = new RegisteredItem(
        "unstable_ender_pearl",
    new UnstableItem(
         getSettings(
                s->
                        s.group(ItemGroup.MISC)
                         .rarity(Rarity.UNCOMMON)
                         .maxCount(16)
                ),
                explosion->
                    explosion.setDelay(250)
                    .setChancePerTick(0.005f)
            ),
            model ->
            model.addOverride(override->
                override.setModel("item/unstable_ender_pearl2").addModelPredicate("explosion",0.01f)
            ).addOverride(override->
                override.setModel("item/unstable_ender_pearl3").addModelPredicate("explosion",0.5f)
            ).addOverride(override->
                override.setModel("item/unstable_ender_pearl4").addModelPredicate("explosion",0.75f)
            ),
            item->item.addPredicate("explosion",(itemstack, world, entity, seed) -> UnstableItem.getExplosionCharge(itemstack))
        );


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



