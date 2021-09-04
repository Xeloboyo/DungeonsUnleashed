package com.xeloklox.dungeons.unleashed.utils.block;

import net.minecraft.nbt.*;
import org.mini2Dx.gdx.utils.*;

//all graph blocks need to be entities?
public abstract class BlockGraph{
    private static int idAccum=0;
    public final int id=idAccum++;
    GraphConnectingEntity core; /// this entity will do the save and load
    protected Array<GraphConnector> connected = new Array<>();
    public boolean coreLoaded = false;
    public long lastUpdate=-1;
    //list of block positions
    // last updated
    // r/w nbt shit
    // on block added
    // on block removed
    // rebuild graph

    public  BlockGraph(GraphConnector init){
        System.out.println("graph created...");
        addConnector(init);
        System.out.println("core: "+core);
    }

    public abstract void writeToNbt(NbtCompound nbtCompound);
    public abstract void readFromNbt(NbtCompound nbtCompound);

    public void addConnector(GraphConnector gc){
        if(gc.graph==this){
            System.out.println("already added?...");
            return;
        }
        gc.graph = this;
        connected.add(gc);
        gc.onAdd();
        if(core==null){
            core = gc.blockEntity;
        }
    }
    public void addOrMerge(GraphConnector gc){
        if(gc.graph!=null){
            mergeWith(gc.graph);
        }else{
            addConnector(gc);
        }
    }

    public void mergeWith(BlockGraph bg){
        if(bg == this || !bg.getClass().equals(this.getClass())){
            return;
        }
        if(bg.connected.size> connected.size && !coreLoaded){
            bg.mergeWith(this);
            return;
        }
        for(int i =0;i<bg.connected.size;i++){
            addConnector(bg.connected.get(i));
        }
        bg.onMerge(this);
        bg.connected.clear();
    }

    public abstract void onMerge(BlockGraph other);

    public void remove(GraphConnector gc){
        int index = connected.indexOf(gc,true);
        if(index==-1){
            System.out.println("wasnt actually connected... weird");
            return;
        }
        if(gc.connections.size==1){
            connected.removeIndex(index);
            gc.onRemove();
        }else{

            //todo: check if graph actually needs to be remade.

            removeItself();
            if(gc.blockEntity!=core){
                addConnector(core.connections.get(this.getClass()));
                rebuildFromCore();
            }else{

            }
        }
        gc.graph = null;
    }

    public void removeItself(){
        for(int i =0;i<connected.size;i++){
            connected.get(i).onRemove();
            connected.get(i).graph=null;
            connected.get(i).needsReconnect=true;
        }
        connected.clear();
    }
    //used for deletions, can be e x p e n s i v e, maybe? idk
    public void rebuildFromCore(){
        if(core==null){
            throw new IllegalStateException("Cannot rebuild graph with no core");
        }
        Array<GraphConnector> frontier = new Array<>(false,16);
        IntSet searched = new IntSet();
        Class<? extends BlockGraph> thisGraph = this.getClass();
        frontier.add(core.connections.get(thisGraph));
        while(!frontier.isEmpty()){
            GraphConnector gc = frontier.pop();
            searched.add(gc.id);
            if(!gc.allowsOutgoingConnection){
                continue;
            }
            gc.blockEntity.eachConnected(thisGraph,gc.blockEntity.getConfig(thisGraph), entity -> {
                GraphConnector external = entity.connections.get(thisGraph);
                if(searched.contains(external.id)){
                    return;
                }
                frontier.add(external);
                addConnector(external);
            });
        }
    }

    public abstract String name();
    public void update(long tick){
        if(tick>lastUpdate){
            lastUpdate=tick;
            onUpdate();
        }
    }

    public abstract void onUpdate();

}
