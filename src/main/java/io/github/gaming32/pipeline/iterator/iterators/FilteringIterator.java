package io.github.gaming32.pipeline.iterator.iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

public final class FilteringIterator<E> implements Iterator<E> {
    private final Iterator<E> wrapped;
    private final Predicate<E> filter;
    private boolean hasScanned;
    private E scannedValue;

    public FilteringIterator(Iterator<E> wrapped, Predicate<E> filter) {
        this.wrapped = wrapped;
        this.filter = filter;
    }

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
        while (wrapped.hasNext()) {
            E value = wrapped.next();
            if (filter.test(value)) {
                hasScanned = true;
                scannedValue = value;
                return;
            }
        }
    }

    @Override
    public String toString() {
        return "FilteringIterator{it=" + wrapped + ", filter=" + filter + "}";
    }
}
