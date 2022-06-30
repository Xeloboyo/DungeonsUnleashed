package com.xeloklox.dungeons.unleashed.utils;

import io.netty.channel.*;
import net.fabricmc.fabric.api.networking.v1.*;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.*;
import net.minecraft.util.registry.*;

public class RegisteredC2SPacketListener extends Registerable<PlayChannelHandler> {
    public RegisteredC2SPacketListener(String id, PlayChannelHandler registration){
        super(id, registration, null, RegisterEnvironment.CLIENT_AND_SERVER);
    }

    @Override
    public void register(){
        ServerPlayNetworking.registerGlobalReceiver(getIdentifier(), get());
    }
}
