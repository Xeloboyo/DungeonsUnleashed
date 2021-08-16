package com.xeloklox.dungeons.unleashed.utils;

import com.xeloklox.dungeons.unleashed.*;
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
import org.apache.commons.io.*;
import org.json.*;
import org.mini2Dx.gdx.utils.*;

import java.io.*;

import static com.xeloklox.dungeons.unleashed.DungeonsUnleashed.MODID;
import static com.xeloklox.dungeons.unleashed.Globals.bootQuery;

public class RegisteredBlock extends Registerable<Block>{
    boolean createBlockItem = true;
    public final RegisteredItem blockitem;
    final BlockRenderLayerRegistration renderlayer;
    BlockStateJson blockStateConfig;
    LootTableJson drops;

    public RegisteredBlock(String id, Block registration,RenderLayerOptions renderlayer, Func2<String,BlockItem,RegisteredItem> blockitem, Settings settings, BlockStateBuilder bsb, Func<LootTableJson,LootTableJson> lt){
        super(id, registration, bootQuery(() -> Registry.BLOCK),RegisterEnvironment.CLIENT_AND_SERVER);
        this.blockitem = blockitem.get(id,new BlockItem(registration, settings));
        blockStateConfig = new BlockStateJson(this, bsb);
        drops = lt.get(new LootTableJson(LootType.block,"blocks/"+id+".json"));
        this.renderlayer = new BlockRenderLayerRegistration(this,renderlayer);
    }

    public RegisteredBlock(String id, Block registration, BlockStateBuilder bsb ){
        this(id, registration, RenderLayerOptions.NORMAL,
        (id2,item)-> new RegisteredItem(id2, item,model->model),
        bootQuery(() -> new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS),new FabricItemSettings()),
        bsb,
        lootTable->
            lootTable.addPool(pool->
                pool.addEntry(LootPoolEntryType.item,entry->
                    entry.setOutput(MODID+":"+id) //just returns the blockitems
                )
                .addCondition(LootPool.survives_explosion())
            )
        );
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
