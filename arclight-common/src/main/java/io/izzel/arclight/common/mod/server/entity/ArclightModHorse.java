package io.izzel.arclight.common.mod.server.entity;

import net.minecraft.world.entity.animal.horse.AbstractHorse;
import org.bukkit.craftbukkit.v.CraftServer;
import org.bukkit.craftbukkit.v.entity.CraftAbstractHorse;
import org.bukkit.entity.EntityCategory;
import org.bukkit.entity.Horse;
import org.jetbrains.annotations.NotNull;

public class ArclightModHorse extends CraftAbstractHorse implements PaperEntitySchedulerBridge {
    private net.kyori.adventure.util.TriState arclight$frictionState = net.kyori.adventure.util.TriState.NOT_SET;

    public ArclightModHorse(CraftServer server, AbstractHorse entity) {
        super(server, entity);
    }

    @Override
    public Horse.@NotNull Variant getVariant() {
        return Horse.Variant.HORSE;
    }

    @Override
    public @NotNull EntityCategory getCategory() {
        return EntityCategory.NONE;
    }
    @Override
    public void setEating(boolean eating) {
        getHandle().setEating(eating);
    }
    @Override public io.papermc.paper.threadedregions.scheduler.EntityScheduler getScheduler() { return PaperEntitySchedulerBridge.super.getScheduler(); }
    @Override public boolean isEating() { return getHandle().isEating(); }
    @Override public void setRearing(boolean rearing) { getHandle().setStanding(rearing); }
    @Override public boolean wouldCollideUsing(org.bukkit.util.BoundingBox box) { return PaperEntityCompatibility.wouldCollide(getHandle(), box); }
    @Override public boolean isRearing() { return getHandle().isStanding(); }
    @Override public boolean collidesAt(org.bukkit.Location location) { return PaperEntityCompatibility.collidesAt(getHandle(), location); }
    @Override public float getYaw() { return getHandle().getYRot(); }
    @Override public void setEatingGrass(boolean eating) { getHandle().setEating(eating); }
    @Override public float getPitch() { return getHandle().getXRot(); }
    @Override public boolean isEatingGrass() { return getHandle().canEatGrass(); }
    @Override public double getX() { return getHandle().getX(); }
    @Override public double getY() { return getHandle().getY(); }
    @Override public double getZ() { return getHandle().getZ(); }
    @Override public String getScoreboardEntryName() { return getHandle().getScoreboardName(); }
    @Override public boolean isInPowderedSnow() { return getHandle().isInPowderSnow; }
    @Override public float getBodyYaw() { return getHandle().yBodyRot; }
    @Override public void setBodyYaw(float yaw) { getHandle().setYBodyRot(yaw); }
    @Override public boolean spawnAt(org.bukkit.Location location, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason reason) { return teleport(location); }
    @Override public void damageItemStack(org.bukkit.inventory.EquipmentSlot slot, int amount) { PaperEntityCompatibility.damageEquipment(getHandle(), slot, amount); }
    @Override public java.util.Set<org.bukkit.entity.Player> getTrackedPlayers() { return PaperEntityCompatibility.trackedPlayers(); }
    @Override public org.bukkit.inventory.ItemStack damageItemStack(org.bukkit.inventory.ItemStack stack, int amount) { return PaperEntityCompatibility.damageItem(getHandle(), stack, amount); }
    @Override public boolean isTicking() { return !getHandle().isRemoved(); }
    @Override public void broadcastSlotBreak(org.bukkit.inventory.EquipmentSlot slot, java.util.Collection<org.bukkit.entity.Player> players) { broadcastSlotBreak(slot); }
    @Override public boolean isInLava() { return getHandle().isInLava(); }
    @Override public void broadcastSlotBreak(org.bukkit.inventory.EquipmentSlot slot) { getHandle().broadcastBreakEvent(org.bukkit.craftbukkit.v.CraftEquipmentSlot.getNMS(slot)); }
    @Override public boolean isInWaterOrRainOrBubbleColumn() { return PaperEntityCompatibility.isInWaterRainOrBubble(getHandle()); }
    @Override public void knockback(double strength, double x, double z) { getHandle().knockback(strength, x, z); }
    @Override public boolean isInWaterOrBubbleColumn() { return PaperEntityCompatibility.isInWaterOrBubble(getHandle()); }
    @Override public void setHurtDirection(float direction) { }
    @Override public boolean isInWaterOrRain() { return PaperEntityCompatibility.isInWaterOrRain(getHandle()); }
    @Override public float getHurtDirection() { return getHandle().getHurtDir(); }
    @Override public boolean isInBubbleColumn() { return PaperEntityCompatibility.isInBubbleColumn(getHandle()); }
    @Override public void playPickupItemAnimation(org.bukkit.entity.Item item, int count) { getHandle().take(((org.bukkit.craftbukkit.v.entity.CraftEntity) item).getHandle(), count); }
    @Override public boolean isInRain() { return PaperEntityCompatibility.isInRain(getHandle()); }
    @Override public boolean isUnderWater() { return PaperEntityCompatibility.isUnderWater(getHandle()); }
    @Override public org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason getEntitySpawnReason() { return org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.DEFAULT; }
    @Override public org.bukkit.inventory.EquipmentSlot getHandRaised() { return PaperEntityCompatibility.handRaised(getHandle()); }
    @Override public boolean isHandRaised() { return getHandle().isUsingItem(); }
    @Override public int getHandRaisedTime() { return getHandle().getTicksUsingItem(); }
    @Override public boolean fromMobSpawner() { return false; }
    @Override public int getItemUseRemainingTime() { return getHandle().getUseItemRemainingTicks(); }
    @Override public org.bukkit.Location getOrigin() { return null; }
    @Override public void clearActiveItem() { getHandle().stopUsingItem(); }
    @Override public net.kyori.adventure.text.Component teamDisplayName() { return net.kyori.adventure.text.Component.text(getScoreboardEntryName()); }
    @Override public org.bukkit.inventory.ItemStack getActiveItem() { return org.bukkit.craftbukkit.v.inventory.CraftItemStack.asBukkitCopy(getHandle().getUseItem()); }
    @Override public boolean hasFixedPose() { return false; }
    @Override public int getShieldBlockingDelay() { return PaperEntityCompatibility.getShieldBlockingDelay(getHandle()); }
    @Override public void setShieldBlockingDelay(int delay) { PaperEntityCompatibility.setShieldBlockingDelay(getHandle(), delay); }
    @Override public void setPose(org.bukkit.entity.Pose pose, boolean fixed) { PaperEntityCompatibility.setPose(getHandle(), pose); }
    @Override public int getArrowsStuck() { return getHandle().getArrowCount(); }
    @Override public void setArrowsStuck(int count) { getHandle().setArrowCount(count); }
    @Override public void setSneaking(boolean sneaking) { getHandle().setShiftKeyDown(sneaking); }
    @Override public boolean hasLineOfSight(org.bukkit.Location location) { return PaperEntityCompatibility.hasLineOfSight(getHandle(), location); }
    @Override public boolean isSneaking() { return getHandle().isShiftKeyDown(); }
    @Override public boolean clearActivePotionEffects() { return PaperEntityCompatibility.clearActivePotionEffects(getHandle()); }
    @Override public void lockFreezeTicks(boolean locked) { }
    @Override public void setKiller(org.bukkit.entity.Player killer) { getHandle().setLastHurtByPlayer(killer == null ? null : ((org.bukkit.craftbukkit.v.entity.CraftPlayer) killer).getHandle()); }
    @Override public boolean isFreezeTickingLocked() { return false; }
    @Override public int getBeeStingersInBody() { return getHandle().getStingerCount(); }
    @Override public void setBeeStingersInBody(int count) { getHandle().setStingerCount(count); }
    @Override public boolean teleport(org.bukkit.Location location, org.bukkit.event.player.PlayerTeleportEvent.TeleportCause cause, io.papermc.paper.entity.TeleportFlag... flags) { return teleport(location, cause); }
    @Override public int getBeeStingerCooldown() { return PaperEntityCompatibility.getBeeStingerCooldown(getHandle()); }
    @Override public void setBeeStingerCooldown(int ticks) { PaperEntityCompatibility.setBeeStingerCooldown(getHandle(), ticks); }
    @Override public net.kyori.adventure.text.Component name() { return net.kyori.adventure.text.Component.text(getName()); }
    @Override public void setArrowsInBody(int count, boolean fireEvent) { PaperEntityCompatibility.setArrowsInBody(getHandle(), count); }
    @Override public void customName(net.kyori.adventure.text.Component name) { setCustomName(name == null ? null : net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection().serialize(name)); }
    @Override public org.bukkit.util.RayTraceResult rayTraceEntities(int maxDistance, boolean ignoreBlocks) { return PaperEntityCompatibility.rayTraceEntities(getHandle(), maxDistance, ignoreBlocks); }
    @Override public net.kyori.adventure.text.Component customName() { return getCustomName() == null ? null : net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection().deserialize(getCustomName()); }
    @Override public com.destroystokyo.paper.entity.TargetEntityInfo getTargetEntityInfo(int maxDistance, boolean ignoreBlocks) { return null; }
    @Override public org.bukkit.entity.Entity getTargetEntity(int maxDistance, boolean ignoreBlocks) { return null; }
    @Override public java.util.UUID getOwnerUniqueId() { return getHandle().getOwnerUUID(); }
    @Override public com.destroystokyo.paper.block.TargetBlockInfo getTargetBlockInfo(int maxDistance, com.destroystokyo.paper.block.TargetBlockInfo.FluidMode fluidMode) { return null; }
    @Override public int getPossibleExperienceReward() { return getHandle().getExperienceReward(); }
    @Override public boolean isLeftHanded() { return getHandle().isLeftHanded(); }
    @Override public void setLeftHanded(boolean leftHanded) { getHandle().setLeftHanded(leftHanded); }
    @Override public int getMaxHeadPitch() { return getHandle().getMaxHeadXRot(); }
    @Override public int getHeadRotationSpeed() { return getHandle().getHeadRotSpeed(); }
    @Override public void lookAt(double x, double y, double z) { getHandle().getLookControl().setLookAt(x, y, z); }
    @Override public void lookAt(double x, double y, double z, float yawLimit, float pitchLimit) { getHandle().getLookControl().setLookAt(x, y, z, yawLimit, pitchLimit); }
    @Override public void lookAt(org.bukkit.entity.Entity entity) { getHandle().getLookControl().setLookAt(((org.bukkit.craftbukkit.v.entity.CraftEntity) entity).getHandle()); }
    @Override public void lookAt(org.bukkit.entity.Entity entity, float yawLimit, float pitchLimit) { getHandle().getLookControl().setLookAt(((org.bukkit.craftbukkit.v.entity.CraftEntity) entity).getHandle(), yawLimit, pitchLimit); }
    @Override public void lookAt(org.bukkit.Location location) { lookAt(location.getX(), location.getY(), location.getZ()); }
    @Override public void lookAt(org.bukkit.Location location, float yawLimit, float pitchLimit) { lookAt(location.getX(), location.getY(), location.getZ(), yawLimit, pitchLimit); }
    @Override public boolean isInDaylight() { return ((io.izzel.arclight.common.mixin.core.world.entity.MobMixin) (Object) getHandle()).arclight$invokeIsSunBurnTick(); }
    @Override public com.destroystokyo.paper.entity.Pathfinder getPathfinder() { return new ArclightPathfinder(getHandle()); }
    @Override public void setJumping(boolean jumping) { if (jumping) getHandle().getJumpControl().jump(); }
    @Override public boolean isJumping() { return ((io.izzel.arclight.common.mixin.core.world.entity.LivingEntityMixin) (Object) getHandle()).arclight$isJumping(); }
    @Override public org.bukkit.block.BlockFace getTargetBlockFace(int maxDistance, org.bukkit.FluidCollisionMode mode) { return PaperEntityCompatibility.getTargetBlockFace(getHandle(), maxDistance, mode); }
    @Override public org.bukkit.block.BlockFace getTargetBlockFace(int maxDistance, com.destroystokyo.paper.block.TargetBlockInfo.FluidMode mode) { return PaperEntityCompatibility.getTargetBlockFace(getHandle(), maxDistance, mode.bukkit); }
    @Override public org.bukkit.block.Block getTargetBlock(int maxDistance, com.destroystokyo.paper.block.TargetBlockInfo.FluidMode mode) { return PaperEntityCompatibility.getTargetBlock(getHandle(), maxDistance, mode.bukkit); }
    @Override public void registerAttribute(org.bukkit.attribute.Attribute attribute) { }
    @Override public <T extends org.bukkit.entity.Projectile> T launchProjectile(Class<? extends T> projectile) { return launchProjectile(projectile, new org.bukkit.util.Vector()); }
    @Override public <T extends org.bukkit.entity.Projectile> T launchProjectile(Class<? extends T> projectile, org.bukkit.util.Vector velocity) { return launchProjectile(projectile, velocity, null); }
    @Override public <T extends org.bukkit.entity.Projectile> T launchProjectile(Class<? extends T> projectile, org.bukkit.util.Vector velocity, org.bukkit.util.Consumer<T> function) {
        return getWorld().spawn(getLocation(), projectile, spawned -> { spawned.setShooter(this); spawned.setVelocity(velocity); if (function != null) function.accept(spawned); });
    }
    @Override public net.kyori.adventure.util.TriState getFrictionState() { return arclight$frictionState; }
    @Override public void setFrictionState(net.kyori.adventure.util.TriState state) { arclight$frictionState = state; }
}
