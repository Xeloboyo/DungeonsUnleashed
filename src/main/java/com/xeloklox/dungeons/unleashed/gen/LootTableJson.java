package com.xeloklox.dungeons.unleashed.gen;

import com.xeloklox.dungeons.unleashed.*;
import com.xeloklox.dungeons.unleashed.gen.LootTableJson.LootPool.C_match_tool.*;
import com.xeloklox.dungeons.unleashed.gen.LootTableJson.LootPool.LootPoolEntry.*;
import com.xeloklox.dungeons.unleashed.utils.lambda.*;
import net.minecraft.state.property.*;
import org.apache.commons.lang3.*;
import org.json.*;
import org.mini2Dx.gdx.utils.*;

import java.util.*;

public class LootTableJson extends JsonConfiguration{
    public LootType type;
    public Array<LootPool> pools= new Array<>();

    public LootTableJson(LootType type, String path){
        super(Paths.lootTables+path, new JSONObject());
        this.type=type;
    }

    public LootTableJson addPool(Func<LootPool,LootPool> poolFunc){
        pools.add(poolFunc.get(new LootPool()));
        return this;
    }


    @Override
    public void fillJSONObj(){
        try{
            json.put("type","minecraft:"+type.name());
            JSONArray poolsjson = new JSONArray();
            for(LootPool lp:pools){
                poolsjson.put(lp.toJSON());
            }
            json.put("pools",poolsjson);
        }catch(JSONException e){}
    }

    public enum LootType{
        empty,block,entity,chest;
    }

    public static class LootPool{
        int rolls=1;
        float bonusrolls=0.0f;
        public Array<LootPoolEntry> entries= new Array<>();
        public Array<LootPoolCondition> conditions= new Array<>();
        public Array<LootPoolFunction> functions= new Array<>();

        public LootPool setRolls(int rolls){
            this.rolls = rolls;
            return this;
        }

        public LootPool setBonusrolls(float bonusrolls){
            this.bonusrolls = bonusrolls;
            return this;
        }

        public LootPool addEntry(LootPoolEntryType type , Func<LootPoolEntry,LootPoolEntry> entryFunc){
            entries.add(entryFunc.get(new LootPoolEntry(type)));
            return this;
        }
        public LootPool condition(LootPoolCondition cond){
            conditions.add(cond);
            return this;
        }
        public LootPool addFunction(LootPoolFunction function){
            functions.add(function);
            return this;
        }

        public JSONObject toJSON(){
            JSONObject jo = new JSONObject();
            try{
                jo.put("rolls",rolls);
                jo.put("bonus_rolls",bonusrolls);
                JSONArray entriesjson = new JSONArray();
                for(LootPoolEntry lp:entries){
                    entriesjson.put(lp.output);
                }
                jo.put("entries",entriesjson);
                JSONArray conditionsjson = new JSONArray();
                for(LootPoolCondition lp:conditions){
                    conditionsjson.put(lp.base);
                }
                jo.put("conditions",conditionsjson);

                JSONArray functionsjson = new JSONArray();
                for(LootPoolFunction lp:functions){
                    conditionsjson.put(lp.base);
                }
                jo.put("functions",functionsjson);


            }catch(JSONException e){}
            return jo;
        }

        public static class LootPoolEntry{
            public JSONObject output =new JSONObject();
            public Array<LootPoolEntry> children = new Array<>();
            public Array<LootPoolCondition> conditions= new Array<>();
            public Array<LootPoolFunction> functions= new Array<>();
            LootPoolEntry(LootPoolEntryType type) {
                try{
                    output.put("type", "minecraft:"+type.name());
                }catch(Exception ignored){}
            }
            // its just 'name' in https://minecraft.fandom.com/wiki/Loot_table#List_of_loot_tables
            public LootPoolEntry setOutput(String name){
                try{
                    output.put("name", name);
                }catch(Exception ignored){}
                return this;
            }

