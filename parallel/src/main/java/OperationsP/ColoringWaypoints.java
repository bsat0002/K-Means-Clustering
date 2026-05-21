package OperationsP;

import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.Waypoint;
import java.awt.*;

public class ColoringWaypoints implements Waypoint {
    // These store the position and color of a point and if it is a centre.
    private final GeoPosition position;
    private final Color color;
    private final boolean isCentroid;

    // Constructor for centroid.
    public ColoringWaypoints(GeoPosition position, Color color, boolean isCentroid) {
        this.position = position;
        this.color = color;
        this.isCentroid = isCentroid;
    }

    // Constructor for normal points.
    public ColoringWaypoints(GeoPosition position, Color color) {
        this.position = position;
        this.color = color;
        this.isCentroid = false;
    }

    @Override
    public GeoPosition getPosition() {
        return position;
    }

    public Color getColor() {
        return color;
    }

}
