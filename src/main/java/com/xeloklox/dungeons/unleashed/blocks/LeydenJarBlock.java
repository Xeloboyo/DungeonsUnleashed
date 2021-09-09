package com.xeloklox.dungeons.unleashed.blocks;

import com.xeloklox.dungeons.unleashed.utils.block.*;
import com.xeloklox.dungeons.unleashed.utils.lambda.*;
import net.fabricmc.fabric.api.object.builder.v1.block.*;
import net.minecraft.block.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.server.world.*;
import net.minecraft.sound.*;
import net.minecraft.state.StateManager.*;
import net.minecraft.state.property.*;
import net.minecraft.util.*;
import net.minecraft.util.hit.*;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.*;
import net.minecraft.world.*;

import java.util.*;

public class LeydenJarBlock extends BasicBlock implements IAffectedByLightning, Oxidizable, IChargeAccessor{
    public static final int MAX_CHARGE = 4;
    public static final IntProperty CHARGE = IntProperty.of("charge",0,MAX_CHARGE);
    public LeydenJarBlock(Material material, Func<FabricBlockSettings, FabricBlockSettings> func){
        super(material, func);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        return VoxelShapes.cuboid(0.1875f, 0f, 0.1875f, 0.8125f, 0.75f, 0.8125f);
    }

    @Override
    protected void appendProperties(Builder<Block, BlockState> builder){
        super.appendProperties(builder);
        builder.add(CHARGE);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit){
        player.getInventory().insertStack(getPickStack(world,pos,state));
        world.setBlockState(pos,Blocks.AIR.getDefaultState());
        player.playSound(SoundEvents.BLOCK_METAL_HIT,0.8f,1);
        return ActionResult.SUCCESS;
    }

    @Override
    public void onStruck(World world, BlockPos pos){
        if(world.isClient){return;}
        BlockState state =  world.getBlockState(pos);
        int charge = state.get(CHARGE);
        if(charge<MAX_CHARGE){
            world.setBlockState(pos, state.with(CHARGE, charge+1));

        }
    }


    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random){
        super.randomDisplayTick(state, world, pos, random);
    }

    @Override
    public OxidizationLevel getDegradationLevel(){
        return OxidizationLevel.UNAFFECTED;
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state){
        ItemStack jar = super.getPickStack(world, pos, state);;
        NbtCompound nbt = new NbtCompound();
        nbt.putString("charge", ""+state.get(CHARGE));
        jar.setSubNbt("BlockStateTag", nbt);
        return jar;
    }

    @Override
    public void tickDegradation(BlockState state, ServerWorld world, BlockPos pos, Random random){
        //dont
    }

    @Override
    public void tryDegrade(BlockState state, ServerWorld world, BlockPos pos, Random random){
        //just dont
    }

    @Override
    public int getCharge(World world, BlockPos pos){
        return world.getBlockState(pos).get(CHARGE);
    }

    @Override
    public int maxCharge(World world, BlockPos pos){
        return MAX_CHARGE;
    }

    @Override
    public void setCharge(World world, BlockPos pos, int charge){
        world.setBlockState(pos, world.getBlockState(pos).with(CHARGE, charge));
    }
}
