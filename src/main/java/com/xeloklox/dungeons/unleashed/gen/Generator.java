package com.xeloklox.dungeons.unleashed.gen;

import com.xeloklox.dungeons.unleashed.*;

import java.io.*;

public abstract class Generator{
    protected String path;
    public Generator(String path){
        this.path=path;
        AssetGenerator.generators.add(this);
    }
    public void pregenerate(){ };
    public abstract void generateFile() throws IOException;

    public String getPath(){
        return path;
    }
}
