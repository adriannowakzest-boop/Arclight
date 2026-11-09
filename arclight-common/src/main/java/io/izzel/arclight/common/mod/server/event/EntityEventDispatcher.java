package io.izzel.arclight.common.mod.server.event;

import io.izzel.arclight.common.mod.util.ArclightCaptures;
import io.izzel.tools.collection.XmapList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.AnimalTameEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.bukkit.craftbukkit.v.event.CraftEventFactory;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v.inventory.CraftItemStack;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EntityEventDispatcher {


    @SubscribeEvent(receiveCanceled = true)
    public void onItemJoinLevel(EntityJoinLevelEvent event) {
        // Entities restored from chunk/entity storage are not new item drops.
        if (event.loadedFromDisk() || !(event.getLevel() instanceof net.minecraft.server.level.ServerLevel) ||
            !(event.getEntity() instanceof ItemEntity itemEntity) || Bukkit.getServer() == null) {
            return;
        }
        ItemSpawnEvent spawnEvent = new ItemSpawnEvent((Item) ((io.izzel.arclight.common.bridge.core.entity.EntityBridge) itemEntity).bridge$getBukkitEntity());
        Bukkit.getPluginManager().callEvent(spawnEvent);
        event.setCanceled(event.isCanceled() || spawnEvent.isCancelled());
    }

    @SubscribeEvent(receiveCanceled = true)
    public void onMobFinalizeSpawn(MobSpawnEvent.FinalizeSpawn event) {
        if (Bukkit.getServer() == null) {
            return;
        }
        CreatureSpawnEvent spawnEvent = CraftEventFactory.callCreatureSpawnEvent(
            event.getEntity(), toBukkitSpawnReason(event.getSpawnType())
        );
        if (spawnEvent.isCancelled()) {
            event.setSpawnCancelled(true);
        }
    }

    private static CreatureSpawnEvent.SpawnReason toBukkitSpawnReason(MobSpawnType spawnType) {
        String reason = switch (spawnType.name()) {
            case "CHUNK_GENERATION" -> "CHUNK_GEN";
            case "SPAWN_EGG" -> "SPAWNER_EGG";
            case "DISPENSER" -> "DISPENSE_EGG";
            case "REINFORCEMENT" -> "REINFORCEMENTS";
            case "MOB_SUMMONED", "COMMAND", "TRIGGERED" -> "CUSTOM";
            default -> spawnType.name();
        };
        try {
            return CreatureSpawnEvent.SpawnReason.valueOf(reason);
        } catch (IllegalArgumentException ignored) {
            return CreatureSpawnEvent.SpawnReason.DEFAULT;
        }
    }

    @SubscribeEvent(receiveCanceled = true)
    public void onLivingDeath(LivingDropsEvent event) {
        if (event.getEntity() instanceof ServerPlayer) {
            // handled at ServerPlayerEntityMixin#onDeath
            // Cancelled at io.izzel.arclight.common.mixin.core.world.entity.LivingEntityMixin#arclight$cancelEvent
            // event.setCanceled(true);
            return;
        }
        LivingEntity livingEntity = event.getEntity();
        Collection<ItemEntity> drops = event.getDrops();
        if (!(drops instanceof ArrayList)) {
            drops = new ArrayList<>(drops);
        }
        List<ItemStack> itemStackList = XmapList.create((List<ItemEntity>) drops, ItemStack.class,
            (ItemEntity entity) -> CraftItemStack.asCraftMirror(entity.getItem()),
            itemStack -> {
                ItemEntity itemEntity = new ItemEntity(livingEntity.level(), livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), CraftItemStack.asNMSCopy(itemStack));
                itemEntity.setDefaultPickUpDelay();
                return itemEntity;
            });
        ArclightEventFactory.callEntityDeathEvent(livingEntity, itemStackList);
        if (drops.isEmpty()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onEntityTame(AnimalTameEvent event) {
        event.setCanceled(event.isCanceled() || CraftEventFactory.callEntityTameEvent(event.getAnimal(), event.getTamer()).isCancelled());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onLightningBolt(EntityStruckByLightningEvent event) {
        ArclightCaptures.captureDamageEventEntity(event.getLightning());
    }
}
