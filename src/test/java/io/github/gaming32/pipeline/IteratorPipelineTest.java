package io.github.gaming32.pipeline;

import java.util.stream.Collectors;

import io.github.gaming32.pipeline.iterator.IteratorPipeline;

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
        IteratorPipeline<String> pipe = Pipelines.iterators(
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
            .peek(System.out::println);
        System.out.println(pipe.collect(Collectors.joining(" ")));
    }
}
