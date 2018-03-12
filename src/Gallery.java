import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class Gallery extends JPanel {
    // Class variable in charge of all the saved images
    private ArrayList<GalleryPiece> myImages = new ArrayList<>();
    // Wrapper JPanel for gallery
    private JPanel galleryContainer = new JPanel();

    /**
     * Constructor function that initializes a Gallery
     * @param width An integer representing the width of the Gallery
     */
    Gallery(int width) {
        setPreferredSize(new Dimension(width, 150));
        setLayout(new BorderLayout());

        galleryContainer.setLayout(new FlowLayout());
        // Scrollbar set up
        JScrollPane scroll = new JScrollPane(galleryContainer);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

        // Toolbar wrapper
        JPanel galleryToolBar = new JPanel();
        galleryToolBar.setLayout(new GridLayout(1, 2));

        // Show Image button listener set up
        JButton showImage = new JButton("Show Image");
        showImage.addActionListener(
                (ActionEvent e) -> {
                    /*
                    For any image selected, call individualPicture() for
                    an enlarged version
                    */
                    for (GalleryPiece img : myImages) {
                        if (img.getIsSelected()) {
                            img.individualPicture();
                        }
                    }
                }
        );

        // Removal button listener set up
        JButton remove = new JButton("Remove");
        remove.addActionListener(
                (ActionEvent e) -> {
                    removeImage(myImages);
                    revalidate();
                    repaint();
                }
        );

        galleryToolBar.add(showImage);
        galleryToolBar.add(remove);

        add(scroll, BorderLayout.CENTER);
        add(galleryToolBar, BorderLayout.SOUTH);
    }

    /**
     * Takes new image and add it both to the gallery and the
     * ArrayList keeping track of all the images
     * @param img A GalleryPiece instance
     */
    void addNewImage(GalleryPiece img) {
        galleryContainer.add(img);
        myImages.add(img);
    }

    /**
     * Method that loops through the ArrayList containing all
     * the saved GalleryPieces and removes the ones selected
     * @param imageList
     */
    private void removeImage(ArrayList<GalleryPiece> imageList) {
        /*
        This is done this way to avoid the ConcurrentModificationException
         */
        ArrayList<GalleryPiece> toRemove = new ArrayList<>();

        for (GalleryPiece img : imageList) {
            if (img.getIsSelected()) {
                toRemove.add(img);
                galleryContainer.remove(img);
            }
        }

        myImages.removeAll(toRemove);
    }

    // Getter method for the current size of the gallery
    int getGallerySize() {
        return this.myImages.size();
    }
}
