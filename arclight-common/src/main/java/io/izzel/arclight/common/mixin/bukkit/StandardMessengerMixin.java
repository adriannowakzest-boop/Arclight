package io.izzel.arclight.common.mixin.bukkit;

import org.bukkit.plugin.messaging.Messenger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At;
import org.bukkit.plugin.messaging.StandardMessenger;

@Mixin(value=StandardMessenger.class, remap = false)
public abstract class StandardMessengerMixin implements Messenger {

    @Redirect(
            method = "validateAndCorrectChannel",
            at = @At(value = "FIELD", target = "Lorg/bukkit/plugin/messaging/Messenger;MAX_CHANNEL_SIZE:I")
    )
    private static int modifyMaxChannelSize() {
        return 256;
    }
}
