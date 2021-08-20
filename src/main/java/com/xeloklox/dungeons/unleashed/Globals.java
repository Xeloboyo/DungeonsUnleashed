package com.xeloklox.dungeons.unleashed;

import com.xeloklox.dungeons.unleashed.utils.*;
import com.xeloklox.dungeons.unleashed.utils.lambda.*;
import net.minecraft.util.registry.*;
import org.mini2Dx.gdx.utils.*;

public class Globals{
    public static boolean bootStrapped = true;
    public static ObjectMap<Registry,ObjectMap<String, Registerable>> register = new ObjectMap<>();
    public static ObjectMap<String, Registerable> register_noRegsitry = new ObjectMap<>();


    public static <T>T bootQuery(Prov<T> t){return bootStrapped?t.get():null;}
    public static <T>T bootQuery(Prov<T> t,T alt){return bootStrapped?t.get():alt;}
    public static <T>void bootRun(Runnable t){ if(bootStrapped)t.run();}

    public static void register(String id,Registerable o){
        if(o.getRegistry()==null){
            register_noRegsitry.put(id,o);
            return;
        }
        if(!register.containsKey(o.getRegistry())){
            register.put(o.getRegistry(),new ObjectMap<>());
        }
        register.get(o.getRegistry()).put(id,o);
    }
    public static boolean hasId(String id,Registry o){
        if(o==null){
            return register_noRegsitry.containsKey(id);
        }
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
        register_noRegsitry.forEach(entry2-> {
            if(entry2.value.getEnvironment().equals(env)){
                entry2.value.register();
            }
        });
    }
}
