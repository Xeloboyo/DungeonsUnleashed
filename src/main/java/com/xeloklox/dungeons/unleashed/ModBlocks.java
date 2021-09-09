package com.xeloklox.dungeons.unleashed;

import com.xeloklox.dungeons.unleashed.blockentity.*;
import com.xeloklox.dungeons.unleashed.blockentity.renderer.*;
import com.xeloklox.dungeons.unleashed.blockentity.screens.*;
import com.xeloklox.dungeons.unleashed.blocks.*;
import com.xeloklox.dungeons.unleashed.blocks.graph.charge.*;
import com.xeloklox.dungeons.unleashed.gen.*;
import com.xeloklox.dungeons.unleashed.gen.BlockStateBuilder.*;
import com.xeloklox.dungeons.unleashed.gen.ItemJsonModel.*;
import com.xeloklox.dungeons.unleashed.gen.ModTag.*;
import com.xeloklox.dungeons.unleashed.utils.*;
import com.xeloklox.dungeons.unleashed.utils.block.*;
import com.xeloklox.dungeons.unleashed.utils.models.*;
import com.xeloklox.dungeons.unleashed.utils.models.ConnectedTextureBlockModel.*;
import com.xeloklox.dungeons.unleashed.utils.models.ModelProvider.*;
import com.xeloklox.dungeons.unleashed.utils.RegisteredBlock.*;
import com.xeloklox.dungeons.unleashed.utils.lambda.*;
import com.xeloklox.dungeons.unleashed.utils.lambda.Cons.*;
import net.fabricmc.fabric.api.object.builder.v1.block.*;
import net.fabricmc.fabric.api.screenhandler.v1.*;
import net.fabricmc.fabric.api.tool.attribute.v1.*;
import net.minecraft.block.*;
import net.minecraft.block.enums.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.sound.*;
import net.minecraft.util.math.*;

import static com.xeloklox.dungeons.unleashed.DungeonsUnleashed.MODID;
import static com.xeloklox.dungeons.unleashed.blocks.LeydenJarBlock.MAX_CHARGE;
import static com.xeloklox.dungeons.unleashed.gen.LootTableJson.LootPool.*;
import static com.xeloklox.dungeons.unleashed.gen.LootTableJson.LootPool.LootPoolEntry.LootPoolEntryType.*;
import static com.xeloklox.dungeons.unleashed.utils.block.BlockStatePresets.*;

public class ModBlocks{
    public static final RegisteredBlock
    END_SOIL = new RegisteredBlock("end_soil"),
    END_WOOD_PLANKS = new RegisteredBlock("end_wood_planks"),
    END_WOOD_PLANK_SLAB = new RegisteredBlock("end_wood_plank_slab"),
    END_WOOD_FENCE = new RegisteredBlock("end_wood_fence"),
    ENDERSEED_SOIL = new RegisteredBlock("enderseed_soil"),
    THUNDERSTONE_ORE = new RegisteredBlock("thunderstone_ore"),
    THUNDERSTONE_BLOCK = new RegisteredBlock("thunderstone_block"),
    PATCHY_END_GRASS = new RegisteredBlock("end_grass_patchy"),
    END_GRASS = new RegisteredBlock("end_grass"),
    END_WOOD = new RegisteredBlock("end_wood"),
    VOID_ROCK = new RegisteredBlock("void_rock"),
    VOID_ROCK_SOILED = new RegisteredBlock("void_rock_soiled"),
    VOID_SHALE = new RegisteredBlock("void_shale"),
    VOID_ROCK_TILE = new RegisteredBlock("void_rock_tile"),
    VOID_ROCK_TILE_SLAB = new RegisteredBlock("void_rock_tile_slab"),
    VOID_ROCK_TILE_STAIRS = new RegisteredBlock("void_rock_tile_stairs"),
    VOID_ROCK_SMOOTH = new RegisteredBlock("void_rock_smooth"),
    VOID_ROCK_SMOOTH_SLAB = new RegisteredBlock("void_rock_smooth_slab"),
    VOID_ROCK_SMOOTH_WALL = new RegisteredBlock("void_rock_smooth_wall"),
    VOID_ROCK_SMOOTH_STAIRS = new RegisteredBlock("void_rock_smooth_stairs"),
    BORDERED_END_STONE = new RegisteredBlock("bordered_end_stone"),
    END_STONE_PILLAR = new RegisteredBlock("end_stone_pillar"),
    END_SCALES = new RegisteredBlock("end_scales"),
    END_LEAVES = new RegisteredBlock("end_leaves"),
    BEDROCK_PILLAR = new RegisteredBlock("bedrock_pillar"),
    LEYDEN_JAR = new RegisteredBlock("leyden_jar"),
    INFUSER = new RegisteredBlock("infuser"),
    CHARGE_TRANSPOSER = new RegisteredBlock("charge_transposer"),
    CHARGE_CELL_PORT = new RegisteredBlock("charge_cell_port"),
    CHARGE_CELL_TANK = new RegisteredBlock("charge_cell_tank");


