package com.xeloklox.dungeons.unleashed;

import com.xeloklox.dungeons.unleashed.utils.Registerable.*;
import com.xeloklox.dungeons.unleashed.utils.ui.*;
import net.fabricmc.api.*;

public class ModInitClient implements ClientModInitializer{

    @Override
    public void onInitializeClient(){
        try{
            Class.forName(ModItems.class.getName());
            Class.forName(ModBlocks.class.getName());
            Class.forName(ModRecipes.class.getName());
            Class.forName(ModResearch.class.getName());
            Class.forName(UITextures.class.getName());
        }catch(ClassNotFoundException e){
            e.printStackTrace();
        }
        Globals.registerAll(RegisterEnvironment.CLIENT);
    }
}

