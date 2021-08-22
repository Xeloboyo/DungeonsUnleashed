package com.xeloklox.dungeons.unleashed.utils.lambda;

public interface Cons<T>{
    void get(T t);

    interface Cons2<T,U>{
        void get(T t,U t2);
    }

    interface Cons3<T>{
        void get(T t,T t2,T t3);
    }
}
