package com.xeloklox.dungeons.unleashed.blockentity;

import com.xeloklox.dungeons.unleashed.*;
import com.xeloklox.dungeons.unleashed.blockentity.screens.*;
import com.xeloklox.dungeons.unleashed.blocks.*;
import com.xeloklox.dungeons.unleashed.utils.*;
import com.xeloklox.dungeons.unleashed.utils.animation.*;
import com.xeloklox.dungeons.unleashed.utils.animation.Interpolations.*;
import com.xeloklox.dungeons.unleashed.utils.block.entity.*;
import net.fabricmc.fabric.api.block.entity.*;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.particle.*;
import net.minecraft.screen.*;
import net.minecraft.state.property.*;
import net.minecraft.text.*;
import net.minecraft.util.collection.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import org.jetbrains.annotations.*;
import org.mini2Dx.gdx.utils.*;

public class InfuserEntity extends ChargeConnectingEntity implements  SegmentedInventory, NamedScreenHandlerFactory, BlockEntityTicker<InfuserEntity>{
    public static final int powerPerCharge = 100;
    final float delayBetweenInfusion = 40;
    int infuseDelay=5;

    //server
    int infuseProgress=0;
    int remainingPowerCharge = 0;
    InfuserRecipe currentRecipe = null;
    boolean readyToMove = false;
    float delayTimer = 0;
    //synced
    public boolean working =false;
    public ItemStack processingStack = ItemStack.EMPTY;
    //client
    public float spin=0,rise=0;


    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(7, ItemStack.EMPTY);
    public static InfuserRecipe[] recipes = {
        new InfuserRecipe(Items.ENDER_PEARL, ModItems.UNSTABLE_ENDER_PEARL.get(), 100),
        new InfuserRecipe(Items.REDSTONE, Items.GUNPOWDER, 50),
        new InfuserRecipe(Items.QUARTZ, Items.AMETHYST_SHARD, 200),
        new InfuserRecipe(Items.COAL, Items.DIAMOND, 500)
    };
    public static final int syncedSize = 6;
    public static final int D_POWERSTATUS = 0;
    public static final int D_PROGRESS = 1;
    public static final int D_TOTAL = 2;
    public static final int D_CHARGELEVEL = 3;
    final int[] syncedInts = new int[syncedSize];

