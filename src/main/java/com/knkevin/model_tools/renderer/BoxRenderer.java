package com.knkevin.model_tools.renderer;

import com.knkevin.model_tools.Main;
import com.knkevin.model_tools.items.HammerModes;
import com.knkevin.model_tools.items.ModItems;
import com.knkevin.model_tools.models.Model;
import com.knkevin.model_tools.models.utils.Point;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import org.joml.*;

import java.lang.Math;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.knkevin.model_tools.items.HammerModes.*;

/**
 * Handles rendering transformation guides and rendering either a blocks preview or bounding box of the currently loaded Model.
 */
public class BoxRenderer {
	/**
	 * The center of the Model.
	 */
	private static final Vector3f center = new Vector3f();

	/**
	 * The size of the Model.
	 */
	private static final Vector3f size = new Vector3f();

	/**
	 * The alphas for the x, y, and z axes.
	 */
	private static final Vector3i alpha = new Vector3i();

	/**
	 * The default texture to be used when rendering the blocks preview of the model.
	 */
	private static final ResourceLocation defaultTexture = new ResourceLocation("textures/block/iron_block.png");

	/**
	 * A list of points around a circle
	 */
	private static final List<Vector2f> circlePoints = new ArrayList<>();

	/**
	 * The number of points to be generated around in a circle.
	 */
	private static final int numCirclePoints = 32;

	//Initialize points around the circle
	static {
		float radius = size.get(size.maxComponent()) + 1;
		for (int i = 0; i < numCirclePoints; ++i) {
			double angle = 2 * Math.PI * i / numCirclePoints;
			float x = radius * (float) Math.cos(angle);
			float y = radius * (float) Math.sin(angle);
			circlePoints.add(new Vector2f(x, y));
		}
	}

	/**
	 * Calls the appropriate rendering functions.
	 */
	public static void renderEvent(RenderLevelStageEvent event) {
		Player player = Minecraft.getInstance().player;
		Model model = Main.model;
		//Only render if player is holding a ModelHammer and if a Model is loaded.
		if (player == null || !player.getMainHandItem().getItem().equals(ModItems.MODEL_HAMMER.get()) || model == null) return;

		//Center of model.
		center.set(model.position);

		//Size of bounding box.
		size.set(model.maxCorner).mul(model.scale);

		//Set appropriate axis alpha to 255 and unselected axes to 64.
		if (selectedAxis == Axis.ALL) alpha.set(255);
		else alpha.set(64).setComponent(selectedAxis.component, 255);

		Vector3f camera = event.getCamera().getPosition().toVector3f();
		PoseStack poseStack = event.getPoseStack();

		//Copy transformation and normal matrices from poseStack.
		Matrix4f matrix4f = new Matrix4f(poseStack.last().pose()).translate(-camera.x, -camera.y, -camera.z);
		Matrix3f matrix3f = new Matrix3f(poseStack.last().normal());

		//Get rotated transformation and normal matrices.
		Matrix4f rotatedMatrix4f = new Matrix4f(matrix4f).translate(center).rotate(model.rotation).translate(center.negate(new Vector3f()));
		Matrix3f rotatedMatrix3f = new Matrix3f(matrix3f).rotate(model.rotation);

		//Render blocks preview.
		if (viewMode == ViewMode.BLOCKS) renderBlocksPreview(matrix4f, camera, model.blockFaces);

		//Render appropriate visual guides for the transform mode.
		RenderSystem.lineWidth(3);
		switch (HammerModes.transformMode) {
			case ROTATE -> renderRotateGuides(rotatedMatrix4f, rotatedMatrix3f);
			case SCALE -> renderScaleGuides(rotatedMatrix4f, rotatedMatrix3f);
			case TRANSLATE -> renderTranslationGuides(matrix4f, matrix3f, model.rotation);
		}

		//Render bounding box.
		if (viewMode == ViewMode.BOX) {
			//Corners of bounding box.
			Vector3f cornerOne = new Vector3f(center).sub((float) Math.floor(size.x) + .5f, (float) Math.floor(size.y) + .5f, (float) Math.floor(size.z) + .5f);
			Vector3f cornerTwo = new Vector3f(center).add((float) Math.floor(size.x) + .5f, (float) Math.floor(size.y) + .5f, (float) Math.floor(size.z) + .5f);

			//Render model bounding box and box outline.
			RenderSystem.lineWidth(2);
			renderLineBox(rotatedMatrix4f, rotatedMatrix3f, cornerOne, cornerTwo, new Vector4i(255, 255, 255, 255));
			renderBox(rotatedMatrix4f, cornerOne, cornerTwo, new Vector4i(255, 255, 255, 64));
		}

		RenderSystem.enableCull();
	}

