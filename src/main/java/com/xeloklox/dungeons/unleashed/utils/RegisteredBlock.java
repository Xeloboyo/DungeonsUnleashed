package com.xeloklox.dungeons.unleashed.utils;

import com.xeloklox.dungeons.unleashed.gen.*;
import com.xeloklox.dungeons.unleashed.gen.LootTableJson.*;
import com.xeloklox.dungeons.unleashed.gen.LootTableJson.LootPool.LootPoolEntry.*;
import net.fabricmc.fabric.api.blockrenderlayer.v1.*;
import net.fabricmc.fabric.api.item.v1.*;
import net.minecraft.block.*;
import net.minecraft.client.render.*;
import net.minecraft.item.*;
import net.minecraft.item.Item.*;
import net.minecraft.util.registry.*;

import static com.xeloklox.dungeons.unleashed.DungeonsUnleashed.MODID;
import static com.xeloklox.dungeons.unleashed.Globals.bootQuery;

public class RegisteredBlock extends Registerable<Block>{
    boolean createBlockItem = true;
    public RegisteredItem blockitem = null;//= (id2, item) -> new RegisteredItem(id2, item, model -> model)
    public BlockRenderLayerRegistration renderlayer;
    BlockStateJson blockStateConfig;
    LootTableJson drops;
    private Settings settings = bootQuery(() -> new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS), new FabricItemSettings());
    private BlockStateBuilder bsb;
    // public RegisteredBlock(String id, Block registration,RenderLayerOptions renderlayer, Func2<String,BlockItem,RegisteredItem> blockitem, Settings settings, BlockStateBuilder bsb, Func<LootTableJson,LootTableJson> lt
    public RegisteredBlock(String id, Block registration, BlockStateBuilder bsb, Cons<RegisteredBlock> builder){
        super(id, registration, bootQuery(() -> Registry.BLOCK),RegisterEnvironment.CLIENT_AND_SERVER);
        renderlayer = new BlockRenderLayerRegistration(this,RenderLayerOptions.NORMAL);
        settings = bootQuery(() -> new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS),new FabricItemSettings());
        blockStateConfig = new BlockStateJson(this, bsb);
        builder.get(this);
        if(blockitem==null){
            blockitem = new RegisteredItem(id, new BlockItem(registration, settings),model->model);
        }
        if(drops==null){
            Func<LootTableJson,LootTableJson> lt = lootTable->
                lootTable.addPool(pool->
                    pool.addEntry(LootPoolEntryType.item,entry->
                        entry.setOutput(MODID+":"+id) //just returns the blockitems
                    )
                    .condition(LootPool.survives_explosion())
                );
            drops = lt.get(new LootTableJson(LootType.block,"blocks/"+id+".json"));
        }
        if(renderlayer==null){
            this.renderlayer = new BlockRenderLayerRegistration(this, RenderLayerOptions.NORMAL);
        }
    }

    public RegisteredBlock setId(String id){
        this.id = id;
        return this;
    }

    public RegisteredBlock setRegistration(Block registration){
        this.registration = registration;
        return this;
    }

    public RegisteredBlock setRenderlayer(RenderLayerOptions renderlayer){
        this.renderlayer.layer=renderlayer.get();
        return this;
    }

    public RegisteredBlock setBlockitem(Func2<String, BlockItem, RegisteredItem> blockitem){
        this.blockitem = blockitem.get(id,new BlockItem(registration, settings));
        return this;
    }

    public RegisteredBlock setSettings(Settings settings){
        this.settings = settings;
        return this;
    }

    public RegisteredBlock setBsb(BlockStateBuilder bsb){
        this.bsb = bsb;
        return this;
    }

    public RegisteredBlock setDrops(Func<LootTableJson, LootTableJson> lt){
        this.drops = lt.get(new LootTableJson(LootType.block,"blocks/"+id+".json"));
        return this;
    }




    public enum RenderLayerOptions{
        NORMAL(()->null),CUTOUT(()->RenderLayer.getCutout()),TRANSLUCENT(()->RenderLayer.getTranslucent());
        Prov<RenderLayer> layer;

        RenderLayerOptions(Prov<RenderLayer> layer){
            this.layer = layer;
        }

        public RenderLayer get(){
            return bootQuery(()->layer.get());
        }
    }

    public class BlockRenderLayerRegistration extends Registerable<Block>{
        public RenderLayer layer;
        public BlockRenderLayerRegistration(RegisteredBlock registration,RenderLayerOptions layer){
            super(registration.id+"renderlayer", registration.get(), bootQuery(() -> Registry.BLOCK), RegisterEnvironment.CLIENT);
            this.layer=layer.get();

        }

        @Override
        public void register(){
            if(layer!=null)
                BlockRenderLayerMap.INSTANCE.putBlock(get(), layer);
        }
    }

}
