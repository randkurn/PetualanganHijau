package model;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

public class TrashRegistry {
    private static Map<String, TrashType> types = new HashMap<>();
    private static Map<String, String> iconPaths = new HashMap<>();
    private static Map<String, BufferedImage> iconCache = new HashMap<>();

    static {
        register("Pisang", TrashType.ORGANIC, "/objects/trash/trash_organic_pisang.png");
        register("Sisa Makanan", TrashType.ORGANIC, "/objects/trash/trash_organic_pisang.png");

        register("Botol Cola", TrashType.ANORGANIC, "/objects/trash/trash_anorganic_botol_cola.png");
        register("Botol Plastik", TrashType.ANORGANIC, "/objects/trash/trash_anorganic_botol_cola.png");

        register("Baterai", TrashType.TOXIC, "/objects/trash/trash_b3_baterai.png");
        register("Lampu", TrashType.TOXIC, "/objects/trash/trash_b3_baterai.png");
    }

    private static void register(String name, TrashType type, String path) {
        types.put(name, type);
        iconPaths.put(name, path);
    }

    public static TrashType getType(String name) {
        return types.get(name);
    }

    public static BufferedImage getIcon(String name) {
        if (iconCache.containsKey(name)) {
            return iconCache.get(name);
        }

        String path = iconPaths.get(name);
        if (path != null) {
            try {
                BufferedImage img = ImageIO.read(TrashRegistry.class.getResourceAsStream(path));
                iconCache.put(name, img);
                return img;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
