package com.xeloklox.dungeons.unleashed.items.hooks;

import com.xeloklox.dungeons.unleashed.items.*;
import com.xeloklox.dungeons.unleashed.utils.*;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.*;
import net.minecraft.item.*;
import net.minecraft.particle.*;
import net.minecraft.util.math.*;
import net.minecraft.world.explosion.*;

public class UnstableExplosiveItemHook extends ItemEntityHook{
    float chance = 0.005f;
    float explosionSize = 1f;
    int immunity = 100;
    int maxCharge = 250;
    String EXPLOSION_CHARGE_TAG = "expchrg";
    //per item in %
    float explosionPowerIncrease = 3f;
    public UnstableExplosiveItemHook(){}

    public UnstableExplosiveItemHook(float chance, float explosionSize){
        this.chance = chance;
        this.explosionSize = explosionSize;
    }

    public int getExplosionCharge(){
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
                    Mathf.rand((rx,ry,rz)-> {
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
                Mathf.rand((rx,ry,rz)->{
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
}
