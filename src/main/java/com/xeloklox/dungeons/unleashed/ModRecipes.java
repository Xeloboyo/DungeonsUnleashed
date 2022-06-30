package com.xeloklox.dungeons.unleashed;

import com.xeloklox.dungeons.unleashed.gen.*;
import com.xeloklox.dungeons.unleashed.gen.RecipeJson.*;
import com.xeloklox.dungeons.unleashed.utils.item.*;
import com.xeloklox.dungeons.unleashed.utils.item.ShapedRecipeGenerator.*;

public class ModRecipes{
    public static final RecipeJson unstable_ender_pearl= (new ShapedRecipeJson("unstable_ender_pearl"))
        .setKeys("minecraft:diamond -> D" , "minecraft:ender_pearl -> E" , "minecraft:glowstone_dust -> G")
        .setPattern(" D ",
                    "GEG",
                    " D "
        )
        .setResult(ModItems.UNSTABLE_ENDER_PEARL, 1);
    public static final RecipeJson leyden_jar= (new ShapedRecipeJson("leyden_jar"))
        .setKeys("minecraft:lightning_rod -> L" , "minecraft:copper_ingot -> C" , "minecraft:glass -> G")
        .setPattern(" L ",
                    "GCG",
                    " G "
        )
        .setResult(ModBlocks.LEYDEN_JAR, 2);
    public static final RecipeJson infuser=
        ShapedRecipeGenerator.create()
            .center_adjacent("minecraft:ender_pearl")
            .center("minecraft:copper_block")
            .corners("minecraft:end_stone")
            .slot("minecraft:copper_ingot", SlotPos.TOP_LEFT)
            .slot("minecraft:copper_ingot", SlotPos.TOP_RIGHT)
            .build("infuser")
            .setResult(ModBlocks.INFUSER, 1);

    public static final RecipeJson tablet=
        ShapedRecipeGenerator.create()
            .fill("minecraft:stone")
            .build("blank_tablet")
            .setResult(ModItems.BLANK_TABLET,1);
}
