package io.github.gaming32.pipeline.unary;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import io.github.gaming32.pipeline.iterator.IteratorPipeline;

public interface UnaryPipeline<T> {
    public static <T> UnaryPipeline<T> of(T value) {
        return new StandardUnaryPipeline<>(value);
    }

    public <R> UnaryPipeline<R> then(Function<T, R> function);

    public T get();

    public <R> UnaryPipeline<R> choose(Predicate<T> chooser, R ifTrue, R ifFalse);

    public <R> UnaryPipeline<R> choose(Predicate<T> chooser, Supplier<R> ifTrue, Supplier<R> ifFalse);

    public <R> UnaryPipeline<R> choose(Predicate<T> chooser, Function<T, R> ifTrue, Function<T, R> ifFalse);

    public <R extends UnaryPipeline<RT>, RT> R choosePipeline(Predicate<T> chooser, Function<UnaryPipeline<T>, R> ifTrue, Function<UnaryPipeline<T>, R> ifFalse);

    public <R> UnaryPipeline<R> replace(R value);

    public IteratorPipeline<T> oneElementIterator();
}