	/**
	 * Renders the circles that make up the rotation guides.
	 * The circles are formed by rendering lines connecting points around a circle.
	 * @param matrix4f The transformation matrix.
	 * @param matrix3f The normal matrix.
	 */
	private static void renderRotateGuides(Matrix4f matrix4f, Matrix3f matrix3f) {
		LineBuffer buffer = new LineBuffer(matrix4f, matrix3f);
		buffer.begin();
		for (int i = 0; i < numCirclePoints; ++i) {
			//Get adjacent points in circle.
			Vector2f p1 = new Vector2f(circlePoints.get(i)).mul(size.get(size.maxComponent()) + 1);
			Vector2f p2 = new Vector2f(circlePoints.get((i + 1) % numCirclePoints)).mul(size.get(size.maxComponent()) + 1);

			//Render red, green, and blue lines of circle.
			buffer.color(255,0,0, alpha.x).beginLine(center.x, center.y + p1.x, center.z + p1.y).endLine(center.x, center.y + p2.x, center.z + p2.y);
			buffer.color(0,255,0, alpha.y).beginLine(center.x + p1.x, center.y, center.z + p1.y).endLine(center.x + p2.x, center.y, center.z + p2.y);
			buffer.color(0,0,255, alpha.z).beginLine(center.x + p1.x, center.y + p1.y, center.z).endLine(center.x + p2.x, center.y + p2.y, center.z);
		}
		buffer.end();
	}

	/**
	 * Renders the lines and boxes that make up the scale guides.
	 * @param matrix4f The transformation matrix.
	 * @param matrix3f The normal matrix.
	 */
	private static void renderScaleGuides(Matrix4f matrix4f, Matrix3f matrix3f) {
		//Size of scale guide boxes.
		float boxSize = Math.max(.5f, Math.max(Math.min(size.x, size.y), Math.max(Math.min(size.x, size.z), Math.min(size.y, size.z))))/4;

		//Distance of scale guide boxes from center
		Vector3f offSet = new Vector3f(size).add(boxSize, boxSize, boxSize).add(1,1,1);

		//Render scale guide boxes.
		renderCube(matrix4f, new Vector3f(center).sub(offSet.x, 0,0), boxSize, new Vector4i(255,0,0, alpha.x));
		renderCube(matrix4f, new Vector3f(center).add(offSet.x, 0,0), boxSize, new Vector4i(255,0,0, alpha.x));
		renderCube(matrix4f, new Vector3f(center).sub(0, offSet.y,0), boxSize, new Vector4i(0,255,0, alpha.y));
		renderCube(matrix4f, new Vector3f(center).add(0, offSet.y,0), boxSize, new Vector4i(0,255,0, alpha.y));
		renderCube(matrix4f, new Vector3f(center).sub(0,0, offSet.z), boxSize, new Vector4i(0,0,255, alpha.z));
		renderCube(matrix4f, new Vector3f(center).add(0,0, offSet.z), boxSize, new Vector4i(0,0,255, alpha.z));

		//Render scale guide lines.
		LineBuffer buffer = new LineBuffer(matrix4f, matrix3f);
		buffer.begin();
		buffer.color(255,0,0, alpha.x).beginLine(center.x - offSet.x, center.y, center.z).endLine(center.x + offSet.x, center.y, center.z);
		buffer.color(0,255,0, alpha.y).beginLine(center.x, center.y - offSet.y, center.z).endLine(center.x, center.y + offSet.y, center.z);
		buffer.color(0,0,255, alpha.z).beginLine(center.x, center.y, center.z - offSet.z).endLine(center.x, center.y, center.z + offSet.z);
		buffer.end();
	}

