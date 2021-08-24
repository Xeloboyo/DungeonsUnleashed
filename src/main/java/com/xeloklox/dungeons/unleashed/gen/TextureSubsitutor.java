package com.xeloklox.dungeons.unleashed.gen;

import com.xeloklox.dungeons.unleashed.*;
import org.apache.commons.io.*;

import java.io.*;

public class TextureSubsitutor extends Generator{
    String notFound="";
    public TextureSubsitutor(String path,String notFound){
        super(path);
        this.notFound=notFound;
    }

    @Override
    public void generateFile() throws IOException{
        File tex = new File(path);
        File defaultTex = new File(notFound);
        if(!tex.exists()){
            try{
                System.out.println("[WARNING] Unable to find "+tex+", replacing with placeholder...");
                FileUtils.copyFile(defaultTex, tex);
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }
}
