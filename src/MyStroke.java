import java.awt.*;
import java.awt.geom.Path2D;

public class MyStroke {
    private Path2D stroke;
    private Color penColor;
    private int penSize;
    private boolean isReflected;

    /**
     * Constructor for MyStroke, which is a data storage class containing
     * all the info needed to reconstruct a single user stroke. Used in
     * undo, redo and GalleryPieces.
     * @param stroke A single Path2D object that represents the path of a
     *               single stroke
     * @param penColor A color object that represents the color of that stroke
     * @param penSize An integer representing the thickness of that stroke
     * @param isReflected A boolean representing whether the stroke should
     *                    be reflected
     */
    MyStroke(Path2D stroke, Color penColor, int penSize, boolean isReflected) {
        this.stroke = stroke;
        this.penColor = penColor;
        this.penSize = penSize;
        this.isReflected = isReflected;
    }

    // Getter function for stroke
    Path2D getStroke() {
        return this.stroke;
    }

    // Getter function for penColor
    Color getPenColor() {
        return this.penColor;
    }

    // Getter function for penSize
    int getPenSize() {
        return this.penSize;
    }

    // Getter function for isReflected
    boolean getIsReflected() {
        return this.isReflected;
    }
}
