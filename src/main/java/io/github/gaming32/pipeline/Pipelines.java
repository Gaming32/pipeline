package io.github.gaming32.pipeline;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import io.github.gaming32.pipeline.iterator.IteratorPipeline;
import io.github.gaming32.pipeline.unary.UnaryPipeline;

public final class Pipelines {
    // Disable instantiation
    private Pipelines() {
    }

    public static <T> UnaryPipeline<T> unary(T value) {
        return UnaryPipeline.of(value);
    }

    public static <E> IteratorPipeline<E> iterator(Iterator<E> iterator) {
        return IteratorPipeline.of(iterator);
    }

    public static <E> IteratorPipeline<E> iterator(Iterable<E> iterable) {
        return IteratorPipeline.of(iterable.iterator());
    }

    public static <E> IteratorPipeline<E> iterator(E[] array) {
        return IteratorPipeline.of(new Iterator<E>() {
            private int i;

            @Override
            public boolean hasNext() {
                return i < array.length;
            }

            @Override
            public E next() {
                try {
                    return array[i++];
                } catch (IndexOutOfBoundsException e) {
                    throw new NoSuchElementException();
                }
            }

            @Override
            public String toString() {
                return "ArrayIterator" + Arrays.toString(array);
            }
        });
    }

    public static <E> IteratorPipeline<E> iterator(Stream<E> stream) {
        return IteratorPipeline.of(stream.iterator());
    }
}
