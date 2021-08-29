package com.xeloklox.dungeons.unleashed.blocks.graph.charge;

import com.xeloklox.dungeons.unleashed.utils.block.*;
import net.minecraft.nbt.*;

public class ChargeStorageGraph extends BlockGraph{
    public int charge;
    public int maxCharge;

    public ChargeStorageGraph(GraphConnector init){
        super(init);
        System.out.println("max charge: "+maxCharge);
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound){
        nbtCompound.putInt("charge",charge);
    }

    @Override
    public void readFromNbt(NbtCompound nbtCompound){
        charge = nbtCompound.getInt("charge");
    }

    @Override
    public void onMerge(BlockGraph other){
        if(other instanceof ChargeStorageGraph cgraph){
            cgraph.charge+=charge;
        }
        charge=0;
    }

    @Override
    public String name(){
        return "charge_graph";
    }

    @Override
    public void onUpdate(){

    }
}
