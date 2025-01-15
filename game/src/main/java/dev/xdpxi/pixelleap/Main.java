package dev.xdpxi.pixelleap;

import com.formdev.flatlaf.FlatDarkLaf;
import dev.xdpxi.pixelleap.Util.Log;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    public static int width = 1920;
    public static int height = 1080;

    public static void main(String[] args) {
        Log.info("Application started");

        String mapNumber = parseMapNumber(args);
        getWidthAndHeight();
        setLookAndFeel();

        if (mapNumber != null) {
            Log.info("Starting game with map: " + mapNumber);
            new Game().run("map" + mapNumber, Integer.parseInt(mapNumber));
        } else {
            Log.info("No map selected. Starting game with default map: map1");
            new Game().run("map1", Maps.currentMap);
        }
    }

    private static String parseMapNumber(String[] args) {
        Log.info("Parsing command line arguments");
        return Arrays.stream(args)
                .filter(arg -> arg.startsWith("--map"))
                .findFirst()
                .map(arg -> arg.contains("=") ? arg.split("=")[1] : args[Arrays.asList(args).indexOf(arg) + 1])
                .orElse(null);
    }

    private static void getWidthAndHeight() {
        Log.info("Retrieving screen dimensions");
        GraphicsDevice defaultScreen = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        DisplayMode displayMode = defaultScreen.getDisplayMode();
        width = displayMode.getWidth();
        height = displayMode.getHeight();
        Log.info("Using dimensions: " + width + "x" + height);
    }

    private static void setLookAndFeel() {
        Log.info("Setting look and feel");
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
            Log.info("FlatDarkLaf look and feel set successfully");
        } catch (UnsupportedLookAndFeelException e) {
            Log.error("Failed to set FlatDarkLaf look and feel", e);
            JOptionPane.showMessageDialog(null, "Failed to set FlatDarkLaf look and feel");
        }
    }

    public static void restartApplication(int mapID) throws IOException {
        Log.info("Restarting application with map ID: " + mapID);
        String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";

        File currentFile = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        Log.info("Current file: " + currentFile);

        if (!currentFile.exists()) {
            throw new IOException("Unable to locate the running application file.");
        }

        ArrayList<String> command = new ArrayList<>();
        command.add(javaBin);

        if (currentFile.getName().endsWith(".jar")) {
            command.addAll(Arrays.asList("-jar", currentFile.getPath(), "--map=" + mapID));
        } else {
            command.addAll(Arrays.asList("-cp", currentFile.getPath(), Main.class.getName(), "--map=" + mapID));
        }

        Log.info("Executing command: " + String.join(" ", command));
        new ProcessBuilder(command).inheritIO().start();

        Log.info("New process started. Exiting current process.");
        System.exit(0);
    }
}