	/**
	 * Renders the lines that make up the translation guides.
	 * @param matrix4f The transformation matrix.
	 * @param matrix3f The normal matrix.
	 * @param rotation The rotation of the Model.
	 */
	private static void renderTranslationGuides(Matrix4f matrix4f, Matrix3f matrix3f, Quaternionf rotation) {
		//Find the minimum and maximum corners of model after rotated and scaled.
		Vector3f minCorner = new Vector3f();
		Vector3f maxCorner = new Vector3f();
		for (int mask = 0; mask < 8; ++mask) {
			Vector3f corner = new Vector3f(size.x, size.y, size.z).mul((mask & 1) == 1 ? -1 : 1, (mask & 2) == 2 ? -1 : 1, (mask & 4) == 4 ? -1 : 1).rotate(rotation);
			minCorner.min(corner);
			maxCorner.max(corner);
		}

		//Length of translation guide lines.
		float size = (maxCorner.sub(minCorner).get(maxCorner.maxComponent()) + 2) * .75f;

		//Render translation guide lines.
		LineBuffer buffer = new LineBuffer(matrix4f, matrix3f);
		buffer.begin();
		buffer.color(255, 0,0, alpha.x).beginLine(center.x - size, center.y, center.z).endLine(center.x + size, center.y, center.z);
		buffer.color(0,255,0, alpha.y).beginLine(center.x, center.y - size, center.z).endLine(center.x, center.y + size, center.z);
		buffer.color(0,0,255, alpha.z).beginLine(center.x, center.y, center.z - size).endLine(center.x, center.y, center.z + size);
		buffer.end();
	}

	/**
	 * Renders a preview of the Model as blocks.
	 * @param matrix4f The transformation matrix.
	 * @param camera The camera position.
	 * @param points A map of Points to Bytes representing which faces to render.
	 */
	private static void renderBlocksPreview(Matrix4f matrix4f, Vector3f camera, Map<Point, Byte> points) {
		//RenderSystem settings.
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.disableCull();
		RenderSystem.enableDepthTest();
		RenderSystem.enableBlend();
		RenderSystem.setShaderTexture(0, defaultTexture);

		//Getting and starting buffer
		BufferBuilder buffer = Tesselator.getInstance().getBuilder();
		buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

		//Calculating where to cull faces that are facing away from the camera.
		Vector3f cullNegative = new Vector3f(camera).sub(.4995f,.4995f,.4995f);
		Vector3f cullPositive = new Vector3f(cullNegative).add(.999f,.999f,.999f);

		//Two points for minimum and maximum corners of blocks.
		Vector3f p1 = new Vector3f();
		Vector3f p2 = new Vector3f();

		for (Map.Entry<Point, Byte> entry: points.entrySet()) {
			Point p = entry.getKey();
			byte faces = entry.getValue();

			//Setting the two corners.
			p1.set(p.x-.4995f, p.y-.4995f, p.z-.4995f).add(center);
			p2.set(p1).add(.999f,.999f,.999f);

			//Render each of the six faces if it is facing towards the camera, and if its corresponding bit is a 1.
			// - x
			if (p1.x > cullNegative.x && (faces & 32) == 32) {
				buffer.vertex(matrix4f, p1.x, p1.y, p1.z).uv(1,1).endVertex();
				buffer.vertex(matrix4f, p1.x, p1.y, p2.z).uv(1,0).endVertex();
				buffer.vertex(matrix4f, p1.x, p2.y, p2.z).uv(0,0).endVertex();
				buffer.vertex(matrix4f, p1.x, p2.y, p1.z).uv(0,1).endVertex();
			}
			// + x
			if (p2.x < cullPositive.x && (faces & 16) == 16) {
				buffer.vertex(matrix4f, p2.x, p1.y, p1.z).uv(0,0).endVertex();
				buffer.vertex(matrix4f, p2.x, p2.y, p1.z).uv(0,1).endVertex();
				buffer.vertex(matrix4f, p2.x, p2.y, p2.z).uv(1,1).endVertex();
				buffer.vertex(matrix4f, p2.x, p1.y, p2.z).uv(1,0).endVertex();
			}
			// - y
			if (p1.y > cullNegative.y && (faces & 8) == 8) {
				buffer.vertex(matrix4f, p1.x, p1.y, p1.z).uv(0,0).endVertex();
				buffer.vertex(matrix4f, p2.x, p1.y, p1.z).uv(0,1).endVertex();
				buffer.vertex(matrix4f, p2.x, p1.y, p2.z).uv(1,1).endVertex();
				buffer.vertex(matrix4f, p1.x, p1.y, p2.z).uv(1,0).endVertex();
			}
			// + y
			if (p2.y < cullPositive.y && (faces & 4) == 4) {
				buffer.vertex(matrix4f, p1.x, p2.y, p1.z).uv(1,1).endVertex();
				buffer.vertex(matrix4f, p1.x, p2.y, p2.z).uv(1,0).endVertex();
				buffer.vertex(matrix4f, p2.x, p2.y, p2.z).uv(0,0).endVertex();
				buffer.vertex(matrix4f, p2.x, p2.y, p1.z).uv(0,1).endVertex();
			}
			// - z
			if (p1.z > cullNegative.z && (faces & 2) == 2) {
				buffer.vertex(matrix4f, p1.x, p1.y, p1.z).uv(0,0).endVertex();
				buffer.vertex(matrix4f, p1.x, p2.y, p1.z).uv(0,1).endVertex();
				buffer.vertex(matrix4f, p2.x, p2.y, p1.z).uv(1,1).endVertex();
				buffer.vertex(matrix4f, p2.x, p1.y, p1.z).uv(1,0).endVertex();
			}
			// + z
			if (p2.z < cullPositive.z && (faces & 1) == 1) {
				buffer.vertex(matrix4f, p1.x, p1.y, p2.z).uv(1,1).endVertex();
				buffer.vertex(matrix4f, p2.x, p1.y, p2.z).uv(1,0).endVertex();
				buffer.vertex(matrix4f, p2.x, p2.y, p2.z).uv(0,0).endVertex();
				buffer.vertex(matrix4f, p1.x, p2.y, p2.z).uv(0,1).endVertex();
			}
		}
		Tesselator.getInstance().end();
	}


