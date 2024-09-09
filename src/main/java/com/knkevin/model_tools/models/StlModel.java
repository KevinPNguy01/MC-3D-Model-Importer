package com.knkevin.model_tools.models;

import com.knkevin.model_tools.models.utils.Point;
import com.knkevin.model_tools.models.utils.Triangle;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a 3D model created from an stl file.
 */
public class StlModel extends Model {
    /**
     * A list of triangles that make up the model.
     */
    protected List<Triangle> triangles = new ArrayList<>();

    /**
     * The default block that makes up this model.
     */
    private final BlockState block = Blocks.STONE.defaultBlockState();

    /**
     * @param triangle Adds the triangle to the list of triangles, updating the size of the model.
     */
    protected void addTriangle(Triangle triangle) {
        for (Point p: triangle.getVertices()) {
            Vector3f temp = new Vector3f(p.x, p.y, p.z);
            minCorner.min(temp);
            maxCorner.max(temp);
        }
        this.triangles.add(triangle);
    }

    /**
     * @see Model#centerModel()
     */
    protected void centerModel() {
        Vector3f center = maxCorner.sub(minCorner, new Vector3f()).div(2);
        for (Triangle triangle: this.triangles)
            for (Point p: triangle.getVertices()) {
                p.x -= minCorner.x + center.x;
                p.y -= minCorner.y + center.y;
                p.z -= minCorner.z + center.z;
            }
        center.mul(-1, minCorner);
        center.mul(1, maxCorner);
    }

    /**
     * @see Model#getBlocks()
     */
    public Map<BlockPos, BlockState> getBlocks() {
        Map<BlockPos, BlockState> blocks = new HashMap<>();
        for (Triangle triangle: this.triangles)
            for (Point p: triangle.transformed(this.getTransformationMatrix()).getBlockPoints())
                blocks.put(p.blockPos(), this.block);
        return blocks;
    }

    /**
     * @see Model#updateBlockFaces()
     */
    public void updateBlockFaces() {
        blockFaces.clear();
        for (Triangle triangle: this.triangles) {
            for (Point p : triangle.transformed(this.getTransformationMatrix()).getBlockPoints()) {
                blockFaces.put(p, (byte) 63);
            }
        }
        this.cullAdjacentFaces();
    }
}