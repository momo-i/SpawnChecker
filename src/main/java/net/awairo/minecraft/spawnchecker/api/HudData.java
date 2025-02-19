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

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.resources.ResourceLocation;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.log4j.Log4j2;
import lombok.val;

public interface HudData {

    Visibility draw(HudRenderer renderer, long elapsedMillis);

    enum Visibility {SHOW, HIDE}

    @Log4j2
    @RequiredArgsConstructor
    @Getter
    @SuppressWarnings("WeakerAccess")
    class Simple implements HudData {
        protected static final Color BASE_COLOR = Color.of(255, 255, 255);
        protected static final float TEXT_X = 20f;
        protected static final float TEXT_Y = 5f;
        protected static final double ICON_SIZE = 16d;

        @NonNull
        private final ComponentContents text;
        @NonNull
        private final ResourceLocation icon;
        @NonNull
        private final ShowDuration showDuration;
        @NonNull
        private final Color baseColor;

        public Simple(ComponentContents text, ResourceLocation icon, ShowDuration showDuration) {
            this(text, icon, showDuration, BASE_COLOR);
        }

        @Override
        public Visibility draw(@NonNull HudRenderer renderer, long elapsedMillis) {
            if (showDuration.isLessThan(elapsedMillis)) {
                val stack = new PoseStack();
                val color = baseColor.withAlpha(computeAlpha(elapsedMillis));
                if (!color.isTransparent()) {
                    setUpGlState();
                    drawIcon(stack, icon, renderer, color);
                    drawText(stack, text, renderer, color);
                }
                if (elapsedMillis == 0) {
                    log.debug(
                        "Show HudData.(text={}, icon={}, showDuration={}, baseColor={}, elapsedMillis={}, color={})",
                        text, icon, showDuration, baseColor, elapsedMillis, color
                    );
                }
                return Visibility.SHOW;
            }
            log.debug(
                "Hide HudData.(text={}, icon={}, showDuration={}, baseColor={}, elapsedMillis={})",
                text, icon, showDuration, baseColor, elapsedMillis
            );
            return Visibility.HIDE;
        }

        protected float computeAlpha(long elapsedMillis) {
            val rate = showDuration.progressRate(elapsedMillis);
            if (rate <= 0.05f)
                return Math.min(rate * 20, 1f);
            if (rate >= 0.9f)
                return Math.min((1 - rate) * 10, 1f);
            return 1f;
        }

        protected void setUpGlState() {
            RenderSystem.enableTexture();
            RenderSystem.enableTexture();
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(
                SourceFactor.SRC_ALPHA.value, DestFactor.ONE_MINUS_SRC_ALPHA.value,
                SourceFactor.ONE.value, DestFactor.ZERO.value
            );
        }

        @SuppressWarnings("Duplicates")
        protected void drawIcon(PoseStack stack, ResourceLocation icon, HudRenderer renderer, Color color) {
            final double xMin, yMin, xMax, yMax, z;
            final float uMin, uMax, vMin, vMax;
            xMin = yMin = z = 0d;
            xMax = yMax = ICON_SIZE;
            uMin = vMin = 0f;
            uMax = vMax = 1f;
            renderer.bindTexture(icon);
            renderer.beginQuads(DefaultVertexFormat.POSITION_COLOR_TEX);
            renderer.addVertex(xMin, yMin, z, uMin, vMin, color);
            renderer.addVertex(xMin, yMax, z, uMin, vMax, color);
            renderer.addVertex(xMax, yMax, z, uMax, vMax, color);
            renderer.addVertex(xMax, yMin, z, uMax, vMin, color);
            renderer.draw();
        }

        protected void drawText(PoseStack stack, ComponentContents text, HudRenderer renderer, Color color) {
            if (isTransparentText(color))
                return;

            renderer.fontRenderer().drawShadow(stack, text.toString(), Simple.TEXT_X, Simple.TEXT_Y, color.toInt());
        }

        // alpha = 3 以下だと不透明で描画されたためスキップした
        protected boolean isTransparentText(Color color) {
            return color.intAlpha() < 4;
        }
    }

    @SuppressWarnings("WeakerAccess")
    final class ModeActivated extends Simple {
        public ModeActivated(@NonNull Mode mode, @NonNull ShowDuration showDuration) {
            super(mode.name().textComponent(), mode.icon(), showDuration);
        }
    }

    @SuppressWarnings("WeakerAccess")
    @Value
    class ShowDuration {
        public static final long MIN_VALUE = 0L;
        public static final long MAX_VALUE = 10_000L;
        public static final ShowDuration DEFAULT = new ShowDuration(5000L);

        public ShowDuration(long milliSeconds) {
            if (milliSeconds < MIN_VALUE || milliSeconds > MAX_VALUE)
                throw new IllegalArgumentException("Out of range. (" + milliSeconds + ")");

            this.milliSeconds = milliSeconds;
        }

        long milliSeconds;

        public boolean isLessThan(long elapsedMillis) {
            return elapsedMillis < milliSeconds;
        }

        public float progressRate(long elapsedMillis) {
            return Math.min((float) elapsedMillis / (float) milliSeconds, 1f);
        }
    }
}
