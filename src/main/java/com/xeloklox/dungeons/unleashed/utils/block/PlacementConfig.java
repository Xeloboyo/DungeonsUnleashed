package com.xeloklox.dungeons.unleashed.utils.block;

import com.xeloklox.dungeons.unleashed.utils.lambda.*;
import net.minecraft.block.*;
import net.minecraft.item.*;
import net.minecraft.state.property.*;

public class PlacementConfig{
    Func2<ItemPlacementContext, BasicBlock, BlockState> placementState;
    Property[] properties;
    Prov<BasicBlockModifier> modifier = ()->null;

    public PlacementConfig(Func2<ItemPlacementContext, BasicBlock, BlockState> placementState,Prov<BasicBlockModifier> m, Property... properties){
        this.placementState = placementState;
        this.properties = properties;
        if(this.properties == null){
            this.properties = new Property[]{};
        }
        this.modifier=m;
    }
    public PlacementConfig(Func2<ItemPlacementContext, BasicBlock, BlockState> placementState, Property... properties){
        this(placementState,()->null,properties);
    }

    boolean hasProperty(Property p){
        for(Property p2 : properties){
            if(p.equals(p2)){
                return true;
            }
        }
        return false;
    }
}
