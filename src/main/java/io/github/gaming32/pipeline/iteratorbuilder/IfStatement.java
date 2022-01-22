package io.github.gaming32.pipeline.iteratorbuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

class IfStatement<E> implements Statement<E> {
    public static final class ConditionStatementListPair<E> {
        final BooleanSupplier condition;
        final StatementList<E> children;

        public ConditionStatementListPair(BooleanSupplier condition, StatementList<E> children) {
            this.condition = condition;
            this.children = children;
        }
    }

    final BooleanSupplier condition;
    final StatementList<E> ifTrue;
    final List<ConditionStatementListPair<E>> otherOptions;
    StatementList<E> ifFalse;

    public IfStatement(BooleanSupplier condition, StatementList<E> ifTrue) {
        this.condition = condition;
        this.ifTrue = ifTrue;
        this.otherOptions = new ArrayList<>();
        this.ifFalse = null;
    }
}
