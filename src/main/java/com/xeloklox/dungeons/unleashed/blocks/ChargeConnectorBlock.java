package com.xeloklox.dungeons.unleashed.blocks;

import com.xeloklox.dungeons.unleashed.blockentity.*;
import com.xeloklox.dungeons.unleashed.utils.*;
import com.xeloklox.dungeons.unleashed.utils.block.*;
import com.xeloklox.dungeons.unleashed.utils.lambda.*;
import net.fabricmc.fabric.api.object.builder.v1.block.*;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.state.property.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

public abstract class ChargeConnectorBlock extends BasicBlock{

    public Vec3f[] connectionRelative = {new Vec3f(-1,0,0),new Vec3f(0,0,1),new Vec3f(1,0,0)};

    public ChargeConnectorBlock(Material material, Func<FabricBlockSettings, FabricBlockSettings> settingsfunc, Cons<BasicBlock> additionalSettings, Vec3f[] connectionRelative){
        super(material, settingsfunc, additionalSettings);
        this.connectionRelative=connectionRelative;
    }

    public ChargeConnectorBlock(Material material, Func<FabricBlockSettings, FabricBlockSettings> settingsfunc, Vec3f[] connectionRelative){
        super(material, settingsfunc);
        this.connectionRelative=connectionRelative;
    }

    public static boolean isChargeAccessPoint(World world, BlockPos pos){
        BlockState bs = world.getBlockState(pos);
        return bs.getBlock() instanceof IChargeAccessor;
    }
    public void recalcJarAttachment(World world, BlockPos pos, BlockState state){
        if(world.isClient){return;}
        Direction d = state.get(Properties.HORIZONTAL_FACING);

        boolean[] jarAttach = new boolean[connectionRelative.length]; //left back right
        for(int i=0;i<connectionRelative.length;i++){
            Vec3i v = Mathf.relativeDirectionHorz(d,connectionRelative[i]);
            jarAttach[i] = isChargeAccessPoint(world,pos.add(v));
        }
        BlockEntity be = world.getBlockEntity(pos);
        if(be instanceof ChargeConnectingEntity infuserEntity){
            infuserEntity.setJarAttach(jarAttach);
            infuserEntity.sync();
        }

    }
    @Override
    public void onDisturbed(BlockState state, World world, BlockPos pos){
            recalcJarAttachment(world,pos,state);
        }
}
