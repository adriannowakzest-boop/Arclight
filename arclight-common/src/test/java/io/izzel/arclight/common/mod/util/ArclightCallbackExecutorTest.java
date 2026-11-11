package io.izzel.arclight.common.mod.util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ArclightCallbackExecutorTest {

    @Test
    public void drainsCallbacksInSubmissionOrder() {
        ArclightCallbackExecutor executor = new ArclightCallbackExecutor();
        List<Integer> calls = new ArrayList<>();

        executor.execute(() -> calls.add(1));
        executor.execute(() -> calls.add(2));
        executor.run();

        assertEquals(List.of(1, 2), calls);
    }

    @Test
    public void continuesAfterCallbackThrows() {
        ArclightCallbackExecutor executor = new ArclightCallbackExecutor();
        List<Integer> calls = new ArrayList<>();

        executor.execute(() -> {
            calls.add(1);
            throw new IllegalStateException("simulated plugin callback failure");
        });
        executor.execute(() -> calls.add(2));
        executor.run();

        assertEquals(List.of(1, 2), calls);
    }

    @Test
    public void drainsCallbacksQueuedByAnotherCallback() {
        ArclightCallbackExecutor executor = new ArclightCallbackExecutor();
        List<Integer> calls = new ArrayList<>();

        executor.execute(() -> {
            calls.add(1);
            executor.execute(() -> calls.add(2));
        });
        executor.run();

        assertEquals(List.of(1, 2), calls);
    }
}
