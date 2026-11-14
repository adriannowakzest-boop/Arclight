package io.papermc.paper.configuration.serializer;

import io.papermc.paper.configuration.serializer.collections.MapSerializer;
import net.minecraft.network.protocol.Packet;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.serialize.ScalarSerializer;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Predicate;

/**
 * Resolves packet classes by their Mojang-mapped simple name.
 *
 * <p>The upstream Paper implementation also consults Paper's runtime
 * obfuscation mapping service. Arclight's Forge runtime does not expose that
 * service yet, so this bridge intentionally supports mapped class names only.</p>
 */
public final class PacketClassSerializer extends ScalarSerializer<Class<? extends Packet<?>>> implements MapSerializer.WriteBack {
    private static final List<String> SUBPACKAGES = List.of("game", "handshake", "login", "status");

    public PacketClassSerializer() {
        super((Class) Class.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends Packet<?>> deserialize(final Type type, final Object obj) throws SerializationException {
        for (final String subpackage : SUBPACKAGES) {
            final String fullClassName = "net.minecraft.network.protocol." + subpackage + "." + obj;
            try {
                final Class<?> packetClass = Class.forName(fullClassName);
                if (Packet.class.isAssignableFrom(packetClass)) {
                    return (Class<? extends Packet<?>>) packetClass;
                }
            } catch (final ClassNotFoundException ignored) {
                // Try the next protocol package.
            }
        }
        throw new SerializationException("Could not deserialize a Mojang-mapped packet from " + obj);
    }

    @Override
    protected @Nullable Object serialize(final Class<? extends Packet<?>> packetClass, final Predicate<Class<?>> typeSupported) {
        return packetClass.getSimpleName();
    }
}
