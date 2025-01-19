package dev.xdpxi.pixelleap.Entities;

import dev.xdpxi.pixelleap.Game;
import dev.xdpxi.pixelleap.Maps;
import dev.xdpxi.pixelleap.Util.Log;

import static org.lwjgl.glfw.GLFW.*;

public class Player {
    public static final float WIDTH = 50f;
    public static final float HEIGHT = 50f;
    public static boolean isGrounded = false;
    public static float X = 0f;
    public static float Y = 0f;
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
            Log.info("Player reached map switch platform");
            Maps.switchMaps();
        }
        if (checkColorCollision("#0FFF50")) {
            Log.info("Player reached map switch platform");
            Maps.switchMaps();
        }
        if (checkColorCollision("#7F00FA")) {
            Log.info("Player reached map switch platform");
            Maps.platforms = Maps.map3a;
        }
        if (checkColorCollision("#7F00FB")) {
            Log.info("Player reached map switch platform");
            Maps.platforms = Maps.map3b;
        }
        if (checkColorCollision("#7F00FC")) {
            Log.info("Player reached map switch platform");
            Maps.platforms = Maps.map3c;
        }
        handleColorCollision("#7F00FF", 1);
        handleColorCollision("#000000", -5);
        if (checkColorCollision("#FF2400")) {
            Log.info("Player hit reset platform");
            resetPos();
        }
    }

    private static void handleColorCollision(String color, int moveAmount) {
        while (checkColorCollision(color)) {
            if (isKeyPressed(GLFW_KEY_D, GLFW_KEY_RIGHT)) {
                X += moveAmount;
            }
            if (isKeyPressed(GLFW_KEY_A, GLFW_KEY_LEFT)) {
                X -= moveAmount;
            }
        }
    }

    public static void resetPos() {
        X = 0f;
        Y = 0f;
        Log.info("Player position reset to X: " + X + ", Y: " + Y);
    }

    public static boolean checkColorCollision(String color) {
        for (Maps.Platform platform : Maps.platforms) {
            if (checkCollision(platform) && color.equals(platform.color())) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkCollision(Maps.Platform platform) {
        return X + WIDTH > platform.x() && X < platform.x() + platform.width() &&
                Y <= platform.y() + platform.height() && Y + HEIGHT >= platform.y();
    }

    public static float getAdjustedSpeed() {
        for (Maps.Platform platform : Maps.platforms) {
            if (checkCollision(platform) && "#FF10F0".equals(platform.color())) {
                Log.debug("Speed boost applied");
                return 10f;
            }
        }
        return 5f;
    }

    public static float getAdjustedJump() {
        for (Maps.Platform platform : Maps.platforms) {
            if (checkCollision(platform) && "#FFAA33".equals(platform.color())) {
                Log.debug("Jump boost applied");
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