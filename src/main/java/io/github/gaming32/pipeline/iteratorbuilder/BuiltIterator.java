package io.github.gaming32.pipeline.iteratorbuilder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

class BuiltIterator<E> implements Iterator<E> {
    private final List<Object> valueStack;
    private final List<Integer> branchStack;
    private int branchPos;
    private StatementList<E> tree;

    private boolean hasNextValue;
    private E nextValue;

    public BuiltIterator(StatementList<E> root) {
        valueStack = new ArrayList<>();
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
                if (stmt instanceof RunStatement) {
                    ((RunStatement<E>)stmt).func.run();
                } else if (stmt instanceof YieldStatement) {
                    hasNextValue = true;
                    nextValue = ((YieldStatement<E>)stmt).result.get();
                    return;
                } else if (stmt instanceof ForStatement) {
                    ForStatement<E> forStmt = (ForStatement<E>)stmt;
                    branchStack.add(branchPos);
                    branchPos = 0;
                    tree = forStmt.children;
                    forStmt.initializer.run();
                } else if (stmt instanceof ForEachStatement) {
                    @SuppressWarnings("unchecked")
                    ForEachStatement<E,?> forEachStmt = (ForEachStatement<E,?>)stmt;
                    Iterator<?> iterator = forEachStmt.iterator.iterator();
                    if (iterator.hasNext()) {
                        branchStack.add(branchPos);
                        branchPos = 0;
                        tree = forEachStmt.children;
                        valueStack.add(iterator);
                        forEachStmt.accept(iterator);
                    }
                } else if (stmt instanceof WhileStatement) {
                    WhileStatement<E> whileStmt = (WhileStatement<E>)stmt;
                    if (whileStmt.condition.getAsBoolean()) {
                        branchStack.add(branchPos);
                        branchPos = 0;
                        tree = whileStmt.children;
                    }
                } else if (stmt instanceof IfStatement) {
                    IfStatement<E> ifStmt = (IfStatement<E>)stmt;
                    if (ifStmt.condition.getAsBoolean()) {
                        branchStack.add(branchPos);
                        branchPos = 0;
                        tree = ifStmt.ifTrue;
                    } else {
                        boolean foundElseIf = false;
                        int branchCount = ifStmt.otherOptions.size();
                        for (int i = 0; i < branchCount; i++) {
                            IfStatement.ConditionStatementListPair<E> branch = ifStmt.otherOptions.get(i);
                            if (branch.condition.getAsBoolean()) {
                                branchStack.add(branchPos);
                                branchPos = 0;
                                tree = branch.children;
                                // Goto would probable be more readable here, but Java doesn't have it
                                foundElseIf = true;
                                // goto foundElseIf;
                                break;
                            }
                        }
                        if (!foundElseIf && ifStmt.ifFalse != null) {
                            // There's an else statement
                            branchStack.add(branchPos);
                            branchPos = 0;
                            tree = ifStmt.ifFalse;
                        }
                        // foundElseIf:
                    }
                }
            }
            Statement<E> parentStatement;
            if ((parentStatement = tree.parentStatement) != null) {
                assert tree.parent != null;
                if (parentStatement instanceof ForStatement) {
                    ForStatement<E> forStmt = (ForStatement<E>)parentStatement;
                    forStmt.increment.run();
                    if (forStmt.condition.getAsBoolean()) {
                        branchPos = 0;
                    } else {
                        branchPos = branchStackPop();
                        tree = tree.parent;
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
                        branchPos = branchStackPop();
                        tree = tree.parent;
                    }
                } else if (parentStatement instanceof WhileStatement) {
                    WhileStatement<E> whileStmt = (WhileStatement<E>)parentStatement;
                    if (whileStmt.condition.getAsBoolean()) {
                        branchPos = 0;
                    } else {
                        branchPos = branchStackPop();
                        tree = tree.parent;
                    }
                } else {
                    branchPos = branchStackPop();
                    tree = tree.parent;
                }
            } else {
                break;
            }
        }
        hasNextValue = false;
    }
}
