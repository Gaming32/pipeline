package io.github.gaming32.pipeline;

import java.util.Arrays;

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
            Arrays.asList(
                "hello",
                "world",
                "test"
            ),
            Arrays.asList(
                "123",
                "456",
                "789"
            )
        )
            .skip(2)
            .limit(3);
        System.out.println(pipe.estimateSize());
        System.out.println(pipe.count());
    }
}
