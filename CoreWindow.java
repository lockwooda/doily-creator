import javax.swing.*;
import java.awt.*;

/**
 * Initialises all the main parts of the GUI, and connects them together via constructors.
 * Packs the parts of the GUI together.
 */

public class CoreWindow extends JFrame {
    protected void init() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1200, 900);
        this.setTitle("Digital Doily");

        Container jpContainer = this.getContentPane();

        DrawingPanel drawing = new DrawingPanel();
        GalleryPanel gallery = new GalleryPanel(drawing);
        SettingsPanel settings = new SettingsPanel(drawing, gallery);

        jpContainer.setLayout(new BorderLayout());

        jpContainer.add(settings, BorderLayout.WEST);
        jpContainer.add(drawing, BorderLayout.CENTER);

        this.pack();
        this.setResizable(false);
        this.setVisible(true);
    }

}