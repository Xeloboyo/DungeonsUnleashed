package com.xeloklox.dungeons.unleashed.blockentity.screens;

import com.xeloklox.dungeons.unleashed.*;
import com.xeloklox.dungeons.unleashed.blockentity.*;
import com.xeloklox.dungeons.unleashed.utils.*;
import com.xeloklox.dungeons.unleashed.utils.block.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.*;
import net.minecraft.server.network.*;
import net.minecraft.util.collection.*;
import org.mini2Dx.gdx.utils.*;

public class ResearchMapScreenHandler extends InventoryScreenHandler{
    private final ScreenHandlerContext context;
    private final PlayerEntity player;
    public final HiddenInventory hiddenInventory;
    public ResearchMapScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context){
        super(ModBlocks.RESEARCH_MAP_SCREEN.get(), syncId, new SimpleInventory(1));
        hiddenInventory = new HiddenInventory(this,64);
        addPlayerInventory(playerInventory,0,0);
        addHiddenSlots(hiddenInventory);
        addSingle(inventory,0,-1,-1);
        this.context = context;
        this.player = playerInventory.player;
        ((SimpleInventory)inventory).addListener(this::onContentChanged);

    }

    public ResearchMapScreenHandler(int syncId,PlayerInventory playerInventory){
       this(syncId,playerInventory, ScreenHandlerContext.EMPTY);
    }
    public void close(PlayerEntity player) {
      super.close(player);
      this.context.run((world, pos) -> {
         this.dropInventory(player, this.hiddenInventory);
         this.dropInventory(player, this.inventory);
      });
    }
    public boolean tabletUpdated = false;
    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player){
        super.onSlotClick(slotIndex, button, actionType, player);
        if(slotIndex<0){return;}
        if(getSlot(slotIndex).inventory ==  inventory){
            if(!player.world.isClient && getSlot(slotIndex).getStack().getItem().equals(ModItems.BLANK_TABLET.get())){
                ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)player;
                ItemStack tbtk = new ItemStack(ModItems.TABLET_OF_KNOWLEDGE.get());
                NbtCompound compound = new NbtCompound();
                for(var entry: ResearchItem.researchItemSet){
                    if(entry.value.children.isEmpty()){
                        compound.putInt(entry.value.getNameID(),0);
                    }
                }
                tbtk.setSubNbt("research",compound);
                System.out.println(tbtk.getNbt().toString());
                inventory.setStack(0, tbtk);
                setPreviousTrackedSlot(slotIndex, tbtk);
                serverPlayerEntity.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(syncId, nextRevision(), slotIndex, tbtk));
            }
        }
    }

    @Override
    public void onContentChanged(Inventory inventory){
        super.onContentChanged(inventory);
        if(inventory == this.inventory){
            tabletUpdated = true;
        }
    }
}