            public LootPoolEntry condition(LootPoolCondition cond){
                try{
                    if(!output.has("conditions")){
                        output.put("conditions", new JSONArray());
                    }
                    JSONArray conditionsjson = output.getJSONArray("conditions");
                    conditionsjson.put(cond.base);
                    output.put("conditions",conditionsjson);
                    conditions.add(cond);
                }catch(Exception ignored){}
                return this;
            }
            public LootPoolEntry addFunction(LootPoolFunction cond){
                try{
                    if(!output.has("functions")){
                        output.put("functions", new JSONArray());
                    }
                    JSONArray functionsjson = output.getJSONArray("functions");
                    functionsjson.put(cond.base);
                    output.put("functions",functionsjson);
                    functions.add(cond);
                }catch(Exception ignored){}
                return this;
            }

            public LootPoolEntry addChild(LootPoolEntryType type , Func<LootPoolEntry,LootPoolEntry> entryFunc){
                try{
                    LootPoolEntry lp = entryFunc.get(new LootPoolEntry(type));
                    children.add(lp);
                    if(!output.has("children")){
                        output.put("children", new JSONArray());
                    }
                    JSONArray entriesjson = output.getJSONArray("children");
                    entriesjson.put(lp.output);
                    output.put("children",entriesjson);
                }catch(Exception ignored){}
                return this;
            }

            public enum LootPoolEntryType{
                item,tag,loot_table,group,alternatives,sequence,dynamic,empty;
            }
        }

        //region FUNCTIONS
        public static abstract class LootPoolFunction{
            public Array<LootPoolCondition> conditions = new Array<>();
            JSONObject base = new JSONObject();

            LootPoolFunction(String name){
                try{
                    base.put("function", name);
                }catch(Exception ignored){
                }
            }

            public LootPoolFunction condition(LootPoolCondition cond){
                try{
                    if(!base.has("conditions")){
                        base.put("conditions", new JSONArray());
                    }
                    JSONArray conditionsjson = base.getJSONArray("conditions");
                    conditionsjson.put(cond.base);
                    base.put("conditions", conditionsjson);
                    conditions.add(cond);
                }catch(Exception ignored){
                }
                return this;
            }
        }
        public static class F_apply_bonus extends LootPoolFunction{

            public F_apply_bonus(String enchantment, Cons<JSONObject> abf){
                super("minecraft:apply_bonus");
                try{
                    base.put("enchantment", enchantment);
                    abf.get(base);
                }catch(JSONException ignored){}
            }
            public static Cons<JSONObject> binomial(int extra,float probability){
                return jsonObject -> {
                    try{
                        jsonObject.put("formula","binomial_with_bonus_count");
                        JSONObject params = new JSONObject();
                        params.put("extra",extra);
                        params.put("probability",probability);
                        jsonObject.put("params",params);
                    }catch(JSONException e){
                        e.printStackTrace();
                    }
                };
            }
            public static Cons<JSONObject> uniform(float bonusMultiplier ){
                return jsonObject -> {
                    try{
                        jsonObject.put("formula","uniform_bonus_count");
                        JSONObject params = new JSONObject();
                        params.put("bonusMultiplier",bonusMultiplier);
                        jsonObject.put("params",params);
                    }catch(JSONException e){
                        e.printStackTrace();
                    }
                };
            }
            public static Cons<JSONObject> ore_drops( ){
                return jsonObject -> {
                    try{
                        jsonObject.put("formula","ore_drops");
                    }catch(JSONException e){
                        e.printStackTrace();
                    }
                };
            }
        }
        public static F_apply_bonus apply_bonus(String enchantment, Cons<JSONObject> abf){return new F_apply_bonus(enchantment,abf);}
        public static class F_set_count extends LootPoolFunction{
            String block;
            Property[] props;

