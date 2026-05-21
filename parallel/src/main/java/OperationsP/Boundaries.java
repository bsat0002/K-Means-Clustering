package OperationsP;

public class Boundaries {
    private double latitude_start;
    private double longitude_start;
    private double latitude_end;
    private double longitude_end;

    // Creates a boundary region using start and end latitude/longitude values.
    public Boundaries(double latitude_start, double longitude_start, double latitude_end, double longitude_end) {
        this.latitude_start = latitude_start;
        this.longitude_start = longitude_start;
        this.latitude_end = latitude_end;
        this.longitude_end = longitude_end;
    }

    public double getLatitude_start() {
        return latitude_start;
    }
    public double getLatitude_end() {
        return latitude_end;
    }
    public double getLongitude_start() {
        return longitude_start;
    }
    public double getLongitude_end() {
        return longitude_end;
    }
}
