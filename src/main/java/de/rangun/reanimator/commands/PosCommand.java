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
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public final class PosCommand extends AbstractReAnimatorContextCommand {

	private final Position position;

	public PosCommand(final Position position, final ReAnimatorContext ctx) {
		super(ctx);
		this.position = position;
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
			blockPos = new BlockPos(((EntityHitResult) hit).getPos());
			break;
		case BLOCK:
			blockPos = ((BlockHitResult) hit).getBlockPos();
			break;
		}

		if (blockPos != null) {

			context().setPosition(position, blockPos);
			ctx.getSource().sendFeedback(Text.literal(
					positionText() + " set to " + blockPos.getX() + ", " + blockPos.getY() + ", " + blockPos.getZ()));

			if (position == Position.TARGET_POS1 && (context().getPosition(Position.SOURCE_POS1) == null
					|| context().getPosition(Position.SOURCE_POS2) == null)) {
				ctx.getSource()
						.sendFeedback(Text.empty()
								.append(Text.literal("please make a source selection with /spos1 and /spos2"))
								.formatted(Formatting.ITALIC));
			}

		} else {
			ctx.getSource().sendError(Text.literal("Could not determine any block at crosshair position"));
		}

		return Command.SINGLE_SUCCESS;
	}

	private String positionText() {

		switch (position) {
		case SOURCE_POS1:
			return "source position 1";
		case SOURCE_POS2:
			return "source position 2";
		case TARGET_POS1:
		case TARGET_POS2:
			return "target position";
		default:
			throw new IllegalStateException("no position text for this position");
		}
	}
}
