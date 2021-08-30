package com.xeloklox.dungeons.unleashed.utils.block;

import com.google.common.collect.*;
import com.xeloklox.dungeons.unleashed.*;
import com.xeloklox.dungeons.unleashed.utils.lambda.*;
import net.fabricmc.fabric.api.object.builder.v1.block.*;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.block.enums.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.fluid.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.screen.*;
import net.minecraft.state.StateManager.*;
import net.minecraft.state.property.*;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.tag.*;
import net.minecraft.util.*;
import net.minecraft.util.function.*;
import net.minecraft.util.hit.*;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.*;
import net.minecraft.world.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.stream.*;

/**
 * Class that tries to cover alot of the general vanilla cases for blocks
 * :D I also hate good clean code too
 **/
public class BasicBlock extends Block implements Waterloggable{
    public static PlacementConfig selectedPlacementConfig;
    PlacementConfig placementConfig = DEFAULT_PLACEMENT;
    boolean waterloggable = false;
    public Func<FabricBlockSettings, FabricBlockSettings> settings;

    public BasicBlock(Material material, Func<FabricBlockSettings, FabricBlockSettings> settingsfunc){
        this(material, settingsfunc, block -> {
        });
    }

    public BasicBlock(Material material, Func<FabricBlockSettings, FabricBlockSettings> settingsfunc, Cons<BasicBlock> additionalSettings){
        super(settingsfunc.get(FabricBlockSettings.of(material)));
        settings = settingsfunc;
        additionalSettings.get(this);
        this.placementConfig = selectedPlacementConfig;
        selectedPlacementConfig = DEFAULT_PLACEMENT;
        waterloggable = this.placementConfig.hasProperty(Properties.WATERLOGGED);
        if(placementConfig == WALLS){
            this.setDefaultState(this.stateManager.getDefaultState().with(UP, true).with(NORTH_SHAPE, WallShape.NONE).with(EAST_SHAPE, WallShape.NONE).with(SOUTH_SHAPE, WallShape.NONE).with(WEST_SHAPE, WallShape.NONE).with(Properties.WATERLOGGED, false));
            this.shapeMap = this.getShapeMap(4.0F, 3.0F, 16.0F, 0.0F, 14.0F, 16.0F);
            this.collisionShapeMap = this.getShapeMap(4.0F, 3.0F, 24.0F, 0.0F, 24.0F, 24.0F);
        }
    }

    public BasicBlock copy(){
        return new BasicBlock(this.material, this.settings);
    }

    public <T extends BlockEntity> Class<T> getEntityClass(){
        return null;
    }

