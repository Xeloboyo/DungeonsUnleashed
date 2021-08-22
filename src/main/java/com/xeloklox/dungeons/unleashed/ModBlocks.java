package com.xeloklox.dungeons.unleashed;

import com.xeloklox.dungeons.unleashed.blockentity.*;
import com.xeloklox.dungeons.unleashed.blockentity.renderer.*;
import com.xeloklox.dungeons.unleashed.blockentity.screens.*;
import com.xeloklox.dungeons.unleashed.blocks.*;
import com.xeloklox.dungeons.unleashed.gen.*;
import com.xeloklox.dungeons.unleashed.gen.BlockStateBuilder.*;
import com.xeloklox.dungeons.unleashed.gen.ItemJsonModel.*;
import com.xeloklox.dungeons.unleashed.utils.*;
import com.xeloklox.dungeons.unleashed.utils.RegisteredBlock.*;
import com.xeloklox.dungeons.unleashed.utils.lambda.*;
import com.xeloklox.dungeons.unleashed.utils.lambda.Cons.*;
import net.fabricmc.fabric.api.object.builder.v1.block.*;
import net.fabricmc.fabric.api.screenhandler.v1.*;
import net.fabricmc.fabric.api.tool.attribute.v1.*;
import net.minecraft.block.*;
import net.minecraft.client.model.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.sound.*;
import net.minecraft.state.property.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.Direction.*;

import static com.xeloklox.dungeons.unleashed.DungeonsUnleashed.MODID;
import static com.xeloklox.dungeons.unleashed.blocks.LeydenJarBlock.MAX_CHARGE;
import static com.xeloklox.dungeons.unleashed.gen.LootTableJson.LootPool.*;
import static com.xeloklox.dungeons.unleashed.gen.LootTableJson.LootPool.LootPoolEntry.LootPoolEntryType.*;

public class ModBlocks{
    public static final RegisteredBlock
    END_SOIL = new RegisteredBlock("end_soil"),
    END_WOOD_PLANKS = new RegisteredBlock("end_wood_planks"),
    ENDERSEED_SOIL = new RegisteredBlock("enderseed_soil"),
    PATCHY_END_GRASS = new RegisteredBlock("end_grass_patchy"),
    END_GRASS = new RegisteredBlock("end_grass"),
    LEYDEN_JAR = new RegisteredBlock("leyden_jar"),
    INFUSER = new RegisteredBlock("infuser"),
    END_WOOD = new RegisteredBlock("end_wood"),
    VOID_ROCK = new RegisteredBlock("void_rock"),
    VOID_ROCK_SOILED = new RegisteredBlock("void_rock_soiled"),
    VOID_SHALE = new RegisteredBlock("void_shale"),
    VOID_ROCK_TILE = new RegisteredBlock("void_rock_tile"),
    VOID_ROCK_SMOOTH = new RegisteredBlock("void_rock_smooth"),
    END_STONE_PILLAR = new RegisteredBlock("end_stone_pillar"),
    END_SCALES = new RegisteredBlock("end_scales"),
    END_LEAVES = new RegisteredBlock("end_leaves"),
    BEDROCK_PILLAR = new RegisteredBlock("bedrock_pillar");


    public static final RegisteredBlockEntity<InfuserEntity> INFUSER_ENTITY;
    public static final RegisteredBlockEntityRenderer<InfuserEntity> INFUSER_ENTITY_RENDERER;
    public static final RegisteredScreenHandler<InfuserScreenHandler,InfuserScreen> INFUSER_SCREEN;

