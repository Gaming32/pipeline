package io.github.gaming32.pipeline.iterator.iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;

public final class FlatMappingIterator<E, R> implements SizeEstimateIterator<R> {
    private final Iterator<E> wrapped;
    private final Function<? super E, ? extends Iterator<R>> mapper;
    private Iterator<R> current;

    public FlatMappingIterator(Iterator<E> wrapped, Function<? super E, ? extends Iterator<R>> mapper) {
        this.wrapped = wrapped;
        this.mapper = mapper;
    }

    @Override
    public boolean hasNext() {
        if (current == null) { // First iteration
            if (wrapped.hasNext()) {
                current = mapper.apply(wrapped.next());
            } else {
                return false;
            }
        }
        while (!current.hasNext()) {
            if (wrapped.hasNext()) {
                current = mapper.apply(wrapped.next());
            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    public R next() {
        if (current == null) { // First iteration
            if (wrapped.hasNext()) {
                current = mapper.apply(wrapped.next());
            } else {
                throw new NoSuchElementException();
            }
        }
        while (!current.hasNext()) {
            if (wrapped.hasNext()) {
                current = mapper.apply(wrapped.next());
            } else {
                throw new NoSuchElementException();
            }
        }
        return current.next();
    }

    @Override
    public int estimateSize() {
        return -1;
    }
}
