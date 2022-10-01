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

import java.util.stream.Collectors;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.JsonOps;

import de.rangun.reanimator.ReAnimatorContext;
import de.rangun.reanimator.ReAnimatorContext.Position;
import de.rangun.reanimator.model.SourceModel;
import de.rangun.reanimator.utils.Utils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.UpdateCommandBlockC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;

@Environment(EnvType.CLIENT)
public final class AssembleCommand extends AbstractReAnimatorContextCommand {

	private final String tag;
	private final double gap;
	private final int time;

	private String modName = "ReAnimator";
	private String modAuthors = "Velnias75, et al.";

	public AssembleCommand(final ReAnimatorContext ctx, final String tag, final double gap, final int time) {
		super(ctx);

		this.tag = tag;
		this.gap = gap + 0.5d;
		this.time = time;

		FabricLoader.getInstance().getModContainer("reanimator").ifPresent((mc) -> {
			this.modName = mc.getMetadata().getName();
			this.modAuthors = mc.getMetadata().getAuthors().stream().map((p) -> {
				return p.getName();
			}).collect(Collectors.joining(", "));
		});
	}

	@Override
	public int run(final CommandContext<FabricClientCommandSource> ctx) throws CommandSyntaxException {

		final ClientPlayerEntity player = ctx.getSource().getPlayer();

		if (player.isCreativeLevelTwoOp()) {

			final SourceModel model = context().getSourceModel();
			final BlockPos targetPos1 = context().getPosition(Position.TARGET_POS1);

			if (model != null && targetPos1 != null) {

				final BlockPos targetPos2 = context().getPosition(Position.TARGET_POS2);
				final BlockPos nPos = new BlockPos(Utils.nPos(targetPos1, targetPos2).add(1d, 1d, 0d));
				final Vec3i dim = Utils.dimension(targetPos1, targetPos2);

				context().setPosition(Position.RESULT_POS1, targetPos1.add(0d, gap + dim.getY() + 1d, 0d));
				context().setPosition(Position.RESULT_POS2, targetPos2.add(0d, gap + dim.getY() + 1d, 0d));

				Utils.traverseArea(BlockPos.ORIGIN, new BlockPos(dim), (modelPos) -> {

					final BlockPos worldPos = nPos.add(modelPos);

					final StringBuilder air = new StringBuilder("setblock ").append(worldPos.getX()).append(' ')
							.append(worldPos.getY()).append(' ').append(worldPos.getZ()).append(' ')
							.append(Registry.BLOCK.getId(Blocks.AIR).toString()).append(" replace");

					final StringBuilder command = new StringBuilder("setblock ").append(worldPos.getX()).append(' ')
							.append(worldPos.getY()).append(' ').append(worldPos.getZ()).append(' ')
							.append(Registry.BLOCK.getId(Blocks.CHAIN_COMMAND_BLOCK).toString())
							.append("[conditional=false,facing=").append(doFacingLayout(modelPos, dim))
							.append("]{CustomName:'{\"text\":\"").append(modName).append(" by ").append(modAuthors)
							.append("\"}',auto:true} replace");

					final BlockState state = model.get(modelPos.getX(), modelPos.getY(), modelPos.getZ());

					player.sendCommand(air.toString());
					player.sendCommand(command.toString());

					if (!(Blocks.AIR.equals(state.getBlock()) || Blocks.CAVE_AIR.equals(state.getBlock())
							|| Blocks.VOID_AIR.equals(state.getBlock()))) {

						player.networkHandler
								.sendPacket(new UpdateCommandBlockC2SPacket(worldPos, createSummonCommand(state, dim),
										CommandBlockBlockEntity.Type.SEQUENCE, false, false, true));
					}

					return false;
				});

			} else if (model == null) {
				ctx.getSource().sendError(Text.literal("please scan a model first with /scan"));
			} else if (targetPos1 == null) {
				ctx.getSource().sendError(Text.literal("please set a target position first with /tpos"));
			}

		} else {
			ctx.getSource().sendError(Text.literal("must be an - at least level 2 - opped player in creative mode"));
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

	private String createSummonCommand(final BlockState state, final Vec3i dim) {
		return new StringBuilder("summon armor_stand ~ ~").append(gap + dim.getY()).append(
				" ~ {CustomNameVisible:0b,NoGravity:1b,Silent:1b,Invulnerable:1b,HasVisualFire:0b,Glowing:1b,ShowArms:0b,Small:1b,Marker:1b,Invisible:1b,NoBasePlate:1b,PersistenceRequired:0b,Tags:[\"")
				.append(tag).append("\"],Passengers:[{id:\"minecraft:falling_block\",BlockState:")
				.append(BlockState.CODEC.encodeStart(JsonOps.INSTANCE, state).result().get())
				.append(",NoGravity:1b,Silent:1b,HasVisualFire:0b,Glowing:0b,Time:").append(time)
				.append(",DropItem:0b,HurtEntities:0b,Tags:[\"").append(tag).append("\"]}],Rotation:[-180F,0F]}")
				.toString();
	}
}
