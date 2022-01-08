package io.github.gaming32.pipeline.iterator.iterators;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public final class ArrayIterator<E> implements Iterator<E> {
    private int i;
    private E[] array;

    public ArrayIterator(E[] array) {
        this.array = array;
    }

    @Override
    public boolean hasNext() {
        return i < array.length;
    }

    @Override
    public E next() {
        try {
            return array[i++];
        } catch (IndexOutOfBoundsException e) {
            throw new NoSuchElementException();
        }
    }

    @Override
    public String toString() {
        return "ArrayIterator" + Arrays.toString(array);
    }
}
