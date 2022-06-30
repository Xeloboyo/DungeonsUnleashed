package com.xeloklox.dungeons.unleashed.utils.ui.particles;

public class ParticleDeleter extends ParticleAffector{
    boolean invert = false;
    public ParticleDeleter(float radius, float x, float y){
        super(radius, x, y);
    }
    public static ParticleDeleter create(float radius, float x, float y){
        return new ParticleDeleter(radius,x,y);
    }
    public ParticleDeleter setInvert(boolean invert){
        this.invert = invert;
        return this;
    }

    @Override
    public void affect(UIParticle particle){
        float dx = x-particle.x;
        float dy = y-particle.y;
        if(dx*dx+dy*dy>radius*radius ^ invert){return;}
        particle.life=-99;
    }
}
