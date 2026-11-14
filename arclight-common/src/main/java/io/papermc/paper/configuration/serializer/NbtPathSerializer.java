package io.papermc.paper.configuration.serializer;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.commands.arguments.NbtPathArgument;
import org.spongepowered.configurate.serialize.ScalarSerializer;
import org.spongepowered.configurate.serialize.SerializationException;

public final class NbtPathSerializer extends ScalarSerializer<NbtPathArgument.NbtPath> {
    public static final NbtPathSerializer SERIALIZER = new NbtPathSerializer();
    private static final NbtPathArgument DUMMY_ARGUMENT = new NbtPathArgument();

    private NbtPathSerializer() {
        super(NbtPathArgument.NbtPath.class);
    }

    @Override
    public NbtPathArgument.NbtPath deserialize(final Type type, final Object obj) throws SerializationException {
        return fromString(obj.toString());
    }

    @Override
    protected Object serialize(final NbtPathArgument.NbtPath item, final Predicate<Class<?>> typeSupported) {
        return item.toString();
    }

    public static List<NbtPathArgument.NbtPath> fromString(final List<String> tags) {
        final List<NbtPathArgument.NbtPath> paths = new ArrayList<>();
        for (final String tag : tags) {
            try {
                paths.add(fromString(tag));
            } catch (final SerializationException ex) {
                throw new IllegalArgumentException("Invalid NBT path: " + tag, ex);
            }
        }
        return List.copyOf(paths);
    }

    private static NbtPathArgument.NbtPath fromString(final String tag) throws SerializationException {
        try {
            return DUMMY_ARGUMENT.parse(new StringReader(tag));
        } catch (final CommandSyntaxException ex) {
            throw new SerializationException(NbtPathArgument.NbtPath.class, ex);
        }
    }
}
