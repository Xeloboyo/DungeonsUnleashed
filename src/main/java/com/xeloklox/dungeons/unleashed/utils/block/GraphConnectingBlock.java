package com.xeloklox.dungeons.unleashed.utils.block;

import com.xeloklox.dungeons.unleashed.utils.block.GraphConnectConfig.*;
import com.xeloklox.dungeons.unleashed.utils.lambda.*;
import net.fabricmc.fabric.api.object.builder.v1.block.*;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.entity.*;
import net.minecraft.item.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import org.jetbrains.annotations.*;

public abstract class GraphConnectingBlock extends BasicBlock implements BlockEntityProvider{
    public ConnectConfigManager connectionConfig;

    public GraphConnectingBlock(Material material, Cons< ConnectConfigManager> connectionConfigFunc, Func<FabricBlockSettings, FabricBlockSettings> settingsfunc){
        super(material, settingsfunc);
        connectionConfig = new ConnectConfigManager();
        connectionConfigFunc.get(connectionConfig);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, getEntityType(), (world1, pos, state1, be) -> be.tick(world1, pos, state1, be));
    }
    public abstract <T extends GraphConnectingEntity> BlockEntityType<T> getEntityType();

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack){
        super.onPlaced(world, pos, state, placer, itemStack);
        if(!world.isClient){
            ((GraphConnectingEntity) world.getBlockEntity(pos)).initalise();
        }
    }

    @Override
    public void onDestroyed(BlockState state, World world, BlockPos pos){
        ((GraphConnectingEntity) world.getBlockEntity(pos)).disconnect();
    }
}
