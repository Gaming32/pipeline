package io.github.gaming32.pipeline;

import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

import io.github.gaming32.pipeline.calling.CallingPipeline;
import io.github.gaming32.pipeline.iterator.IteratorPipeline;
import io.github.gaming32.pipeline.iterator.iterators.ArrayIterator;
import io.github.gaming32.pipeline.unary.UnaryPipeline;

public final class Pipelines {
    // Disable instantiation
    private Pipelines() {
    }

    public static <T> UnaryPipeline<T> unary(T value) {
        return UnaryPipeline.of(value);
    }

    public static <V> CallingPipeline<V> call(Callable<V> func) {
        return CallingPipeline.of(func);
    }

    public static <E> IteratorPipeline<E> iterator(Iterator<E> iterator) {
        return IteratorPipeline.of(iterator);
    }

    public static <E> IteratorPipeline<E> iterator(Iterable<E> iterable) {
        return IteratorPipeline.of(iterable.iterator());
    }

    public static <E> IteratorPipeline<E> iterator(E[] array) {
        return IteratorPipeline.of(new ArrayIterator<>(array));
    }

    public static <E> IteratorPipeline<E> iterator(Stream<E> stream) {
        return IteratorPipeline.of(stream.iterator());
    }

    @SafeVarargs
    public static <E> IteratorPipeline<E> iterators(Iterator<E>... iterators) {
        return IteratorPipeline.multi(iterators);
    }

    @SafeVarargs
    public static <E> IteratorPipeline<E> iterators(Iterable<E>... iterables) {
        @SuppressWarnings("unchecked")
        Iterator<E>[] iterators = new Iterator[iterables.length];
        for (int i = 0; i < iterables.length; i++) {
            iterators[i] = iterables[i].iterator();
        }
        return IteratorPipeline.multi(iterators);
    }

    @SafeVarargs
    public static <E> IteratorPipeline<E> iterators(E[]... arrays) {
        @SuppressWarnings("unchecked")
        Iterator<E>[] iterators = new Iterator[arrays.length];
        for (int i = 0; i < arrays.length; i++) {
            iterators[i] = new ArrayIterator<>(arrays[i]);
        }
        return IteratorPipeline.multi(iterators);
    }

    @SafeVarargs
    public static <E> IteratorPipeline<E> iterators(Stream<E>... streams) {
        @SuppressWarnings("unchecked")
        Iterator<E>[] iterators = new Iterator[streams.length];
        for (int i = 0; i < streams.length; i++) {
            iterators[i] = streams[i].iterator();
        }
        return IteratorPipeline.multi(iterators);
    }
}
