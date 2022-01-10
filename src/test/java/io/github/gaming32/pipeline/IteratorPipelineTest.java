package io.github.gaming32.pipeline;

import java.util.stream.Collectors;

import io.github.gaming32.pipeline.iterator.IteratorPipeline;
import io.github.gaming32.pipeline.iterator.iterators.ArrayIterator;

public class IteratorPipelineTest {
    public static void main(String[] args) {
        // System.out.println(
        //     Pipelines.iterator(new String[] {
        //         "hello  ",
        //         "  world ",
        //         "123"
        //     })
        //         .pipelineMap(
        //             p ->
        //                 p.then(String::trim)
        //                  .then(String::toUpperCase)
        //         )
        //         .collect(Collectors.joining(", ", "[", "]"))
        // );
        IteratorPipeline<Character> pipe = Pipelines.iterators(
            new String[] {
                "hello",
                "world",
                "test"
            },
            new String[] {
                "123",
                "456",
                "789"
            }
        )
            .flatMap((s) -> {
                char[] in = s.toCharArray();
                Character[] out = new Character[in.length + 1];
                for (int i = 0; i < in.length; i++) {
                    out[i] = in[i];
                }
                out[in.length] = ' ';
                return new ArrayIterator<>(out);
            });
        System.out.println(
            pipe.map(c -> c.toString())
                .collect(Collectors.joining(""))
        );
    }
}
