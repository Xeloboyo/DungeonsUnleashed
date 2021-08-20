package com.xeloklox.dungeons.unleashed.utils;

import com.xeloklox.dungeons.unleashed.gen.*;
import com.xeloklox.dungeons.unleashed.utils.lambda.*;
import net.fabricmc.fabric.api.object.builder.v1.client.model.*;
import net.minecraft.client.item.*;
import net.minecraft.item.*;
import net.minecraft.item.Item.*;
import net.minecraft.util.*;
import net.minecraft.util.registry.*;
import org.mini2Dx.gdx.utils.*;

import static com.xeloklox.dungeons.unleashed.Globals.bootQuery;

public class RegisteredItem extends Registerable<Item>{
    public ItemJsonModel model;
    ObjectMap<String, UnclampedModelPredicateProvider> predicates = new ObjectMap<>();

    public RegisteredItem(String id, Item registration, Func<ItemJsonModel,ItemJsonModel> modelfunc, Func<RegisteredItem,RegisteredItem> extra){
        super(id, registration, bootQuery(()->Registry.ITEM),RegisterEnvironment.CLIENT_AND_SERVER);
        model = modelfunc.get(new ItemJsonModel(this));
        extra.get(this);
    }

    public RegisteredItem(String id, Item registration, Func<ItemJsonModel,ItemJsonModel> modelfunc){
        this(id, registration, modelfunc,a->a);
    }

    public RegisteredItem(String id, Settings settings){

        this(id, bootQuery(()->new Item(settings)),model->model);
    }

    public RegisteredItem addPredicate(String name, UnclampedModelPredicateProvider prov){
        predicates.put(name,prov);
        return this;
    }

    @Override
    public void register(){
        super.register();
        predicates.forEach(entry->{
            FabricModelPredicateProviderRegistry.register(get(),new Identifier(entry.key),entry.value);
        });
    }
}
