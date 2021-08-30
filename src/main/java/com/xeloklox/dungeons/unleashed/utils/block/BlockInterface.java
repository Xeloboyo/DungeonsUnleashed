package com.xeloklox.dungeons.unleashed.utils.block;

import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.fluid.*;
import net.minecraft.item.*;
import net.minecraft.screen.*;
import net.minecraft.state.*;
import net.minecraft.state.StateManager.*;
import net.minecraft.state.property.Property;
import net.minecraft.util.*;
import net.minecraft.util.hit.*;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.*;
import net.minecraft.world.*;
import org.jetbrains.annotations.*;
import org.mini2Dx.gdx.utils.Array;

import java.lang.reflect.*;

public interface BlockInterface{

    public static Array<Property> propertiesToAppend = new Array<>();
    public void appendProperties(Builder<Block, BlockState> builder);

    public static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> checkType(BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker){
        return expectedType == givenType ? (BlockEntityTicker<A>)ticker : null;
    }
    public static void replaceStateManager(Block b, StateManager<Block, BlockState> stateManager){
        try{
            Field f = b.getClass().getDeclaredField("stateManager");
            f.setAccessible(true);
            f.set(b, stateManager);
        }catch(NoSuchFieldException | IllegalAccessException e){
            e.printStackTrace();
        }
    }

    default void onDisturbed(BlockState state, World world, BlockPos pos){}

    default void onDestroyed(BlockState state, World world, BlockPos pos){}

    default void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify){
        onDisturbed(state,world,pos);
    }

    default void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack){
        onDisturbed(state,world,pos);
    }

    default void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify){
        onDisturbed(state,world,pos);
    }

    default void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved){
        if(state.getBlock()!=newState.getBlock()){
            onDestroyed(state,world,pos);
        }
        onDisturbed(state,world,pos);
    }
    default ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit){
        boolean isGUI = getEntityClass() != null && NamedScreenHandlerFactory.class.isAssignableFrom(getEntityClass());
        if(!world.isClient && isGUI){
            NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);
            if(screenHandlerFactory != null){
                //With this call the server will request the client to open the appropriate Screenhandler
                player.openHandledScreen(screenHandlerFactory);
            }
            return ActionResult.SUCCESS;
        }
        return isGUI ? ActionResult.SUCCESS : ActionResult.PASS;
    }
    default NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos){
        boolean isGUI = getEntityClass() != null && NamedScreenHandlerFactory.class.isAssignableFrom(getEntityClass());
        if(isGUI){
            BlockEntity blockEntity = world.getBlockEntity(pos);
            return blockEntity instanceof NamedScreenHandlerFactory ? (NamedScreenHandlerFactory)blockEntity : null;
        }else{
            return null;
        }
    }
    default <T extends BlockEntity> Class<T> getEntityClass(){
        return null;
    }


}