    public static final RegisteredBlockEntity<InfuserEntity> INFUSER_ENTITY;
    public static final RegisteredBlockEntity<ChargeStoragePortEntity> CHARGE_CELL_PORT_ENTITY;
    public static final RegisteredBlockEntity<ChargeStorageTankEntity> CHARGE_CELL_STORAGE_ENTITY;
    public static final RegisteredBlockEntity<ChargeTransposerEntity> CHARGE_TRANSPOSER_ENTITY;

    public static final RegisteredBlockEntityRenderer<InfuserEntity> INFUSER_ENTITY_RENDERER;
    public static final RegisteredBlockEntityRenderer<ChargeStoragePortEntity> CHARGE_CELL_PORT_ENTITY_RENDERER;
    public static final RegisteredBlockEntityRenderer<ChargeStorageTankEntity> CHARGE_CELL_TANK_ENTITY_RENDERER;
    public static final RegisteredBlockEntityRenderer<ChargeTransposerEntity> CHARGE_TRANSPOSER_ENTITY_RENDERER;

    public static final RegisteredScreenHandler<InfuserScreenHandler,InfuserScreen> INFUSER_SCREEN;
    public static final RegisteredScreenHandler<ChargeTransposerScreenHandler,ChargeTransposerScreen> CHARGE_TRANSPOSER_SCREEN;

    //tags go here for now
    static ModTag<Block> wallsTag = new ModTag<>(TagDomain.minecraft,"walls", TagCategory.blocks);
    static ModTag<Block> slabsTag = new ModTag<>(TagDomain.minecraft,"slabs", TagCategory.blocks);
    static ModTag<Block> stairsTag = new ModTag<>(TagDomain.minecraft,"stairs", TagCategory.blocks);
    static ModTag<Block> fencesTag = new ModTag<>(TagDomain.minecraft,"fences", TagCategory.blocks);
    static ModTag<Block> planksTag = new ModTag<>(TagDomain.minecraft,"planks", TagCategory.blocks);

