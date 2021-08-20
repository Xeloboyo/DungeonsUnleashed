package com.xeloklox.dungeons.unleashed.blockentity.screens;

import com.xeloklox.dungeons.unleashed.blockentity.*;
import com.xeloklox.dungeons.unleashed.utils.lambda.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.*;
import org.jetbrains.annotations.*;

public class InventoryScreenHandler extends ScreenHandler{
    protected final Inventory inventory;
    PropertyDelegate propertyDelegate;
    protected InventoryScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId, Inventory i){
        super(type, syncId);
        this.inventory=i;
    }

    public int getSyncedInt(int index){
        return propertyDelegate.get(index);
    }

    @Override
    public boolean canUse(PlayerEntity player){
        return this.inventory.canPlayerUse(player);
    }

    public void addSingle(Inventory pl, int index, int x,int y){
        this.addSlot(new Slot(pl, index, x , y));
    }
    public void addColumn(Inventory pl, int index, int x,int y, int amount){
        for (int m = 0; m < amount; ++m) {
            this.addSlot(new Slot(pl, m+index, x , y+ m * 18));
        }
    }

    public void addRow(Inventory pl, int index, int x,int y, int amount){
        for (int m = 0; m < amount; ++m) {
            this.addSlot(new Slot(pl, m+index, x+ m * 18 , y));
        }
    }

    public void addPlayerInventory(PlayerInventory pl, int x,int y){
        int m;
        int l;
        //The player inventory
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 9; ++l) {
                this.addSlot(new Slot(pl, l + m * 9 + 9, x + l * 18, y+ m * 18));
            }
        }
        //The player Hotbar
        for (m = 0; m < 9; ++m) {
            this.addSlot(new Slot(pl, m, x + m * 18, y+58));
        }
    }

    // Shift + Player Inv Slot
    @Override
    public ItemStack transferSlot(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.size()) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        return newStack;
    }
}
