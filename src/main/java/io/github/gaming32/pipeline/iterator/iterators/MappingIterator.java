package io.github.gaming32.pipeline.iterator.iterators;

import java.util.Iterator;
import java.util.function.Function;

public final class MappingIterator<E, R> implements SizeEstimateIterator<R> {
    private final Iterator<E> wrapped;
    private final Function<E, R> mapper;

    public MappingIterator(Iterator<E> wrapped, Function<E, R> mapper) {
        this.wrapped = wrapped;
        this.mapper = mapper;
    }

    @Override
    public boolean hasNext() {
        return wrapped.hasNext();
    }

    @Override
    public R next() {
        return mapper.apply(wrapped.next());
    }

    @Override
    public int estimateSize() {
        return SizeEstimateIterator.estimateSize(wrapped);
    }

    @Override
    public String toString() {
        return "FilteringIterator{it=" + wrapped + ", mapper=" + mapper + "}";
    }
}