    static {
        LootPoolCondition silktouch = (matches_tool(filter->filter.setEnchantments("minecraft:silk_touch , [1,-]")));

        Func<FabricBlockSettings,FabricBlockSettings> dirtSettings =
                settings -> settings
                    .breakByHand(true)
                    .breakByTool(FabricToolTags.SHOVELS)
                    .sounds(BlockSoundGroup.SOUL_SOIL)
                    .hardness(0.5f)
                    .resistance(0.6f);

        Func<FabricBlockSettings,FabricBlockSettings> woodSettings =
                settings -> settings
                    .breakByHand(true)
                    .breakByTool(FabricToolTags.AXES)
                    .sounds(BlockSoundGroup.WOOD)
                    .hardness(2f)
                    .resistance(3f);

        //settings overloading settings?
        Func<FabricBlockSettings,FabricBlockSettings> grassSettings =
                    settings ->
                    dirtSettings.get(settings)
                    .sounds(BlockSoundGroup.GRASS)
                    .hardness(0.6f)
                    .ticksRandomly();

        Func<FabricBlockSettings,FabricBlockSettings> logSettings =
                settings ->
                        woodSettings.get(settings)
                        .hardness(2f)
                        .resistance(2f);

        Func<FabricBlockSettings,FabricBlockSettings> stoneSettings =
                settings -> settings
                        .breakByHand(false)
                        .breakByTool(FabricToolTags.PICKAXES)
                        .sounds(BlockSoundGroup.STONE)
                        .hardness(1.5f)
                        .resistance(6f);
        Func<FabricBlockSettings,FabricBlockSettings> bedrockSettings =
                        settings ->
                        stoneSettings.get(settings)
                        .hardness(-1)
                        .resistance(36000000);

        Func<FabricBlockSettings,FabricBlockSettings> leafSettings =
                settings -> settings
                        .breakByHand(true)
                        .breakByTool(FabricToolTags.SHEARS)
                        .sounds(BlockSoundGroup.GRASS)
                        .hardness(0.2f)
                        .resistance(0.2f)
                        .nonOpaque();

        //yes im very lazy
        Cons2<RegisteredBlock,Prov<BasicBlock>> genericBlockBuilder = (block ,blk) -> {
            final String model = BlockModelPresetBuilder.allSidesSame(block.id,"block/"+block.id);
            block.setBlock(Globals.bootQuery(blk));
            block.setBlockState(BlockStateBuilder.create().noState(oneVariant(model)));

        };
        //region BLOCKS

        /* Simple inert blocks*/
        genericBlockBuilder.get(VOID_ROCK,() -> new BasicBlock(Material.STONE, stoneSettings)); VOID_ROCK.finalise();

        genericBlockBuilder.get(VOID_SHALE,() -> new BasicBlock(Material.STONE, stoneSettings));
        VOID_SHALE.dropsUnlessSilktouched(VOID_ROCK.getJSONID());
        VOID_SHALE.finalise();


        genericBlockBuilder.get(VOID_ROCK_TILE,() -> new BasicBlock(Material.STONE, stoneSettings));
        VOID_ROCK_TILE.setName("Void Stone Tile");
        VOID_ROCK_TILE.finalise();

        genericBlockBuilder.get(VOID_ROCK_SMOOTH,() -> new BasicBlock(Material.STONE, stoneSettings));
        VOID_ROCK_SMOOTH.setName("Smooth Void Stone");
        VOID_ROCK_SMOOTH.dropsUnlessSilktouched(VOID_ROCK.getJSONID());
        VOID_ROCK_SMOOTH.finalise();

        genericBlockBuilder.get(VOID_ROCK_SOILED,() -> new BasicBlock(Material.STONE, stoneSettings));
        VOID_ROCK_SOILED.dropsUnlessSilktouched(VOID_ROCK.getJSONID());
        VOID_ROCK_SOILED.finalise();

        genericBlockBuilder.get(END_SCALES,() -> new BasicBlock(Material.STONE, stoneSettings));
        END_SCALES.finalise();

        final String END_SOIL_model = BlockModelPresetBuilder.allSidesSame("end_soil","block/end_soil");
        END_SOIL.setBlock(Globals.bootQuery(() -> new BasicBlock(Material.SOIL, dirtSettings)));
        END_SOIL.setBlockState(BlockStateBuilder.create().noState(randomHorizontalRotationVariants(END_SOIL_model)));
        END_SOIL.finalise();

        final String END_WOOD_PLANKS_model = BlockModelPresetBuilder.allSidesSame("end_wood_planks","block/end_wood_planks");
        END_WOOD_PLANKS.setBlock(Globals.bootQuery(() -> new BasicBlock(Material.WOOD, woodSettings )));
        END_WOOD_PLANKS.setBlockState(BlockStateBuilder.create().noState(randomRotationVariants(END_WOOD_PLANKS_model)));
        END_WOOD_PLANKS.setFlammablility(5,5);
        END_WOOD_PLANKS.finalise();

        final String END_WOOD_model = BlockModelPresetBuilder.TopBottomSide("end_wood","block/end_wood_top","block/end_wood_side","block/end_wood_top");
        BasicBlock.selectedPlacementConfig=BasicBlock.PILLAR_PLACEMENT;
        END_WOOD.setBlock(Globals.bootQuery(() -> new BasicBlock(Material.WOOD, logSettings)));
        END_WOOD.setBlockState(axisStates(END_WOOD_model));
        END_WOOD.setFlammablility(5,5);
        END_WOOD.finalise();

        final String END_ROCK_PILLAR_model = BlockModelPresetBuilder.TopBottomSide("end_rock_pillar","block/end_rock_pillar_top","block/end_rock_pillar_side","block/end_rock_pillar_top");
        BasicBlock.selectedPlacementConfig=BasicBlock.PILLAR_PLACEMENT;
        END_STONE_PILLAR.setBlock(Globals.bootQuery(() -> new BasicBlock(Material.STONE, stoneSettings)));
        END_STONE_PILLAR.setBlockState(axisStates(END_ROCK_PILLAR_model));
        END_STONE_PILLAR.setName("End stone pillar");
        END_STONE_PILLAR.finalise();

        final String END_LEAVES_model = BlockModelPresetBuilder.allSidesSame("end_leaves","block/end_leaves");
        END_LEAVES.setBlock(Globals.bootQuery(() -> new BasicBlock(Material.LEAVES, leafSettings)));
        END_LEAVES.setBlockState(BlockStateBuilder.create().noState(oneVariant(END_LEAVES_model)));
        END_LEAVES.setRenderlayer(RenderLayerOptions.CUTOUT);
        END_LEAVES.setFlammablility(30,60);
        END_LEAVES.setDrops(lootTable->
            lootTable.addPool(pool->
                pool.addEntry(item, entry->
                    entry.setOutput("minecraft:stick")
                    .condition(droprate(10)) //10% chance
                    .condition(invert(silktouch))
                ).addEntry(item, entry->
                    entry.setOutput(END_LEAVES.getJSONID())
                    .condition(anyOf(
                        silktouch,
                        matches_tool(tool->tool.setItems("minecraft:shears"))
                    ))
                )
            )
        );
        END_LEAVES.finalise(); //todo -> LEAVES BLOCK NEEDS BLOCK STATES

        final String BEDROCK_PILLAR_model = BlockModelPresetBuilder.TopBottomSide("bedrock_pillar","block/bedrock_pillar_top","block/bedrock_pillar_side","block/bedrock_pillar_top");
        BasicBlock.selectedPlacementConfig=BasicBlock.PILLAR_PLACEMENT;
        BEDROCK_PILLAR.setBlock(Globals.bootQuery(() -> new BasicBlock(Material.STONE, bedrockSettings)));
        BEDROCK_PILLAR.setBlockState(axisStates(BEDROCK_PILLAR_model));
        BEDROCK_PILLAR.finalise();

        /*
        ---------------------------------------------------------------------
        A more complex block with 4 variants and a more advanced ore-like loot table.
        */

        final String[] ENDERSEED_SOIL_models = {
            BlockModelPresetBuilder.allSidesSame("enderseed_soil","block/enderseed_soil1"),
            BlockModelPresetBuilder.allSidesSame("enderseed_soil2","block/enderseed_soil2"),
            BlockModelPresetBuilder.allSidesSame("enderseed_soil3","block/enderseed_soil3"),
            BlockModelPresetBuilder.allSidesSame("enderseed_soil4","block/enderseed_soil4")
        };

        ENDERSEED_SOIL.setBlock(Globals.bootQuery(() ->
            new BasicBlock(Material.SOIL, settings ->
                settings
                .breakByHand(true)
                .breakByTool(FabricToolTags.HOES)
                .sounds(BlockSoundGroup.SOUL_SOIL)
                .hardness(0.7f)
                .resistance(0.8f)
            )
        ));
        ENDERSEED_SOIL.setBlockState(BlockStateBuilder.create().noState(modelVariants(ENDERSEED_SOIL_models)));
        ENDERSEED_SOIL.setDrops(lootTable ->
            lootTable.addPool(pool -> //chance of dropping a ender pearl if mined by a hoe
                pool.addEntry(item, entry ->
                    entry.setOutput("minecraft:ender_pearl")
                        .condition(droprate_with_enchantment(
                        "minecraft:fortune",
                        25 /* <- no enchant % */, 50, 60, 75, 90 // <- max lvl fortune %
                        ))
                ).condition(survives_explosion())
                .condition(matches_tool(filter -> filter.setTag("fabric:hoes")))
                .condition(invert(silktouch))
            ).addPool(pool -> //always drop the dirt if no silk touch
                    pool.addEntry(item, entry ->
                        entry.setOutput(MODID + ":end_soil")
                    ).condition(survives_explosion())
                    .condition(invert(silktouch))
            ).addPool(pool -> //otherwise drop the original block
                    pool.addEntry(item, entry ->
                        entry.setOutput(MODID + ":enderseed_soil")
                    ).condition(survives_explosion())
                    .condition(silktouch)
            )
        );
        ENDERSEED_SOIL.finalise();
        //---------------------------------------------------------------------
        //A block with custom behaviour and a different kind of model.
        final RegisteredBlock[] grassProgression = {END_SOIL,PATCHY_END_GRASS,END_GRASS};

        final String PATCHY_END_GRASS_model = BlockModelPresetBuilder.TopBottomSide("end_grass_patchy","block/end_grass_patchy","block/end_grass_patchy_side","block/end_soil");
        PATCHY_END_GRASS.setBlock(Globals.bootQuery(() -> new ModGrassBlock(Material.SOLID_ORGANIC, grassProgression, 0, 4,grassSettings)));
        PATCHY_END_GRASS.setBlockState(BlockStateBuilder.create().noState(randomHorizontalRotationVariants(PATCHY_END_GRASS_model)));
        PATCHY_END_GRASS.setName("Patchy end grass");
        PATCHY_END_GRASS.dropsUnlessSilktouched(END_SOIL.getJSONID());
        PATCHY_END_GRASS.finalise();



        final String END_GRASS_model = BlockModelPresetBuilder.TopBottomSide("end_grass","block/end_grass_top","block/end_grass_side","block/end_soil");
        END_GRASS.setBlock(Globals.bootQuery(() -> new ModGrassBlock(Material.SOLID_ORGANIC, grassProgression, 4, 8,grassSettings)));
        END_GRASS.setBlockState(BlockStateBuilder.create().noState(randomHorizontalRotationVariants(END_GRASS_model)));
        END_GRASS.dropsUnlessSilktouched(END_SOIL.getJSONID());
        END_GRASS.finalise();

        //---------------------------------------------------------------------
        //A multipart complex block with custom behaviour and 5 states, where items persist block state

        //generate the block state models and the block state
        final String LEYDEN_JAR_model = "custom/leyden_jar";
        final String[] LEYDEN_JAR_ROD_model = new String[5];
        BlockStateBuilder LEYDEN_JAR_STATE = BlockStateBuilder.createMultipart() // <--multipart !!!!
                                        .addPart(model->model.setModel(v->v.setModel(LEYDEN_JAR_model)));

        for(int i =0;i<LEYDEN_JAR_ROD_model.length;i++){
            int finalI = i;
            LEYDEN_JAR_ROD_model[i] =  BlockModelPresetBuilder.customTemplate("block/custom/leyden_jar_rod","leyden_jar_rod"+i,"block/custom/copper_rod"+i);
            //have the rod change based on the state...
            LEYDEN_JAR_STATE.addPart(model->
                model.setModel(v->v.setModel(LEYDEN_JAR_ROD_model[finalI]))
                     .addConditions(cond->cond.set(LeydenJarBlock.CHARGE,finalI))
            );
        }

        //create the block
        LEYDEN_JAR.setBlock(Globals.bootQuery(() -> new LeydenJarBlock(Material.GLASS,
        settings ->
            settings.breakByHand(true)
            .breakByTool(FabricToolTags.PICKAXES)
            .sounds(BlockSoundGroup.GLASS)
            .hardness(0.3f)
            .resistance(0.3f)
            .nonOpaque())
        ));
        LEYDEN_JAR.setBlockState(LEYDEN_JAR_STATE);
        LEYDEN_JAR.setRenderlayer(RenderLayerOptions.CUTOUT);
        LEYDEN_JAR.setSettings(ModItems.getSettings((s) -> s.group(ItemGroup.BUILDING_BLOCKS).maxCount(8)));
        LEYDEN_JAR.setBlockitem((id, bitem) ->
            new RegisteredItem(id, bitem,
                model -> {
                    model.setModelParent(ModelParent.ITEM_GENERATED).setTextureLayers("item/leyden_jar");
                    for(int i = 1; i <= MAX_CHARGE; i++){
                        int finalI = i;
                        model.addOverride(override -> override.setModel("item/leyden_jar" + (finalI + 1)).addModelPredicate("charge", finalI / (float)MAX_CHARGE));
                    }
                    return model;
                },
                item -> item.addPredicate("charge", (itemstack, world, entity, seed) -> {
                    NbtCompound blockEntityTag = itemstack.getOrCreateSubNbt("BlockStateTag");
                    if(blockEntityTag == null){
                        return 0;
                    }
                    return Strings.parseInt(blockEntityTag.getString(LeydenJarBlock.CHARGE.getName())) / (float)MAX_CHARGE;
                })
            )
        );
        LEYDEN_JAR.setDrops(lootTable ->
            lootTable.addPool(pool ->
                pool.addEntry(item, entry ->
                    entry.setOutput(MODID + ":leyden_jar")
                         .addFunction(copy_block_state(MODID + ":leyden_jar", LeydenJarBlock.CHARGE))
                )
            )
        );
        LEYDEN_JAR.finalise();

        //---------------------------------------------------------------------
        //A complex block with custom behaviour, a inventory, ui and block entity
        final String INFUSER_model = BlockModelPresetBuilder.customTemplate("block/custom/infuser", "infuser", "block/custom/infuser");
        BasicBlock.selectedPlacementConfig=BasicBlock.HORIZONTAL_FACING_PLAYER_PLACEMENT;
        INFUSER.setBlock(Globals.bootQuery(() -> new InfuserBlock(Material.STONE,
            settings ->
            settings.breakByHand(false)
            .breakByTool(FabricToolTags.PICKAXES)
            .sounds(BlockSoundGroup.STONE)
            .hardness(1.5f)
            .resistance(7f)
        )));
        INFUSER.setBlockState(BlockStateBuilder.create()
            .addStateVariant(HORIZONTAL_FACING(), Direction.NORTH,variant->variant.addModel(INFUSER_model,0))
            .addStateVariant(HORIZONTAL_FACING(), Direction.EAST,variant->variant.addModel(INFUSER_model,90))
            .addStateVariant(HORIZONTAL_FACING(), Direction.SOUTH,variant->variant.addModel(INFUSER_model,180))
            .addStateVariant(HORIZONTAL_FACING(), Direction.WEST,variant->variant.addModel(INFUSER_model,270))
        );
        INFUSER.setSettings(ModItems.getSettings((s) -> s.group(ItemGroup.DECORATIONS)));
        INFUSER.finalise();
        //end region
        //region ENTITIES
        INFUSER_ENTITY = new RegisteredBlockEntity<>("infuser_entity", InfuserEntity::new, INFUSER);
        INFUSER_ENTITY_RENDERER = new RegisteredBlockEntityRenderer<>(()->INFUSER_ENTITY.get(), InfuserRenderer::new);
        INFUSER_SCREEN =
            Globals.bootQuery( ()->
                new RegisteredScreenHandler<>(
                    "infuserscreen",
                    ScreenHandlerRegistry.registerSimple(INFUSER.getIdentifier(), InfuserScreenHandler::new),
                    InfuserScreen::new
                )
            );
         Globals.bootRun(()->{try{
             Class.forName(InfuserRenderer.class.getName());
         }catch(ClassNotFoundException ignored){}});
        //end region

        new JsonModelProvider(); // yes

    }

