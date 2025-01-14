package dev.xdpxi.pixelleap.GUI;

import dev.xdpxi.pixelleap.Util.Log;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class PauseMenu {
    public static final AtomicBoolean isPaused = new AtomicBoolean(false);
    private static final JFrame fakeFrame = new JFrame();
    private static JDialog pauseDialog;

    public static void show(long window) {
        Log.info("Showing pause menu");
        isPaused.set(true);
        if (pauseDialog == null) {
            Log.warn("Pause dialog not initialized. Setting up...");
            setup();
        }
        pauseDialog.setVisible(true);
        Log.debug("Pause dialog is now visible");
    }

    private static void setup() {
        Log.info("Setting up pause menu");
        fakeFrame.setUndecorated(true);
        fakeFrame.setVisible(true);
        fakeFrame.setLocationRelativeTo(null);
        pauseDialog = new JDialog(fakeFrame, "Paused", true);
        pauseDialog.setUndecorated(true);
        pauseDialog.setSize(300, 200);
        pauseDialog.setOpacity(0.8f);
        pauseDialog.setAlwaysOnTop(true);
        pauseDialog.setLayout(new GridLayout(0, 1, 10, 10));
        pauseDialog.setLocationRelativeTo(fakeFrame);
        JPanel contentPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPanel.add(new JLabel("Game Paused", SwingConstants.CENTER));
        contentPanel.add(createButton("Resume", PauseMenu::hide));
        contentPanel.add(createButton("Exit Game", () -> {
            Log.info("User chose to exit the game");
            System.exit(1);
        }));
        pauseDialog.add(contentPanel);
        pauseDialog.toFront();
        pauseDialog.requestFocus();
        Log.info("Pause menu setup completed");
    }

    private static JButton createButton(String text, Runnable action) {
        Log.debug("Creating button: " + text);
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));
        button.setBackground(Color.DARK_GRAY);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> {
            Log.debug("Button clicked: " + text);
            action.run();
        });
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.GRAY);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.DARK_GRAY);
            }
        });
        return button;
    }

    public static void hide() {
        Log.info("Hiding pause menu");
        if (pauseDialog != null) {
            pauseDialog.dispose();
            isPaused.set(false);
            Log.info("Pause dialog disposed and game unpaused");
        } else {
            Log.warn("Attempted to hide pause menu, but it was not initialized");
        }
    }
}