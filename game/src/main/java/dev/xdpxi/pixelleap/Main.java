package dev.xdpxi.pixelleap;

import com.formdev.flatlaf.FlatDarkLaf;
import dev.xdpxi.pixelleap.Util.Log;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static int width = 1920;
    public static int height = 1080;

    public static void main(String[] args) {
        Log.info("Application started");
        String mapNumber = null;

        Log.info("Parsing command line arguments");
        for (int i = 0; i < args.length; i++) {
            if ("--map".equals(args[i]) || args[i].startsWith("--map=")) {
                if (args[i].contains("=")) {
                    mapNumber = args[i].split("=")[1];
                } else if (i + 1 < args.length) {
                    mapNumber = args[i + 1];
                }
                Log.info("Map number specified: " + mapNumber);
                break;
            }
        }

        Log.info("Retrieving screen dimensions");
        getWidthAndHeight();

        Log.info("Setting look and feel");
        setLookAndFeel();

        if (mapNumber != null) {
            Log.info("Starting game with map: " + mapNumber);
            new Game().run("map" + mapNumber, Integer.parseInt(mapNumber));
        } else {
            Log.info("No map selected. Starting game with default map: map1");
            new Game().run("map1", Maps.currentMap);
        }
    }

    private static void getWidthAndHeight() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] devices = ge.getScreenDevices();

        Log.info("Detected " + devices.length + " monitor(s)");
        for (int i = 0; i < devices.length; i++) {
            DisplayMode displayMode = devices[i].getDisplayMode();
            Log.info("Monitor " + (i + 1) + ":");
            Log.info("Width: " + displayMode.getWidth() + " px");
            Log.info("Height: " + displayMode.getHeight() + " px");

            width = displayMode.getWidth();
            height = displayMode.getHeight();
        }
        Log.info("Using dimensions: " + width + "x" + height);
    }

    private static void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
            Log.info("FlatDarkLaf look and feel set successfully");
        } catch (UnsupportedLookAndFeelException e) {
            Log.error("Failed to set FlatDarkLaf look and feel");
            showError(e);
        }
    }

    private static void showError(Exception e) {
        JOptionPane.showMessageDialog(null, "Failed to set FlatDarkLaf look and feel");
        if (e != null) {
            Log.error("Failed to set FlatDarkLaf look and feel", e);
        }
    }

    public static void restartApplication(int mapID) throws IOException {
        Log.info("Restarting application with map ID: " + mapID);
        String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";

        File currentFile = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        Log.info("Current file: " + currentFile);

        if (!currentFile.exists()) {
            Log.error("Unable to locate the running application file.");
            throw new IOException("Unable to locate the running application file.");
        }

        ArrayList<String> command = new ArrayList<>();
        command.add(javaBin);

        if (currentFile.getName().endsWith(".jar")) {
            Log.info("Running from JAR file");
            command.add("-jar");
            command.add(currentFile.getPath());
            command.add("--map=" + mapID);
        } else {
            Log.info("Running from class files");
            command.add("-cp");
            command.add(currentFile.getPath());
            command.add(Main.class.getName());
            command.add("--map=" + mapID);
        }

        Log.info("Executing command: " + String.join(" ", command));
        ProcessBuilder processBuilder = new ProcessBuilder(command.toArray(new String[0]))
                .inheritIO();
        processBuilder.start();

        Log.info("New process started. Exiting current process.");
        System.exit(0);
    }
}