    @Nullable
    @Override
    public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos){
        boolean isGUI = getEntityClass() != null && NamedScreenHandlerFactory.class.isAssignableFrom(getEntityClass());
        if(isGUI){
            BlockEntity blockEntity = world.getBlockEntity(pos);
            return blockEntity instanceof NamedScreenHandlerFactory ? (NamedScreenHandlerFactory)blockEntity : null;
        }else{
            return super.createScreenHandlerFactory(state, world, pos);
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit){
        boolean isGUI = getEntityClass() != null && NamedScreenHandlerFactory.class.isAssignableFrom(getEntityClass());
        if(!world.isClient && isGUI){
            NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);
            if(screenHandlerFactory != null){
                //With this call the server will request the client to open the appropriate Screenhandler
                player.openHandledScreen(screenHandlerFactory);
                System.out.println("screen opening!");
            }
            return ActionResult.SUCCESS;
        }
        return isGUI ? ActionResult.SUCCESS : ActionResult.PASS;
    }


    @Override
    protected void appendProperties(Builder<Block, BlockState> builder){
        super.appendProperties(builder);
        for(var a : selectedPlacementConfig.properties){
            builder.add(a);
        }
    }

    @Nullable
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> checkType(BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker){
        return expectedType == givenType ? (BlockEntityTicker<A>)ticker : null;
    }

    public void onDisturbed(BlockState state, World world, BlockPos pos){
    }

    public void onDestroyed(BlockState state, World world, BlockPos pos){
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify){
        super.onBlockAdded(state, world, pos, oldState, notify);
        onDisturbed(state, world, pos);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack){
        super.onPlaced(world, pos, state, placer, itemStack);
        onDisturbed(state, world, pos);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify){
        super.neighborUpdate(state, world, pos, block, fromPos, notify);
        onDisturbed(state, world, pos);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved){
        if(newState.getBlock() != this){
            onDisturbed(state, world, pos);
            onDestroyed(state, world, pos);
        }else{
            if(getEntityClass() != null && Inventory.class.isAssignableFrom(getEntityClass())){
                BlockEntity blockEntity = world.getBlockEntity(pos);
                if(blockEntity instanceof Inventory inventory){
                    ItemScatterer.spawn(world, pos, inventory);
                    // update comparators
                    world.updateComparators(pos, this);
                }
            }
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    public boolean canReplace(BlockState state, ItemPlacementContext context){
        if(this.placementConfig == HALF_SLAB){
            ItemStack itemStack = context.getStack();
            SlabType slabType = state.get(Properties.SLAB_TYPE);
            if(slabType != SlabType.DOUBLE && itemStack.isOf(this.asItem())){
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
        }
        return false;
    }


    public BlockState getPlacementState(ItemPlacementContext ctx){
        return placementConfig.placementState.get(ctx, this);
    }

    public void setPlacementConfig(PlacementConfig placementConfig){
        this.placementConfig = placementConfig;
    }

    public FluidState getFluidState(BlockState state){
        if(waterloggable){
            return state.get(Properties.WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
        }else{
            return super.getFluidState(state);
        }
    }

    public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState){
        if(waterloggable){
            if(this.placementConfig == HALF_SLAB){
                return state.get(Properties.SLAB_TYPE) != SlabType.DOUBLE && Waterloggable.super.tryFillWithFluid(world, pos, state, fluidState);
            }else{
                return Waterloggable.super.tryFillWithFluid(world, pos, state, fluidState);
            }
        }
        return false;
    }

    public boolean canFillWithFluid(BlockView world, BlockPos pos, BlockState state, Fluid fluid){
        if(waterloggable){
            if(this.placementConfig == HALF_SLAB){
                return state.get(Properties.SLAB_TYPE) != SlabType.DOUBLE && Waterloggable.super.canFillWithFluid(world, pos, state, fluid);
            }else{
                return Waterloggable.super.canFillWithFluid(world, pos, state, fluid);
            }
        }
        return false;
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos){
        if(waterloggable && state.get(Properties.WATERLOGGED)){
            world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        if(this.placementConfig == STAIRS){
            return direction.getAxis().isHorizontal() ? state.with(Properties.STAIR_SHAPE, getStairShape(state, world, pos)) : super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
        }
        if(this.placementConfig == WALLS){
            if(direction == Direction.DOWN){
                return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
            }else{
                return direction == Direction.UP ? this.getAdjacencyState(world, state, neighborPos, neighborState) : this.getConnectionState(world, pos, state, neighborPos, neighborState, direction);
            }
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    public boolean hasSidedTransparency(BlockState state){
        if(this.placementConfig == HALF_SLAB){
            return state.get(Properties.SLAB_TYPE) != SlabType.DOUBLE;
        }else return this.placementConfig == STAIRS;
    }

    public BlockState rotate(BlockState state, BlockRotation rotation){
        if(this.placementConfig == STAIRS){
            rotateStairs(state, rotation);
        }else if(this.placementConfig == WALLS){
            rotateWalls(state, rotation);
        }
        return super.rotate(state, rotation);
    }

    public BlockState mirror(BlockState state, BlockMirror mirror){
        if(this.placementConfig == STAIRS){
            mirrorStairs(super.mirror(state, mirror), state, mirror);
        }else if(this.placementConfig == WALLS){
            mirrorWalls(super.mirror(state, mirror), state, mirror);
        }
        return super.mirror(state, mirror);
    }


    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context){
        if(this.placementConfig == HALF_SLAB){
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
        if(this.placementConfig == STAIRS){
            return (state.get(Properties.BLOCK_HALF) == BlockHalf.TOP ? TOP_SHAPES : BOTTOM_SHAPES)[SHAPE_INDICES[this.getShapeIndexIndex(state)]];
        }
        if(this.placementConfig == WALLS){
            return this.shapeMap.get(state);
        }
        return VoxelShapes.fullCube();
    }

    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context){
        if(this.placementConfig == WALLS){
            return this.collisionShapeMap.get(state);
        }
        return super.getCollisionShape(state, world, pos, context);
    }


    public static PlacementConfig
    DEFAULT_PLACEMENT,
    PILLAR_PLACEMENT,
    HORIZONTAL_FACING_PLAYER_PLACEMENT,
    FACING_OPPOSITE_PLAYER_PLACEMENT,
    FACING_PLAYER_PLACEMENT,
    STAIRS,
    WALLS,
    HALF_SLAB;

    public static class PlacementConfig{
        Func2<ItemPlacementContext, Block, BlockState> placementState;
        Property[] properties;

        public PlacementConfig(Func2<ItemPlacementContext, Block, BlockState> placementState, Property... properties){
            this.placementState = placementState;
            this.properties = properties;
            if(this.properties == null){
                this.properties = new Property[]{};
            }
        }

        boolean hasProperty(Property p){
            for(Property p2 : properties){
                if(p.equals(p2)){
                    return true;
                }
            }
            return false;
        }
    }

    ///region slabs
    protected static VoxelShape
    BOTTOM_SHAPE,
    TOP_SHAPE;


    //endregion
    //region stairs

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
        boolean bl = direction == Direction.NORTH ? shouldConnectTo(blockState2, blockState2.isSideSolidFullSquare(worldView, blockPos2, direction2), direction2) : isNotEmptyWall(blockState, NORTH_SHAPE);
        boolean bl2 = direction == Direction.EAST ? shouldConnectTo(blockState2, blockState2.isSideSolidFullSquare(worldView, blockPos2, direction2), direction2) : isNotEmptyWall(blockState, EAST_SHAPE);
        boolean bl3 = direction == Direction.SOUTH ? shouldConnectTo(blockState2, blockState2.isSideSolidFullSquare(worldView, blockPos2, direction2), direction2) : isNotEmptyWall(blockState, SOUTH_SHAPE);
        boolean bl4 = direction == Direction.WEST ? shouldConnectTo(blockState2, blockState2.isSideSolidFullSquare(worldView, blockPos2, direction2), direction2) : isNotEmptyWall(blockState, WEST_SHAPE);
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

                            BlockState blockState = this.getDefaultState().with(UP, boolean_).with(EAST_SHAPE, wallShape).with(WEST_SHAPE, wallShape3).with(NORTH_SHAPE, wallShape2).with(SOUTH_SHAPE, wallShape4);
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

    static boolean shouldConnectTo(BlockState state, boolean faceFullSquare, Direction side){
        Block block = state.getBlock();
        boolean bl = block instanceof FenceGateBlock && FenceGateBlock.canWallConnect(state, side);
        return state.isIn(BlockTags.WALLS) || !cannotConnect(state) && faceFullSquare || block instanceof PaneBlock || bl;
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
    //i see why its at the end now
    static{
        if(Globals.bootStrapped){
            UP = Properties.UP;
            EAST_SHAPE = Properties.EAST_WALL_SHAPE;
            NORTH_SHAPE = Properties.NORTH_WALL_SHAPE;
            SOUTH_SHAPE = Properties.SOUTH_WALL_SHAPE;
            WEST_SHAPE = Properties.WEST_WALL_SHAPE;


            DEFAULT_PLACEMENT = new PlacementConfig((ctx, block) -> block.getDefaultState());
            PILLAR_PLACEMENT = new PlacementConfig((ctx, block) -> block.getDefaultState().with(Properties.AXIS, ctx.getSide().getAxis()), Properties.AXIS);
            HORIZONTAL_FACING_PLAYER_PLACEMENT = new PlacementConfig((ctx, block) -> block.getDefaultState().with(Properties.HORIZONTAL_FACING, ctx.getPlayerFacing()), Properties.HORIZONTAL_FACING);
            FACING_OPPOSITE_PLAYER_PLACEMENT = new PlacementConfig((ctx, block) -> block.getDefaultState().with(Properties.HORIZONTAL_FACING, ctx.getPlayerLookDirection().getOpposite()), Properties.HORIZONTAL_FACING);
            FACING_PLAYER_PLACEMENT = new PlacementConfig((ctx, block) -> block.getDefaultState().with(Properties.HORIZONTAL_FACING, ctx.getPlayerLookDirection()), Properties.HORIZONTAL_FACING);
            HALF_SLAB = new PlacementConfig((ctx, block) -> {
                BlockPos blockPos = ctx.getBlockPos();
                BlockState blockState = ctx.getWorld().getBlockState(blockPos);
                if(blockState.isOf(block)){
                    return blockState.with(Properties.SLAB_TYPE, SlabType.DOUBLE).with(Properties.WATERLOGGED, false);
                }else{
                    FluidState fluidState = ctx.getWorld().getFluidState(blockPos);
                    BlockState blockState2 = block.getDefaultState().with(Properties.SLAB_TYPE, SlabType.BOTTOM).with(Properties.WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
                    Direction direction = ctx.getSide();
                    return direction != Direction.DOWN && (direction == Direction.UP || !(ctx.getHitPos().y - (double)blockPos.getY() > 0.5D)) ? blockState2 : blockState2.with(Properties.SLAB_TYPE, SlabType.TOP);
                }
            }, Properties.SLAB_TYPE, Properties.WATERLOGGED);
            STAIRS = new PlacementConfig((ctx, block) -> {
                Direction direction = ctx.getSide();
                BlockPos blockPos = ctx.getBlockPos();
                FluidState fluidState = ctx.getWorld().getFluidState(blockPos);
                BlockState blockState = block.getDefaultState().with(Properties.HORIZONTAL_FACING, ctx.getPlayerFacing()).with(Properties.BLOCK_HALF, direction != Direction.DOWN && (direction == Direction.UP || !(ctx.getHitPos().y - (double)blockPos.getY() > 0.5D)) ? BlockHalf.BOTTOM : BlockHalf.TOP).with(Properties.WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
                return blockState.with(Properties.STAIR_SHAPE, getStairShape(blockState, ctx.getWorld(), blockPos));
            }, Properties.BLOCK_HALF, Properties.HORIZONTAL_FACING, Properties.STAIR_SHAPE, Properties.WATERLOGGED);
            WALLS = new PlacementConfig((ctx, block) -> {
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
                boolean c_south = shouldConnectTo(blockStateNorth, blockStateNorth.isSideSolidFullSquare(worldView, north, Direction.SOUTH), Direction.SOUTH);
                boolean c_west = shouldConnectTo(blockStateEast, blockStateEast.isSideSolidFullSquare(worldView, east, Direction.WEST), Direction.WEST);
                boolean c_north = shouldConnectTo(blockStateSouth, blockStateSouth.isSideSolidFullSquare(worldView, south, Direction.NORTH), Direction.NORTH);
                boolean c_east = shouldConnectTo(blockStateWest, blockStateWest.isSideSolidFullSquare(worldView, west, Direction.EAST), Direction.EAST);
                BlockState isWaterLogged = block.getDefaultState().with(Properties.WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
                return getStateFromNeighbours(worldView, isWaterLogged, up, blockStateUp, c_south, c_west, c_north, c_east);
            }, UP, EAST_SHAPE, NORTH_SHAPE, SOUTH_SHAPE, WEST_SHAPE, Properties.WATERLOGGED);


            selectedPlacementConfig = DEFAULT_PLACEMENT;
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


            TALL_POST_SHAPE = Block.createCuboidShape(7.0D, 0.0D, 7.0D, 9.0D, 16.0D, 9.0D);
            TALL_NORTH_SHAPE = Block.createCuboidShape(7.0D, 0.0D, 0.0D, 9.0D, 16.0D, 9.0D);
            TALL_SOUTH_SHAPE = Block.createCuboidShape(7.0D, 0.0D, 7.0D, 9.0D, 16.0D, 16.0D);
            TALL_WEST_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 7.0D, 9.0D, 16.0D, 9.0D);
            TALL_EAST_SHAPE = Block.createCuboidShape(7.0D, 0.0D, 7.0D, 16.0D, 16.0D, 9.0D);
        }
    }

}
