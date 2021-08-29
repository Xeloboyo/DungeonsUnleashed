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
            return (Boolean)state.get(Properties.WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
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
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    public boolean hasSidedTransparency(BlockState state) {
        if(this.placementConfig == HALF_SLAB){
            return state.get(Properties.SLAB_TYPE) != SlabType.DOUBLE;
        }
        return false;
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
        return VoxelShapes.fullCube();
    }


    protected static VoxelShape
        BOTTOM_SHAPE,
        TOP_SHAPE;
    public static PlacementConfig
        DEFAULT_PLACEMENT,
        PILLAR_PLACEMENT,
        HORIZONTAL_FACING_PLAYER_PLACEMENT,
        FACING_OPPOSITE_PLAYER_PLACEMENT,
        FACING_PLAYER_PLACEMENT,
        HALF_SLAB;
    static {
        if(Globals.bootStrapped){
            DEFAULT_PLACEMENT = new PlacementConfig((ctx,block)->block.getDefaultState());
            PILLAR_PLACEMENT = new PlacementConfig((ctx,block)->block.getDefaultState().with(Properties.AXIS, ctx.getSide().getAxis()) ,Properties.AXIS );
            HORIZONTAL_FACING_PLAYER_PLACEMENT = new PlacementConfig((ctx,block)->block.getDefaultState().with(Properties.HORIZONTAL_FACING, ctx.getPlayerFacing()),Properties.HORIZONTAL_FACING);
            FACING_OPPOSITE_PLAYER_PLACEMENT = new PlacementConfig((ctx,block)->block.getDefaultState().with(Properties.FACING, ctx.getPlayerLookDirection().getOpposite()),Properties.FACING);
            FACING_PLAYER_PLACEMENT = new PlacementConfig((ctx,block)->block.getDefaultState().with(Properties.FACING, ctx.getPlayerLookDirection()),Properties.FACING);
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
            selectedPlacementConfig  = DEFAULT_PLACEMENT;
            BOTTOM_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
            TOP_SHAPE = Block.createCuboidShape(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D);
        }
    }
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


}
