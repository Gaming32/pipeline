package io.github.gaming32.pipeline.iteratorbuilder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

class StatementList<E> implements IteratorBuilder<E> {
    final StatementList<E> parent;
    Statement<E> parentStatement;
    final List<Statement<E>> children;
    private boolean built;

    public StatementList() {
        parent = null;
        parentStatement = null;
        children = new ArrayList<>();
    }

    public StatementList(StatementList<E> parent) {
        this.parent = parent;
        this.parentStatement = null;
        this.children = new ArrayList<>();
    }

    private StatementList<E> setParentStatement(Statement<E> parentStatement) {
        this.parentStatement = parentStatement;
        return this;
    }

    @Override
    public IteratorBuilder<E> run(Runnable func) {
        children.add(new RunStatement<>(func));
        return this;
    }

    @Override
    public IteratorBuilder<E> for_(Runnable initializer, BooleanSupplier condition, Runnable increment) {
        StatementList<E> child = new StatementList<>(this);
        Statement<E> childStatement = new ForStatement<>(initializer, condition, increment, child);
        children.add(childStatement);
        return child.setParentStatement(childStatement);
    }

    @Override
    public <T> IteratorBuilder<E> forEach(Consumer<T> action, Iterable<T> iterator) {
        StatementList<E> child = new StatementList<>(this);
        Statement<E> childStatement = new ForEachStatement<>(action, iterator, child);
        children.add(childStatement);
        return child.setParentStatement(childStatement);
    }

    @Override
    public IteratorBuilder<E> while_(BooleanSupplier condition) {
        StatementList<E> child = new StatementList<>(this);
        Statement<E> childStatement = new WhileStatement<>(condition, child);
        children.add(childStatement);
        return child.setParentStatement(childStatement);
    }

    @Override
    public IteratorBuilder<E> if_(BooleanSupplier condition) {
        StatementList<E> child = new StatementList<>(this);
        Statement<E> childStatement = new IfStatement<>(condition, child);
        children.add(childStatement);
        return child.setParentStatement(childStatement);
    }

    @Override
    public IteratorBuilder<E> elseIf(BooleanSupplier condition) {
        if (parentStatement == null || !(parentStatement instanceof IfStatement)) {
            throw new IllegalStateException("else statement must directly follow if statement (no end())");
        }
        assert parent != null;
        StatementList<E> elseIfBranch = new StatementList<>(parent); // Same parent
        elseIfBranch.setParentStatement(parentStatement);
        ((IfStatement<E>)parentStatement).otherOptions.add(new IfStatement.ConditionStatementListPair<>(condition, elseIfBranch));
        return elseIfBranch;
    }

    @Override
    public IteratorBuilder<E> else_() {
        if (parentStatement == null || !(parentStatement instanceof IfStatement)) {
            throw new IllegalStateException("else statement must directly follow if statement (no end())");
        }
        assert parent != null;
        StatementList<E> elseBranch = new StatementList<>(parent); // Same parent
        elseBranch.setParentStatement(parentStatement);
        ((IfStatement<E>)parentStatement).ifFalse = elseBranch;
        return elseBranch;
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
