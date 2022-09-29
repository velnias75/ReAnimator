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
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import de.rangun.reanimator.ReAnimatorContext;
import de.rangun.reanimator.ReAnimatorContext.Position;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public final class PosCommand implements Command<FabricClientCommandSource> {

	private final Position position;
	private final ReAnimatorContext rCtx;

	public PosCommand(final Position position, final ReAnimatorContext rCtx) {
		this.position = position;
		this.rCtx = rCtx;
	}

	@Override
	public int run(final CommandContext<FabricClientCommandSource> ctx) throws CommandSyntaxException {

		final MinecraftClient client = ctx.getSource().getClient();
		final HitResult hit = client.crosshairTarget;

		BlockPos blockPos = null;

		switch (hit.getType()) {
		case MISS:
			break;
		case ENTITY:
			break;
		case BLOCK:
			blockPos = ((BlockHitResult) hit).getBlockPos();
			break;
		}

		rCtx.setPosition(position, blockPos);

		return Command.SINGLE_SUCCESS;
	}
}
