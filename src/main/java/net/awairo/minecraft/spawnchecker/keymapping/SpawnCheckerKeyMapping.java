/*
 * SpawnChecker
 * Copyright (C) 2019 alalwww
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package net.awairo.minecraft.spawnchecker.keymapping;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import net.minecraftforge.client.settings.KeyModifier;

import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants.Type;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

@Log4j2
public final class SpawnCheckerKeyMapping extends KeyMapping implements SpawnCheckerKeyMappingImpl {

    private static final String CATEGORY = "spawnchecker.key.categoryName";
    private static final String KEY_BINDING_DESCRIPTION_PREFIX = "spawnchecker.key.";

    private final KeyMappingState state;
    private final int ordinal;
    private final AtomicInteger pressCount = new AtomicInteger(0);

    private long pressStartMillis = 0L;
    private long pressTime = 0L;
    private long lastCountedUpTime = 0L;

    private SpawnCheckerKeyMapping(
        @NonNull KeyMappingState state,
        @NonNull String description,
        @NonNull KeyModifier keyModifier,
        int keyCode,
        int ordinal
    ) {
        super(
            description,
            SpawnCheckerKeyConflictContext.INSTANCE,
            keyModifier,
            Type.KEYSYM,
            keyCode,
            CATEGORY
        );
        this.state = state;
        this.ordinal = ordinal;
    }

    @Override
    public int compareTo(final @NotNull KeyMapping other) {
        if (other instanceof SpawnCheckerKeyMapping) {
            return Integer.compare(ordinal, ((SpawnCheckerKeyMapping) other).ordinal);
        }
        return super.compareTo(other);
    }

    @Override
    public boolean isPressed() {
        return pressCount.getAndUpdate(prev -> Math.max(0, prev - 1)) > 0;
    }

    void update(long nowMilliTime) {

        if (isPressed()) {
            if (isBeforePressed()) {
                pressTime = nowMilliTime - pressStartMillis;
                if (isRepeated(nowMilliTime)) {
                    pressCount.getAndIncrement();
                    lastCountedUpTime = nowMilliTime;
                }
            } else {
                pressCount.set(1);
                lastCountedUpTime = pressStartMillis = nowMilliTime;
                pressTime = 0;
            }
        } else {
            pressStartMillis = 0;
            pressTime = 0;
        }

        // noinspection StatementWithEmptyBody
        while (super.isUnbound()) {
            // consume underlying pressTime
        }
    }

    private boolean isBeforePressed() {
        return pressStartMillis != 0;
    }

    private boolean isRepeated(long nowMilliTime) {
        return pressTime > state.repeatDelay().milliSeconds()
            && nowMilliTime - lastCountedUpTime > state.repeatRate().milliSeconds();
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    static final class Builder {
        @NonNull
        private final KeyMappingState state;
        private String descriptionSuffix;
        private KeyModifier keyModifier;
        private int keyCode;
        private int ordinal;

        SpawnCheckerKeyMapping build() {
            Objects.requireNonNull(descriptionSuffix, "descriptionSuffix");
            Objects.requireNonNull(keyModifier, "keyModifier");
            if (keyCode <= 0)
                throw new IllegalStateException("keyCode");
            if (ordinal < 0)
                throw new IllegalStateException("ordinal");

            return new SpawnCheckerKeyMapping(
                state,
                KEY_BINDING_DESCRIPTION_PREFIX + descriptionSuffix,
                keyModifier,
                keyCode,
                ordinal
            );
        }
    }
}
