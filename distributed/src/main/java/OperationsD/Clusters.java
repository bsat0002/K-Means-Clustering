package OperationsD;

import java.io.Serializable;
import java.util.*;

public class Clusters implements Serializable {

    private double centre_longitude;
    private double centre_latitude;
    private List<Records> records;

    // Creates a new cluster with an initial centre position.
    public Clusters(double centre_latitude, double centre_longitude) {
        this.centre_longitude = centre_longitude;
        this.centre_latitude = centre_latitude;
        this.records = new ArrayList<>();
    }

    public double getCentre_longitude() {
        return centre_longitude;
    }

    public double getCentre_latitude() {
        return centre_latitude;
    }

    public void updateCentre(Point new_centre) {
        this.centre_latitude = new_centre.getLatitude();
        this.centre_longitude = new_centre.getLongitude();
    }

    public List<Records> getRecords() {
        return records;
    }

    public void setRecords(List<Records> records) {
        this.records = records;
    }

}
