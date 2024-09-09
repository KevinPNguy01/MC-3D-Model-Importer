package com.knkevin.model_tools.models;

import com.knkevin.model_tools.models.utils.Point;
import com.knkevin.model_tools.models.utils.Triangle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Represents a 3D Model created from an ascii stl file.
 */
public class StlAsciiModel extends StlModel {
    /**
     * @param file A File to the stl file.
     * @throws IOException The file could not be opened.
     */
    public StlAsciiModel(File file) throws IOException {
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        while (bufferedReader.ready()) {
            String[] line = bufferedReader.readLine().strip().replaceAll(" +", " ").split(" ");
            if (line[0].equals("facet")) {
                bufferedReader.readLine();
                String[] p1 = bufferedReader.readLine().strip().replaceAll(" +", " ").split(" ");
                String[] p2 = bufferedReader.readLine().strip().replaceAll(" +", " ").split(" ");
                String[] p3 = bufferedReader.readLine().strip().replaceAll(" +", " ").split(" ");
                this.addTriangle(new Triangle(
                        new Point(Float.parseFloat(p1[1]), Float.parseFloat(p1[3]), Float.parseFloat(p1[2])),
                        new Point(Float.parseFloat(p2[1]), Float.parseFloat(p2[3]), Float.parseFloat(p2[2])),
                        new Point(Float.parseFloat(p3[1]), Float.parseFloat(p3[3]), Float.parseFloat(p3[2]))
                ));
            }
        }
        this.centerModel();
        this.setScale(0);
        this.updateBlockFaces();
    }
}
