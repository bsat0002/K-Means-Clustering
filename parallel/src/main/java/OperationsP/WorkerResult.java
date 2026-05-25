package OperationsP;

import java.util.*;

// Stores the result for each processor.
public class WorkerResult {

    public double[] latitude_sum;
    public double[] longitude_sum;
    public int[] counts;
    public List<List<Records>> cluster_records;

    public WorkerResult(int k) {
        latitude_sum = new double[k];
        longitude_sum = new double[k];
        counts = new int[k];

        cluster_records = new ArrayList<>();

        // Creates an empty record list for each cluster.
        for (int i = 0; i < k; i++) {
            cluster_records.add(new ArrayList<>());
        }
    }

    // Adds one record to the selected cluster and updates its totals.
    public void addRecord(int cluster_index, Records record) {
        latitude_sum[cluster_index] += record.getLatitude();
        longitude_sum[cluster_index] += record.getLongitude();
        counts[cluster_index]++;
        cluster_records.get(cluster_index).add(record);
    }
}