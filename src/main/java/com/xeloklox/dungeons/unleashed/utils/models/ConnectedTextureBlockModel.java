package com.xeloklox.dungeons.unleashed.utils.models;

import com.xeloklox.dungeons.unleashed.*;
import com.xeloklox.dungeons.unleashed.gen.*;
import com.xeloklox.dungeons.unleashed.utils.lambda.*;
import net.fabricmc.fabric.api.renderer.v1.mesh.*;
import net.fabricmc.fabric.api.renderer.v1.render.*;
import net.minecraft.block.*;
import net.minecraft.client.render.model.*;
import net.minecraft.client.util.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

import java.util.*;
import java.util.function.*;

public class ConnectedTextureBlockModel extends GeneratedModel{

    //0 1 2
    //3 X 4
    //5 6 7
    //indexes correspond to
    static final int[] indexes = {
    27,27,19,19,27,27,19,19,26,26,13,18,26,26,13,18,
    24,24,12,12,24,24,16,16,25,25,29,41,25,25,31,17,
    27,27,19,19,27,27,19,19,26,26,13,18,26,26,13,18,
    24,24,12,12,24,24,16,16,25,25,29,41,25,25,31,17,
    3,3,11,11,3,3,11,11,5,5,21,23,5,5,21,23,
    4,4,28,28,4,4,40,40,20,20,34,35,20,20,43,48,
    3,3,11,11,3,3,11,11,2,2,33,10,2,2,33,10,
    4,4,28,28,4,4,40,40,22,22,59,56,22,22,42,6,
    27,27,19,19,27,27,19,19,26,26,13,18,26,26,13,18,
    24,24,12,12,24,24,16,16,25,25,29,41,25,25,31,17,
    27,27,19,19,27,27,19,19,26,26,13,18,26,26,13,18,
    24,24,12,12,24,24,16,16,25,25,29,41,25,25,31,17,
    3,3,11,11,3,3,11,11,5,5,21,23,5,5,21,23,
    0,0,30,30,0,0,8,8,32,32,51,50,32,32,49,7,
    3,3,11,11,3,3,11,11,2,2,33,10,2,2,33,10,
    0,0,30,30,0,0,8,8,1,1,57,14,1,1,15,9};


    static void getUV(int index, float[] input){
        input[0] = (indexes[index] & 7)*0.125f;
        input[1] = (indexes[index] >> 3)*0.125f;
    }
    /*
    *  DOWN(0, 1, -1, "down", Direction.AxisDirection.NEGATIVE, Direction.Axis.Y, new Vec3i(0, -1, 0)),
       UP(1, 0, -1, "up", Direction.AxisDirection.POSITIVE, Direction.Axis.Y, new Vec3i(0, 1, 0)),
       NORTH(2, 3, 2, "north", Direction.AxisDirection.NEGATIVE, Direction.Axis.Z, new Vec3i(0, 0, -1)),
       SOUTH(3, 2, 0, "south", Direction.AxisDirection.POSITIVE, Direction.Axis.Z, new Vec3i(0, 0, 1)),
       WEST(4, 5, 1, "west", Direction.AxisDirection.NEGATIVE, Direction.Axis.X, new Vec3i(-1, 0, 0)),
       EAST(5, 4, 3, "east", Direction.AxisDirection.POSITIVE, Direction.Axis.X, new Vec3i(1, 0, 0));
    *
    * */
    static final Vec3i[] offsets = {
        new Vec3i(-1,0,1), new Vec3i(-1,0,0), new Vec3i(-1,0,-1), new Vec3i(0,0,1), new Vec3i(0,0,-1),  new Vec3i(1,0,1), new Vec3i(1,0,0), new Vec3i(1,0,-1),
        new Vec3i(-1,0,1), new Vec3i(-1,0,0), new Vec3i(-1,0,-1), new Vec3i(0,0,1), new Vec3i(0,0,-1),  new Vec3i(1,0,1), new Vec3i(1,0,0), new Vec3i(1,0,-1),
        new Vec3i(-1,1,0), new Vec3i(0,1,0), new Vec3i(1,0,0), new Vec3i(-1,0,0), new Vec3i(1,0,0),  new Vec3i(-1,-1,0), new Vec3i(0,-1,0), new Vec3i(1,-1,0),
        new Vec3i(-1,1,0), new Vec3i(0,1,0), new Vec3i(1,0,0), new Vec3i(-1,0,0), new Vec3i(1,0,0),  new Vec3i(-1,-1,0), new Vec3i(0,-1,0), new Vec3i(1,-1,0),
        new Vec3i(0,1,-1), new Vec3i(0,1,0), new Vec3i(0,1,1), new Vec3i(0,0,-1), new Vec3i(0,0,1),  new Vec3i(0,-1,-1), new Vec3i(0,-1,0), new Vec3i(0,-1,1),
        new Vec3i(0,1,-1), new Vec3i(0,1,0), new Vec3i(0,1,1), new Vec3i(0,0,-1), new Vec3i(0,0,1),  new Vec3i(0,-1,-1), new Vec3i(0,-1,0), new Vec3i(0,-1,1),
    };
    static final Vec3i[] offsetsFront = new Vec3i[offsets.length];

