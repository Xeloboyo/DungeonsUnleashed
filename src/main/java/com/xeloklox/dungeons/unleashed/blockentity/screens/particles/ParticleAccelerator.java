package com.xeloklox.dungeons.unleashed.blockentity.screens.particles;

import net.minecraft.util.math.*;

public class ParticleAccelerator extends ParticleAffector{
    float amount;
    public ParticleAccelerator(float radius, float x, float y,float amount){
        super(radius, x, y);
        this.amount=amount;
    }

    @Override
    public void affect(UIParticle particle){
        float dx = x-particle.x;
        float dy = y-particle.y;
        if(dx*dx+dy*dy>radius*radius){return;}
        particle.vx*=amount;
        particle.vy*=amount;
    }
}
