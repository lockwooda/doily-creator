/**
 * Class that holds the cartesian coordinates of the drawn point
 */

public class Point {
    int iXCoordinate;
    int iYCoordinate;

    public Point(int iX, int iY) {
        this.setX(iX);
        this.setY(iY);
    }

    //getters
    public int getX() {
        return this.iXCoordinate;
    }

    public int getY() {
        return this.iYCoordinate;
    }

    //setters
    private void setX(int x) {
        this.iXCoordinate = x;
    }

    private void setY(int y) {
        this.iYCoordinate = y;
    }
}
