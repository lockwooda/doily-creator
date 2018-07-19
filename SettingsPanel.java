import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.image.BufferedImage;

/**
 * Settings panel to change drawing options for the drawing panel, and to access the gallery panel.
 */
public class SettingsPanel extends JPanel {
    private DrawingPanel dp;
    private GalleryPanel gp;

    /**
     * Constructor that sets up the JPanel
     * @param dp The DrawingPanel to change
     * @param gp The GalleryPanel to change
     */
    public SettingsPanel(DrawingPanel dp, GalleryPanel gp) {
        //set the appropriate drawingpanel and gallerypanel
        this.setDrawingPanel(dp);
        this.setGalleryPanel(gp);

        //set the layout of the settings panel
        this.setLayout(new GridLayout(15,1));

        //button to pick the colour
        JButton jColorButton = new JButton("Colour Picker");

        //slider to select number of sectors
        JLabel jSectorsLabel = new JLabel("Number of Sectors");
        JSlider jSectorsSlide = new JSlider(JSlider.HORIZONTAL, 1, 60, 12);
        jSectorsSlide.setMinorTickSpacing(1);
        jSectorsSlide.setMajorTickSpacing(10);
        jSectorsSlide.setPaintTicks(true);
        jSectorsSlide.setPaintLabels(true);
        jSectorsSlide.setSnapToTicks(true);

        //slider to select size of stroke
        JLabel jSizeLabel = new JLabel("Size");
        JSlider jSizeSlide = new JSlider(JSlider.HORIZONTAL, 1, 20, 5);
        jSizeSlide.setMinorTickSpacing(1);
        jSizeSlide.setMajorTickSpacing(4);
        jSizeSlide.setPaintTicks(true);
        jSizeSlide.setPaintLabels(true);
        jSizeSlide.setSnapToTicks(true);

        //clear, undo and redo buttons
        JButton jClearButton = new JButton("Clear");
        JButton jUndoButton = new JButton("Undo");
        JButton jRedoButton = new JButton("Redo");

        //checkboxes to draw lines, reflect and to erase
        JCheckBox jDrawLines = new JCheckBox("Draw Sector Lines");
        jDrawLines.setSelected(true);
        JCheckBox jReflectDragged = new JCheckBox("Reflect Dragged Line");
        JCheckBox jEraser = new JCheckBox("Eraser");

        //button to access gallery
        JButton jGalleryButton = new JButton("Gallery");

        //set a preferred size, and set a border stating that it is the settings
        this.setPreferredSize(new Dimension(400, 750));
        this.setBorder(BorderFactory.createTitledBorder("Settings"));

        //set the color to the selected colour from a colour panel
        jColorButton.addActionListener((ActionEvent e) -> {
            SettingsPanel.this.getDrawingPanel().setColor(JColorChooser.showDialog(null, "Choose a Color", Color.RED));
        });

        //change the number of sectors
        jSectorsSlide.addChangeListener((ChangeEvent e) -> {
            SettingsPanel.this.getDrawingPanel().setSectors(jSectorsSlide.getValue());
            SettingsPanel.this.getDrawingPanel().repaint();
        });

        //change the size of the stroke
        jSizeSlide.addChangeListener((ChangeEvent e) -> {
            SettingsPanel.this.getDrawingPanel().setStrokeSize(jSizeSlide.getValue());
        });

        //clear the current stack
        jClearButton.addActionListener((ActionEvent e) -> {
            SettingsPanel.this.getDrawingPanel().getCurrentStack().clear();
            SettingsPanel.this.getDrawingPanel().repaint();
        });

        //add an element from the current stack to the redo stack
        jUndoButton.addActionListener((ActionEvent e) -> {
            SettingsPanel.this.getDrawingPanel().getRedoStack().push(SettingsPanel.this.getDrawingPanel().getCurrentStack().pop());
            SettingsPanel.this.getDrawingPanel().repaint();
        });

        //add an element from the redo stack to the undo stack
        jRedoButton.addActionListener((ActionEvent e) -> {
            SettingsPanel.this.getDrawingPanel().getCurrentStack().push(SettingsPanel.this.getDrawingPanel().getRedoStack().pop());
            SettingsPanel.this.getDrawingPanel().repaint();
        });

        //if selected, draw lines. repaint
        jDrawLines.addItemListener((ItemEvent e) -> {
            if(e.getStateChange() == ItemEvent.SELECTED) {
                SettingsPanel.this.getDrawingPanel().setDrawingLines(true);
            }
            else {
                SettingsPanel.this.getDrawingPanel().setDrawingLines(false);
            }

            SettingsPanel.this.getDrawingPanel().repaint();
        });

        //if selected, reflect newly drawn points
        jReflectDragged.addItemListener((ItemEvent e) -> {
            if(e.getStateChange() == ItemEvent.SELECTED) {
                SettingsPanel.this.getDrawingPanel().setReflecting(true);
            }
            else {
                SettingsPanel.this.getDrawingPanel().setReflecting(false);
            }
        });

        //if selected, newly drawn points will erase
        jEraser.addItemListener((ItemEvent e) -> {
            if(e.getStateChange() == ItemEvent.SELECTED) {
                SettingsPanel.this.getDrawingPanel().setErasing(true);
            }
            else {
                SettingsPanel.this.getDrawingPanel().setErasing(false);
            }
        });

        //if selected, create a new jframe and show the gallery pane
        jGalleryButton.addActionListener((ActionEvent e) -> {
            JFrame jGalleryFrame = new JFrame();
            jGalleryFrame.setTitle("Doily Gallery");
            jGalleryFrame.add(gp);
            jGalleryFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            jGalleryFrame.setSize(new Dimension(900, 900));
            jGalleryFrame.setResizable(false);
            jGalleryFrame.setVisible(true);
        });

        //add all the components to our settings panel
        this.add(jColorButton, Component.CENTER_ALIGNMENT);
        this.add(Box.createRigidArea(new Dimension(0, 15)));
        this.add(jSectorsLabel);
        this.add(jSectorsSlide);
        this.add(Box.createRigidArea(new Dimension(0, 15)));
        this.add(jSizeLabel);
        this.add(jSizeSlide);
        this.add(jClearButton);
        this.add(jUndoButton);
        this.add(jRedoButton);
        this.add(jDrawLines);
        this.add(jReflectDragged);
        this.add(jEraser);
        this.add(jGalleryButton);
    }

    //getters
    public DrawingPanel getDrawingPanel() {
        return dp;
    }

    public GalleryPanel getGalleryPanel() {
        return gp;
    }

    //setters
    private void setDrawingPanel(DrawingPanel dp) {
        this.dp = dp;
    }

    private void setGalleryPanel(GalleryPanel gp) {
        this.gp = gp;
    }
}
