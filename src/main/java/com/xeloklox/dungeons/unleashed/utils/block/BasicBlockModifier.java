package com.xeloklox.dungeons.unleashed.utils.block;

import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.block.enums.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.fluid.*;
import net.minecraft.item.*;
import net.minecraft.screen.*;
import net.minecraft.state.*;
import net.minecraft.state.StateManager.*;
import net.minecraft.state.property.*;
import net.minecraft.state.property.Property;
import net.minecraft.util.*;
import net.minecraft.util.hit.*;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.*;
import net.minecraft.world.*;
import org.jetbrains.annotations.*;
import org.mini2Dx.gdx.utils.Array;

import java.lang.reflect.*;

public abstract class BasicBlockModifier{
    BasicBlock parent;

    public void setParent(BasicBlock parent){
        this.parent = parent;
    }
    public void onCreate(){};
    public abstract boolean canReplace(BlockState state, ItemPlacementContext context);
    public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState){return true;}
    public boolean canFillWithFluid(BlockView world, BlockPos pos, BlockState state, Fluid fluid){return true;}
    public abstract BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos);
    public boolean hasSidedTransparency(BlockState state){return false;}
    public abstract BlockState rotate(BlockState state, BlockRotation rotation);
    public abstract BlockState mirror(BlockState state, BlockMirror mirror);
    public abstract VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context);
    public abstract VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context);
    public abstract BlockState getPlacementState(ItemPlacementContext ctx);
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        return ActionResult.PASS;
    }
}
