import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

public class Canvas extends JPanel {
    // Numbers of sector defaults at 12
    private int sectors = 12;
    // Tracks x and y of user mouse movement
    private int x;
    private int y;
    // Data storage for when eraser is used
    private Color penColorStorage;
    private Color penColor = Color.WHITE;
    private int penSize = 5;
    private boolean isEraser = false;
    private boolean isReflected = true;
    private boolean showLine = true;
    // The BufferedImage we will be painting on
    private BufferedImage canvas;
    // Initializes the Path2D object used for stroke path
    private Path2D stroke = new Path2D.Double();

    // Initializes stacks to store undo and redo strokes
    private Stack<MyStroke> history = new Stack<>();
    private Stack<MyStroke> redo = new Stack<>();

    /**
     * Constructor function for initializing a new Canvas
     * @param width Integer representing the width of the canvas
     * @param height  Integer representing the height of the canvas
     */
    Canvas(int width, int height) {
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.BLACK);
        setLayout(new FlowLayout());

        // Initializes canvas
        canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        /*
        BufferedImage cannot be directly added to a JPanel so
        here it is shoved into a JLabel wrapper
         */
        JLabel canvasWrapper = new JLabel(new ImageIcon(canvas));
        /*
        Sets up the listeners for drawing
         */
        canvasWrapper.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                mousePressedListener(e);
            }

            public void mouseReleased(MouseEvent e) {
                mouseReleasedListener(e);
            }
        });
        canvasWrapper.addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                mouseDragListener(e);
            }
        });
        add(canvasWrapper);
    }

    /**
     * This method handles updating the canvas for a single stroke
     * @param myStroke A MyStroke object is passed in containing all
     *                 the information needed to paint a single stroke.
     */
    private void updateCanvas(MyStroke myStroke) {
        // Grabs the canvas graphic
        Graphics2D g = (Graphics2D) canvas.getGraphics();
        // Set up rendering rules
        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        // Sets the paint color as the penColor from MyStroke
        g.setPaint(myStroke.getPenColor());
        /*
        Initializes rotatingAngles, used for rotating the dot
        across the sectors
         */
        double rotateAngle = 0;

        /*
        This for loop handles all the transformations and the
        eventual drawing of the stroke
         */
        for (int i = 0; i < this.sectors; i++) {
            // Sets the stroke width to the penSize
            g.setStroke(new BasicStroke(
                    (float) myStroke.getPenSize(),
                    BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND
            ));

            // Rotates everything across the sectors using rotateAngle
            AffineTransform at = AffineTransform.getRotateInstance(
                    Math.toRadians(rotateAngle),
                    this.getWidth()/2,
                    this.getHeight()/2
            );
            g.draw(at.createTransformedShape(myStroke.getStroke()));

            // If reflection is turn on, draws reflection
            if (isReflected) {
                at.translate(this.getWidth(), 0);
                at.scale(-1, 1);

                g.draw(at.createTransformedShape(myStroke.getStroke()));
            }

            // Finally, increment the rotateAngle for the next loop
            rotateAngle += 360.0 / this.sectors;
        }

        // Updates canvas to reflect changes at the end
        repaint();
    }

    /**
     * This method handles the eraser. Essentially eraser is just a
     * pen with the color black.
     */
    private void toggleEraser() {
        if (isEraser) {
            /*
            When eraser is turned on originally, we first store the
            currently used color inside penColorStorage so when eraser
            is turned off, we can revert back to the original color that
            was being used
             */
            this.penColorStorage = new Color(this.penColor.getRGB());
            this.penColor = Color.BLACK;
        } else {
            this.penColor = new Color(this.penColorStorage.getRGB());
        }

        // Updates the canvas to reflect changes
        repaint();
    }

    /**
     * This is a helper function that clears the canvas by covering it
     * with a transparent rectangle. Note that this will not cover up the
     * sector lines as that is drawn directly onto the Canvas JPanel instead
     * of the canvas BufferedImage
     */
    private void clear() {
        Graphics2D g = (Graphics2D) canvas.getGraphics();
        g.setBackground(new Color(255, 255, 255, 0));
        g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        repaint();
    }

    /**
     * Listener function for the clear button, calls clear() to mop up the
     * canvas and history.clear() to also clear the undo stack
     */
    void clearButtonListener() {
        history.clear();
        clear();
    }

    /**
     * Listener function for the undo button. Pops and pushes the top element
     * of the history stack to the redo stack and calls helper function
     * repaintOnChange() to repaint the remaining strokes from history if the
     * history stack isn't empty
     */
    void undoButtonListener() {
        if (!history.empty()) {
            redo.push(history.pop());
            repaintOnChange();
        }
    }

    /**
     * Listener function for the redo button. Simply paint the stroke at the top
     * of the redo stack if the stack isn't empty.
     */
    void redoButtonListener() {
        if (!redo.empty()) {
            MyStroke redonePath = redo.pop();
            history.push(redonePath);
            updateCanvas(redonePath);
        }
    }

    /**
     * Listener function for when a mouse first pressed down on the BufferedImage
     * canvas. It first clears the redo stack and then sets class variables x and y
     * to the latest position. It then initializes the initial point for the Path2D
     * object stroke. Lastly, it connects the initial point to itself so mouse pressed
     * and released would result in a dot (Path2D stroke only show up if there is at least
     * an initial point and a point it connects to.
     * @param e Event variable passed back when the listener is triggered,
     *          contains the x and y when the listener is triggered.
     */
    private void mousePressedListener(MouseEvent e) {
        redo.clear();

        x = e.getX();
        y = e.getY();
        stroke.moveTo(x, y);
        stroke.lineTo(x, y);
    }

    /**
     * Listener function for when the mouse is released, signalling the end of
     * the stroke. A new myStroke is created and pushed into the history stack
     * and the member variable stroke is reset waiting for the next stroke to
     * be initialized.
     * @param e Event variable passed back when the listener is triggered,
     *          contains the x and y when the listener is triggered.
     */
    private void mouseReleasedListener(MouseEvent e) {
        Path2D copy = (Path2D) stroke.clone();
        MyStroke myStroke = new MyStroke(
                copy,
                this.penColor,
                this.penSize,
                this.isReflected
        );
        history.push(myStroke);
        stroke.reset();
    }

    /**
     * Listener function for when the mouse is actively dragging and extending
     * the stroke. Each call to this listener function will trigger the class
     * variable x and y being set to the latest location, stroke connected to
     * that x, y pair, and a temporary stroke to be created and passed into the
     * updateCanvas method so the user can see the stroke they're drawing in
     * real time
     * @param e Event variable passed back when the listener is triggered,
     *          contains the x and y when the listener is triggered.
     */
    private void mouseDragListener(MouseEvent e) {
        x = e.getX();
        y = e.getY();

        stroke.lineTo(x, y);

        MyStroke tempStroke = new MyStroke(
                this.stroke,
                this.penColor,
                this.penSize,
                this.isReflected
        );

        updateCanvas(tempStroke);
    }

    /**
     * This method handles the drawing of sector lines directly onto the Canvas
     * JPanel. The logic is very similar to updateCanvas in how it is a single
     * component rotated across the canvas anchoring from the center.
     * @param g Graphics object that gives us access to the Graphics of the
     *          Canvas JPanel
     */
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Checks if the user wants sector lines to be shown
        if (this.showLine) {

            double rotateAngle = 0;

            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.WHITE);
            for (int i = 0; i < this.sectors; i++) {
                Line2D line = new Line2D.Double(
                        this.getWidth() / 2,
                        this.getHeight() / 2,
                        this.getWidth() / 2,
                        0
                );
                AffineTransform at = AffineTransform.getRotateInstance(
                        Math.toRadians(rotateAngle),
                        this.getWidth() / 2,
                        this.getHeight() / 2
                );

                g2d.draw(at.createTransformedShape(line));
                rotateAngle += 360.0 / this.sectors;
            }
        }
    }

    /**
     * This method clears the canvas, history stack and redo stack,
     * and resets sectors, penColor and penSize to the default values.
     */
    void reset() {
        clear();
        history.clear();
        redo.clear();
        this.sectors = 12;
        this.penColor = Color.WHITE;
        this.penSize = 5;
    }

    /**
     * Just a helper function that checks if the history stack is empty
     * or not. If it is, just call clear(); else, call clear() and then
     * paint everything inside the history stack onto the BufferedImage
     * canvas
     */
    private void repaintOnChange() {
        if (!history.empty()) {
            clear();

            for (MyStroke stroke : history) {
                updateCanvas(stroke);
            }
        } else {
            clear();
        }
    }

    // Setter function for sector numbers
    void setSectors(int num) {
        this.sectors = num;
        repaintOnChange();
    }

    // Setter function for pen color
    void setPenColor(Color color) {
        this.penColor = color;
    }

    // Setter function for isEraser
    void setIsEraser(boolean bool) {
        this.isEraser = bool;
        toggleEraser();
    }

    // Setter function for penSize
    void setPenSize(int val) {
        this.penSize = val;
    }

    // Setter function for isReflected
    void setReflected(boolean bool) {
        this.isReflected = bool;
        repaintOnChange();
    }

    // Setter function for showLine
    void setShowLine(boolean bool) {
        this.showLine = bool;
        repaint();
    }

    // Getter function for the history stack
    Stack<MyStroke> getHistory() {
        return this.history;
    }

    // Getter function for sector number
    int getSectors() {
        return this.sectors;
    }

    // Getter function for showLine
    boolean getShowLine() {
        return this.showLine;
    }
}