    // convenience stuff:

    static DirectionProperty HORIZONTAL_FACING(){
        return Globals.bootQuery(()->Properties.HORIZONTAL_FACING,  DirectionProperty.of("facing"));
    }
    static DirectionProperty FACING(){
            return Globals.bootQuery(()->Properties.FACING,  DirectionProperty.of("facing"));
        }
    static EnumProperty<Direction.Axis> AXIS(){
        return Globals.bootQuery(()->Properties.AXIS,  EnumProperty.of("axis",Direction.Axis.class));
    }

    static Func<ModelVariantList, ModelVariantList> randomHorizontalRotationVariants(String modelStr){
        return
        //rotated randomly when placed like dirt
        variants->variants
        .addModel(model->model.setModel(modelStr).setY(0))
        .addModel(model->model.setModel(modelStr).setY(90))
        .addModel(model->model.setModel(modelStr).setY(180))
        .addModel(model->model.setModel(modelStr).setY(270));
    }
    static Func<ModelVariantList, ModelVariantList> randomRotationVariants(String modelStr){
        return
        variants->variants
        .addModel(model->model.setModel(modelStr).setY(0))
        .addModel(model->model.setModel(modelStr).setX(90).setY(90).setZ(90))
        .addModel(model->model.setModel(modelStr).setX(180).setY(180).setZ(180))
        .addModel(model->model.setModel(modelStr).setX(270).setY(270).setZ(270));
    }

