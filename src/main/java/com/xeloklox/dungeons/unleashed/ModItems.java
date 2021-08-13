package com.xeloklox.dungeons.unleashed;

import com.xeloklox.dungeons.unleashed.items.*;
import com.xeloklox.dungeons.unleashed.items.hooks.*;
import com.xeloklox.dungeons.unleashed.utils.*;
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
                )
            )
        );
        //endregion
        //region HOOKS
        ItemEntityWrapper.addHookSelector((item)->{
            if(item.getStack().isOf(UNSTABLE_ENDER_PEARL.get())){
                return new UnstableExplosiveItemHook();
            }
            return null;
        });
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



