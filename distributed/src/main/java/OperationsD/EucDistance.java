package OperationsD;

public class EucDistance implements Distance<Records, Clusters> {

    @Override
    public double calculateDistance(Records records, Clusters clusters) {

        double distance = 0;

        double lat = records.getLatitude();
        double lon = records.getLongitude();

        double centre_lat = clusters.getCentre_latitude();
        double centre_lon = clusters.getCentre_longitude();

        double lat_diff = centre_lat - lat;
        double lon_diff = centre_lon - lon;

        return Math.sqrt((lat_diff * lat_diff) + (lon_diff * lon_diff));
    }

}
