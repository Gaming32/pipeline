package io.github.gaming32.pipeline.calling;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

class StandardCallingPipeline<V> implements CallingPipeline<V> {
    private static final ExecutorService DEFAULT_EXECUTOR = ForkJoinPool.commonPool();

    private final OneArgCallable<?, ?>[] parents;
    private BiPredicate<Exception, Object>[] exceptionalHandlers;
    private final OneArgCallable<?, V> func;
    private CallingState state = CallingState.NOT_YET;
    private ExecutorService executor = null;

    private Future<V> waitingFuture;
    private Exception exception;
    private V result;

    @SuppressWarnings("unchecked")
    public StandardCallingPipeline(Callable<V> func) {
        this.parents = new OneArgCallable[0];
        this.exceptionalHandlers = new BiPredicate[0];
        this.func = new OneArgCallable<Object,V>() {
            @Override
            public V call(Object ignored) throws Exception {
                return func.call();
            }
        };
    }

    private <L> StandardCallingPipeline(StandardCallingPipeline<L> previous, OneArgCallable<L, V> func) {
        this.executor = previous.executor;
        this.parents = Arrays.copyOf(previous.parents, previous.parents.length + 1);
        this.parents[previous.parents.length] = previous.func;
        this.exceptionalHandlers = previous.exceptionalHandlers;
        this.func = func;
    }

    private void checkNotCalled() {
        if (state != CallingState.NOT_YET) {
            throw new IllegalStateException("Cannot call pipeline");
        }
    }

    @SuppressWarnings("unchecked")
    private V execute() throws Exception {
        Object value = null;
        try {
            for (OneArgCallable<?, ?> parent : parents) {
                value = ((OneArgCallable<Object, Object>)parent).call(value);
            }
            return ((OneArgCallable<Object, V>)func).call(value);
        } catch (Exception e) {
            for (BiPredicate<Exception, Object> handler : exceptionalHandlers) {
                if (handler.test(e, value)) {
                    break;
                }
            }
            throw e;
        }
    }

    @Override
    public CallingPipeline<V> withDefaultExecutor() {
        checkNotCalled();
        executor = DEFAULT_EXECUTOR;
        return this;
    }

    @Override
    public CallingPipeline<V> withExecutor(ExecutorService executor) {
        checkNotCalled();
        this.executor = executor;
        return this;
    }

    @Override
    public CallingPipeline<V> call() {
        checkNotCalled();
        if (executor == null) {
            try {
                result = execute();
                state = CallingState.DONE;
            } catch (Exception e) {
                exception = e;
                state = CallingState.EXCEPTIONAL;
            }
        } else {
            waitingFuture = executor.submit(this::execute);
            state = CallingState.WAITING;
        }
        return this;
    }

    @Override
    public CallingPipeline<V> join() {
        if (state == CallingState.NOT_YET) {
            call();
        }
        if (state == CallingState.WAITING) {
            try {
                result = waitingFuture.get();
                state = CallingState.DONE;
            } catch (CancellationException e) {
                state = CallingState.CANCELLED;
            } catch (ExecutionException e) {
                exception = (Exception)e.getCause();
                state = CallingState.EXCEPTIONAL;
            } catch (Exception e) {
                exception = e;
                state = CallingState.EXCEPTIONAL;
            }
        }
        return this;
    }

    @Override
    public CallingState getState() {
        if (state == CallingState.WAITING && waitingFuture.isDone()) {
            // Set proper done state: DONE, CANCELLED, or EXCEPTIONAL
            join();
        }
        return state;
    }

    @Override
    public V get() throws Exception {
        join();
        if (state == CallingState.CANCELLED) {
            throw new CancellationException();
        }
        if (state == CallingState.EXCEPTIONAL) {
            throw exception;
        }
        return result;
    }

    @Override
    public V getUnchecked() {
        try {
            return get();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <R> CallingPipeline<R> then(OneArgCallable<V, R> handler) {
        return new StandardCallingPipeline<>(this, handler);
    }

    @Override
    public CallingPipeline<V> thenPassive(OneArgNoReturnCallable<V> handler) {
        return new StandardCallingPipeline<>(this, new OneArgCallable<V, V>() {
            @Override
            public V call(V arg) throws Exception {
                handler.call(arg);
                return arg;
            }
        });
    }

    private void addExceptionHandler(BiPredicate<Exception, Object> handler) {
        exceptionalHandlers = Arrays.copyOf(exceptionalHandlers, exceptionalHandlers.length + 1);
        exceptionalHandlers[exceptionalHandlers.length - 1] = handler;
    }

    @Override
    public CallingPipeline<V> ifExceptional(BiPredicate<Exception, Object> handler) {
        addExceptionHandler(handler);
        return this;
    }

    @Override
    public CallingPipeline<V> ifExceptionalPassive(BiConsumer<Exception, Object> handler) {
        addExceptionHandler(new BiPredicate<Exception, Object>() {
            @Override
            public boolean test(Exception e, Object value) {
                handler.accept(e, value);
                return false;
            }
        });
        return this;
    }
}
