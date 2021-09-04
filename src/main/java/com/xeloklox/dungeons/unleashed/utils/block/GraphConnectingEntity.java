package com.xeloklox.dungeons.unleashed.utils.block;

import com.xeloklox.dungeons.unleashed.utils.block.GraphConnector.*;
import com.xeloklox.dungeons.unleashed.utils.lambda.*;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.nbt.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

import java.util.*;

public abstract class GraphConnectingEntity extends BlockEntity implements BlockEntityTicker<GraphConnectingEntity>{
    public GraphConnectionManager connections = new GraphConnectionManager();
    GraphConnectingBlock graphBlock;
    boolean initialised =false;
    boolean connected = false;

    @Override
    public void readNbt(NbtCompound nbt){
        if(!initialised){
            initalise();
        }
        super.readNbt(nbt);
        connections.each((cls, con) -> {
            if(nbt.contains(con.graph.name())){
                NbtCompound subnbt = nbt.getCompound(con.graph.name());
                con.graph.readFromNbt(subnbt);
            }
        });
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt){
        connections.each((cls, con) -> {
            if(con.graph.core==this){
                NbtCompound subnbt = new NbtCompound();
                con.graph.writeToNbt(subnbt);
                nbt.put(con.graph.name(),subnbt);
            }
        });
        return super.writeNbt(nbt);
    }

    public GraphConnectingEntity(BlockEntityType<?> type, BlockPos pos, BlockState state){
        super(type, pos, state);
    }

    public  <T extends BlockGraph> T getGraph(Class<T> cls){
        if(!initialised){
            initalise();
        }
        return connections.get(cls).graph;
    }

    public GraphConnectingBlock getBlock(){
        if(graphBlock == null){
            graphBlock = (GraphConnectingBlock)world.getBlockState(pos).getBlock();
        }
        return graphBlock;
    }

    public <T extends BlockGraph> GraphConnectConfig getConfig(Class<T> cls){
        return getBlock().connectionConfig.get(cls);
    }

    public void initalise(){
        intialiseConnectors();
        initaliseGraphs();
        initialised =true;
    }

    public abstract void intialiseConnectors();
    public void initaliseGraphs(){
        connections.each((cls,con)->{
            con.graph=con.newGraph();
        });
    }

    public void disconnect(){
        connections.each((cls, connector) -> {
            connector.graph.remove(connector);
            connector.disconnect();
        });
    }

    public void connect(){
        getBlock().connectionConfig.each((cls, config) -> {
            GraphConnector connector =  this.connections.get(cls);
            eachConnected(cls, config, entity -> {
                if(connector.isConnectedWith(entity.connections.get(cls))){
                    return;
                }

                connector.connectWith(entity.connections.get(cls));
                BlockGraph bgExternal = entity.getGraph(cls);
                if(bgExternal!=connector.graph){
                    connector.graph.mergeWith(bgExternal);
                }
                onConnect(entity.connections.get(cls));
                entity.connections.get(cls).blockEntity.onConnect(connector);
            });
        });
    }

    public void onConnect(GraphConnector bg){};
    public void onDisconnect(GraphConnector bg){};

    //maybe cache these connections ... later
    public <T extends BlockGraph> void eachConnected(Class<T> cls, GraphConnectConfig<T> config, Cons<GraphConnectingEntity> cons){
        final BlockPos bp = this.pos;
        final World world = this.world;
        BlockPos[] cpoints = config.getConnectionPoints(world, bp);
        //For all connection points this block can connect to via this graph
        //if external block can connect back, merge this graph with external graph
        for(BlockPos rbp : cpoints){
            if(world.getBlockState(rbp).getBlock() instanceof GraphConnectingBlock externalGraphBlock){
                GraphConnectConfig externalconfig = externalGraphBlock.connectionConfig.get(cls);
                if(externalconfig != null && externalconfig.canConnectTo(world, rbp, bp)){
                    GraphConnectingEntity external = (GraphConnectingEntity)world.getBlockEntity(rbp);
                    cons.get(external);
                }
            }
        }
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState state, GraphConnectingEntity blockEntity){
        if(!world.isClient){
            serverUpdate(world,pos,state,blockEntity);
        }else{
            clientUpdate(world,pos,state,blockEntity);
        }

    }
    public void clientUpdate(World world, BlockPos pos, BlockState state, GraphConnectingEntity blockEntity){}
    public void serverUpdate(World world, BlockPos pos, BlockState state, GraphConnectingEntity blockEntity){
        if(!initialised){
            initalise();
        }
        if(!connected){
            connect();
            connected=true;
        }
        connections.each((aClass, connector) -> {
            if(connector.needsReconnect){
                connector.reconnect();
            }
            connector.graph.update(world.getTime());
        });
    }
}
