package com.xeloklox.dungeons.unleashed.utils;

import com.xeloklox.dungeons.unleashed.*;
import com.xeloklox.dungeons.unleashed.gen.*;
import com.xeloklox.dungeons.unleashed.gen.LootTableJson.*;
import com.xeloklox.dungeons.unleashed.gen.LootTableJson.LootPool.*;
import com.xeloklox.dungeons.unleashed.gen.LootTableJson.LootPool.LootPoolEntry.*;
import com.xeloklox.dungeons.unleashed.utils.lambda.*;
import net.fabricmc.fabric.api.blockrenderlayer.v1.*;
import net.fabricmc.fabric.api.item.v1.*;
import net.fabricmc.fabric.api.registry.*;
import net.minecraft.block.*;
import net.minecraft.client.render.*;
import net.minecraft.item.*;
import net.minecraft.item.Item.*;
import net.minecraft.util.registry.*;
import org.apache.commons.lang3.*;
import org.json.*;

import static com.xeloklox.dungeons.unleashed.DungeonsUnleashed.MODID;
import static com.xeloklox.dungeons.unleashed.Globals.bootQuery;
import static com.xeloklox.dungeons.unleashed.gen.LootTableJson.LootPool.*;

public class RegisteredBlock extends Registerable<Block> implements IHasName{
    boolean createBlockItem = true;
    public RegisteredItem blockitem = null;//= (id2, item) -> new RegisteredItem(id2, item, model -> model)
    public BlockRenderLayerRegistration renderlayer;
    BlockStateJson blockStateConfig;
    LootTableJson drops;
    private Settings settings = bootQuery(() -> new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS), new FabricItemSettings());
    private BlockStateBuilder bsb;
    FlammablilityConfig flammablilityConfig;
    String name = null;

    public RegisteredBlock(String id){
        super(id, null, bootQuery(() -> Registry.BLOCK),RegisterEnvironment.CLIENT_AND_SERVER);
        name = StringUtils.capitalize(id.replace("_"," "));
        IHasName.names.add(this);
    }

    public void finalise(){
        if(bsb==null || (registration==null && Globals.bootStrapped)){
            throw new IllegalStateException("Youve done fucked up a block called "+id);
        }
        blockStateConfig = new BlockStateJson(this, bsb);
        if(settings==null){ // somehow
            settings = bootQuery(() -> new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS),new FabricItemSettings());
        }
        if(renderlayer==null){
            this.renderlayer = new BlockRenderLayerRegistration(this, RenderLayerOptions.NORMAL);
        }
        if(blockitem==null){
            blockitem = new RegisteredItem(id, new BlockItem(registration, settings),model->model);
            blockitem.setName(name);
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

    }

    public RegisteredBlock setBlock(Block registration){
        this.registration = registration;
        return this;
    }


    public RegisteredBlock setRenderlayer(RenderLayerOptions renderlayer){
        this.renderlayer= new BlockRenderLayerRegistration(this, renderlayer);
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
    public BlockStateBuilder.ModelList getPrimaryModel(){
        if(this.bsb.map.containsKey("")){
            return this.bsb.map.get("");
        }
        for(var e:bsb.map){
            return e.value;
        }
        return null;
    }
    public String[] getPrimaryModelTextures(){
        var modelList = getPrimaryModel();
        final String[] output=new String[]{null,null,null};
        modelList.eachModelVariant(variant->{
            if(output[0]!=null){
                return;
            }
            if(variant.getModel().startsWith("@@")){
                try{
                    JSONObject model = new JSONObject(variant.getModel().substring(2));
                    if(model.has("tex_all")){
                        String modelName = model.getString("tex_all");
                        output[0] = modelName;
                        output[1] = modelName;
                        output[2] = modelName;
                    }else if(model.has("tex_0")){
                        String modelName = model.getString("tex_0");
                        output[0] = modelName;
                        output[1] = modelName;
                        output[2] = modelName;
                    }else if(model.has("tex_top")){
                        output[0] = model.getString("tex_top");
                        output[1] = model.getString("tex_side");
                        output[2] = model.getString("tex_bottom");
                    }
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }
        });
        if(output[0]!=null){
            return output;
        }
        return new String[]{"block/default","block/default","block/default"};
    }

    public RegisteredBlock setBlockState(BlockStateBuilder bsb){
        this.bsb = bsb;
        return this;
    }

    public RegisteredBlock setFlammablility(int flammability, int encouragement){
        this.flammablilityConfig = new FlammablilityConfig(flammability,encouragement);
        return this;
    }

    public RegisteredBlock setDrops(Func<LootTableJson, LootTableJson> lt){
        this.drops = lt.get(new LootTableJson(LootType.block,"blocks/"+id+".json"));
        return this;
    }
    public RegisteredBlock dropsUnlessSilktouched(String item){
        LootPoolCondition silktouch = (matches_tool(filter->filter.setEnchantments("minecraft:silk_touch , [1,-]")));
        Func<LootTableJson,LootTableJson> lt = lootTable->
            lootTable.addPool(pool->
                pool.addEntry(LootPoolEntryType.item,entry->
                    entry.setOutput(MODID+":"+id)
                         .condition(silktouch)
                ).addEntry(LootPoolEntryType.item,entry->
                    entry.setOutput(item)
                         .condition(invert(silktouch))
                ).condition(LootPool.survives_explosion())

            );
        drops = lt.get(new LootTableJson(LootType.block,"blocks/"+id+".json"));
        return this;
    }


    public RegisteredBlock setName(String name){
        this.name=name;
        return this;
    }

    @Override
    public String getName(){
        return name;
    }

    @Override
    public String getNameID(){
        return "block."+MODID+"."+id;
    }


    public static class FlammablilityConfig{
        public int flammability, encouragement;

        public FlammablilityConfig(int flammability, int encouragement){
            this.flammability = flammability;
            this.encouragement = encouragement;
        }
    }

    @Override
    public void register(){
        super.register();
        if(flammablilityConfig!=null){
            FlammableBlockRegistry.getDefaultInstance().add(get(), flammablilityConfig.flammability, flammablilityConfig.encouragement);
        }
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
