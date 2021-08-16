package com.xeloklox.dungeons.unleashed.utils;

import com.xeloklox.dungeons.unleashed.*;
import net.minecraft.util.*;
import net.minecraft.util.registry.*;

import static com.xeloklox.dungeons.unleashed.DungeonsUnleashed.MODID;
import static com.xeloklox.dungeons.unleashed.Globals.bootStrapped;

public abstract class Registerable<T>{
    public String id;
    T registration;
    Registry registry;
    RegisterEnvironment environment;

    public Registerable(String id, T registration, Registry registry, RegisterEnvironment rd){
        this.id = id;
        this.registration = registration;
        this.registry = registry;
        if(bootStrapped){
            if(!Globals.hasId(id, registry)){
                Globals.register(id, this);
            }else{
                System.out.println("[MOD REGISTRY]: CONFLICT!!! : " + id + " of registry " + registry.toString());
            }
        }
        environment = rd;
    }

    public void register(){
        System.out.println("["+registry.toString()+" MOD REGISTRY]: registered "+id);
        Registry.register(registry, new Identifier(MODID, id), registration);
    }

    public T get(){
        return registration;
    }

    public Registry getRegistry(){
        return registry;
    }

    public RegisterEnvironment getEnvironment(){
        return environment;
    }

    public String getJSONID(){
        return MODID+":"+id;
    }

    public enum RegisterEnvironment{
        CLIENT,SERVER,CLIENT_AND_SERVER
    }
}
