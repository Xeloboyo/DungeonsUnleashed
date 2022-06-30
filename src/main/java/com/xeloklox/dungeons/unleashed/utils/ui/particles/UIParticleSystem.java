package com.xeloklox.dungeons.unleashed.utils.ui.particles;

import com.xeloklox.dungeons.unleashed.utils.ui.*;
import net.minecraft.client.util.math.*;
import org.mini2Dx.gdx.utils.*;

public class UIParticleSystem{
    Array<UIParticle> particles = new Array<>(false,50);
    Array<ParticleAffector> affectors = new Array<>();

    public UIParticle add(UIParticle p){
        particles.add(p);
        return p;
    }
    public void addAffector(ParticleAffector p){
        affectors.add(p);
    }

    public void update(){
        for(int i=0;i<affectors.size;i++){
            for(int p=0;p<particles.size;p++){
                if((affectors.get(i).layer & particles.get(p).layer) ==0){continue;}
                if(affectors.get(i).endTime < particles.get(p).life || affectors.get(i).startTime > particles.get(p).life){continue;}
                affectors.get(i).affect(particles.get(p));
            }
        }
        for(int i=0;i<particles.size;i++){
            particles.get(i).update();
            if(particles.get(i).life<0 && !particles.get(i).cantDie){
                particles.removeIndex(i);
                i--;
            }
        }
    }

    public void draw(MatrixStack matrices, AnimatedScreen at){
        for(int i=0;i<particles.size;i++){
            for(int p=0;p<particles.get(i).renderAffectors.length;p++){
                particles.get(i).renderAffectors[p].effect.get(matrices,at,particles.get(i));
            }
            particles.get(i).draw(matrices,at);
        }
    }
}
