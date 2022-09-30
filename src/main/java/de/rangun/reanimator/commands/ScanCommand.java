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
import de.rangun.reanimator.utils.Utils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.block.BlockState;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public final class ScanCommand extends ReAnimatorContextCommand {

	public ScanCommand(final ReAnimatorContext ctx) {
		super(ctx);
	}

	@Override
	public int run(final CommandContext<FabricClientCommandSource> ctx) throws CommandSyntaxException {

		final BlockPos sPos1 = context().getPosition(Position.SOURCE_POS1);
		final BlockPos sPos2 = context().getPosition(Position.SOURCE_POS2);

		final World world = ctx.getSource().getWorld();

		if (!(sPos1 == null || sPos2 == null)) {

			Utils.traverseArea(sPos1, sPos2, (pos) -> {

				final BlockState state = world.getBlockState(pos);

				System.out.println(
						"x=" + pos.getX() + ", y=" + pos.getY() + ", z=" + pos.getZ() + ", state=" + state.toString());

				return false;
			});

		} else {
			ctx.getSource().sendError(
					Text.empty().append(Text.literal("please select a source first with /spos1 and /spos2")));
		}

		return Command.SINGLE_SUCCESS;
	}
}
