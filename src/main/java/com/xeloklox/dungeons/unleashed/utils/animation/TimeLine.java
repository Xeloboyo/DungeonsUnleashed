package com.xeloklox.dungeons.unleashed.utils.animation;

import com.xeloklox.dungeons.unleashed.utils.animation.Interpolations.*;
import com.xeloklox.dungeons.unleashed.utils.animation.TimeLine.*;
import net.minecraft.util.math.*;
import org.mini2Dx.gdx.utils.*;

public abstract class TimeLine<T>{
    Array<KeyFrame> keyframes = new Array<>();
    boolean empty = true;
    float endtime = 0;
    float starttime = Float.MAX_VALUE;

    class KeyFrame{
        float time;
        T value;
        InterpolateType interpolateType = SingularInterpolateType.LINEAR;

        public KeyFrame(T value, float time){
            this.time = time;
            this.value = value;
        }
    }

    public KeyFrame add(T t, float time){
        var v = new KeyFrame(t, time);
        keyframes.add(v);
        keyframes.sort((a, b) -> Float.compare(a.time, b.time));
        empty = false;
        endtime = Math.max(endtime, time);
        starttime = Math.min(starttime, time);
        return v;
    }

    public KeyFrame add(T t, float time, InterpolateType it){
        var v = add(t, time);
        v.interpolateType = it;
        return v;
    }

    abstract T interpolate(KeyFrame prev, KeyFrame x1, KeyFrame x2, KeyFrame after, float t);

    private KeyFrame p, x1, x2, a;

    private void recalcFrame(float time){
        p = keyframes.get(0);
        x1 = keyframes.get(0);
        x2 = keyframes.get(0);
        int index = 0;
        while(x2.time < time && index < keyframes.size - 1){
            index++;
            p = x1;
            x1 = x2;
            x2 = keyframes.get(index);
        }
        a = keyframes.get(MathHelper.clamp(index + 1, 0, keyframes.size - 1));
    }

    T get(float time){
        if(keyframes.isEmpty()){
            return null;
        }
        if(keyframes.size == 1 || time <= starttime){
            return keyframes.get(0).value;
        }
        if(time >= endtime){
            return keyframes.get(keyframes.size - 1).value;
        }
        if(keyframes.size == 2){
            float dt = keyframes.get(1).time - keyframes.get(0).time;
            return interpolate(keyframes.get(0), keyframes.get(0), keyframes.get(1), keyframes.get(1), (time - keyframes.get(0).time) / dt);
        }

        if(x1 == null || time < x1.time || time > x2.time){
            recalcFrame(time);
        }
        float dt = x2.time - x1.time;
        return interpolate(p, x1, x2, a, (time - x1.time) / dt);
    }


    public static class Vec3fTimeLine extends TimeLine<Vec3f>{
        @Override
        Vec3f interpolate(KeyFrame prev, KeyFrame x1, KeyFrame x2, KeyFrame after, float t){
            float dur = x2.time - x1.time;
            float pt = (prev.time - x1.time) / dur;
            float at = 1 + (after.time - x2.time) / dur;
            if(at == 1){
                at += 1;
            }
            if(pt == 0){
                pt -= 1;
            }
            float x = x1.interpolateType.interpolate(prev.value.getX(), pt, x1.value.getX(), x2.value.getX(), after.value.getX(), at, t, 0.2f);
            float y = x1.interpolateType.interpolate(prev.value.getY(), pt, x1.value.getY(), x2.value.getY(), after.value.getY(), at, t, 0.2f);
            float z = x1.interpolateType.interpolate(prev.value.getZ(), pt, x1.value.getZ(), x2.value.getZ(), after.value.getZ(), at, t, 0.2f);
            return new Vec3f(x, y, z);
        }
    }
}
