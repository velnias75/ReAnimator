/*
 * Copyright 2022 by Heiko Schäfer <heiko@rangun.de>
 *
 * This file is part of ReAnimator.
 *
 * ReAnimator is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * ReAnimator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ReAnimator.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.rangun.reanimator.utils;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public final class Utils {

	private Utils() {
	}

	public static void traverseArea(final BlockPos pos1, final BlockPos pos2, final AreaVisitor callback) {

		final BlockPos nPos = new BlockPos(nPos(pos1, pos2));
		final BlockPos sPos = new BlockPos(sPos(pos1, pos2));

		for (int y = Math.min(nPos.getY(), sPos.getY()) + 1; y < Math.max(sPos.getY(), nPos.getY()); ++y) {

			for (int x = Math.min(nPos.getX(), sPos.getX()) + 1; x < Math.max(sPos.getX(), nPos.getX()); ++x) {

				for (int z = Math.min(nPos.getZ(), sPos.getZ()); z <= Math.max(sPos.getZ(), nPos.getZ()); ++z) {

					if (callback.visit(new BlockPos(x, y, z))) {
						return;
					}
				}
			}
		}
	}

	public static Vec3d nPos(final BlockPos pos1, final BlockPos pos2) {
		return new Vec3d(Math.min(pos1.getX(), pos2.getX()) - 1d, Math.min(pos1.getY(), pos2.getY()) - 1d,
				Math.min(pos1.getZ(), pos2.getZ()));
	}

	public static Vec3d sPos(final BlockPos pos1, final BlockPos pos2) {
		return new Vec3d(Math.max(pos1.getX(), pos2.getX()) + 1d, Math.max(pos1.getY(), pos2.getY()) + 1d,
				Math.max(pos1.getZ(), pos2.getZ()));
	}

	public static Vec3i dimension(final BlockPos pos1, final BlockPos pos2) {
		return new Vec3i(Math.max(pos1.getX(), pos2.getX()) - Math.min(pos1.getX(), pos2.getX()),
				Math.max(pos1.getY(), pos2.getY()) - Math.min(pos1.getY(), pos2.getY()),
				Math.max(pos1.getZ(), pos2.getZ()) - Math.min(pos1.getZ(), pos2.getZ()));
	}
}
