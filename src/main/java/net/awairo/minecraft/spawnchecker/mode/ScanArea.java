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

package net.awairo.minecraft.spawnchecker.mode;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Cursor3D;

import net.awairo.minecraft.spawnchecker.api.PlayerPos;
import net.awairo.minecraft.spawnchecker.api.ScanRange;

import lombok.*;

@Value
public class ScanArea {
    private static int CHARACTERISTICS = Spliterator.DISTINCT | Spliterator.IMMUTABLE | Spliterator.NONNULL;
    private static boolean PARALLEL = true;

    PlayerPos playerPos;
    ScanRange.Horizontal hRange;
    ScanRange.Vertical vRange;

    Stream<XZ> xzStream() {
        val minX = playerPos.blockPos().getX() - hRange.value();
        val maxX = playerPos.blockPos().getX() + hRange.value();
        val y = playerPos.blockPos().getY();
        val maxZ = playerPos.blockPos().getZ() + hRange.value();
        val minZ = playerPos.blockPos().getZ() - hRange.value();
        val estSize = (hRange.value() * 2 + 1) ^ 2;
        val posIter = new BlockPosIterator(new Cursor3D(minX, y, minZ, maxX, y, maxZ));

        return StreamSupport
            .stream(Spliterators.spliterator(posIter, estSize, CHARACTERISTICS), PARALLEL)
            .map(XZ::new);
    }

    @RequiredArgsConstructor
    private static final class BlockPosIterator implements Iterator<BlockPos> {
        private final Cursor3D coords;

        @Override
        public boolean hasNext() {
            return coords.advance();
        }

        @Override
        public BlockPos next() {
            return new BlockPos(coords.nextX(), coords.nextY(), coords.nextZ());
        }
    }

    @Value
    class XZ {
        // Area内のいずれかのx,z座標でPlayerと同じy座標のpos
        BlockPos playerYPos;

        IntStream yStream() {
            return IntStream.range(playerYPos.getY() - vRange.value(), playerYPos.getY() + vRange.value() + 1);
        }

        BlockPos withY(int y) {
            return new BlockPos(playerYPos.getX(), y, playerYPos.getZ());
        }

        Stream<BlockPos> posStream() {
            return yStream().mapToObj(this::withY);
        }
    }
}
