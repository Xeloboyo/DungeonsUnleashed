package com.xeloklox.dungeons.unleashed.blocks;

import com.xeloklox.dungeons.unleashed.*;
import com.xeloklox.dungeons.unleashed.blockentity.*;
import com.xeloklox.dungeons.unleashed.utils.block.*;
import com.xeloklox.dungeons.unleashed.utils.lambda.*;
import net.fabricmc.fabric.api.object.builder.v1.block.*;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.state.StateManager.*;
import net.minecraft.state.property.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import org.jetbrains.annotations.*;

public class ChargeTransposerBlock extends ChargeConnectorBlock implements BlockEntityProvider{
    public static Vec3f[] transferConnectionPoints = {new Vec3f(0, 0, -1), new Vec3f(0, 0, 1)};
    public static final BooleanProperty FLIPPED = BooleanProperty.of("flipped");

    public ChargeTransposerBlock(Material material, Func<FabricBlockSettings, FabricBlockSettings> settingsfunc, Cons<BasicBlock> additionalSettings){
        super(material, settingsfunc, additionalSettings, transferConnectionPoints);
        setDefaultState(getDefaultState().with(FLIPPED,false));
    }

    @Override
    protected void appendProperties(Builder<Block, BlockState> builder){
        super.appendProperties(builder);
        builder.add(FLIPPED);
    }

    @Override
    public <T extends BlockEntity> Class<T> getEntityClass(){
        return (Class<T>)ChargeTransposerEntity.class;
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify){
        super.neighborUpdate(state, world, pos, block, fromPos, notify);
        if(!world.isClient){
            boolean bl = state.get(FLIPPED);
            if(bl != world.isReceivingRedstonePower(pos)){
                world.setBlockState(pos, state.cycle(FLIPPED), Block.NOTIFY_LISTENERS);
                BlockEntity be = world.getBlockEntity(pos);
                if(be instanceof ChargeConnectingEntity infuserEntity){
                    infuserEntity.updateConnections();
                    infuserEntity.sync();
                }
            }
        }
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state){
        return new ChargeTransposerEntity(pos,state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
            return checkType(type, ModBlocks.CHARGE_TRANSPOSER_ENTITY.get(), (world1, pos, state1, be) -> be.tick(world1, pos, state1, be));
        }

}
