package io.izzel.arclight.common.bridge.core.entity;

/** Bridges Paper's configurable daylight-burning state to Forge entities. */
public interface PaperSunBurningEntityBridge {

    boolean bridge$shouldBurnInDay();

    void bridge$setShouldBurnInDay(boolean shouldBurnInDay);
}