    private final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) { return syncedInts[index]; }
        @Override
        public void set(int index, int value) { syncedInts[index] = value; }
        @Override
        public int size() { return syncedInts.length; }
    };


    public InfuserEntity(BlockPos pos, BlockState state){
        super(ModBlocks.INFUSER_ENTITY.get(), pos, state, (ChargeConnectorBlock)state.getBlock());
        addSegment(InventorySegment.of("input",0,1,2).insertableFrom(Direction.EAST,Direction.NORTH,Direction.SOUTH,Direction.WEST));
        addSegment(InventorySegment.of("output",4,5,6).extractableFrom(Direction.DOWN));
        addSegment(InventorySegment.of("processing",3).maxItems(1).syncOnChange(true));

    }


    @Override
    public NbtCompound writeNbt(NbtCompound nbt){
        super.writeNbt(nbt);
        nbt.putInt("infuseProgress",infuseProgress);
        nbt.putFloat("delayTimer",delayTimer);
        nbt.putInt("remainingPowerCharge",remainingPowerCharge);
        Inventories.writeNbt(nbt, items);
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt){
        super.readNbt(nbt);
        delayTimer = nbt.getFloat("delayTimer");
        infuseProgress = nbt.getInt("infuseProgress");
        remainingPowerCharge = nbt.getInt("remainingPowerCharge");
        Inventories.readNbt(nbt, items);
    }

    public void tick(World world, BlockPos pos, BlockState state, InfuserEntity be) {
        if(world.isClient){
            be.updateClient(world,pos,state);
        }else{
            be.updateServer(world, pos, state);
        }
    }

    public void updateCharge(World world, BlockPos pos, BlockState state){
        while(remainingPowerCharge<=0){
            int powerget  = drainCharge()?powerPerCharge:0;;
            remainingPowerCharge += powerget;
            if(powerget==0){
                break;
            }
        }
        updateConnections();
        syncedInts[D_POWERSTATUS] =charge;
    }
    public void updateClient(World world, BlockPos pos, BlockState state){
        updateAnimations();
        rise += (Utils.pixels(processingStack.isEmpty()?0:2)-rise)*0.1f;
        if(working && Mathf.randFloat(10)<1){
            Mathf.randVec3((rx, ry, rz)->{
                getWorld().addParticle(ParticleTypes.ENCHANT,
                (double)getPos().getX()+0.5, (double)getPos().getY()+1.5, (double)getPos().getZ()+0.5,
                    rx, ry-0.5, rz);
            });
        }
    }
    public void updateServer(World world, BlockPos pos, BlockState state){
        if(jarUpdate){
            updateCharge(world,pos,state);
        }
        syncedInts[D_PROGRESS] = (infuseProgress);
        syncedInts[D_CHARGELEVEL] = (int)(100f* remainingPowerCharge/powerPerCharge);

        int processingslot = this.getSegment("processing").getAnyFilledSlot();
        if(currentRecipe!=null){
            if(processingslot==-1){
                currentRecipe=null; // welp its empty
                //refund the energy :o
                remainingPowerCharge+=infuseProgress;
            }else{
                syncedInts[D_TOTAL] = currentRecipe.energy;
                if(isPowered() && infuseProgress < currentRecipe.energy){
                    if(world.getTime() % infuseDelay == 0){
                        remainingPowerCharge -= 1;
                        infuseProgress += 1;
                        if(remainingPowerCharge <= 0){
                            jarUpdate = true;
                        }
                    }
                }
                if(infuseProgress>=currentRecipe.energy){
                    // all done, move it to output...
                    remainingPowerCharge+=infuseProgress-currentRecipe.energy;
                    setStack(processingslot,currentRecipe.getOutput());
                    infuseProgress=0;
                    currentRecipe=null;
                    readyToMove = true;
                    delayTimer = delayBetweenInfusion;
                    this.sync();
                }
            }
        }else{
            syncedInts[D_TOTAL] = 0;
            if(delayTimer>0){
                delayTimer--;
                if(delayTimer<=0 && readyToMove){
                    if(processingslot!=-1){
                        tryTransferIntoSegment(this.getSegment("output"), processingslot);
                        this.sync();
                    }
                    readyToMove = false;
                }
            }else{
                if(processingslot != -1){ // somethings there but im not set to craft anything
                    ItemStack stack = getStack(processingslot);
                    for(InfuserRecipe recipe : recipes){
                        if(recipe.input.equals(stack.getItem())){
                            currentRecipe = recipe;
                            jarUpdate = true;
                            this.sync();
                            break;
                        }
                    }
                }else{
                    // nothings there, see if input has something
                    int slot = this.getSegment("input").getFirstSlot(item -> {
                        for(InfuserRecipe recipe : recipes){
                            if(recipe.input.equals(item.getItem())){
                                return true;
                            }
                        }
                        return false;
                    });
                    if(slot != -1){
                        tryTransferIntoSegment(this.getSegment("processing"), slot);
                    }
                }
            }
        }
    }

    public boolean isPowered(){
        return syncedInts[D_POWERSTATUS]>0 || remainingPowerCharge>0;
    }

    @Override
    public DefaultedList<ItemStack> getItems(){
        return items;
    }

    //received from server
    @Override
    public void fromClientTag(NbtCompound tag){
        super.fromClientTag(tag);
        working = tag.getBoolean("working");
        processingStack = ItemStack.fromNbt(tag.getCompound("item"));
    }

    //send to client
    @Override
    public NbtCompound toClientTag(NbtCompound tag){
        tag = super.toClientTag(tag);
        NbtCompound item = new NbtCompound();
        getStack(getSegment("processing").slots[0]).writeNbt(item);
        tag.put("item",item);
        tag.putBoolean("working",currentRecipe!=null &&  isPowered() );
        return tag;
    }

    Array<InventorySegment> segments;
    @Override
    public Array<InventorySegment> getConfig(){
        return segments;
    }

    @Override
    public void setConfig(Array<InventorySegment> s){
        segments=s;
    }

    @Override
    public void onChange(int slot){
        this.sync();
        if(readyToMove && getSegment(slot).name.equals("processing")){ //in case someone takes items out of the processing slot during the delay,
            readyToMove = false;
        }
    }

    @Override
    public Text getDisplayName(){
        return  new TranslatableText(getCachedState().getBlock().getTranslationKey());
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player){
        return new InfuserScreenHandler(syncId,inv,this,propertyDelegate);
    }

    public static class InfuserRecipe{
        Item input;
        Item output;
        int amount=1;
        int energy;

        public InfuserRecipe(Item input, Item output, int energy){
            this.input = input;
            this.output = output;
            this.energy=energy;
        }

        ItemStack getOutput(){
            return new ItemStack(output,amount);
        }
    }
}
