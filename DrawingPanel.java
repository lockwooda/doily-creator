import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.*;

/**
 * Handles the drawing functionality of the doily.
 * Does all the calculations required for reflection and erasing
 */

public class DrawingPanel extends JPanel {
    private ArrayList<Point> alCurrentShapes = new ArrayList<>();
    private Stack<DraggedPoints> stCurrentStack = new Stack<>();
    private Stack<DraggedPoints> stRedoStack = new Stack<>();

    private int iSectors;
    private int iSize;
    private Color cColor;
    private boolean bReflecting;
    private boolean bErasing;
    private boolean bLines;

    /**
     * Constructor that initialises the size, background colour, initial variables and listeners
     */
    protected DrawingPanel() {
        this.setPreferredSize(new Dimension(800, 750));
        this.setBackground(Color.BLACK);

        this.addMouseListener(new DragListener());
        this.addMouseMotionListener(new DragListener());
        this.setSectors(12);
        this.setStrokeSize(5);
        this.setDrawingLines(true);
        this.setColor(Color.RED);
    }

    /**
     * Listener that handles the user drawing on the screen
     */
    class DragListener implements MouseListener, MouseMotionListener {
        //when first pressed, get the coordinates and add to an arraylist. repaint to show new point
        public void mousePressed(MouseEvent e) {
            DrawingPanel.this.getCurrentShapes().add(new Point(e.getX(), e.getY()));
            repaint();
        }

        //when mouse is released, if not erasing then add the new shape to the stack, repaint
        //if erasing, send the arraylist to removePoints and then repaint once complete
        //clear the list once done
        public void mouseReleased(MouseEvent e) {
            if(!DrawingPanel.this.isErasing()) {
                ArrayList<Point> newCurrentShapes;
                newCurrentShapes = (ArrayList) DrawingPanel.this.getCurrentShapes().clone();
                DrawingPanel.this.getCurrentStack().push(new DraggedPoints(newCurrentShapes
                        , DrawingPanel.this.getStrokeSize()
                        , DrawingPanel.this.getColor()
                        , DrawingPanel.this.isReflecting()
                        , DrawingPanel.this.isErasing())
                );
                repaint();
            }
            else {
                DrawingPanel.this.removePoints(DrawingPanel.this.getCurrentShapes());
                repaint();
            }
            DrawingPanel.this.getCurrentShapes().clear();
        }

        public void mouseDragged(MouseEvent e) {
            DrawingPanel.this.getCurrentShapes().add(new Point(e.getX(), e.getY()));
            repaint();
        }

        //unimplemented methods
        @Override
        public void mouseClicked(MouseEvent e) {
        }
        @Override
        public void mouseEntered(MouseEvent e) {
        }
        @Override
        public void mouseExited(MouseEvent e) {
        }
        @Override
        public void mouseMoved(MouseEvent e) {
        }
    }

