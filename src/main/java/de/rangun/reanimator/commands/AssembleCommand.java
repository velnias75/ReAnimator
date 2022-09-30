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
import de.rangun.reanimator.model.SourceModel;
import de.rangun.reanimator.utils.Utils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.block.Blocks;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;

@Environment(EnvType.CLIENT)
public final class AssembleCommand extends AbstractReAnimatorContextCommand {

	public AssembleCommand(final ReAnimatorContext ctx) {
		super(ctx);
	}

	@Override
	public int run(final CommandContext<FabricClientCommandSource> ctx) throws CommandSyntaxException {

		final SourceModel model = context().getSourceModel();
		final BlockPos targetPos1 = context().getPosition(Position.TARGET_POS1);

		if (model != null && targetPos1 != null) {

			final BlockPos targetPos2 = context().getPosition(Position.TARGET_POS2);
			final BlockPos nPos = new BlockPos(Utils.nPos(targetPos1, targetPos2).add(1d, 1d, 0d));
			final Vec3i dim = Utils.dimension(targetPos1, targetPos2);

			Utils.traverseArea(BlockPos.ORIGIN, new BlockPos(dim), (pos) -> {

				// final BlockState state = model.get(pos.getX(), pos.getY(), pos.getZ());

				final StringBuilder command = new StringBuilder("setblock ").append(nPos.getX() + pos.getX())
						.append(' ').append(nPos.getY() + pos.getY()).append(' ').append(nPos.getZ() + pos.getZ())
						.append(' ').append(Registry.BLOCK.getId(Blocks.CHAIN_COMMAND_BLOCK)).append("[facing=")
						.append(doFacingLayout(pos, dim)).append(']');

				// System.out.println(command.toString());

				ctx.getSource().getPlayer().sendCommand(command.toString());

				return false;
			});

		} else if (model == null) {
			ctx.getSource().sendError(Text.literal("please scan a model first with /scan"));
		} else if (targetPos1 == null) {
			ctx.getSource().sendError(Text.literal("please set a target position first with /tpos"));
		}

		return Command.SINGLE_SUCCESS;
	}

	private String doFacingLayout(final BlockPos pos, final Vec3i dim) {

		if (pos.getY() % 2 == 0) {

			if (pos.getX() % 2 == 0) {

				if (pos.getZ() == 0) {
					return pos.getX() != dim.getX() ? "east" : "up";
				} else {
					return "north";
				}

			} else {

				if (pos.getZ() == dim.getZ()) {
					return pos.getX() != dim.getX() ? "east" : "up";
				} else {
					return "south";
				}
			}

		} else {

			if (pos.getX() % 2 == 0) {

				if (pos.getZ() == dim.getZ()) {
					return pos.getX() != 0 ? "west" : "up";
				} else {
					return "south";
				}

			} else {

				if (pos.getZ() != 0) {
					return "north";
				} else {
					return "west";
				}
			}

		}
	}
}
