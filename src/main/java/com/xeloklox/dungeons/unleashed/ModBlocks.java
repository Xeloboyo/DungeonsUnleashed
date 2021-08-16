package com.xeloklox.dungeons.unleashed;

import com.xeloklox.dungeons.unleashed.blocks.*;
import com.xeloklox.dungeons.unleashed.gen.*;
import com.xeloklox.dungeons.unleashed.gen.BlockStateBuilder.*;
import com.xeloklox.dungeons.unleashed.gen.ItemJsonModel.*;
import com.xeloklox.dungeons.unleashed.utils.*;
import com.xeloklox.dungeons.unleashed.utils.RegisteredBlock.*;
import net.fabricmc.fabric.api.tool.attribute.v1.*;
import net.minecraft.block.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.sound.*;

import static com.xeloklox.dungeons.unleashed.DungeonsUnleashed.MODID;
import static com.xeloklox.dungeons.unleashed.blocks.LeydenJarBlock.MAX_CHARGE;
import static com.xeloklox.dungeons.unleashed.gen.LootTableJson.LootPool.*;
import static com.xeloklox.dungeons.unleashed.gen.LootTableJson.LootPool.LootPoolEntry.LootPoolEntryType.*;

public class ModBlocks{
    public static final RegisteredBlock END_SOIL,ENDERSEED_SOIL,END_GRASS,LEYDEN_JAR,INFUSER;

    static {
        LootPoolCondition silktouch = (matches_tool(filter->filter.setEnchantments("minecraft:silk_touch , [1,-]")));
        //region BLOCKS
        //A simple inert block
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

        //---------------------------------------------------------------------
        //A more complex block with 4 variants and a more advanced ore-like loot table.

        final String[] ENDERSEED_SOIL_model = {
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
        RenderLayerOptions.NORMAL,
        (id,item)-> new RegisteredItem(id, item,model->model),
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
                .addCondition(invert(silktouch))
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


        //---------------------------------------------------------------------
        //A block with custom behaviour and a different kind of model.

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
        RenderLayerOptions.NORMAL,
        (id,item)-> new RegisteredItem(id, item,model->model),
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

        //---------------------------------------------------------------------
        //A multipart complex block with custom behaviour and 5 states, where items persist block state

        //generate the block state models and the block state
        final String LEYDEN_JAR_model = "custom/leyden_jar";
        final String[] LEYDEN_JAR_ROD_model = new String[5];
        BlockStateBuilder LEYDEN_JAR_STATE = BlockStateBuilder.createMultipart() // <--multipart !!!!
                                        .addPart(model->model.setModel(v->v.setModel(LEYDEN_JAR_model)));

        for(int i =0;i<LEYDEN_JAR_ROD_model.length;i++){
            int finalI = i;
            LEYDEN_JAR_ROD_model[i] =  BlockModel.customTemplate("custom/leyden_jar_rod","leyden_jar_rod"+i,"block/custom/copper_rod"+i);
            //have the rod change based on the state...
            LEYDEN_JAR_STATE.addPart(model->
                model.setModel(v->v.setModel(LEYDEN_JAR_ROD_model[finalI]))
                     .addConditions(cond->cond.set(LeydenJarBlock.CHARGE,finalI))
            );
        }

        //create the block
        LEYDEN_JAR = new RegisteredBlock("leyden_jar",
            Globals.bootQuery(()->new LeydenJarBlock(Material.GLASS,
                settings->
                    settings.breakByHand(true)
                    .breakByTool(FabricToolTags.PICKAXES)
                    .sounds(BlockSoundGroup.GLASS)
                    .hardness(0.3f)
                    .resistance(0.3f)
                    .nonOpaque())
                ),
            RenderLayerOptions.CUTOUT,
            (id,bitem)-> new RegisteredItem(id, bitem,model->{

                    model.setModelParent(ModelParent.ITEM_GENERATED).setTextureLayers("item/leyden_jar");
                    for(int i = 1;i<=MAX_CHARGE;i++){
                        int finalI = i;
                        model.addOverride(override-> override.setModel("item/leyden_jar"+(finalI+1)).addModelPredicate("charge", finalI /(float)MAX_CHARGE));
                    }
                    return model;
                },
            item->item.addPredicate("charge",(itemstack, world, entity, seed) ->
                    {
                        NbtCompound blockEntityTag = itemstack.getOrCreateSubNbt("BlockStateTag");
                        if(blockEntityTag==null){return 0;}
                        return Strings.parseInt(blockEntityTag.getString(LeydenJarBlock.CHARGE.getName())) / (float)MAX_CHARGE;
                    }
                )
            ),
            ModItems.getSettings((s)->s.group(ItemGroup.BUILDING_BLOCKS).maxCount(8)),
            LEYDEN_JAR_STATE,
            lootTable->
            lootTable.addPool(pool->
                pool.addEntry(item,entry->
                    entry.setOutput(MODID+":leyden_jar")
                         .addFunction(copy_block_state(MODID+":leyden_jar",LeydenJarBlock.CHARGE))
                )
            )
        );

        //---------------------------------------------------------------------
        //A multipart complex block with custom behaviour and 32 different states, with a inventory and block entity
        final String INFUSER_model = "custom/infuser";
        INFUSER = new RegisteredBlock("infuser",
            Globals.bootQuery(()-> new InfuserBlock(Material.STONE,
            settings->
                    settings.breakByHand(false)
                    .breakByTool(FabricToolTags.PICKAXES)
                    .sounds(BlockSoundGroup.STONE)
                    .hardness(1.5f)
                    .resistance(7f))),
            RenderLayerOptions.NORMAL,
            (id,item)-> new RegisteredItem(id, item,model->model),
            ModItems.getSettings((s)->s.group(ItemGroup.DECORATIONS)),
             BlockStateBuilder.create().noState(oneVariant(BlockModel.customTemplate(INFUSER_model,"infuser","block/custom/infuser"))),
            lootTable->
                lootTable.addPool(pool->
                    pool.addEntry(item,entry->
                        entry.setOutput(MODID+":infuser")
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
    static Func<ModelVariantList, ModelVariantList> oneVariant(String modelStr){
        return
        //rotated randomly when placed like dirt
        variants->variants
        .addModel(model->model.setModel(modelStr));
    }
}