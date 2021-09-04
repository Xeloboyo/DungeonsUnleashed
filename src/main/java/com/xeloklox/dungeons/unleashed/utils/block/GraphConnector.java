package com.xeloklox.dungeons.unleashed.utils.block;

import com.xeloklox.dungeons.unleashed.utils.lambda.*;
import com.xeloklox.dungeons.unleashed.utils.lambda.Cons.*;
import org.mini2Dx.gdx.utils.*;

//manages connections in entity?
public abstract class GraphConnector<T extends BlockGraph>{
    boolean allowsOutgoingConnection = true;
    private static int idAccum = 0;
    public final int id = idAccum++;
    public GraphConnectingEntity blockEntity;
    public T graph;
    public boolean needsReconnect = false;

    public Array<GraphConnector<T>> connections= new Array<>();

    public GraphConnector(GraphConnectingEntity blockEntity, Cons<GraphConnector> preSuper){
        preSuper.get(this);
        this.blockEntity = blockEntity;
    }

    public void connectWith(GraphConnector<T> connector){
        if(connector==this){return;}
        if(connector.getClass().equals(this.getClass())){
            connections.add(connector);
            connector.connections.add(this);
        }
    }

    public boolean isConnectedWith(GraphConnector<T> connector){
        if(connector.getClass().equals(this.getClass())){
            for(int i=0;i<connections.size;i++){
                if(connections.get(i)==connector){
                    return true;
                }
            }
        }
        return false;
    }
    public void reconnect(){
        if(graph==null){
            graph = newGraph();
        }
        for(int i=0;i<connections.size;i++){
            graph.addOrMerge(connections.get(i));
        }
        needsReconnect=false;
    }

    public void disconnect(){
        for(int i=0;i<connections.size;i++){
            connections.get(i).connections.removeValue(this,true);
            connections.get(i).blockEntity.onDisconnect(this);
            blockEntity.onDisconnect(connections.get(i));
        }

    }

    public abstract T newGraph();

    public abstract void onAdd();
    public abstract void onRemove();

    public static class GraphConnectionManager{
        ObjectMap<Class,GraphConnector> connectionConfig = new ObjectMap<>();
        public <T extends BlockGraph> void add(GraphConnector<T> config){
            connectionConfig.put(config.graph.getClass(),config);
        }
        public <T extends BlockGraph> void add(Class<T> clazz, GraphConnector<T> connector){
            connectionConfig.put(clazz,connector);
        }

        public <T extends BlockGraph> GraphConnector<T> get(Class<T> clazz){
            return connectionConfig.get(clazz);
        }
        public void each(Cons2<Class, GraphConnector> cons){
            var iter = connectionConfig.iterator();
            while(iter.hasNext()){
                var e = iter.next();
                cons.get(e.key,e.value);
            }
        }
    }

    @Override
    public String toString(){
        return "GraphConnector {" +
        "  allowsOutgoingConnection=" + allowsOutgoingConnection +
        ", id=" + id +
        ", blockEntity=" + blockEntity +
        ", graph=" + graph.name() + ":" + graph.id + "," +  graph.connected.size +
        ", needsReconnect=" + needsReconnect +
        ", connections=" + connections.size +
        '}';
    }
}
