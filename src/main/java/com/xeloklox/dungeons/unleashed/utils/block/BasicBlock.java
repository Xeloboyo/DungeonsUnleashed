package com.xeloklox.dungeons.unleashed.utils.block;

import com.xeloklox.dungeons.unleashed.*;
import com.xeloklox.dungeons.unleashed.blockentity.*;
import com.xeloklox.dungeons.unleashed.blocks.*;
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
import net.minecraft.state.property.Property;
import net.minecraft.util.*;
import net.minecraft.util.hit.*;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.*;
import net.minecraft.world.*;
import org.jetbrains.annotations.*;

import java.util.stream.*;

/**
 * Class that tries to cover alot of the general vanilla cases for blocks
 *
 *
 *
 * **/
public class BasicBlock extends Block implements Waterloggable{
    public static PlacementConfig selectedPlacementConfig;
    PlacementConfig placementConfig = DEFAULT_PLACEMENT;
    boolean waterloggable = false;
    public Func<FabricBlockSettings,FabricBlockSettings> settings;
    public BasicBlock(Material material, Func<FabricBlockSettings,FabricBlockSettings> settingsfunc){
        this(material,settingsfunc,block->{});
    }
    public BasicBlock(Material material, Func<FabricBlockSettings,FabricBlockSettings> settingsfunc, Cons<BasicBlock> additionalSettings){
        super(settingsfunc.get(FabricBlockSettings.of(material)));
        settings= settingsfunc;
        additionalSettings.get(this);
        this.placementConfig = selectedPlacementConfig;
        selectedPlacementConfig = DEFAULT_PLACEMENT;
        waterloggable = this.placementConfig.hasProperty(Properties.WATERLOGGED);
    }

    public BasicBlock copy(){
        return new BasicBlock(this.material,this.settings);
    }

    public <T extends BlockEntity> Class<T> getEntityClass(){
        return null;
    }

