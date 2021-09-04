package com.xeloklox.dungeons.unleashed.blocks.graph.charge;

import com.xeloklox.dungeons.unleashed.*;
import com.xeloklox.dungeons.unleashed.blockentity.*;
import com.xeloklox.dungeons.unleashed.blocks.*;
import com.xeloklox.dungeons.unleashed.utils.block.GraphConnectConfig.*;
import com.xeloklox.dungeons.unleashed.utils.block.*;
import com.xeloklox.dungeons.unleashed.utils.lambda.*;
import net.fabricmc.fabric.api.object.builder.v1.block.*;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.item.*;
import net.minecraft.state.StateManager.*;
import net.minecraft.state.property.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import org.jetbrains.annotations.*;

public class ChargeStorageTankBlock extends GraphConnectingBlock implements IAffectedByLightning, Oxidizable{
    public static final BooleanProperty UP = BooleanProperty.of("up");
    public static final BooleanProperty DOWN = BooleanProperty.of("down");
    public ChargeStorageTankBlock(Material material, Func<FabricBlockSettings, FabricBlockSettings> settingsfunc){
        super(material, manager -> {
            manager.add(new SidedConnectConfig<>(ChargeStorageGraph.class,Direction.UP,Direction.DOWN));
        }, settingsfunc);
    }
    @Override
    public <T extends GraphConnectingEntity> BlockEntityType<T> getEntityType(){
        return (BlockEntityType<T>)ModBlocks.CHARGE_CELL_STORAGE_ENTITY.get();
    }

    @Override
    protected void appendProperties(Builder<Block, BlockState> builder){
        super.appendProperties(builder);
        builder.add(UP);
        builder.add(DOWN);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx){
        BlockState newstate= super.getPlacementState(ctx);
        BlockView blockView = ctx.getWorld();
        boolean up = canConnect(blockView.getBlockState(ctx.getBlockPos().up()),blockView,ctx.getBlockPos(),ctx.getBlockPos().up());
        boolean down = canConnect(blockView.getBlockState(ctx.getBlockPos().down()),blockView,ctx.getBlockPos(),ctx.getBlockPos().down());
        newstate= newstate.with(DOWN,down);
        newstate= newstate.with(UP,up);
        return newstate;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos){
        BlockState newstate= super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
        if(newstate==state){
            boolean canconnect = canConnect(neighborState,world,pos,neighborPos);
            if(direction.equals(Direction.DOWN)){
                newstate= newstate.with(DOWN,canconnect);
            }else if(direction.equals(Direction.UP)){
                newstate= newstate.with(UP,canconnect);
            }
        }
        return newstate;
    }

    public boolean canConnect(BlockState other,  BlockView world, BlockPos pos, BlockPos neighborPos){
        if(other.getBlock() instanceof ChargeStorageTankBlock gb){
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state){
        return new ChargeStorageTankEntity(pos,state);
    }

    @Override
    public void onStruck(World world, BlockPos pos){
        if(world.isClient){return;}
        if(world.getBlockEntity(pos) instanceof ChargeStorageTankEntity ce){
            if(ce.getCharge()<ce.getChargeCapacity()){
                ce.setCharge(ce.getCharge()+1);
            }
        }
    }

    @Override
    public OxidizationLevel getDegradationLevel(){
        return OxidizationLevel.UNAFFECTED;
    }
}
