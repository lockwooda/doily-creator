import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

/**
 * Handles the Gallery panel, including holding the images, removing and saving the images
 */

public class GalleryPanel extends JPanel {
    private DrawingPanel dp;
    private JToggleButton[] jGalleryPanes = new JToggleButton[12];

    /**
     * The constructor of the GalleryPanel
     * @param dp The DrawingPanel, used to get images
     */
    protected GalleryPanel(DrawingPanel dp) {
        //set jpanel properties
        this.setPreferredSize(new Dimension(900, 900));
        this.dp = dp;
        this.setLayout(new BorderLayout());

        //button group for gallery panes
        ButtonGroup buttonGroup = new ButtonGroup();

        //jpanels for the cardlayout for multiple pages
        JPanel jCardGallery = new JPanel(new CardLayout());
        JPanel jFirstPage = new JPanel();
        JPanel jSecondPage = new JPanel();

        //instantiate togglebuttons and add to the buttongroup
        for(int i = 0 ; i < 12 ; i++) {
            this.getGalleryPanes()[i] = new JToggleButton();
            buttonGroup.add(this.getGalleryPanes()[i]);
        }

        //add 6 buttons to the first page, 6 to the second
        for(int i = 0 ; i < 6 ; i++) {
            jFirstPage.add(jGalleryPanes[i]);
        }
        for(int i = 6 ; i < 12 ; i++) {
            jSecondPage.add(jGalleryPanes[i]);
        }

        //set page layouts as (3x2) grid
        jFirstPage.setLayout(new GridLayout(3,2));
        jSecondPage.setLayout(new GridLayout(3,2));

        //add to cardlayout pane the pages
        jCardGallery.add(jFirstPage);
        jCardGallery.add(jSecondPage);

        //create new jbuttons for saving, remove and changing page
        JButton jPreviousPageButton = new JButton("Previous Page");
        JButton jSaveButton = new JButton("Save");
        JButton jRemoveButton = new JButton("Remove");
        JButton jNextPageButton = new JButton("Next Page");

        jPreviousPageButton.setEnabled(false);

        //go back a page
        jPreviousPageButton.addActionListener((ActionEvent e) -> {
            CardLayout cardLayout = (CardLayout) jCardGallery.getLayout();
            cardLayout.previous(jCardGallery);
            jNextPageButton.setEnabled(true);
            jPreviousPageButton.setEnabled(false);
        });

        //save an image to a button
        jSaveButton.addActionListener((ActionEvent e) -> {
            BufferedImage image = new BufferedImage(dp.getWidth(), dp.getHeight(), BufferedImage.TYPE_INT_RGB);
            dp.paint(image.getGraphics());
            Image scaledImage = image.getScaledInstance(dp.getWidth() / 2, dp.getHeight() / 2, BufferedImage.TYPE_INT_RGB);
            this.saveImage(scaledImage);
        });

        //remove an image
        jRemoveButton.addActionListener((ActionEvent e) -> {
            this.removeImage();
        });

        //change to the next page
        jNextPageButton.addActionListener((ActionEvent e) -> {
            CardLayout cardLayout = (CardLayout) jCardGallery.getLayout();
            cardLayout.next(jCardGallery);
            jNextPageButton.setEnabled(false);
            jPreviousPageButton.setEnabled(true);
        });

        //create one final jpanel for the buttons on the bottom
        JPanel jButtonPanel = new JPanel();
        jButtonPanel.setLayout(new GridLayout(1, 4));
        jButtonPanel.add(jPreviousPageButton);
        jButtonPanel.add(jRemoveButton);
        jButtonPanel.add(jSaveButton);
        jButtonPanel.add(jNextPageButton);

        //add the two jpanels to the main jpanel
        this.add(jCardGallery, BorderLayout.CENTER);
        this.add(jButtonPanel, BorderLayout.SOUTH);
    }

    /**
     * Saving an image to a button
     * @param image The image to save to the button
     */
    private void saveImage(Image image) {
        boolean bSelected = false;
        //find the selected button, and add the image to the button
        for(JToggleButton button : this.getGalleryPanes()) {
            if(button.isSelected()) {
                button.setIcon(new ImageIcon(image));
                bSelected = true;
                break;
            }
        }

        //if no button is selected, go through each button and add to an empty button if it exists
        if(!bSelected) {
            for(JToggleButton button : this.getGalleryPanes()) {
                if(button.getIcon() == null) {
                    button.setIcon(new ImageIcon(image));
                    bSelected = true;
                    break;
                }
            }
        }

        //if there is still no place to save it, overwrite the first icon
        if(!bSelected) {
            this.getGalleryPanes()[0].setIcon(new ImageIcon(image));
        }
    }

    /**
     * Removing an image from the gallery
     */
    private void removeImage() {
        //if a button is selected (and only one will be), then remove the image form the button
        for(JToggleButton button : this.getGalleryPanes()) {
            if(button.isSelected()) {
                button.setIcon(null);
                break;
            }
        }
    }

    //getter
    private JToggleButton[] getGalleryPanes() {
        return this.jGalleryPanes;
    }
}
