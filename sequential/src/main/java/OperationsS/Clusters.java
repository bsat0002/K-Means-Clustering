package OperationsS;

import java.util.*;

public class Clusters {

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

    // Recalculates the cluster centre using the average position of its records.
    public Point setCentre() {

        if (records.isEmpty()) {
            return null;
        }
        double total_lat = 0;
        double total_lng = 0;

        for (Records record : records) {
            total_lat += record.getLatitude();
            total_lng += record.getLongitude();
        }

        centre_latitude = total_lat / records.size();
        centre_longitude = total_lng / records.size();

        return new Point(centre_latitude, centre_longitude);
    }

    public void addRecord(Records record) {
        records.add(record);
    }
    public List<Records> getRecords() {
        return records;
    }

    public void clearRecords() {
        records.clear();
    }

}
