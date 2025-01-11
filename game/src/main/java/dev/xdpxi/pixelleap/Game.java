package dev.xdpxi.pixelleap;

import dev.xdpxi.pixelleap.Entities.Player;
import dev.xdpxi.pixelleap.GUI.PauseMenu;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL46.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Game {
    private static final double ORTHO_NEAR = -1.0;
    private static final double ORTHO_FAR = 1.0;
    public static long window;
    private final float zoom = 1f;
    private float cameraX = 0f;
    private float cameraY = 0f;

    public static void main(String[] args) {
        new Game().run("map1", Maps.currentMap);
    }

    public void run(String mapID, int mapNumber) {
        Maps.currentMap = mapNumber;
        if (mapID == null) {
            System.exit(1);
        }
        Maps.platforms = Maps.getMap(mapID);

        init();
        loop();

        glfwDestroyWindow(window);
        glfwTerminate();
        try (GLFWErrorCallback callback = glfwSetErrorCallback(null)) {
            if (callback != null) {
                callback.free();
            }
        }
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) {
            throw new IllegalStateException("Failed to initialize GLFW");
        }

        try {
            glfwDefaultWindowHints();
            glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
            glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

            window = glfwCreateWindow(Main.width, Main.height, "Pixelbound", NULL, NULL);
            if (window == NULL) {
                throw new RuntimeException("Failed to create the GLFW window");
            }

            glfwMakeContextCurrent(window);

            GL.createCapabilities();
            glfwSwapInterval(1); // VSync

            glfwShowWindow(window);

            setupProjectionMatrix();
        } catch (Exception e) {
            glfwTerminate();
            throw new RuntimeException("Initialization failed: " + e.getMessage(), e);
        }
    }

    private void setupProjectionMatrix() {
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, Main.width / zoom, 0, Main.height / zoom, ORTHO_NEAR, ORTHO_FAR);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
    }

    private void loop() {
        while (!glfwWindowShouldClose(window)) {
            glfwPollEvents();
            if (PauseMenu.isPaused.get()) continue;

            update();
            render();
            glfwSwapBuffers(window);
        }
    }

    private void update() {
        Player.handleMovement();
        Player.handlePlatforms();
        Player.velocityY -= 0.5f; // gravity
        Player.Y += Player.velocityY;

        if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS) {
            PauseMenu.show(window);
        }

        Player.isGrounded = false;
        for (Platform platform : Maps.platforms) {
            if (Player.checkCollision(platform)) {
                Player.Y = platform.y() + platform.height();
                Player.velocityY = 0;
                Player.isGrounded = true;
            }
        }

        if (Player.Y < 0) {
            Player.Y = 0;
            Player.velocityY = 0;
            Player.isGrounded = true;
        }

        cameraX += (Player.X - cameraX - (Main.width / (2 * zoom))) * 0.1f;
        cameraY += (Player.Y - cameraY - (Main.height / (2 * zoom))) * 0.1f;
    }


    private void render() {
        glClearColor(0.5f, 0.7f, 1.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT);

        glLoadIdentity();
        glScalef(zoom, zoom, 1);
        glTranslatef(-cameraX, -cameraY, 0);

        drawRect(Player.X, Player.Y, Player.Width, Player.Height, "#FF3131");

        for (Platform platform : Maps.platforms) {
            drawRect(platform.x(), platform.y(), platform.width(), platform.height(), platform.color());
        }
    }

    private void drawRect(float x, float y, float width, float height, String color) {
        if (isRectVisible(x, y, width, height)) {
            float[] rgb = hexToRGB(color);
            float[] darkerRgb = {Math.max(rgb[0] * 0.8f, 0), Math.max(rgb[1] * 0.8f, 0), Math.max(rgb[2] * 0.8f, 0)};
            drawQuad(x, y, width, height, darkerRgb);
            drawQuad(x + 4, y + 4, width - 8, height - 8, rgb);
        }
    }

    public static float[] hexToRGB(String hex) {
        int r = Integer.parseInt(hex.substring(1, 3), 16);
        int g = Integer.parseInt(hex.substring(3, 5), 16);
        int b = Integer.parseInt(hex.substring(5, 7), 16);
        return new float[]{r / 255f, g / 255f, b / 255f};
    }

    private boolean isRectVisible(float x, float y, float width, float height) {
        return (x + width >= cameraX - Main.width / zoom) && (x <= cameraX + Main.width / zoom) &&
                (y + height >= cameraY - Main.height / zoom) && (y <= cameraY + Main.height / zoom);
    }

    private void drawQuad(float x, float y, float width, float height, float[] color) {
        glBegin(GL_QUADS);
        glColor3f(color[0], color[1], color[2]);
        glVertex2f(x, y);
        glVertex2f(x + width, y);
        glVertex2f(x + width, y + height);
        glVertex2f(x, y + height);
        glEnd();
    }
}