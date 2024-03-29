import javax.swing.*;
import java.awt.*;

public class Main extends JFrame{
    Main(String title) {
        super(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Initializes Digital Doilies Windows
     */
    public static void main(String[] args) {
        Main myMainFrame = new Main("Digital Doilies");
        ControlPanel myControlPanel = new ControlPanel();
        JPanel contentContainer = new JPanel();
        contentContainer.setLayout(new FlowLayout());

        myMainFrame.setContentPane(contentContainer);
        contentContainer.add(myControlPanel);

        myMainFrame.pack();
        myMainFrame.setVisible(true);
    }
}
