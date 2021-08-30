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

import java.util.stream.*;

import static com.xeloklox.dungeons.unleashed.utils.block.BasicBlock.STAIRS;

public class StairsModifier extends BasicBlockModifier{

    @Override
    public boolean canReplace(BlockState state, ItemPlacementContext context){
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
        return direction.getAxis().isHorizontal() ? state.with(Properties.STAIR_SHAPE, getStairShape(state, world, pos)) : null;
    }

    @Override
    public boolean hasSidedTransparency(BlockState state){
        return true;
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation){
        return rotateStairs(state, rotation);
    }
    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror){
        return mirrorStairs(state,state,mirror);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context){
        return (state.get(Properties.BLOCK_HALF) == BlockHalf.TOP ? TOP_SHAPES : BOTTOM_SHAPES)[SHAPE_INDICES[this.getShapeIndexIndex(state)]];
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context){
        return null;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx){
        Direction direction = ctx.getSide();
        BlockPos blockPos = ctx.getBlockPos();
        FluidState fluidState = ctx.getWorld().getFluidState(blockPos);
        BlockState blockState = parent.getDefaultState().with(Properties.HORIZONTAL_FACING, ctx.getPlayerFacing()).with(Properties.BLOCK_HALF, direction != Direction.DOWN && (direction == Direction.UP || !(ctx.getHitPos().y - (double)blockPos.getY() > 0.5D)) ? BlockHalf.BOTTOM : BlockHalf.TOP).with(Properties.WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
        return blockState.with(Properties.STAIR_SHAPE, getStairShape(blockState, ctx.getWorld(), blockPos));
    }

    //region stairs
    ///region slabs
       protected static VoxelShape
       BOTTOM_SHAPE,
       TOP_SHAPE;
        protected static VoxelShape[] TOP_SHAPES;
        protected static VoxelShape[] BOTTOM_SHAPES;
        static int[] SHAPE_INDICES = new int[]{12, 5, 3, 10, 14, 13, 7, 11, 13, 7, 11, 14, 8, 4, 1, 2, 4, 1, 2, 8};

        private int getShapeIndexIndex(BlockState state){
            return state.get(Properties.STAIR_SHAPE).ordinal() * 4 + state.get(Properties.HORIZONTAL_FACING).getHorizontal();
        }


        private static StairShape getStairShape(BlockState state, BlockView world, BlockPos pos){
            Direction direction = state.get(Properties.HORIZONTAL_FACING);
            BlockState blockState = world.getBlockState(pos.offset(direction));
            if(isStairs(blockState) && state.get(Properties.BLOCK_HALF) == blockState.get(Properties.BLOCK_HALF)){
                Direction direction2 = blockState.get(Properties.HORIZONTAL_FACING);
                if(direction2.getAxis() != state.get(Properties.HORIZONTAL_FACING).getAxis() && isDifferentOrientation(state, world, pos, direction2.getOpposite())){
                    if(direction2 == direction.rotateYCounterclockwise()){
                        return StairShape.OUTER_LEFT;
                    }

                    return StairShape.OUTER_RIGHT;
                }
            }

            BlockState blockState2 = world.getBlockState(pos.offset(direction.getOpposite()));
            if(isStairs(blockState2) && state.get(Properties.BLOCK_HALF) == blockState2.get(Properties.BLOCK_HALF)){
                Direction direction3 = blockState2.get(Properties.HORIZONTAL_FACING);
                if(direction3.getAxis() != state.get(Properties.HORIZONTAL_FACING).getAxis() && isDifferentOrientation(state, world, pos, direction3)){
                    if(direction3 == direction.rotateYCounterclockwise()){
                        return StairShape.INNER_LEFT;
                    }

                    return StairShape.INNER_RIGHT;
                }
            }

            return StairShape.STRAIGHT;
        }

        private static boolean isDifferentOrientation(BlockState state, BlockView world, BlockPos pos, Direction dir){
            BlockState blockState = world.getBlockState(pos.offset(dir));
            return !isStairs(blockState) || blockState.get(Properties.HORIZONTAL_FACING) != state.get(Properties.HORIZONTAL_FACING) || blockState.get(Properties.BLOCK_HALF) != state.get(Properties.BLOCK_HALF);
        }

        public static boolean isStairs(BlockState state){
            if(state.getBlock() instanceof BasicBlock bb){
                if(bb.placementConfig == STAIRS){
                    return true;
                }
            }
            return state.getBlock() instanceof StairsBlock;
        }

        public static BlockState rotateStairs(BlockState state, BlockRotation rotation){
            return state.with(Properties.HORIZONTAL_FACING, rotation.rotate(state.get(Properties.HORIZONTAL_FACING)));
        }

        public static BlockState mirrorStairs(BlockState defaultMirror, BlockState state, BlockMirror mirror){
            Direction direction = state.get(Properties.HORIZONTAL_FACING);
            StairShape stairShape = state.get(Properties.STAIR_SHAPE);
            switch(mirror){
                case LEFT_RIGHT:
                    if(direction.getAxis() == Direction.Axis.Z){
                        switch(stairShape){
                            case INNER_LEFT:
                                return state.rotate(BlockRotation.CLOCKWISE_180).with(Properties.STAIR_SHAPE, StairShape.INNER_RIGHT);
                            case INNER_RIGHT:
                                return state.rotate(BlockRotation.CLOCKWISE_180).with(Properties.STAIR_SHAPE, StairShape.INNER_LEFT);
                            case OUTER_LEFT:
                                return state.rotate(BlockRotation.CLOCKWISE_180).with(Properties.STAIR_SHAPE, StairShape.OUTER_RIGHT);
                            case OUTER_RIGHT:
                                return state.rotate(BlockRotation.CLOCKWISE_180).with(Properties.STAIR_SHAPE, StairShape.OUTER_LEFT);
                            default:
                                return state.rotate(BlockRotation.CLOCKWISE_180);
                        }
                    }
                    break;
                case FRONT_BACK:
                    if(direction.getAxis() == Direction.Axis.X){
                        switch(stairShape){
                            case INNER_LEFT:
                                return state.rotate(BlockRotation.CLOCKWISE_180).with(Properties.STAIR_SHAPE, StairShape.INNER_LEFT);
                            case INNER_RIGHT:
                                return state.rotate(BlockRotation.CLOCKWISE_180).with(Properties.STAIR_SHAPE, StairShape.INNER_RIGHT);
                            case OUTER_LEFT:
                                return state.rotate(BlockRotation.CLOCKWISE_180).with(Properties.STAIR_SHAPE, StairShape.OUTER_RIGHT);
                            case OUTER_RIGHT:
                                return state.rotate(BlockRotation.CLOCKWISE_180).with(Properties.STAIR_SHAPE, StairShape.OUTER_LEFT);
                            case STRAIGHT:
                                return state.rotate(BlockRotation.CLOCKWISE_180);
                        }
                    }
            }
            return defaultMirror;
        }

        public static VoxelShape[] composeShapes(VoxelShape base, VoxelShape northWest, VoxelShape northEast, VoxelShape southWest, VoxelShape southEast){
            return IntStream.range(0, 16).mapToObj((i) -> {
                return composeShape(i, base, northWest, northEast, southWest, southEast);
            }).toArray((i) -> {
                return new VoxelShape[i];
            });
        }

        public static VoxelShape composeShape(int i, VoxelShape base, VoxelShape northWest, VoxelShape northEast, VoxelShape southWest, VoxelShape southEast){
            VoxelShape voxelShape = base;
            if((i & 1) != 0){
                voxelShape = VoxelShapes.union(base, northWest);
            }

            if((i & 2) != 0){
                voxelShape = VoxelShapes.union(voxelShape, northEast);
            }

            if((i & 4) != 0){
                voxelShape = VoxelShapes.union(voxelShape, southWest);
            }

            if((i & 8) != 0){
                voxelShape = VoxelShapes.union(voxelShape, southEast);
            }

            return voxelShape;
        }

        //endregion
    static {
            if(Globals.bootStrapped){
                BOTTOM_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
                TOP_SHAPE = Block.createCuboidShape(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D);
                VoxelShape BOTTOM_NORTH_WEST_CORNER_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 8.0D, 8.0D, 8.0D);
                VoxelShape BOTTOM_SOUTH_WEST_CORNER_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 8.0D, 8.0D, 8.0D, 16.0D);
                VoxelShape TOP_NORTH_WEST_CORNER_SHAPE = Block.createCuboidShape(0.0D, 8.0D, 0.0D, 8.0D, 16.0D, 8.0D);
                VoxelShape TOP_SOUTH_WEST_CORNER_SHAPE = Block.createCuboidShape(0.0D, 8.0D, 8.0D, 8.0D, 16.0D, 16.0D);
                VoxelShape BOTTOM_NORTH_EAST_CORNER_SHAPE = Block.createCuboidShape(8.0D, 0.0D, 0.0D, 16.0D, 8.0D, 8.0D);
                VoxelShape BOTTOM_SOUTH_EAST_CORNER_SHAPE = Block.createCuboidShape(8.0D, 0.0D, 8.0D, 16.0D, 8.0D, 16.0D);
                VoxelShape TOP_NORTH_EAST_CORNER_SHAPE = Block.createCuboidShape(8.0D, 8.0D, 0.0D, 16.0D, 16.0D, 8.0D);
                VoxelShape TOP_SOUTH_EAST_CORNER_SHAPE = Block.createCuboidShape(8.0D, 8.0D, 8.0D, 16.0D, 16.0D, 16.0D);
                TOP_SHAPES = composeShapes(TOP_SHAPE, BOTTOM_NORTH_WEST_CORNER_SHAPE, BOTTOM_NORTH_EAST_CORNER_SHAPE, BOTTOM_SOUTH_WEST_CORNER_SHAPE, BOTTOM_SOUTH_EAST_CORNER_SHAPE);
                BOTTOM_SHAPES = composeShapes(BOTTOM_SHAPE, TOP_NORTH_WEST_CORNER_SHAPE, TOP_NORTH_EAST_CORNER_SHAPE, TOP_SOUTH_WEST_CORNER_SHAPE, TOP_SOUTH_EAST_CORNER_SHAPE);
            }}
}
