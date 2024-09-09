package com.knkevin.model_tools.models;

import com.knkevin.model_tools.items.HammerModes;
import com.knkevin.model_tools.models.utils.Point;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Represents a 3D model.
 */
public abstract class Model {
    /**
     * This Model's rotation represented as a quaternion.
     */
    public final Quaternionf rotation = new Quaternionf();

    /**
     * This Model's scale represented as a vector.
     */
    public final Vector3f scale = new Vector3f(1,1,1);

    /**
     * This Model's position in the world represented as a vector.
     */
    public final Vector3f position = new Vector3f();

    /**
     * The minimum and maximum corners of this Model's bounding box.
     */
    public final Vector3f minCorner = new Vector3f(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE), maxCorner = new Vector3f(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);

    /**
     * To be used for rendering the preview of this model.
     * Points representing block coordinates are mapped to bytes.
     * The bits of each byte determine whether the face is to be rendered.
     */
    public final ConcurrentMap<Point, Byte> blockFaces = new ConcurrentHashMap<>();

    /**
     * Stores block coordinates mapped to BlockStates, representing the blocks before the model was placed.
     */
    private final Map<BlockPos, BlockState> undo = new HashMap<>();

    /**
     * @return A 4x4 transformation from this Model's rotation and scale.
     */
    public Matrix4f getTransformationMatrix() {
        return new Matrix4f().rotate(rotation).scale(scale);
    }

    /**
     * @return A map of block positions mapped to block states representing this 3d model as blocks.
     */
    public abstract Map<BlockPos, BlockState> getBlocks();

    /**
     * Centers this Model.
     */
    protected abstract void centerModel();

    /**
     * Recalculates the blocks and faces to be rendered by this Model's preview.
     */
    protected abstract void updateBlockFaces();

    /**
     * Converts the model into Minecraft by representing it as blocks.
     * @param level The world to place the model in.
     * @return The number of blocks placed.
     */
    public int placeBlocks(Level level) {
        this.undo.clear();
        int count = 0;
        for (Map.Entry<BlockPos, BlockState> entry: this.getBlocks().entrySet()) {
            BlockPos blockPos = entry.getKey().offset((int)Math.floor(position.x), (int)Math.floor(position.y), (int)Math.floor(position.z));
            BlockState blockState = entry.getValue();
            this.undo.put(blockPos, level.getBlockState(blockPos));
            level.setBlockAndUpdate(blockPos, blockState);
            ++count;
        }
        return count;
    }

    /**
     * Undoes the previous placement of blocks.
     * @param level The world to undo the placement.
     */
    public void undo(Level level) {
        for (Map.Entry<BlockPos, BlockState> entry: this.undo.entrySet()) {
            BlockPos blockPos = entry.getKey();
            BlockState blockState = entry.getValue();
            level.setBlockAndUpdate(blockPos, blockState);
        }
    }

    /**
     * @param x_axis The angle in degrees to rotate around the x-axis.
     * @param y_axis The angle in degrees to rotate around the y-axis.
     * @param z_axis The angle in degrees to rotate around the z-axis.
     */
    public void applyRotation(float x_axis, float y_axis, float z_axis) {
        float x_angle = Math.toRadians(x_axis), y_angle = Math.toRadians(y_axis), z_angle = Math.toRadians(z_axis);
        rotation.rotateXYZ(-x_angle, -y_angle, -z_angle);
        if (HammerModes.viewMode == HammerModes.ViewMode.BLOCKS) this.updateBlockFaces();
    }

    /**
     * @param axis The axis to rotate around.
     * @param angle The angle in degrees to rotate.
     */
    public void applyAxisRotation(String axis, float angle) {
        switch (axis) {
            case "x" -> applyRotation(angle, 0, 0);
            case "y" -> applyRotation(0, angle, 0);
            case "z" -> applyRotation(0, 0, angle);
        }
    }

    /**
     * @param xScale The scale to set the x-component to.
     * @param yScale The scale to set the y-component to.
     * @param zScale The scale to set the z-component to.
     */
    public void setScale(float xScale, float yScale, float zScale) {
        this.scale.set(xScale, yScale, zScale);
        this.scale.max(new Vector3f(0,0,0));
        if (HammerModes.viewMode == HammerModes.ViewMode.BLOCKS) this.updateBlockFaces();
    }

    /**
     * @param scale The scale to set the Model to.
     */
    public void setScale(float scale) {
        this.setScale(scale, scale, scale);
    }

    /**
     * @param axis The axis to set the scale of.
     * @param scale The scalar.
     */
    public void setAxisScale(String axis, float scale) {
        switch (axis) {
            case "x" -> this.setScale(scale, this.scale.y, this.scale.z);
            case "y" -> this.setScale(this.scale.x, scale, this.scale.z);
            case "z" -> this.setScale(this.scale.x, this.scale.y, scale);
        }
    }

    /**
     * @param xScale The amount to scale the x-component.
     * @param yScale The amount to scale the x-component.
     * @param zScale The amount to scale the x-component.
     */
    public void applyScale(float xScale, float yScale, float zScale) {
        this.setScale(this.scale.x * xScale, this.scale.y * yScale, this.scale.z * zScale);
    }

    /**
     * @param scale The scalar.
     */
    public void applyScale(float scale) {
        this.setScale(this.scale.x * scale, this.scale.y * scale, this.scale.z * scale);
    }

    /**
     * @param axis The axis to scale.
     * @param scale The scalar.
     */
    public void applyAxisScale(String axis, float scale) {
        switch (axis) {
            case "x" -> this.applyScale(scale,1,1);
            case "y" -> this.applyScale(1, scale,1);
            case "z" -> this.applyScale(1,1, scale);
        }
    }

    /**
     * @param direction The direction to move the model in.
     * @param distance The distance to move the model.
     */
    public void move(Direction direction, float distance) {
        position.add(direction.step().mul(distance));
    }

    /**
     * @param axis The axis to move the model.
     * @param distance The distance to move the model.
     */
    public void move(String axis, float distance) {
        switch (axis) {
            case "x" -> position.add(distance,0,0);
            case "y" -> position.add(0,distance,0);
            case "z" -> position.add(0,0,distance);
        }
    }

    /**
     * Iterates through blockFaces and sets the appropriate bits to 1 if the face is to be rendered, or 0 otherwise.
     */
    protected void cullAdjacentFaces() {
        Point adjacent = new Point(0,0,0);
        for (Map.Entry<Point, Byte> entry: blockFaces.entrySet()) {
            Point p = entry.getKey();
            byte value = entry.getValue();
            adjacent.x = p.x - 1;
            adjacent.y = p.y;
            adjacent.z = p.z;

            if (blockFaces.containsKey(adjacent)) value -= 32;
            adjacent.x = p.x + 1;
            if (blockFaces.containsKey(adjacent)) value -= 16;
            adjacent.x = p.x;

            adjacent.y = p.y - 1;
            if (blockFaces.containsKey(adjacent)) value -= 8;
            adjacent.y = p.y + 1;
            if (blockFaces.containsKey(adjacent)) value -= 4;
            adjacent.y = p.y;

            adjacent.z = p.z - 1;
            if (blockFaces.containsKey(adjacent)) value -= 2;
            adjacent.z = p.z + 1;
            if (blockFaces.containsKey(adjacent)) value -= 1;
            entry.setValue(value);
        }
    }
}