    static final int lockedRotationVertexOrder[][] = {
        {0,1,2,3},
        {1,0,3,2},
        {3,0,1,2},
        {0,3,2,1},
        {0,3,2,1},
        {3,0,1,2},

    };

    static {
        for(int i =0;i<offsets.length;i++){
            offsetsFront[i] = offsets[i].offset(Direction.byId(i/8));
        }
    }

    Boolf<BlockState> connectsTo = (b)->true;

    public ConnectedTextureBlockModel(SpriteIdentifier texture, String name, Boolf<BlockState> connectsTo){
        super(new SpriteIdentifier[]{texture}, name);
        new TextureSubsitutor(Paths.texture+texture.getTextureId().getPath()+".png",Paths.texture+"block/connected_default.png");
        this.connectsTo=connectsTo;
    }

    @Override
    public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context){
        QuadEmitter emitter = context.getEmitter();
        for(Direction direction : Direction.values()) {
            BlockPos bp  = pos.offset(direction);
            if(!blockView.getBlockState(bp).isOpaqueFullCube(blockView,bp)){
                int index = 0;
                int dirId = direction.getId();
                if(dirId*8<offsets.length){
                    for(int i =0;i<8;i++){
                        int offsetindex = dirId*8+i;
                        if(connectsTo.get(blockView.getBlockState(pos.add(offsets[offsetindex]))) &&
                            !blockView.getBlockState(pos.add(offsetsFront[offsetindex])).isOpaqueFullCube(blockView,pos.add(offsetsFront[offsetindex]))){
                            index+=1<<i;
                        }
                    }
                }
                emitTexturedFace(emitter,index,direction);
            }
        }
    }

    @Override
    public void bake(QuadEmitter emitter, ModelLoader loader, ModelBakeSettings rotationContainer, Identifier modelId){
        for(Direction direction : Direction.values()) {
            emitTexturedFace(emitter,0,direction);
        }
    }
    float[] tempf = new float[2];
    float unitTile = 0.125f;
    void emitTexturedFace(QuadEmitter emitter, int index, Direction direction){
        emitter.square(direction, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f);
        int dirId = direction.getId();
        getUV(index,tempf);
        emitter.sprite(lockedRotationVertexOrder[dirId][0],0,tempf[0],tempf[1]);
        emitter.sprite(lockedRotationVertexOrder[dirId][1],0,tempf[0]+unitTile,tempf[1]);
        emitter.sprite(lockedRotationVertexOrder[dirId][2],0,tempf[0]+unitTile,tempf[1]+unitTile);
        emitter.sprite(lockedRotationVertexOrder[dirId][3],0,tempf[0],tempf[1]+unitTile);
        emitter.spriteBake(0, sprites[0], MutableQuadView.BAKE_NORMALIZED);
        emitter.spriteColor(0, -1, -1, -1, -1);
        emitter.emit();
    }


}