    /**
     * Method that paints the panel, once called
     * @param g
     */
    public void paintComponent(Graphics g) {
        //initialising variables and calling super-class
        super.paintComponent(g);
        Ellipse2D.Double e;
        DraggedPoints d;

        Graphics2D g2d = (Graphics2D) g;
        AffineTransform atx;
        //setting antialiasing on to make it look pretty!
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setBackground(Color.BLACK);

        //draw the lines if required
        if(DrawingPanel.this.isDrawingLines()) {
            this.drawBackgroundLines(g2d);
        }

        //for the alCurrentShapes currently inside of the current points stack
        Iterator<DraggedPoints> it = DrawingPanel.this.getCurrentStack().iterator();
        while (it.hasNext()) {
            d = it.next();
            g2d.setColor(d.getcPointsColor());
            //if only one Point in the arraylist
            if(d.getAlListOfPoints().size() == 1) {
                this.drawPoint(g2d, d);
            }
            //if multiple points in the arraylist
            else {
                this.drawLines(g2d, d);
            }
        }

        //if erasing, temporarily set the alphacomposite to DST_OUT.
        if(this.isErasing()) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_OUT));
        }
        //for the currently drawn alCurrentShapes
        if(DrawingPanel.this.getCurrentShapes().size() == 1) {
            this.drawPoint(g2d, DrawingPanel.this.getCurrentShapes().get(0));
        }
        else {
            this.drawLines(g2d, DrawingPanel.this.getCurrentShapes());
        }
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
    }

    /**
     * Draw the background lines
     * @param g2d
     */
    private void drawBackgroundLines(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        for(int i = 0 ; i < this.getSectors() ; i++) {
            g2d.rotate(Math.toRadians(360.0 / this.getSectors()), this.getSize().getWidth() / 2, this.getSize().getHeight() / 2);
            g2d.drawLine((int) this.getSize().getWidth() / 2, 0, (int) this.getSize().getWidth() / 2, (int) this.getSize().getHeight() / 2);
            g2d.setStroke(new BasicStroke(1));
        }
    }

    /**
     * Drawing a singular point from the stack
     * @param g2d
     * @param d DraggedPoint from the stack
     */
    private void drawPoint(Graphics2D g2d, DraggedPoints d) {
        //define variables
        Ellipse2D.Double e;
        AffineTransform atx;
        e = new Ellipse2D.Double(d.getAlListOfPoints().get(0).getX() - (d.getiSize() / 2)
                , d.getAlListOfPoints().get(0).getY() - (d.getiSize() / 2)
                , d.getiSize()
                , d.getiSize());

        //if reflecting, perform an affinetranform and then draw the reflected parts.
        if(d.isbReflected()) {
            for (int i = 0; i < this.getSectors(); i++) {
                atx = AffineTransform.getTranslateInstance(this.getSize().getWidth() / 2, 0);
                atx.scale(-1, 1);
                atx.translate(-this.getSize().getWidth() / 2, 0);
                atx.rotate(Math.toRadians((360.0 / this.getSectors()) * i), this.getSize().getWidth() / 2, this.getSize().getHeight() / 2);

                g2d.setTransform(atx);
                g2d.draw(e);
                g2d.fill(e);

                g2d.setTransform(AffineTransform.getScaleInstance(1, 1));
            }
        }
        //draw the normal points
        for (int i = 0; i < this.getSectors(); i++) {
            g2d.rotate(Math.toRadians(360.0 / this.getSectors()), this.getSize().getWidth() / 2, this.getSize().getHeight() / 2);
            g2d.draw(e);
            g2d.fill(e);
        }
    }

    /**
     * Drawing a singular point from the current shapes
     * @param g2d
     * @param p Point from the ArrayList
     */
    private void drawPoint(Graphics2D g2d, Point p) {
        //initialise variables, set color to the current one being used
        Ellipse2D.Double e;
        AffineTransform atx;
        g2d.setColor(DrawingPanel.this.getColor());

        e = new Ellipse2D.Double(p.getX() - (DrawingPanel.this.getStrokeSize() / 2)
                , p.getY() - (DrawingPanel.this.getStrokeSize() / 2)
                , DrawingPanel.this.getStrokeSize()
                , DrawingPanel.this.getStrokeSize());

        //if reflecting, perform an affinetranform and then draw the reflected parts.
        if(DrawingPanel.this.isReflecting()) {
            for (int i = 0; i < this.getSectors(); i++) {
                atx = AffineTransform.getTranslateInstance(this.getSize().getWidth() / 2, 0);
                atx.scale(-1, 1);
                atx.translate(-this.getSize().getWidth() / 2, 0);
                atx.rotate(Math.toRadians((360.0 / this.getSectors()) * i), this.getSize().getWidth() / 2, this.getSize().getHeight() / 2);

                g2d.setTransform(atx);
                g2d.draw(e);
                g2d.fill(e);
            }
            g2d.setTransform(AffineTransform.getScaleInstance(1, 1));
        }
        //draw the normal points
        for (int i = 0; i < this.getSectors(); i++) {
            g2d.rotate(Math.toRadians(360.0 / this.getSectors()), this.getSize().getWidth() / 2, this.getSize().getHeight() / 2);
            g2d.draw(e);
            g2d.fill(e);
        }
    }

    /**
     * Drawing a list of points (as lines) from the stack
     * @param g2d
     * @param d DraggedPoints from the stack
     */
    private void drawLines(Graphics2D g2d, DraggedPoints d) {
        AffineTransform atx;

        //start from one, easier than requiring an if statement inside each time to check if 0
        for (int i = 1; i < d.getAlListOfPoints().size(); i++) {
            //for the pair of points, draw a line between them and rotate by the appropriate amount.
            if(d.isbReflected()) {
                for (int j = 0; j < this.getSectors(); j++) {
                    atx = AffineTransform.getTranslateInstance(this.getSize().getWidth() / 2, 0);
                    atx.scale(-1, 1);
                    atx.translate(-this.getSize().getWidth() / 2, 0);
                    atx.rotate(Math.toRadians((360.0 / this.getSectors()) * j), this.getSize().getWidth() / 2, this.getSize().getHeight() / 2);

                    g2d.setTransform(atx);
                    g2d.drawLine(d.getAlListOfPoints().get(i).getX()
                            , d.getAlListOfPoints().get(i).getY()
                            , d.getAlListOfPoints().get(i - 1).getX()
                            , d.getAlListOfPoints().get(i - 1).getY());
                    g2d.setStroke(new BasicStroke((float)(d.getiSize() / Math.sqrt(2.0))));
                }
                g2d.setTransform(AffineTransform.getScaleInstance(1,1));
            }
            for (int j = 0; j < this.getSectors(); j++) {
                g2d.rotate(Math.toRadians(360.0 / this.getSectors()), this.getSize().getWidth() / 2, this.getSize().getHeight() / 2);
                g2d.drawLine(d.getAlListOfPoints().get(i).getX()
                        , d.getAlListOfPoints().get(i).getY()
                        , d.getAlListOfPoints().get(i - 1).getX()
                        , d.getAlListOfPoints().get(i - 1).getY());
                g2d.setStroke(new BasicStroke((float)(d.getiSize() / Math.sqrt(2.0))));
            }
        }
    }

    /**
     * Drawing a list of points (as lines) from the current shapes list
     * @param g2d
     * @param d ArrayList from the current shapes list
     */
    private void drawLines(Graphics2D g2d, ArrayList<Point> d) {
        AffineTransform atx;
        g2d.setColor(DrawingPanel.this.getColor());

        //start from one, easier than requiring an if statement inside each time to check if 0
        for (int i = 1; i < d.size(); i++) {
            //for the pair of points, draw a line between them and rotate by the appropriate amount.
            if(DrawingPanel.this.isReflecting()) {
                for (int j = 0; j < this.getSectors(); j++) {
                    atx = AffineTransform.getTranslateInstance(this.getSize().getWidth() / 2, 0);
                    atx.scale(-1, 1);
                    atx.translate(-this.getSize().getWidth() / 2, 0);
                    atx.rotate(Math.toRadians((360.0 / this.getSectors()) * j), this.getSize().getWidth() / 2, this.getSize().getHeight() / 2);

                    g2d.setTransform(atx);
                    g2d.drawLine(d.get(i).getX()
                            , d.get(i).getY()
                            , d.get(i - 1).getX()
                            , d.get(i - 1).getY());
                    g2d.setStroke(new BasicStroke((float)(DrawingPanel.this.getStrokeSize() / Math.sqrt(2.0))));
                }
                g2d.setTransform(AffineTransform.getScaleInstance(1,1));
            }
            for (int j = 0; j < this.getSectors(); j++) {
                g2d.rotate(Math.toRadians(360.0 / this.getSectors()), this.getSize().getWidth() / 2, this.getSize().getHeight() / 2);
                g2d.drawLine(d.get(i).getX()
                        , d.get(i).getY()
                        , d.get(i - 1).getX()
                        , d.get(i - 1).getY());
                g2d.setStroke(new BasicStroke((float)(DrawingPanel.this.getStrokeSize() / Math.sqrt(2.0))));
            }
        }
    }

    /**
     * Used to remove points once the user has released the mouse
     * @param alEraserPoints
     */
    private void removePoints(ArrayList<Point> alEraserPoints) {
        //declare and initialise variables
        Iterator<DraggedPoints> it = this.getCurrentStack().iterator();
        DraggedPoints d;
        TreeSet<Integer> tPointsToRemove = new TreeSet<>();
        ArrayList<DraggedPoints> alListOfNewPoints = new ArrayList<>();
        ArrayList<Point> alInnerPoints = new ArrayList<>();
        double dLength1, dAngle1;
        boolean bErased;

        //iterate through the stack
        while(it.hasNext()) {
            bErased = false;
            d = it.next();

            //for all points of DraggedPoint, check it with each drawn eraser point in each sector
            for(int i = 0 ; i < d.getAlListOfPoints().size() ; i++) {

                for(int j = 0 ; j < alEraserPoints.size() ; j++) {
                    dLength1 = Math.sqrt(Math.pow(alEraserPoints.get(j).getX() - (this.getWidth() / 2), 2) + Math.pow(alEraserPoints.get(j).getY() - (this.getHeight() / 2), 2));
                    dAngle1 = Math.atan2((double)(alEraserPoints.get(j).getY() - (this.getHeight() / 2)), (double)(alEraserPoints.get(j).getX() - (this.getWidth() / 2)));

                    //if the distance between the two points is less than the radius of the eraser, mark for deletion in a set
                    for(int k = 0 ; k < this.getSectors() ; k++) {
                        if(Math.sqrt(Math.pow(d.getAlListOfPoints().get(i).getX() - (dLength1 * Math.cos(dAngle1 + Math.toRadians((360 / this.getSectors()) * k)) + (this.getWidth() / 2)), 2)
                                + Math.pow(d.getAlListOfPoints().get(i).getY() - (dLength1 * Math.sin(dAngle1 + Math.toRadians((360 / this.getSectors()) * k)) + (this.getHeight() / 2)), 2)) <= (this.getStrokeSize() / 2)) {
                            tPointsToRemove.add(i);
                            bErased = true;
                        }
                    }
                    //if the points in question are reflected, check the alternate side and iterate through them
                    if(d.isbReflected()) {
                        dLength1 = Math.sqrt(Math.pow((this.getWidth() / 2) - alEraserPoints.get(j).getX(), 2) + Math.pow((this.getHeight() / 2) - alEraserPoints.get(j).getY(), 2));
                        dAngle1 = Math.atan2((double)((this.getHeight() / 2) - alEraserPoints.get(j).getY()), (double)((this.getWidth() / 2) - alEraserPoints.get(j).getX()));

                        //if the distance between the two points is less than the radius of the eraser, mark for deletion in a set
                        for(int k = 0 ; k < this.getSectors() ; k++) {
                            if(Math.sqrt(Math.pow(d.getAlListOfPoints().get(i).getX() - (dLength1 * Math.cos(dAngle1 + Math.toRadians((360 / this.getSectors()) * k)) + (this.getWidth() / 2)), 2)
                                    + Math.pow(d.getAlListOfPoints().get(i).getY() - (dLength1 * Math.sin(dAngle1 + Math.toRadians((360 / this.getSectors()) * k)) + (this.getHeight() / 2)), 2)) <= (this.getStrokeSize() / 2)) {
                                tPointsToRemove.add(i);
                                bErased = true;
                            }
                        }
                    }
                }
            }

            //if erasing, iterate through each point in the line
            if(bErased) {
                int iStartLine = 0;
                int iEndLine = 0;

                for(int i = 0 ; i < d.getAlListOfPoints().size() ; i++) {
                    //if a point marked for deletion appears, and does not follow another point, or is the last point
                    if((tPointsToRemove.contains(i)) || (i == d.getAlListOfPoints().size() - 1)) {
                        //mark the end of the line
                        iEndLine = i;
                        if(tPointsToRemove.contains(i - 1)) {
                            iStartLine++;
                            continue;
                        }
                        //iterate through the subset of the points marked and add to an arraylist of erased lines
                        for(int j = iStartLine ; j < iEndLine ; j++) {
                            alInnerPoints.add(d.getAlListOfPoints().get(j));
                        }
                        alListOfNewPoints.add(new DraggedPoints(new ArrayList<>(alInnerPoints)
                                , d.getiSize()
                                , d.getcPointsColor()
                                , d.isbReflected()
                                , d.isbEraser()));

                        iStartLine = i + 1;
                        alInnerPoints.clear();
                    }
                    else {
                        iEndLine++;
                    }
                }
                //remove the old line
                it.remove();
            }
        }

        //add each new line to the stack
        for(DraggedPoints newD : alListOfNewPoints) {
            this.getCurrentStack().add(newD);
        }
    }

    //getters
    private ArrayList<Point> getCurrentShapes() {
        return alCurrentShapes;
    }

    protected Stack<DraggedPoints> getCurrentStack() {
        return stCurrentStack;
    }

    protected Stack<DraggedPoints> getRedoStack() {
        return stRedoStack;
    }

    private int getSectors() {
        return iSectors;
    }

    private int getStrokeSize() {
        return iSize;
    }

    private Color getColor() {
        return cColor;
    }

    private boolean isReflecting() {
        return bReflecting;
    }

    private boolean isErasing() {
        return bErasing;
    }

    private boolean isDrawingLines() {
        return bLines;
    }

    //setters
    protected void setSectors(int iSectors) {
        this.iSectors = iSectors;
    }

    protected void setStrokeSize(int iSize) {
        this.iSize = iSize;
    }

    protected void setColor(Color cColor) {
        this.cColor = cColor;
    }

    protected void setReflecting(boolean bReflecting) {
        this.bReflecting = bReflecting;
    }

    protected void setErasing(boolean bErasing) {
        this.bErasing = bErasing;
    }

    protected void setDrawingLines(boolean bLines) {
        this.bLines = bLines;
    }
}