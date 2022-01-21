package io.github.gaming32.pipeline.iteratorbuilder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

class BuiltIterator<E> implements Iterator<E> {
    private final List<Object> valueStack;
    private final List<Statement<E>> statementStack;
    private final List<Integer> branchStack;
    private int branchPos;
    private StatementList<E> tree;

    private boolean hasNextValue;
    private E nextValue;

    public BuiltIterator(StatementList<E> root) {
        valueStack = new ArrayList<>();
        statementStack = new ArrayList<>();
        statementStack.add(null); // Marker
        branchStack = new ArrayList<>();
        branchPos = 0;
        tree = root;
    }

    @Override
    public boolean hasNext() {
        if (!hasNextValue) {
            getNext();
        }
        return hasNextValue;
    }

    @Override
    public E next() {
        if (!hasNextValue) {
            getNext();
            if (!hasNextValue) {
                // Still no value
                throw new NoSuchElementException();
            }
        }
        hasNextValue = false;
        return nextValue;
    }

    @SuppressWarnings("unchecked")
    private <T> T valueStackPeek() {
        return (T)valueStack.get(valueStack.size() - 1);
    }

    @SuppressWarnings("unchecked")
    private <T> T valueStackPop() {
        return (T)valueStack.remove(valueStack.size() - 1);
    }

    private Statement<E> statementStackPeek() {
        return statementStack.get(statementStack.size() - 1);
    }

    private Statement<E> statementStackPop() {
        return statementStack.remove(statementStack.size() - 1);
    }

    private int branchStackPeek() {
        return branchStack.get(branchStack.size() - 1);
    }

    private int branchStackPop() {
        return branchStack.remove(branchStack.size() - 1);
    }

    private void getNext() {
        while (true) {
            while (branchPos < tree.children.size()) {
                Statement<E> stmt = tree.children.get(branchPos++);
                if (stmt instanceof YieldStatement) {
                    hasNextValue = true;
                    nextValue = ((YieldStatement<E>)stmt).result.get();
                    return;
                } else if (stmt instanceof ForStatement) {
                    ForStatement<E> forStmt = (ForStatement<E>)stmt;
                    statementStack.add(stmt);
                    branchStack.add(branchPos);
                    branchPos = 0;
                    tree = forStmt.children;
                    forStmt.initializer.run();
                } else if (stmt instanceof ForEachStatement) {
                    @SuppressWarnings("unchecked")
                    ForEachStatement<E,?> forEachStmt = (ForEachStatement<E,?>)stmt;
                    Iterator<?> iterator = forEachStmt.iterator.iterator();
                    if (iterator.hasNext()) {
                        statementStack.add(stmt);
                        branchStack.add(branchPos);
                        branchPos = 0;
                        tree = forEachStmt.children;
                        valueStack.add(iterator);
                        forEachStmt.accept(iterator);
                    }
                } else if (stmt instanceof WhileStatement) {
                    WhileStatement<E> whileStmt = (WhileStatement<E>)stmt;
                    statementStack.add(stmt);
                    branchStack.add(branchPos);
                    branchPos = 0;
                    tree = whileStmt.children;
                }
            }
            Statement<E> parentStatement;
            if ((parentStatement = statementStackPeek()) != null) {
                if (parentStatement instanceof ForStatement) {
                    ForStatement<E> forStmt = (ForStatement<E>)parentStatement;
                    forStmt.increment.run();
                    if (forStmt.condition.getAsBoolean()) {
                        branchPos = 0;
                    } else {
                        statementStackPop();
                        if (tree.parent != null) {
                            branchPos = branchStackPop();
                            tree = tree.parent;
                        } else {
                            break;
                        }
                    }
                } else if (parentStatement instanceof ForEachStatement) {
                    @SuppressWarnings("unchecked")
                    ForEachStatement<E,?> forEachStmt = (ForEachStatement<E,?>)parentStatement;
                    Iterator<?> iterator = valueStackPeek();
                    if (iterator.hasNext()) {
                        forEachStmt.accept(iterator);
                        branchPos = 0;
                    } else {
                        valueStackPop();
                        statementStackPop();
                        if (tree.parent != null) {
                            branchPos = branchStackPop();
                            tree = tree.parent;
                        } else {
                            break;
                        }
                    }
                } else if (parentStatement instanceof WhileStatement) {
                    WhileStatement<E> whileStmt = (WhileStatement<E>)parentStatement;
                    if (whileStmt.condition.getAsBoolean()) {
                        branchPos = 0;
                    } else {
                        statementStackPop();
                        if (tree.parent != null) {
                            branchPos = branchStackPop();
                            tree = tree.parent;
                        } else {
                            break;
                        }
                    }
                }
            } else {
                if (tree.parent != null) {
                    branchPos = branchStackPop();
                    tree = tree.parent;
                } else {
                    break;
                }
            }
        }
        hasNextValue = false;
    }
}
