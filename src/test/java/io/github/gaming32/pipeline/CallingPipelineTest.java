package io.github.gaming32.pipeline;

import java.util.Arrays;

import io.github.gaming32.pipeline.calling.CallingPipeline;

public class CallingPipelineTest {
    public static void main(String[] args) throws Exception {
        CallingPipeline<String> pipeline =
            Pipelines.call(CallingPipelineTest::getString)
                .withDefaultExecutor()
                .then(String::trim)
                .then(String::toUpperCase)
                .thenPassive(System.out::println)
                .then(String::toCharArray)
                .then(Arrays::toString)
                .ifExceptionalPassive(
                    (e, value) ->
                        System.out.println("An exception occurred with " + value + ": " + e)
                )
                .call(); // Make sure to run call() to kick off any threads
        System.out.println(pipeline.getState());
        Thread.sleep(1000);
        System.out.println(pipeline.getState());
        System.out.println(pipeline.get());
    }

    private static String getString() {
        return "   Hello world ";
    }
}
