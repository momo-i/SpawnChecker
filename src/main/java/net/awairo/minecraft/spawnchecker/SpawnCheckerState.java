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

package net.awairo.minecraft.spawnchecker;

import java.util.Comparator;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.Util;
import net.minecraft.world.level.LevelAccessor;

import net.awairo.minecraft.spawnchecker.config.SpawnCheckerConfig;
import net.awairo.minecraft.spawnchecker.hud.HudRendererImpl;
import net.awairo.minecraft.spawnchecker.keymapping.KeyMappingState;
import net.awairo.minecraft.spawnchecker.mode.ModeState;

import lombok.*;
import lombok.extern.log4j.Log4j2;

@Log4j2
final class SpawnCheckerState {

    private final Set<LevelAccessor> loadedWorldClient = new ConcurrentSkipListSet<>(Comparator.comparing(Object::toString));

    private final Minecraft minecraft;

    @Getter
    private final SpawnCheckerConfig config;

    @Getter
    private final KeyMappingState KeyMappingStates;
    @Getter
    private final ModeState modeState;

    private final HudRendererImpl hudRenderer;

    @Getter(AccessLevel.PACKAGE)
    private final SpawnCheckerCommands commands;

    private int tickCount = 0;

    @Nullable
    private LocalPlayer player = null;

    SpawnCheckerState(Minecraft minecraft, SpawnCheckerConfig config, ModeState state) {
        ModeState modeState1;
        this.minecraft = minecraft;
        this.config = config;
        modeState1 = state;
        hudRenderer = new HudRendererImpl(minecraft, config, modeState1);
        modeState1 = new ModeState(minecraft, config, hudRenderer::setData);
        this.modeState = modeState1;
        KeyMappingStates = new KeyMappingState(modeState, config);
        commands = new SpawnCheckerCommands(config);
    }

    void initialize() {
        modeState.initialize();
    }

    void loadWorld(LevelAccessor world) {
        if (world instanceof ClientLevel) {
            loadedWorldClient.add(world);
            modeState.loadWorldClient((ClientLevel) world);
        }
    }

    void unloadWorld(LevelAccessor world) {
        if (world instanceof ClientLevel) {
            modeState.unloadWorldClient((ClientLevel) world);
            loadedWorldClient.remove(world);
        }
    }

    void onTickStart() {
        if (player != minecraft.player) {
            player = minecraft.player;
            if (player != null)
                commands.registerTo(player);
        }
    }

    void onTickEnd() {
        val nowMilliTime = Util.getMillis();
        tickCount++;
        KeyMappingStates.onTick(nowMilliTime);
        modeState.onTick(tickCount, nowMilliTime);
    }

    boolean started() {
        return enabled() && !loadedWorldClient.isEmpty();
    }

    private boolean enabled() {
        return config.enabled();
    }

    void renderHud(float partialTicks) {
        hudRenderer.render(tickCount, partialTicks);
    }
}
