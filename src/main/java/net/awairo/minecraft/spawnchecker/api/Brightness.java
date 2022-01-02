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

package net.awairo.minecraft.spawnchecker.api;

import lombok.Getter;

public enum Brightness {
    MINUS_15(-15), MINUS_14(-14), MINUS_13(-13), MINUS_12(-12), MINUS_11(-11), MINUS_10(-10),
    MINUS_9(-9), MINUS_8(-8), MINUS_7(-7), MINUS_6(-6), MINUS_5(-5), MINUS_4(-4),
    MINUS_3(-3), MINUS_2(-2), MINUS_1(-1),
    ZERO(0),
    PLUS_1(1), PLUS_2(2), PLUS_3(3), PLUS_4(4), PLUS_5(5), PLUS_6(6), PLUS_7(7),
    PLUS_8(8), PLUS_9(9), PLUS_10(10), PLUS_11(11), PLUS_12(12), PLUS_13(13),
    PLUS_14(14), PLUS_15(15);

    public static final Brightness DEFAULT = ZERO;

    @Getter
    private final int value;

    Brightness(int value) {
        this.value = value;
    }

    public Brightness next() {
        return switch (this) {
            case MINUS_15 -> MINUS_14;
            case MINUS_14 -> MINUS_13;
            case MINUS_13 -> MINUS_12;
            case MINUS_12 -> MINUS_11;
            case MINUS_11 -> MINUS_10;
            case MINUS_10 -> MINUS_9;
            case MINUS_9 -> MINUS_8;
            case MINUS_8 -> MINUS_7;
            case MINUS_7 -> MINUS_6;
            case MINUS_6 -> MINUS_5;
            case MINUS_5 -> MINUS_4;
            case MINUS_4 -> MINUS_3;
            case MINUS_3 -> MINUS_2;
            case MINUS_2 -> MINUS_1;
            case MINUS_1 -> ZERO;
            case ZERO -> PLUS_1;
            case PLUS_1 -> PLUS_2;
            case PLUS_2 -> PLUS_3;
            case PLUS_3 -> PLUS_4;
            case PLUS_4 -> PLUS_5;
            case PLUS_5 -> PLUS_6;
            case PLUS_6 -> PLUS_7;
            case PLUS_7 -> PLUS_8;
            case PLUS_8 -> PLUS_9;
            case PLUS_9 -> PLUS_10;
            case PLUS_10 -> PLUS_11;
            case PLUS_11 -> PLUS_12;
            case PLUS_12 -> PLUS_13;
            case PLUS_13 -> PLUS_14;
            case PLUS_14, PLUS_15 -> PLUS_15;
            default -> throw new InternalError("Unreachable code");
        };
    }

    public Brightness prev() {
        return switch (this) {
            case MINUS_15, MINUS_14 -> MINUS_15;
            case MINUS_13 -> MINUS_14;
            case MINUS_12 -> MINUS_13;
            case MINUS_11 -> MINUS_12;
            case MINUS_10 -> MINUS_11;
            case MINUS_9 -> MINUS_10;
            case MINUS_8 -> MINUS_9;
            case MINUS_7 -> MINUS_8;
            case MINUS_6 -> MINUS_7;
            case MINUS_5 -> MINUS_6;
            case MINUS_4 -> MINUS_5;
            case MINUS_3 -> MINUS_4;
            case MINUS_2 -> MINUS_3;
            case MINUS_1 -> MINUS_2;
            case ZERO -> MINUS_1;
            case PLUS_1 -> ZERO;
            case PLUS_2 -> PLUS_1;
            case PLUS_3 -> PLUS_2;
            case PLUS_4 -> PLUS_3;
            case PLUS_5 -> PLUS_4;
            case PLUS_6 -> PLUS_5;
            case PLUS_7 -> PLUS_6;
            case PLUS_8 -> PLUS_7;
            case PLUS_9 -> PLUS_8;
            case PLUS_10 -> PLUS_9;
            case PLUS_11 -> PLUS_10;
            case PLUS_12 -> PLUS_11;
            case PLUS_13 -> PLUS_12;
            case PLUS_14 -> PLUS_13;
            case PLUS_15 -> PLUS_14;
            default -> throw new InternalError("Unreachable code");
        };
    }
}
