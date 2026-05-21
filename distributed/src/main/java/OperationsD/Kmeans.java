package OperationsD;

import mpi.MPI;
import java.io.*;
import java.util.*;
/* I followed this link on how to structure the project and split the classes
 https://www.baeldung.com/java-k-means-clustering-algorithm */
public class Kmeans {

    private static final int MASTER = 0;
    private List<Clusters> clusters;
    private final List<Records> records;
    private final int k;
    private final int rank;
    private final int size;

    public Kmeans(List<Records> records, int k) {
        this.records = records;
        this.k = k;
        this.clusters = new ArrayList<>();
        this.rank = MPI.COMM_WORLD.Rank();
        this.size = MPI.COMM_WORLD.Size();
    }

    /* Combined assign and create cluster into assign cluster since the initial clusters are created on master rank */
    public void Assign_Cluster(EucDistance distance) throws Exception {

        int total_records = 0;

        if (rank == MASTER) {
            total_records = records.size();
        }

        // Broadcast the total number of records to all ranks.
        int[] total_records_array = new int[]{total_records};
        MPI.COMM_WORLD.Bcast(total_records_array, 0, 1, MPI.INT, MASTER);
        total_records = total_records_array[0];

        // Calculate how many records each process should receive.
        int chunk_size = (int) Math.ceil((double) total_records / size);
        List<Records> local_records;

        if (rank == MASTER) {

            System.out.println("Master distributing data chunks");

            for (int i = 1; i < size; i++) {

                int start = i * chunk_size;
                int end = Math.min((i + 1) * chunk_size, total_records);
                List<Records> chunk_records = new ArrayList<>(records.subList(start, end));

                // Serialize each chunk before sending it through MPI.
                byte[] bytes = serializeObject(chunk_records);
                int[] length = new int[]{bytes.length};

                // Send the byte array length first, then the data.
                MPI.COMM_WORLD.Send(length, 0, 1, MPI.INT, i, 0);
                MPI.COMM_WORLD.Send(bytes, 0, bytes.length, MPI.BYTE, i, 1);
            }

            local_records = new ArrayList<>(records.subList(0, Math.min(chunk_size, total_records)));
        }
        else {

            int[] length = new int[1];
            // Receive the byte array length first.
            MPI.COMM_WORLD.Recv(length, 0, 1, MPI.INT, MASTER, 0);
            byte[] bytes = new byte[length[0]];
            // Receive the actual serialized data.
            MPI.COMM_WORLD.Recv(bytes, 0, length[0], MPI.BYTE, MASTER, 1);
            local_records = (List<Records>) deserializeObject(bytes);
        }

        System.out.println("Rank " + rank + " received " + local_records.size() + " records.");

        ClusterCentre[] centres = new ClusterCentre[k];

        if (rank == MASTER) {

            for (int i = 0; i < k; i++) {

                Records record = records.get(i);
                Clusters cluster = new Clusters(record.getLatitude(), record.getLongitude());
                clusters.add(cluster);
                centres[i] = new ClusterCentre(record.getLatitude(), record.getLongitude());
            }
        }

        centres = BroadcastCentres(centres);
        boolean cluster_changed;
        /* Using max iter and tolerance to avoid infinite loop*/
        int iteration = 0;
        int max_iterations = 100;
        double tolerance = 0.000001;

        do {
            iteration++;

            // Each rank stores its own partial clustering result.
            WorkerResult local_result = new WorkerResult(k);

            for (Records record : local_records) {

                int nearest_cluster_index = 0;
                Clusters first_cluster = new Clusters(centres[0].getCentre_latitude(), centres[0].getCentre_longitude());
                double minimum_distance = distance.calculateDistance(record, first_cluster);

                for (int i = 1; i < k; i++) {
                    Clusters temporary_cluster = new Clusters(centres[i].getCentre_latitude(), centres[i].getCentre_longitude());
                    double current_distance = distance.calculateDistance(record, temporary_cluster);

                    if (current_distance < minimum_distance) {
                        minimum_distance = current_distance;
                        nearest_cluster_index = i;
                    }
                }

                local_result.addRecord(nearest_cluster_index, record);
            }

            // Collect all partial results back to the master.
            WorkerResult[] all_worker_results = GetWorkerResults(local_result);

            cluster_changed = false;

            if (rank == MASTER) {

                double[] total_latitude = new double[k];
                double[] total_longitude = new double[k];
                int[] total_count = new int[k];
                List<List<Records>> new_cluster_records = new ArrayList<>();

                for (int i = 0; i < k; i++) {
                    new_cluster_records.add(new ArrayList<>());
                }

                // Combine all partial results from each rank.
                for (WorkerResult worker_result : all_worker_results) {

                    for (int i = 0; i < k; i++) {

                        total_latitude[i] += worker_result.latitude_sum[i];
                        total_longitude[i] += worker_result.longitude_sum[i];
                        total_count[i] += worker_result.counts[i];

                        new_cluster_records.get(i).addAll(
                                worker_result.cluster_records.get(i)
                        );
                    }
                }

                for (int i = 0; i < k; i++) {

                    if (total_count[i] == 0) {
                        continue;
                    }

                    Clusters cluster = clusters.get(i);

                    double old_latitude = cluster.getCentre_latitude();
                    double old_longitude = cluster.getCentre_longitude();

                    double new_latitude = total_latitude[i] / total_count[i];
                    double new_longitude = total_longitude[i] / total_count[i];

                    double latitude_difference = Math.abs(old_latitude - new_latitude);
                    double longitude_difference = Math.abs(old_longitude - new_longitude);

                    if (latitude_difference > tolerance || longitude_difference > tolerance) {
                        cluster_changed = true;
                    }

                    cluster.updateCentre(new Point(new_latitude, new_longitude));
                    cluster.setRecords(new_cluster_records.get(i));

                    centres[i] = new ClusterCentre(new_latitude, new_longitude);
                }

            }

            // Broadcast whether another iteration is needed.
            boolean[] changed_array = new boolean[]{cluster_changed};
            MPI.COMM_WORLD.Bcast(changed_array, 0, 1, MPI.BOOLEAN, MASTER);
            cluster_changed = changed_array[0];

            centres = BroadcastCentres(centres);

        } while (cluster_changed && iteration < max_iterations);
    }

