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

    private static final class ArrayIterator<E> implements Iterator<E> {
        private int i;
        private E[] array;

        public ArrayIterator(E[] array) {
            this.array = array;
        }

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
    }
}
