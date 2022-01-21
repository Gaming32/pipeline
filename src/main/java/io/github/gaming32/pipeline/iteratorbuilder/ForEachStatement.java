package io.github.gaming32.pipeline.iteratorbuilder;

import java.util.Iterator;
import java.util.function.Consumer;

public class ForEachStatement<E, T> implements Statement<E> {
    final Consumer<T> action;
    final Iterable<T> iterator;
    final StatementList<E> children;

    public ForEachStatement(Consumer<T> action, Iterable<T> iterator, StatementList<E> children) {
        this.action = action;
        this.iterator = iterator;
        this.children = children;
    }

    @SuppressWarnings("unchecked")
    void accept(Iterator<?> iterator) {
        action.accept((T)iterator.next());
    }
}
