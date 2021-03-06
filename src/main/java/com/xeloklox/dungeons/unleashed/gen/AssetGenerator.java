package com.xeloklox.dungeons.unleashed.gen;

import com.xeloklox.dungeons.unleashed.*;
import com.xeloklox.dungeons.unleashed.utils.*;
import org.mini2Dx.gdx.utils.*;

import java.io.*;

public class AssetGenerator{
    public static final Array<Generator> generators = new Array<>();
    public static boolean isQueued(String path){
        for(int i = 0;i<generators.size;i++){
            if(generators.get(i).getPath().equals(path)){
                return true;
            }
        }
        return false;
    }


    public static void main(String args[]) throws ClassNotFoundException{
        Globals.bootStrapped = false;
        Class.forName(ModItems.class.getName());
        Class.forName(ModBlocks.class.getName());
        Class.forName(ModRecipes.class.getName());
        Class.forName(ModResearch.class.getName());
        generators.forEach(Generator::pregenerate);
        generators.forEach(gen->{
            try{
                gen.generateFile();
            }catch(IOException e){
                e.printStackTrace();
            }
        });
        IHasName.generateLang(Paths.modResource+"lang/en_us.json");


    }
}
