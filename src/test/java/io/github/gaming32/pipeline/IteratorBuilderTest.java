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

    public static Iterator<String> test3(int whichOne) {
        return IteratorBuilder.<String>create()
            .if_(() -> whichOne == 0)
                .yield(() -> "hello")
            .elseIf(() -> whichOne == 1)
                .yield(() -> "world")
            .elseIf(() -> whichOne == 2)
                .yield(() -> "123")
            .elseIf(() -> whichOne == 3)
                .yield(() -> "45")
            .else_()
                .yield(() -> "sorry")
            .end()
        .end().iterator();
    }

    public static void main(String[] args) {
        // System.out.println(
        //     IteratorPipeline.of(new IteratorBuilderTest())
        //         .collect(Collectors.joining(", ", "[", "]"))
        // );
        for (int i = 0; i < 5; i++) {
            System.out.println(
                IteratorPipeline.of(test3(i))
                    .collect(Collectors.joining())
            );
            // System.out.println(test3(i).next());
        }
        // System.out.println(
        //     IteratorPipeline.of(concat(
        //         new IteratorBuilderTest().iterator(),
        //         new IteratorBuilderTest().iterator()
        //     ))
        //     .collect(Collectors.joining(", ", "[", "]"))
        // );
    }
}
