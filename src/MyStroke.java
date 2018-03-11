import java.awt.*;
import java.awt.geom.Path2D;

public class MyStroke {
    private Path2D stroke;
    private Color penColor;
    private int penSize;
    private boolean isReflected;

    MyStroke(Path2D stroke, Color penColor, int penSize, boolean isReflected) {
        this.stroke = stroke;
        this.penColor = penColor;
        this.penSize = penSize;
        this.isReflected = isReflected;
    }

    Path2D getStroke() {
        return this.stroke;
    }

    Color getPenColor() {
        return this.penColor;
    }

    int getPenSize() {
        return this.penSize;
    }

    boolean getIsReflected() {
        return this.isReflected;
    }
}
