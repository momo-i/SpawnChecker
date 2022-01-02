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

package net.awairo.minecraft.spawnchecker.hud;

import java.util.Objects;
import javax.annotation.Nullable;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.Util;

import net.awairo.minecraft.spawnchecker.api.Color;
import net.awairo.minecraft.spawnchecker.api.HudData;
import net.awairo.minecraft.spawnchecker.api.HudData.Visibility;
import net.awairo.minecraft.spawnchecker.api.HudRenderer;
import net.awairo.minecraft.spawnchecker.config.SpawnCheckerConfig;
import net.awairo.minecraft.spawnchecker.mode.ModeState;

import lombok.extern.log4j.Log4j2;
import lombok.*;

// TODO: 位置と表示内容の実装
@Log4j2
@RequiredArgsConstructor
public final class HudRendererImpl implements HudRenderer {
    private final Minecraft minecraft;
    private final SpawnCheckerConfig config;
    private final ModeState state;

    private static final long UNDEFINED = -1;
    @Nullable
    private HudData hudData = null;
    private long showStartTime = UNDEFINED;
    private int tickCount;
    private float partialTicks;

    @Override
    public int tickCount() {
        return tickCount;
    }

    @Override
    public float partialTicks() {
        return partialTicks;
    }

    @Override
    public Font fontRenderer() {
        return minecraft.font;
    }

    @Override
    public void bindTexture(ResourceLocation texture) {
        minecraft.textureManager.getTexture(texture);
    }

    @Override
    public void addVertex(double x, double y, double z) {
        buffer()
            .vertex(x, y, z)
            .endVertex();
    }

    @Override
    public void addVertex(double x, double y, double z, float u, float v) {
        buffer()
            .vertex(x, y, z)
            .uv(u, v)
            .endVertex();
    }

    @Override
    public void addVertex(double x, double y, double z, Color color) {
        buffer()
            .vertex(x, y, z)
            .color(color.red(), color.green(), color.blue(), color.alpha())
            .endVertex();
    }

    @Override
    public void addVertex(double x, double y, double z, float u, float v, Color color) {
        buffer()
            .vertex(x, y, z)
            .color(color.red(), color.green(), color.blue(), color.alpha())
            .uv(u, v)
            .endVertex();
    }

    public void setData(HudData hudData) {
        removeData();
        this.hudData = Objects.requireNonNull(hudData, "hudData");
    }

    private void removeData() {
        this.hudData = null;
        showStartTime = UNDEFINED;
    }

    public void render(int tickCount, float partialTicks) {
        if (hudData == null || minecraft.isPaused())
            return;
        PoseStack modelviewPose = RenderSystem.getModelViewStack();
        this.tickCount = tickCount;
        this.partialTicks = partialTicks;
        val now = Util.getMillis();
        if (showStartTime == UNDEFINED) {
            showStartTime = now;
        }
        val h = minecraft.getWindow().getGuiScaledHeight();
        val w = minecraft.getWindow().getGuiScaledWidth();
        modelviewPose.pushPose();
        modelviewPose.translate(
            w / 20 + config.hudConfig().xOffset().value(),
            h / 3 + config.hudConfig().yOffset().value(),
            0d
        );
        modelviewPose.scale(1.0f, 1.0f, 1f);
        val hudVisibility = hudData.draw(this, now - showStartTime);
        modelviewPose.popPose();

        if (hudVisibility == Visibility.HIDE)
            removeData();
    }
}