    // Sends worker partial results to master and returns all results on master.
    private WorkerResult[] GetWorkerResults(WorkerResult local_result) throws Exception {

        byte[] local_data = serializeObject(local_result);

        if (rank == MASTER) {

            WorkerResult[] worker_results = new WorkerResult[size];
            worker_results[MASTER] = local_result;

            for (int source = 1; source < size; source++) {

                int[] length = new int[1];
                MPI.COMM_WORLD.Recv(length, 0, 1, MPI.INT, source, 30 + source);

                byte[] data = new byte[length[0]];
                MPI.COMM_WORLD.Recv(data, 0, length[0], MPI.BYTE, source, 40 + source);

                worker_results[source] = (WorkerResult) deserializeObject(data);
            }

            return worker_results;

        }

        // Worker ranks send their partial result to master.
        else {

            int[] length = new int[]{local_data.length};

            MPI.COMM_WORLD.Send(length, 0, 1, MPI.INT, MASTER, 30 + rank);
            MPI.COMM_WORLD.Send(local_data, 0, local_data.length, MPI.BYTE, MASTER, 40 + rank);

            return null;
        }
    }

    // Broadcasts the current cluster centres from master to all ranks.
    private ClusterCentre[] BroadcastCentres(ClusterCentre[] centres) throws Exception {

        byte[] data = null;
        int[] length = new int[1];

        if (rank == MASTER) {
            data = serializeObject(centres);
            length[0] = data.length;
        }

        MPI.COMM_WORLD.Bcast(length, 0, 1, MPI.INT, MASTER);

        if (rank != MASTER) {
            data = new byte[length[0]];
        }

        MPI.COMM_WORLD.Bcast(data, 0, length[0], MPI.BYTE, MASTER);

        if (rank != MASTER) {
            centres = (ClusterCentre[]) deserializeObject(data);
        }

        return centres;
    }

    // Converts an object into a byte array so it can be sent through MPI.
    private static byte[] serializeObject(Object object) throws IOException {

        ByteArrayOutputStream byte_stream = new ByteArrayOutputStream();
        ObjectOutputStream object_stream = new ObjectOutputStream(byte_stream);

        object_stream.writeObject(object);
        object_stream.flush();

        return byte_stream.toByteArray();
    }

    // Converts a received byte array back into its original object.
    private static Object deserializeObject(byte[] data) throws IOException, ClassNotFoundException {

        ByteArrayInputStream byte_stream = new ByteArrayInputStream(data);
        ObjectInputStream object_stream = new ObjectInputStream(byte_stream);

        return object_stream.readObject();
    }

    public List<Clusters> getClusters() {
        return clusters;
    }
}