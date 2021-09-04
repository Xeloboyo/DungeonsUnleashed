package com.xeloklox.dungeons.unleashed.utils.block;

import com.google.common.collect.*;
import com.xeloklox.dungeons.unleashed.*;
import net.minecraft.block.*;
import net.minecraft.block.enums.*;
import net.minecraft.fluid.*;
import net.minecraft.item.*;
import net.minecraft.state.property.*;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.*;
import net.minecraft.util.*;
import net.minecraft.util.function.*;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.*;
import net.minecraft.world.*;

import java.util.*;

public class WallsModifier extends BasicBlockModifier{
    @Override
    public void onCreate(){
        this.shapeMap = this.getShapeMap(4.0F, 3.0F, 16.0F, 0.0F, 14.0F, 16.0F);
        this.collisionShapeMap = this.getShapeMap(4.0F, 3.0F, 24.0F, 0.0F, 24.0F, 24.0F);
    }
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
        if(direction == Direction.DOWN){
            return null;
        }else{
            return direction == Direction.UP ? this.getAdjacencyState(world, state, neighborPos, neighborState) : this.getConnectionState(world, pos, state, neighborPos, neighborState, direction);
        }
    }
    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation){
        return rotateWalls(state, rotation);
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror){
        return mirrorWalls(state,state,mirror);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context){
        return this.shapeMap.get(state);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context){
        return this.collisionShapeMap.get(state);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx){
        WorldView worldView = ctx.getWorld();
        BlockPos blockPos = ctx.getBlockPos();
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        BlockPos north = blockPos.north();
        BlockPos east = blockPos.east();
        BlockPos south = blockPos.south();
        BlockPos west = blockPos.west();
        BlockPos up = blockPos.up();
        BlockState blockStateNorth = worldView.getBlockState(north);
        BlockState blockStateEast = worldView.getBlockState(east);
        BlockState blockStateSouth = worldView.getBlockState(south);
        BlockState blockStateWest = worldView.getBlockState(west);
        BlockState blockStateUp = worldView.getBlockState(up);
        boolean c_south = canConnectWall(blockStateNorth, blockStateNorth.isSideSolidFullSquare(worldView, north, Direction.SOUTH), Direction.SOUTH);
        boolean c_west = canConnectWall(blockStateEast, blockStateEast.isSideSolidFullSquare(worldView, east, Direction.WEST), Direction.WEST);
        boolean c_north = canConnectWall(blockStateSouth, blockStateSouth.isSideSolidFullSquare(worldView, south, Direction.NORTH), Direction.NORTH);
        boolean c_east = canConnectWall(blockStateWest, blockStateWest.isSideSolidFullSquare(worldView, west, Direction.EAST), Direction.EAST);
        BlockState isWaterLogged = parent.getDefaultState().with(Properties.WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
        return getStateFromNeighbours(worldView, isWaterLogged, up, blockStateUp, c_south, c_west, c_north, c_east);
    }


    //region walls

        public static BooleanProperty UP;
        public static EnumProperty<WallShape> EAST_SHAPE;
        public static EnumProperty<WallShape> NORTH_SHAPE;
        public static EnumProperty<WallShape> SOUTH_SHAPE;
        public static EnumProperty<WallShape> WEST_SHAPE;
        private static VoxelShape TALL_POST_SHAPE;
        private static VoxelShape TALL_NORTH_SHAPE;
        private static VoxelShape TALL_SOUTH_SHAPE;
        private static VoxelShape TALL_WEST_SHAPE;
        private static VoxelShape TALL_EAST_SHAPE;
        private Map<BlockState, VoxelShape> shapeMap;
        private Map<BlockState, VoxelShape> collisionShapeMap;

        private static boolean isNotEmptyWall(BlockState blockState, Property<WallShape> property){
            return blockState.get(property) != WallShape.NONE;
        }

        private BlockState getAdjacencyState(WorldView worldView, BlockState blockState, BlockPos blockPos, BlockState blockState2){
            boolean bl = isNotEmptyWall(blockState, NORTH_SHAPE);
            boolean bl2 = isNotEmptyWall(blockState, EAST_SHAPE);
            boolean bl3 = isNotEmptyWall(blockState, SOUTH_SHAPE);
            boolean bl4 = isNotEmptyWall(blockState, WEST_SHAPE);
            return getStateFromNeighbours(worldView, blockState, blockPos, blockState2, bl, bl2, bl3, bl4);
        }

        private BlockState getConnectionState(WorldView worldView, BlockPos blockPos, BlockState blockState, BlockPos blockPos2, BlockState blockState2, Direction direction){
            Direction direction2 = direction.getOpposite();
            boolean bl = direction == Direction.NORTH ? canConnectWall(blockState2, blockState2.isSideSolidFullSquare(worldView, blockPos2, direction2), direction2) : isNotEmptyWall(blockState, NORTH_SHAPE);
            boolean bl2 = direction == Direction.EAST ? canConnectWall(blockState2, blockState2.isSideSolidFullSquare(worldView, blockPos2, direction2), direction2) : isNotEmptyWall(blockState, EAST_SHAPE);
            boolean bl3 = direction == Direction.SOUTH ? canConnectWall(blockState2, blockState2.isSideSolidFullSquare(worldView, blockPos2, direction2), direction2) : isNotEmptyWall(blockState, SOUTH_SHAPE);
            boolean bl4 = direction == Direction.WEST ? canConnectWall(blockState2, blockState2.isSideSolidFullSquare(worldView, blockPos2, direction2), direction2) : isNotEmptyWall(blockState, WEST_SHAPE);
            BlockPos blockPos3 = blockPos.up();
            BlockState blockState3 = worldView.getBlockState(blockPos3);
            return getStateFromNeighbours(worldView, blockState, blockPos3, blockState3, bl, bl2, bl3, bl4);
        }

        private Map<BlockState, VoxelShape> getShapeMap(float f, float g, float h, float i, float j, float k){
            float l = 8.0F - f;
            float m = 8.0F + f;
            float n = 8.0F - g;
            float o = 8.0F + g;
            VoxelShape voxelShape = Block.createCuboidShape(l, 0.0D, l, m, h, m);
            VoxelShape voxelShape2 = Block.createCuboidShape(n, i, 0.0D, o, j, o);
            VoxelShape voxelShape3 = Block.createCuboidShape(n, i, n, o, j, 16.0D);
            VoxelShape voxelShape4 = Block.createCuboidShape(0.0D, i, n, o, j, o);
            VoxelShape voxelShape5 = Block.createCuboidShape(n, i, n, 16.0D, j, o);
            VoxelShape voxelShape6 = Block.createCuboidShape(n, i, 0.0D, o, k, o);
            VoxelShape voxelShape7 = Block.createCuboidShape(n, i, n, o, k, 16.0D);
            VoxelShape voxelShape8 = Block.createCuboidShape(0.0D, i, n, o, k, o);
            VoxelShape voxelShape9 = Block.createCuboidShape(n, i, n, 16.0D, k, o);
            ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();
            Iterator var21 = UP.getValues().iterator();

            while(var21.hasNext()){
                Boolean boolean_ = (Boolean)var21.next();
                Iterator var23 = EAST_SHAPE.getValues().iterator();

                while(var23.hasNext()){
                    WallShape wallShape = (WallShape)var23.next();
                    Iterator var25 = NORTH_SHAPE.getValues().iterator();

                    while(var25.hasNext()){
                        WallShape wallShape2 = (WallShape)var25.next();
                        Iterator var27 = WEST_SHAPE.getValues().iterator();

                        while(var27.hasNext()){
                            WallShape wallShape3 = (WallShape)var27.next();
                            Iterator var29 = SOUTH_SHAPE.getValues().iterator();

                            while(var29.hasNext()){
                                WallShape wallShape4 = (WallShape)var29.next();
                                VoxelShape voxelShape10 = VoxelShapes.empty();
                                voxelShape10 = getCombinedWallVoxelShape(voxelShape10, wallShape, voxelShape5, voxelShape9);
                                voxelShape10 = getCombinedWallVoxelShape(voxelShape10, wallShape3, voxelShape4, voxelShape8);
                                voxelShape10 = getCombinedWallVoxelShape(voxelShape10, wallShape2, voxelShape2, voxelShape6);
                                voxelShape10 = getCombinedWallVoxelShape(voxelShape10, wallShape4, voxelShape3, voxelShape7);
                                if(boolean_){
                                    voxelShape10 = VoxelShapes.union(voxelShape10, voxelShape);
                                }

                                BlockState blockState = parent.getDefaultState().with(UP, boolean_).with(EAST_SHAPE, wallShape).with(WEST_SHAPE, wallShape3).with(NORTH_SHAPE, wallShape2).with(SOUTH_SHAPE, wallShape4);
                                builder.put(blockState.with(Properties.WATERLOGGED, false), voxelShape10);
                                builder.put(blockState.with(Properties.WATERLOGGED, true), voxelShape10);
                            }
                        }
                    }
                }
            }

            return builder.build();
        }

        private static VoxelShape getCombinedWallVoxelShape(VoxelShape voxelShape, WallShape wallShape, VoxelShape voxelShape2, VoxelShape voxelShape3){
            if(wallShape == WallShape.TALL){
                return VoxelShapes.union(voxelShape, voxelShape3);
            }else{
                return wallShape == WallShape.LOW ? VoxelShapes.union(voxelShape, voxelShape2) : voxelShape;
            }
        }


        public static BlockState rotateWalls(BlockState state, BlockRotation rotation){
            switch(rotation){
                case CLOCKWISE_180:
                    return state.with(NORTH_SHAPE, state.get(SOUTH_SHAPE)).with(EAST_SHAPE, state.get(WEST_SHAPE)).with(SOUTH_SHAPE, state.get(NORTH_SHAPE)).with(WEST_SHAPE, state.get(EAST_SHAPE));
                case COUNTERCLOCKWISE_90:
                    return state.with(NORTH_SHAPE, state.get(EAST_SHAPE)).with(EAST_SHAPE, state.get(SOUTH_SHAPE)).with(SOUTH_SHAPE, state.get(WEST_SHAPE)).with(WEST_SHAPE, state.get(NORTH_SHAPE));
                case CLOCKWISE_90:
                    return state.with(NORTH_SHAPE, state.get(WEST_SHAPE)).with(EAST_SHAPE, state.get(NORTH_SHAPE)).with(SOUTH_SHAPE, state.get(EAST_SHAPE)).with(WEST_SHAPE, state.get(SOUTH_SHAPE));
                default:
                    return state;
            }
        }

        public static BlockState mirrorWalls(BlockState defaultState, BlockState state, BlockMirror mirror){
            switch(mirror){
                case LEFT_RIGHT:
                    return state.with(NORTH_SHAPE, state.get(SOUTH_SHAPE)).with(SOUTH_SHAPE, state.get(NORTH_SHAPE));
                case FRONT_BACK:
                    return state.with(EAST_SHAPE, state.get(WEST_SHAPE)).with(WEST_SHAPE, state.get(EAST_SHAPE));
                default:
                    return defaultState;
            }
        }

        static boolean canConnectWall(BlockState state, boolean faceFullSquare, Direction side){
            Block block = state.getBlock();
            boolean bl = block instanceof FenceGateBlock && FenceGateBlock.canWallConnect(state, side);
            return state.isIn(BlockTags.WALLS) || !Block.cannotConnect(state) && faceFullSquare || block instanceof PaneBlock || bl;
        }


        private static boolean matchesAnywhere(VoxelShape voxelShape, VoxelShape voxelShape2){
            return !VoxelShapes.matchesAnywhere(voxelShape2, voxelShape, BooleanBiFunction.ONLY_FIRST);
        }

        static WallShape getWallHeight(boolean connects, VoxelShape voxelShape, VoxelShape voxelShape2){
            if(connects){
                return matchesAnywhere(voxelShape, voxelShape2) ? WallShape.TALL : WallShape.LOW;
            }else{
                return WallShape.NONE;
            }
        }

        static BlockState getStateFromAdjacent(BlockState blockState, boolean south, boolean west, boolean north, boolean east, VoxelShape voxelShape){
            return blockState.with(NORTH_SHAPE, getWallHeight(south, voxelShape, TALL_NORTH_SHAPE)).with(EAST_SHAPE, getWallHeight(west, voxelShape, TALL_EAST_SHAPE)).with(SOUTH_SHAPE, getWallHeight(north, voxelShape, TALL_SOUTH_SHAPE)).with(WEST_SHAPE, getWallHeight(east, voxelShape, TALL_WEST_SHAPE));
        }

        static BlockState getStateFromNeighbours(WorldView worldView, BlockState blockState, BlockPos blockPos, BlockState blockStateAbove, boolean south, boolean west, boolean north, boolean east){
            VoxelShape voxelShape = blockStateAbove.getCollisionShape(worldView, blockPos).getFace(Direction.DOWN);
            BlockState blockState3 = getStateFromAdjacent(blockState, south, west, north, east, voxelShape);
            return blockState3.with(UP, isPostShape(blockState3, blockStateAbove, voxelShape));
        }

        static boolean isPostShape(BlockState blockState, BlockState blockState2, VoxelShape voxelShape){
            boolean bl = blockState2.getBlock() instanceof WallBlock && blockState2.get(UP);
            if(bl){
                return true;
            }else{
                WallShape wallShape = blockState.get(NORTH_SHAPE);
                WallShape wallShape2 = blockState.get(SOUTH_SHAPE);
                WallShape wallShape3 = blockState.get(EAST_SHAPE);
                WallShape wallShape4 = blockState.get(WEST_SHAPE);
                boolean bl2 = wallShape2 == WallShape.NONE;
                boolean bl3 = wallShape4 == WallShape.NONE;
                boolean bl4 = wallShape3 == WallShape.NONE;
                boolean bl5 = wallShape == WallShape.NONE;
                boolean bl6 = bl5 && bl2 && bl3 && bl4 || bl5 != bl2 || bl3 != bl4;
                if(bl6){
                    return true;
                }else{
                    boolean bl7 = wallShape == WallShape.TALL && wallShape2 == WallShape.TALL || wallShape3 == WallShape.TALL && wallShape4 == WallShape.TALL;
                    if(bl7){
                        return false;
                    }else{
                        return blockState2.isIn(BlockTags.WALL_POST_OVERRIDE) || matchesAnywhere(voxelShape, TALL_POST_SHAPE);
                    }
                }
            }
        }

        //endregion

    static {
        if(Globals.bootStrapped){
            UP = Properties.UP;
            EAST_SHAPE = Properties.EAST_WALL_SHAPE;
            NORTH_SHAPE = Properties.NORTH_WALL_SHAPE;
            SOUTH_SHAPE = Properties.SOUTH_WALL_SHAPE;
            WEST_SHAPE = Properties.WEST_WALL_SHAPE;
            TALL_POST_SHAPE = Block.createCuboidShape(7.0D, 0.0D, 7.0D, 9.0D, 16.0D, 9.0D);
            TALL_NORTH_SHAPE = Block.createCuboidShape(7.0D, 0.0D, 0.0D, 9.0D, 16.0D, 9.0D);
            TALL_SOUTH_SHAPE = Block.createCuboidShape(7.0D, 0.0D, 7.0D, 9.0D, 16.0D, 16.0D);
            TALL_WEST_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 7.0D, 9.0D, 16.0D, 9.0D);
            TALL_EAST_SHAPE = Block.createCuboidShape(7.0D, 0.0D, 7.0D, 16.0D, 16.0D, 9.0D);
        }
    }
}
