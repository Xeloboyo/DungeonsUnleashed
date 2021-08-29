package com.xeloklox.dungeons.unleashed.utils.block.entity;

import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.util.collection.*;

/**
 * A simple {@code Inventory} implementation with only default methods + an item list getter.
 *
 * Originally by Juuz
 */
public interface ImplementedInventory extends Inventory{

    /**
     * Retrieves the item list of this inventory.
     * Must return the same instance every time it's called.
     */
    DefaultedList<ItemStack> getItems();

    /**
     * Creates an inventory from the item list.
     */
    static ImplementedInventory of(DefaultedList<ItemStack> items) {
        return () -> items;
    }

    /**
     * Creates a new inventory with the specified size.
     */
    static ImplementedInventory ofSize(int size) {
        return of(DefaultedList.ofSize(size, ItemStack.EMPTY));
    }

    /**
     * Returns the inventory size.
     */
    @Override
    default int size() {
        return getItems().size();
    }

    /**
     * Checks if the inventory is empty.
     * @return true if this inventory has only empty stacks, false otherwise.
     */
    @Override
    default boolean isEmpty() {
        for (int i = 0; i < size(); i++) {
            ItemStack stack = getStack(i);
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Retrieves the item in the slot.
     */
    @Override
    default ItemStack getStack(int slot) {
        return getItems().get(slot);
    }

    /**
     * Removes items from an inventory slot.
     * @param slot  The slot to remove from.
     * @param count How many items to remove. If there are less items in the slot than what are requested,
     *              takes all items in that slot.
     */
    @Override
    default ItemStack removeStack(int slot, int count) {
        ItemStack result = Inventories.splitStack(getItems(), slot, count);
        if (!result.isEmpty()) {
            markDirty();
        }
        return result;
    }

    /**
     * Removes all items from an inventory slot.
     * @param slot The slot to remove from.
     */
    @Override
    default ItemStack removeStack(int slot) {
        return Inventories.removeStack(getItems(), slot);
    }

    /**
     * Replaces the current stack in an inventory slot with the provided stack.
     * @param slot  The inventory slot of which to replace the itemstack.
     * @param stack The replacing itemstack. If the stack is too big for
     *              this inventory ({@link Inventory#getMaxCountPerStack()}),
     *              it gets resized to this inventory's maximum amount.
     */
    @Override
    default void setStack(int slot, ItemStack stack) {
        getItems().set(slot, stack);
        if (stack.getCount() > getMaxCount(slot)) {
            stack.setCount(getMaxCount(slot));
        }
    }

    /**
     * Clears the inventory.
     */
    @Override
    default void clear() {
        getItems().clear();
    }

    /**
     * Marks the state as dirty.
     * Must be called after changes in the inventory, so that the game can properly save
     * the inventory contents and notify neighboring blocks of inventory changes.
     */
    @Override
    default void markDirty() {
        // Override if you want behavior.
    }

    /**
     * @return true if the player can use the inventory, false otherwise.
     */
    @Override
    default boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    default int getMaxCount(int slotindex){
        return 64;
    }
    default int getSpace(int slotindex){
        ItemStack slot = getItems().get(slotindex);
        if(slot.isEmpty()){return getMaxCount(slotindex);}
        return Math.min(slot.getMaxCount(),getMaxCount(slotindex))-slot.getCount();
    }
    /**
     * @return amount of items left over after insert
     */
    default int tryInsert(int slotindex, ItemStack stack){
        ItemStack slot = getItems().get(slotindex);
        int space = getSpace(slotindex);
        int insertam = Math.min(space,stack.getCount());
        if(slot.isEmpty()){
            setStack(slotindex,stack.copy());
            return stack.getCount()-insertam;
        }else if(ItemStack.canCombine(slot,stack)){
            slot.setCount(slot.getCount()+insertam);
            return stack.getCount()-insertam;
        }else{
            return stack.getCount();
        }
    }
    /**
     * transfer items between slots
     */
    default void tryTransfer(int fromslot, int toslot){
        int leftover = tryInsert(toslot,getItems().get(fromslot));
        getItems().get(fromslot).setCount(leftover);
    }

}