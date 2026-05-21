package OperationsD;

import java.io.Serializable;

public class Records implements Serializable {

    private static final long serialVersionUID = 1L;
    private double lat;
    private double lon;

    public Records(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public double getLatitude() {
        return lat;
    }

    public double getLongitude() {
        return lon;
    }
}