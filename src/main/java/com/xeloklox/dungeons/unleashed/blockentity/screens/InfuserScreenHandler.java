package com.xeloklox.dungeons.unleashed.blockentity.screens;

import com.xeloklox.dungeons.unleashed.*;
import com.xeloklox.dungeons.unleashed.blockentity.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.screen.*;

public class InfuserScreenHandler extends InventoryScreenHandler{

    public InfuserScreenHandler( int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate){
        super(ModBlocks.INFUSER_SCREEN.get(), syncId,inventory);
        addPlayerInventory(playerInventory,22,118);
        addColumn(inventory,0,14,24,3);
        addSingle(inventory, 3,94,46);
        addColumn(inventory,4,172,24,3);
        this.propertyDelegate=propertyDelegate;
        this.addProperties(propertyDelegate);
    }

    public InfuserScreenHandler(int syncId,PlayerInventory playerInventory){
       this(syncId,playerInventory, new SimpleInventory(7),new ArrayPropertyDelegate(InfuserEntity.syncedSize));
    }
}
