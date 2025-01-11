import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class Main {
    private static JFrame frame;
    private static JProgressBar progressBar;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            Log.error("Failed to set Look and Feel", e);
        }

        if (!isSupportedOperatingSystem()) {
            JOptionPane.showMessageDialog(null,
                    "Your operating system is not supported!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        initUI();
    }

    private static boolean isSupportedOperatingSystem() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("windows");
    }

    private static void initUI() {
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Pixel Leap - Launcher");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(325, 175);
            frame.setLayout(new BorderLayout());
            frame.setLocationRelativeTo(null);

            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JComboBox<String> versionDropdown = createVersionDropdown();

            JButton playButton = createPlayButton(versionDropdown);

            progressBar = new JProgressBar(0, 100);
            progressBar.setStringPainted(true);
            progressBar.setVisible(false);

            mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            mainPanel.add(versionDropdown);
            mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            mainPanel.add(playButton);
            mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            mainPanel.add(progressBar);

            frame.add(mainPanel, BorderLayout.CENTER);
            frame.setVisible(true);
        });
    }

    private static JComboBox<String> createVersionDropdown() {
        JComboBox<String> versionDropdown = new JComboBox<>();
        versionDropdown.setAlignmentX(Component.CENTER_ALIGNMENT);
        versionDropdown.setMaximumSize(new Dimension(200, 25));
    
        new Thread(() -> {
            ArrayList<String> versions = fetchVersions();
            SwingUtilities.invokeLater(() -> {
                if (!versions.isEmpty()) {
                    versions.forEach(versionDropdown::addItem);
                } else {
                    versionDropdown.addItem("Error fetching versions");
                }
            });
        }).start();
    
        return versionDropdown;
    }

    private static JButton createPlayButton(JComboBox<String> versionDropdown) {
        JButton playButton = new JButton("Play");
        playButton.setFont(new Font("Arial", Font.BOLD, 14));
        playButton.setFocusPainted(false);
        playButton.setBackground(new Color(70, 73, 75));
        playButton.setForeground(Color.WHITE);
        playButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        playButton.addActionListener((ActionEvent e) -> {
            String selectedVersion = (String) versionDropdown.getSelectedItem();
            if (selectedVersion != null && !selectedVersion.equals("Error fetching versions")) {
                String downloadURL = "https://raw.githubusercontent.com/XDPXI/Pixel-Leap/refs/heads/main/game/builds/" + selectedVersion + ".jar";
                progressBar.setVisible(true);
                downloadAndRunGame(downloadURL, selectedVersion);
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid version selected.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return playButton;
    }

    private static void downloadAndRunGame(String fileURL, String versionName) {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                HttpURLConnection connection = null;
                try {
                    File gameFile = prepareGameFile(versionName);
                    if (gameFile.exists()) {
                        Log.info("File already exists: " + gameFile.getAbsolutePath());
                        runGame(gameFile);
                        return null;
                    }

                    connection = (HttpURLConnection) new URI(fileURL).toURL().openConnection();
                    connection.setRequestMethod("GET");

                    int contentLength = connection.getContentLength();
                    if (contentLength <= 0) throw new IOException("Failed to retrieve file size.");

                    downloadFile(connection, gameFile, contentLength);
                    runGame(gameFile);
                } catch (IOException | URISyntaxException e) {
                    Log.error("Error downloading or running game", e);
                    SwingUtilities.invokeLater(() -> showErrorDialog("Failed to download or run the game. Please check your connection and try again."));
                } finally {
                    if (connection != null) connection.disconnect();
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

    private static File prepareGameFile(String versionName) throws IOException {
        String userHome = System.getProperty("user.home");
        File downloadDir = new File(userHome, "AppData/Roaming/PixelLeap");

        if (!downloadDir.exists() && !downloadDir.mkdirs()) {
            throw new IOException("Failed to create directory: " + downloadDir.getAbsolutePath());
        }

        return new File(downloadDir, "game-" + versionName + ".jar");
    }

    private static void downloadFile(HttpURLConnection connection, File gameFile, int contentLength) throws IOException {
        try (InputStream in = connection.getInputStream(); FileOutputStream out = new FileOutputStream(gameFile)) {
            byte[] buffer = new byte[4096];
            int totalRead = 0, bytesRead;

            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                totalRead += bytesRead;

                int progress = (int) ((totalRead / (double) contentLength) * 100);
                SwingUtilities.invokeLater(() -> progressBar.setValue(progress));
            }
        }
    }

    private static void runGame(File gameFile) {
        try {
            new ProcessBuilder("java", "-jar", gameFile.getAbsolutePath()).start();
            SwingUtilities.invokeLater(() -> frame.setState(Frame.ICONIFIED));
        } catch (IOException e) {
            Log.error("Failed to run the game", e);
            SwingUtilities.invokeLater(() -> showErrorDialog("Failed to run the game. Please check your Java installation."));
        }
    }

    private static void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private static ArrayList<String> fetchVersions() {
        String urlString = "https://raw.githubusercontent.com/XDPXI/Pixel-Leap/refs/heads/main/game/builds/builds";
        ArrayList<String> versionList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URI(urlString).toURL().openStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim().replace("\"", "").replace(",", "");
                if (!trimmed.isEmpty()) versionList.add(trimmed);
            }
        } catch (Exception e) {
            Log.error("Error fetching versions", e);
        }

        return versionList;
    }
}