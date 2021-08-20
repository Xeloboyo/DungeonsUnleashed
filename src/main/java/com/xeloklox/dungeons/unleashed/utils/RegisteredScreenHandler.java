package com.xeloklox.dungeons.unleashed.utils;

import net.fabricmc.fabric.api.client.screenhandler.v1.*;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry.*;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.screen.*;
import net.minecraft.util.registry.*;

public class RegisteredScreenHandler <H extends ScreenHandler, S extends Screen&ScreenHandlerProvider<H>>  extends Registerable<ScreenHandlerType<H>>{
    Factory<H,S> factory;
    public RegisteredScreenHandler(String id, ScreenHandlerType<H> registration, Factory<H,S> factory){
        super(id, registration, null, RegisterEnvironment.CLIENT);
        this.factory =factory;
    }

    @Override
    public void register(){
        ScreenRegistry.register(get(), factory);
    }
}
