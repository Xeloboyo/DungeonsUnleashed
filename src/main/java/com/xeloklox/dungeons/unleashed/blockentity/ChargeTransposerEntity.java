package com.xeloklox.dungeons.unleashed.blockentity;

import com.xeloklox.dungeons.unleashed.*;
import com.xeloklox.dungeons.unleashed.blockentity.screens.*;
import com.xeloklox.dungeons.unleashed.blocks.*;
import com.xeloklox.dungeons.unleashed.utils.*;
import com.xeloklox.dungeons.unleashed.utils.block.entity.*;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.screen.*;
import net.minecraft.text.*;
import net.minecraft.util.collection.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import org.jetbrains.annotations.*;

public class ChargeTransposerEntity extends ChargeConnectingEntity implements ImplementedInventory, NamedScreenHandlerFactory, BlockEntityTicker<ChargeTransposerEntity>{
    //server
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(1, ItemStack.EMPTY);
    public static final int delay=100;
    public boolean doingItem=false;
    //synced
    public int delayTimer=0;
    public static final int D_DELAY = 0;
    public static final int D_FLIP = 1;
    public static final int D_ITEM = 2;
    public static final int syncedSize = 4;
    final int[] syncedInts = new int[syncedSize];
    private final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) { return syncedInts[index]; }
        @Override
        public void set(int index, int value) { syncedInts[index] = value; }
        @Override
        public int size() { return syncedInts.length; }
    };
    //client
    float transition = 0;


    @Override
    public NbtCompound writeNbt(NbtCompound nbt){
        Inventories.writeNbt(nbt, items);
        nbt.putBoolean("doingItem",doingItem);
        nbt.putInt("delayTimer",delayTimer);
        return super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt){
        Inventories.readNbt(nbt, items);
        doingItem = nbt.getBoolean("doingItem");
        delayTimer = nbt.getInt("delayTimer");
        super.readNbt(nbt);
    }

    public ChargeTransposerEntity(BlockPos pos, BlockState state){
        super(ModBlocks.CHARGE_TRANSPOSER_ENTITY.get(), pos, state, (ChargeConnectorBlock)state.getBlock());
    }

    @Override
    public DefaultedList<ItemStack> getItems(){
        return items;
    }

    public void clientTick(World world, BlockPos pos, BlockState state, ChargeTransposerEntity blockEntity){
        updateAnimations();

    }

    public void serverTick(World world, BlockPos pos, BlockState state, ChargeTransposerEntity blockEntity){
        if(jarUpdate){
            updateConnections();
        }

        delayTimer++;
        syncedInts[D_FLIP] = state.get(ChargeTransposerBlock.FLIPPED) ? 1 : 0;
        syncedInts[D_DELAY] = delayTimer;
        syncedInts[D_ITEM] = doingItem ? 1 : 0;



        if(delayTimer>=delay){

            delayTimer=0;
            boolean flipped = state.get(ChargeTransposerBlock.FLIPPED);
            int from = flipped?1:0;
            int to = 1-from;


            if(doingItem){
                if(isFull(to)){
                    delayTimer=delay;
                    return;
                }
                doingItem=false;
                if(!addCharge(to)){
                    addCharge(from);
                }
            }else{
                if(drainCharge(from)){
                    if(!addCharge(to)){
                        addCharge(from);
                    }
                }
            }
            if(!getStack(0).isEmpty()&&getStack(0).getItem() == ModItems.THUNDERSTONE.get()){
                removeStack(0,1);
                doingItem = true;
            }
        }
    }


    @Override
    public void tick(World world, BlockPos pos, BlockState state, ChargeTransposerEntity blockEntity){
        if(world.isClient){
            clientTick(world,pos,state,blockEntity);
        }else{
            serverTick(world,pos,state,blockEntity);
        }
    }


    @Override
    public Text getDisplayName(){
        return new TranslatableText(getCachedState().getBlock().getTranslationKey());
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player){
        return new ChargeTransposerScreenHandler(syncId,inv,this,propertyDelegate);
    }
}
