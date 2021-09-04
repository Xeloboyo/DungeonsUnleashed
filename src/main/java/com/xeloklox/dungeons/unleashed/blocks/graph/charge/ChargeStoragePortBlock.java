package com.xeloklox.dungeons.unleashed.blocks.graph.charge;

import com.xeloklox.dungeons.unleashed.*;
import com.xeloklox.dungeons.unleashed.blockentity.*;
import com.xeloklox.dungeons.unleashed.blocks.*;
import com.xeloklox.dungeons.unleashed.utils.block.*;
import com.xeloklox.dungeons.unleashed.utils.block.GraphConnectConfig.*;
import com.xeloklox.dungeons.unleashed.utils.lambda.*;
import net.fabricmc.fabric.api.object.builder.v1.block.*;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.network.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import net.minecraft.util.hit.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import org.jetbrains.annotations.*;

public class ChargeStoragePortBlock extends GraphConnectingBlock implements IChargeStorage{

    public ChargeStoragePortBlock(Material material, Func<FabricBlockSettings, FabricBlockSettings> settingsfunc){
        super(material, manager -> {
            manager.add(new SidedConnectConfig<>(ChargeStorageGraph.class,Direction.UP,Direction.DOWN));
        }, settingsfunc);
    }
    @Override
    public <T extends GraphConnectingEntity> BlockEntityType<T> getEntityType(){
        return (BlockEntityType<T>)ModBlocks.CHARGE_CELL_PORT_ENTITY.get();
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit){
        BlockEntity be = world.getBlockEntity(pos);
        if(!world.isClient && be instanceof ChargeStoragePortEntity charge_entity){
            String text = "charge=" + charge_entity.getCharge()+"/"+charge_entity.getChargeCapacity();
            world.getServer().getPlayerManager().broadcastChatMessage(new LiteralText(text ), MessageType.SYSTEM, Util.NIL_UUID);
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state){
        return new ChargeStoragePortEntity(pos,state);
    }

    @Override
    public int getCharge(World world, BlockPos pos){
        BlockEntity be = world.getBlockEntity(pos);
        if(!world.isClient && be instanceof ChargeStoragePortEntity charge_entity){
            return charge_entity.getCharge();
        }
        return 0;
    }

    @Override
    public int maxCharge(World world, BlockPos pos){
        BlockEntity be = world.getBlockEntity(pos);
        if(!world.isClient && be instanceof ChargeStoragePortEntity charge_entity){
            return charge_entity.getChargeCapacity();
        }
        return 0;
    }

    @Override
    public void setCharge(World world, BlockPos pos, int charge){
        BlockEntity be = world.getBlockEntity(pos);
        if(!world.isClient && be instanceof ChargeStoragePortEntity charge_entity){
             charge_entity.setCharge(charge);
        }
    }
}
