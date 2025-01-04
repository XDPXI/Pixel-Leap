import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Game {
    private static final AtomicBoolean isPaused = new AtomicBoolean(false);
    private static final JFrame fakeFrame = new JFrame();
    public static int currentMap = 1;
    public static float playerX = 100f;
    public static float playerY = 400f;
    private static JDialog pauseDialog;
    private final float playerWidth = 50f;
    private final float playerHeight = 50f;
    private final float gravity = -0.5f;
    private final float cameraSpeed = 0.1f; // Controls the smoothness of the camera follow
    private final float zoom = 1f; // Camera zoom (3x)
    private Platform[] platforms = {};
    private long window;
    private float playerVelocityY = 0f;
    private boolean isGrounded = false;
    private float cameraX = 0f;
    private float cameraY = 0f;

    public static void showPauseMenu(long window) {
        isPaused.set(true);

        if (pauseDialog == null) {
            fakeFrame.setUndecorated(true);
            fakeFrame.setVisible(true);
            fakeFrame.setLocationRelativeTo(null);

            // Create the pause menu dialog
            pauseDialog = new JDialog(fakeFrame, "Paused", true);
            pauseDialog.setUndecorated(true);
            pauseDialog.setSize(300, 200);
            pauseDialog.setOpacity(0.8f);
            pauseDialog.setAlwaysOnTop(true);
            pauseDialog.setLayout(new GridLayout(0, 1, 10, 10));

            pauseDialog.toFront();
            pauseDialog.requestFocus();

            // Center the dialog on the owner
            pauseDialog.setLocationRelativeTo(fakeFrame);

            // Add components to the pause menu
            JPanel contentPanel = new JPanel();
            contentPanel.setLayout(new GridLayout(0, 1, 10, 10));
            contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JLabel titleLabel = new JLabel("Game Paused", SwingConstants.CENTER);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

            JButton resumeButton = createCustomButton("Resume");
            resumeButton.addActionListener(e -> hidePauseMenu());

            JButton exitButton = createCustomButton("Exit Game");
            exitButton.addActionListener(e -> {
                System.exit(1);
            });

            // Add components to the content panel
            contentPanel.add(titleLabel);
            contentPanel.add(resumeButton);
            contentPanel.add(exitButton);

            pauseDialog.add(contentPanel);

            pauseDialog.toFront();
            pauseDialog.requestFocus();
        }

        pauseDialog.setVisible(true);
    }

    private static JButton createCustomButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));
        button.setBackground(Color.DARK_GRAY);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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

    public static void hidePauseMenu() {
        if (pauseDialog != null) {
            pauseDialog.dispose();
            isPaused.set(false);
        }
    }

    public void run(String mapID, int mapNumber) {
        currentMap = mapNumber;

        if (mapID == null) {
            System.exit(1);
        }
        platforms = Maps.getMap(mapID);

        init();
        loop();

        // Clean up
        glfwDestroyWindow(window);
        glfwTerminate();
        GLFWErrorCallback callback = glfwSetErrorCallback(null);
        if (callback != null) {
            callback.free();
        }
    }

    private void init() {
        // Set up error callback
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        // Create a window the size of the monitor
        window = glfwCreateWindow(Main.width, Main.height, "Pixelbound", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        glfwShowWindow(window);

        // Set up OpenGL capabilities
        GL.createCapabilities();

        // Set up 2D projection
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, Main.width / zoom, 0, Main.height / zoom, -1, 1);
        glMatrixMode(GL_MODELVIEW);
    }

    private void loop() {
        while (!glfwWindowShouldClose(window)) {
            SwingUtilities.invokeLater(() -> {
                if (fakeFrame.isAlwaysOnTop()) {
                    fakeFrame.toFront();
                }
            });

            if (isPaused.get()) {
                org.lwjgl.glfw.GLFW.glfwPollEvents();
                continue;
            }

            glfwPollEvents();
            update();
            render();
            glfwSwapBuffers(window);
        }
    }

    private float getAdjustedSpeed(float x, float y) {
        for (Platform platform : this.platforms) {
            if (checkCollision(x, y, platform) && "#FF10F0".equals(platform.color())) {
                return 10f; // Return 10f if touching the specified color
            }
        }
        return 5f; // Default speed if not touching the target color
    }

    private float getAdjustedJump(float x, float y) {
        for (Platform platform : this.platforms) {
            if (checkCollision(x, y, platform) && "#FFAA33".equals(platform.color())) {
                return 20f; // Return 10f if touching the specified color
            }
        }
        return 10f; // Default speed if not touching the target color
    }

    private void handleMovement() {
        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS || glfwGetKey(window, GLFW_KEY_RIGHT) == GLFW_PRESS) {
            playerX += getAdjustedSpeed(playerX, playerY);
        }
        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS || glfwGetKey(window, GLFW_KEY_LEFT) == GLFW_PRESS) {
            playerX -= getAdjustedSpeed(playerX, playerY);
        }
        if ((glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS || glfwGetKey(window, GLFW_KEY_UP) == GLFW_PRESS || glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS) && isGrounded) {
            playerVelocityY = getAdjustedJump(playerX, playerY);
            isGrounded = false;
        }
    }

    private void handlePlatforms() {
        if (touchingColor(playerX, playerY, "#FFAA33")) {
            playerVelocityY = getAdjustedJump(playerX, playerY);
            isGrounded = false;
        }
        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS || glfwGetKey(window, GLFW_KEY_RIGHT) == GLFW_PRESS) {
            while (touchingColor(playerX, playerY, "#7F00FF")) {
                playerX += 1;
            }
        }
        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS || glfwGetKey(window, GLFW_KEY_LEFT) == GLFW_PRESS) {
            while (touchingColor(playerX, playerY, "#7F00FF")) {
                playerX -= 1;
            }
        }
    }

    private void switchMaps() {
        switch (currentMap) {
            case 1:
                platforms = Maps.getMap("map2");
                currentMap = 2;
                playerX = 100f;
                playerY = 400f;
                break;
            case 2:
                platforms = Maps.getMap("map3");
                currentMap = 3;
                playerX = 100f;
                playerY = 400f;
                break;
        }
    }

    private void update() {
        handleMovement();
        handlePlatforms();

        if (touchingColor(playerX, playerY, "#FF2400")) {
            try {
                Main.restartApplication(currentMap);
            } catch (Exception e) {
                System.err.println("Error restarting application: " + e.getMessage());
            }
        }

        if (touchingColor(playerX, playerY, "#0FFF50")) {
            switchMaps();
        }

        if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS) {
            SwingUtilities.invokeLater(() -> showPauseMenu(window));
        }

        // Apply gravity and update vertical position
        playerVelocityY += gravity;
        playerY += playerVelocityY;

        // Check collision with platforms
        isGrounded = false;
        for (Platform platform : platforms) {
            if (checkCollision(playerX, playerY, platform)) {
                playerY = platform.y() + platform.height();
                playerVelocityY = 0;
                isGrounded = true;
            }
        }

        // Prevent player from falling below the screen
        if (playerY < 0) {
            playerY = 0;
            playerVelocityY = 0;
            isGrounded = true;
        }

        // Smoothly update camera position
        cameraX += (playerX - cameraX - (Main.width / (2 * zoom))) * cameraSpeed;
        cameraY += (playerY - cameraY - (Main.height / (2 * zoom))) * cameraSpeed;
    }

    private boolean touchingColor(float x, float y, String targetColor) {
        for (Platform platform : this.platforms) {
            if (checkCollision(x, y, platform) && targetColor.equals(platform.color())) {
                return true;
            }
        }
        return false;
    }

    private void render() {
        glClearColor(0.5f, 0.7f, 1.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT);

        // Apply camera transformations
        glLoadIdentity();
        glScalef(zoom, zoom, 1);
        glTranslatef(-cameraX, -cameraY, 0);

        // Render player
        drawRect(playerX, playerY, playerWidth, playerHeight, "#FF3131");

        // Render platforms
        for (Platform platform : platforms) {
            drawRect(platform.x(), platform.y(), platform.width(), platform.height(), platform.color());
        }
    }

    private boolean checkCollision(float x, float y, Platform platform) {
        return x + playerWidth > platform.x() && x < platform.x() + platform.width() &&
                y <= platform.y() + platform.height() && y + playerHeight >= platform.y();
    }

    private void drawRect(float x, float y, float width, float height, String color) {
        // Convert the color to RGB
        float[] rgb = hexToRGB(color);

        // Darken the color for the outline
        float[] darkerRgb = {
                Math.max(rgb[0] * 0.8f, 0), // Reduce brightness to darken
                Math.max(rgb[1] * 0.8f, 0),
                Math.max(rgb[2] * 0.8f, 0)
        };

        int outlineWidth = 4;

        // Draw the outline (slightly larger rectangle)
        glColor3f(darkerRgb[0], darkerRgb[1], darkerRgb[2]);
        glBegin(GL_QUADS);
        glVertex2f(x, y); // Expand by 2px
        glVertex2f(x + width, y);
        glVertex2f(x + width, y + height);
        glVertex2f(x, y + height);
        glEnd();

        // Draw the filled rectangle
        glColor3f(rgb[0], rgb[1], rgb[2]);
        glBegin(GL_QUADS);
        glVertex2f(x + outlineWidth, y + outlineWidth);
        glVertex2f(x + width - outlineWidth, y + outlineWidth);
        glVertex2f(x + width - outlineWidth, y + height - outlineWidth);
        glVertex2f(x + outlineWidth, y + height - outlineWidth);
        glEnd();
    }

    private float[] hexToRGB(String hexColor) {
        int r = Integer.parseInt(hexColor.substring(1, 3), 16);
        int g = Integer.parseInt(hexColor.substring(3, 5), 16);
        int b = Integer.parseInt(hexColor.substring(5, 7), 16);
        return new float[]{r / 255f, g / 255f, b / 255f};
    }
}