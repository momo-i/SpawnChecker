package net.awairo.minecraft.spawnchecker.keymapping;

import java.util.concurrent.atomic.AtomicInteger;

public interface SpawnCheckerKeyMappingImpl {

    AtomicInteger pressCount = new AtomicInteger(0);
    default boolean isPressed() {
        return pressCount.getAndUpdate(prev -> Math.max(0, prev - 1)) > 0;
    }
}
