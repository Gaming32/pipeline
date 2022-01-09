package io.github.gaming32.pipeline.iterator.iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class LimitIterator<E> implements Iterator<E> {
    private final Iterator<E> wrapped;
    private final int maxElements;
    private int count;

    public LimitIterator(Iterator<E> wrapped, int n) {
        this.wrapped = wrapped;
        this.maxElements = n;
    }

    @Override
    public boolean hasNext() {
        if (count < maxElements) {
            return wrapped.hasNext();
        }
        return false;
    }

    @Override
    public E next() {
        if (count < maxElements) {
            E value = wrapped.next();
            // Perform increment *after* calling next to safeguard
            // against next() throwing any exceptions (e.g. NoSuchElementException)
            count++;
            return value;
        }
        throw new NoSuchElementException();
    }

    @Override
    public String toString() {
        return "LimitIterator{it=" + wrapped + ", n=" + maxElements + "}";
    }
}
