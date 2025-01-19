package dev.xdpxi.pixelleap;

import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_MAXIMIZED;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex2f;
import static org.lwjgl.system.MemoryUtil.NULL;

import dev.xdpxi.pixelleap.Entities.Player;
import dev.xdpxi.pixelleap.Util.Log;

public class Game {
    private static final double ORTHO_NEAR = -1.0;
    private static final double ORTHO_FAR = 1.0;
    public static long window;
    private final float zoom = 1f;
    private float cameraX = 0f;
    private float cameraY = 0f;

    public static void main(String[] args) {
        if (System.getProperty("os.name").toLowerCase().contains("mac") && !System.getProperty("java.vm.name").toLowerCase().contains("openjdk")) {
            Log.error("On macOS, this application must be run with -XstartOnFirstThread JVM argument");
            System.exit(1);
        }
        Log.info("Game main method called");
        new Game().run("map1", Maps.currentMap);
    }

    public void run(String mapID, int mapNumber) {
        Log.info("Starting game with mapID: " + mapID + ", mapNumber: " + mapNumber);
        Maps.currentMap = mapNumber;
        if (mapID == null) {
            Log.error("MapID is null. Exiting game.");
            System.exit(1);
        }
        Maps.platforms = Maps.getMap(mapID);

        init();
        loop();

        Log.info("Game loop ended. Cleaning up resources.");
        glfwDestroyWindow(window);
        glfwTerminate();
        try (GLFWErrorCallback callback = glfwSetErrorCallback(null)) {
            if (callback != null) {
                callback.free();
            }
        }
        Log.info("Game resources cleaned up. Exiting.");
    }

    private void init() {
        Log.info("Initializing game");
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) {
            Log.error("Failed to initialize GLFW");
            throw new IllegalStateException("Failed to initialize GLFW");
        }

        try {
            Log.info("Setting up GLFW window hints");
            glfwDefaultWindowHints();
            glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
            glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

            Log.info("Creating GLFW window");
            window = glfwCreateWindow(Main.width, Main.height, "Pixelbound", NULL, NULL);
            if (window == NULL) {
                Log.error("Failed to create the GLFW window");
                throw new RuntimeException("Failed to create the GLFW window");
            }

            glfwMakeContextCurrent(window);

            Log.info("Creating OpenGL capabilities");
            GL.createCapabilities();
            glfwSwapInterval(1);

            glfwShowWindow(window);

            Log.info("Setting up projection matrix");
            setupProjectionMatrix();
            Log.info("Game initialization completed successfully");
        } catch (IllegalStateException e) {
            Log.error("GLFW initialization failed: " + e.getMessage(), e);
            throw e;
        } catch (RuntimeException e) {
            Log.error("Window creation failed: " + e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            Log.error("Unexpected error during initialization: " + e.getMessage(), e);
            throw new RuntimeException("Initialization failed: " + e.getMessage(), e);
        } finally {
            if (window == NULL) {
                glfwTerminate();
            }
        }
    }

    private void setupProjectionMatrix() {
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, Main.width / zoom, 0, Main.height / zoom, ORTHO_NEAR, ORTHO_FAR);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        Log.info("Projection matrix set up with dimensions: " + Main.width + "x" + Main.height + ", zoom: " + zoom);
    }

    private void loop() {
        Log.info("Entering game loop");
        while (!glfwWindowShouldClose(window)) {
            glfwPollEvents();

            update();
            render();
            glfwSwapBuffers(window);
        }
        Log.info("Game loop ended");
    }

    private void update() {
        Player.handleMovement();
        Player.handlePlatforms();
        Player.velocityY -= 0.5f;
        Player.Y += Player.velocityY;

        Player.isGrounded = false;
        for (Maps.Platform platform : Maps.platforms) {
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

        drawRect(Player.X, Player.Y, Player.WIDTH, Player.HEIGHT, "#FF3131");

        for (Maps.Platform platform : Maps.platforms) {
            if (isRectVisible(platform.x(), platform.y(), platform.width(), platform.height())) {
                drawRect(platform.x(), platform.y(), platform.width(), platform.height(), platform.color());
            }
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