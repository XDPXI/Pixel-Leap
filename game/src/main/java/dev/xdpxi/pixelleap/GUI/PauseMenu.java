package dev.xdpxi.pixelleap.GUI;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class PauseMenu {
    public static final AtomicBoolean isPaused = new AtomicBoolean(false);
    private static final JFrame fakeFrame = new JFrame();
    private static JDialog pauseDialog;

    public static void show(long window) {
        isPaused.set(true);
        if (pauseDialog == null) {
            setup();
        }
        pauseDialog.setVisible(true);
    }

    private static void setup() {
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
        contentPanel.add(new JLabel("dev.xdpxi.pixelleap.Game Paused", SwingConstants.CENTER));
        contentPanel.add(createButton("Resume", PauseMenu::hide));
        contentPanel.add(createButton("Exit dev.xdpxi.pixelleap.Game", () -> System.exit(1)));
        pauseDialog.add(contentPanel);
        pauseDialog.toFront();
        pauseDialog.requestFocus();
    }

    private static JButton createButton(String text, Runnable action) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));
        button.setBackground(Color.DARK_GRAY);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> action.run());
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
        if (pauseDialog != null) {
            pauseDialog.dispose();
            isPaused.set(false);
        }
    }
}