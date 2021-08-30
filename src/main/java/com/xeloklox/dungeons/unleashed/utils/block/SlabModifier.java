package com.xeloklox.dungeons.unleashed.utils.block;

import com.xeloklox.dungeons.unleashed.*;
import net.minecraft.block.*;
import net.minecraft.block.enums.*;
import net.minecraft.fluid.*;
import net.minecraft.item.*;
import net.minecraft.state.property.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.*;
import net.minecraft.world.*;

public class SlabModifier extends BasicBlockModifier{
    protected static VoxelShape
        BOTTOM_SHAPE,
        TOP_SHAPE;

    @Override
    public boolean canReplace(BlockState state, ItemPlacementContext context){
        ItemStack itemStack = context.getStack();
       SlabType slabType = state.get(Properties.SLAB_TYPE);
       if(slabType != SlabType.DOUBLE && itemStack.isOf(parent.asItem())){
           if(context.canReplaceExisting()){
               boolean bl = context.getHitPos().y - (double)context.getBlockPos().getY() > 0.5D;
               Direction direction = context.getSide();
               if(slabType == SlabType.BOTTOM){
                   return direction == Direction.UP || bl && direction.getAxis().isHorizontal();
               }else{
                   return direction == Direction.DOWN || !bl && direction.getAxis().isHorizontal();
               }
           }else{
               return true;
           }
       }
       return false;
    }

    @Override
    public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState){
        return state.get(Properties.SLAB_TYPE) != SlabType.DOUBLE;
    }

    public boolean canFillWithFluid(BlockView world, BlockPos pos, BlockState state, Fluid fluid){
        return state.get(Properties.SLAB_TYPE) != SlabType.DOUBLE;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos){
        return null;
    }

    @Override
    public boolean hasSidedTransparency(BlockState state){
        return state.get(Properties.SLAB_TYPE) != SlabType.DOUBLE;
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation){
        return state;
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror){
        return state;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context){
        SlabType slabType = state.get(Properties.SLAB_TYPE);
        switch(slabType){
            case DOUBLE:
                return VoxelShapes.fullCube();
            case TOP:
                return TOP_SHAPE;
            default:
                return BOTTOM_SHAPE;
        }
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context){
        return null;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx){
        BlockPos blockPos = ctx.getBlockPos();
        BlockState blockState = ctx.getWorld().getBlockState(blockPos);
        if(blockState.isOf(parent)){
            return blockState.with(Properties.SLAB_TYPE, SlabType.DOUBLE).with(Properties.WATERLOGGED, false);
        }else{
            FluidState fluidState = ctx.getWorld().getFluidState(blockPos);
            BlockState blockState2 = parent.getDefaultState().with(Properties.SLAB_TYPE, SlabType.BOTTOM).with(Properties.WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
            Direction direction = ctx.getSide();
            return direction != Direction.DOWN && (direction == Direction.UP || !(ctx.getHitPos().y - (double)blockPos.getY() > 0.5D)) ? blockState2 : blockState2.with(Properties.SLAB_TYPE, SlabType.TOP);
        }
    }

    static {
        if(Globals.bootStrapped){
            BOTTOM_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
            TOP_SHAPE = Block.createCuboidShape(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D);
        }
    }

}
