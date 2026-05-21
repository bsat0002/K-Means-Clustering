package OperationsP;

public class Records {

    private double lat;
    private double lon;

    public Records(double lat, double lon) {
        this.lat = lat;
        this.lon =  lon;
    }
    public double getLatitude()
    {
        return lat;
    }
    public double getLongitude() {
        return lon;
    }

}
