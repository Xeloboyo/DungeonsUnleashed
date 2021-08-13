package com.xeloklox.dungeons.unleashed.gen;

import com.xeloklox.dungeons.unleashed.*;
import org.mini2Dx.gdx.utils.*;

import java.io.*;

public class AssetGenerator{
    public static final Array<Generator> generators = new Array<>();
    public static void main(String args[]) throws ClassNotFoundException{
        Globals.bootStrapped = false;
        Class.forName(ModItems.class.getName());
        Class.forName(ModBlocks.class.getName());
        generators.forEach(gen->{
            try{
                gen.generateFile();
            }catch(IOException e){
                e.printStackTrace();
            }
        });
    }
}