    static {
        LootPoolCondition silktouch = (matches_tool(filter->filter.setEnchantments("minecraft:silk_touch , [1,-]")));
        //region SETTINGS
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
        //endregion
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
        makeSlabFrom(VOID_ROCK_TILE,VOID_ROCK_TILE_SLAB); VOID_ROCK_TILE_SLAB.finalise();
        makeStairsFrom(VOID_ROCK_TILE,VOID_ROCK_TILE_STAIRS); VOID_ROCK_TILE_STAIRS.finalise();

        genericBlockBuilder.get(VOID_ROCK_SMOOTH,() -> new BasicBlock(Material.STONE, stoneSettings));
        VOID_ROCK_SMOOTH.setName("Smooth Void Stone");
        VOID_ROCK_SMOOTH.dropsUnlessSilktouched(VOID_ROCK.getJSONID());
        VOID_ROCK_SMOOTH.finalise();
        makeWallsFrom(VOID_ROCK_SMOOTH,VOID_ROCK_SMOOTH_WALL);VOID_ROCK_SMOOTH_WALL.finalise();
        makeSlabFrom(VOID_ROCK_SMOOTH,VOID_ROCK_SMOOTH_SLAB);VOID_ROCK_SMOOTH_SLAB.finalise();
        makeStairsFrom(VOID_ROCK_SMOOTH,VOID_ROCK_SMOOTH_STAIRS);VOID_ROCK_SMOOTH_STAIRS.finalise();

        genericBlockBuilder.get(VOID_ROCK_SOILED,() -> new BasicBlock(Material.STONE, stoneSettings));
        VOID_ROCK_SOILED.dropsUnlessSilktouched(VOID_ROCK.getJSONID());
        VOID_ROCK_SOILED.finalise();

        genericBlockBuilder.get(BORDERED_END_STONE,()->new BasicBlock(Material.STONE, stoneSettings));
        ConnectedTextureBlockModel bordered_end_stoneCBTM = new ConnectedTextureBlockModel(Utils.getSprite("block/bordered_end_stone"),"bordered_end_stone",block -> block.getBlock().equals(BORDERED_END_STONE.get()));
        BORDERED_END_STONE.setBlockState(BlockStateBuilder.create().noState(oneVariant(BlockModelPresetBuilder.generated(bordered_end_stoneCBTM))));
        BORDERED_END_STONE.finalise();

        genericBlockBuilder.get(END_SCALES,() -> new BasicBlock(Material.STONE, stoneSettings));
        END_SCALES.finalise();

        final String END_SOIL_model = BlockModelPresetBuilder.allSidesSame(END_SOIL.id,"block/end_soil");
        END_SOIL.setBlock(Globals.bootQuery(() -> new BasicBlock(Material.SOIL, dirtSettings)));
        END_SOIL.setBlockState(BlockStateBuilder.create().noState(randomHorizontalRotationVariants(END_SOIL_model)));
        END_SOIL.finalise();

        final String END_WOOD_PLANKS_model = BlockModelPresetBuilder.allSidesSame(END_WOOD_PLANKS.id,"block/end_wood_planks");
        END_WOOD_PLANKS.setBlock(Globals.bootQuery(() -> new BasicBlock(Material.WOOD, woodSettings )));
        END_WOOD_PLANKS.setBlockState(BlockStateBuilder.create().noState(randomRotationVariants(END_WOOD_PLANKS_model)));
        END_WOOD_PLANKS.setFlammablility(5,5);
        END_WOOD_PLANKS.finalise();
        planksTag.add(END_WOOD_PLANKS);
        makeSlabFrom(END_WOOD_PLANKS,END_WOOD_PLANK_SLAB); END_WOOD_PLANK_SLAB.finalise();
        String END_WOOD_FENCE_SIDE_model = BlockModelPresetBuilder.customTemplate("block/custom/end_wood_fence_side","end_wood_fence_side","block/custom/end_wood_fence_side");
        String END_WOOD_FENCE_SIDE_TALL_model = BlockModelPresetBuilder.customTemplate("block/custom/end_wood_fence_side_tall","end_wood_fence_side_tall","block/custom/end_wood_fence_side_tall");
        makeWallsFrom(END_WOOD_PLANKS,END_WOOD_FENCE,null,END_WOOD_FENCE_SIDE_model,END_WOOD_FENCE_SIDE_TALL_model,null); END_WOOD_FENCE.finalise();

        final String END_WOOD_model = BlockModelPresetBuilder.TopBottomSide(END_WOOD.id,"block/end_wood_top","block/end_wood_side","block/end_wood_top");
        BasicBlock.selectedPlacementConfig=BasicBlock.PILLAR_PLACEMENT;
        END_WOOD.setBlock(Globals.bootQuery(() -> new BasicBlock(Material.WOOD, logSettings)));
        END_WOOD.setBlockState(axisStates(END_WOOD_model));
        END_WOOD.setFlammablility(5,5);
        END_WOOD.finalise();

        final String END_ROCK_PILLAR_model = BlockModelPresetBuilder.TopBottomSide(END_STONE_PILLAR.id,"block/end_rock_pillar_top","block/end_rock_pillar_side","block/end_rock_pillar_top");
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
        END_LEAVES.finalise();

        final String BEDROCK_PILLAR_model = BlockModelPresetBuilder.TopBottomSide("bedrock_pillar","block/bedrock_pillar_top","block/bedrock_pillar_side","block/bedrock_pillar_top");
        BasicBlock.selectedPlacementConfig=BasicBlock.PILLAR_PLACEMENT;
        BEDROCK_PILLAR.setBlock(Globals.bootQuery(() -> new BasicBlock(Material.STONE, bedrockSettings)));
        BEDROCK_PILLAR.setBlockState(axisStates(BEDROCK_PILLAR_model));
        BEDROCK_PILLAR.finalise();


        final String[] THUNDERSTONE_ORE_models = {
            BlockModelPresetBuilder.allSidesSame(THUNDERSTONE_ORE.id, "block/thunderstone_ore"),
            BlockModelPresetBuilder.allSidesSame(THUNDERSTONE_ORE.id+"2", "block/thunderstone_ore2"),
        };
        ConnectedTextureBlockModel THUNDERSTONE_ORE_CBTM = new ConnectedTextureBlockModel(Utils.getSprite("block/thunderstone_ore"),"thunderstone_ore",
            block -> !block.getBlock().equals(THUNDERSTONE_BLOCK.get()),
            (block,w,p) -> !block.getBlock().equals(THUNDERSTONE_BLOCK.get()),
            TextureOrientation.BLOCK_ROTATION
        );
        THUNDERSTONE_ORE.setBlock(Globals.bootQuery(() ->new BasicBlock(Material.STONE, stoneSettings)));
        THUNDERSTONE_ORE.setBlockState(BlockStateBuilder.create().noState(oneVariant(BlockModelPresetBuilder.generated(THUNDERSTONE_ORE_CBTM))));
        THUNDERSTONE_ORE.setDrops(loottable->
            loottable.addPool(pool->
                pool.addEntry(item, entry->
                    entry.setOutput(ModItems.THUNDERSTONE.getJSONID())
                        .addFunction(set_count(num_uniform_random(1,3),false))
                        .addFunction(apply_bonus("minecraft:fortune", F_apply_bonus.ore_drops()))
                        .condition(invert(silktouch))

                ).addEntry(item, entry->
                    entry.setOutput(THUNDERSTONE_ORE.getJSONID())
                         .condition(silktouch)
                )
            )
        );
        THUNDERSTONE_ORE.finalise();

        ConnectedTextureBlockModel THUNDERSTONE_BLOCK_CBTM = new ConnectedTextureBlockModel(Utils.getSprite("block/thunderstone_block_connect"),"thunderstone_block",
          block -> block.getBlock().equals(THUNDERSTONE_BLOCK.get()) || block.getBlock().equals(THUNDERSTONE_ORE.get()),
          (block,w,p) -> (block.getBlock().equals(THUNDERSTONE_BLOCK.get()) || block.getBlock().equals(THUNDERSTONE_ORE.get())) || !block.isOpaqueFullCube(w,p),
          TextureOrientation.BLOCK_ROTATION
        );
        THUNDERSTONE_BLOCK.setBlock(Globals.bootQuery(() ->new BasicBlock(Material.STONE,
        settings->stoneSettings.get(settings).sounds(BlockSoundGroup.AMETHYST_BLOCK))));
        THUNDERSTONE_BLOCK.setBlockState(BlockStateBuilder.create().noState(oneVariant(BlockModelPresetBuilder.generated(THUNDERSTONE_BLOCK_CBTM))));
        THUNDERSTONE_BLOCK.setDrops(loottable->
            loottable.addPool(pool->
                pool.addEntry(item, entry->
                    entry.setOutput(ModItems.THUNDERSTONE.getJSONID())
                        .addFunction(set_count(num_uniform_random(3,5),false))
                        .addFunction(apply_bonus("minecraft:fortune", F_apply_bonus.ore_drops()))
                        .condition(invert(silktouch))

                ).addEntry(item, entry->
                    entry.setOutput(THUNDERSTONE_BLOCK.getJSONID())
                         .condition(silktouch)
                )
            )
            .addPool(pool->
                pool.addEntry(item, entry->
                    entry.setOutput(ModItems.THUNDER_CORE.getJSONID())
                    .condition(droprate_with_enchantment(
                    "minecraft:fortune",
                    50 /* <- no enchant % */, 70, 80, 100, 100 // <- max lvl fortune %
                    ))
                ).condition(invert(silktouch))
            )
        );
        THUNDERSTONE_BLOCK.finalise();


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
            .nonOpaque()
        )));
        INFUSER.setBlockState(BlockStateBuilder.create()
            .addStateVariant(HORIZONTAL_FACING(), Direction.NORTH,variant->variant.addModel(INFUSER_model,0))
            .addStateVariant(HORIZONTAL_FACING(), Direction.EAST,variant->variant.addModel(INFUSER_model,90))
            .addStateVariant(HORIZONTAL_FACING(), Direction.SOUTH,variant->variant.addModel(INFUSER_model,180))
            .addStateVariant(HORIZONTAL_FACING(), Direction.WEST,variant->variant.addModel(INFUSER_model,270))
        );
        INFUSER.setSettings(ModItems.getSettings((s) -> s.group(ItemGroup.DECORATIONS)));
        INFUSER.finalise();


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

        //---------------------------------------------------------------------
        /// Multiblock (graph) for energy storage

        final String CHARGE_CELL_PORT_model = BlockModelPresetBuilder.customTemplate("block/custom/charge_cell_port", "charge_cell_port", "block/custom/charge_cell_port");
        CHARGE_CELL_PORT.setBlock(Globals.bootQuery(() -> new ChargeAccessorPortBlock(Material.STONE,
            settings ->
            settings.breakByHand(false)
            .breakByTool(FabricToolTags.PICKAXES)
            .sounds(BlockSoundGroup.STONE)
            .hardness(2f)
            .resistance(10f)
            .nonOpaque()
        )));
        CHARGE_CELL_PORT.setBlockState(BlockStateBuilder.create().noState(oneVariant(CHARGE_CELL_PORT_model)));
        CHARGE_CELL_PORT.setSettings(ModItems.getSettings((s) -> s.group(ItemGroup.DECORATIONS)));
        CHARGE_CELL_PORT.setName("Small Charge Port");
        CHARGE_CELL_PORT.finalise();

        CHARGE_CELL_PORT_ENTITY = new RegisteredBlockEntity<>("charge_cell_port",ChargeStoragePortEntity::new,CHARGE_CELL_PORT);
        CHARGE_CELL_PORT_ENTITY_RENDERER = new RegisteredBlockEntityRenderer<>(CHARGE_CELL_PORT_ENTITY::get, ChargePortRenderer::new);



        final String CHARGE_CELL_TANK_model = BlockModelPresetBuilder.customTemplate("block/custom/charge_cell_tank_middle", "charge_cell_tank_middle", "block/custom/charge_cell_tank");
        final String CHARGE_CELL_TANK_TOP_model = BlockModelPresetBuilder.customTemplate("block/custom/charge_cell_tank_top", "charge_cell_tank_top", "block/custom/charge_cell_tank");
        final String CHARGE_CELL_TANK_BOTTOM_model = BlockModelPresetBuilder.customTemplate("block/custom/charge_cell_tank_bottom", "charge_cell_tank_bottom", "block/custom/charge_cell_tank");
        final String CHARGE_CELL_TANK_SINGULAR_model = BlockModelPresetBuilder.customTemplate("block/custom/charge_cell_tank_unconnected", "charge_cell_tank_unconnected", "block/custom/charge_cell_tank");
        CHARGE_CELL_TANK.setBlock(Globals.bootQuery(() -> new ChargeStorageTankBlock(Material.GLASS,
            settings->
            settings.breakByHand(false)
            .breakByTool(FabricToolTags.PICKAXES)
            .sounds(BlockSoundGroup.GLASS)
            .hardness(2f)
            .resistance(10f)
            .nonOpaque()
        )));
        CHARGE_CELL_TANK.setBlockState(BlockStateBuilder.create().stateCombinations((comb,variantList)->{
            boolean up = comb.get(ChargeStorageTankBlock.UP);
            boolean down = comb.get(ChargeStorageTankBlock.DOWN);
            if(up&&down){
                variantList.addModel(CHARGE_CELL_TANK_model);
            }else if(down){
                variantList.addModel(CHARGE_CELL_TANK_TOP_model);
            }else if(up){
                variantList.addModel(CHARGE_CELL_TANK_BOTTOM_model);
            }else{
                variantList.addModel(CHARGE_CELL_TANK_SINGULAR_model);
            }
        },ChargeStorageTankBlock.UP,ChargeStorageTankBlock.DOWN));
        CHARGE_CELL_TANK.setRenderlayer(RenderLayerOptions.CUTOUT);
        CHARGE_CELL_TANK.setName("Small Charge Tank");
        CHARGE_CELL_TANK.setSettings(ModItems.getSettings((s) -> s.group(ItemGroup.DECORATIONS)));
        CHARGE_CELL_TANK.setBlockitem((id, bitem) ->
            new RegisteredItem(id, bitem,
                item->{item.modelId = "charge_cell_tank_unconnected"; return item;}
            )
        );
        CHARGE_CELL_TANK.finalise();

        CHARGE_CELL_STORAGE_ENTITY = new RegisteredBlockEntity<>("charge_cell_tank",ChargeStorageTankEntity::new,CHARGE_CELL_TANK);
        CHARGE_CELL_TANK_ENTITY_RENDERER = new RegisteredBlockEntityRenderer<>(CHARGE_CELL_STORAGE_ENTITY::get, ChargeTankRenderer::new);



        final String CHARGE_TRANSPOSER_model = BlockModelPresetBuilder.directional( CHARGE_TRANSPOSER.id, "block/custom/charge_transferer");
        BasicBlock.selectedPlacementConfig = BasicBlock.HORIZONTAL_FACING_PLAYER_PLACEMENT;
        CHARGE_TRANSPOSER.setBlock(Globals.bootQuery(() -> new ChargeTransposerBlock(Material.STONE,
            settings->
            settings.breakByHand(false)
            .breakByTool(FabricToolTags.PICKAXES)
            .sounds(BlockSoundGroup.STONE)
            .hardness(2f)
            .resistance(10f)
        ,(b)->{}
        )));
        CHARGE_TRANSPOSER.setBlockState(
            horizontalDirectionalStates(CHARGE_TRANSPOSER_model)
            .addStateCombination(ChargeTransposerBlock.FLIPPED,
            (flip, ml)->{
                    ml.eachModelVariant(mv->{
                        if(flip){ mv.setY(mv.getY()+180); }
                    });
                }
            )
        );
        CHARGE_TRANSPOSER.setSettings(ModItems.getSettings((s) -> s.group(ItemGroup.DECORATIONS)));
        CHARGE_TRANSPOSER.finalise();

        CHARGE_TRANSPOSER_ENTITY = new RegisteredBlockEntity<>(CHARGE_TRANSPOSER.id,ChargeTransposerEntity::new,CHARGE_TRANSPOSER);
        CHARGE_TRANSPOSER_ENTITY_RENDERER = new RegisteredBlockEntityRenderer<>(CHARGE_TRANSPOSER_ENTITY::get,ChargeTransposerRenderer::new);

        CHARGE_TRANSPOSER_SCREEN=
            Globals.bootQuery( ()->
                new RegisteredScreenHandler<>(
                    "chargetransposerscreen",
                    ScreenHandlerRegistry.registerSimple(CHARGE_TRANSPOSER.getIdentifier(), ChargeTransposerScreenHandler::new),
                    ChargeTransposerScreen::new
                )
            );

         Globals.bootRun(()->{try{
             Class.forName(InfuserRenderer.class.getName());
             Class.forName(ChargePortRenderer.class.getName());
             Class.forName(ChargeTankRenderer.class.getName());
             Class.forName(ChargeTransposerScreen.class.getName());
         }catch(ClassNotFoundException ignored){}});
        //endregion

        new RegisteredModelProvider("modelProvider",new ModelProvider()); // yes
    }

    // convenience stuff:

    //I hope to fuck theres an easier way of doing this

    static void copyBlock(RegisteredBlock from,RegisteredBlock to){
        Globals.bootRun(()->{
            if(from.get() instanceof BasicBlock bb){
                to.setBlock(bb.copy());
            }else{
                to.setBlock(new Block(AbstractBlock.Settings.copy(from.get())));
            }
        });
    }

    static void makeSlabFrom(RegisteredBlock ORIGINAL, RegisteredBlock SLAB){
        BasicBlock.selectedPlacementConfig = BasicBlock.HALF_SLAB;
        copyBlock(ORIGINAL,SLAB);
        String[] tex = ORIGINAL.getPrimaryModelTextures();
        String top = BlockModelPresetBuilder.SlabTop(SLAB.id+"_top",tex[0],tex[1],tex[2]);
        String bottom = BlockModelPresetBuilder.SlabBottom(SLAB.id,tex[0],tex[1],tex[2]);
        String doubleSlab = BlockModelPresetBuilder.TopBottomSide(SLAB.id+"_double",tex[0],tex[1],tex[2]);
        var modelvar = ORIGINAL.getPrimaryModel().getFirst();
        if(modelvar!=null){
            doubleSlab=modelvar.getModel();
        }
        SLAB.setBlockState(slabStates(top,bottom,doubleSlab));
        SLAB.setDrops(loottable->
            loottable.addPool(pool->
                pool.addEntry(item,entry->
                    entry.setOutput(SLAB.getJSONID())
                    .addFunction(
                        set_count(num_constant(2),false)
                        .condition(state_matches(SLAB.getJSONID(),makeEntry(SLAB(),SlabType.DOUBLE)))
                    )
                )
            )
        );
        slabsTag.add(SLAB);
    }

    static void makeStairsFrom(RegisteredBlock ORIGINAL, RegisteredBlock STAIRS){
        BasicBlock.selectedPlacementConfig = BasicBlock.STAIRS;
        copyBlock(ORIGINAL,STAIRS);
        String[] tex = ORIGINAL.getPrimaryModelTextures();
        String stairs = BlockModelPresetBuilder.Stairs(STAIRS.id,tex[0],tex[1],tex[2]);
        String stairsOuter = BlockModelPresetBuilder.StairsOuter(STAIRS.id+"_outer",tex[0],tex[1],tex[2]);
        String stairsInner = BlockModelPresetBuilder.StairsInner(STAIRS.id+"_inner",tex[0],tex[1],tex[2]);
        STAIRS.setBlockState(stairStates(stairs,stairsInner,stairsOuter));
        stairsTag.add(STAIRS);
    }
    static void makeWallsFrom(RegisteredBlock ORIGINAL, RegisteredBlock WALL){
        makeWallsFrom(ORIGINAL,WALL,null,null,null,null);
    }
    static void makeWallsFrom(RegisteredBlock ORIGINAL, RegisteredBlock WALL, String postModel, String sideModel, String sideTallModel, String inventorymodel){
        BasicBlock.selectedPlacementConfig = BasicBlock.WALLS;
        copyBlock(ORIGINAL,WALL);
        String[] tex = ORIGINAL.getPrimaryModelTextures();
        String wallPost = postModel==null?BlockModelPresetBuilder.WallPost(WALL.id+"_post",tex[0]):postModel;
        String wallSide = sideModel==null?BlockModelPresetBuilder.WallSide(WALL.id+"_side",tex[0]):sideModel;
        String wallSideTall = sideTallModel==null?BlockModelPresetBuilder.WallSideTall(WALL.id+"_side_tall",tex[0]):sideTallModel;
        ModelJson inventory = BlockModelPresetBuilder.getModelJson(inventorymodel==null?BlockModelPresetBuilder.WallInventory("block/"+WALL.id+"_inventory",tex[0]):inventorymodel);
        WALL.setBlockState(wallStates(wallPost,wallSide,wallSideTall));
        WALL.setBlockitem((id, bitem) ->
            new RegisteredItem(id, bitem,
                item->{item.modelId = WALL.id+"_inventory"; return item;}
            )
        );
        wallsTag.add(WALL);
    }
    static void makeFenceFrom(RegisteredBlock ORIGINAL, RegisteredBlock FENCE){
        makeFenceFrom(ORIGINAL,FENCE,null,null,null);
    }

    static void makeFenceFrom(RegisteredBlock ORIGINAL, RegisteredBlock FENCE, String postModel, String sideModel, String inventorymodel){
        BasicBlock.selectedPlacementConfig = BasicBlock.FENCE;
        copyBlock(ORIGINAL,FENCE);
        String[] tex = ORIGINAL.getPrimaryModelTextures();
        String fencePost = postModel==null?BlockModelPresetBuilder.FencePost(FENCE.id+"_post",tex[0]):postModel;
        String fenceSide = sideModel==null?BlockModelPresetBuilder.FenceSide(FENCE.id+"_side",tex[0]):sideModel;
        ModelJson inventory = BlockModelPresetBuilder.getModelJson(inventorymodel==null?BlockModelPresetBuilder.FenceInventory("block/"+FENCE.id+"_inventory",tex[0]):inventorymodel);
        FENCE.setBlockState(fenceStates(fencePost,fenceSide));
        FENCE.setBlockitem((id, bitem) ->
            new RegisteredItem(id, bitem,
                item->{item.modelId = FENCE.id+"_inventory"; return item;}
            )
        );
        fencesTag.add(FENCE);
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






    static Func<ModelVariantList, ModelVariantList> oneVariant(String modelStr){
        return
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
