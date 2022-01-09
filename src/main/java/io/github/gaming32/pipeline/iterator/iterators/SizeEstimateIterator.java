package io.github.gaming32.pipeline.iterator.iterators;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public interface SizeEstimateIterator<E> extends Iterator<E> {
    @SuppressWarnings("rawtypes")
    static final Class<?> ARRAYLIST_ITERATOR = new ArrayList().iterator().getClass();
    @SuppressWarnings("rawtypes")
    static final Class<?> HASHMAP_ITERATOR = new HashMap().entrySet().iterator().getClass().getSuperclass();

    default public int estimateSize() {
        return -1;
    }

    public static <E> int estimateSize(Iterator<E> it) {
        if (it instanceof SizeEstimateIterator) {
            return ((SizeEstimateIterator<E>)it).estimateSize();
        }
        // Not a SizeEstimateIterator, so we'll attempt to estimate
        // a different way
        // First try some common iterator types
        if (ARRAYLIST_ITERATOR.isInstance(it)) {
            try {
                Field f = ARRAYLIST_ITERATOR.getDeclaredField("this$0");
                f.setAccessible(true);
                return ((ArrayList<?>)f.get(it)).size();
            } catch (ReflectiveOperationException e) {
                return -1;
            }
        }
        if (HASHMAP_ITERATOR.isInstance(it)) {
            try {
                Field f = HASHMAP_ITERATOR.getDeclaredField("this$0");
                f.setAccessible(true);
                return ((HashMap<?, ?>)f.get(it)).size();
            } catch (ReflectiveOperationException e) {
                return -1;
            }
        }
        // Then try to get the size of any collection type
        try {
            Field f = it.getClass().getDeclaredField("this$0");
            f.setAccessible(true);
            Object iterable = f.get(it);
            Class<?> klass = iterable.getClass();
            if (Collection.class.isAssignableFrom(klass)) {
                return ((Collection<?>)iterable).size();
            } else if (Map.class.isAssignableFrom(klass)) {
                return ((Map<?, ?>)iterable).size();
            }
        } catch (ReflectiveOperationException e) {
        }
        // Unable to estimate size
        return -1;
    }
}
