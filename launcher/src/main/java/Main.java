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
    private static final String GAME_DOWNLOAD_DIR = "AppData/Roaming/PixelLeap";
    private static final String VERSION_URL = "https://raw.githubusercontent.com/XDPXI/Pixel-Leap/refs/heads/main/game/builds/builds";
    private static final String GAME_BASE_URL = "https://raw.githubusercontent.com/XDPXI/Pixel-Leap/refs/heads/main/game/builds/";
    private static JFrame frame;
    private static JProgressBar progressBar;
    private static JButton playButton;

    public static void main(String[] args) {
        setLookAndFeel();
        if (!isSupportedOperatingSystem()) {
            showErrorDialog("Your operating system is not supported!");
            System.exit(1);
        }
        initUI();
    }

    private static void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            Log.error("Error setting look and feel", e);
        }
    }

    private static boolean isSupportedOperatingSystem() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("windows");
    }

    private static void initUI() {
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Pixel Leap - Launcher");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(330, 180);
            frame.setLayout(new BorderLayout());
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);

            JPanel mainPanel = createMainPanel();
            frame.add(mainPanel, BorderLayout.CENTER);
            frame.setVisible(true);
        });
    }

    private static JPanel createMainPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JComboBox<String> versionDropdown = createVersionDropdown();
        JButton playButton = createPlayButton(versionDropdown);
        progressBar = createProgressBar();

        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(versionDropdown);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(playButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(progressBar);

        return mainPanel;
    }

    private static JComboBox<String> createVersionDropdown() {
        JComboBox<String> versionDropdown = new JComboBox<>();
        versionDropdown.setAlignmentX(Component.CENTER_ALIGNMENT);
        versionDropdown.setMaximumSize(new Dimension(250, 30));

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
        playButton = new JButton("Play");
        playButton.setFont(new Font("Arial", Font.BOLD, 14));
        playButton.setFocusPainted(false);
        playButton.setBackground(new Color(70, 73, 75));
        playButton.setForeground(Color.WHITE);
        playButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        playButton.setPreferredSize(new Dimension(120, 40));

        playButton.addActionListener((ActionEvent e) -> {
            String selectedVersion = (String) versionDropdown.getSelectedItem();
            if (selectedVersion != null && !selectedVersion.equals("Error fetching versions")) {
                String downloadURL = GAME_BASE_URL + selectedVersion + ".jar";
                progressBar.setVisible(true);
                playButton.setVisible(false);
                downloadAndRunGame(downloadURL, selectedVersion);
            } else {
                showErrorDialog("Invalid version selected.");
            }
        });

        return playButton;
    }

    private static JProgressBar createProgressBar() {
        JProgressBar bar = new JProgressBar(0, 100);
        bar.setStringPainted(true);
        bar.setVisible(false);
        return bar;
    }

    private static void downloadAndRunGame(String fileURL, String versionName) {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try {
                    File gameFile = prepareGameFile(versionName);
                    if (gameFile.exists()) {
                        runGame(gameFile);
                        return null;
                    }

                    downloadFile(fileURL, gameFile);
                    runGame(gameFile);
                } catch (IOException | URISyntaxException e) {
                    showErrorDialog("Failed to download or run the game. Please check your connection and try again.");
                }
                return null;
            }

            @Override
            protected void done() {
                progressBar.setVisible(false);
                playButton.setVisible(true);
            }
        };

        worker.execute();
    }

    private static File prepareGameFile(String versionName) throws IOException {
        File downloadDir = new File(System.getProperty("user.home"), GAME_DOWNLOAD_DIR);
        if (!downloadDir.exists() && !downloadDir.mkdirs()) {
            throw new IOException("Failed to create directory: " + downloadDir.getAbsolutePath());
        }
        return new File(downloadDir, "game-" + versionName + ".jar");
    }

    private static void downloadFile(String fileURL, File gameFile) throws IOException, URISyntaxException {
        HttpURLConnection connection = (HttpURLConnection) new URI(fileURL).toURL().openConnection();
        connection.setRequestMethod("GET");

        int contentLength = connection.getContentLength();
        if (contentLength <= 0) throw new IOException("Failed to retrieve file size.");

        try (InputStream in = connection.getInputStream(); FileOutputStream out = new FileOutputStream(gameFile)) {
            byte[] buffer = new byte[4096];
            int totalRead = 0, bytesRead;

            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                totalRead += bytesRead;

                int progress = (int) ((totalRead / (double) contentLength) * 100);
                SwingUtilities.invokeLater(() -> progressBar.setValue(progress));
            }
        } finally {
            connection.disconnect();
        }
    }

    private static void runGame(File gameFile) {
        try {
            new ProcessBuilder("java", "-jar", gameFile.getAbsolutePath()).start();
            frame.setState(Frame.ICONIFIED);
        } catch (IOException e) {
            showErrorDialog("Failed to run the game. Please check your Java installation.");
        }
    }

    private static void showErrorDialog(String message) {
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE));
    }

    private static ArrayList<String> fetchVersions() {
        ArrayList<String> versionList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URI(VERSION_URL).toURL().openStream()))) {
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