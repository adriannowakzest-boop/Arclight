package io.papermc.paper.configuration.type.fallback;

import org.spongepowered.configurate.serialize.SerializationException;

import java.util.Map;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.Supplier;

public class AutosavePeriod extends FallbackValue.Int {

    AutosavePeriod(Map<ContextKey<?>, Object> contextMap, Object value) throws SerializationException {
        super(contextMap, fromObject(value));
    }

    private AutosavePeriod(Map<ContextKey<?>, Object> contextMap) {
        super(contextMap, OptionalInt.empty());
    }

    @Override
    protected OptionalInt process(int value) {
        return Util.negToDef(value);
    }

    @Override
    protected Set<ContextKey<?>> required() {
        return Set.of();
    }

    @Override
    protected int fallback() {
        return 6000; // Vanilla Minecraft 1.20.1 autosave interval (ticks).
    }

    public static AutosavePeriod def() {
        return new AutosavePeriod(Map.of());
    }
}
