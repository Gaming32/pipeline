package io.github.gaming32.pipeline.iterator;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import io.github.gaming32.pipeline.iterator.iterators.FilteringIterator;
import io.github.gaming32.pipeline.iterator.iterators.FlatMappingIterator;
import io.github.gaming32.pipeline.iterator.iterators.LimitIterator;
import io.github.gaming32.pipeline.iterator.iterators.MappingIterator;
import io.github.gaming32.pipeline.iterator.iterators.PeekingIterator;
import io.github.gaming32.pipeline.iterator.iterators.SizeEstimateIterator;
import io.github.gaming32.pipeline.iterator.iterators.SkipIterator;
import io.github.gaming32.pipeline.unary.UnaryPipeline;

class StandardIteratorPipeline<E> implements IteratorPipeline<E> {
    private final Iterator<E> next;

    public StandardIteratorPipeline(Iterator<E> iterator) {
        this.next = iterator;
    }

    @Override
    public Iterator<E> iterator() {
        return next;
    }

    @Override
    public <R> IteratorPipeline<R> map(Function<E, R> mapper) {
        return new StandardIteratorPipeline<>(new MappingIterator<>(next, mapper));
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
        return new StandardIteratorPipeline<>(new FilteringIterator<>(next, filter));
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
    public boolean allMatch(Predicate<E> predicate) {
        while (next.hasNext()) {
            if (!predicate.test(next.next())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean anyMatch(Predicate<E> predicate) {
        while (next.hasNext()) {
            if (predicate.test(next.next())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean noneMatch(Predicate<E> predicate) {
        while (next.hasNext()) {
            if (predicate.test(next.next())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public IteratorPipeline<E> skip(long n) {
        return new StandardIteratorPipeline<>(new SkipIterator<>(next, n));
    }

    @Override
    public IteratorPipeline<E> limit(long n) {
        return new StandardIteratorPipeline<>(new LimitIterator<>(next, n));
    }

    @Override
    public int estimateSize() {
        return SizeEstimateIterator.estimateSize(next);
    }

    @Override
    public long count() {
        long n;
        for (n = 0; next.hasNext(); next.next()) n++;
        return n;
    }

    @Override
    public IteratorPipeline<E> distinct() {
        Set<E> encounteredBefore = new HashSet<>();
        return filter(encounteredBefore::add);
    }

    @Override
    public <R> IteratorPipeline<R> flatMap(Function<? super E, ? extends Iterator<R>> mapper) {
        return new StandardIteratorPipeline<>(new FlatMappingIterator<>(next, mapper));
    }

    @Override
    public IteratorPipeline<E> peek(Consumer<E> action) {
        return new StandardIteratorPipeline<>(new PeekingIterator<>(next, action));
    }

    @Override
    public String toString() {
        return "IteratorPipeline[" + next + "]";
    }
}
