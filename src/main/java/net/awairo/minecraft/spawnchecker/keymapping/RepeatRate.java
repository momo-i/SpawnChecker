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

import lombok.Value;

@Value
public class RepeatRate {
    public static final int MIN = 1;
    public static final int MAX = 3_000;
    public static final RepeatRate DEFAULT = new RepeatRate(150);

    public static RepeatRate ofMilliSeconds(int milliSeconds) {
        return milliSeconds == DEFAULT.milliSeconds ? DEFAULT : new RepeatRate(milliSeconds);
    }

    int milliSeconds;

    private RepeatRate(int milliSeconds) {
        if (milliSeconds < MIN || milliSeconds > MAX)
            throw new IllegalArgumentException("Out of range. (" + milliSeconds + ")");
        this.milliSeconds = milliSeconds;
    }
}
