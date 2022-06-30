package com.xeloklox.dungeons.unleashed.utils;

import com.xeloklox.dungeons.unleashed.gen.*;
import com.xeloklox.dungeons.unleashed.utils.lambda.*;
import net.fabricmc.fabric.api.object.builder.v1.client.model.*;
import net.minecraft.client.item.*;
import net.minecraft.item.*;
import net.minecraft.item.Item.*;
import net.minecraft.util.*;
import net.minecraft.util.registry.*;
import org.apache.commons.lang3.*;
import org.mini2Dx.gdx.utils.*;

import static com.xeloklox.dungeons.unleashed.ModInitClientServer.MODID;
import static com.xeloklox.dungeons.unleashed.Globals.bootQuery;

public class RegisteredItem extends Registerable<Item>implements IHasName{
    public ItemJsonModel model;
    String name = null;
    ObjectMap<String, UnclampedModelPredicateProvider> predicates = new ObjectMap<>();

    public RegisteredItem(String id, Item registration){
        super(id, registration, bootQuery(()->Registry.ITEM),RegisterEnvironment.CLIENT_AND_SERVER);
        IHasName.names.add(this);
    }

    public RegisteredItem(String id, Settings settings){
        this(id, bootQuery(()->new Item(settings)));
    }

    public void finalise(){
        if(name==null){
            name = StringUtils.capitalize(id.replace("_"," "));
        }
        if(model==null){
            model = new ItemJsonModel(this);
        }
    }
    public RegisteredItem setModel(Func<ItemJsonModel,ItemJsonModel> modelfunc){
        model = modelfunc.get(new ItemJsonModel(this));
        return this;
    }

    public RegisteredItem addPredicate(String name, UnclampedModelPredicateProvider prov){
        predicates.put(name,prov);
        return this;
    }

    public RegisteredItem setName(String name){
        this.name=name;
        return this;
    }

    @Override
    public String getName(){
        return name;
    }

    @Override
    public String getNameID(){
        return "item."+MODID+"."+id;
    }

    @Override
    public void register(){
        super.register();
        predicates.forEach(entry->{
            FabricModelPredicateProviderRegistry.register(get(),new Identifier(entry.key),entry.value);
        });
    }
}
