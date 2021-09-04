package com.xeloklox.dungeons.unleashed.utils.block;

import com.google.common.collect.*;
import com.xeloklox.dungeons.unleashed.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.block.*;
import net.minecraft.item.*;
import net.minecraft.state.property.*;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.*;
import net.minecraft.world.*;

import java.util.*;

public class HorizontalConnectingModifier extends BasicBlockModifier{
    public static BooleanProperty NORTH;
    public static BooleanProperty EAST;
    public static BooleanProperty SOUTH;
    public static BooleanProperty WEST;
    public static BooleanProperty WATERLOGGED;
    protected static Map<Direction, BooleanProperty> FACING_PROPERTIES;
    protected VoxelShape[] collisionShapes;
    protected VoxelShape[] boundingShapes;
    private final Object2IntMap<BlockState> SHAPE_INDEX_CACHE = new Object2IntOpenHashMap();

    float radius1;
    float radius2;
    float boundingHeight1;
    float boundingHeight2;
    float collisionHeight;

    public HorizontalConnectingModifier(float radius1, float radius2, float boundingHeight1, float boundingHeight2, float collisionHeight){
        this.radius1 = radius1;
        this.radius2 = radius2;
        this.boundingHeight1 = boundingHeight1;
        this.boundingHeight2 = boundingHeight2;
        this.collisionHeight = collisionHeight;
    }

    @Override
    public void onCreate(){
        this.collisionShapes = this.createShapes(radius1, radius2, collisionHeight, 0.0F, collisionHeight);
        this.boundingShapes = this.createShapes(radius1, radius2, boundingHeight1, 0.0F, boundingHeight2);
        UnmodifiableIterator var7 = parent.getStateManager().getStates().iterator();

        while(var7.hasNext()){
            BlockState blockState = (BlockState)var7.next();
            this.getShapeIndex(blockState);
        }
    }

    @Override
    public boolean canReplace(BlockState state, ItemPlacementContext context){
        return false;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos){
        return null;
    }

    protected VoxelShape[] createShapes(float radius1, float radius2, float height1, float offset2, float height2){
        float f = 8.0F - radius1;
        float g = 8.0F + radius1;
        float h = 8.0F - radius2;
        float i = 8.0F + radius2;
        VoxelShape voxelShape = Block.createCuboidShape(f, 0.0D, f, g, height1, g);
        VoxelShape voxelShape2 = Block.createCuboidShape(h, offset2, 0.0D, i, height2, i);
        VoxelShape voxelShape3 = Block.createCuboidShape(h, offset2, h, i, height2, 16.0D);
        VoxelShape voxelShape4 = Block.createCuboidShape(0.0D, offset2, h, i, height2, i);
        VoxelShape voxelShape5 = Block.createCuboidShape(h, offset2, h, 16.0D, height2, i);
        VoxelShape voxelShape6 = VoxelShapes.union(voxelShape2, voxelShape5);
        VoxelShape voxelShape7 = VoxelShapes.union(voxelShape3, voxelShape4);
        VoxelShape[] voxelShapes = new VoxelShape[]{VoxelShapes.empty(), voxelShape3, voxelShape4, voxelShape7, voxelShape2, VoxelShapes.union(voxelShape3, voxelShape2), VoxelShapes.union(voxelShape4, voxelShape2), VoxelShapes.union(voxelShape7, voxelShape2), voxelShape5, VoxelShapes.union(voxelShape3, voxelShape5), VoxelShapes.union(voxelShape4, voxelShape5), VoxelShapes.union(voxelShape7, voxelShape5), voxelShape6, VoxelShapes.union(voxelShape3, voxelShape6), VoxelShapes.union(voxelShape4, voxelShape6), VoxelShapes.union(voxelShape7, voxelShape6)};

        for(int j = 0; j < 16; ++j){
            voxelShapes[j] = VoxelShapes.union(voxelShape, voxelShapes[j]);
        }

        return voxelShapes;
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation){
        switch(rotation){
            case CLOCKWISE_180:
                return state.with(NORTH, state.get(SOUTH)).with(EAST, state.get(WEST)).with(SOUTH, state.get(NORTH)).with(WEST, state.get(EAST));
            case COUNTERCLOCKWISE_90:
                return state.with(NORTH, state.get(EAST)).with(EAST, state.get(SOUTH)).with(SOUTH, state.get(WEST)).with(WEST, state.get(NORTH));
            case CLOCKWISE_90:
                return state.with(NORTH, state.get(WEST)).with(EAST, state.get(NORTH)).with(SOUTH, state.get(EAST)).with(WEST, state.get(SOUTH));
            default:
                return state;
        }
    }


    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror){
        switch(mirror){
            case LEFT_RIGHT:
                return state.with(NORTH, state.get(SOUTH)).with(SOUTH, state.get(NORTH));
            case FRONT_BACK:
                return state.with(EAST, state.get(WEST)).with(WEST, state.get(EAST));
            default:
                return null;
        }
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context){
        return this.boundingShapes[this.getShapeIndex(state)];
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context){
        return this.collisionShapes[this.getShapeIndex(state)];
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx){
        return null;
    }

    private static int getDirectionMask(Direction dir){
        return 1 << dir.getHorizontal();
    }

    protected int getShapeIndex(BlockState state){
        return this.SHAPE_INDEX_CACHE.computeIntIfAbsent(state, (blockState) -> {
            int i = 0;
            if(blockState.get(NORTH)){
                i |= getDirectionMask(Direction.NORTH);
            }

            if(blockState.get(EAST)){
                i |= getDirectionMask(Direction.EAST);
            }

            if(blockState.get(SOUTH)){
                i |= getDirectionMask(Direction.SOUTH);
            }

            if(blockState.get(WEST)){
                i |= getDirectionMask(Direction.WEST);
            }

            return i;
        });
    }

    static{
        if(Globals.bootStrapped){
            NORTH = ConnectingBlock.NORTH;
            EAST = ConnectingBlock.EAST;
            SOUTH = ConnectingBlock.SOUTH;
            WEST = ConnectingBlock.WEST;
            WATERLOGGED = Properties.WATERLOGGED;
            FACING_PROPERTIES = ConnectingBlock.FACING_PROPERTIES.entrySet().stream().filter((entry) -> {
                return entry.getKey().getAxis().isHorizontal();
            }).collect(Util.toMap());
        }
    }
}
