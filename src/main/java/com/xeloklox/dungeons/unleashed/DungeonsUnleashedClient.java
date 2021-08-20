package com.xeloklox.dungeons.unleashed;

import com.xeloklox.dungeons.unleashed.utils.Registerable.*;
import net.fabricmc.api.*;

public class DungeonsUnleashedClient implements ClientModInitializer{

    @Override
    public void onInitializeClient(){
        try{
            Class.forName(ModItems.class.getName());
            Class.forName(ModBlocks.class.getName());
            Class.forName(ModRecipes.class.getName());
        }catch(ClassNotFoundException e){
            e.printStackTrace();
        }
        Globals.registerAll(RegisterEnvironment.CLIENT);
    }
}

