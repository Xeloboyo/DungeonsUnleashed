package com.xeloklox.dungeons.unleashed.blocks;

import com.xeloklox.dungeons.unleashed.utils.lambda.*;
import net.fabricmc.fabric.api.object.builder.v1.block.*;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import org.mini2Dx.gdx.utils.*;

//all graph blocks need to be entities?
public abstract class BlockGraph{
    GraphConnectingEntity core; /// this entity will do the save and load
    //list of block positions
    // last updated
    // r/w nbt shit
    // on block added
    // on block removed
    // rebuild graph

    //how to connect
    public static abstract class GraphConnectConfig <T extends BlockGraph>{ //list stored in Block
        public final Class<T> graph;
        public GraphConnectConfig(Class<T> graph){
            this.graph = graph;
        }
        //what positions you can connect to
        abstract boolean canConnectTo(World world, BlockPos to, BlockPos from);
    }

    public static class ConnectConfigManager{
        ObjectMap<Class,GraphConnectConfig> connectionConfig = new ObjectMap<>();
        <T extends BlockGraph> void addConfig(GraphConnectConfig<T> config){
            connectionConfig.put(config.graph,config);
        }

        <T extends BlockGraph> GraphConnectConfig<T> get(Class<T> clazz){
            return connectionConfig.get(clazz);
        }
        // can a BlockGraphConnector connect to you?
    }

    public abstract static class GraphConnectingBLock extends BasicBlock{
        public ConnectConfigManager connectionConfig;

        public GraphConnectingBLock(Material material, Func<ConnectConfigManager, ConnectConfigManager> connectionConfigFunc, Func<FabricBlockSettings, FabricBlockSettings> settingsfunc){
            super(material, settingsfunc);
            connectionConfig = connectionConfigFunc.get(new ConnectConfigManager());
        }
    }

    public abstract static class GraphConnectingEntity extends BlockEntity{
        GraphConnectionManager connections;
        public GraphConnectingEntity(BlockEntityType<?> type, BlockPos pos, BlockState state){
            super(type, pos, state);
            intialiseGraphs();
        }

        public abstract void intialiseGraphs();
    }

    public static class GraphConnectionManager{
        ObjectMap<Class,GraphConnector> connectionConfig = new ObjectMap<>();
        <T extends BlockGraph> void addConfig(GraphConnector<T> config){
            connectionConfig.put(config.graph.getClass(),config);
        }

        <T extends BlockGraph> GraphConnector<T> get(Class<T> clazz){
            return connectionConfig.get(clazz);
        }
    }

    //manages connections in entity?
    public abstract class GraphConnector <T extends BlockGraph>{
        BlockEntity block;
        T graph;
        abstract T newGraph();


    }



}
