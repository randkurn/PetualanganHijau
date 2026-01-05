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
        register("Botol Minum", TrashType.ANORGANIC, "/objects/trash/botol cola.png");
        register("Sisa Pisang", TrashType.ORGANIC, "/objects/trash/pisang.png");
        register("Baterai Bekas", TrashType.TOXIC, "/objects/trash/baterai.png");
        register("Bohlam Putus", TrashType.TOXIC, "/objects/trash/bohlam.png");
        register("Charger Rusak", TrashType.TOXIC, "/objects/trash/charger.png");
        register("Kaleng Makanan", TrashType.ANORGANIC, "/objects/trash/kaleng makanan.png");
        register("Masker Bekas", TrashType.TOXIC, "/objects/trash/masker.png");
        register("Sisa Apel", TrashType.ORGANIC, "/objects/trash/sisa apel.png");
        register("Pop Es", TrashType.ANORGANIC, "/objects/trash/pop es.png");
        register("Botol Tertanam", TrashType.ANORGANIC, "/objects/trash/botol aqua tertanam.png");
        register("Susu Kotak", TrashType.ANORGANIC, "/objects/trash/susu.png");
        register("Ceri Busuk", TrashType.ORGANIC, "/objects/trash/ceri.png");

        // Non-trash items for icon persistence on load
        register("Bibit Pohon", null, "/objects/tree1.png");
        register("Bibit Mahoni", null, "/objects/tree1.png");
        register("Kunci", null, "/objects/key.png");
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
