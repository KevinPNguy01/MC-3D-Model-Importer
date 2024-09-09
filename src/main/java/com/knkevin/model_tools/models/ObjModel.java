package com.knkevin.model_tools.models;

import com.knkevin.model_tools.models.utils.Palette;
import com.knkevin.model_tools.models.utils.Point;
import com.knkevin.model_tools.models.utils.Triangle;
import com.knkevin.model_tools.models.utils.VectorColors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;
import org.joml.Vector4i;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

/**
 * Represents a 3D Model created from an obj file.
 */
public class ObjModel extends Model {
    /**
     * The default material for missing or unassigned textures.
     */
    public static final String DEFAULT_MATERIAL = "stone";

    /**
     * The default color for missing or unassigned textures.
     */
    public static final int DEFAULT_COLOR = (255 << 24) | (128 << 16) | (128 << 8) | 128;

    /**
     * @param file A file to the texture to be opened.
     * @return A BufferedImage representing the texture if opened successfully, null otherwise.
     */
    private static BufferedImage openTexture(File file) {
        try {
            return ImageIO.read(file);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @param img A BufferedImage representing a texture.
     * @param x A number 0-1 representing the horizontal position in the texture.
     * @param y A number 0-1 representing the vertical position in the texture.
     * @return The color at the location in the texture as an integer, 0 if the texture is null.
     */
    private static int getColor(BufferedImage img, float x, float y) {
        if (img == null) return DEFAULT_COLOR;
        y = 1 - y;
        int tx = (int) (x * img.getWidth()) - 1;
        tx = (tx % img.getWidth() + img.getWidth()) % img.getWidth();
        int ty = (int) (y * img.getHeight()) - 1;
        ty = (ty % img.getHeight() + img.getHeight()) % img.getHeight();
        return img.getRGB(tx,ty);
    }

    /**
     * A Map that maps material names to a list of Faces that use that material.
     */
    private final HashMap<String, List<Face>> materialFaceMap = new HashMap<>();

    /**
     * A Map that maps material names to Files to texture images.
     */
    private final HashMap<String, File> materialFileMap = new HashMap<>();

    /**
     * A Map that maps material names to colors.
     */
    private final HashMap<String, Integer> materialColorMap = new HashMap<>();

    /**
     * A list of float arrays that represent xyz coordinates of a vertex.
     */
    private final List<float[]> positionVertices = new ArrayList<>();

    /**
     * A list of float arrays that represent uv coordinates of a vertex.
     */
    private final List<float[]> textureVertices = new ArrayList<>();

    /**
     * The name of the current material in the mtl or obj file.
     */
    private String currentMaterial;

    /**
     * @param file A file to the obj file.
     * @throws IOException The file could not be opened.
     */
    public ObjModel(File file) throws IOException {
        readMtl(file);
        currentMaterial = DEFAULT_MATERIAL;
        materialFaceMap.put(currentMaterial, new ArrayList<>());
        readObj(file);
        setScale(0);
        centerModel();
        updateBlockFaces();
    }

    /**
     * @see Model#getBlocks()
     */
    public Map<BlockPos, BlockState> getBlocks() {
        Map<BlockPos, BlockState> blocks = new HashMap<>();
        for (String material: materialFaceMap.keySet()) {
            BufferedImage texture = ObjModel.openTexture(materialFileMap.get(material));
            int color = materialColorMap.getOrDefault(material, DEFAULT_COLOR);
            for (Face face: materialFaceMap.get(material)) {
                for (Point p: face.getBlockPoints()) {
                    if (texture != null) color = ObjModel.getColor(texture, p.tx, p.ty);
                    BlockState blockState = Palette.getNearestBlock(color);
                    blocks.put(p.blockPos(),blockState);
                }
            }
        }
        return blocks;
    }

    /**
     * @see Model#centerModel()
     */
    protected void centerModel() {
        Vector3f center = maxCorner.sub(minCorner, new Vector3f()).div(2);
        for (float[] xyz: positionVertices) {
            xyz[0] -= minCorner.x + center.x;
            xyz[1] -= minCorner.y + center.y;
            xyz[2] -= minCorner.z + center.z;
        }
        center.mul(-1, minCorner);
        center.mul(1, maxCorner);
    }

    /**
     * @see Model#updateBlockFaces()
     */
    protected void updateBlockFaces() {
        blockFaces.clear();
        for (String material: materialFaceMap.keySet()) {
            for (Face face: materialFaceMap.get(material)) {
                for (Point p: face.getBlockPoints()) {
                    blockFaces.put(p, (byte) 63);
                }
            }
        }
        cullAdjacentFaces();
    }

    /**
     * Reads and parses the mtl file line by line.
     * @param file A file to the mtl file.
     * @throws IOException The file could not be opened or read.
     */
    private void readMtl(File file) throws IOException {
        String objName = file.getName();
        String mtlName = "models/" + objName.substring(0, objName.length() - 4) + ".mtl";
        FileReader fileReader;
        try {fileReader = new FileReader(mtlName);}
        catch (FileNotFoundException e) {return;}
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        while (bufferedReader.ready()) {
            readMtlLine(bufferedReader.readLine().strip().replaceAll(" +", " ").split(" ", 2));
        }
    }

    /**
     * Reads the first string in the array and calls the appropriate method to parse the array of strings.
     * @param line An array of strings representing a line from the mtl file.
     */
    private void readMtlLine(String[] line) {
        try {
            switch (line[0]) {
                case "newmtl" -> readNewMtl(line);
                case "map_Kd" -> readMapKd(line);
                case "Kd" -> readKd(line);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Parses the array of strings and sets currentMaterial to the material referenced by the command. Adds a new List to materialFaceMap for this material.
     * @param line An array of strings representing a newmtl command.
     */
    private void readNewMtl(String[] line) {
        currentMaterial = line[1];
        materialFaceMap.put(currentMaterial, new ArrayList<>());
    }

    /**
     * Parses the array of strings and adds a File to materialFileMap
     * @param line An array of strings representing a map_kd command.
     */
    private void readMapKd(String[] line) {
        File texturePath = new File("models/" + line[1]);
        if (texturePath.isFile())
            materialFileMap.put(currentMaterial, texturePath);
        texturePath = new File(line[1]);
        if (texturePath.isFile())
            materialFileMap.put(currentMaterial, texturePath);
    }

    /**
     * Parses the array of strings and adds
     * @param line An array of strings representing a Kd command.
     */
    private void readKd(String[] line) {
        String[] colors = line[1].split(" ");
        int red = (int) (Float.parseFloat(colors[0]) * 255), green = (int) (Float.parseFloat(colors[1]) * 255), blue = (int) (Float.parseFloat(colors[2]) * 255);
        int color = VectorColors.ARGBToInt(new Vector4i(255, red, green, blue));
        materialColorMap.put(currentMaterial, color);
    }

    /**
     * Reads and parses the obj file line by line.
     * @param file A file to the obj file.
     * @throws IOException The file could not be opened or read.
     */
    private void readObj(File file) throws IOException {
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        while (bufferedReader.ready()) {
            readObjLine(bufferedReader.readLine().strip().replaceAll(" +", " ").split(" "));
        }
    }

    /**
     * Reads the first string in the array and calls the appropriate method to parse the array of strings.
     * @param line An array of strings representing a line from the obj file.
     */
    private void readObjLine(String[] line) {
        try {
            switch (line[0]) {
                case "v" -> readVertex(line);
                case "vt" -> readTextureVertex(line);
                case "usemtl" -> readUseMaterial(line);
                case "f" -> readFace(line);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Parses the array of strings and adds the position vertex as a float array to this model's list of position vertices.
     * @param line An array of strings representing a vertex command.
     */
    private void readVertex(String[] line) {
        float x = Float.parseFloat(line[1]), y = Float.parseFloat(line[2]), z = Float.parseFloat(line[3]);
        positionVertices.add(new float[]{x, y, z});
        Vector3f temp = new Vector3f(x, y, z);
        minCorner.min(temp);
        maxCorner.max(temp);
    }

    /**
     * Parses the array of strings and adds the texture vertex as a float array to this model's list of texture vertices.
     * @param line An array of strings representing a texture vertex command.
     */
    private void readTextureVertex(String[] line) {
        textureVertices.add(new float[]{Float.parseFloat(line[1]), Float.parseFloat(line[2])});
    }

    /**
     * Parses the array of strings and sets the current material if the material was defined in the mtl file.
     * @param line An array of strings representing a usemtl command.
     */
    private void readUseMaterial(String[] line) {
        currentMaterial = DEFAULT_MATERIAL;
        if (line.length > 1 && materialFaceMap.containsKey(line[1]))
            currentMaterial = line[1];
    }

    /**
     * Parses the array of strings and adds the face to the corresponding list of Faces based on the current material.
     * @param line An array of strings representing a face command.
     */
    private void readFace(String[] line) {
        materialFaceMap.get(currentMaterial).add(new Face(line));
    }

    /**
     * Represents a face in an obj model.
     */
    private class Face {
        /**
         * An array of indices corresponding to vertices in the obj model.
         */
        private final int[] vertexIndices;

        /**
         * An array of indices corresponding to texture vertices in the obj model.
         */
        private final int[] textureIndices;

        /**
         * The number of vertices that make up the Face.
         */
        private final int numVertices;

        /**
         * True if there are indices to texture vertices, false otherwise.
         */
        private final boolean textured;

        /**
         * @param line An array of strings containing face data.
         */
        private Face(String[] line) {
            numVertices = line.length - 1;
            vertexIndices = new int[numVertices];
            textureIndices = new int[numVertices];
            for (int i = 1; i < line.length; ++i) {
                String[] numbers = line[i].split("/");
                int vertexIndex = Integer.parseInt(numbers[0]);
                vertexIndex = vertexIndex < 0 ? positionVertices.size() + vertexIndex : vertexIndex - 1;
                vertexIndices[i-1] = vertexIndex;
                if (numbers.length >= 2 && !numbers[1].isEmpty()) {
                    int textureIndex = Integer.parseInt(numbers[1]);
                    textureIndex = textureIndex < 0 ? textureVertices.size() + textureIndex : textureIndex - 1;
                    textureIndices[i-1] = textureIndex;
                }
            }
            textured = textureIndices[0] != 0;
        }

        /**
         * @return A list of Points representing the vertices that make up this Face.
         */
        private List<Point> getVertices() {
            List<Point> vertices = new ArrayList<>();
            for (int i = 0; i < numVertices; ++i) {
                float[] xyz = positionVertices.get(vertexIndices[i]);
                float[] uv = new float[2];
                if (textured)
                    uv = textureVertices.get(textureIndices[i]);
                vertices.add(new Point(xyz, uv).transformed(ObjModel.this.getTransformationMatrix()));
            }
            return vertices;
        }

        /**
         * @return A list of Triangles that make up this Face.
         */
        private List<Triangle> getTriangles() {
            List<Triangle> triangles = new ArrayList<>();
            List<Point> vertices = getVertices();
            Point p1 = vertices.get(0);
            for (int i = 1; i < numVertices-1; ++i) {
                Point p2 = vertices.get(i);
                Point p3 = vertices.get(i+1);
                triangles.add(new Triangle(p1, p2, p3));
            }
            return triangles;
        }

        /**
         * @return A set of points inside the polygon described by this Face using integer coordinates.
         */
        private Set<Point> getBlockPoints() {
            Set<Point> blockPoints = new HashSet<>();
            for (Triangle triangle: getTriangles())
                blockPoints.addAll(triangle.getBlockPoints());
            return blockPoints;
        }
    }
}
