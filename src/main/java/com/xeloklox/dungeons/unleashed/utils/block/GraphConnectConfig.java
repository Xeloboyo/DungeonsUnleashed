package com.xeloklox.dungeons.unleashed.utils.block;

import com.xeloklox.dungeons.unleashed.utils.lambda.Cons.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import org.mini2Dx.gdx.utils.*;

import static java.lang.Math.abs;

//how to connect
public abstract class GraphConnectConfig<T extends BlockGraph>{ //list stored in Block
    public final Class<T> graphClass;

    public GraphConnectConfig(Class<T> graphClass){
        this.graphClass = graphClass;
    }

    //what positions you can connect to
    abstract boolean canConnectTo(World world, BlockPos from, BlockPos to);

    abstract BlockPos[] getConnectionPoints(World world, BlockPos from);


    public static class AllSidesConnectConfig<T extends BlockGraph> extends GraphConnectConfig<T>{
        public AllSidesConnectConfig(Class<T> graph){
            super(graph);
        }

        @Override
        boolean canConnectTo(World world, BlockPos from, BlockPos to){
            return abs(from.getX()-to.getX())+abs(from.getY()-to.getY())+abs(from.getZ()-to.getZ())==1;
        }

        @Override
        BlockPos[] getConnectionPoints(World world, BlockPos from){
            return new BlockPos[]{
                from.add(0,0,1),from.add(0,1,0),from.add(1,0,0),
                from.add(0,0,-1),from.add(0,-1,0),from.add(-1,0,0)
            };
        }
    }

    public static class SidedConnectConfig<T extends BlockGraph> extends GraphConnectConfig<T>{
        int[] sides;
        public SidedConnectConfig(Class<T> graph, int[] sides){
            super(graph);
            this.sides=sides;
        }
        public SidedConnectConfig(Class<T> graph, Direction...dirs){
            this(graph,toInts(dirs));
        }

        private static int[] toInts(Direction...dirs){
            int[] s = new int[dirs.length];
            for(int i=0;i<dirs.length;i++){
                s[i]=dirs[i].getId();
            }
            return s;
        }

        @Override
        boolean canConnectTo(World world, BlockPos from, BlockPos to){
            int rx = to.getX()-from.getX();
            int ry = to.getY()-from.getY();
            int rz = to.getZ()-from.getZ();
            for(int i=0;i<sides.length;i++){
                Direction dir = Direction.byId(sides[i]);
                if( dir.getOffsetX()==rx && dir.getOffsetY()==ry && dir.getOffsetZ()==rz){
                    return true;
                }
            }
            return false;
        }

        @Override
        BlockPos[] getConnectionPoints(World world, BlockPos from){
            BlockPos[] points = new BlockPos[sides.length];
            for(int i=0;i<sides.length;i++){
                points[i] = from.offset(Direction.byId(sides[i]));
            }
            return points;
        }
    }









    public static class ConnectConfigManager{
        ObjectMap<Class,GraphConnectConfig> connectionConfig = new ObjectMap<>();
        public <T extends BlockGraph> void add(GraphConnectConfig<T>config){
             connectionConfig.put(config.graphClass,config);
        }
        public <T extends BlockGraph> GraphConnectConfig<T> get(Class<T> clazz){
            return connectionConfig.get(clazz);
        }
        public void each(Cons2<Class, GraphConnectConfig> cons){
            var iter = connectionConfig.iterator();
            while(iter.hasNext()){
                var e = iter.next();
                cons.get(e.key,e.value);
            }
        }
        // can a BlockGraphConnector connect to you?
    }
}
