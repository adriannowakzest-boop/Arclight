package io.izzel.arclight.common.mod.server.entity;

import net.minecraft.world.entity.Mob;
import org.bukkit.craftbukkit.v.CraftServer;
import org.bukkit.craftbukkit.v.entity.CraftMob;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class ArclightModMob extends CraftMob {

    public ArclightModMob(CraftServer server, Mob entity) {
        super(server, entity);
    }
    @Override
    public int getPossibleExperienceReward() {
        return getHandle().getExperienceReward();
    }

    @Override
    public boolean isLeftHanded() {
        return getHandle().isLeftHanded();
    }

    @Override
    public void setLeftHanded(boolean leftHanded) {
        getHandle().setLeftHanded(leftHanded);
    }

    @Override public int getHeadRotationSpeed() { return getHandle().getHeadRotSpeed(); }
    @Override public int getMaxHeadPitch() { return getHandle().getMaxHeadXRot(); }
    @Override public float getBodyYaw() { return getHandle().yBodyRot; }
    @Override public void setBodyYaw(float yaw) { getHandle().setYBodyRot(yaw); }
    @Override public org.bukkit.inventory.ItemStack damageItemStack(org.bukkit.inventory.ItemStack stack, int amount) { return PaperEntityCompatibility.damageItem(getHandle(), stack, amount); }
    @Override public void damageItemStack(org.bukkit.inventory.EquipmentSlot slot, int amount) { PaperEntityCompatibility.damageEquipment(getHandle(), slot, amount); }
    @Override public void broadcastSlotBreak(org.bukkit.inventory.EquipmentSlot slot) { getHandle().broadcastBreakEvent(org.bukkit.craftbukkit.v.CraftEquipmentSlot.getNMS(slot)); }
    @Override public void broadcastSlotBreak(org.bukkit.inventory.EquipmentSlot slot, java.util.Collection<org.bukkit.entity.Player> players) { broadcastSlotBreak(slot); }
    @Override public void knockback(double strength, double x, double z) { getHandle().knockback(strength, x, z); }
    @Override public float getHurtDirection() { return getHandle().getHurtDir(); }
    @Override public void setHurtDirection(float direction) { /* computed by vanilla from the most recent damage source */ }
    @Override public void playPickupItemAnimation(org.bukkit.entity.Item item, int count) { getHandle().take(((org.bukkit.craftbukkit.v.entity.CraftEntity) item).getHandle(), count); }
    @Override public void setJumping(boolean jumping) { if (jumping) getHandle().getJumpControl().jump(); }
    @Override public boolean isJumping() { return ((io.izzel.arclight.common.mixin.core.world.entity.LivingEntityMixin) (Object) getHandle()).arclight$isJumping(); }
    @Override public boolean isInDaylight() { return ((io.izzel.arclight.common.mixin.core.world.entity.MobMixin) (Object) getHandle()).arclight$invokeIsSunBurnTick(); }
    @Override public com.destroystokyo.paper.entity.Pathfinder getPathfinder() { return new ArclightPathfinder(getHandle()); }

    @Override public void lookAt(Location location) { getHandle().getLookControl().setLookAt(location.getX(), location.getY(), location.getZ()); }
    @Override public void lookAt(Location location, float yawLimit, float pitchLimit) { getHandle().getLookControl().setLookAt(location.getX(), location.getY(), location.getZ(), yawLimit, pitchLimit); }
    @Override public void lookAt(Entity entity) { getHandle().getLookControl().setLookAt(((org.bukkit.craftbukkit.v.entity.CraftEntity) entity).getHandle()); }
    @Override public void lookAt(Entity entity, float yawLimit, float pitchLimit) { getHandle().getLookControl().setLookAt(((org.bukkit.craftbukkit.v.entity.CraftEntity) entity).getHandle(), yawLimit, pitchLimit); }
    @Override public void lookAt(double x, double y, double z) { getHandle().getLookControl().setLookAt(x, y, z); }
    @Override public void lookAt(double x, double y, double z, float yawLimit, float pitchLimit) { getHandle().getLookControl().setLookAt(x, y, z, yawLimit, pitchLimit); }
    @Override public org.bukkit.inventory.EquipmentSlot getHandRaised() { return PaperEntityCompatibility.handRaised(getHandle()); }
    @Override public boolean isHandRaised() { return getHandle().isUsingItem(); }
    @Override public int getHandRaisedTime() { return getHandle().getTicksUsingItem(); }
    @Override public int getItemUseRemainingTime() { return getHandle().getUseItemRemainingTicks(); }
    @Override public void clearActiveItem() { getHandle().stopUsingItem(); }
    @Override public org.bukkit.inventory.ItemStack getActiveItem() { return org.bukkit.craftbukkit.v.inventory.CraftItemStack.asBukkitCopy(getHandle().getUseItem()); }
    @Override public int getShieldBlockingDelay() { return PaperEntityCompatibility.getShieldBlockingDelay(getHandle()); }
    @Override public void setShieldBlockingDelay(int delay) { PaperEntityCompatibility.setShieldBlockingDelay(getHandle(), delay); }
    @Override public int getArrowsStuck() { return getHandle().getArrowCount(); }
    @Override public void setArrowsStuck(int count) { getHandle().setArrowCount(count); }
    @Override public boolean hasLineOfSight(org.bukkit.Location location) { return PaperEntityCompatibility.hasLineOfSight(getHandle(), location); }
    @Override public boolean clearActivePotionEffects() { return PaperEntityCompatibility.clearActivePotionEffects(getHandle()); }
    @Override public void setKiller(org.bukkit.entity.Player killer) { getHandle().setLastHurtByPlayer(killer == null ? null : ((org.bukkit.craftbukkit.v.entity.CraftPlayer) killer).getHandle()); }
    @Override public void setBeeStingersInBody(int count) { getHandle().setStingerCount(count); }
}
