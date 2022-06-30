package com.xeloklox.dungeons.unleashed.blocks;

import com.xeloklox.dungeons.unleashed.blockentity.screens.*;
import com.xeloklox.dungeons.unleashed.utils.block.*;
import com.xeloklox.dungeons.unleashed.utils.lambda.*;
import net.fabricmc.fabric.api.object.builder.v1.block.*;
import net.minecraft.block.*;
import net.minecraft.entity.player.*;
import net.minecraft.screen.*;
import net.minecraft.stat.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import net.minecraft.util.hit.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

public class JourneyerTableBlock extends BasicBlock{
    private static final Text TITLE = new TranslatableText("asjkdhkshfkesuhkfesufh");
    public JourneyerTableBlock(Material material, Func<FabricBlockSettings, FabricBlockSettings> settingsfunc){
        super(material, settingsfunc);
    }

    public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
       return new SimpleNamedScreenHandlerFactory((syncId, inventory, player) -> {
          return new ResearchMapScreenHandler(syncId, inventory, ScreenHandlerContext.create(world, pos));
       }, TITLE);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit){
        if (world.isClient) {
            return ActionResult.SUCCESS;
         } else {
            player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
            return ActionResult.CONSUME;
         }
    }
}
