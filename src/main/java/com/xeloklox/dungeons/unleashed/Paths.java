package com.xeloklox.dungeons.unleashed;

import static com.xeloklox.dungeons.unleashed.ModInitClientServer.MODID;

public class Paths{
    public static final String
        data                = "src/main/resources/data/",
        modData             = data+MODID+"/",
        lootTables          = modData+"loot_tables/",
        recipes             = modData+"recipes/",
        blockLootTables     = lootTables+"blocks/",
        modResource         = "src/main/resources/assets/"+MODID+"/",
        animations          = modResource+"animation/",
        texture             = modResource+"textures/",
        models              = modResource+"models/",
        itemModel           = modResource+"models/item/",
        itemTexture         = modResource+"textures/item/",
        blockModel          = modResource+"models/block/",
        blockTexture        = modResource+"models/item/",
        blockState          = modResource+"blockstates/",
        itemTexture_DEFAULT = modResource+"textures/item/default.png",
        blockTexture_DEFAULT= modResource+"textures/block/default.png";
}
