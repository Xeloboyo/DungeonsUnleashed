package com.xeloklox.dungeons.unleashed.blocks.graph.charge;

import com.xeloklox.dungeons.unleashed.*;
import com.xeloklox.dungeons.unleashed.utils.block.*;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.util.math.*;

public class ChargeStoragePortEntity extends GraphConnectingEntity{
    public ChargeStoragePortEntity(BlockPos pos, BlockState state){
        super(ModBlocks.CHARGE_CELL_PORT_ENTITY.get(), pos, state);
    }

    @Override
    public void intialiseConnectors(){
        connections.add(ChargeStorageGraph.class,new ChargeStorageGraphConnector(this,1));
    }

    public int getChargeCapacity(){
        return connections.get(ChargeStorageGraph.class).graph.maxCharge;
    }
}