    @Nullable
    @Override
    public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos){
        boolean isGUI = getEntityClass()!=null && NamedScreenHandlerFactory.class.isAssignableFrom(getEntityClass());
        if(isGUI){
            BlockEntity blockEntity = world.getBlockEntity(pos);
            return blockEntity instanceof NamedScreenHandlerFactory ? (NamedScreenHandlerFactory)blockEntity : null;
        }else{
            return super.createScreenHandlerFactory(state, world, pos);
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit){
        boolean isGUI = getEntityClass()!=null && NamedScreenHandlerFactory.class.isAssignableFrom(getEntityClass());
        if (!world.isClient && isGUI) {
            NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);
            if (screenHandlerFactory != null) {
                //With this call the server will request the client to open the appropriate Screenhandler
                player.openHandledScreen(screenHandlerFactory);
                System.out.println("screen opening!");
            }
            return ActionResult.SUCCESS;
        }
        return isGUI?ActionResult.SUCCESS:ActionResult.PASS;
    }


    @Override
    protected void appendProperties(Builder<Block, BlockState> builder){
        super.appendProperties(builder);
        for(var a: selectedPlacementConfig.properties){
            builder.add(a);
        }
    }

    @Nullable
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> checkType(BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker) {
      return expectedType == givenType ? (BlockEntityTicker<A>)ticker : null;
    }

    public void onDisturbed(BlockState state, World world, BlockPos pos){ }
    public void onDestroyed(BlockState state, World world, BlockPos pos){ }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify){
        super.onBlockAdded(state, world, pos, oldState, notify);
        onDisturbed(state,world,pos);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack){
        super.onPlaced(world, pos, state, placer, itemStack);
        onDisturbed(state,world,pos);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify){
        super.neighborUpdate(state, world, pos, block, fromPos, notify);
        onDisturbed(state,world,pos);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved){
        if(newState.getBlock() != this){
            onDisturbed(state,world,pos);
            onDestroyed(state,world,pos);
        }else{
            if(getEntityClass()!=null && Inventory.class.isAssignableFrom(getEntityClass())){
                BlockEntity blockEntity = world.getBlockEntity(pos);
                if (blockEntity instanceof Inventory inventory) {
                    ItemScatterer.spawn(world, pos, inventory);
                    // update comparators
                    world.updateComparators(pos,this);
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
        return placementConfig.placementState.get(ctx,this);
    }

    public void setPlacementConfig(PlacementConfig placementConfig){
        this.placementConfig = placementConfig;
    }

    public FluidState getFluidState(BlockState state) {
        if (waterloggable){
            return state.get(Properties.WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
        }else{
            return super.getFluidState(state);
        }
    }

    public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
        if (waterloggable){
            if(this.placementConfig == HALF_SLAB){
                return state.get(Properties.SLAB_TYPE) != SlabType.DOUBLE && Waterloggable.super.tryFillWithFluid(world, pos, state, fluidState);
            }else{
                return  Waterloggable.super.tryFillWithFluid(world, pos, state, fluidState);
            }
        }
        return false;
    }

    public boolean canFillWithFluid(BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
        if (waterloggable){
            if(this.placementConfig == HALF_SLAB){
                return state.get(Properties.SLAB_TYPE) != SlabType.DOUBLE && Waterloggable.super.canFillWithFluid(world, pos, state, fluid);
            }else{
                return  Waterloggable.super.canFillWithFluid(world, pos, state, fluid);
            }
        }
        return false;
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (waterloggable && state.get(Properties.WATERLOGGED)) {
           world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        if(this.placementConfig == STAIRS){
            return direction.getAxis().isHorizontal() ? (BlockState)state.with(Properties.STAIR_SHAPE, getStairShape(state, world, pos)) : super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    public boolean hasSidedTransparency(BlockState state) {
        if(this.placementConfig == HALF_SLAB){
            return state.get(Properties.SLAB_TYPE) != SlabType.DOUBLE;
        }
        else if(this.placementConfig == STAIRS){
            return true;
        }
        return false;
    }

    public BlockState rotate(BlockState state, BlockRotation rotation){
        if(this.placementConfig == STAIRS){
            rotateStairs(state,rotation);
        }
        return super.rotate(state,rotation);
    }

    public BlockState mirror(BlockState state, BlockMirror mirror) {
        if(this.placementConfig == STAIRS){
            mirrorStairs(super.mirror(state,mirror),state,mirror);
        }
        return super.mirror(state,mirror);
    }


    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
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
        return VoxelShapes.fullCube();
    }


    public static PlacementConfig
        DEFAULT_PLACEMENT,
        PILLAR_PLACEMENT,
        HORIZONTAL_FACING_PLAYER_PLACEMENT,
        FACING_OPPOSITE_PLAYER_PLACEMENT,
        FACING_PLAYER_PLACEMENT,
        STAIRS,
        HALF_SLAB;

    public static class PlacementConfig{
        Func2<ItemPlacementContext,Block,BlockState> placementState;
        Property[] properties;

        public PlacementConfig(Func2<ItemPlacementContext, Block, BlockState> placementState, Property... properties){
            this.placementState = placementState;
            this.properties = properties;
            if(this.properties ==null){
                this.properties  = new Property[]{};
            }
        }
        boolean hasProperty(Property p){
            for(Property p2:properties){
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

    private int getShapeIndexIndex(BlockState state) {
      return ((StairShape)state.get(Properties.STAIR_SHAPE)).ordinal() * 4 + ((Direction)state.get(Properties.HORIZONTAL_FACING)).getHorizontal();
   }


    private static StairShape getStairShape(BlockState state, BlockView world, BlockPos pos){
        Direction direction = state.get(Properties.HORIZONTAL_FACING);
        BlockState blockState = world.getBlockState(pos.offset(direction));
        if(isStairs(blockState) && state.get(Properties.BLOCK_HALF) == blockState.get(Properties.BLOCK_HALF)){
            Direction direction2 = blockState.get(Properties.HORIZONTAL_FACING);
            if(direction2.getAxis() != ((Direction)state.get(Properties.HORIZONTAL_FACING)).getAxis() && isDifferentOrientation(state, world, pos, direction2.getOpposite())){
                if(direction2 == direction.rotateYCounterclockwise()){
                    return StairShape.OUTER_LEFT;
                }

                return StairShape.OUTER_RIGHT;
            }
        }

        BlockState blockState2 = world.getBlockState(pos.offset(direction.getOpposite()));
        if(isStairs(blockState2) && state.get(Properties.BLOCK_HALF) == blockState2.get(Properties.BLOCK_HALF)){
            Direction direction3 = blockState2.get(Properties.HORIZONTAL_FACING);
            if(direction3.getAxis() != ((Direction)state.get(Properties.HORIZONTAL_FACING)).getAxis() && isDifferentOrientation(state, world, pos, direction3)){
                if(direction3 == direction.rotateYCounterclockwise()){
                    return StairShape.INNER_LEFT;
                }

                return StairShape.INNER_RIGHT;
            }
        }

        return StairShape.STRAIGHT;
    }

    private static boolean isDifferentOrientation(BlockState state, BlockView world, BlockPos pos, Direction dir) {
       BlockState blockState = world.getBlockState(pos.offset(dir));
       return !isStairs(blockState) || blockState.get(Properties.HORIZONTAL_FACING) != state.get(Properties.HORIZONTAL_FACING) || blockState.get(Properties.BLOCK_HALF) != state.get(Properties.BLOCK_HALF);
    }

    public static boolean isStairs(BlockState state) {
       return state.getBlock() instanceof StairsBlock;
    }

    public static BlockState rotateStairs(BlockState state, BlockRotation rotation){
        return (BlockState)state.with(Properties.HORIZONTAL_FACING, rotation.rotate((Direction)state.get(Properties.HORIZONTAL_FACING)));
    }

    public static BlockState mirrorStairs(BlockState defaultMirror,BlockState state, BlockMirror mirror){
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
        return (VoxelShape[])IntStream.range(0, 16).mapToObj((i) -> {
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

    //i see why its at the end now
    static {
        if(Globals.bootStrapped){
            DEFAULT_PLACEMENT = new PlacementConfig((ctx,block)->block.getDefaultState());
            PILLAR_PLACEMENT = new PlacementConfig((ctx,block)->block.getDefaultState().with(Properties.AXIS, ctx.getSide().getAxis()) ,Properties.AXIS );
            HORIZONTAL_FACING_PLAYER_PLACEMENT = new PlacementConfig((ctx,block)->block.getDefaultState().with(Properties.HORIZONTAL_FACING, ctx.getPlayerFacing()),Properties.HORIZONTAL_FACING);
            FACING_OPPOSITE_PLAYER_PLACEMENT = new PlacementConfig((ctx,block)->block.getDefaultState().with(Properties.HORIZONTAL_FACING, ctx.getPlayerLookDirection().getOpposite()),Properties.HORIZONTAL_FACING);
            FACING_PLAYER_PLACEMENT = new PlacementConfig((ctx,block)->block.getDefaultState().with(Properties.HORIZONTAL_FACING, ctx.getPlayerLookDirection()),Properties.HORIZONTAL_FACING);
            HALF_SLAB = new PlacementConfig((ctx,block)->{
                BlockPos blockPos = ctx.getBlockPos();
                BlockState blockState = ctx.getWorld().getBlockState(blockPos);
                if (blockState.isOf(block)) {
                    return blockState.with(Properties.SLAB_TYPE, SlabType.DOUBLE).with(Properties.WATERLOGGED, false);
                } else {
                    FluidState fluidState = ctx.getWorld().getFluidState(blockPos);
                    BlockState blockState2 = block.getDefaultState().with(Properties.SLAB_TYPE, SlabType.BOTTOM).with(Properties.WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
                    Direction direction = ctx.getSide();
                    return direction != Direction.DOWN && (direction == Direction.UP || !(ctx.getHitPos().y - (double)blockPos.getY() > 0.5D)) ? blockState2 : (BlockState)blockState2.with(Properties.SLAB_TYPE, SlabType.TOP);
                }
            },Properties.SLAB_TYPE,Properties.WATERLOGGED);
            STAIRS = new PlacementConfig((ctx,block)->{
                Direction direction = ctx.getSide();
                  BlockPos blockPos = ctx.getBlockPos();
                  FluidState fluidState = ctx.getWorld().getFluidState(blockPos);
                  BlockState blockState = block.getDefaultState().with(Properties.HORIZONTAL_FACING, ctx.getPlayerFacing()).with(Properties.BLOCK_HALF, direction != Direction.DOWN && (direction == Direction.UP || !(ctx.getHitPos().y - (double)blockPos.getY() > 0.5D)) ? BlockHalf.BOTTOM : BlockHalf.TOP).with(Properties.WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
                  return blockState.with(Properties.STAIR_SHAPE, getStairShape(blockState, ctx.getWorld(), blockPos));
            },Properties.BLOCK_HALF,Properties.HORIZONTAL_FACING,Properties.STAIR_SHAPE,Properties.WATERLOGGED);


            selectedPlacementConfig  = DEFAULT_PLACEMENT;
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

        }
    }

}
