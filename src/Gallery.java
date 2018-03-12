import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class Gallery extends JPanel {
    private ArrayList<GalleryPiece> myImages = new ArrayList<>();
    private JPanel galleryContainer = new JPanel();

    Gallery(int width) {
        setPreferredSize(new Dimension(width, 150));
        setLayout(new BorderLayout());

        galleryContainer.setLayout(new FlowLayout());
        JScrollPane scroll = new JScrollPane(galleryContainer);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

        JPanel galleryToolBar = new JPanel();
        galleryToolBar.setLayout(new GridLayout(1, 2));

        JButton showImage = new JButton("Show Image");
        showImage.addActionListener(
                (ActionEvent e) -> {
                    for (GalleryPiece img : myImages) {
                        if (img.getIsSelected()) {
                            img.individualPicture();
                        }
                    }
                }
        );

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

    void addNewImage(GalleryPiece img) {
        galleryContainer.add(img);
        myImages.add(img);
    }

    private void removeImage(ArrayList<GalleryPiece> imageList) {
        ArrayList<GalleryPiece> toRemove = new ArrayList<>();

        for (GalleryPiece img : imageList) {
            if (img.getIsSelected()) {
                toRemove.add(img);
                galleryContainer.remove(img);
            }
        }

        myImages.removeAll(toRemove);
    }

    int getGallerySize() {
        return this.myImages.size();
    }
}
