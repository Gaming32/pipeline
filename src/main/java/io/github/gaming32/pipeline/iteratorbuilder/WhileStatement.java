package io.github.gaming32.pipeline.iteratorbuilder;

import java.util.function.BooleanSupplier;

public class WhileStatement<E> implements Statement<E> {
    final BooleanSupplier condition;
    final StatementList<E> children;

    public WhileStatement(BooleanSupplier condition, StatementList<E> children) {
        this.condition = condition;
        this.children = children;
    }
}
