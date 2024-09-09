package com.knkevin.model_tools.models;

import com.knkevin.model_tools.models.utils.Point;
import com.knkevin.model_tools.models.utils.Triangle;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Represents a 3D Model created from a binary stl file.
 */
public class StlBinaryModel extends StlModel {
    /**
     * @param file A file to the stl file.
     * @throws IOException The file could not be opened.
     */
    public StlBinaryModel(File file) throws IOException {
        FileInputStream stream = new FileInputStream(file);
        stream.readNBytes(80);
        int num_triangles = stream.read() + stream.read() * 256 + stream.read() * 65536 + stream.read() * 16777216;
        for (int i = 0; i < num_triangles; ++i) {
            stream.readNBytes(12);
            float[] coordinates = new float[9];
            for (int j = 0; j < 9; ++j)
                coordinates[j] = ByteBuffer.wrap(stream.readNBytes(4)).order(ByteOrder.LITTLE_ENDIAN).getFloat();
            this.addTriangle(new Triangle(
                    new Point(coordinates[0], coordinates[2], coordinates[1]),
                    new Point(coordinates[3], coordinates[5], coordinates[4]),
                    new Point(coordinates[6], coordinates[8], coordinates[7])
            ));
            stream.readNBytes(2);
        }
        this.centerModel();
        this.setScale(0);
        this.updateBlockFaces();
    }
}
