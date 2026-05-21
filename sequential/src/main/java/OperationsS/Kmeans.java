package OperationsS;

import java.util.ArrayList;
import java.util.List;
/* I followed this link on how to structure the project and split the classes
 https://www.baeldung.com/java-k-means-clustering-algorithm */
public class Kmeans {

    private List<Records> records;
    private List<Clusters> clusters;
    private int k;

    public Kmeans(List<Records> records, int k) {
        this.records = records;
        this.k = k;
        this.clusters = new ArrayList<>();
    }

    // Will create clusters using the first k records as centres.
    public void Create_Clusters(){

        for(int i = 0; i < k; i++) {

            Records record = records.get(i);
            double latitude =  record.getLatitude();
            double longitude = record.getLongitude();
            clusters.add(new Clusters(latitude, longitude));
        }
    }

    // Assigns records to nearest cluster and updates centre
    public void Assign_Cluster(EucDistance distance) {
        boolean cluster_changed = true;

        while (cluster_changed) {
        cluster_changed = false;

        for (Clusters cluster : clusters) {
            cluster.clearRecords();
        }
        for (Records record : records) {
            Clusters nearest = null;
            double min_dist = Double.MAX_VALUE;

            for (Clusters cluster : clusters) {

                double dist = distance.calculateDistance(record, cluster);
                if (dist < min_dist) {
                    min_dist = dist;
                    nearest = cluster;
                }
            }
            nearest.addRecord(record);
        }

        for (Clusters cluster : clusters) {
            double current_centre_lat = cluster.getCentre_latitude();
            double current_centre_long = cluster.getCentre_longitude();

            Point new_centre = cluster.setCentre();

            if(new_centre != null &&
                    (current_centre_lat != new_centre.getLatitude() || current_centre_long != new_centre.getLongitude())){
                cluster_changed = true;
            }
        }

        }
    }
    public List<Clusters> getClusters() {
        return clusters;
    }
}
