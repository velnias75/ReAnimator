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

package de.rangun.reanimator.commands;

import com.mojang.brigadier.Command;

import de.rangun.reanimator.ReAnimatorContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

abstract class ReAnimatorContextCommand implements Command<FabricClientCommandSource> {

	private final ReAnimatorContext ctx;

	public ReAnimatorContextCommand(final ReAnimatorContext ctx) {
		this.ctx = ctx;
	}

	protected ReAnimatorContext context() {
		return ctx;
	}
}
