package io.github.gaming32.pipeline;

import java.util.Arrays;

public class CallingPipelineTest {
    public static void main(String[] args) throws Exception {
        System.out.println(
            Pipelines.call(CallingPipelineTest::getString)
                .withDefaultExecutor()
                .then(String::trim)
                .then(String::toUpperCase)
                .then(String::toCharArray)
                .then(Arrays::toString)
                .get()
        );
    }

    private static String getString() {
        return "   Hello world ";
    }
}
