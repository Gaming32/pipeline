package io.github.gaming32.pipeline;

public class UnaryPipelineTest {
    public static void main(String[] args) {
        System.out.println(
            Pipelines.unary("helloo")
                .choosePipeline(
                    s -> s.length() == 5,
                    p ->
                        p.then(s -> s.substring(0, 3))
                         .then(String::toUpperCase),
                    p ->
                        p.then(s -> s.substring(4))
                )
                .get()
        );
    }
}
