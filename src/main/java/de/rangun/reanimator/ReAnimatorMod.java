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

import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public final class ReAnimatorMod implements ClientModInitializer {

	private static enum Color {

		RED(255, 0, 0, 255), BLUE(0, 0, 255, 255);

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

	@Override
	public void onInitializeClient() {

		WorldRenderEvents.BEFORE_DEBUG_RENDER.register((ctx) -> {

			final Vec3d pos1 = new Vec3d(6d, -1d, -8d);
			final Vec3d pos2 = new Vec3d(9d, 4d, -11d);

			final Vec3d pos3 = new Vec3d(1d, 1d, -8d);
			final Vec3d pos4 = new Vec3d(0d, 0d, -7d);

			RenderSystem.lineWidth(1.0f);
			RenderSystem.depthMask(false);
			RenderSystem.disableTexture();
			RenderSystem.disableBlend();

			RenderSystem.setShader(GameRenderer::getPositionColorShader);

			renderCube(ctx, pos1, pos2, Color.RED);
			renderCube(ctx, pos3, pos4, Color.BLUE);

			RenderSystem.enableBlend();
			RenderSystem.enableTexture();
			RenderSystem.depthMask(true);
		});
	}

	private void renderCube(final WorldRenderContext ctx, final Vec3d pos1, final Vec3d pos2, Color color) {

		final Vec3d npos1 = pos1.getZ() < pos2.getZ() ? pos2 : pos1;
		final Vec3d npos2 = pos1.getZ() < pos2.getZ() ? pos1 : pos2;

		final Vec3d camera = ctx.camera().getPos();

		final Tessellator tessellator = Tessellator.getInstance();
		final BufferBuilder buffer = tessellator.getBuffer();

		buffer.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

		final Vec3d[] vertices = new Vec3d[] { new Vec3d(npos1.getX(), npos1.getY(), npos1.getZ() + 1d),
				new Vec3d(npos1.getX(), npos1.getY(), npos2.getZ()),
				new Vec3d(npos1.getX(), npos1.getY(), npos2.getZ()),
				new Vec3d(npos2.getX() + 1d, npos1.getY(), npos2.getZ()),
				new Vec3d(npos2.getX() + 1d, npos1.getY(), npos2.getZ()),
				new Vec3d(npos2.getX() + 1d, npos1.getY(), npos1.getZ() + 1d),
				new Vec3d(npos2.getX() + 1d, npos1.getY(), npos1.getZ() + 1d),
				new Vec3d(npos1.getX(), npos1.getY(), npos1.getZ() + 1d),
				new Vec3d(npos1.getX(), npos2.getY() + 1d, npos1.getZ() + 1d),
				new Vec3d(npos1.getX(), npos2.getY() + 1d, npos2.getZ()),
				new Vec3d(npos1.getX(), npos2.getY() + 1d, npos2.getZ()),
				new Vec3d(npos2.getX() + 1d, npos2.getY() + 1d, npos2.getZ()),
				new Vec3d(npos2.getX() + 1d, npos2.getY() + 1d, npos2.getZ()),
				new Vec3d(npos2.getX() + 1d, npos2.getY() + 1d, npos1.getZ() + 1d),
				new Vec3d(npos2.getX() + 1d, npos2.getY() + 1d, npos1.getZ() + 1d),
				new Vec3d(npos1.getX(), npos2.getY() + 1d, npos1.getZ() + 1d),
				new Vec3d(npos1.getX(), npos1.getY(), npos1.getZ() + 1d),
				new Vec3d(npos1.getX(), npos2.getY() + 1d, npos1.getZ() + 1d),
				new Vec3d(npos2.getX() + 1d, npos1.getY(), npos1.getZ() + 1d),
				new Vec3d(npos2.getX() + 1d, npos2.getY() + 1d, npos1.getZ() + 1d),
				new Vec3d(npos1.getX(), npos1.getY(), npos2.getZ()),
				new Vec3d(npos1.getX(), npos2.getY() + 1d, npos2.getZ()),
				new Vec3d(npos2.getX() + 1d, npos1.getY(), npos2.getZ()),
				new Vec3d(npos2.getX() + 1d, npos2.getY() + 1d, npos2.getZ()) };

		for (final Vec3d v : vertices) {

			final Vec3d cv = v.subtract(camera);

			buffer.vertex(cv.getX(), cv.getY(), cv.getZ())
					.color(color.red(), color.green(), color.blue(), color.alpha()).next();
		}

		tessellator.draw();
	}
}
