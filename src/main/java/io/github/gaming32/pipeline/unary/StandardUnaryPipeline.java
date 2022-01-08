package io.github.gaming32.pipeline.unary;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

class StandardUnaryPipeline<T> implements UnaryPipeline<T> {
    private final T value;

    public StandardUnaryPipeline(T value) {
        this.value = value;
    }

    @Override
    public <R> UnaryPipeline<R> then(Function<T, R> function) {
        return new StandardUnaryPipeline<>(function.apply(value));
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public <R> UnaryPipeline<R> choose(Predicate<T> chooser, R ifTrue, R ifFalse) {
        if (chooser.test(value)) {
            return new StandardUnaryPipeline<>(ifTrue);
        } else {
            return new StandardUnaryPipeline<>(ifFalse);
        }
    }

    @Override
    public <R> UnaryPipeline<R> choose(Predicate<T> chooser, Supplier<R> ifTrue, Supplier<R> ifFalse) {
        if (chooser.test(value)) {
            return new StandardUnaryPipeline<>(ifTrue.get());
        } else {
            return new StandardUnaryPipeline<>(ifFalse.get());
        }
    }

    @Override
    public <R> UnaryPipeline<R> choose(Predicate<T> chooser, Function<T, R> ifTrue, Function<T, R> ifFalse) {
        if (chooser.test(value)) {
            return new StandardUnaryPipeline<>(ifTrue.apply(value));
        } else {
            return new StandardUnaryPipeline<>(ifFalse.apply(value));
        }
    }

    @Override
    public <R extends UnaryPipeline<RT>, RT> R choosePipeline(Predicate<T> chooser, Function<UnaryPipeline<T>, R> ifTrue, Function<UnaryPipeline<T>, R> ifFalse) {
        if (chooser.test(value)) {
            return ifTrue.apply(this);
        } else {
            return ifFalse.apply(this);
        }
    }

    @Override
    public <R> UnaryPipeline<R> replace(R value) {
        return new StandardUnaryPipeline<>(value);
    }

    @Override
    public String toString() {
        return "UnaryPipeline{value=" + value + "}";
    }
}
