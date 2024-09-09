package com.knkevin.model_tools.models.utils;

import net.minecraft.core.BlockPos;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

/**
 * An object representing a point in space.
 * Can contain texture coordinate data.
 */
public class Point {
    /**
     * Position and texture coordinates of this Point.
     */
    public float x, y, z, tx, ty;

    /**
     * @param x X-coordinate of point.
     * @param y Y-coordinate of point.
     * @param z Z-coordinate of point.
     * @param tx X-coordinate of texture.
     * @param ty Y-coordinate of texture.
     */
    public Point(float x, float y, float z, float tx, float ty) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.tx = tx;
        this.ty = ty;
    }

    /**
     * @see #Point(float, float, float, float, float)
     */
    public Point(float x, float y, float z) {
        this(x, y, z, 0, 0);
    }

    /**
     * @see #Point(float, float, float, float, float)
     */
    public Point(float[] xyz, float[] uv) {
        this(xyz[0], xyz[1], xyz[2], uv[0], uv[1]);
    }

    /**
     * @param matrix A 4x4 transformation matrix.
     * @return A copy of this Point after being transformed by the transformation matrix.
     */
    public Point transformed(Matrix4f matrix) {
        float x = this.x, y = this.y, z = this.z;
        Point transformedPoint = new Point(x, y, z, this.tx, this.ty);
        transformedPoint.x = Math.fma(matrix.m00(), x, Math.fma(matrix.m10(), y, Math.fma(matrix.m20(), z, matrix.m30())));
        transformedPoint.y = Math.fma(matrix.m01(), x, Math.fma(matrix.m11(), y, Math.fma(matrix.m21(), z, matrix.m31())));
        transformedPoint.z = Math.fma(matrix.m02(), x, Math.fma(matrix.m12(), y, Math.fma(matrix.m22(), z, matrix.m32())));
        return transformedPoint;
    }

    /**
     * @return A new Point with integer position coordinates.
     */
    public Point blockPoint() {
        return new Point((int) this.x, (int) this.y, (int) this.z, this.tx, this.ty);
    }

    /**
     * @return A new BlockPos with this Point's coordinates as integers.
     */
    public BlockPos blockPos() {
        return new BlockPos((int) (this.x), (int) (this.y), (int) (this.z));
    }

    /**
     * @param p Endpoint of the line.
     * @return A list of Points between this Point and Point p.
     */
    protected List<Point> line(Point p) {
        float precision = 1.5f;
        float deltaX = p.x - this.x, deltaY = p.y - this.y, deltaZ = p.z - this.z, deltaTx = p.tx - this.tx, deltaTy = p.ty - this.ty;
        float distance = (float) Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2) + Math.pow(deltaZ, 2));
        float dx = deltaX / distance / precision, dy = deltaY / distance / precision, dz = deltaZ / distance / precision, dtx = deltaTx / distance / precision, dty = deltaTy / distance / precision;
        float x = this.x, y = this.y, z = this.z, tx = this.tx, ty = this.ty;
        List<Point> points = new ArrayList<>();
        for (float i = 0; i < distance; i+= 1 / precision) {
            points.add(new Point(x,y,z,tx,ty));
            x += dx;
            y += dy;
            z += dz;
            tx += dtx;
            ty += dty;
        }
        points.add(new Point(p.x, p.y, p.z, p.tx, p.ty));
        return points;
    }

    /**
     * @param obj Object being compared.
     * @return True if obj is a Point and its positional coordinates are equal, and false otherwise.
     */
    @Override
    public boolean equals(Object obj){
        if (this == obj) return true;
        if (!(obj instanceof Point other)) return false;
        return this.x == other.x && this.y == other.y && this.z == other.z;
    }

    /**
     * @return A hashcode from this Point's positional coordinates.
     */
    @Override
    public int hashCode(){
        int hash = 17;
        hash = hash * 31 + Float.hashCode(this.x);
        hash = hash * 31 + Float.hashCode(this.y);
        hash = hash * 31 + Float.hashCode(this.z);
        return hash;
    }
}
