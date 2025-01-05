package dev.xdpxi.pixelleap.Util;

public class Hex {
    public static String toHex(int color) {
        String hex = Integer.toHexString(color);
        return hex.substring(2);
    }

    public static float[] toRGB(String hex) {
        int r = Integer.parseInt(hex.substring(1, 3), 16);
        int g = Integer.parseInt(hex.substring(3, 5), 16);
        int b = Integer.parseInt(hex.substring(5, 7), 16);
        return new float[]{r / 255f, g / 255f, b / 255f};
    }
}