package OperationsP;

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
    public void Create_Clusters() {

        for (int i = 0; i < k; i++) {

            Records record = records.get(i);
            double latitude =  record.getLatitude();
            double longitude = record.getLongitude();
            clusters.add(new Clusters(latitude, longitude));
        }
    }

    public void Assign_Cluster(EucDistance distance) throws InterruptedException {

        boolean cluster_changed = true;
        int number_of_threads = Runtime.getRuntime().availableProcessors();

        int iteration = 0; /* I also used chatgpt similar to WorkerTask class to use max iterations and tolerance
        because I could not get the records to be assigned to clusters and the map was never generating */
        int max_iterations = 100;
        double tolerance = 0.000001;

        while (cluster_changed && iteration < max_iterations) {

            cluster_changed = false;
            iteration++;

            for (Clusters cluster : clusters) {
                cluster.clearRecords();
            }

            List<List<Records>> chunks = splitIntoChunks(records, number_of_threads);

            List<Thread> threads = new ArrayList<>();
            List<WorkerResult> worker_results = new ArrayList<>();

            for (List<Records> chunk : chunks) {

                WorkerResult worker_result = new WorkerResult(k);
                worker_results.add(worker_result);
                WorkerTask task = new WorkerTask(chunk, clusters, worker_result, distance);
                Thread thread = new Thread(task);
                threads.add(thread);
                thread.start();
            }

            for (Thread t : threads) {
                t.join();
            }

            for (int i = 0; i < k; i++) {

                double total_latitude = 0;
                double total_longitude = 0;
                int total_count = 0;
                List<Records> all_records = new ArrayList<>();

                for (WorkerResult worker_result : worker_results) {

                    total_latitude += worker_result.latitude_sum[i];
                    total_longitude += worker_result.longitude_sum[i];
                    total_count += worker_result.counts[i];
                    all_records.addAll(worker_result.cluster_records.get(i));
                }

                Clusters cluster = clusters.get(i);

                double old_latitude = cluster.getCentre_latitude();
                double old_longitude = cluster.getCentre_longitude();

                for (Records record : all_records) {
                    cluster.addRecord(record);
                }

                if (total_count > 0) {

                    Point new_centre = cluster.setCentre();

                    if (new_centre != null) {

                        double latitude_difference =
                                Math.abs(old_latitude - new_centre.getLatitude());

                        double longitude_difference =
                                Math.abs(old_longitude - new_centre.getLongitude());

                        if (latitude_difference > tolerance ||
                                longitude_difference > tolerance) {
                            cluster_changed = true;
                        }
                    }
                }
            }
        }
    }
    /* I did use Chatgpt here to obtain chunkSize because I was not sure how to split it */
    private List<List<Records>> splitIntoChunks(List<Records> records, int number_of_chunks) {

        List<List<Records>> chunks = new ArrayList<>();
        int chunk_size = (int) Math.ceil((double) records.size() / number_of_chunks);
        for (int i = 0; i < records.size(); i += chunk_size) {
            int end = Math.min(i + chunk_size, records.size());
            chunks.add(records.subList(i, end));
        }

        return chunks;
    }

    public List<Clusters> getClusters() {
        return clusters;
    }
}