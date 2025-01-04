public class Maps {
    // Map 1
    public static final Platform[] map1 = {
            // Dirt - Brown
            new Platform(-1_073_741_823f, -2_147_483_647f, 2_147_483_647f, 2_147_483_647f, "#7B3F00"),
            // Grass - Green
            new Platform(-1_073_741_823f, -100f, 2_147_483_647f, 100f, "#228B22"),

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
            new Platform(-2_147_485_000f, -1_073_741_823f, 2_147_483_647f, 2_147_483_647f, "#000000")
    };

    // Map 2
    public static final Platform[] map2 = {
            // Dirt - Brown
            new Platform(-1_073_741_823f, -2_147_481_948f, 2_147_483_647f, 2_147_483_647f, "#7B3F00"),
            // Grass - Green
            new Platform(-1_073_741_823f, 1564f, 2_147_483_647f, 100f, "#228B22"),

            // Lava - Reddw
            new Platform(3000f, -2_147_481_947f, 2000f, 2_147_483_647f, "#FF2400"),

            // Normal - Gray
            new Platform(3100f, 1760f, 200f, 20f, "#4b6371"),
            new Platform(3500f, 1760f, 200f, 20f, "#4b6371"),
            new Platform(3900f, 1760f, 200f, 20f, "#4b6371"),
            new Platform(4300f, 1760f, 200f, 20f, "#4b6371"),
            new Platform(4700f, 1760f, 200f, 20f, "#4b6371"),

            // Lava - Red
            new Platform(5400f, -2_147_481_947f, 500f, 2_147_483_647f, "#FF2400"),

            // Teleporter - Purple
            new Platform(5300f, 1760f, 200f, 20f, "#7F00FF"),
            new Platform(5700f, 1760f, 200f, 20f, "#7F00FF"),

            // Complete - Green
            new Platform(6100f, 1664f, 100f, 20f, "#0FFF50"),

            // Void - Black
            new Platform(-2_147_482_000f, -1_073_741_823f, 2_147_483_647f, 2_147_483_647f, "#000000")
    };

    // Map 3
    public static final Platform[] map3 = {
            // Dirt - Brown
            new Platform(-1_073_741_823f, -2_147_481_948f, 2_147_483_647f, 2_147_483_647f, "#7B3F00"),
            // Grass - Green
            new Platform(-1_073_741_823f, 1564f, 2_147_483_647f, 100f, "#228B22"),

            // Void - Black
            new Platform(-2_147_479_000f, -1_073_741_823f, 2_147_483_647f, 2_147_483_647f, "#000000")
    };

    public static Platform[] getMap(String mapID) {
        return switch (mapID) {
            case "map2" -> map2;
            case "map3" -> map3;
            default -> map1;
        };
    }
}