package io.izzel.arclight.common.mod.server.entity;

import io.papermc.paper.threadedregions.scheduler.EntityScheduler;
import org.bukkit.entity.Entity;

public interface PaperEntitySchedulerBridge {
    default EntityScheduler getScheduler() {
        return new ArclightEntityScheduler((Entity) this);
    }
}