            public F_set_count(NumberProvider prov, boolean add){
                super("minecraft:set_count");
                this.block = block;
                try{
                    base.put("count", prov.base);
                    base.put("add",add);
                }catch(JSONException ignored){}
            }
        }
        public static F_set_count set_count(NumberProvider prov, boolean add){return new F_set_count(prov,add);}
        public static class F_copy_state extends LootPoolFunction{
            String block;
            Property[] props;

            public F_copy_state(String block, Property[] props){
                super("minecraft:copy_state");
                this.block = block;
                this.props = props;
                try{
                    base.put("block", this.block);
                    JSONArray jsonArray = new JSONArray();
                    for(Property lt: props){
                        jsonArray.put(lt.getName());
                    }
                    base.put("properties",jsonArray);
                }catch(JSONException ignored){}
            }
        }
        public static F_copy_state copy_block_state(String block, Property... props){return new F_copy_state(block,props);}

        //end region
        //region CONDITIONS
        public static abstract class LootPoolCondition{
            JSONObject base = new JSONObject();
            LootPoolCondition(String name){
                try{
                    base.put("condition", name);
                }catch(Exception ignored){}
            }
        }

        public static class C_survives_explosion extends LootPoolCondition{
            C_survives_explosion(){
                super("minecraft:survives_explosion");
            }
        }
        public static C_survives_explosion survives_explosion(){return new C_survives_explosion();}

        public static class C_inverted extends LootPoolCondition{
            public LootPoolCondition term;
            C_inverted(LootPoolCondition term){
                super("minecraft:inverted");
                this.term=term;
                try{
                    base.put("term", this.term.base);
                }catch(JSONException ignored){}
            }
        }
        public static C_inverted invert(LootPoolCondition c){return new C_inverted(c);}

        public static class C_alternative extends LootPoolCondition{
            public LootPoolCondition[] term;
            C_alternative(LootPoolCondition... terms){
                super("minecraft:alternative");
                this.term=terms;
                try{
                    JSONArray jsonArray = new JSONArray();
                    for(LootPoolCondition lt: terms){
                        jsonArray.put(lt.base);
                    }
                    base.put("terms",jsonArray);
                }catch(JSONException ignored){}
            }
        }
        public static C_alternative anyOf(LootPoolCondition... c){return new C_alternative(c);}

        public static class C_random_chance extends LootPoolCondition{

            C_random_chance(float probability){
                super("minecraft:random_chance");
                try{
                    base.put("chance",probability*0.01f);
                }catch(JSONException ignored){}
            }
        }
        public static C_random_chance droprate(float probability){return new C_random_chance(probability);}

        public static class C_block_state_property extends LootPoolCondition{

            C_block_state_property(String block,PropertyEntry ... entries){
                super("minecraft:block_state_property");
                try{
                    base.put("block",block);
                    if(entries.length>0){
                        JSONObject ent = new JSONObject();
                        for(PropertyEntry e:entries){
                            ent.put(e.prop.getName(),e.value.toString().toLowerCase(Locale.ROOT));
                        }
                    }
                }catch(JSONException ignored){}
            }
        }
        public static C_block_state_property state_matches(String block,PropertyEntry ... entries){return new C_block_state_property(block,entries);}

        public static class C_table_bonus extends LootPoolCondition{

            C_table_bonus(String enchantment,float[] probabilities){
                super("minecraft:table_bonus");
                try{
                    base.put("enchantment", enchantment);
                    JSONArray jsonArray = new JSONArray();
                    for(Float lt: probabilities){
                        jsonArray.put(lt*0.01f);
                    }
                    base.put("chances",jsonArray);
                }catch(JSONException ignored){}
            }
        }
        public static C_table_bonus droprate_with_enchantment(String enchantment,float... probabilities){return new C_table_bonus(enchantment,probabilities);}

