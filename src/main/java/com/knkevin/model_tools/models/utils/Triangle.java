package com.knkevin.model_tools.models.utils;

import org.joml.Matrix4f;

import java.util.HashSet;
import java.util.Set;

/**
 * An object representing a triangle in space.
 * Contains three Points.
 */
public class Triangle {
    /**
     * The three Points that make up this Triangle.
     */
    public Point v1, v2, v3;

    /**
     * @param v1 First vertex.
     * @param v2 Second vertex.
     * @param v3 Third vertex.
     */
    public Triangle(Point v1, Point v2, Point v3) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
    }

    /**
     * @param matrix A 4x4 transformation matrix.
     * @return A copy of this Triangle after being transformed by the transformation matrix.
     */
    public Triangle transformed(Matrix4f matrix) {
        return new Triangle(this.v1.transformed(matrix), this.v2.transformed(matrix), this.v3.transformed(matrix));
    }

    /**
     * @return A list of vertices of this Triangle.
     */
    public Point[] getVertices() {
        return new Point[]{this.v1, this.v2, this.v3};
    }

    /**
     * @return A set of Points between the three vertices of this Triangle using integer coordinates.
     */
    public Set<Point> getBlockPoints() {
        Set<Point> points = new HashSet<>();
        for (Point v4: v1.line(v2))
            for (Point p: v4.line(v3))
                points.add(p.blockPoint());
        return points;
    }
}
