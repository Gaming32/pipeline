package io.github.gaming32.pipeline.iterator.iterators;

import java.util.Iterator;

public final class SkipIterator<E> implements Iterator<E> {
    private final Iterator<E> wrapped;
    private final int toSkip;
    private boolean hasSkipped;

    public SkipIterator(Iterator<E> wrapped, int n) {
        this.wrapped = wrapped;
        this.toSkip = n;
    }

    @Override
    public boolean hasNext() {
        if (!hasSkipped) performSkip();
        return wrapped.hasNext();
    }

    @Override
    public E next() {
        if (!hasSkipped) performSkip();
        return wrapped.next();
    }

    private void performSkip() {
        for (int i = 0; i < toSkip && wrapped.hasNext(); i++) {
            wrapped.next();
        }
        hasSkipped = true;
    }

    @Override
    public String toString() {
        return "SkipIterator{it=" + wrapped + ", n=" + toSkip + "}";
    }
}
