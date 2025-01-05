package dev.xdpxi.pixelleap.Entities;

import dev.xdpxi.pixelleap.Game;
import dev.xdpxi.pixelleap.Main;
import dev.xdpxi.pixelleap.Maps;
import dev.xdpxi.pixelleap.Platform;

import static org.lwjgl.glfw.GLFW.*;

public class Player {
    public static final float Width = 50f;
    public static final float Height = 50f;
    public static boolean isGrounded = false;
    public static float X = 100f;
    public static float Y = 400f;
    public static float velocityY = 0f;

    public static void handleMovement() {
        if (isKeyPressed(GLFW_KEY_D, GLFW_KEY_RIGHT)) {
            X += getAdjustedSpeed();
        }
        if (isKeyPressed(GLFW_KEY_A, GLFW_KEY_LEFT)) {
            X -= getAdjustedSpeed();
        }
        if (isKeyPressed(GLFW_KEY_W, GLFW_KEY_UP, GLFW_KEY_SPACE) && isGrounded) {
            velocityY = getAdjustedJump();
            isGrounded = false;
        }
    }

    public static void handlePlatforms() {
        if (checkColorCollision("#0FFF50")) {
            Maps.switchMaps();
        }
        if (checkColorCollision("#FFAA33")) {
            velocityY = getAdjustedJump();
            isGrounded = false;
        }
        while (checkColorCollision("#7F00FF")) {
            if (glfwGetKey(Game.window, GLFW_KEY_D) == GLFW_PRESS || glfwGetKey(Game.window, GLFW_KEY_RIGHT) == GLFW_PRESS) {
                X += 1;
            }
            if (glfwGetKey(Game.window, GLFW_KEY_A) == GLFW_PRESS || glfwGetKey(Game.window, GLFW_KEY_LEFT) == GLFW_PRESS) {
                X -= 1;
            }
        }
        while (checkColorCollision("#000000")) {
            if (glfwGetKey(Game.window, GLFW_KEY_D) == GLFW_PRESS || glfwGetKey(Game.window, GLFW_KEY_RIGHT) == GLFW_PRESS) {
                X -= 5;
            }
            if (glfwGetKey(Game.window, GLFW_KEY_A) == GLFW_PRESS || glfwGetKey(Game.window, GLFW_KEY_LEFT) == GLFW_PRESS) {
                X += 5;
            }
        }
        if (checkColorCollision("#FF2400")) {
            try {
                Main.restartApplication(Maps.currentMap);
            } catch (Exception e) {
                System.err.println("Error restarting application: " + e.getMessage());
            }
        }
    }

    public static boolean checkColorCollision(String color) {
        for (Platform platform : Maps.platforms) {
            if (checkCollision(platform) && color.equals(platform.color())) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkCollision(Platform platform) {
        return X + Width > platform.x() && X < platform.x() + platform.width() &&
                Y <= platform.y() + platform.height() && Y + Height >= platform.y();
    }

    public static float getAdjustedSpeed() {
        for (Platform platform : Maps.platforms) {
            if (checkCollision(platform) && "#FF10F0".equals(platform.color())) {
                return 10f;
            }
        }
        return 5f;
    }

    public static float getAdjustedJump() {
        for (Platform platform : Maps.platforms) {
            if (checkCollision(platform) && "#FFAA33".equals(platform.color())) {
                return 20f;
            }
        }
        return 10f;
    }

    private static boolean isKeyPressed(int... keys) {
        for (int key : keys) {
            if (glfwGetKey(Game.window, key) == GLFW_PRESS) {
                return true;
            }
        }
        return false;
    }
}