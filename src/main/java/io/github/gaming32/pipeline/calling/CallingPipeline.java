package io.github.gaming32.pipeline.calling;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

public interface CallingPipeline<V> {
    @FunctionalInterface
    public static interface OneArgCallable<V, R> {
        public R call(V arg) throws Exception;
    }

    @FunctionalInterface
    public static interface OneArgNoReturnCallable<V> {
        public void call(V arg) throws Exception;
    }

    public static enum CallingState {
        NOT_YET,
        WAITING,
        DONE,
        CANCELLED,
        EXCEPTIONAL
    }

    public static <V> CallingPipeline<V> of(Callable<V> func) {
        return new StandardCallingPipeline<>(func);
    }

    public CallingPipeline<V> withDefaultExecutor();

    public CallingPipeline<V> withExecutor(ExecutorService executor);

    public CallingPipeline<V> call();

    public CallingPipeline<V> join();

    public CallingState getState();

    // public CallingPipeline<V> ifExceptional(Consumer<Exception> handler);

    // public CallingPipeline<V> ifExceptional(Function<Exception, V> handler);

    public V get() throws Exception;

    public V getUnchecked();

    public <R> CallingPipeline<R> then(OneArgCallable<V, R> handler);

    public CallingPipeline<V> thenPassive(OneArgNoReturnCallable<V> handler);
}
