import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static int width = 1920;
    public static int height = 1080;

    public static void main(String[] args) {
        String mapNumber = null;

        for (int i = 0; i < args.length; i++) {
            if ("--map".equals(args[i]) || args[i].startsWith("--map=")) {
                // If it's the equals format (e.g., --map=2)
                if (args[i].contains("=")) {
                    mapNumber = args[i].split("=")[1];  // Split at '=' and extract map number
                }
                // If it's the space format (e.g., --map 2)
                else if (i + 1 < args.length) {
                    mapNumber = args[i + 1];
                }
                break;
            }
        }

        getWidthAndHeight();
        setLookAndFeel();

        if (mapNumber != null) {
            System.out.println("Starting game with map: " + mapNumber);
            new Game().run("map" + mapNumber, Integer.parseInt(mapNumber));
        } else {
            System.out.println("No Selected Map Selected. Starting game with default map: map1");
            new Game().run("map1", Game.currentMap);
        }
    }

    private static void getWidthAndHeight() {
        // Get the local graphics environment
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

        // Get the array of graphics devices (monitors)
        GraphicsDevice[] devices = ge.getScreenDevices();

        // Iterate through each monitor and get its dimensions
        for (int i = 0; i < devices.length; i++) {
            DisplayMode displayMode = devices[i].getDisplayMode();
            System.out.println("Monitor " + (i + 1) + ":");
            System.out.println("Width: " + displayMode.getWidth() + " px");
            System.out.println("Height: " + displayMode.getHeight() + " px");
            System.out.println();

            width = displayMode.getWidth();
            height = displayMode.getHeight();
        }
    }

    private static void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (UnsupportedLookAndFeelException e) {
            showError("Failed to set FlatDarkLaf look and feel", e);
        }
    }

    private static void showError(String message, Exception e) {
        JOptionPane.showMessageDialog(null, message);
        if (e != null) {
            Logger logger = Logger.getLogger(Main.class.getName());
            logger.log(Level.SEVERE, message, e);
        }
    }

    public static void restartApplication(int mapID) throws IOException {
        String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";

        // Get the current file path
        File currentFile = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        System.out.println("Current file: " + currentFile);

        if (!currentFile.exists()) {
            throw new IOException("Unable to locate the running application file.");
        }

        // Prepare the command
        ArrayList<String> command = new ArrayList<>();
        command.add(javaBin);

        // Handle .jar and non-jar scenarios
        if (currentFile.getName().endsWith(".jar")) {
            command.add("-jar");
            command.add(currentFile.getPath());
            command.add("--map=" + mapID);  // Pass mapID as an argument
        } else {
            command.add("-cp");
            command.add(currentFile.getPath());
            command.add(Main.class.getName());
            command.add("--map=" + mapID);  // Pass mapID as an argument
        }

        // Start the process
        ProcessBuilder processBuilder = new ProcessBuilder(command.toArray(new String[0]))
                .inheritIO();  // Inherit IO to see output in the current terminal
        processBuilder.start();

        // Exit the current application
        System.exit(0);
    }
}