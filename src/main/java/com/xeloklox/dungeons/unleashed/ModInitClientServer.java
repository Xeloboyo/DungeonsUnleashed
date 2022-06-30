package com.xeloklox.dungeons.unleashed;

import com.xeloklox.dungeons.unleashed.utils.Registerable.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.*;

public class ModInitClientServer implements ModInitializer {
    public static final String MODID = "almechanistpath";
    public static final String MODNAME = "The Almechanist's path";

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.

		// Proceed with mild caution.
        System.out.println(MODNAME+" loading...");
        try{
            Class.forName(ModItems.class.getName());
            Class.forName(ModBlocks.class.getName());
            Class.forName(ModResearch.class.getName());
            Class.forName(ModRecipes.class.getName());
        }catch(ClassNotFoundException e){
            e.printStackTrace();
        }
        //todo: config utilities
        FabricLoader.getInstance().getConfigDir().resolve(MODID+"/config.json");

        Globals.registerAll(RegisterEnvironment.CLIENT_AND_SERVER);
        //
	}

}
