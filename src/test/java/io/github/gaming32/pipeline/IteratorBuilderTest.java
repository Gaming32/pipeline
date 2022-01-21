package io.github.gaming32.pipeline;

import java.util.Iterator;
import java.util.stream.Collectors;

import io.github.gaming32.pipeline.iterator.IteratorPipeline;
import io.github.gaming32.pipeline.iteratorbuilder.IteratorBuilder;

public class IteratorBuilderTest implements Iterable<String> {
    @Override
    public Iterator<String> iterator() {
        class State {
            int i;
        }
        State state = new State();
        return IteratorBuilder.<String>create()
            .while_(() -> state.i < 5)
                .yield(() -> Integer.toString(state.i++))
            .end()
        .end().iterator();
        // return IteratorBuilder.<String>create()
        //     .yield(() -> "hello")
        //     .yield(() -> "world")
        // .end().iterator();
    }

    public static void main(String[] args) {
        System.out.println(
            IteratorPipeline.of(new IteratorBuilderTest())
                .collect(Collectors.joining(", ", "[", "]"))
        );
    }
}
