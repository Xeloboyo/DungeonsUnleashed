package com.xeloklox.dungeons.unleashed.blockentity.screens.particles;

public abstract class ParticleAffector{
    float radius;
    public float x;
    public float y;
    public long layer = 1;
    float startTime=0,endTime=Float.MAX_VALUE;


    public ParticleAffector(float radius, float x, float y){
        this.radius = radius;
        this.x = x;
        this.y = y;
    }

    public abstract void affect(UIParticle particle);

    public ParticleAffector setStartTime(float startTime){
        this.startTime = startTime;
        return this;
    }

    public ParticleAffector setEndTime(float endTime){
        this.endTime = endTime;
        return this;
    }

    public ParticleAffector setLayer(int layer, boolean enabled){
        this.layer = (this.layer&(1L<<layer))|(enabled?1L:0L)<<layer;
        return this;
    }
    public ParticleAffector setToLayer(int layer){
        this.layer = 1L<<layer;
        return this;
    }
}
