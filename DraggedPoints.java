import java.awt.Color;
import java.util.ArrayList;

/**
 * DraggedPoints
 * The class which holds a sequence of points, along with its associated settings
 */

public class DraggedPoints {
    private ArrayList<Point> alListOfPoints;
    private int iSize;
    Color cPointsColor;
    private boolean bReflected;
    private boolean bEraser;

    //constructor
    public DraggedPoints(ArrayList<Point> alListOfPoints
            , int iSize
            , Color cPointsColor
            , boolean bReflected
            , boolean bEraser) {
        this.setAlListOfPoints(alListOfPoints);
        this.setiSize(iSize);
        this.setcPointsColor(cPointsColor);
        this.setbReflected(bReflected);
        this.setbEraser(bEraser);
    }

    //getters
    public ArrayList<Point> getAlListOfPoints() {
        return alListOfPoints;
    }

    public int getiSize() {
        return iSize;
    }

    public Color getcPointsColor() {
        return cPointsColor;
    }

    public boolean isbReflected() {
        return bReflected;
    }

    public boolean isbEraser() {
        return bEraser;
    }

    //setters
    public void setAlListOfPoints(ArrayList<Point> alListOfPoints) {
        this.alListOfPoints = alListOfPoints;
    }

    public void setiSize(int iSize) {
        this.iSize = iSize;
    }

    public void setcPointsColor(Color cPointsColor) {
        this.cPointsColor = cPointsColor;
    }

    public void setbReflected(boolean bReflected) {
        this.bReflected = bReflected;
    }

    public void setbEraser(boolean bEraser) {
        this.bEraser = bEraser;
    }
}
