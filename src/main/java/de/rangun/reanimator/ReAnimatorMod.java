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

package de.rangun.reanimator;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import com.mojang.blaze3d.systems.RenderSystem;

import de.rangun.reanimator.commands.AssembleCommand;
import de.rangun.reanimator.commands.PosCommand;
import de.rangun.reanimator.commands.ScanCommand;
import de.rangun.reanimator.model.SourceModel;
import de.rangun.reanimator.utils.Utils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public final class ReAnimatorMod implements ClientModInitializer, ReAnimatorContext {

	private final static double DEFAULT_GAP = 3.0d;
	private final static int DEFAULT_TIME = -72000;

	private static enum Color {

		RED(255, 0, 0, 255), BLUE(0, 0, 255, 255), YELLOW(255, 255, 0, 255);

		private final int r, g, b, a;

		Color(int r, int g, int b, int a) {

			this.r = r;
			this.g = g;
			this.b = b;
			this.a = a;
		}

		public int red() {
			return r;
		}

		public int green() {
			return g;
		}

		public int blue() {
			return b;
		}

		public int alpha() {
			return a;
		}
	};

	private SourceModel sourceModel = null;

	private BlockPos sourcePos1 = null;
	private BlockPos sourcePos2 = null;

	private BlockPos targetPos1 = null;
	private BlockPos targetPos2 = null;

	private BlockPos resultPos1 = null;
	private BlockPos resultPos2 = null;

	@Override
	public void onInitializeClient() {

		WorldRenderEvents.BEFORE_DEBUG_RENDER.register((ctx) -> {

			RenderSystem.lineWidth(1.0f);
			RenderSystem.depthMask(false);
			RenderSystem.disableTexture();
			RenderSystem.disableBlend();

			RenderSystem.setShader(GameRenderer::getPositionColorShader);

			renderCube(ctx, sourcePos1, sourcePos2, Color.RED);
			renderCube(ctx, targetPos1, targetPos2, Color.BLUE);
			renderCube(ctx, resultPos1, resultPos2, Color.YELLOW);

			RenderSystem.enableBlend();
			RenderSystem.enableTexture();
			RenderSystem.depthMask(true);
		});

		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {

			sourceModel = null;

			sourcePos1 = null;
			sourcePos2 = null;
			targetPos1 = null;
			targetPos2 = null;

		});

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			dispatcher.register(literal("spos1").executes(new PosCommand(Position.SOURCE_POS1, this)));
		});

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			dispatcher.register(literal("spos2").executes(new PosCommand(Position.SOURCE_POS2, this)));
		});

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			dispatcher.register(literal("tpos").executes(new PosCommand(Position.TARGET_POS1, this)));
		});

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			dispatcher.register(literal("scan").executes(new ScanCommand(this)));
		});

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			dispatcher.register(literal("assemble").requires((source) -> source.getPlayer().isCreativeLevelTwoOp())
					.then((argument("tag", word()))
							.then(argument("gap", integer())
									.executes((ctx) -> (new AssembleCommand(this, getString(ctx, "tag"),
											(double) getInteger(ctx, "gap"), DEFAULT_TIME).run(ctx)))
									.then(argument("time", integer())
											.executes((ctx) -> (new AssembleCommand(this, getString(ctx, "tag"),
													(double) getInteger(ctx, "gap"), getInteger(ctx, "time"))
													.run(ctx)))))
							.executes((
									ctx) -> (new AssembleCommand(this, getString(ctx, "tag"), DEFAULT_GAP, DEFAULT_TIME)
											.run(ctx)))));
		});
	}

	private void renderCube(final WorldRenderContext ctx, final BlockPos pos1, final BlockPos pos2, Color color) {

		if (pos1 == null || pos2 == null) {
			return;
		}

		final Vec3d nPos = Utils.nPos(pos1, pos2);
		final Vec3d sPos = Utils.sPos(pos1, pos2);
		final Vec3d camera = ctx.camera().getPos();

		final Tessellator tessellator = Tessellator.getInstance();
		final BufferBuilder buffer = tessellator.getBuffer();

		buffer.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

		final Vec3d[] vertices = new Vec3d[] { new Vec3d(sPos.getX(), sPos.getY(), sPos.getZ() + 1d),
				new Vec3d(sPos.getX(), sPos.getY(), nPos.getZ()), new Vec3d(sPos.getX(), sPos.getY(), nPos.getZ()),
				new Vec3d(nPos.getX() + 1d, sPos.getY(), nPos.getZ()),
				new Vec3d(nPos.getX() + 1d, sPos.getY(), nPos.getZ()),
				new Vec3d(nPos.getX() + 1d, sPos.getY(), sPos.getZ() + 1d),
				new Vec3d(nPos.getX() + 1d, sPos.getY(), sPos.getZ() + 1d),
				new Vec3d(sPos.getX(), sPos.getY(), sPos.getZ() + 1d),
				new Vec3d(sPos.getX(), nPos.getY() + 1d, sPos.getZ() + 1d),
				new Vec3d(sPos.getX(), nPos.getY() + 1d, nPos.getZ()),
				new Vec3d(sPos.getX(), nPos.getY() + 1d, nPos.getZ()),
				new Vec3d(nPos.getX() + 1d, nPos.getY() + 1d, nPos.getZ()),
				new Vec3d(nPos.getX() + 1d, nPos.getY() + 1d, nPos.getZ()),
				new Vec3d(nPos.getX() + 1d, nPos.getY() + 1d, sPos.getZ() + 1d),
				new Vec3d(nPos.getX() + 1d, nPos.getY() + 1d, sPos.getZ() + 1d),
				new Vec3d(sPos.getX(), nPos.getY() + 1d, sPos.getZ() + 1d),
				new Vec3d(sPos.getX(), sPos.getY(), sPos.getZ() + 1d),
				new Vec3d(sPos.getX(), nPos.getY() + 1d, sPos.getZ() + 1d),
				new Vec3d(nPos.getX() + 1d, sPos.getY(), sPos.getZ() + 1d),
				new Vec3d(nPos.getX() + 1d, nPos.getY() + 1d, sPos.getZ() + 1d),
				new Vec3d(sPos.getX(), sPos.getY(), nPos.getZ()), new Vec3d(sPos.getX(), nPos.getY() + 1d, nPos.getZ()),
				new Vec3d(nPos.getX() + 1d, sPos.getY(), nPos.getZ()),
				new Vec3d(nPos.getX() + 1d, nPos.getY() + 1d, nPos.getZ()) };

		for (final Vec3d v : vertices) {

			final Vec3d cv = v.subtract(camera);

			buffer.vertex(cv.getX(), cv.getY(), cv.getZ())
					.color(color.red(), color.green(), color.blue(), color.alpha()).next();
		}

		tessellator.draw();
	}

	@Override
	public void setPosition(final Position position, final BlockPos blockPos) {

		switch (position) {
		case SOURCE_POS1:
			sourcePos1 = blockPos;
			break;
		case SOURCE_POS2:
			sourcePos2 = blockPos;
			break;
		case TARGET_POS1:
			targetPos1 = blockPos;
			break;
		case TARGET_POS2:
			targetPos2 = blockPos;
			break;
		case RESULT_POS1:
			resultPos1 = blockPos;
			break;
		case RESULT_POS2:
			resultPos2 = blockPos;
			break;
		}

		if (targetPos1 != null && sourcePos1 != null && sourcePos2 != null) {
			targetPos2 = targetPos1.subtract(Utils.dimension(sourcePos1, sourcePos2));
		}
	}

	@Override
	public BlockPos getPosition(final Position position) {

		switch (position) {
		case SOURCE_POS1:
			return sourcePos1;
		case SOURCE_POS2:
			return sourcePos2;
		case TARGET_POS1:
			return targetPos1;
		case TARGET_POS2:
			return targetPos2;
		case RESULT_POS1:
			return resultPos1;
		case RESULT_POS2:
			return resultPos2;
		default:
			throw new IllegalStateException("invalid position");
		}
	}

	@Override
	public void setSourceModel(final SourceModel model) {
		this.sourceModel = model;
	}

	@Override
	public SourceModel getSourceModel() {
		return sourceModel;
	}
}
