package io.github.gaming32.pipeline.iteratorbuilder;

import java.util.function.BooleanSupplier;

public class ForStatement<E> implements Statement<E> {
    final Runnable initializer;
    final BooleanSupplier condition;
    final Runnable increment;
    final StatementList<E> children;

    public ForStatement(Runnable initializer, BooleanSupplier condition, Runnable increment, StatementList<E> children) {
        this.initializer = initializer;
        this.condition = condition;
        this.increment = increment;
        this.children = children;
    }
}
