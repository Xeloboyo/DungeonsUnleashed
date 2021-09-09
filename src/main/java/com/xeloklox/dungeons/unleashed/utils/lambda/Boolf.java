package com.xeloklox.dungeons.unleashed.utils.lambda;

public interface Boolf<T>{
    boolean get(T t);

    public interface Boolf2<T,G>{
        boolean get(T t, G g);
    }

    public interface Boolf3<T,G,H>{
        boolean get(T t, G g, H h);
    }
}
