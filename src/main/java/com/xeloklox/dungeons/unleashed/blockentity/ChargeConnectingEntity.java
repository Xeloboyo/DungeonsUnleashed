package com.xeloklox.dungeons.unleashed.blockentity;

import com.xeloklox.dungeons.unleashed.blocks.*;
import com.xeloklox.dungeons.unleashed.utils.*;
import com.xeloklox.dungeons.unleashed.utils.animation.*;
import com.xeloklox.dungeons.unleashed.utils.animation.Interpolations.*;
import com.xeloklox.dungeons.unleashed.utils.lambda.Boolf.*;
import com.xeloklox.dungeons.unleashed.utils.lambda.Cons.*;
import net.fabricmc.fabric.api.block.entity.*;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.state.property.*;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.*;

import java.util.*;

public abstract class ChargeConnectingEntity extends BlockEntity implements BlockEntityClientSerializable{
    //server
    boolean jarUpdate = true;
    //synced
    boolean[] jarAttach;
    protected int charge = 0;
    //client
    float[] jarAttachAnimationTick;
    ParameterMap[] animation;
    public ChargeConnectorBlock block;

    @Override
    public NbtCompound writeNbt(NbtCompound nbt){
        nbt.putInt("attach",Utils.toIntMask(jarAttach));
        return super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt){
        Utils.fromIntMask(nbt.getInt("attach"),jarAttach);
        super.readNbt(nbt);
    }

    public ChargeConnectingEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, ChargeConnectorBlock block){
        super(type, pos, state);
        this.block = block;
        jarAttach = new boolean[block.connectionRelative.length];
        jarAttachAnimationTick = new float[block.connectionRelative.length];
        animation = new ParameterMap[block.connectionRelative.length];
        for(int i=0;i<jarAttach.length;i++){
            animation[i] = new ParameterMap();
            animation[i].add("rotation",0f);
            animation[i].add("extend",Utils.pixels(-5));
            animation[i].addChainedInterpolator(SingularInterpolateType.EXPONENTIAL2,0.2f,"rotation",new FloatInterpolate(),
                FrameState.get(0f,0.6f),
                FrameState.get(180f,0.4f)
            );
            animation[i].addChainedInterpolator(SingularInterpolateType.EXPONENTIAL2,0.2f,"extend",new FloatInterpolate(),
                FrameState.get(0f,0.6f),
                FrameState.get(0f,0.4f)
            );
        }
    }

    public void updateAnimations(){
        for(int i=0;i<jarAttach.length;i++){
            jarAttachAnimationTick[i] += (jarAttach[i]?1:-1)*0.02f;
            jarAttachAnimationTick[i] = MathHelper.clamp(jarAttachAnimationTick[i],0f,1f);
            animation[i].update(jarAttachAnimationTick[i]);
        }
    }

    public void updateConnections(){
        charge = getCharge();
    }
    public boolean addCharge(){
        return findChargeAccessor((ic,bp,i)->ic.addCharge(world,bp,1))!=null;
    }

    public boolean addCharge(int index){
        return findChargeAccessor((ic,bp,i)->index==i&&ic.addCharge(world,bp,1))!=null;
    }
    public boolean isFull(int index){
        return findChargeAccessor((ic,bp,i)->index==i&&ic.getCharge(world,bp)>=ic.maxCharge(world,bp))!=null;
    }

    public boolean drainCharge(){
        return findChargeAccessor((ic,bp,i)->ic.drainCharge(world,bp))!=null;
    }
    public boolean drainCharge(int index){
        return findChargeAccessor((ic,bp,i)->index==i&&ic.drainCharge(world,bp))!=null;
    }
    public int getCharge(){
        final Wrapper<Integer> accum = new Wrapper<>(0);
        eachChargeAccessor((ic,bp,i)->{accum.val+=ic.getCharge(world,bp);});
        return accum.val;
    }
    public int getCharge(int index){
        final Wrapper<BlockPos> accum = new Wrapper<>(null);
        return findChargeAccessor((ic,bp,i)->{accum.val=bp; return index==i;}).getCharge(world,accum.val);
    }
    public void eachChargeAccessor(Cons3<IChargeAccessor,BlockPos, Integer> cons){
        findChargeAccessor((ic,p,i)->{ cons.get(ic,p,i); return false;});
    }
    public IChargeAccessor findChargeAccessor(Boolf3<IChargeAccessor,BlockPos,Integer> cons){
        BlockState state = world.getBlockState(pos);
        for(int i=0;i<jarAttach.length;i++){
            Vec3i v = Mathf.relativeDirectionHorz(world.getBlockState(pos).get(Properties.HORIZONTAL_FACING),((ChargeConnectorBlock)state.getBlock()).connectionRelative[i]);
            BlockPos bp = pos.add(v);
            BlockState adjacent = world.getBlockState(bp);
            if(adjacent.getBlock() instanceof IChargeAccessor chargeStorage){
                if(cons.get(chargeStorage,bp,i)){
                    return chargeStorage;
                }
            }
        }
        return null;
    }

    public void setJarAttach(boolean[] jarAttach){
       this.jarAttach = jarAttach;
        jarUpdate = true;
    }

    public float[] getJarAttachAnimationTick(){
        return jarAttachAnimationTick;
    }
    public ParameterMap[] getAnimation(){
        return animation;
    }

    //received from server
   @Override
   public void fromClientTag(NbtCompound tag){
       Utils.fromIntMask(tag.getInt("attach"),jarAttach);
   }

   //send to client
   @Override
   public NbtCompound toClientTag(NbtCompound tag){
       tag.putInt("attach",Utils.toIntMask(jarAttach));
       return tag;
   }



}
