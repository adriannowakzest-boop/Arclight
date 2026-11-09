package io.izzel.arclight.common.mod.server.event;

import io.izzel.arclight.common.bridge.bukkit.CraftServerBridge;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.bukkit.Bukkit;

public class WorldEventDispatcher {

    @SubscribeEvent
    public void onWorldUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel level) {
            org.bukkit.Server server = Bukkit.getServer();
            if (server instanceof CraftServerBridge bridge) {
                bridge.bridge$removeWorld(level);
            }
        }
    }
}
