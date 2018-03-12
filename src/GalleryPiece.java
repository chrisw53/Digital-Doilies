import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.*;

public class GalleryPiece extends JPanel{
    private ArrayList<MyStroke> strokeInstructions = new ArrayList<>();
    private BufferedImage canvas;
    private int sectors;
    private boolean showLine;
    private boolean isSelected = false;

    /**
     * Constructor for individual gallery pieces
     * @param history Stack containing all the strokes made on that painting
     * @param width Integer representing the width of the gallery piece
     * @param height Integer representing the height of the gallery piece
     * @param sectors Integer representing the number of sectors
     * @param showLine Boolean representing whether the sectors lines should
     *                 be drawn
     */
    GalleryPiece(
            Stack<MyStroke> history,
            int width,
            int height,
            int sectors,
            boolean showLine
    ) {
        this.showLine = showLine;
        setBackground(Color.BLACK);
        setLayout(new FlowLayout());
        // Initializes a BufferedImage for the strokes to be drawn on
        canvas = new BufferedImage(
                600,
                600,
                BufferedImage.TYPE_INT_ARGB
        );
        // Adds everything in the history stack into the class variable ArrayList
        strokeInstructions.addAll(history);
        this.sectors = sectors;

        // Placeholder updateCanvas call to draw blank canvas with sector lines
        updateCanvas(new MyStroke(
                new Path2D.Double(),
                new Color(255, 255, 255, 0),
                0,
                false
        ));

        // Draws all the strokes inside strokeInstruction
        for (MyStroke myStroke : strokeInstructions) {
            updateCanvas(myStroke);
        }

        /*
        Wraps the BufferedImage inside a JLabel with the intended
        width and height
        */
        JLabel canvasWrapper = new JLabel(new ImageIcon(
                canvas.getScaledInstance(
                    width,
                    height,
                    Image.SCALE_SMOOTH
                )
        ));

        add(canvasWrapper);

        // Set up the listeners for each image to be selected
        canvasWrapper.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                isSelected = !isSelected;
                if (isSelected) {
                    setBorder(BorderFactory.createLineBorder(Color.RED, 3));
                } else {
                    setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
                }
            }
        });
    }

    /**
     * Handles repainting the selected images onto a larger canvas
     * in a new dialog window
     */
    void individualPicture() {
        JDialog myWindow = new JDialog();
        myWindow.setTitle("Your Image");
        myWindow.setLayout(new BorderLayout());
        myWindow.setSize(new Dimension(600,620));
        Stack<MyStroke> temp = new Stack<>();
        temp.addAll(this.strokeInstructions);
        myWindow.add(new GalleryPiece(
                temp,
                600,
                600,
                this.sectors,
                this.showLine
        ), BorderLayout.CENTER);
        myWindow.setVisible(true);
    }


    /**
     * Handles drawing a single stroke and all the sector lines
     * if showLine is true
     * @param myStroke A MyStroke object that contains all the info
     *                 required to paint a stroke.
     */
    private void updateCanvas(MyStroke myStroke) {
        Graphics2D g = (Graphics2D) canvas.getGraphics();
        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        g.setPaint(myStroke.getPenColor());
        double rotateAngle = 0;

        for (int i = 0; i < this.sectors; i++) {
            // Initializes the sector line
            Line2D sectorLine = new Line2D.Double(
                    300,
                    300,
                    300,
                    0
            );

            AffineTransform at = AffineTransform.getRotateInstance(
                    Math.toRadians(rotateAngle),
                    300,
                    300
            );

            /*
            If showLine returns true, this logic rotates the Line2D
            initialized up top around the canvas
            */
            if (this.showLine) {
                // Sets the color to white and stroke width to 1.0
                g.setPaint(Color.WHITE);
                g.setStroke(new BasicStroke(1.0f));
                g.draw(at.createTransformedShape(sectorLine));
                // Resets the paint color
                g.setPaint(myStroke.getPenColor());
            }

            // Resets the stroke width
            g.setStroke(new BasicStroke(
                    (float) myStroke.getPenSize(),
                    BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND
            ));

            g.draw(at.createTransformedShape(myStroke.getStroke()));

            // If isReflected is true, this handles the logic to print the mirror
            if (myStroke.getIsReflected()) {
                at.translate(600, 0);
                at.scale(-1, 1);

                g.draw(at.createTransformedShape(myStroke.getStroke()));
            }


            rotateAngle += 360.0 / this.sectors;
        }

        repaint();
    }

    // Getter function for isSelected
    boolean getIsSelected() {
        return isSelected;
    }
}
