package io.github.gaming32.pipeline.iterator.iterators;

import java.util.Iterator;
import java.util.function.Consumer;

public final class PeekingIterator<E> implements SizeEstimateIterator<E> {
    private final Iterator<E> wrapped;
    private final Consumer<E> action;

    public PeekingIterator(Iterator<E> wrapped, Consumer<E> action) {
        this.wrapped = wrapped;
        this.action = action;
    }

    @Override
    public boolean hasNext() {
        return wrapped.hasNext();
    }

    @Override
    public E next() {
        E value = wrapped.next();
        action.accept(value);
        return value;
    }

    @Override
    public int estimateSize() {
        return SizeEstimateIterator.estimateSize(wrapped);
    }

    @Override
    public String toString() {
        return "PeekingIterator{it=" + wrapped + ", action=" + action + "}";
    }
}