	/**
	 * Renders the outline of a box.
	 * @param matrix4f The transformation matrix.
	 * @param matrix3f The normal matrix.
	 * @param cornerOne The first corner of the box.
	 * @param cornerTwo The second corner of the box.
	 * @param color The color of the box.
	 */
	public static void renderLineBox(Matrix4f matrix4f, Matrix3f matrix3f, Vector3f cornerOne, Vector3f cornerTwo, Vector4i color) {
		//Get minimum and maximum corners of the box.
		Vector3f p1 = new Vector3f(cornerOne).min(cornerTwo);
		Vector3f p2 = new Vector3f(cornerTwo).max(cornerTwo);

		//Render three lines from four non-adjacent corners.
		LineBuffer buffer = new LineBuffer(matrix4f, matrix3f).color(color.x, color.y, color.z, color.w);
		buffer.begin();
		buffer.beginLine(p1.x, p1.y, p1.z).endLine(p2.x, p1.y, p1.z).endLine(p1.x, p2.y, p1.z).endLine(p1.x, p1.y, p2.z);
		buffer.beginLine(p2.x, p1.y, p2.z).endLine(p1.x, p1.y, p2.z).endLine(p2.x, p2.y, p2.z).endLine(p2.x, p1.y, p1.z);
		buffer.beginLine(p2.x, p2.y, p1.z).endLine(p1.x, p2.y, p1.z).endLine(p2.x, p1.y, p1.z).endLine(p2.x, p2.y, p2.z);
		buffer.beginLine(p1.x, p2.y, p2.z).endLine(p1.x, p2.y, p1.z).endLine(p1.x, p1.y, p2.z).endLine(p2.x, p2.y, p2.z);
		buffer.end();
	}

