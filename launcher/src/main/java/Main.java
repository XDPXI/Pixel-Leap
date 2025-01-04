import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

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
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Failed to set Look and Feel", e);
        }

        if (!isSupportedOperatingSystem()) {
            JOptionPane.showMessageDialog(null, "Your operating system is not supported!", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        init();
    }

    private static void init() {
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Pixel Leap - Launcher");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(325, 175);
            frame.setLayout(new BorderLayout());
            frame.setState(Frame.NORMAL);

            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JComboBox<String> versionDropdown = new JComboBox<>();
            versionDropdown.setAlignmentX(Component.CENTER_ALIGNMENT);
            versionDropdown.setMaximumSize(new Dimension(200, 25));

            new Thread(() -> {
                ArrayList<String> versions = fetchVersions();
                if (versions != null) {
                    SwingUtilities.invokeLater(() -> {
                        for (String version : versions) {
                            versionDropdown.addItem(version);
                        }
                    });
                } else {
                    SwingUtilities.invokeLater(() -> {
                        versionDropdown.addItem("Error fetching versions");
                    });
                }
            }).start();

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
                String downloadURL = "https://raw.githubusercontent.com/XDPXI/Pixel-Leap/refs/heads/main/game/builds/" + selectedVersion + ".jar";

                progressBar.setVisible(true);
                downloadAndRunGame(downloadURL, selectedVersion);
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

    private static void downloadAndRunGame(String fileURL, String versionName) {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                HttpURLConnection connection = null;
                try {
                    String userHome = System.getProperty("user.home");
                    File downloadDir = new File(userHome, "AppData/Roaming/PixelLeap");

                    if (!downloadDir.exists() && !downloadDir.mkdirs()) {
                        throw new IOException("Failed to create directory: " + downloadDir.getAbsolutePath());
                    }

                    File gameFile = new File(downloadDir, "game-" + versionName + ".jar");

                    if (gameFile.exists()) {
                        System.out.println("File already exists: " + gameFile.getAbsolutePath());
                        runGame(gameFile);
                        return null;
                    }

                    connection = (HttpURLConnection) new URI(fileURL).toURL().openConnection();
                    connection.setRequestMethod("GET");

                    int contentLength = connection.getContentLength();
                    if (contentLength == -1) {
                        throw new IOException("Failed to retrieve file size.");
                    }

                    int totalRead = 0;

                    try (InputStream in = connection.getInputStream();
                         FileOutputStream out = new FileOutputStream(gameFile)) {

                        byte[] buffer = new byte[4096];
                        int bytesRead;

                        while ((bytesRead = in.read(buffer)) != -1) {
                            out.write(buffer, 0, bytesRead);
                            totalRead += bytesRead;

                            int progress = (int) ((totalRead / (double) contentLength) * 100);
                            SwingUtilities.invokeLater(() -> progressBar.setValue(progress));
                        }
                    }

                    System.out.println("Download complete: " + gameFile.getAbsolutePath());

                    runGame(gameFile);
                } catch (IOException | URISyntaxException e) {
                    // Use a logger instead of printStackTrace
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Error downloading or running game", e);
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(frame,
                            "Failed to download or run the game. Please check your connection and try again.",
                            "Error", JOptionPane.ERROR_MESSAGE));
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
                return null;
            }

            @Override
            protected void done() {
                SwingUtilities.invokeLater(() -> progressBar.setVisible(false));
            }
        };

        worker.execute();
    }

    private static void runGame(File gameFile) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", gameFile.getAbsolutePath());
            processBuilder.start();

            SwingUtilities.invokeLater(() -> frame.setState(Frame.ICONIFIED));
        } catch (IOException e) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Failed to run the game", e);
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(frame,
                    "Failed to run the game. Please check your Java installation.",
                    "Error", JOptionPane.ERROR_MESSAGE));
        }
    }

    private static ArrayList<String> fetchVersions() {
        String urlString = "https://raw.githubusercontent.com/XDPXI/Pixel-Leap/refs/heads/main/game/builds/builds";
        ArrayList<String> versionList = new ArrayList<>();
        try {
            URI uri = new URI(urlString);
            HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
            connection.setRequestMethod("GET");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim().replace("\"", "").replace(",", "");
                    if (!line.isEmpty()) {
                        versionList.add(line);
                    }
                }
            }
        } catch (Exception e) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Error fetching versions", e);
            return null;
        }
        return versionList;
    }
}