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
import net.minecraft.state.*;
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

import java.lang.reflect.*;
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
    BasicBlockModifier modifier = null;

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
        modifier = placementConfig.modifier.get();
        if(modifier!=null){
            modifier.parent=this;
            modifier.onCreate();
        }
    }
    /**
     * StateManager.Builder<Block, BlockState> builder = new StateManager.Builder(this);
     * this.appendProperties(builder);
     * block.appendProperties(builder);
     * BlockInterface.replaceStateManager(this, builder.build(Block::getDefaultState, BlockState::new));
     * **/
    public void replaceStateManager(StateManager<Block, BlockState> stateManager){
        try{
            Field f = getClass().getDeclaredField("stateManager");
            f.setAccessible(true);
            f.set(this, stateManager);
        }catch(NoSuchFieldException | IllegalAccessException e){
            e.printStackTrace();
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
        if(modifier!=null){
            return modifier.canReplace(state,context);
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
            return Waterloggable.super.tryFillWithFluid(world, pos, state, fluidState)
            && (modifier==null || modifier.tryFillWithFluid(world, pos, state, fluidState));
        }
        return false;
    }

    public boolean canFillWithFluid(BlockView world, BlockPos pos, BlockState state, Fluid fluid){
        if(waterloggable){
            return Waterloggable.super.canFillWithFluid(world, pos, state, fluid)
                   && (modifier==null || modifier.canFillWithFluid(world, pos, state, fluid));
        }
        return false;
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos){
        if(waterloggable && state.get(Properties.WATERLOGGED)){
            world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        if(modifier!=null){
            BlockState bs = modifier.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
            if(bs!=null){
                return bs;
            }
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    public boolean hasSidedTransparency(BlockState state){
        if(modifier!=null){
            return modifier.hasSidedTransparency(state);
        }
        return false;
    }

    public BlockState rotate(BlockState state, BlockRotation rotation){
        if(modifier!=null){
            BlockState bs =  modifier.rotate(state, rotation);
            if(bs!=null){
                return bs;
            }
        }
        return super.rotate(state, rotation);
    }

    public BlockState mirror(BlockState state, BlockMirror mirror){
        if(modifier!=null){
            BlockState bs =  modifier.mirror(state, mirror);
            if(bs!=null){
                return bs;
            }
        }
        return super.mirror(state, mirror);
    }


    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context){
        if(modifier!=null){
            VoxelShape bs =  modifier.getOutlineShape(state, world,pos,context);
            if(bs!=null){
                return bs;
            }
        }
        return VoxelShapes.fullCube();
    }

    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context){
        if(modifier!=null){
            VoxelShape bs =  modifier.getCollisionShape(state, world,pos,context);
            if(bs!=null){
                return bs;
            }
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
    //i see why its at the end now
    static{
        if(Globals.bootStrapped){
            DEFAULT_PLACEMENT = new PlacementConfig((ctx, block) -> block.getDefaultState());
            PILLAR_PLACEMENT = new PlacementConfig((ctx, block) -> block.getDefaultState().with(Properties.AXIS, ctx.getSide().getAxis()), Properties.AXIS);
            HORIZONTAL_FACING_PLAYER_PLACEMENT = new PlacementConfig((ctx, block) -> block.getDefaultState().with(Properties.HORIZONTAL_FACING, ctx.getPlayerFacing()), Properties.HORIZONTAL_FACING);
            FACING_OPPOSITE_PLAYER_PLACEMENT = new PlacementConfig((ctx, block) -> block.getDefaultState().with(Properties.HORIZONTAL_FACING, ctx.getPlayerLookDirection().getOpposite()), Properties.HORIZONTAL_FACING);
            FACING_PLAYER_PLACEMENT = new PlacementConfig((ctx, block) -> block.getDefaultState().with(Properties.HORIZONTAL_FACING, ctx.getPlayerLookDirection()), Properties.HORIZONTAL_FACING);
            HALF_SLAB = new PlacementConfig((ctx, block) -> {
                return block.modifier.getPlacementState(ctx);
            }, SlabModifier::new,Properties.SLAB_TYPE, Properties.WATERLOGGED);
            STAIRS = new PlacementConfig((ctx, block) -> {
                return block.modifier.getPlacementState(ctx);
            }, StairsModifier::new,Properties.BLOCK_HALF, Properties.HORIZONTAL_FACING, Properties.STAIR_SHAPE, Properties.WATERLOGGED);
            WALLS = new PlacementConfig((ctx, block) -> {
                return block.modifier.getPlacementState(ctx);
            }, WallsModifier::new,WallsModifier.UP, WallsModifier.EAST_SHAPE, WallsModifier.NORTH_SHAPE, WallsModifier.SOUTH_SHAPE, WallsModifier.WEST_SHAPE, Properties.WATERLOGGED);


            selectedPlacementConfig = DEFAULT_PLACEMENT;
        }
    }

}
