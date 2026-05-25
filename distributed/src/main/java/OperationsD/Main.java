package OperationsD;

import mpi.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class Main {

    private static final int MASTER = 0;
    private static int WIDTH = 800;
    private static int HEIGHT = 600;

    public static void main(String[] args) throws Exception {
    // I did not use invokeLater here since ranks need to synchronize


            System.out.println("Running distributed...");
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        int k = 0;
        int accumulation_sites = 0;
        int continue_program = 1;

        if (rank == MASTER) {

            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout(2, 0));

            JTextField kField = new JTextField();
            JTextField sitesField = new JTextField();

            panel.add(new JLabel(" Insert number of Clusters required:"));
            panel.add(kField);
            panel.add(new JLabel("Insert number of Accumulation Sites required:"));
            panel.add(sitesField);

            int result = JOptionPane.showConfirmDialog(null, panel, "Distributed K-Means-Menu",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result != JOptionPane.OK_OPTION) {
                System.out.println("Exiting...");
                continue_program = 0;
            }


            try {

                k = Integer.parseInt(kField.getText().trim());
                accumulation_sites = Integer.parseInt(sitesField.getText().trim());

                if (k > accumulation_sites) {
                    JOptionPane.showMessageDialog(null,
                            "Please enter a number of clusters that is less than or equal to the number of accumulation sites.",
                            "Invalid Input",
                            JOptionPane.WARNING_MESSAGE);

                    continue_program = 0;
                }

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter a positive integer value.");
                continue_program = 0;
            }

        }

        int[] input_data = new int[]{continue_program, k, accumulation_sites};

        MPI.COMM_WORLD.Bcast(input_data, 0, 3, MPI.INT, MASTER);

        continue_program = input_data[0];
        k = input_data[1];
        accumulation_sites = input_data[2];

        if (continue_program == 0) {
            MPI.Finalize();
            return;
        }

        List<Records> records = null;

        if (rank == MASTER) {

            System.out.println("Map Size: " + WIDTH + "x" + HEIGHT);
            System.out.println("Number of Clusters " + k);
            System.out.println("Number of Accumulation Sites: " + accumulation_sites);

            records = FileReading.loadRecords("Italy/italy.json"); /* Using Italy as the main country
                Also the Test.json can be substituted instead of the italy.json to test how the random sites are generated,
                especially since the italy.json has around 36k coordinates and taking a parameter larger enough to obtain a decent
                clustering outside of Italy made the program choppy, especially if you zoom out further.*/

            if (records.isEmpty()) {
                System.out.println("No records found.");
                MPI.Finalize();
                return;
            }

            if (accumulation_sites > records.size()) { /* Code to generate extra data points. Got help and suggestions
                from chatgpt on how to tackle it and opted to create the boundaries and definedboundaries classes to "border"
                the points and not be random in the mediterranean. Also considered some countries in Europe and not all.*/

                System.out.println("Generating additional accumulation sites..");

                int extra_sites = accumulation_sites - records.size();
                List<Boundaries> new_area = DefinedBoundaries.getBoundaries();
                Random rand = new Random();

                for (int i = 0; i < extra_sites; i++) {

                    Boundaries boundary = new_area.get(rand.nextInt(new_area.size()));

                    double latitude = boundary.getLatitude_start()
                            + (boundary.getLatitude_end() - boundary.getLatitude_start()) * rand.nextDouble();

                    double longitude = boundary.getLongitude_start()
                            + (boundary.getLongitude_end() - boundary.getLongitude_start()) * rand.nextDouble();

                    records.add(new Records(latitude, longitude));
                }
            }

            records = new ArrayList<>(records.subList(0, accumulation_sites));
        }

        long start_time = System.currentTimeMillis(); /* Starting my timer here so that it only calculates
                the time it took to run kmeans and excludes the time it took the user to give input */

        Kmeans kmeans = new Kmeans(records, k);
        kmeans.Assign_Cluster(new EucDistance());


        if (rank == MASTER) {

            List<Clusters> clusters = kmeans.getClusters();

            System.out.println("\nClusters information:");

            for (int i = 0; i < clusters.size(); i++) {

                Clusters cluster = clusters.get(i);

                System.out.printf("Cluster %d Centre: (%.6f, %.6f) with %d records.%n", i + 1, cluster.getCentre_latitude(), cluster.getCentre_longitude(), cluster.getRecords().size());
            }

            JFrame mapFrame = Map.createmap(clusters);

            mapFrame.setSize(WIDTH, HEIGHT);
            mapFrame.setVisible(true);

            long end_time = System.currentTimeMillis() - start_time;
            System.out.println("\nProgram total run time: " + end_time + "ms");

        }

        MPI.Finalize();
    }
}