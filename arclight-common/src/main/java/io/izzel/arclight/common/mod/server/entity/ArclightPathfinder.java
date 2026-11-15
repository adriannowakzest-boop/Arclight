package io.izzel.arclight.common.mod.server.entity;

import com.destroystokyo.paper.entity.Pathfinder;
import io.izzel.arclight.common.bridge.core.entity.EntityBridge;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.Path;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v.entity.CraftEntity;
import org.bukkit.entity.LivingEntity;

import java.util.List;

public final class ArclightPathfinder implements Pathfinder {
    private final Mob handle;
    private boolean canOpenDoors;
    private boolean canPassDoors;
    private boolean canFloat;

    public ArclightPathfinder(Mob handle) {
        this.handle = handle;
    }

    @Override public org.bukkit.entity.Mob getEntity() { return (org.bukkit.entity.Mob) ((EntityBridge) this.handle).bridge$getBukkitEntity(); }
    @Override public void stopPathfinding() { this.handle.getNavigation().stop(); }
    @Override public boolean hasPath() { return this.handle.getNavigation().isInProgress(); }

    @Override
    public PathResult getCurrentPath() {
        Path path = this.handle.getNavigation().getPath();
        return path == null ? null : new Result(path, this.getEntity().getWorld());
    }

    @Override public PathResult findPath(Location location) {
        return this.result(this.handle.getNavigation().createPath(location.getX(), location.getY(), location.getZ(), 0));
    }

    @Override public PathResult findPath(LivingEntity target) {
        return this.result(this.handle.getNavigation().createPath(((CraftEntity) target).getHandle(), 0));
    }

    private PathResult result(Path path) {
        return path == null ? null : new Result(path, this.getEntity().getWorld());
    }

    @Override public boolean moveTo(PathResult path, double speed) {
        return path instanceof Result && this.handle.getNavigation().moveTo(((Result) path).path, speed);
    }

    private NodeEvaluator evaluator() { return this.handle.getNavigation().getNodeEvaluator(); }
    @Override public boolean canOpenDoors() { NodeEvaluator node = evaluator(); return node == null ? this.canOpenDoors : node.canOpenDoors(); }
    @Override public void setCanOpenDoors(boolean value) { this.canOpenDoors = value; NodeEvaluator node = evaluator(); if (node != null) node.setCanOpenDoors(value); }
    @Override public boolean canPassDoors() { NodeEvaluator node = evaluator(); return node == null ? this.canPassDoors : node.canPassDoors(); }
    @Override public void setCanPassDoors(boolean value) { this.canPassDoors = value; NodeEvaluator node = evaluator(); if (node != null) node.setCanPassDoors(value); }
    @Override public boolean canFloat() { NodeEvaluator node = evaluator(); return node == null ? this.canFloat : node.canFloat(); }
    @Override public void setCanFloat(boolean value) { this.canFloat = value; NodeEvaluator node = evaluator(); if (node != null) node.setCanFloat(value); }

    private static final class Result implements PathResult {
        private final Path path;
        private final org.bukkit.World world;
        private Result(Path path, org.bukkit.World world) { this.path = path; this.world = world; }
        @Override public List<Location> getPoints() { return PaperEntityCompatibility.toLocations(this.path, this.world); }
        @Override public int getNextPointIndex() { return this.path.getNextNodeIndex(); }
        @Override public Location getNextPoint() {
            int index = this.path.getNextNodeIndex();
            return index >= this.path.getNodeCount() ? null : this.getPoints().get(index);
        }
        @Override public Location getFinalPoint() {
            return this.path.getNodeCount() == 0 ? null : this.getPoints().get(this.path.getNodeCount() - 1);
        }
    }
}
