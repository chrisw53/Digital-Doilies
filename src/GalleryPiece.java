import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.*;

public class GalleryPiece extends JPanel{
    private ArrayList<MyStroke> strokeInstructions = new ArrayList<>();
    private BufferedImage canvas;
    private int sectors;
    private boolean showLine;
    private boolean isSelected = false;

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
        canvas = new BufferedImage(
                600,
                600,
                BufferedImage.TYPE_INT_ARGB
        );
        strokeInstructions.addAll(history);
        this.sectors = sectors;

        for (MyStroke myStroke : strokeInstructions) {
            updateCanvas(myStroke);
        }

        JLabel canvasWrapper = new JLabel(new ImageIcon(
                canvas.getScaledInstance(
                    width,
                    height,
                    Image.SCALE_SMOOTH
                )
        ));

        add(canvasWrapper);

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

    private void updateCanvas(MyStroke myStroke) {
        Graphics2D g = (Graphics2D) canvas.getGraphics();
        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        g.setPaint(myStroke.getPenColor());
        double rotateAngle = 0;

        for (int i = 0; i < this.sectors; i++) {
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

            if (this.showLine) {
                g.setPaint(Color.WHITE);
                g.setStroke(new BasicStroke(1.0f));
                g.draw(at.createTransformedShape(sectorLine));
                g.setPaint(myStroke.getPenColor());
            }

            g.setStroke(new BasicStroke(
                    (float) myStroke.getPenSize(),
                    BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND
            ));

            g.draw(at.createTransformedShape(myStroke.getStroke()));

            if (myStroke.getIsReflected()) {
                at.translate(600, 0);
                at.scale(-1, 1);

                g.draw(at.createTransformedShape(myStroke.getStroke()));
            }


            rotateAngle += 360.0 / this.sectors;
        }

        repaint();
    }

    boolean getIsSelected() {
        return isSelected;
    }
}
