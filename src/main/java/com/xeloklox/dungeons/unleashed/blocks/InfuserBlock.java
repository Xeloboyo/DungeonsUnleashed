package com.xeloklox.dungeons.unleashed.blocks;

import com.xeloklox.dungeons.unleashed.*;
import com.xeloklox.dungeons.unleashed.blockentity.*;
import com.xeloklox.dungeons.unleashed.utils.*;
import com.xeloklox.dungeons.unleashed.utils.lambda.*;
import net.fabricmc.fabric.api.object.builder.v1.block.*;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.screen.*;
import net.minecraft.state.*;
import net.minecraft.state.property.*;
import net.minecraft.util.*;
import net.minecraft.util.hit.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import org.jetbrains.annotations.*;

public class InfuserBlock extends BasicBlock implements BlockEntityProvider{
    public InfuserBlock(Material material, Func<FabricBlockSettings, FabricBlockSettings> func){
        super(material, func);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state){
        return new InfuserEntity(pos,state);
    }

    @Override
    public <T extends BlockEntity> Class<T> getEntityClass(){
        return (Class<T>)InfuserEntity.class;
    }

    @Override
    public void onDisturbed(BlockState state, World world, BlockPos pos){
        recalcJarAttachment(world,pos,state);
    }

    public static Vec3f[] connectionRelative = {new Vec3f(-1,0,0),new Vec3f(0,0,1),new Vec3f(1,0,0)};

    public static void recalcJarAttachment(World world, BlockPos pos, BlockState state){
        if(world.isClient){return;}
        Direction d = state.get(Properties.HORIZONTAL_FACING);

        boolean[] jarAttach = new boolean[connectionRelative.length]; //left back right
        for(int i=0;i<connectionRelative.length;i++){
            Vec3i v = Mathf.relativeDirectionHorz(d,connectionRelative[i]);
            jarAttach[i] = isJar(world,pos.add(v));
        }
        BlockEntity be = world.getBlockEntity(pos);
        if(be instanceof InfuserEntity infuserEntity){
            infuserEntity.setJarAttach(jarAttach);
            infuserEntity.sync();
        }

    }

    public static boolean isJar(World world, BlockPos pos){
        BlockState bs = world.getBlockState(pos);
        return bs.getBlock() instanceof IChargeStorage;
    }
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlocks.INFUSER_ENTITY.get(), (world1, pos, state1, be) -> InfuserEntity.tick(world1, pos, state1, be));
    }

}
