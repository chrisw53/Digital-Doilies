import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

public class Canvas extends JPanel {
    private int sectors = 12;
    private int x;
    private int y;
    private Color penColorStorage;
    private Color penColor = Color.WHITE;
    private int penSize = 5;
    private boolean isEraser = false;
    private boolean isReflected = true;
    private boolean showLine = true;
    private BufferedImage canvas;
    private Path2D stroke = new Path2D.Double();
    private Stack<MyStroke> history = new Stack<>();
    private Stack<MyStroke> redo = new Stack<>();

    Canvas(int width, int height) {
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.BLACK);
        setLayout(new FlowLayout());

        canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        JLabel canvasWrapper = new JLabel(new ImageIcon(canvas));
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

    private void updateCanvas(MyStroke myStroke) {
        Graphics2D g = (Graphics2D) canvas.getGraphics();
        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        g.setPaint(myStroke.getPenColor());
        double rotateAngle = 0;

        for (int i = 0; i < this.sectors; i++) {
            g.setStroke(new BasicStroke(
                    (float) myStroke.getPenSize(),
                    BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND
            ));

            AffineTransform at = AffineTransform.getRotateInstance(
                    Math.toRadians(rotateAngle),
                    this.getWidth()/2,
                    this.getHeight()/2
            );
            g.draw(at.createTransformedShape(myStroke.getStroke()));

            if (isReflected) {
                at.translate(this.getWidth(), 0);
                at.scale(-1, 1);

                g.draw(at.createTransformedShape(myStroke.getStroke()));
            }

            rotateAngle += 360.0 / this.sectors;
        }

        repaint();
    }

    private void toggleEraser() {
        if (isEraser) {
            this.penColorStorage = new Color(this.penColor.getRGB());
            this.penColor = Color.BLACK;
        } else {
            this.penColor = new Color(this.penColorStorage.getRGB());
        }
        repaint();
    }

    private void clear() {
        Graphics2D g = (Graphics2D) canvas.getGraphics();
        g.setBackground(new Color(255, 255, 255, 0));
        g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        repaint();
    }

    void clearButtonListener() {
        history.clear();
        clear();
    }

    void undoButtonListener() {
        if (!history.empty()) {
            redo.push(history.pop());
            repaintOnChange();
        }
    }

    void redoButtonListener() {
        if (!redo.empty()) {
            MyStroke redonePath = redo.pop();
            history.push(redonePath);
            updateCanvas(redonePath);
        }
    }

    private void mousePressedListener(MouseEvent e) {
        redo.clear();

        x = e.getX();
        y = e.getY();
        stroke.moveTo(x, y);
        stroke.lineTo(x, y);
    }

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

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
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

    void reset() {
        clear();
        history.clear();
        redo.clear();
        this.sectors = 12;
        this.penColor = Color.WHITE;
        this.penSize = 5;
    }

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

    void setSectors(int num) {
        this.sectors = num;
        repaintOnChange();
    }

    void setPenColor(Color color) {
        this.penColor = color;
    }

    void setIsEraser(boolean bool) {
        this.isEraser = bool;
        toggleEraser();
    }

    void setPenSize(int val) {
        this.penSize = val;
    }

    void setReflected(boolean bool) {
        this.isReflected = bool;
        repaintOnChange();
    }

    void setShowLine(boolean bool) {
        this.showLine = bool;
        repaint();
    }

    Stack<MyStroke> getHistory() {
        return this.history;
    }

    int getSectors() {
        return this.sectors;
    }

    boolean getShowLine() {
        return this.showLine;
    }
}
