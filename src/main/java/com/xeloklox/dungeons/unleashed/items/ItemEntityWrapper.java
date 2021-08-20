package com.xeloklox.dungeons.unleashed.items;

import com.xeloklox.dungeons.unleashed.utils.lambda.*;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.*;
import net.minecraft.item.*;
import org.mini2Dx.gdx.utils.*;
import org.mini2Dx.gdx.utils.Array.*;

public class ItemEntityWrapper{
    public static IdentityMap<ItemEntity,ItemEntityWrapper> entityWrappers = new IdentityMap<>();
    public static Array<ItemEntityWrapper> tracked = new Array<>(false,16);
    public static Array<Func<ItemEntity,ItemEntityHook>> hookSelectors = new Array<>(false,16);

    public static void onEntitySpawn(ItemEntity ie){
        if(!entityWrappers.containsKey(ie)){
            ItemEntityWrapper temp = new ItemEntityWrapper(ie);
            temp.track_index = tracked.size;
            tracked.add(temp);
            entityWrappers.put(ie,temp);
        }else{

        }
    }
    public static void onItemChanged(ItemEntity ie){
        if(!entityWrappers.containsKey(ie)){
            return;
        }
        ItemEntityWrapper wrapper = entityWrappers.get(ie);
        wrapper.invalidateHooks();
        for(int i = 0;i<hookSelectors.size;i++){
            Func<ItemEntity,ItemEntityHook> func = hookSelectors.get(i);
            ItemEntityHook hook = func.get(ie);
            if(hook!=null){
                wrapper.addHook(hook);
            }
        }
        wrapper.refreshHooks();
    }

    public static void addHookSelector(Func<ItemEntity,ItemEntityHook> func){
        hookSelectors.add(func);
    }



    int track_index;
    private ItemEntity entity;
    private ItemStack prevStack;
    public Array<ItemEntityHook> hooks = new Array<>();

    public ItemEntityWrapper(ItemEntity entity){

        this.entity = entity;
        prevStack = entity.getStack();
    }

    public synchronized void addHook(ItemEntityHook newhook){
        ItemEntityHook hook;
        if((hook = getHook(newhook.getClass()))!=null){
            hook.toRemove=false;
            return;
        }
        newhook.wrapper=this;
        hooks.add(newhook);
    }
    public synchronized void invalidateHooks(){
        for(ItemEntityHook hook:hooks){
            hook.toRemove=true;
        }
    }
    public boolean hasHook(ItemEntityHook ht){
        return getHook(ht.getClass())!=null;
    }
    public <T extends ItemEntityHook> ItemEntityHook getHook(Class<T> ht){
        for(ItemEntityHook hook:hooks){
            if(hook.getClass().equals(ht)){
                return hook;
            }
        }
        return null;
    }

    public synchronized void tick(){
        if(entity.getStack()!=prevStack){
            onItemChanged(entity);
            prevStack = entity.getStack();
        }
        for(int i = 0;i<hooks.size;i++){
            ItemEntityHook hook = hooks.get(i);
            hook.onTick();
        }

        refreshHooks();
    }

    public synchronized void remove(){
        for(ItemEntityHook hook:hooks){
            hook.onRemove();
        }
        entityWrappers.remove(entity);
        tracked.removeIndex(track_index);
        if(track_index<tracked.size){
            tracked.get(track_index).track_index=track_index;
        }
    }

    public ItemEntity getEntity(){
        return entity;
    }

    public synchronized void damage(DamageSource source, float amount){

        for(ItemEntityHook hook:hooks){
            hook.onDamage(source, amount);
        }
        refreshHooks();

    }

    public synchronized void refreshHooks(){

        ArrayIterator<ItemEntityHook> it= hooks.iterator();
        while(it.hasNext()){
            if(it.next().toRemove){
                it.remove();
            }
        }
    }



    public boolean client(){
        return entity.world.isClient;
    }
    public void syncItem(){
        ItemStack is = entity.getStack().copy();
        entity.setStack(is);
    }

}
