package io.izzel.arclight.common.mod.server.entity;

import net.minecraft.world.entity.animal.horse.AbstractHorse;
import org.bukkit.craftbukkit.v.CraftServer;
import org.bukkit.craftbukkit.v.entity.CraftAbstractHorse;
import org.bukkit.entity.EntityCategory;
import org.bukkit.entity.Horse;
import org.jetbrains.annotations.NotNull;

public class ArclightModHorse extends CraftAbstractHorse {

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

    @Override
    public boolean isEating() {
        return getHandle().isEating();
    }

    @Override public boolean isEatingGrass() { return getHandle().isEating(); }
    @Override public void setEatingGrass(boolean eating) { getHandle().setEating(eating); }
    @Override public boolean isRearing() { return getHandle().isStanding(); }
    @Override public void setRearing(boolean rearing) { getHandle().setStanding(rearing); }
    @Override public String getScoreboardEntryName() { return getHandle().getScoreboardName(); }
    @Override public io.papermc.paper.threadedregions.scheduler.EntityScheduler getScheduler() { return new ArclightEntityScheduler(this); }
    @Override public boolean wouldCollideUsing(org.bukkit.util.BoundingBox box) { return PaperEntityCompatibility.wouldCollide(getHandle(), box); }
    @Override public boolean collidesAt(org.bukkit.Location location) { return PaperEntityCompatibility.collidesAt(getHandle(), location); }
    @Override public org.bukkit.inventory.ItemStack damageItemStack(org.bukkit.inventory.ItemStack stack, int amount) { return PaperEntityCompatibility.damageItem(getHandle(), stack, amount); }
    @Override public void damageItemStack(org.bukkit.inventory.EquipmentSlot slot, int amount) { PaperEntityCompatibility.damageEquipment(getHandle(), slot, amount); }
    @Override public float getYaw() { return getHandle().getYRot(); }
    @Override public float getPitch() { return getHandle().getXRot(); }
    @Override public double getX() { return getHandle().getX(); }
    @Override public double getY() { return getHandle().getY(); }
    @Override public double getZ() { return getHandle().getZ(); }
    @Override public boolean isInPowderedSnow() { return getHandle().isInPowderSnow; }
    @Override public boolean spawnAt(org.bukkit.Location location, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason reason) { return this.teleport(location); }
    @Override public java.util.Set<org.bukkit.entity.Player> getTrackedPlayers() { return PaperEntityCompatibility.trackedPlayers(); }
    @Override public void knockback(double strength, double x, double z) { getHandle().knockback(strength, x, z); }
    @Override public boolean isTicking() { return PaperEntityCompatibility.isTicking(getHandle()); }
    @Override public boolean isInLava() { return getHandle().isInLava(); }
    @Override public float getHurtDirection() { return getHandle().getHurtDir(); }
    @Override public void setHurtDirection(float direction) { /* computed by vanilla from the most recent damage source */ }
    @Override public boolean isInWaterOrRainOrBubbleColumn() { return PaperEntityCompatibility.isInWaterRainOrBubble(getHandle()); }
    @Override public boolean isInWaterOrBubbleColumn() { return PaperEntityCompatibility.isInWaterOrBubble(getHandle()); }
    @Override public void setJumping(boolean jumping) { if (jumping) getHandle().getJumpControl().jump(); }
    @Override public boolean isInWaterOrRain() { return PaperEntityCompatibility.isInWaterOrRain(getHandle()); }
    @Override public org.bukkit.inventory.EquipmentSlot getHandRaised() { return PaperEntityCompatibility.handRaised(getHandle()); }
    @Override public boolean isHandRaised() { return getHandle().isUsingItem(); }
    @Override public int getHandRaisedTime() { return getHandle().getTicksUsingItem(); }
    @Override public boolean isInBubbleColumn() { return PaperEntityCompatibility.isInBubbleColumn(getHandle()); }
    @Override public int getItemUseRemainingTime() { return getHandle().getUseItemRemainingTicks(); }
    @Override public boolean isInRain() { return PaperEntityCompatibility.isInRain(getHandle()); }
    @Override public void clearActiveItem() { getHandle().stopUsingItem(); }
    @Override public boolean isUnderWater() { return PaperEntityCompatibility.isUnderWater(getHandle()); }
    @Override public org.bukkit.inventory.ItemStack getActiveItem() { return org.bukkit.craftbukkit.v.inventory.CraftItemStack.asBukkitCopy(getHandle().getUseItem()); }
    @Override public org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason getEntitySpawnReason() { return org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.DEFAULT; }
    @Override public int getShieldBlockingDelay() { return PaperEntityCompatibility.getShieldBlockingDelay(getHandle()); }
    @Override public void setShieldBlockingDelay(int delay) { PaperEntityCompatibility.setShieldBlockingDelay(getHandle(), delay); }
    @Override public boolean fromMobSpawner() { return PaperEntityCompatibility.fromMobSpawner(getHandle()); }
    @Override public int getArrowsStuck() { return getHandle().getArrowCount(); }
    @Override public void setArrowsStuck(int count) { getHandle().setArrowCount(count); }
    @Override public org.bukkit.Location getOrigin() { return PaperEntityCompatibility.getOrigin(getHandle()); }
    @Override public boolean hasLineOfSight(org.bukkit.Location location) { return PaperEntityCompatibility.hasLineOfSight(getHandle(), location); }
    @Override public net.kyori.adventure.text.Component teamDisplayName() { return net.kyori.adventure.text.Component.text(getHandle().getScoreboardName()); }
    @Override public boolean clearActivePotionEffects() { return PaperEntityCompatibility.clearActivePotionEffects(getHandle()); }
    @Override public boolean hasFixedPose() { return false; }
    @Override public void setKiller(org.bukkit.entity.Player killer) { getHandle().setLastHurtByPlayer(killer == null ? null : ((org.bukkit.craftbukkit.v.entity.CraftPlayer) killer).getHandle()); }
    @Override public void setPose(org.bukkit.entity.Pose pose, boolean fixed) { PaperEntityCompatibility.setPose(getHandle(), pose); }
    @Override public void setBeeStingersInBody(int count) { getHandle().setStingerCount(count); }
}
