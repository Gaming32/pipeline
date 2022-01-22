package io.github.gaming32.pipeline.iteratorbuilder;

class RunStatement<E> implements Statement<E> {
    final Runnable func;

    public RunStatement(Runnable func) {
        this.func = func;
    }
}
