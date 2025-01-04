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
                if (args[i].contains("=")) {
                    mapNumber = args[i].split("=")[1];
                }
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
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

        GraphicsDevice[] devices = ge.getScreenDevices();

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
            showError(e);
        }
    }

    private static void showError(Exception e) {
        JOptionPane.showMessageDialog(null, "Failed to set FlatDarkLaf look and feel");
        if (e != null) {
            Logger logger = Logger.getLogger(Main.class.getName());
            logger.log(Level.SEVERE, "Failed to set FlatDarkLaf look and feel", e);
        }
    }

    public static void restartApplication(int mapID) throws IOException {
        String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";

        File currentFile = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        System.out.println("Current file: " + currentFile);

        if (!currentFile.exists()) {
            throw new IOException("Unable to locate the running application file.");
        }

        ArrayList<String> command = new ArrayList<>();
        command.add(javaBin);

        if (currentFile.getName().endsWith(".jar")) {
            command.add("-jar");
            command.add(currentFile.getPath());
            command.add("--map=" + mapID);
        } else {
            command.add("-cp");
            command.add(currentFile.getPath());
            command.add(Main.class.getName());
            command.add("--map=" + mapID);
        }

        ProcessBuilder processBuilder = new ProcessBuilder(command.toArray(new String[0]))
                .inheritIO();
        processBuilder.start();

        System.exit(0);
    }
}