package com.xeloklox.dungeons.unleashed.blocks;

import com.xeloklox.dungeons.unleashed.blockentity.*;
import com.xeloklox.dungeons.unleashed.utils.lambda.*;
import net.fabricmc.fabric.api.object.builder.v1.block.*;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.screen.*;
import net.minecraft.state.StateManager.*;
import net.minecraft.util.*;
import net.minecraft.util.hit.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import org.jetbrains.annotations.*;

public class BasicBlock extends Block{
    //Super.class.isAssignableFrom(Sub.class)

    public BasicBlock(Material material, Func<FabricBlockSettings,FabricBlockSettings> func){
        super(func.get(FabricBlockSettings.of(material)));
    }


    public <T extends BlockEntity> Class<T> getEntityClass(){
        return null;
    }

    @Nullable
    @Override
    public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos){
        boolean isGUI = getEntityClass()!=null && NamedScreenHandlerFactory.class.isAssignableFrom(getEntityClass());
        if(isGUI){
            BlockEntity blockEntity = world.getBlockEntity(pos);
            return blockEntity instanceof NamedScreenHandlerFactory ? (NamedScreenHandlerFactory)blockEntity : null;
        }else{
            return super.createScreenHandlerFactory(state, world, pos);
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit){
        boolean isGUI = getEntityClass()!=null && NamedScreenHandlerFactory.class.isAssignableFrom(getEntityClass());
        System.out.println(isGUI);
        if (!world.isClient && isGUI) {
            NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);
            if (screenHandlerFactory != null) {
                //With this call the server will request the client to open the appropriate Screenhandler
                player.openHandledScreen(screenHandlerFactory);
                System.out.println("screen opening!");
            }
            return ActionResult.SUCCESS;
        }
        return isGUI?ActionResult.SUCCESS:ActionResult.PASS;
    }


    @Override
    protected void appendProperties(Builder<Block, BlockState> builder){
        super.appendProperties(builder);
    }

    @Nullable
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> checkType(BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker) {
      return expectedType == givenType ? (BlockEntityTicker<A>)ticker : null;
    }

    public void onDisturbed(BlockState state, World world, BlockPos pos){ }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify){
        super.onBlockAdded(state, world, pos, oldState, notify);
        onDisturbed(state,world,pos);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack){
        super.onPlaced(world, pos, state, placer, itemStack);
        onDisturbed(state,world,pos);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify){
        super.neighborUpdate(state, world, pos, block, fromPos, notify);
        onDisturbed(state,world,pos);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved){
        if(newState.getBlock() instanceof InfuserBlock){
            onDisturbed(state,world,pos);
        }else{
            if(getEntityClass()!=null && Inventory.class.isAssignableFrom(getEntityClass())){
                BlockEntity blockEntity = world.getBlockEntity(pos);
                if (blockEntity instanceof InfuserEntity infuser) {
                    ItemScatterer.spawn(world, pos, infuser);
                    // update comparators
                    world.updateComparators(pos,this);
                }
            }
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }
}
