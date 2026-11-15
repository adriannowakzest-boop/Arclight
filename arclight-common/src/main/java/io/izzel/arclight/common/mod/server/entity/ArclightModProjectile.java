package io.izzel.arclight.common.mod.server.entity;

import net.minecraft.world.entity.projectile.Projectile;
import org.bukkit.craftbukkit.v.CraftServer;
import org.bukkit.craftbukkit.v.entity.CraftProjectile;
import org.bukkit.util.BoundingBox;
import org.bukkit.Location;

public class ArclightModProjectile extends CraftProjectile implements PaperEntitySchedulerBridge {

    public ArclightModProjectile(CraftServer server, Projectile entity) {
        super(server, entity);
    }
    @Override
    public String getScoreboardEntryName() {
        return getHandle().getScoreboardName();
    }

    @Override
    public io.papermc.paper.threadedregions.scheduler.EntityScheduler getScheduler() {
        return PaperEntitySchedulerBridge.super.getScheduler();
    }

    @Override public boolean wouldCollideUsing(BoundingBox box) { return PaperEntityCompatibility.wouldCollide(getHandle(), box); }
    @Override public boolean collidesAt(Location location) { return PaperEntityCompatibility.collidesAt(getHandle(), location); }
    @Override public float getYaw() { return getHandle().getYRot(); }
    @Override public float getPitch() { return getHandle().getXRot(); }
    @Override public double getX() { return getHandle().getX(); }
    @Override public double getY() { return getHandle().getY(); }
    @Override public double getZ() { return getHandle().getZ(); }
    @Override public boolean isInPowderedSnow() { return getHandle().isInPowderSnow; }
    @Override public boolean spawnAt(org.bukkit.Location location, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason reason) { return this.teleport(location); }
    @Override public java.util.Set<org.bukkit.entity.Player> getTrackedPlayers() { return PaperEntityCompatibility.trackedPlayers(); }
    @Override public boolean isTicking() { return PaperEntityCompatibility.isTicking(getHandle()); }
    @Override public boolean isInLava() { return getHandle().isInLava(); }
    @Override public boolean isInWaterOrRainOrBubbleColumn() { return PaperEntityCompatibility.isInWaterRainOrBubble(getHandle()); }
    @Override public boolean isInWaterOrBubbleColumn() { return PaperEntityCompatibility.isInWaterOrBubble(getHandle()); }
    @Override public boolean isInWaterOrRain() { return PaperEntityCompatibility.isInWaterOrRain(getHandle()); }
    @Override public boolean isInBubbleColumn() { return PaperEntityCompatibility.isInBubbleColumn(getHandle()); }
    @Override public boolean isInRain() { return PaperEntityCompatibility.isInRain(getHandle()); }
    @Override public boolean isUnderWater() { return PaperEntityCompatibility.isUnderWater(getHandle()); }
    @Override public org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason getEntitySpawnReason() { return org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.DEFAULT; }
    @Override public boolean fromMobSpawner() { return PaperEntityCompatibility.fromMobSpawner(getHandle()); }
    @Override public org.bukkit.Location getOrigin() { return PaperEntityCompatibility.getOrigin(getHandle()); }
    @Override public net.kyori.adventure.text.Component teamDisplayName() { return net.kyori.adventure.text.Component.text(getHandle().getScoreboardName()); }
    @Override public boolean hasFixedPose() { return false; }
    @Override public void setPose(org.bukkit.entity.Pose pose, boolean fixed) { PaperEntityCompatibility.setPose(getHandle(), pose); }
}
