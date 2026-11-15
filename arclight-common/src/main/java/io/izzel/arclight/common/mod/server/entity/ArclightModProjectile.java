package io.izzel.arclight.common.mod.server.entity;

import net.minecraft.world.entity.projectile.Projectile;
import org.bukkit.craftbukkit.v.CraftServer;
import org.bukkit.craftbukkit.v.entity.CraftProjectile;

public class ArclightModProjectile extends CraftProjectile implements PaperEntitySchedulerBridge {

    public ArclightModProjectile(CraftServer server, Projectile entity) {
        super(server, entity);
    }
    @Override
    public String getScoreboardEntryName() {
        return getHandle().getScoreboardName();
    }
    @Override public io.papermc.paper.threadedregions.scheduler.EntityScheduler getScheduler() { return PaperEntitySchedulerBridge.super.getScheduler(); }
    @Override public boolean wouldCollideUsing(org.bukkit.util.BoundingBox box) { return PaperEntityCompatibility.wouldCollide(getHandle(), box); }
    @Override public boolean collidesAt(org.bukkit.Location location) { return PaperEntityCompatibility.collidesAt(getHandle(), location); }
    @Override public float getYaw() { return getHandle().getYRot(); }
    @Override public float getPitch() { return getHandle().getXRot(); }
    @Override public double getX() { return getHandle().getX(); }
    @Override public double getY() { return getHandle().getY(); }
    @Override public double getZ() { return getHandle().getZ(); }
    @Override public boolean isInPowderedSnow() { return getHandle().isInPowderSnow; }
    @Override public boolean spawnAt(org.bukkit.Location location, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason reason) { return teleport(location); }
    @Override public java.util.Set<org.bukkit.entity.Player> getTrackedPlayers() { return PaperEntityCompatibility.trackedPlayers(); }
    @Override public boolean isTicking() { return !getHandle().isRemoved(); }
    @Override public boolean isInLava() { return getHandle().isInLava(); }
    @Override public boolean isInWaterOrRainOrBubbleColumn() { return PaperEntityCompatibility.isInWaterRainOrBubble(getHandle()); }
    @Override public boolean isInWaterOrBubbleColumn() { return PaperEntityCompatibility.isInWaterOrBubble(getHandle()); }
    @Override public boolean isInWaterOrRain() { return PaperEntityCompatibility.isInWaterOrRain(getHandle()); }
    @Override public boolean isInBubbleColumn() { return PaperEntityCompatibility.isInBubbleColumn(getHandle()); }
    @Override public boolean isInRain() { return PaperEntityCompatibility.isInRain(getHandle()); }
    @Override public boolean isUnderWater() { return PaperEntityCompatibility.isUnderWater(getHandle()); }
    @Override public org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason getEntitySpawnReason() { return org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.DEFAULT; }
    @Override public boolean fromMobSpawner() { return false; }
    @Override public org.bukkit.Location getOrigin() { return null; }
    @Override public net.kyori.adventure.text.Component teamDisplayName() { return net.kyori.adventure.text.Component.text(getScoreboardEntryName()); }
    @Override public boolean hasFixedPose() { return false; }
    @Override public void setPose(org.bukkit.entity.Pose pose, boolean fixed) { PaperEntityCompatibility.setPose(getHandle(), pose); }
    @Override public void setSneaking(boolean sneaking) { getHandle().setShiftKeyDown(sneaking); }
    @Override public boolean isSneaking() { return getHandle().isShiftKeyDown(); }
    @Override public void lockFreezeTicks(boolean locked) { }
    @Override public boolean isFreezeTickingLocked() { return false; }
    @Override public boolean teleport(org.bukkit.Location location, org.bukkit.event.player.PlayerTeleportEvent.TeleportCause cause, io.papermc.paper.entity.TeleportFlag... flags) { return teleport(location, cause); }
    @Override public net.kyori.adventure.text.Component name() { return net.kyori.adventure.text.Component.text(getName()); }
    @Override public void customName(net.kyori.adventure.text.Component name) { setCustomName(name == null ? null : net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection().serialize(name)); }
    @Override public net.kyori.adventure.text.Component customName() { return getCustomName() == null ? null : net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection().deserialize(getCustomName()); }
    @Override public java.util.UUID getOwnerUniqueId() { return getHandle().getOwner() == null ? null : getHandle().getOwner().getUUID(); }
    @Override public void hitEntity(org.bukkit.entity.Entity entity) {
        net.minecraft.world.entity.Entity target = ((org.bukkit.craftbukkit.v.entity.CraftEntity) entity).getHandle();
        ((io.izzel.arclight.common.mixin.core.world.entity.projectile.ProjectileMixin) (Object) getHandle()).arclight$invokePreOnHit(new net.minecraft.world.phys.EntityHitResult(target, getHandle().position()));
    }
    @Override public void hitEntity(org.bukkit.entity.Entity entity, org.bukkit.util.Vector vector) {
        net.minecraft.world.entity.Entity target = ((org.bukkit.craftbukkit.v.entity.CraftEntity) entity).getHandle();
        ((io.izzel.arclight.common.mixin.core.world.entity.projectile.ProjectileMixin) (Object) getHandle()).arclight$invokePreOnHit(new net.minecraft.world.phys.EntityHitResult(target, new net.minecraft.world.phys.Vec3(vector.getX(), vector.getY(), vector.getZ())));
    }
    @Override public boolean canHitEntity(org.bukkit.entity.Entity entity) { return ((io.izzel.arclight.common.mixin.core.world.entity.projectile.ProjectileMixin) (Object) getHandle()).arclight$canHitEntity(((org.bukkit.craftbukkit.v.entity.CraftEntity) entity).getHandle()); }
    @Override public void setHasBeenShot(boolean shot) { ((io.izzel.arclight.common.mixin.core.world.entity.projectile.ProjectileMixin) (Object) getHandle()).arclight$setHasBeenShot(shot); }
    @Override public boolean hasBeenShot() { return ((io.izzel.arclight.common.mixin.core.world.entity.projectile.ProjectileMixin) (Object) getHandle()).arclight$hasBeenShot(); }
    @Override public void setHasLeftShooter(boolean left) { ((io.izzel.arclight.common.mixin.core.world.entity.projectile.ProjectileMixin) (Object) getHandle()).arclight$setHasLeftShooter(left); }
    @Override public boolean hasLeftShooter() { return ((io.izzel.arclight.common.mixin.core.world.entity.projectile.ProjectileMixin) (Object) getHandle()).arclight$hasLeftShooter(); }
}