    static BlockStateBuilder directionalStates(String modelStr){
        return
            BlockStateBuilder.create()
            .addStateVariant(FACING(), Direction.UP,variant->variant.addModel(modelStr,0))
            .addStateVariant(FACING(), Direction.DOWN,variant->variant.addModel(modelStr,180,0))
            .addStateVariant(FACING(), Direction.NORTH,variant->variant.addModel(modelStr,90,0))
            .addStateVariant(FACING(), Direction.EAST,variant->variant.addModel(modelStr,90,90))
            .addStateVariant(FACING(), Direction.SOUTH,variant->variant.addModel(modelStr,90,180))
            .addStateVariant(FACING(), Direction.WEST,variant->variant.addModel(modelStr,90,270));
    }

    static BlockStateBuilder axisStates(String modelStr){
        return
            BlockStateBuilder.create()
            .addStateVariant(AXIS(), Axis.Y,variant->variant.addModel(modelStr,0))
            .addStateVariant(AXIS(), Axis.X,variant->variant.addModel(modelStr,90,90))
            .addStateVariant(AXIS(), Axis.Z,variant->variant.addModel(modelStr,90,0));
    }

    static Func<ModelVariantList, ModelVariantList> oneVariant(String modelStr){
        return
        //rotated randomly when placed like dirt
        variants->variants
        .addModel(model->model.setModel(modelStr));
    }
    static Func<ModelVariantList, ModelVariantList> modelVariants(String... modelStr){
        return
        variants-> {
            for(String s:modelStr){
                variants.addModel(model -> model.setModel(s));
            }
            return variants;
        };
    }
}
