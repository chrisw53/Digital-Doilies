import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class ControlPanel extends JPanel {
    ControlPanel() {
        /*
        Initializes canvas and gallery
         */
        Canvas myCanvas = new Canvas(600, 600);
        Gallery myGallery = new Gallery(this.getWidth());

        // Clear button listener set up
        JButton clear = new JButton("Clear");
        clear.setFont(new Font("Arial", Font.PLAIN, 18));
        clear.addActionListener(
                (ActionEvent e) -> myCanvas.clearButtonListener()
        );

        // Pen color JColorChooser set up
        JButton chooseColor = new JButton("Pen Color");
        chooseColor.setFont(new Font("Arial", Font.PLAIN, 18));
        chooseColor.addActionListener((ActionEvent e) -> {
            JColorChooser pickColor = new JColorChooser();
            // Class to handle color submission once ok is hit
            class OkListener implements ActionListener {
                public void actionPerformed(ActionEvent e) {
                    Color myColor = pickColor.getColor();
                    // Establishes the pen color as the color selected
                    myCanvas.setPenColor(myColor);
                }
            }

            // New dialog window for JColorChooser
            JDialog colorChooserWindow = JColorChooser.createDialog(
                    null,
                    "Choose a color",
                    false,
                    pickColor,
                    new OkListener(),
                    null
            );

            pickColor.setColor(Color.WHITE);
            colorChooserWindow.setVisible(true);
        });

        // Undo and redo button listener set up
        JButton undo = new JButton("Undo");
        undo.setFont(new Font("Arial", Font.PLAIN, 18));
        undo.addActionListener(
                (ActionEvent e) -> myCanvas.undoButtonListener()
        );
        JButton redo = new JButton("Redo");
        redo.setFont(new Font("Arial", Font.PLAIN, 18));
        redo.addActionListener(
                (ActionEvent e) -> myCanvas.redoButtonListener()
        );

        // Save button action listener set up
        JButton save = new JButton("Save");
        save.setFont(new Font("Arial", Font.PLAIN, 18));
        save.addActionListener(
                (ActionEvent e) -> {
                    // This ensures only up to 12 images can be saved
                    if (myGallery.getGallerySize() > 11) {
                        JOptionPane.showMessageDialog(
                                new JFrame(),
                                "Max size (12) reached! Please remove one.",
                                "Save Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                    } else {
                        GalleryPiece newImg = new GalleryPiece(
                                myCanvas.getHistory(),
                                100,
                                100,
                                myCanvas.getSectors(),
                                myCanvas.getShowLine()
                        );

                        // Adds the new image to Gallery class
                        myGallery.addNewImage(newImg);
                        // Resets the canvas to empty
                        myCanvas.reset();

                        // Reloads control panel to reflect the changes
                        validate();
                        repaint();
                    }
                }
        );

        // Eraser button listener set up
        JToggleButton eraser = new JToggleButton("Eraser");
        eraser.setFont(new Font("Arial", Font.PLAIN, 18));
        eraser.addActionListener(
                (ActionEvent e) -> myCanvas.setIsEraser(eraser.isSelected())
        );

        // Show Line button listener set up
        JToggleButton showLine = new JToggleButton("Show Line");
        showLine.setFont(new Font("Arial", Font.PLAIN, 18));
        showLine.addActionListener(
                (ActionEvent e) -> myCanvas.setShowLine(showLine.isSelected())
        );
        // Defaults show canvas line option to ne selected
        showLine.setSelected(true);

        // Reflection button listener set up
        JToggleButton reflection = new JToggleButton("Reflection");
        reflection.setFont(new Font("Arial", Font.PLAIN, 18));
        reflection.addActionListener(
                (ActionEvent e) -> myCanvas.setReflected(reflection.isSelected())
        );
        // Defaults reflection option to be selected
        reflection.setSelected(true);

        // Pen size slider and label set up
        JSlider penSize = new JSlider(1, 10);
        penSize.setValue(5);
        penSize.setMajorTickSpacing(3);
        penSize.setMinorTickSpacing(1);
        penSize.setPaintTicks(true);
        penSize.setPaintLabels(true);
        penSize.setSnapToTicks(true);
        JLabel penSizeLabel = new JLabel(
                "Pen Size: " + penSize.getValue() + " mm",
                SwingConstants.CENTER
        );
        penSizeLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        penSize.addChangeListener((ChangeEvent e) -> {
            penSizeLabel.setText("Pen Size: " + penSize.getValue() + " mm");
            myCanvas.setPenSize(penSize.getValue());
            myCanvas.repaint();
        });

        // Canvas sector lines slider and label set up
        JSlider sectorNum = new JSlider(2, 100);
        sectorNum.setValue(12);
        sectorNum.setSnapToTicks(true);
        JLabel sectorNumLabel = new JLabel(
                "Number of Sectors: " + sectorNum.getValue(),
                SwingConstants.CENTER
        );
        sectorNumLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        sectorNum.addChangeListener((ChangeEvent e) -> {
            sectorNumLabel.setText("Number of Sectors: " + sectorNum.getValue());
            myCanvas.setSectors(sectorNum.getValue());
        });

        // BorderLayout for the overall panel
        setLayout(new BorderLayout());

        // Wrapper panel for the control components on the left of the canvas
        JPanel leftControl = new JPanel();
        leftControl.setLayout(new GridLayout(6, 1));
        leftControl.add(clear);
        leftControl.add(undo);
        leftControl.add(redo);
        leftControl.add(eraser);
        leftControl.add(penSizeLabel);
        leftControl.add(penSize);
        add(leftControl, BorderLayout.WEST);

        // Wrapper panel for the control components on the right of the canvas
        JPanel rightControl = new JPanel();
        rightControl.setLayout(new GridLayout(6, 1));
        rightControl.add(chooseColor);
        rightControl.add(reflection);
        rightControl.add(showLine);
        rightControl.add(save);
        rightControl.add(sectorNumLabel);
        rightControl.add(sectorNum);
        add(rightControl, BorderLayout.EAST);

        // Wrapper panel for the drawing canvas
        JPanel canvasContainer = new JPanel();
        canvasContainer.setLayout(new FlowLayout());
        canvasContainer.add(myCanvas);
        add(canvasContainer, BorderLayout.CENTER);

        // Adds the gallery component to the panel
        add(myGallery, BorderLayout.SOUTH);
    }
}
