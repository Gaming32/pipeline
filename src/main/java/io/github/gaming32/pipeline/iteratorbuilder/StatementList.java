package io.github.gaming32.pipeline.iteratorbuilder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

class StatementList<E> implements IteratorBuilder<E> {
    final StatementList<E> parent;
    final List<Statement<E>> children;
    private boolean built;

    public StatementList() {
        parent = null;
        children = new ArrayList<>();
    }

    public StatementList(StatementList<E> parent) {
        this.parent = parent;
        this.children = new ArrayList<>();
    }

    @Override
    public IteratorBuilder<E> for_(Runnable initializer, BooleanSupplier condition, Runnable increment) {
        StatementList<E> child = new StatementList<>(this);
        children.add(new ForStatement<>(initializer, condition, increment, child));
        return child;
    }

    @Override
    public IteratorBuilder<E> yield(Supplier<E> result) {
        children.add(new YieldStatement<>(result));
        return this;
    }

    @Override
    public StatementList<E> end() {
        if (built) {
            throw new IllegalStateException("StatementList already ended");
        }
        built = true;
        return parent == null ? this : parent;
    }

    @Override
    public Iterator<E> iterator() {
        if (!built) {
            throw new IllegalStateException("StatementList not ended");
        }
        return new BuiltIterator<>(this);
    }
}
