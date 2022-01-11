package io.github.gaming32.pipeline;

import java.util.Arrays;

public class CallingPipelineTest {
    public static void main(String[] args) throws Exception {
        System.out.println(
            Pipelines.call(CallingPipelineTest::getString)
                // .withDefaultExecutor()
                .then(String::trim)
                .then(String::toUpperCase)
                .thenPassive(System.out::println)
                .thenPassive(v -> {
                    throw new Exception("haha");
                })
                .then(String::toCharArray)
                .then(Arrays::toString)
                .ifExceptionalPassive(
                    (e, value) ->
                        System.out.println("An exception occurred with " + value + ": " + e)
                )
                .get()
        );
    }

    private static String getString() {
        return "   Hello world ";
    }
}
