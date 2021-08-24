package com.xeloklox.dungeons.unleashed;

import com.xeloklox.dungeons.unleashed.utils.Registerable.*;
import net.fabricmc.api.ModInitializer;

public class DungeonsUnleashed implements ModInitializer {
    public static final String MODID = "dungeonunleash";
    public static final String MODNAME = "Dungeons Unleashed";

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.

		// Proceed with mild caution.
        System.out.println(MODNAME+" loading...");
        try{
            Class.forName(ModItems.class.getName());
            Class.forName(ModBlocks.class.getName());
            Class.forName(ModRecipes.class.getName());
        }catch(ClassNotFoundException e){
            e.printStackTrace();
        }

        Globals.registerAll(RegisterEnvironment.CLIENT_AND_SERVER);
        //
	}

}
