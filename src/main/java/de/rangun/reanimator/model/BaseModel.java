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

package de.rangun.reanimator.model;

import de.rangun.reanimator.utils.Utils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

class BaseModel<T extends Object> {

	protected final T modelData[][][];
	protected final int offX;
	protected final int offY;
	protected final int offZ;

	@SuppressWarnings("unchecked")
	protected BaseModel(final BlockPos pos1, final BlockPos pos2) {

		final Vec3i dim = Utils.dimension(pos1, pos2).add(1, 1, 1);
		final BlockPos nPos = new BlockPos(Utils.nPos(pos1, pos2).add(1d, 1d, 0d));

		this.offX = Math.abs(nPos.getX()) * (nPos.getX() < 0 ? 1 : -1);
		this.offY = Math.abs(nPos.getY()) * (nPos.getY() < 0 ? 1 : -1);
		this.offZ = Math.abs(nPos.getZ()) * (nPos.getZ() < 0 ? 1 : -1);

		this.modelData = (T[][][]) new Object[dim.getX()][dim.getY()][dim.getZ()];
	}

	public T get(int x, int y, int z) {
		return modelData[x][y][z];
	}

	public void set(final BlockPos pos, final T state) {
		modelData[pos.getX() + offX][pos.getY() + offY][pos.getZ() + offZ] = state;
	}

}
