package com.xeloklox.dungeons.unleashed;

import com.xeloklox.dungeons.unleashed.gen.*;
import com.xeloklox.dungeons.unleashed.gen.RecipeJson.*;

public class ModRecipes{
    public static final RecipeJson unstable_ender_pearl= (new ShapedRecipeJson("unstable_ender_pearl"))
        .setKeys("minecraft:diamond -> D" , "minecraft:ender_pearl -> E" , "minecraft:glowstone_dust -> G")
        .setPattern(" D ",
                    "GEG",
                    " D "
        )
        .setResult(ModItems.UNSTABLE_ENDER_PEARL.getJSONID(), 1);
    public static final RecipeJson leyden_jar= (new ShapedRecipeJson("leyden_jar"))
        .setKeys("minecraft:lightning_rod -> L" , "minecraft:copper_ingot -> C" , "minecraft:glass -> G")
        .setPattern(" L ",
                    "GCG",
                    " G "
        )
        .setResult(ModBlocks.LEYDEN_JAR.blockitem.getJSONID(), 2);
    public static final RecipeJson infuser= (new ShapedRecipeJson("infuser"))
            .setKeys("minecraft:end_stone -> L" , "minecraft:copper_ingot -> C" , "minecraft:copper_block -> B" ,"minecraft:ender_pearl -> E")
            .setPattern("CLC",
                        "EBE",
                        "LEL"
            )
            .setResult(ModBlocks.LEYDEN_JAR.blockitem.getJSONID(), 2);
}
