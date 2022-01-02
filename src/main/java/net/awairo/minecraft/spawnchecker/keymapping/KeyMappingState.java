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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import net.minecraftforge.client.settings.KeyModifier;
import org.lwjgl.glfw.GLFW;
import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;

import net.awairo.minecraft.spawnchecker.config.SpawnCheckerConfig;
import net.awairo.minecraft.spawnchecker.mode.ModeState;

import lombok.*;
import lombok.extern.log4j.Log4j2;

@Getter
@Log4j2
public final class KeyMappingState {

    @Getter(AccessLevel.NONE)
    private final Collection<SpawnCheckerKeyMapping> bindings;
    @Getter(AccessLevel.NONE)
    private final ModeState modeState;
    @Getter(AccessLevel.NONE)
    private final SpawnCheckerConfig config;

    private final SpawnCheckerKeyMapping prevMode;
    private final SpawnCheckerKeyMapping nextMode;

    private final SpawnCheckerKeyMapping prevModeOption;
    private final SpawnCheckerKeyMapping nextModeOption;

    private final SpawnCheckerKeyMapping horizontalRangePlus;
    private final SpawnCheckerKeyMapping horizontalRangeMinus;

    private final SpawnCheckerKeyMapping verticalRangePlus;
    private final SpawnCheckerKeyMapping verticalRangeMinus;

    private final SpawnCheckerKeyMapping brightnessPlus;
    private final SpawnCheckerKeyMapping brightnessMinus;

    public KeyMappingState(@NonNull ModeState modeState, @NonNull SpawnCheckerConfig config) {
        this.modeState = modeState;
        this.config = config;

        val bindings = new LinkedList<SpawnCheckerKeyMapping>();

        // FIXME: キーバインド処理の実装とリストへの登録

        prevMode = new SpawnCheckerKeyMapping.Builder(this)
            .descriptionSuffix("prevMode")
            .keyModifier(KeyModifier.CONTROL)
            .keyCode(GLFW.GLFW_KEY_UP)
            .ordinal(0)
            .build();
            //bindings.add(prevMode);

        nextMode = new SpawnCheckerKeyMapping.Builder(this)
            .descriptionSuffix("nextMode")
            .keyModifier(KeyModifier.CONTROL)
            .keyCode(GLFW.GLFW_KEY_DOWN)
            .ordinal(1)
            .build();
        //        bindings.add(nextMode);

        prevModeOption = new SpawnCheckerKeyMapping.Builder(this)
            .descriptionSuffix("prevModeOption")
            .keyModifier(KeyModifier.NONE)
            .keyCode(GLFW.GLFW_KEY_UP)
            .ordinal(2)
            .build();
        //        bindings.add(prevModeOption);

        nextModeOption = new SpawnCheckerKeyMapping.Builder(this)
            .descriptionSuffix("nextModeOption")
            .keyModifier(KeyModifier.NONE)
            .keyCode(GLFW.GLFW_KEY_DOWN)
            .ordinal(3)
            .build();
        //        bindings.add(nextModeOption);

        horizontalRangePlus = new SpawnCheckerKeyMapping.Builder(this)
            .descriptionSuffix("horizontalRangePlus")
            .keyModifier(KeyModifier.NONE)
            .keyCode(GLFW.GLFW_KEY_KP_ADD)
            .ordinal(4)
            .build();
        // @TODO ハングアップするの何とかする
        //bindings.add(horizontalRangePlus);

        horizontalRangeMinus = new SpawnCheckerKeyMapping.Builder(this)
            .descriptionSuffix("horizontalRangeMinus")
            .keyModifier(KeyModifier.NONE)
            .keyCode(GLFW.GLFW_KEY_KP_SUBTRACT)
            .ordinal(5)
            .build();
        // @TODO ハングアップするの何とかする
        //bindings.add(horizontalRangeMinus);

        verticalRangePlus = new SpawnCheckerKeyMapping.Builder(this)
            .descriptionSuffix("verticalRangePlus")
            .keyModifier(KeyModifier.CONTROL)
            .keyCode(GLFW.GLFW_KEY_KP_ADD)
            .ordinal(6)
            .build();
        // @TODO ハングアップするの何とかする
        //bindings.add(verticalRangePlus);

        verticalRangeMinus = new SpawnCheckerKeyMapping.Builder(this)
            .descriptionSuffix("verticalRangeMinus")
            .keyModifier(KeyModifier.CONTROL)
            .keyCode(GLFW.GLFW_KEY_KP_SUBTRACT)
            .ordinal(7)
            .build();
        // @TODO ハングアップするの何とかする
        //bindings.add(verticalRangeMinus);

        brightnessPlus = new SpawnCheckerKeyMapping.Builder(this)
            .descriptionSuffix("brightnessPlus")
            .keyModifier(KeyModifier.ALT)
            .keyCode(GLFW.GLFW_KEY_KP_ADD)
            .ordinal(8)
            .build();
        //        bindings.add(brightnessPlus);

        brightnessMinus = new SpawnCheckerKeyMapping.Builder(this)
            .descriptionSuffix("brightnessMinus")
            .keyModifier(KeyModifier.ALT)
            .keyCode(GLFW.GLFW_KEY_KP_SUBTRACT)
            .ordinal(9)
            .build();
        //        bindings.add(brightnessMinus);

        this.bindings = Collections.unmodifiableCollection(bindings);
    }

    RepeatDelay repeatDelay() {
        return config.keyConfig().repeatDelay();
    }

    RepeatRate repeatRate() {
        return config.keyConfig().repeatRate();
    }

    public Collection<KeyMapping> bindings() {
        return Collections.unmodifiableCollection(bindings);
    }

    public void onTick(long nowMilliTime) {
        bindings.parallelStream().forEach(s -> s.update(nowMilliTime));

        // mode
        while (prevMode().isPressed()) {
            modeState.proceedPrevMode();
        }
        while (nextMode().isPressed()) {
            modeState.proceedNextMode();
        }

        // mode option
        while (prevModeOption().isPressed()) {
            modeState.proceedPrevModeOption();
        }
        while (nextModeOption().isPressed()) {
            modeState.proceedNextModeOption();
        }

        // h range
        while (horizontalRangePlus().isPressed()) {
            modeState.proceedNextHorizontalRange();
        }
        while (horizontalRangeMinus().isPressed()) {
            modeState.proceedPrevHorizontalRange();
        }

        // v range
        while (verticalRangePlus().isPressed()) {
            modeState.proceedNextVerticalRange();
        }
        while (verticalRangeMinus().isPressed()) {
            modeState.proceedPrevVerticalRange();
        }

        // brightness
        while (brightnessPlus().isPressed()) {
            modeState.proceedNextBrightness();
        }
        while (brightnessMinus().isPressed()) {
            modeState.proceedPrevBrightness();
        }
    }
}

