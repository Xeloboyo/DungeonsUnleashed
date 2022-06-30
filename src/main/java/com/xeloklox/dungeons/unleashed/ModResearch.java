package com.xeloklox.dungeons.unleashed;

import com.xeloklox.dungeons.unleashed.blockentity.screens.*;
import com.xeloklox.dungeons.unleashed.utils.*;
import com.xeloklox.dungeons.unleashed.utils.animation.Interpolations.*;
import com.xeloklox.dungeons.unleashed.utils.ui.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.util.registry.*;
import org.mini2Dx.gdx.utils.*;

import static com.xeloklox.dungeons.unleashed.ModInitClientServer.MODID;

public class ModResearch{
    public static RegisteredC2SPacketListener moveItemsBack;
    public static RegisteredC2SPacketListener researchItem;

    public static LangText defaultText = new LangText("<TODO>","defaulttext");
    public static final int texw=512,texh=256;
    public static final Identifier SPLASH_ICON = new Identifier(MODID, "textures/gui/splash_images.png");

    public static ResearchItem RESEARCH_THUNDERSTONE = new ResearchItem("Thunderstone");
    public static ResearchItem RESEARCH_THUNDERCORE = new ResearchItem("Thunder Core");


    static {
        ///packet
        moveItemsBack = new RegisteredC2SPacketListener("moveitemsback",(server, player, handler, buf, responseSender) -> {
            System.out.println("packet recieved");
            if( player.currentScreenHandler instanceof ResearchMapScreenHandler rmsh){
                for(int i = 0;i<rmsh.hiddenInventory.size();i++){
                    if(rmsh.hiddenInventory.getStack(i).isEmpty()){continue;}
                    rmsh.pl.insertStack(rmsh.hiddenInventory.getStack(i));
                    rmsh.hiddenInventory.removeStack(i);
                }
            }
        });
        /*
        * Receives:
        * Research id: Int
        * Slot amount: Short
        * Slot Id: Short   x  Slot amount
        * */
        //much of this logic feels like it should be in the screenhandler.
        researchItem = new RegisteredC2SPacketListener("research_item",(server, player, handler, buf, responseSender) -> {
            System.out.println("research packet received");
            if( player.currentScreenHandler instanceof ResearchMapScreenHandler rmsh){
                int researchitemid = buf.readInt();
                short slotam = buf.readShort();
                IntArray slots = new IntArray();
                for(int i = 0;i<slotam;i++){
                    slots.add(buf.readShort());
                }
                ResearchItem researchItem = ResearchItem.researchItemSet.get(researchitemid);
                System.out.println("Researched item No."+ researchitemid+" : ("+ researchItem.name+")");
                ObjectMap<ResearchItem.ItemRequirement,Wrapper<Integer>> itemsTaken = new ObjectMap<>();
                for(var itemreq: researchItem.itemRequirement){
                    itemsTaken.put(itemreq,new Wrapper<>(itemreq.getAmount()));
                }
                for(int i = 0;i<slotam;i++){
                    ItemStack stack = rmsh.getSlot(slots.get(i)).getStack();
                    for(var itemreq: researchItem.itemRequirement){
                        if(itemsTaken.get(itemreq).val > 0 && itemreq.getItem().equals(Registry.ITEM.getId(stack.getItem()))){
                            int taken = Math.min(stack.getCount(),itemsTaken.get(itemreq).val);
                            stack.decrement(Math.min(stack.getCount(),itemsTaken.get(itemreq).val));
                            itemsTaken.get(itemreq).val -= taken;
                            break;
                        }
                    }
                }
                ItemStack tablet = rmsh.getInventory().getStack(0);
                int current = tablet.getSubNbt("research").getInt(researchItem.getNameID());
                tablet.getSubNbt("research").putInt(researchItem.getNameID(),current+1);
                ///todo: for now
                if(current==1){
                    for(var child: researchItem.children){
                        tablet.getSubNbt("research").putInt(child.getNameID(),0);
                    }
                }
            }
        });

        ///research
        RESEARCH_THUNDERSTONE.initialDescription(
        "A glimmering in the rock, the raw vis of heaven's bolts concentrated into a dark subterranean grave. " +
        "\n It is a rare resource, but it is said by the miners of old that it can be found more commonly in higher elevations." +
        "\n Many suspect it degrades over the eons into simple inclusions of amethyst and other lower gemstones.");
        RESEARCH_THUNDERSTONE.unlockedDescription("It tingles in the hands, the mind dances with potential");
        RESEARCH_THUNDERSTONE.instructions("Ill need some samples to continue further.");
        RESEARCH_THUNDERSTONE.addItem(ModItems.THUNDERSTONE,2);
        RESEARCH_THUNDERSTONE.preUnlockName("A jagged path..");
        RESEARCH_THUNDERSTONE.splash = new TextureRegion(0,0,48,48,texw,texh,SPLASH_ICON);
        RESEARCH_THUNDERSTONE.splashFaded = new TextureRegion(48,0,48,48,texw,texh,SPLASH_ICON);
        RESEARCH_THUNDERSTONE.iconFaded = new TextureRegion(16,48,16,16,texw,texh,SPLASH_ICON);
        RESEARCH_THUNDERSTONE.icon = new TextureRegion(0,48,16,16,texw,texh,SPLASH_ICON);

        RESEARCH_THUNDERCORE.initialDescription(
            "The thunderstone is noticeably impure. It is jagged and the lightning it contains is brittle and weak. But to be impure implies purity." +
            "\n There must exist a cabochon which can hold a stronger charge, formed in the heart of a thunder bolt");
        RESEARCH_THUNDERCORE.unlockedDescription("It bites my fingers when I touch it, I feel thunder in the back of my head.");
        RESEARCH_THUNDERCORE.instructions("Ill need a sample to continue further.");
        RESEARCH_THUNDERCORE.addItem(ModItems.THUNDER_CORE,1);
        RESEARCH_THUNDERCORE.preUnlockName("The heart of the storm");
        RESEARCH_THUNDERCORE.splash = new TextureRegion(96,0,48,48,texw,texh,SPLASH_ICON);
        RESEARCH_THUNDERCORE.splashFaded = new TextureRegion(144,0,48,48,texw,texh,SPLASH_ICON);
        RESEARCH_THUNDERCORE.iconFaded = new TextureRegion(48,48,16,16,texw,texh,SPLASH_ICON);
        RESEARCH_THUNDERCORE.icon = new TextureRegion(32,48,16,16,texw,texh,SPLASH_ICON);
        RESEARCH_THUNDERCORE.addChild(RESEARCH_THUNDERSTONE);
    }

}
