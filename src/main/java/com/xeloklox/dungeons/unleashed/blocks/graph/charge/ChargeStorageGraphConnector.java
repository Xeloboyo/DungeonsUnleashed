package com.xeloklox.dungeons.unleashed.blocks.graph.charge;

import com.xeloklox.dungeons.unleashed.utils.block.*;

public class ChargeStorageGraphConnector extends GraphConnector<ChargeStorageGraph>{
    int chargeCapacity;
    int tempCharge;
    //temp charge..

    public ChargeStorageGraphConnector(GraphConnectingEntity blockEntity, int chargeCapacity){
        super(blockEntity,graphConnector -> {
            //fk me
            ((ChargeStorageGraphConnector)graphConnector).chargeCapacity = chargeCapacity;
        });
    }

    @Override
    public ChargeStorageGraph newGraph(){
        return new ChargeStorageGraph(this);
    }

    @Override
    public void onAdd(){
        graph.maxCharge+=this.chargeCapacity;
        graph.charge += tempCharge;
        tempCharge = 0;
        System.out.println("Graph charge is now: "+graph.maxCharge);
    }

    @Override
    public void onRemove(){
        graph.maxCharge-=this.chargeCapacity;
        if(graph.charge> graph.maxCharge){
            tempCharge = graph.charge-graph.maxCharge;
            graph.charge = graph.maxCharge;
        }
    }
}
