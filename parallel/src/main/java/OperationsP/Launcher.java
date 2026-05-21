package OperationsP;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Launcher {
// I used chatgpt here because I did not know how to provide an option to run from different modules
    public static void main(String[] args) {

        JFrame frame = new JFrame("Select Mode");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setLayout(new BorderLayout());

        JLabel label = new JLabel("Choose a mode to run K-Means:", SwingConstants.CENTER);
        frame.add(label, BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));

        JButton sequentialButton = new JButton("Sequential");
        JButton parallelButton = new JButton("Parallel");
        JButton distributedButton = new JButton("Distributed");

        panel.add(sequentialButton);
        panel.add(parallelButton);
        panel.add(distributedButton);

        frame.add(panel, BorderLayout.CENTER);

        sequentialButton.addActionListener(e ->
        { frame.dispose();
            try {
                OperationsS.Main.main(new String[]{});
            } catch (Exception ex) { showError(ex); }
        });

        parallelButton.addActionListener(e ->
        { frame.dispose();
            try {
                OperationsP.Main.main(new String[]{});
            } catch (Exception ex) { showError(ex); }
        });

        distributedButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                    frame,
                    "Please read the distributed section in the report for guidelines on how to run it.",
                    "Distributed Mode Info",
                    JOptionPane.INFORMATION_MESSAGE
            );

            frame.dispose();
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void showError(Exception ex) {
        ex.printStackTrace();

        JOptionPane.showMessageDialog(
                null,
                "Error: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}