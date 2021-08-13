package com.xeloklox.dungeons.unleashed.utils;

import com.xeloklox.dungeons.unleashed.*;
import com.xeloklox.dungeons.unleashed.gen.*;
import com.xeloklox.dungeons.unleashed.gen.LootTableJson.*;
import com.xeloklox.dungeons.unleashed.gen.LootTableJson.LootPool.LootPoolEntry.*;
import net.fabricmc.fabric.api.item.v1.*;
import net.minecraft.block.*;
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
    final RegisteredItem blockitem;
    BlockStateJson blockStateConfig;
    LootTableJson drops;

    public RegisteredBlock(String id, Block registration, boolean createBlockItem, Settings settings, BlockStateBuilder bsb, Func<LootTableJson,LootTableJson> lt){
        super(id, registration, bootQuery(() -> Registry.BLOCK));
        this.createBlockItem = createBlockItem;
        if(this.createBlockItem){
            blockitem = new RegisteredItem(id, new BlockItem(registration, settings));
        }else{
            blockitem = null;
        }
        blockStateConfig = new BlockStateJson(this, bsb);
        drops = lt.get(new LootTableJson(LootType.block,"blocks/"+id+".json"));
    }

    public RegisteredBlock(String id, Block registration, BlockStateBuilder bsb ){
        this(id, registration, true,
        bootQuery(() -> new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS), new FabricItemSettings()),
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

}
