package io.github.gaming32.pipeline.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import io.github.gaming32.pipeline.unary.UnaryPipeline;

class StandardIteratorPipeline<E> implements IteratorPipeline<E> {
    private final Iterator<E> next;

    public StandardIteratorPipeline(Iterator<E> iterator) {
        this.next = iterator;
    }

    @Override
    public boolean hasNext() {
        return next.hasNext();
    }

    @Override
    public E next() {
        return next.next();
    }

    @Override
    public <R> IteratorPipeline<R> map(Function<E, R> mapper) {
        return new StandardIteratorPipeline<>(new Iterator<R>() {
            @Override
            public boolean hasNext() {
                return next.hasNext();
            }
            @Override
            public R next() {
                return mapper.apply(next.next());
            }
        });
    }

    @Override
    public <R, RP extends UnaryPipeline<R>> IteratorPipeline<R> pipelineMap(Function<UnaryPipeline<E>, RP> mapper) {
        return new StandardIteratorPipeline<>(new Iterator<R>() {
            @Override
            public boolean hasNext() {
                return next.hasNext();
            }
            @Override
            public R next() {
                return mapper.apply(UnaryPipeline.of(next.next())).get();
            }
        });
    }

    @Override
    public IteratorPipeline<E> filter(Predicate<E> filter) {
        return new StandardIteratorPipeline<>(new Iterator<E>() {
            private boolean hasScanned;
            private E scannedValue;

            @Override
            public boolean hasNext() {
                if (!hasScanned) {
                    scan();
                }
                return hasScanned;
            }

            @Override
            public E next() {
                if (!hasScanned) {
                    scannedValue = null; // Allow garbage collection
                    scan();
                    if (!hasScanned) {
                        // *Still* no element available
                        throw new NoSuchElementException();
                    }
                }
                hasScanned = false;
                return scannedValue;
            }

            private void scan() {
                while (next.hasNext()) {
                    E value = next.next();
                    if (filter.test(value)) {
                        hasScanned = true;
                        scannedValue = value;
                        return;
                    }
                }
            }
        });
    }

    @Override
    public void forEach(Consumer<E> consumer) {
        while (next.hasNext()) {
            consumer.accept(next.next());
        }
    }

    @Override
    public void pipelineForEach(Consumer<UnaryPipeline<E>> consumer) {
        while (next.hasNext()) {
            consumer.accept(UnaryPipeline.of(next.next()));
        }
    }

    @Override
    public <A, R> R collect(Supplier<A> supplier, BiConsumer<A, ? super E> accumulator, Function<A, R> finisher) {
        A result = supplier.get();
        while (next.hasNext()) {
            accumulator.accept(result, next.next());
        }
        return finisher.apply(result);
    }

    @Override
    public String toString() {
        return "IteratorPipeline[" + next + "]";
    }
}
