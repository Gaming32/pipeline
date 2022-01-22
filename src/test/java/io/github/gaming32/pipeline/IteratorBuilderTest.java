package io.github.gaming32.pipeline;

import java.util.Iterator;
import java.util.stream.Collectors;

import io.github.gaming32.pipeline.iterator.IteratorPipeline;
import io.github.gaming32.pipeline.iteratorbuilder.IteratorBuilder;

public class IteratorBuilderTest implements Iterable<String> {
    @Override
    public Iterator<String> iterator() {
        class State {
            int i = 0;
        }
        State state = new State();
        return IteratorBuilder.<String>create()
            .while_(() -> state.i < 5)
                .if_(() -> state.i != 2)
                    .yield(() -> Integer.toString(state.i++))
                .else_()
                    .run(() -> state.i++)
                .end()
            .end()
        .end().iterator();
        // return IteratorBuilder.<String>create()
        //     .yield(() -> "hello")
        //     .yield(() -> "world")
        // .end().iterator();
    }

    @SafeVarargs
    public static <T> Iterator<T> concat(Iterator<T>... iterators) {
        class State {
            Iterator<T> current;
        }
        State state = new State();
        return IteratorBuilder.<T>create()
            .forEach(it -> state.current = it, () -> iterators)
                .run(() -> System.out.println(state.current))
                .yieldFrom(() -> state.current)
            .end()
        .end().iterator();
    }

    public static void main(String[] args) {
        System.out.println(
            IteratorPipeline.of(new IteratorBuilderTest())
                .collect(Collectors.joining(", ", "[", "]"))
        );
        // System.out.println(
        //     IteratorPipeline.of(concat(
        //         new IteratorBuilderTest().iterator(),
        //         new IteratorBuilderTest().iterator()
        //     ))
        //     .collect(Collectors.joining(", ", "[", "]"))
        // );
    }
}
