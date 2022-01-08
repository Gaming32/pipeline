package io.github.gaming32.pipeline;

import java.util.stream.Collectors;

public class IteratorPipelineTest {
    public static void main(String[] args) {
        System.out.println(
            Pipelines.iterator(new String[] {
                "hello  ",
                "  world ",
                "123"
            })
                .pipelineMap(
                    p ->
                        p.then(String::trim)
                         .then(String::toUpperCase)
                )
                .collect(Collectors.joining(", ", "[", "]"))
        );
    }
}
