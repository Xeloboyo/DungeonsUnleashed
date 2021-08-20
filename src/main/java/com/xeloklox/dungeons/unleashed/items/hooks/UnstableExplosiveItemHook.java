package com.xeloklox.dungeons.unleashed.items.hooks;

import com.xeloklox.dungeons.unleashed.items.*;
import com.xeloklox.dungeons.unleashed.utils.*;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.*;
import net.minecraft.particle.*;
import net.minecraft.world.explosion.*;

public class UnstableExplosiveItemHook extends ItemEntityHook{
    float chance = 0.005f;
    float explosionSize = 1f;
    int immunity = 100;
    int maxCharge = 250;
    public static final String EXPLOSION_CHARGE_TAG = "expchrg";
    //per item in %
    float explosionPowerIncrease = 3f;

    public UnstableExplosiveItemHook(){
    }

    public UnstableExplosiveItemHook(float chance, float explosionSize){
        this.chance = chance;
        this.explosionSize = explosionSize;
    }

    public int getExplosionCharge(){
        if(!hasTag(EXPLOSION_CHARGE_TAG)){return 0;}
        return getOrCreateTag(EXPLOSION_CHARGE_TAG,0);
    }
    public void setExplosionCharge(int value){
        getNBT().putInt(EXPLOSION_CHARGE_TAG, value);
        wrapper.syncItem();
    }
    @Override
    public void onTick(){
        int explosionCharge  = getExplosionCharge();
        float chanceam = 0.05f*(1f+explosionCharge);
        if(!wrapper.client()){
            immunity--;
            if(immunity < 0){
                float jitterchance = Math.min(chanceam*0.1f,0.1f);
                if(Math.random()<jitterchance){
                    Mathf.randVec3((rx, ry, rz)-> {
                        itemEntity().setVelocity(rx*0.2,ry*0.2,rz*0.2);
                    });
                }


                if(Math.random() < chance && explosionCharge == 0){
                    setExplosionCharge(explosionCharge + 1);
                }else if(explosionCharge > maxCharge){
                    itemEntity().discard();
                    world().createExplosion(itemEntity(), itemEntity().getX()+0.01f, itemEntity().getY(), itemEntity().getZ(),
                    (float)(explosionSize + itemstack().getCount() * explosionPowerIncrease * 0.01f), true, Explosion.DestructionType.DESTROY);
                }else if(explosionCharge > 0){
                    setExplosionCharge(explosionCharge + 1);
                }
            }
        }else{

            while(Math.random()<chanceam){
                ItemEntity pos = itemEntity();
                float speed = Math.max(explosionCharge*0.05f,1.5f);
                Mathf.randVec3((rx, ry, rz)->{
                    world().addParticle(ParticleTypes.PORTAL,
                        pos.getX()+rx*speed, pos.getY()+ry*speed, pos.getZ()+rz*speed,
                        -rx*speed, -ry*speed, -rz*speed);
                });
                chanceam-=1;
            }

        }
    }

    @Override
    public void onRemove(){

    }

    @Override
    public void onDamage(DamageSource source, float amount){

    }

    public UnstableExplosiveItemHook setChancePerTick(float chance){
        this.chance = chance;
        return this;
    }

    public UnstableExplosiveItemHook setExplosionSize(float explosionSize){
        this.explosionSize = explosionSize;
        return this;
    }

    public UnstableExplosiveItemHook setImmunity(int immunity){
        this.immunity = immunity;
        return this;
    }

    public UnstableExplosiveItemHook setDelay(int maxCharge){
        this.maxCharge = maxCharge;
        return this;
    }

    public UnstableExplosiveItemHook setExplosionPowerIncrease(float explosionPowerIncrease){
        this.explosionPowerIncrease = explosionPowerIncrease;
        return this;
    }

}
