package io.izzel.arclight.common.mod.server.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.bukkit.util.BoundingBox;
import org.bukkit.Location;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.v.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.World;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;
import org.bukkit.entity.Player;

final class PaperEntityCompatibility {
    private static final java.util.Map<net.minecraft.world.entity.LivingEntity, Integer> SHIELD_DELAYS = new WeakHashMap<>();
    private static final java.util.Map<net.minecraft.world.entity.LivingEntity, Integer> STINGER_DELAYS = new WeakHashMap<>();
    private PaperEntityCompatibility() {}

    static synchronized int getShieldBlockingDelay(net.minecraft.world.entity.LivingEntity entity) {
        return SHIELD_DELAYS.getOrDefault(entity, 0);
    }
    static synchronized void setShieldBlockingDelay(net.minecraft.world.entity.LivingEntity entity, int delay) {
        SHIELD_DELAYS.put(entity, Math.max(0, delay));
    }
    static synchronized int getBeeStingerCooldown(net.minecraft.world.entity.LivingEntity entity) { return STINGER_DELAYS.getOrDefault(entity, 0); }
    static synchronized void setBeeStingerCooldown(net.minecraft.world.entity.LivingEntity entity, int ticks) { STINGER_DELAYS.put(entity, Math.max(0, ticks)); }
    static boolean fromMobSpawner(Entity entity) { return false; }
    static Location getOrigin(Entity entity) { return null; }
    static boolean hasLineOfSight(net.minecraft.world.entity.LivingEntity entity, Location location) {
        if (location.getWorld() == null || entity.level().isClientSide) return false;
        net.minecraft.world.phys.Vec3 start = entity.getEyePosition();
        net.minecraft.world.phys.Vec3 end = new net.minecraft.world.phys.Vec3(location.getX(), location.getY(), location.getZ());
        return entity.level().clip(new net.minecraft.world.level.ClipContext(start, end,
            net.minecraft.world.level.ClipContext.Block.COLLIDER, net.minecraft.world.level.ClipContext.Fluid.NONE, entity)).getType()
            == net.minecraft.world.phys.HitResult.Type.MISS;
    }
    static org.bukkit.block.BlockFace getTargetBlockFace(net.minecraft.world.entity.LivingEntity entity, int distance, org.bukkit.FluidCollisionMode mode) {
        net.minecraft.world.phys.Vec3 start = entity.getEyePosition();
        net.minecraft.world.phys.Vec3 end = start.add(entity.getViewVector(1.0F).scale(distance));
        net.minecraft.world.level.ClipContext.Fluid fluid = mode == org.bukkit.FluidCollisionMode.ALWAYS
            ? net.minecraft.world.level.ClipContext.Fluid.ANY : net.minecraft.world.level.ClipContext.Fluid.NONE;
        net.minecraft.world.phys.HitResult hit = entity.level().clip(new net.minecraft.world.level.ClipContext(start, end,
            net.minecraft.world.level.ClipContext.Block.COLLIDER, fluid, entity));
        return hit instanceof net.minecraft.world.phys.BlockHitResult
            ? org.bukkit.craftbukkit.v.block.CraftBlock.notchToBlockFace(((net.minecraft.world.phys.BlockHitResult) hit).getDirection()) : null;
    }
    static org.bukkit.block.Block getTargetBlock(net.minecraft.world.entity.LivingEntity entity, int distance, org.bukkit.FluidCollisionMode mode) {
        net.minecraft.world.phys.Vec3 start = entity.getEyePosition();
        net.minecraft.world.phys.Vec3 end = start.add(entity.getViewVector(1.0F).scale(distance));
        net.minecraft.world.level.ClipContext.Fluid fluid = mode == org.bukkit.FluidCollisionMode.ALWAYS
            ? net.minecraft.world.level.ClipContext.Fluid.ANY : net.minecraft.world.level.ClipContext.Fluid.NONE;
        net.minecraft.world.phys.HitResult hit = entity.level().clip(new net.minecraft.world.level.ClipContext(start, end,
            net.minecraft.world.level.ClipContext.Block.COLLIDER, fluid, entity));
        if (!(hit instanceof net.minecraft.world.phys.BlockHitResult) || hit.getType() == net.minecraft.world.phys.HitResult.Type.MISS) return null;
        net.minecraft.core.BlockPos pos = ((net.minecraft.world.phys.BlockHitResult) hit).getBlockPos();
        return ((io.izzel.arclight.common.bridge.core.entity.EntityBridge) (Object) entity).bridge$getBukkitEntity().getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ());
    }
    static boolean clearActivePotionEffects(net.minecraft.world.entity.LivingEntity entity) { return entity.removeAllEffects(); }
    static void setPose(Entity entity, org.bukkit.entity.Pose pose) {
        entity.setPose(net.minecraft.world.entity.Pose.valueOf(pose.name()));
    }
    static void setArrowsInBody(net.minecraft.world.entity.LivingEntity entity, int count) { entity.setArrowCount(Math.max(0, count)); }
    static org.bukkit.util.RayTraceResult rayTraceEntities(net.minecraft.world.entity.LivingEntity entity, int maxDistance, boolean ignoreBlocks) {
        return null;
    }

    static boolean wouldCollide(Entity entity, BoundingBox box) {
        AABB bounds = new AABB(box.getMinX(), box.getMinY(), box.getMinZ(), box.getMaxX(), box.getMaxY(), box.getMaxZ());
        return !entity.level().noCollision(entity, bounds);
    }

    static boolean collidesAt(Entity entity, Location location) {
        AABB bounds = entity.getBoundingBox().move(location.getX() - entity.getX(), location.getY() - entity.getY(), location.getZ() - entity.getZ());
        return !entity.level().noCollision(entity, bounds);
    }

    static List<Location> toLocations(Path path, World world) {
        List<Location> points = new ArrayList<>(path.getNodeCount());
        for (int index = 0; index < path.getNodeCount(); index++) {
            Node node = path.getNode(index);
            points.add(new Location(world, node.x + 0.5D, node.y, node.z + 0.5D));
        }
        return points;
    }

    static ItemStack damageItem(net.minecraft.world.entity.LivingEntity entity, ItemStack stack, int amount) {
        net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(stack);
        nms.hurt(amount, entity.getRandom(), entity instanceof ServerPlayer ? (ServerPlayer) entity : null);
        return CraftItemStack.asBukkitCopy(nms);
    }

    static void damageEquipment(net.minecraft.world.entity.LivingEntity entity, org.bukkit.inventory.EquipmentSlot slot, int amount) {
        net.minecraft.world.item.ItemStack item = entity.getItemBySlot(org.bukkit.craftbukkit.v.CraftEquipmentSlot.getNMS(slot));
        item.hurt(amount, entity.getRandom(), entity instanceof ServerPlayer ? (ServerPlayer) entity : null);
    }

    static Set<Player> trackedPlayers() { return Set.of(); }
    static boolean isTicking(Entity entity) { return !entity.isRemoved(); }
    static boolean isInWaterRainOrBubble(Entity entity) { return entity.isInWaterRainOrBubble(); }
    static boolean isInWaterOrBubble(Entity entity) { return entity.isInWaterOrBubble(); }
    static boolean isInWaterOrRain(Entity entity) { return entity.isInWaterOrRain(); }
    static boolean isInBubbleColumn(Entity entity) {
        return entity.level().getBlockState(entity.blockPosition()).is(net.minecraft.world.level.block.Blocks.BUBBLE_COLUMN);
    }
    static boolean isInRain(Entity entity) { return entity.level().isRainingAt(entity.blockPosition()); }
    static boolean isUnderWater(Entity entity) { return entity.isUnderWater(); }
    static org.bukkit.inventory.EquipmentSlot handRaised(net.minecraft.world.entity.LivingEntity entity) {
        if (!entity.isUsingItem()) return null;
        return entity.getUsedItemHand() == net.minecraft.world.InteractionHand.MAIN_HAND
            ? org.bukkit.inventory.EquipmentSlot.HAND : org.bukkit.inventory.EquipmentSlot.OFF_HAND;
    }
}
