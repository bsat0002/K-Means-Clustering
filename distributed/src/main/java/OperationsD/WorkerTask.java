package OperationsD;

import java.util.List;

public class WorkerTask implements Runnable {

    private final List<Records> chunk_records;
    private final List<Clusters> clusters;
    private final WorkerResult worker_result;
    private final EucDistance distance;

    public WorkerTask(List<Records> recordsChunk, List<Clusters> clusters, WorkerResult result, EucDistance distance) {

        this.chunk_records = recordsChunk;
        this.clusters = clusters;
        this.worker_result = result;
        this.distance = distance;
    }

    @Override
    public void run() { // I did use Chatgpt for this because records where not being assigned to clusters and I could not get it to work

        for (Records record : chunk_records) {

            int nearest_cluster_index = -1;
            double min_distance = Double.MAX_VALUE;

            for (int i = 0; i < clusters.size(); i++) {

                double currentDistance = distance.calculateDistance(record, clusters.get(i));

                if (currentDistance < min_distance) {
                    min_distance = currentDistance;
                    nearest_cluster_index = i;
                }
            }

            worker_result.addRecord(nearest_cluster_index, record);
        }
    }
}