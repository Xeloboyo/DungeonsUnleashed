package com.xeloklox.dungeons.unleashed.utils.block;

import net.minecraft.block.*;
import net.minecraft.entity.player.*;
import net.minecraft.fluid.*;
import net.minecraft.item.*;
import net.minecraft.state.property.*;
import net.minecraft.tag.*;
import net.minecraft.util.*;
import net.minecraft.util.hit.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

public class FenceModifier extends HorizontalConnectingModifier{
    public FenceModifier(){
        super(2.0F, 2.0F, 16.0F, 16.0F, 24.0F);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx){
        BlockView blockView = ctx.getWorld();
        BlockPos blockPos = ctx.getBlockPos();
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        BlockPos blockPos2 = blockPos.north();
        BlockPos blockPos3 = blockPos.east();
        BlockPos blockPos4 = blockPos.south();
        BlockPos blockPos5 = blockPos.west();
        BlockState blockState = blockView.getBlockState(blockPos2);
        BlockState blockState2 = blockView.getBlockState(blockPos3);
        BlockState blockState3 = blockView.getBlockState(blockPos4);
        BlockState blockState4 = blockView.getBlockState(blockPos5);
        return parent.getDefaultState().with(NORTH, this.canConnect(blockState, blockState.isSideSolidFullSquare(blockView, blockPos2, Direction.SOUTH), Direction.SOUTH)).with(EAST, this.canConnect(blockState2, blockState2.isSideSolidFullSquare(blockView, blockPos3, Direction.WEST), Direction.WEST)).with(SOUTH, this.canConnect(blockState3, blockState3.isSideSolidFullSquare(blockView, blockPos4, Direction.NORTH), Direction.NORTH)).with(WEST, this.canConnect(blockState4, blockState4.isSideSolidFullSquare(blockView, blockPos5, Direction.EAST), Direction.EAST)).with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);

    }

    public boolean canConnect(BlockState state, boolean neighborIsFullSquare, Direction dir){
        Block block = state.getBlock();
        boolean bl = this.canConnectToFence(state);
        boolean bl2 = block instanceof FenceGateBlock && FenceGateBlock.canWallConnect(state, dir);
        return !Block.cannotConnect(state) && neighborIsFullSquare || bl || bl2;
    }

    private boolean canConnectToFence(BlockState state){
        return state.isIn(BlockTags.FENCES) && state.isIn(BlockTags.WOODEN_FENCES) == parent.getDefaultState().isIn(BlockTags.WOODEN_FENCES);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos){
        if((Boolean)state.get(WATERLOGGED)){
            world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return direction.getAxis().getType() == Direction.Type.HORIZONTAL ? (BlockState)state.with((Property)FACING_PROPERTIES.get(direction), this.canConnect(neighborState, neighborState.isSideSolidFullSquare(world, neighborPos, direction.getOpposite()), direction.getOpposite())) : super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit){
        if (world.isClient) {
             ItemStack itemStack = player.getStackInHand(hand);
             return itemStack.isOf(Items.LEAD) ? ActionResult.SUCCESS : ActionResult.PASS;
          } else {
             return LeadItem.attachHeldMobsToBlock(player, world, pos);
          }
    }
}
