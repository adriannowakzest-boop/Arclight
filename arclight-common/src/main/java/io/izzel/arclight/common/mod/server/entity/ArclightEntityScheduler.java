package io.izzel.arclight.common.mod.server.entity;

import io.papermc.paper.threadedregions.scheduler.EntityScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.function.Consumer;

/** Adapts Paper's entity scheduler to Arclight's single-threaded server scheduler. */
public final class ArclightEntityScheduler implements EntityScheduler {
    private final Entity entity;

    public ArclightEntityScheduler(Entity entity) {
        this.entity = entity;
    }

    @Override
    public boolean execute(Plugin plugin, Runnable run, Runnable retired, long delay) {
        if (!this.entity.isValid()) {
            if (retired != null) retired.run();
            return false;
        }
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (this.entity.isValid()) run.run();
            else if (retired != null) retired.run();
        }, Math.max(1L, delay));
        return true;
    }

    @Override
    public ScheduledTask run(Plugin plugin, Consumer<ScheduledTask> task, Runnable retired) {
        return this.schedule(plugin, task, retired, 1L, 0L);
    }

    @Override
    public ScheduledTask runDelayed(Plugin plugin, Consumer<ScheduledTask> task, Runnable retired, long delay) {
        return this.schedule(plugin, task, retired, Math.max(1L, delay), 0L);
    }

    @Override
    public ScheduledTask runAtFixedRate(Plugin plugin, Consumer<ScheduledTask> task, Runnable retired, long initialDelay, long period) {
        if (period < 1L) throw new IllegalArgumentException("period must be at least 1 tick");
        return this.schedule(plugin, task, retired, Math.max(1L, initialDelay), period);
    }

    private ScheduledTask schedule(Plugin plugin, Consumer<ScheduledTask> callback, Runnable retired, long delay, long period) {
        if (!this.entity.isValid()) {
            if (retired != null) retired.run();
            return null;
        }
        LegacyTask[] holder = new LegacyTask[1];
        Runnable action = () -> {
            LegacyTask scheduled = holder[0];
            if (!this.entity.isValid()) {
                scheduled.state = ScheduledTask.ExecutionState.CANCELLED;
                scheduled.bukkitTask.cancel();
                if (retired != null) retired.run();
                return;
            }
            scheduled.state = ScheduledTask.ExecutionState.RUNNING;
            callback.accept(scheduled);
            if (period == 0L) scheduled.state = ScheduledTask.ExecutionState.FINISHED;
            else if (scheduled.state == ScheduledTask.ExecutionState.RUNNING) scheduled.state = ScheduledTask.ExecutionState.IDLE;
        };
        BukkitTask bukkitTask = period == 0L
            ? Bukkit.getScheduler().runTaskLater(plugin, action, delay)
            : Bukkit.getScheduler().runTaskTimer(plugin, action, delay, period);
        holder[0] = new LegacyTask(plugin, bukkitTask, period != 0L);
        return holder[0];
    }

    private static final class LegacyTask implements ScheduledTask {
        private final Plugin plugin;
        private final BukkitTask bukkitTask;
        private final boolean repeating;
        private volatile ExecutionState state = ExecutionState.IDLE;

        private LegacyTask(Plugin plugin, BukkitTask bukkitTask, boolean repeating) {
            this.plugin = plugin;
            this.bukkitTask = bukkitTask;
            this.repeating = repeating;
        }

        @Override public Plugin getOwningPlugin() { return this.plugin; }
        @Override public boolean isRepeatingTask() { return this.repeating; }

        @Override
        public CancelledState cancel() {
            if (this.state == ExecutionState.FINISHED) return CancelledState.ALREADY_EXECUTED;
            if (this.state == ExecutionState.RUNNING) {
                this.bukkitTask.cancel();
                this.state = ExecutionState.CANCELLED_RUNNING;
                return CancelledState.RUNNING;
            }
            boolean alreadyCancelled = this.bukkitTask.isCancelled();
            this.bukkitTask.cancel();
            this.state = ExecutionState.CANCELLED;
            if (alreadyCancelled) return this.repeating ? CancelledState.NEXT_RUNS_CANCELLED_ALREADY : CancelledState.CANCELLED_ALREADY;
            return this.repeating ? CancelledState.NEXT_RUNS_CANCELLED : CancelledState.CANCELLED_BY_CALLER;
        }

        @Override
        public ExecutionState getExecutionState() {
            return this.state;
        }
    }
}
