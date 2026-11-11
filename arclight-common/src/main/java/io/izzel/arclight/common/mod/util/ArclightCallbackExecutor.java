package io.izzel.arclight.common.mod.util;

import io.izzel.arclight.common.mod.ArclightMod;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;

public class ArclightCallbackExecutor implements Executor, Runnable {

    private final Queue<Runnable> queue = new ConcurrentLinkedQueue<>();

    @Override
    public void execute(Runnable runnable) {
        if (runnable == null) {
            throw new NullPointerException("runnable");
        }
        queue.add(runnable);
    }

    @Override
    public void run() {
        for (;;) {
            var poll = queue.poll();
            if (poll == null) {
                return;
            }
            try {
                poll.run();
            } catch (RuntimeException exception) {
                // A broken callback must not strand later chunk load/unload callbacks.
                ArclightMod.LOGGER.error("Failed to execute queued server callback", exception);
            }
        }
    }
}
