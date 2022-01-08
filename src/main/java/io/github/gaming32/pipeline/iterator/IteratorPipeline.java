package io.github.gaming32.pipeline.iterator;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;

import io.github.gaming32.pipeline.unary.UnaryPipeline;

public interface IteratorPipeline<E> extends Iterator<E> {
    public static <E> IteratorPipeline<E> of(Iterator<E> iterator) {
        return new StandardIteratorPipeline<>(iterator);
    }

    public static <E> IteratorPipeline<E> of(Iterable<E> iterable) {
        return new StandardIteratorPipeline<>(iterable.iterator());
    }

    public static <E> IteratorPipeline<E> empty() {
        return new StandardIteratorPipeline<>(Collections.emptyIterator());
    }

    @SafeVarargs
    public static <E> IteratorPipeline<E> multi(Iterator<E>... iterators) {
        if (iterators.length == 0) {
            return new StandardIteratorPipeline<>(Collections.emptyIterator());
        }
        return new StandardIteratorPipeline<>(new Iterator<E>() {
            private int nextIndex = 1;
            private Iterator<E> current = iterators[0];

            @Override
            public boolean hasNext() {
                if (current.hasNext()) {
                    return true;
                }
                while (nextIndex < iterators.length) {
                    current = iterators[nextIndex++];
                    if (current.hasNext()) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public E next() {
                if (current.hasNext()) {
                    return current.next();
                }
                while (nextIndex < iterators.length) {
                    current = iterators[nextIndex++];
                    if (current.hasNext()) {
                        return current.next();
                    }
                }
                throw new NoSuchElementException();
            }
        });
    }

    public <R> IteratorPipeline<R> map(Function<E, R> mapper);

    public <R, RP extends UnaryPipeline<R>> IteratorPipeline<R> pipelineMap(Function<UnaryPipeline<E>, RP> mapper);

    public IteratorPipeline<E> filter(Predicate<E> filter);

    public void forEach(Consumer<E> consumer);

    public void pipelineForEach(Consumer<UnaryPipeline<E>> consumer);

    public <A, R> R collect(Supplier<A> supplier, BiConsumer<A, ? super E> accumulator, Function<A, R> finisher);

    default public <A, R> R collect(Collector<? super E, A, R> collector) {
        return collect(collector.supplier(), collector.accumulator(), collector.finisher());
    }

    public boolean allMatch(Predicate<E> predicate);

    public boolean anyMatch(Predicate<E> predicate);
}
