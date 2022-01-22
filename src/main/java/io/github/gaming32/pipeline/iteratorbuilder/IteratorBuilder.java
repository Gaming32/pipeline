package io.github.gaming32.pipeline.iteratorbuilder;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

import io.github.gaming32.pipeline.iterator.iterators.ArrayIterator;

public interface IteratorBuilder<E> extends Iterable<E> {
    @FunctionalInterface
    public interface ArraySupplier<T> {
        public T[] get();
    }

    public static <E> IteratorBuilder<E> create() {
        return new StatementList<>();
    }

    public IteratorBuilder<E> run(Runnable func);
    public IteratorBuilder<E> for_(Runnable initializer, BooleanSupplier condition, Runnable increment);
    public <T> IteratorBuilder<E> forEach(Consumer<T> action, Iterable<T> iterator);
    public IteratorBuilder<E> while_(BooleanSupplier condition);
    public IteratorBuilder<E> yield(Supplier<E> result);
    public IteratorBuilder<E> end();

    default public <T> IteratorBuilder<E> forEach(Consumer<T> action, Supplier<Iterable<T>> iterable) {
        return forEach(action, () -> iterable.get().iterator());
    }

    default public <T> IteratorBuilder<E> forEach(Consumer<T> action, ArraySupplier<T> array) {
        return forEach(action, () -> new ArrayIterator<>(array.get()));
    }

    @SuppressWarnings("unchecked")
    default public IteratorBuilder<E> yieldFrom(Iterable<E> iterator) {
        Object[] current = new Object[1];
        return forEach(e -> current[0] = e, iterator)
            .yield(() -> (E)current[0])
        .end();
    }

    @SuppressWarnings("unchecked")
    default public IteratorBuilder<E> yieldFrom(Supplier<Iterable<E>> iterable) {
        Object[] current = new Object[1];
        return forEach(e -> current[0] = e, iterable)
            .yield(() -> (E)current[0])
        .end();
    }

    @SuppressWarnings("unchecked")
    default public IteratorBuilder<E> yieldFrom(ArraySupplier<E> array) {
        Object[] current = new Object[1];
        return forEach(e -> current[0] = e, array)
            .yield(() -> (E)current[0])
        .end();
    }
}
