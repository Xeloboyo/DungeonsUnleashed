package com.xeloklox.dungeons.unleashed.utils;

public class LangText implements IHasName{
    public String name;
    public String id;

    public LangText(String name, String id){
        this.name = name;
        this.id = id;
        names.add(this);
    }

    @Override
    public String getName(){
        return name;
    }

    @Override
    public String getNameID(){
        return id;
    }
}
