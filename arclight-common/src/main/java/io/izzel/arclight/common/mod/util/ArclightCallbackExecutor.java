package io.izzel.arclight.common.mod.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;

public class ArclightCallbackExecutor implements Executor, Runnable {

    private static final Logger LOGGER = LogManager.getLogger(ArclightCallbackExecutor.class);

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
                LOGGER.error("Failed to execute queued server callback", exception);
            }
        }
    }
}
