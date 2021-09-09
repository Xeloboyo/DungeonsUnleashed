package com.xeloklox.dungeons.unleashed.blockentity.screens;

import com.xeloklox.dungeons.unleashed.*;
import com.xeloklox.dungeons.unleashed.blockentity.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.screen.*;
import org.jetbrains.annotations.*;

public class ChargeTransposerScreenHandler extends InventoryScreenHandler{
    public ChargeTransposerScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate){
        super(ModBlocks.CHARGE_TRANSPOSER_SCREEN.get(), syncId, inventory);
        addPlayerInventory(playerInventory,8,118);
        addSingle(inventory,0,13,42);
        this.propertyDelegate=propertyDelegate;
        this.addProperties(propertyDelegate);
    }

    public ChargeTransposerScreenHandler(int syncId,PlayerInventory playerInventory){
        this(syncId,playerInventory, new SimpleInventory(1),new ArrayPropertyDelegate(ChargeTransposerEntity.syncedSize));
    }
}
