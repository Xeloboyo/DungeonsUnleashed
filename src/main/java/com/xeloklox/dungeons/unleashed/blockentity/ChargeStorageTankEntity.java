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

public class ChargeStorageTankEntity extends GraphConnectingEntity implements BlockEntityClientSerializable{
    //server
    int pcharge = 0;
    //synced
    public boolean bottomConnected = false;
    public boolean topConnected = false;
    public float chargePortion = 0;
    //client
    public ObjectMap<String, BoneTranslationParameters> animationParamsBottom = null;
    public ObjectMap<String, BoneTranslationParameters> animationParamsTop = null;
    public float contractBase = 0;
    public float contractTop = 0;


    public ChargeStorageTankEntity(BlockPos pos, BlockState state){
        super(ModBlocks.CHARGE_CELL_STORAGE_ENTITY.get(), pos, state);
    }

    @Override
    public void intialiseConnectors(){
        connections.add(ChargeStorageGraph.class,new ChargeStorageGraphConnector(this,8));
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
        int c = getCharge();
        float cp = getChargeCapacity();
        chargePortion = c/cp;
        if(pcharge!=c){
            sync();
        }
        pcharge=c;
    }

    @Override
    public void onConnect(GraphConnector bg){
        if(bg.blockEntity.getPos().getY()<getPos().getY()){
            bottomConnected = true;
        }else{
            topConnected = true;
        }
        sync();
    }

    @Override
    public void onDisconnect(GraphConnector bg){
        if(bg.blockEntity.getPos().getY()<getPos().getY()){
            bottomConnected = false;
        }else{
            topConnected = false;
        }
        sync();
    }

    @Override
    public void clientUpdate(World world, BlockPos pos, BlockState state, GraphConnectingEntity blockEntity){
        contractBase = Mathf.approach(contractBase,bottomConnected?1:0,0.03f);
        contractTop = Mathf.approach(contractTop,topConnected?1:0,0.03f);
    }

    @Override
    public void fromClientTag(NbtCompound tag){
        bottomConnected = tag.getBoolean("bottomConnected");
        topConnected = tag.getBoolean("topConnected");
        chargePortion = tag.getFloat("chargePortion");
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag){
        tag.putBoolean("bottomConnected",bottomConnected);
        tag.putBoolean("topConnected",topConnected);
        tag.putFloat("chargePortion",chargePortion);
        return tag;
    }
}
