package dev.xdpxi.pixelleap;

import dev.xdpxi.pixelleap.Entities.Player;
import dev.xdpxi.pixelleap.Util.Log;

public class Maps {
    // Map 1
    public static final Platform[] map1 = {
            // Dirt - Brown
            new Platform(-5000f, -2000f, 10000f, 2000f, "#7B3F00"),
            // Grass - Green
            new Platform(-5000f, -100f, 10000f, 100f, "#228B22"),

            // Normal - Gray
            new Platform(200f, 100f, 200f, 20f, "#4b6371"),
            new Platform(475f, 200f, 150f, 20f, "#4b6371"),
            new Platform(750f, 300f, 100f, 20f, "#4b6371"),
            new Platform(950f, 400f, 300f, 20f, "#4b6371"),

            // Elevator - Blue
            new Platform(1150f, 500f, 100f, 20f, "#1F51FF"),
            new Platform(1150f, 600f, 100f, 20f, "#1F51FF"),
            new Platform(1150f, 700f, 100f, 20f, "#1F51FF"),
            new Platform(1150f, 800f, 100f, 20f, "#1F51FF"),
            new Platform(1150f, 900f, 100f, 20f, "#1F51FF"),
            new Platform(1150f, 1000f, 100f, 20f, "#1F51FF"),
            new Platform(1150f, 1100f, 100f, 20f, "#1F51FF"),

            // Speed - Pink
            new Platform(1300f, 1100f, 200f, 20f, "#FF10F0"),
            new Platform(1575f, 1200f, 150f, 20f, "#FF10F0"),
            new Platform(1850f, 1300f, 100f, 20f, "#FF10F0"),
            new Platform(2050f, 1400f, 300f, 20f, "#FF10F0"),

            // Jump - Orange
            new Platform(2450f, 1400f, 100f, 20f, "#FFAA33"),
            new Platform(2650f, 1700f, 100f, 20f, "#FFAA33"),

            // Complete - Green
            new Platform(2850f, 1700f, 100f, 20f, "#0FFF50"),

            // Void - Black
            new Platform(-5000f, -3000, 4000f, 6000f, "#000000"),
            new Platform(4000f, -4000, 4000f, 8000f, "#000000")
    };

    // Map 2
    public static final Platform[] map2 = {
            // Dirt - Brown
            new Platform(-5000f, -2000f, 10000f, 2000f, "#7B3F00"),
            // Grass - Green
            new Platform(-5000f, -100f, 10000f, 100f, "#228B22"),

            // Lava - Red
            new Platform(300f, -2000f, 2000f, 2000f, "#FF2400"),

            // Normal - Gray
            new Platform(400f, 100f, 200f, 20f, "#4b6371"),
            new Platform(800f, 100f, 200f, 20f, "#4b6371"),
            new Platform(1200f, 100f, 200f, 20f, "#4b6371"),
            new Platform(1600f, 100f, 200f, 20f, "#4b6371"),
            new Platform(2000f, 100f, 200f, 20f, "#4b6371"),

            // Lava - Red
            new Platform(2700f, -2000f, 500f, 2000f, "#FF2400"),

            // Teleporter - Purple
            new Platform(2600f, 100f, 200f, 20f, "#7F00FF"),
            new Platform(3000f, 100f, 200f, 20f, "#7F00FF"),

            // Complete - Green
            new Platform(3300f, 0f, 100f, 20f, "#0FFF50"),

            // Void - Black
            new Platform(-5000f, -2000, 4000f, 4000f, "#000000"),
            new Platform(4000f, -2000, 4000f, 4000f, "#000000")
    };
    // Map 3
    public static final Platform[] map3 = {
            // Dirt - Brown
            new Platform(-5000f, -2000f, 10000f, 2000f, "#7B3F00"),
            // Grass - Green
            new Platform(-5000f, -100f, 10000f, 100f, "#228B22"),

            // Map Teleporter - Purple
            new Platform(200f, 100f, 200f, 20f, "#7F00FA"),
            new Platform(550f, 100f, 200f, 20f, "#7F00FB"),
            new Platform(900f, 100f, 200f, 20f, "#7F00FC"),

            // Void - Black
            new Platform(-5000f, -2000, 4000f, 4000f, "#000000"),
            new Platform(2500f, -2000, 4000f, 4000f, "#000000")
    };
    // Map 3A
    public static final Platform[] map3a = {
            // Dirt - Brown
            new Platform(-5000f, -2000f, 10000f, 2000f, "#7B3F00"),
            // Grass - Green
            new Platform(-5000f, -100f, 10000f, 100f, "#228B22"),

            // Map Teleporter - Purple
            new Platform(200f, 100f, 200f, 20f, "#7F00FA"),

            // Void - Black
            new Platform(-5000f, -2000, 4000f, 4000f, "#000000"),
            new Platform(2500f, -2000, 4000f, 4000f, "#000000")
    };
    // Map 3B
    public static final Platform[] map3b = {
            // Dirt - Brown
            new Platform(-5000f, -2000f, 10000f, 2000f, "#7B3F00"),
            // Grass - Green
            new Platform(-5000f, -100f, 10000f, 100f, "#228B22"),

            // Map Teleporter - Purple
            new Platform(550f, 100f, 200f, 20f, "#7F00FB"),

            // Void - Black
            new Platform(-5000f, -2000, 4000f, 4000f, "#000000"),
            new Platform(2500f, -2000, 4000f, 4000f, "#000000")
    };
    // Map 3C
    public static final Platform[] map3c = {
            // Dirt - Brown
            new Platform(-5000f, -2000f, 10000f, 2000f, "#7B3F00"),
            // Grass - Green
            new Platform(-5000f, -100f, 10000f, 100f, "#228B22"),

            // Map Teleporter - Purple
            new Platform(900f, 100f, 200f, 20f, "#7F00FC"),

            // Void - Black
            new Platform(-5000f, -2000, 4000f, 4000f, "#000000"),
            new Platform(2500f, -2000, 4000f, 4000f, "#000000")
    };
    public static Platform[] platforms = {};
    public static int currentMap = 1;

    public static Platform[] getMap(String mapID) {
        Log.debug("Getting map for mapID: " + mapID);
        Platform[] selectedMap = switch (mapID) {
            case "map2" -> map2;
            case "map3" -> map3;
            default -> map1;
        };
        Log.info("Selected map: " + mapID + " with " + selectedMap.length + " platforms");
        return selectedMap;
    }

    public static void switchMaps() {
        Log.info("Switching maps. Current map: " + currentMap);
        switch (currentMap) {
            case 1 -> {
                platforms = Maps.getMap("map2");
                currentMap = 2;
                Player.resetPos();
                Log.info("Switched to map 2");
            }
            case 2 -> {
                platforms = Maps.getMap("map3");
                currentMap = 3;
                Player.X = 600f;
                Player.Y = 0f;
                Log.info("Switched to map 3");
            }
            default -> Log.warn("Attempted to switch from unknown map: " + currentMap);
        }
        Log.debug("New map has " + platforms.length + " platforms");
    }

    public record Platform(float x, float y, float width, float height, String color) {
    }
}