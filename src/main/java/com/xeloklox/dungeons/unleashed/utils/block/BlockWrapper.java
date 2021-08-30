package com.xeloklox.dungeons.unleashed.utils.block;

import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.screen.*;
import net.minecraft.state.*;
import net.minecraft.util.*;
import net.minecraft.util.hit.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import org.jetbrains.annotations.*;

public class BlockWrapper extends Block{
    PlacementConfig placementConfig;
    protected BlockInterface block;
    public BlockWrapper(PlacementConfig placementConfig, BlockInterface i,Settings settings){
        super(settings);
        block = i;
        this.placementConfig=placementConfig;
        StateManager.Builder<Block, BlockState> builder = new StateManager.Builder(this);
        this.appendProperties(builder);
        block.appendProperties(builder);
        BlockInterface.replaceStateManager(this, builder.build(Block::getDefaultState, BlockState::new));
        this.setDefaultState(this.stateManager.getDefaultState());
    }

    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify){
        super.onBlockAdded(state,world,pos,oldState,notify);
        block.onBlockAdded(state,world,pos,oldState,notify);
    }
    public BlockState getPlacementState(ItemPlacementContext ctx){
        return null;
    }

    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack){
        super.onPlaced(world,pos,state,placer,itemStack);
        block.onPlaced(world,pos,state,placer,itemStack);
    }

    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify){
        super.neighborUpdate(state,world,pos,block,fromPos,notify);
        this.block.neighborUpdate(state,world,pos,block,fromPos,notify);
    }

    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved){
        this.block.onStateReplaced(state, world, pos, newState, moved);
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit){
        ActionResult a = this.block.onUse(state, world, pos, player, hand, hit);
        if(a.equals(ActionResult.PASS)){
            return super.onUse(state, world, pos, player, hand, hit);
        }
        return a;
    }

    @Nullable
    @Override
    public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos){
        NamedScreenHandlerFactory nms = block.createScreenHandlerFactory(state, world, pos);
        if(nms == null){
            return super.createScreenHandlerFactory(state, world, pos);
        }
        return nms;
    }


}
