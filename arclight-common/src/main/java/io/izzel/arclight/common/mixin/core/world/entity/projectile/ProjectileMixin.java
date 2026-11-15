package io.izzel.arclight.common.mixin.core.world.entity.projectile;

import io.izzel.arclight.common.bridge.core.entity.EntityBridge;
import io.izzel.arclight.common.mixin.core.world.entity.EntityMixin;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.bukkit.craftbukkit.v.entity.CraftEntity;
import org.bukkit.craftbukkit.v.event.CraftEventFactory;
import org.bukkit.projectiles.ProjectileSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(Projectile.class)
public abstract class ProjectileMixin extends EntityMixin {
    @Shadow private boolean hasBeenShot;
    @Shadow private boolean leftOwner;

    // @formatter:off
    @Shadow @Nullable public abstract Entity getOwner();
    @Shadow protected void onHit(HitResult result) { }
    // @formatter:on

    @Inject(method = "setOwner", at = @At("RETURN"))
    private void arclight$updateSource(Entity entityIn, CallbackInfo ci) {
        if (entityIn != null) {
            CraftEntity entity = ((EntityBridge) entityIn).bridge$getBukkitEntity();
            if (entity instanceof ProjectileSource) {
                this.projectileSource = ((ProjectileSource) entity);
            }
        }
    }

    private boolean hitCancelled = false;

    @Inject(method = "onHitBlock", cancellable = true, at = @At("HEAD"))
    private void arclight$cancelBlockHit(BlockHitResult result, CallbackInfo ci) {
        if (hitCancelled) {
            ci.cancel();
        }
    }

    protected void preOnHit(HitResult hitResult) {
        org.bukkit.event.entity.ProjectileHitEvent event = CraftEventFactory.callProjectileHitEvent((Projectile) (Object) this, hitResult);
        this.hitCancelled = event != null && event.isCancelled();
        if (hitResult.getType() == HitResult.Type.BLOCK || !this.hitCancelled) {
            this.onHit(hitResult);
        }
    }

    public void arclight$invokePreOnHit(HitResult hitResult) { this.preOnHit(hitResult); }
    public boolean arclight$canHitEntity(Entity entity) { return this.canHitEntity(entity); }
    public void arclight$setHasBeenShot(boolean value) { this.hasBeenShot = value; }
    public boolean arclight$hasBeenShot() { return this.hasBeenShot; }
    public void arclight$setHasLeftShooter(boolean value) { this.leftOwner = value; }
    public boolean arclight$hasLeftShooter() { return this.leftOwner; }
    @Shadow protected abstract boolean canHitEntity(Entity entity);
}
