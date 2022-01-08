package io.github.gaming32.pipeline;

import io.github.gaming32.pipeline.unary.UnaryPipeline;

public final class Pipelines {
    // Disable instantiation
    private Pipelines() {
    }

    public static <T> UnaryPipeline<T> unary(T value) {
        return UnaryPipeline.of(value);
    }
}
