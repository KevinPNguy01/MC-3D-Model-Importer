package com.knkevin.model_tools.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

/**
 * A utility class used for rendering lines.
 */
public class LineBuffer {
    /**
     * Buffer used for adding vertices.
     */
    private final BufferBuilder buffer = Tesselator.getInstance().getBuilder();

    /**
     * Transformation matrix.
     */
    private final Matrix4f matrix4f;

    /**
     * Normal matrix.
     */
    private final Matrix3f matrix3f;

    /**
     * Coordinates for the start point of a line.
     */
    private float x1, y1, z1;

    /**
     * Default color values.
     */
    private int red = 255, green= 255, blue = 255, alpha = 255;

    public LineBuffer(Matrix4f m4, Matrix3f m3) {
        //RenderSystem Settings
        RenderSystem.setShader(GameRenderer::getRendertypeLinesShader);
        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();

        matrix4f = m4;
        matrix3f = m3;
    }

    /**
     * Begin the buffer to allow rendering.
     */
    public void begin() {
        buffer.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL);
    }

    /**
     * End the buffer and render.
     */
    public void end() {
        Tesselator.getInstance().end();
    }

    /**
     * Sets the color of this LineBuffer.
     * @param r Red.
     * @param g Green.
     * @param b Blue.
     * @param a Alpha.
     * @return This LineBuffer.
     */
    public LineBuffer color(int r, int g, int b, int a) {
        red = r;
        green = g;
        blue = b;
        alpha = a;
        return this;
    }

    /**
     * Sets the start point of the line to draw.
     * @param x X-coordinate.
     * @param y Y-coordinate.
     * @param z Z-coordinate.
     * @return This LineBuffer.
     */
    public LineBuffer beginLine(float x, float y, float z) {
        x1 = x;
        y1 = y;
        z1 = z;
        return this;
    }

    /**
     * Creates a line from the start point to this end point and writes it to the buffer.
     * @param x X-coordinate.
     * @param y Y-coordinate.
     * @param z Z-coordinate.
     * @return This LineBuffer.
     */
    public LineBuffer endLine(float x, float y, float z) {
        if (!buffer.building()) return this;
        float dx = x-x1, dy = y-y1, dz = z-z1;
        float distance = (float) Math.sqrt(dx*dx + dy*dy + dz*dz);
        buffer.vertex(matrix4f, x1, y1, z1).color(red, green, blue, alpha).normal(matrix3f,dx/distance,dy/distance,dz/distance).endVertex();
        buffer.vertex(matrix4f, x, y, z).color(red, green, blue, alpha).normal(matrix3f,dx/distance,dy/distance,dz/distance).endVertex();
        return this;
    }
}