	/**
	 * Renders a filled box.
	 * @param matrix4f The transformation matrix.
	 * @param cornerOne The first corner of the box.
	 * @param cornerTwo The second corner of the box.
	 * @param color The color of the box.
	 */
	public static void renderBox(Matrix4f matrix4f, Vector3f cornerOne, Vector3f cornerTwo, Vector4i color) {
		//RenderSystem settings.
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		RenderSystem.enableCull();
		RenderSystem.enableDepthTest();
		RenderSystem.enableBlend();

		//Get minimum and maximum corners of the box.
		Vector3f p1 = new Vector3f(cornerOne).min(cornerTwo);
		Vector3f p2 = new Vector3f(cornerTwo).max(cornerTwo);

		//Render each side of the box.
		BufferBuilder buffer = Tesselator.getInstance().getBuilder();
		buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
		//-x
		buffer.vertex(matrix4f, p1.x, p1.y, p1.z).color(color.x, color.y, color.z, color.w).endVertex();
		buffer.vertex(matrix4f, p1.x, p1.y, p2.z).color(color.x, color.y, color.z, color.w).endVertex();
		buffer.vertex(matrix4f, p1.x, p2.y, p2.z).color(color.x, color.y, color.z, color.w).endVertex();
		buffer.vertex(matrix4f, p1.x, p2.y, p1.z).color(color.x, color.y, color.z, color.w).endVertex();

		//+x
		buffer.vertex(matrix4f, p2.x, p1.y, p1.z).color(color.x, color.y, color.z, color.w).endVertex();
		buffer.vertex(matrix4f, p2.x, p2.y, p1.z).color(color.x, color.y, color.z, color.w).endVertex();
		buffer.vertex(matrix4f, p2.x, p2.y, p2.z).color(color.x, color.y, color.z, color.w).endVertex();
		buffer.vertex(matrix4f, p2.x, p1.y, p2.z).color(color.x, color.y, color.z, color.w).endVertex();

		//-y
		buffer.vertex(matrix4f, p1.x, p1.y, p1.z).color(color.x, color.y, color.z, color.w).endVertex();
		buffer.vertex(matrix4f, p2.x, p1.y, p1.z).color(color.x, color.y, color.z, color.w).endVertex();
		buffer.vertex(matrix4f, p2.x, p1.y, p2.z).color(color.x, color.y, color.z, color.w).endVertex();
		buffer.vertex(matrix4f, p1.x, p1.y, p2.z).color(color.x, color.y, color.z, color.w).endVertex();

		//+y
		buffer.vertex(matrix4f, p1.x, p2.y, p1.z).color(color.x, color.y, color.z, color.w).endVertex();
		buffer.vertex(matrix4f, p1.x, p2.y, p2.z).color(color.x, color.y, color.z, color.w).endVertex();
		buffer.vertex(matrix4f, p2.x, p2.y, p2.z).color(color.x, color.y, color.z, color.w).endVertex();
		buffer.vertex(matrix4f, p2.x, p2.y, p1.z).color(color.x, color.y, color.z, color.w).endVertex();

		//-z
		buffer.vertex(matrix4f, p1.x, p1.y, p1.z).color(color.x, color.y, color.z, color.w).endVertex();
		buffer.vertex(matrix4f, p1.x, p2.y, p1.z).color(color.x, color.y, color.z, color.w).endVertex();
		buffer.vertex(matrix4f, p2.x, p2.y, p1.z).color(color.x, color.y, color.z, color.w).endVertex();
		buffer.vertex(matrix4f, p2.x, p1.y, p1.z).color(color.x, color.y, color.z, color.w).endVertex();

		//+z
		buffer.vertex(matrix4f, p1.x, p1.y, p2.z).color(color.x, color.y, color.z, color.w).endVertex();
		buffer.vertex(matrix4f, p2.x, p1.y, p2.z).color(color.x, color.y, color.z, color.w).endVertex();
		buffer.vertex(matrix4f, p2.x, p2.y, p2.z).color(color.x, color.y, color.z, color.w).endVertex();
		buffer.vertex(matrix4f, p1.x, p2.y, p2.z).color(color.x, color.y, color.z, color.w).endVertex();
		Tesselator.getInstance().end();
	}

	/**
	 * Renders a filled cube.
	 * @param matrix4f The transformation matrix.
	 * @param center The center of the cube.
	 * @param s The side length of the cube.
	 * @param color The color of the cube.
	 */
	public static void renderCube(Matrix4f matrix4f, Vector3f center, float s, Vector4i color) {
		renderBox(matrix4f, new Vector3f(center).sub(s/2,s/2,s/2), new Vector3f(center).add(s/2,s/2,s/2), color);
	}
}
