import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class ControlPanel extends JPanel {
    ControlPanel() {
        Canvas myCanvas = new Canvas(600, 600);
        Gallery myGallery = new Gallery(this.getWidth());

        JButton clear = new JButton("Clear");
        clear.addActionListener(
                (ActionEvent e) -> myCanvas.clearButtonListener()
        );

        JButton chooseColor = new JButton("Pen Color");

        JButton undo = new JButton("Undo");
        undo.addActionListener(
                (ActionEvent e) -> myCanvas.undoButtonListener()
        );
        JButton redo = new JButton("Redo");
        redo.addActionListener(
                (ActionEvent e) -> myCanvas.redoButtonListener()
        );

        JButton save = new JButton("Save");
        save.addActionListener(
                (ActionEvent e) -> {
                    GalleryPiece newImg = new GalleryPiece(
                            myCanvas.getHistory(),
                            100,
                            100,
                            myCanvas.getSectors(),
                            myCanvas.getShowLine()
                    );

                    myGallery.addNewImage(newImg);
                    myCanvas.reset();
                    validate();
                    repaint();
                }
        );

        JToggleButton eraser = new JToggleButton("Eraser");
        eraser.addActionListener(
                (ActionEvent e) -> myCanvas.setIsEraser(eraser.isSelected())
        );

        JToggleButton showLine = new JToggleButton("Show Line");
        showLine.addActionListener(
                (ActionEvent e) -> myCanvas.setShowLine(showLine.isSelected())
        );

        JToggleButton reflection = new JToggleButton("Reflection");
        reflection.setSelected(true);
        reflection.addActionListener(
                (ActionEvent e) -> myCanvas.setReflected(reflection.isSelected())
        );

        showLine.setSelected(true);

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
        penSize.addChangeListener((ChangeEvent e) -> {
            penSizeLabel.setText("Pen Size: " + penSize.getValue() + " mm");
            myCanvas.setPenSize(penSize.getValue());
            myCanvas.repaint();
        });

        JSlider sectorNum = new JSlider(2, 100);
        sectorNum.setValue(12);
        sectorNum.setSnapToTicks(true);
        JLabel sectorNumLabel = new JLabel(
                "Number of Sectors: " + sectorNum.getValue(),
                SwingConstants.CENTER
        );
        sectorNum.addChangeListener((ChangeEvent e) -> {
            sectorNumLabel.setText("Number of Sectors: " + sectorNum.getValue());
            myCanvas.setSectors(sectorNum.getValue());
        });

        chooseColor.addActionListener((ActionEvent e) -> {
            JColorChooser pickColor = new JColorChooser();
            class OkListener implements ActionListener {
                public void actionPerformed(ActionEvent e) {
                    Color myColor = pickColor.getColor();
                    myCanvas.setPenColor(myColor);
                }
            }

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

        setLayout(new BorderLayout());

        JPanel leftControl = new JPanel();
        leftControl.setLayout(new GridLayout(6, 1));
        leftControl.add(clear);
        leftControl.add(undo);
        leftControl.add(redo);
        leftControl.add(eraser);
        leftControl.add(penSize);
        leftControl.add(penSizeLabel);
        add(leftControl, BorderLayout.WEST);

        JPanel rightControl = new JPanel();
        rightControl.setLayout(new GridLayout(6, 1));
        rightControl.add(chooseColor);
        rightControl.add(reflection);
        rightControl.add(showLine);
        rightControl.add(save);
        rightControl.add(sectorNum);
        rightControl.add(sectorNumLabel);
        add(rightControl, BorderLayout.EAST);

        JPanel canvasContainer = new JPanel();
        canvasContainer.setLayout(new FlowLayout());
        canvasContainer.add(myCanvas);
        add(canvasContainer, BorderLayout.CENTER);

        add(myGallery, BorderLayout.SOUTH);
    }
}
