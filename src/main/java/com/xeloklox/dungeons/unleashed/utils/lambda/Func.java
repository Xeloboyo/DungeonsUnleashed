package com.xeloklox.dungeons.unleashed.utils.lambda;

import java.util.function.*;

public interface Func<I,O>{
    O get(I t);
    //:D i hate myself
    public static <I,O> Func<I,O> fromFunction(Function<I,O> f){
        return f::apply;
    }
}
