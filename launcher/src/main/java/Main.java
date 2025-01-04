import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Main {
    private static JFrame frame;
    private static JProgressBar progressBar;

    private static boolean isSupportedOperatingSystem() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("windows");
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!isSupportedOperatingSystem()) {
            JOptionPane.showMessageDialog(null, "Your operating system is not supported!", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Pixel Leap - Launcher");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(325, 175);
            frame.setLayout(new BorderLayout());
            frame.setState(Frame.NORMAL);

            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JComboBox<String> versionDropdown = new JComboBox<>(
                    new String[]{
                            "Latest Build",
                            "1.0.0-Beta.4",
                            "1.0.0-Beta.2",
                            "1.0.0-Beta.1"
                    }
            );
            versionDropdown.setAlignmentX(Component.CENTER_ALIGNMENT);
            versionDropdown.setMaximumSize(new Dimension(200, 25));

            JButton playButton = new JButton("Play");
            playButton.setBounds(150, 100, 199, 40);
            playButton.setFont(new Font("Arial", Font.BOLD, 14));
            playButton.setFocusPainted(false);
            playButton.putClientProperty("JComponent.minimumWidth", 200);
            playButton.setBackground(new Color(70, 73, 75));
            playButton.setForeground(Color.WHITE);
            playButton.setAlignmentX(Component.CENTER_ALIGNMENT);

            progressBar = new JProgressBar(0, 100);
            progressBar.setStringPainted(true);
            progressBar.setVisible(false);

            playButton.addActionListener((ActionEvent e) -> {
                String selectedVersion = (String) versionDropdown.getSelectedItem();
                String downloadURL = null;

                switch (selectedVersion) {
                    case "1.0.0-Beta.1":
                        downloadURL = "https://raw.githubusercontent.com/XDPXI/Pixel-Leap/refs/heads/main/game/builds/pixelbound-1.0.0-beta.1-game.jar";
                        break;
                    case "1.0.0-Beta.2":
                        downloadURL = "https://raw.githubusercontent.com/XDPXI/Pixel-Leap/refs/heads/main/game/builds/pixelbound-1.0.0-beta.2.jar";
                        break;
                    case "1.0.0-Beta.4":
                        downloadURL = "https://raw.githubusercontent.com/XDPXI/Pixel-Leap/refs/heads/main/game/builds/Pixelbound-1.0.0-beta.4.jar";
                        break;
                    case "Latest Build":
                        downloadURL = "https://raw.githubusercontent.com/XDPXI/Pixel-Leap/refs/heads/main/game/builds/latest-build.jar";
                        break;
                    case null:
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + selectedVersion);
                }

                if (downloadURL != null) {
                    progressBar.setVisible(true);
                    downloadAndRunGame(downloadURL);
                }
            });

            mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            mainPanel.add(versionDropdown);
            mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            mainPanel.add(playButton);
            mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            mainPanel.add(progressBar);

            frame.add(mainPanel, BorderLayout.CENTER);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    private static void downloadAndRunGame(String fileURL) {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try {
                    File tempFile = File.createTempFile("game", ".jar");
                    tempFile.deleteOnExit();

                    HttpURLConnection connection = (HttpURLConnection) new URL(fileURL).openConnection();
                    connection.setRequestMethod("GET");

                    int contentLength = connection.getContentLength();
                    int totalRead = 0;

                    try (InputStream in = connection.getInputStream();
                         FileOutputStream out = new FileOutputStream(tempFile)) {

                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = in.read(buffer)) != -1) {
                            out.write(buffer, 0, bytesRead);
                            totalRead += bytesRead;
                            int progress = (int) ((totalRead / (double) contentLength) * 100);
                            progressBar.setValue(progress);
                        }
                    }

                    System.out.println("Download complete: " + tempFile.getAbsolutePath());

                    ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", tempFile.getAbsolutePath());
                    processBuilder.start();
                    frame.setState(Frame.ICONIFIED);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void done() {
                progressBar.setVisible(false);
            }
        };

        worker.execute();
    }
}