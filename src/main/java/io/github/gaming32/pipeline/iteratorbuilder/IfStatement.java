package io.github.gaming32.pipeline.iteratorbuilder;

import java.util.function.BooleanSupplier;

class IfStatement<E> implements Statement<E> {
    final BooleanSupplier condition;
    final StatementList<E> ifTrue;
    StatementList<E> ifFalse;

    public IfStatement(BooleanSupplier condition, StatementList<E> ifTrue) {
        this.condition = condition;
        this.ifTrue = ifTrue;
        this.ifFalse = null;
    }
}
