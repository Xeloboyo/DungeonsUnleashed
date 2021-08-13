package com.xeloklox.dungeons.unleashed.utils;

import com.xeloklox.dungeons.unleashed.gen.*;
import net.minecraft.item.*;
import net.minecraft.item.Item.*;
import net.minecraft.util.registry.*;

import static com.xeloklox.dungeons.unleashed.Globals.bootQuery;

public class RegisteredItem extends Registerable<Item>{
    public ItemJsonModel model;
    public RegisteredItem(String id, Item registration){
        super(id, registration, bootQuery(()->Registry.ITEM));
        model = new ItemJsonModel(this);
    }

    public RegisteredItem(String id, Settings settings){
        this(id, bootQuery(()->new Item(settings)));
    }

}
