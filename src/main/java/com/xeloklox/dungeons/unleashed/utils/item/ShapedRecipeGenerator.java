package com.xeloklox.dungeons.unleashed.utils.item;

import com.xeloklox.dungeons.unleashed.gen.RecipeJson.*;
import net.minecraft.util.*;
import org.mini2Dx.gdx.utils.*;

public class ShapedRecipeGenerator{
    String[] receipe;
    private ShapedRecipeGenerator(){};
    public static ShapedRecipeGenerator create(){
        var s = new ShapedRecipeGenerator();
        s.receipe = new String[9];
        return s;
    }

    public ShapedRecipeJson build(String id){
        Array<String> keys = new Array<>();
        for(int i=0;i<9;i++){
            if(receipe[i]==null){continue;}
            if(!keys.contains(receipe[i],false)){
                keys.add(receipe[i]);
            }
        }
        char keychar = 'A';
        ObjectMap<String,Character> keymap = new ObjectMap<>();
        for(var key:keys){
            keymap.put(key,keychar++);
        }
        String[] keyarray = new String[keys.size];
        int z =0;
        for(var keyentry:keymap){
            keyarray[z] = keyentry.key + " -> " + (char)keyentry.value;
            z++;
        }
        String[] pattern  = new String[3];
        for(int i=0;i<3;i++){
            pattern[i] = "";
            pattern[i] += receipe[i*3]==null?" ":keymap.get(receipe[i*3]);
            pattern[i] += receipe[i*3+1]==null?" ":keymap.get(receipe[i*3+1]);
            pattern[i] += receipe[i*3+2]==null?" ":keymap.get(receipe[i*3+2]);
        }
        return (new ShapedRecipeJson(id)).setKeys(keyarray).setPattern(pattern);
    }

    public ShapedRecipeGenerator corners(String item){
        receipe[0] = item;
        receipe[2] = item;
        receipe[6] = item;
        receipe[8] = item;
        return this;
    }
    public ShapedRecipeGenerator center_adjacent(String item){
        receipe[1] = item;
        receipe[3] = item;
        receipe[5] = item;
        receipe[7] = item;
        return this;
    }
    public ShapedRecipeGenerator donut(String item){
        corners(item);
        center_adjacent(item);
        return this;
    }
    public ShapedRecipeGenerator center(String item){
        receipe[4] = item;
        return this;
    }
    public ShapedRecipeGenerator fill(String item){
        donut(item);
        center(item);
        return this;
    }
    public ShapedRecipeGenerator square2x2(String item){
        receipe[0] = item;
        receipe[1] = item;
        receipe[3] = item;
        receipe[4] = item;
        return this;
    }

    public ShapedRecipeGenerator row(String item,int row){
        receipe[0+row*3] = item;
        receipe[1+row*3] = item;
        receipe[2+row*3] = item;
        return this;
    }
    public ShapedRecipeGenerator column(String item,int column){
        receipe[0+column] = item;
        receipe[3+column] = item;
        receipe[6+column] = item;
        return this;
    }
    public ShapedRecipeGenerator diagonal_cross(String item){
        corners(item);
        center(item);
        return this;
    }
    public ShapedRecipeGenerator forward_diagonal(String item){
        receipe[2] = item;
        receipe[4] = item;
        receipe[6] = item;
        return this;
    }
    public ShapedRecipeGenerator backward_diagonal(String item){
        receipe[0] = item;
        receipe[4] = item;
        receipe[8] = item;
        return this;
    }
    public static enum SlotPos{
        TOP_LEFT(0),TOP_CENTER(1),TOP_RIGHT(2),
        MIDDLE_LEFT(3),MIDDLE_CENTER(4),MIDDLE_RIGHT(5),
        BOTTOM_LEFT(6),BOTTOM_CENTER(7),BOTTOM_RIGHT(8),
        ;
        int index;

        SlotPos(int index){
            this.index = index;
        }
    }
    public ShapedRecipeGenerator slot(String item,SlotPos slot){
        receipe[slot.index] = item;
        return this;
    }
    /*
    * functions like
    *   //armor patterns
    *   //tool patterns
    *   //slabs and block variants
    * */
}
