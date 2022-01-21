package io.github.gaming32.pipeline.iteratorbuilder;

import java.util.function.Supplier;

public class YieldStatement<E> implements Statement<E> {
    final Supplier<E> result;

    public YieldStatement(Supplier<E> result) {
        this.result = result;
    }
}
