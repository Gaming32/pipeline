package io.github.gaming32.pipeline.iterator.iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.StringJoiner;

public final class MultiIterator<E> implements SizeEstimateIterator<E> {
    private final Iterator<E>[] iterators;
    private int nextIndex;
    private Iterator<E> current;

    @SafeVarargs
    public MultiIterator(Iterator<E>... iterators) {
        this.iterators = iterators;
        this.nextIndex = 1;
        this.current = iterators[0];
    }

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

    @Override
    public int estimateSize() {
        int sum = 0;
        for (Iterator<E> it : iterators) {
            sum += SizeEstimateIterator.estimateSize(it);
        }
        return sum;
    }

    @Override
    public String toString() {
        StringJoiner result = new StringJoiner(", ", "MultiIterator[", "]");
        for (Iterator<E> iterator : iterators) {
            result.add(iterator.toString());
        }
        return result.toString();
    }
}
