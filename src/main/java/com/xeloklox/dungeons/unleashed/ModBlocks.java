package com.xeloklox.dungeons.unleashed;

import com.xeloklox.dungeons.unleashed.blocks.*;
import com.xeloklox.dungeons.unleashed.gen.*;
import com.xeloklox.dungeons.unleashed.gen.BlockStateBuilder.*;
import com.xeloklox.dungeons.unleashed.gen.LootTableJson.*;
import com.xeloklox.dungeons.unleashed.gen.LootTableJson.LootPool.LootPoolEntry.*;
import com.xeloklox.dungeons.unleashed.utils.*;
import net.fabricmc.fabric.api.item.v1.*;
import net.fabricmc.fabric.api.tool.attribute.v1.*;
import net.minecraft.block.*;
import net.minecraft.item.*;
import net.minecraft.sound.*;

import static com.xeloklox.dungeons.unleashed.DungeonsUnleashed.MODID;
import static com.xeloklox.dungeons.unleashed.gen.LootTableJson.LootPool.*;
import static com.xeloklox.dungeons.unleashed.gen.LootTableJson.LootPool.LootPoolEntry.LootPoolEntryType.*;

public class ModBlocks{
    public static final RegisteredBlock END_SOIL,ENDERSEED_SOIL,END_GRASS;

    static {

        LootPoolCondition silktouch = (matches_tool(filter->filter.setEnchantments("minecraft:silk_touch , [1,-]")));
        //region BLOCKS
        final String END_SOIL_model = BlockModel.allSidesSame("end_soil","block/end_soil");
        END_SOIL = new RegisteredBlock("end_soil",
        Globals.bootQuery(()->
                new BasicBlock(Material.SOIL, settings->
                            settings
                            .breakByHand(true)
                            .breakByTool(FabricToolTags.SHOVELS)
                            .sounds(BlockSoundGroup.SOUL_SOIL)
                            .hardness(0.5f)
                            .resistance(0.6f)

                )
            ),
        BlockStateBuilder.create().noState(randomRotationVariants(END_SOIL_model))
        );


        final String ENDERSEED_SOIL_model[] = {
            BlockModel.allSidesSame("enderseed_soil","block/enderseed_soil1"),
            BlockModel.allSidesSame("enderseed_soil2","block/enderseed_soil2"),
            BlockModel.allSidesSame("enderseed_soil3","block/enderseed_soil3"),
            BlockModel.allSidesSame("enderseed_soil4","block/enderseed_soil4")
        };

        ENDERSEED_SOIL = new RegisteredBlock("enderseed_soil",
        Globals.bootQuery(()->
                new BasicBlock(Material.SOIL, settings->
                    settings
                    .breakByHand(true)
                    .breakByTool(FabricToolTags.HOES)
                    .sounds(BlockSoundGroup.SOUL_SOIL)
                    .hardness(0.7f)
                    .resistance(0.8f)
                )
            ),
            true,
            ModItems.getSettings((s)->s.group(ItemGroup.BUILDING_BLOCKS)),
            BlockStateBuilder.create().noState(
                variants->variants
                .addModel(model->model.setModel(ENDERSEED_SOIL_model[0]))
                .addModel(model->model.setModel(ENDERSEED_SOIL_model[1]))
                .addModel(model->model.setModel(ENDERSEED_SOIL_model[2]))
                .addModel(model->model.setModel(ENDERSEED_SOIL_model[3]))
            ),
            lootTable->
            lootTable.addPool(pool-> //chance of dropping a ender pearl if mined by a hoe
                pool.addEntry(item, entry->
                    entry.setOutput("minecraft:ender_pearl")
                        .addCondition(droprate_with_enchantment(
                        "minecraft:fortune",
                        25 /* <- no enchant % */, 50, 60, 75, 90 // <- max lvl fortune %
                        )
                    )
                ).addCondition(survives_explosion())
                .addCondition(matches_tool(filter-> filter.setTag("fabric:hoes")))
                .addCondition(invert(matches_tool(
                    filter->
                    filter.setEnchantments("minecraft:silk_touch , [1,-]")
                )))
            ).addPool(pool-> //always drop the dirt if no silk touch
                pool.addEntry(item, entry->
                    entry.setOutput(MODID+":end_soil")
                ).addCondition(survives_explosion())
                .addCondition(invert(silktouch))
            ).addPool(pool-> //otherwise drop the original block
                pool.addEntry(item, entry->
                    entry.setOutput(MODID+":enderseed_soil")
                ).addCondition(survives_explosion())
                .addCondition(silktouch)
            )
        );

        final String END_GRASS_model = BlockModel.TopBottomSide("end_grass","block/end_grass_top","block/end_grass_side","block/end_soil");
        END_GRASS = new RegisteredBlock("end_grass",
            Globals.bootQuery(()->new ModGrassBlock(Material.SOLID_ORGANIC, END_SOIL.get(), 2,
            settings->
                settings.breakByHand(true)
                .breakByTool(FabricToolTags.SHOVELS)
                .sounds(BlockSoundGroup.SOUL_SOIL)
                .hardness(0.6f)
                .resistance(0.6f)
                .ticksRandomly())
            ),
            true,
            ModItems.getSettings((s)->s.group(ItemGroup.BUILDING_BLOCKS)),
            BlockStateBuilder.create().noState(randomRotationVariants(END_GRASS_model)),
        lootTable->
            lootTable.addPool(pool->
                pool.addEntry(item,entry->
                    entry.setOutput(MODID+":end_soil")
                    .addCondition(invert(silktouch))
                ).addEntry(item,entry->
                    entry.setOutput(MODID+":end_grass")
                    .addCondition(silktouch)
                )
            )
        );
        //end region

    }


    static Func<ModelVariantList, ModelVariantList> randomRotationVariants(String modelStr){
        return
        //rotated randomly when placed like dirt
        variants->variants
        .addModel(model->model.setModel(modelStr).setY(0))
        .addModel(model->model.setModel(modelStr).setY(90))
        .addModel(model->model.setModel(modelStr).setY(180))
        .addModel(model->model.setModel(modelStr).setY(270));
    }
}
