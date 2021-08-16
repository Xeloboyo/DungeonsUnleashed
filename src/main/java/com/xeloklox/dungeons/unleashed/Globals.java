package com.xeloklox.dungeons.unleashed;

import com.xeloklox.dungeons.unleashed.utils.*;
import net.minecraft.util.registry.*;
import org.mini2Dx.gdx.utils.*;

public class Globals{
    public static boolean bootStrapped = true;
    public static ObjectMap<Registry,ObjectMap<String, Registerable>> register = new ObjectMap<>();



    public static <T>T bootQuery(Prov<T> t){return bootStrapped?t.get():null;}
    public static <T>T bootQuery(Prov<T> t,T alt){return bootStrapped?t.get():alt;}

    public static void register(String id,Registerable o){
        if(!register.containsKey(o.getRegistry())){
            register.put(o.getRegistry(),new ObjectMap<>());
        }
        register.get(o.getRegistry()).put(id,o);
    }
    public static boolean hasId(String id,Registry o){
        if(!register.containsKey(o)){
            return false;
        }
        return register.get(o).containsKey(id);
    }
    public static void registerAll(Registerable.RegisterEnvironment env){
        register.forEach(entry->{
            entry.value.forEach(entry2-> {
                if(entry2.value.getEnvironment().equals(env)){
                    entry2.value.register();
                }
            });
        });
    }
}
