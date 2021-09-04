package com.xeloklox.dungeons.unleashed.utils.block;

import com.xeloklox.dungeons.unleashed.*;
import com.xeloklox.dungeons.unleashed.gen.*;
import net.minecraft.block.enums.*;
import net.minecraft.state.property.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.Direction.*;

public class BlockStatePresets{
    public static DirectionProperty HORIZONTAL_FACING(){
        return Globals.bootQuery(() -> Properties.HORIZONTAL_FACING, DirectionProperty.of("facing", Type.HORIZONTAL));
    }

    public static DirectionProperty FACING(){
        return Globals.bootQuery(() -> Properties.FACING, DirectionProperty.of("facing"));
    }

    public static BooleanProperty UP(){
        return Globals.bootQuery(() -> Properties.UP, BooleanProperty.of("up"));
    }

    public static EnumProperty<WallShape> WALL(String dir){
        return Globals.bootQuery(() -> {
            switch(dir){
                default:
                case "east":
                    return Properties.EAST_WALL_SHAPE;
                case "west":
                    return Properties.WEST_WALL_SHAPE;
                case "north":
                    return Properties.NORTH_WALL_SHAPE;
                case "south":
                    return Properties.SOUTH_WALL_SHAPE;
            }
        }, EnumProperty.of(dir, WallShape.class));
    }
    public static BooleanProperty FENCE(String dir){
        return Globals.bootQuery(() -> {
            switch(dir){
                default:
                case "east":
                    return Properties.EAST;
                case "west":
                    return Properties.WEST;
                case "north":
                    return Properties.NORTH;
                case "south":
                    return Properties.SOUTH;
            }
        }, BooleanProperty.of(dir));
    }


    public static EnumProperty<SlabType> SLAB(){
        return Globals.bootQuery(() -> Properties.SLAB_TYPE, EnumProperty.of("type", SlabType.class));
    }

    public static EnumProperty<BlockHalf> HALF(){
        return Globals.bootQuery(() -> Properties.BLOCK_HALF, EnumProperty.of("half", BlockHalf.class));
    }

    public static EnumProperty<StairShape> STAIR(){
        return Globals.bootQuery(() -> Properties.STAIR_SHAPE, EnumProperty.of("shape", StairShape.class));
    }

    public static EnumProperty<Direction.Axis> AXIS(){
        return Globals.bootQuery(() -> Properties.AXIS, EnumProperty.of("axis", Direction.Axis.class));
    }

    public static BlockStateBuilder axisStates(String modelStr){
        return
        BlockStateBuilder.create()
        .addStateVariant(AXIS(), Axis.Y, variant -> variant.addModel(modelStr, 0))
        .addStateVariant(AXIS(), Axis.X, variant -> variant.addModel(modelStr, 90, 90))
        .addStateVariant(AXIS(), Axis.Z, variant -> variant.addModel(modelStr, 90, 0));
    }

    public static BlockStateBuilder wallStates(String wallPost, String wallSide, String wallSideTall){
        String[] dirs = {"north", "east", "south", "west"};
        BlockStateBuilder bsb = BlockStateBuilder.createMultipart();
        bsb.addPart(part ->
            part.setModel(wallPost)
                .addConditions(cond -> cond.set(UP(), true))
        );
        for(int i = 0; i < 4; i++){
            int index = i;
            bsb.addPart(part ->
            part.setModel(model -> model.setModel(wallSide).setY(index * 90).setUVLock(true))
                .addConditions(cond -> cond.set(WALL(dirs[index]), WallShape.LOW))
            );
            bsb.addPart(part ->
            part.setModel(model -> model.setModel(wallSideTall).setY(index * 90).setUVLock(true))
                .addConditions(cond -> cond.set(WALL(dirs[index]), WallShape.TALL))
            );
        }
        return bsb;
    }

    public static BlockStateBuilder stairStates(String stairs, String stairsInner, String stairsOuts){
        return BlockStateBuilder.create().stateCombinations((state, modelVariant) -> {
            Direction facing = state.get(HORIZONTAL_FACING());
            BlockHalf half = state.get(HALF());
            StairShape shape = state.get(STAIR());
            int yRot = (int)facing.rotateYClockwise().asRotation();
            if(shape == StairShape.INNER_LEFT || shape == StairShape.OUTER_LEFT){
                yRot += 270;
            }
            if(shape != StairShape.STRAIGHT && half == BlockHalf.TOP){
                yRot += 90;
            }
            yRot %= 360;
            boolean uvlock = yRot != 0 || half == BlockHalf.TOP;
            int finalYRot = yRot;
            modelVariant.addModel(model ->
            model.setModel(shape == StairShape.STRAIGHT ? stairs : (shape == StairShape.INNER_LEFT || shape == StairShape.INNER_RIGHT ? stairsInner : stairsOuts))
                .setY(finalYRot)
                .setX(half == BlockHalf.BOTTOM ? 0 : 180)
                .setUVLock(uvlock)
            );
        }, HORIZONTAL_FACING(), STAIR(), HALF());
    }
    public static BlockStateBuilder fenceStates(String fencePost, String fenceSide){
        String[] dirs = {"north", "east", "south", "west"};
        BlockStateBuilder bsb = BlockStateBuilder.createMultipart();
        bsb.addPart(part ->
            part.setModel(fencePost)
        );
        for(int i = 0; i < 4; i++){
            int index = i;
            bsb.addPart(part ->
            part.setModel(model -> model.setModel(fenceSide).setY(index * 90).setUVLock(true))
                .addConditions(cond -> cond.set(FENCE(dirs[index]), true))
            );
        }
        return bsb;
    }
    public static BlockStateBuilder slabStates(String slabTop, String slabBottom, String slabDouble){
        return
        BlockStateBuilder.create()
        .addStateVariant(SLAB(), SlabType.TOP, variant -> variant.addModel(slabTop))
        .addStateVariant(SLAB(), SlabType.BOTTOM, variant -> variant.addModel(slabBottom))
        .addStateVariant(SLAB(), SlabType.DOUBLE, variant -> variant.addModel(slabDouble));
    }
    static BlockStateBuilder directionalStates(String modelStr){
        return
            BlockStateBuilder.create()
            .addStateVariant(FACING(), Direction.UP,variant->variant.addModel(modelStr,0))
            .addStateVariant(FACING(), Direction.DOWN,variant->variant.addModel(modelStr,180,0))
            .addStateVariant(FACING(), Direction.NORTH,variant->variant.addModel(modelStr,90,0))
            .addStateVariant(FACING(), Direction.EAST,variant->variant.addModel(modelStr,90,90))
            .addStateVariant(FACING(), Direction.SOUTH,variant->variant.addModel(modelStr,90,180))
            .addStateVariant(FACING(), Direction.WEST,variant->variant.addModel(modelStr,90,270));
    }
}