        public static class C_match_tool extends LootPoolCondition{
            C_match_tool(Func<ItemPredicateBuilder,ItemPredicateBuilder> func){
                super("minecraft:match_tool");
                try{
                    base.put("predicate", func.get(new ItemPredicateBuilder()).output);
                }catch(JSONException ignored){}
            }
            public static class ItemPredicateBuilder{
                JSONObject output = new JSONObject();
                public ItemPredicateBuilder setCount(int count){
                    try{
                        output.put("count", count);
                    }catch(Exception ignored){}
                    return this;
                }
                public ItemPredicateBuilder setCount(int min,int max){
                    try{
                        JSONObject level = new JSONObject();
                        level.put("min",min);
                        level.put("max",max);
                        output.put("count", level);
                    }catch(Exception ignored){}
                    return this;
                }
                public ItemPredicateBuilder setDurability(int durability){
                    try{
                        output.put("durability", durability);
                    }catch(Exception ignored){}
                    return this;
                }
                public ItemPredicateBuilder setTag(String Tag){
                    try{
                        output.put("tag", Tag);
                    }catch(Exception ignored){}
                    return this;
                }
                public ItemPredicateBuilder setItems(String... items){
                    try{
                        JSONArray jsonArray = new JSONArray();
                        for(String lt: items){
                            jsonArray.put(lt);
                        }
                        output.put("items", jsonArray);
                    }catch(Exception ignored){}
                    return this;
                }
                public ItemPredicateBuilder setEnchantments(String... items){
                    try{
                        JSONArray jsonArray = new JSONArray();
                        for(String lt: items){
                            String[] data = lt.split(",");
                            if(data.length==0){continue;}
                            JSONObject jo = new JSONObject();
                            jo.put("enchantment",data[0].trim());
                            if(data.length==2){
                                if(StringUtils.isNumeric(data[1])){
                                    jo.put("levels",Integer.parseInt(data[1].trim()));
                                }else{
                                    String rawstr = data[1].trim();
                                    rawstr=rawstr.substring(1,rawstr.length()-1);
                                    String minmax[] = rawstr.split(",");
                                    JSONObject level = new JSONObject();
                                    if(!minmax[0].equals("-")){
                                        level.put("min",minmax[0]);
                                    }
                                    if(!minmax[1].equals("-")){
                                        level.put("max",minmax[1]);
                                    }
                                    jo.put("levels",level);
                                }

                            }
                            jsonArray.put(jo);
                        }
                        output.put("enchantments", jsonArray);
                    }catch(Exception ignored){}
                    return this;
                }
            }
        }
        public static C_match_tool matches_tool(Func<ItemPredicateBuilder,ItemPredicateBuilder> func){return new C_match_tool(func);}

        //endregion

        public static abstract class NumberProvider{
            String type;
            JSONObject base = new JSONObject();
            NumberProvider(String type){
                this.type=type;
                try{
                   base.put("type", type);
                }catch(Exception ignored){}
            }
        }
        public static class NumberProviderConstant extends NumberProvider{
            NumberProviderConstant(float value){
                super("constant");
                try{
                   base.put("value", value);
                }catch(Exception ignored){}
            }
        }
        public static NumberProviderConstant num_constant(float v){return new NumberProviderConstant(v);}

        public static class NumberProviderUniform extends NumberProvider{
            NumberProviderUniform(float min,float max){
                super("uniform");
                try{
                   base.put("min", min);
                   base.put("max", max);
                }catch(Exception ignored){}
            }
        }
        public static NumberProviderUniform num_uniform_random(float min,float max){return new NumberProviderUniform(min,max);}


        public static class PropertyEntry<T extends Comparable<T>>{
            Property<T> prop; T value;
            PropertyEntry(Property<T> prop, T value){
                this.prop=prop;
                this.value=value;
            }
        }
        public static  <T extends Comparable<T>>  PropertyEntry<T> makeEntry (Property<T> prop, T value){
            return new PropertyEntry<>(prop,value);
        }
    }
}
