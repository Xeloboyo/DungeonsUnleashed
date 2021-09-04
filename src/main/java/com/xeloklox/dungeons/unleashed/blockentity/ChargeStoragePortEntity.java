package com.xeloklox.dungeons.unleashed.blockentity;

import com.xeloklox.dungeons.unleashed.*;
import com.xeloklox.dungeons.unleashed.blocks.graph.charge.*;
import com.xeloklox.dungeons.unleashed.utils.*;
import com.xeloklox.dungeons.unleashed.utils.block.*;
import com.xeloklox.dungeons.unleashed.utils.models.RenderableModel.*;
import net.fabricmc.fabric.api.block.entity.*;
import net.minecraft.block.*;
import net.minecraft.nbt.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import org.mini2Dx.gdx.utils.*;

public class ChargeStoragePortEntity extends GraphConnectingEntity implements BlockEntityClientSerializable{
    //server

    //synced
    public boolean bottomConnected = false;
    //client
    public ObjectMap<String, BoneTranslationParameters> animationParams = null;
    public float contractBase = 0;

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
    public int getCharge(){
            return connections.get(ChargeStorageGraph.class).graph.charge;
        }
    public int setCharge(int charge){
                return connections.get(ChargeStorageGraph.class).graph.charge=charge;
            }

    @Override
    public void serverUpdate(World world, BlockPos pos, BlockState state, GraphConnectingEntity blockEntity){
        super.serverUpdate(world, pos, state, blockEntity);
    }

    @Override
    public void onConnect(GraphConnector bg){
        if(bg.blockEntity.getPos().getY()<getPos().getY()){
            bottomConnected = true;
            sync();
        }
    }

    @Override
    public void onDisconnect(GraphConnector bg){
        if(bg.blockEntity.getPos().getY()<getPos().getY()){
            bottomConnected = false;
            sync();
        }
    }

    @Override
    public void clientUpdate(World world, BlockPos pos, BlockState state, GraphConnectingEntity blockEntity){
        contractBase = Mathf.approach(contractBase,bottomConnected?1:0,0.03f);

    }

    @Override
    public void fromClientTag(NbtCompound tag){
        bottomConnected = tag.getBoolean("bottomConnected");
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag){
        tag.putBoolean("bottomConnected",bottomConnected);
        return tag;
    }
}
