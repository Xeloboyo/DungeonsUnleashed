package com.xeloklox.dungeons.unleashed.utils;

import com.xeloklox.dungeons.unleashed.utils.ui.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.util.registry.*;
import org.mini2Dx.gdx.utils.*;

import java.util.*;

import static com.xeloklox.dungeons.unleashed.ModResearch.defaultText;

public class ResearchItem implements IHasName{

    public static IntMap<ResearchItem> researchItemSet = new IntMap();
    public static Array<ResearchItem> researchItems = new Array<>();

    public String name;
    // how many times can the item be researched to unlock more stuff within this item
    public int maxLevels = 1;
    public TextureRegion iconFaded,icon,splashFaded, splash;
    // if null will just be normal name.
    public LangText preUnlockName = null;
    // pre-unlock description text
    public LangText initialDesc = defaultText;
    // post-unlock description text
    public LangText researchedDesc = defaultText;
    //instruction text
    public LangText instructions = defaultText;

    public Array<ItemRequirement> itemRequirement = new Array<>();

    public Array<ResearchItem> parent= new Array<>();
    public Array<ResearchItem> children= new Array<>();
    //other sections to research..
    //elemental categories?
    private static int counter = 0;
    public final int id = counter++;


    public ResearchItem(String name){
        this.name = name;
        names.add(this);
        researchItemSet.put(id,this);
        researchItems.add(this);
    }

    @Override
    public String getName(){
        return name;
    }

    @Override
    public String getNameID(){
        return "research_item_"+name.toLowerCase(Locale.ROOT).replace(' ','_');
    }

    public ResearchItem initialDescription(String desc){
        initialDesc = new LangText(desc,getNameID()+"_initialDesc");
        return this;
    }
    public ResearchItem unlockedDescription(String desc){
        researchedDesc = new LangText(desc,getNameID()+"_unlockedDesc");
        return this;
    }

    public ResearchItem preUnlockName(String desc){
        preUnlockName = new LangText(desc,getNameID()+"_preUnlockName");
        return this;
    }

    public ResearchItem instructions(String desc){
        instructions = new LangText(Strings.CODE_BOLD+desc,getNameID()+"_instructions");
        return this;
    }

    public ResearchItem addItem(RegisteredItem item, int amount){
        itemRequirement.add(new ItemRequirement(item.getIdentifier(),amount));
        return this;
    }

    public ResearchItem addItem(String item, int amount){
        itemRequirement.add(new ItemRequirement(new Identifier(item),amount));
        return this;
    }
    public ResearchItem addVanillaItem(String item, int amount){
        itemRequirement.add(new ItemRequirement(new Identifier("minecraft",item),amount));
        return this;
    }

    public ResearchItem addChild(ResearchItem item){
        if(item==this){
            throw new IllegalStateException("'"+name+"' cannot parent itself");
        }
        if(item.parent.contains(this,true)){return this;}
        item.parent.add(this);
        children.add(item);
        return this;
    }

    public static class ItemRequirement{
        Identifier item;
        int amount;

        public ItemRequirement(Identifier item, int amount){
            this.item = item;
            this.amount = amount;
        }

        public Identifier getItem(){
            return item;
        }

        public int getAmount(){
            return amount;
        }
    }

}
