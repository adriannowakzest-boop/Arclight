package io.izzel.arclight.common.mod.server.event;

import io.izzel.arclight.common.bridge.core.entity.EntityBridge;
import io.izzel.arclight.common.bridge.core.entity.player.ServerPlayerEntityBridge;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v.entity.CraftEntity;
import org.bukkit.craftbukkit.v.entity.CraftPlayer;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class EntityTeleportEventDispatcher {

    @SubscribeEvent(receiveCanceled = true)
    public void onTeleport(net.minecraftforge.event.entity.EntityTeleportEvent.EnderEntity event) {
        if (event.getEntity() instanceof ServerPlayer) {
            CraftPlayer player = ((ServerPlayerEntityBridge) event.getEntity()).bridge$getBukkitEntity();
            PlayerTeleportEvent bukkitEvent = new PlayerTeleportEvent(player, player.getLocation(), new Location(player.getWorld(), event.getTargetX(), event.getTargetY(), event.getTargetZ()), PlayerTeleportEvent.TeleportCause.ENDER_PEARL);
            Bukkit.getPluginManager().callEvent(bukkitEvent);
            Location destination = bukkitEvent.getTo();
            if (!isValidDestination(destination, player.getWorld())) {
                event.setCanceled(true);
                return;
            }
            event.setCanceled(event.isCanceled() || bukkitEvent.isCancelled());
            event.setTargetX(destination.getX());
            event.setTargetY(destination.getY());
            event.setTargetZ(destination.getZ());
        } else {
            CraftEntity entity = ((EntityBridge) event.getEntity()).bridge$getBukkitEntity();
            EntityTeleportEvent bukkitEvent = new EntityTeleportEvent(entity, entity.getLocation(), new Location(entity.getWorld(), event.getTargetX(), event.getTargetY(), event.getTargetZ()));
            Bukkit.getPluginManager().callEvent(bukkitEvent);
            Location destination = bukkitEvent.getTo();
            if (!isValidDestination(destination, entity.getWorld())) {
                event.setCanceled(true);
                return;
            }
            event.setCanceled(event.isCanceled() || bukkitEvent.isCancelled());
            event.setTargetX(destination.getX());
            event.setTargetY(destination.getY());
            event.setTargetZ(destination.getZ());
        }
    }

    private static boolean isValidDestination(Location destination, org.bukkit.World currentWorld) {
        return destination != null
            && destination.getWorld() == currentWorld
            && Double.isFinite(destination.getX())
            && Double.isFinite(destination.getY())
            && Double.isFinite(destination.getZ());
    }
}
