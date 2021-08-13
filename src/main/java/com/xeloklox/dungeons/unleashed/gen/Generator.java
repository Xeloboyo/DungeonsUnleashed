package com.xeloklox.dungeons.unleashed.gen;

import com.xeloklox.dungeons.unleashed.*;

import java.io.*;

public abstract class Generator{
    public Generator(){
        AssetGenerator.generators.add(this);
    }
    public abstract void generateFile() throws IOException;
}
