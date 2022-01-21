package io.github.gaming32.pipeline.iteratorbuilder;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public interface IteratorBuilder<E> extends Iterable<E> {
    public static <E> IteratorBuilder<E> create() {
        return new StatementList<>();
    }

    public IteratorBuilder<E> for_(Runnable initializer, BooleanSupplier condition, Runnable increment);
    public IteratorBuilder<E> yield(Supplier<E> result);
    public IteratorBuilder<E> end();
}
