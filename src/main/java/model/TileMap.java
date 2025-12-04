package model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TileMap {

    private int cols, rows;
    private final int tileRendered = 64;

    private int[][] tiles;

    private final Map<Integer, TileDefinition> tileDefs = new HashMap<>();

    public TileMap(String fileName) {
        loadMap(fileName);
        initTileDefinitions();
    }

    // ===================== LOAD MAP TXT ======================
    private void loadMap(String fileName) {
        try {
            InputStream in = getClass().getResourceAsStream("/assets/maps/" + fileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            ArrayList<int[]> lines = new ArrayList<>();
            String line;

            while ((line = br.readLine()) != null) {
                String[] split = line.split(" ");
                int[] row = new int[split.length];

                for (int i = 0; i < split.length; i++) {
                    row[i] = Integer.parseInt(split[i]);
                }

                lines.add(row);
            }

            rows = lines.size();
            cols = lines.get(0).length;

            tiles = new int[rows][cols];
            for (int r = 0; r < rows; r++) {
                tiles[r] = lines.get(r);
            }

        } catch (Exception e) {
            System.out.println("Gagal load map TXT!");
            e.printStackTrace();
        }
    }

    // ===================== REGISTRASI TILE ======================
    /**
     * Menggunakan PNG terpisah di folder /assets/tiles sebagai tile utama.
     *
     * ID di map:
     *  0 -> grass.png   (tanah rumput, bisa dilalui)
     *  1 -> path.png    (jalan, bisa dilalui)
     *  2 -> farm.png    (lahan, bisa dilalui)
     *  3 -> cliff.png   (tebing/bangunan, tidak bisa dilalui)
     *  4 -> tree.png    (pohon/halangan, tidak bisa dilalui)
     *  5 -> batu.png    (batu/pagar, tidak bisa dilalui)
     *  6 -> water.png   (air, tidak bisa dilalui)
     *  7 -> bridge.png  (jembatan, bisa dilalui)
     *  8 -> beach.png   (pasir, bisa dilalui)
     *  9 -> tanah.png   (tanah biasa, bisa dilalui)
     */
    private void initTileDefinitions() {
        tileDefs.put(0, tile("/assets/tiles/grass.png", "#7BD67A", false, false));
        tileDefs.put(1, tile("/assets/tiles/path.png", "#C48A4A", false, false));
        tileDefs.put(2, tile("/assets/tiles/farm.png", "#C58F3D", false, false));
        tileDefs.put(3, tile("/assets/tiles/cliff.png", "#5C4B3A", true, false));
        tileDefs.put(4, tile("/assets/tiles/tree.png", "#1B5E20", true, true));
        tileDefs.put(5, tile("/assets/tiles/batu.png", "#777777", true, false));
        tileDefs.put(6, tile("/assets/tiles/water.png", "#4DA0FF", true, false));
        tileDefs.put(7, tile("/assets/tiles/bridge.png", "#C4823C", false, true));
        tileDefs.put(8, tile("/assets/tiles/beach.png", "#E9D8A6", false, false));
        tileDefs.put(9, tile("/assets/tiles/tanah.png", "#B89D72", false, false));
    }

    private TileDefinition tile(String path, String fallbackHex, boolean blocked, boolean removeWhite) {
        return new TileDefinition(loadTile(path, removeWhite), Color.web(fallbackHex), blocked);
    }

    private Image loadTile(String path, boolean removeWhite) {
        Image raw = safeLoad(path);
        if (raw == null || !removeWhite) {
            return raw;
        }

        int width = (int) raw.getWidth();
        int height = (int) raw.getHeight();
        WritableImage cleaned = new WritableImage(width, height);
        PixelReader reader = raw.getPixelReader();
        PixelWriter writer = cleaned.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = reader.getColor(x, y);
                if (color.getRed() > 0.95 && color.getGreen() > 0.95 && color.getBlue() > 0.95) {
                    writer.setColor(x, y, new Color(color.getRed(), color.getGreen(), color.getBlue(), 0));
                } else {
                    writer.setColor(x, y, color);
                }
            }
        }

        return cleaned;
    }

    private Image safeLoad(String path) {
        try {
            InputStream in = getClass().getResourceAsStream(path);
            if (in == null) {
                System.out.println("PNG tidak ditemukan: " + path);
                return null;
            }
            return new Image(in);
        } catch (Exception e) {
            System.out.println("Gagal load PNG: " + path);
            return null;
        }
    }

    // ===================== RENDER TILE ======================
    public void render(GraphicsContext gc, double camX, double camY) {

        int size = tileRendered;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {

                int tile = tiles[r][c];
                double drawX = c * size - camX;
                double drawY = r * size - camY;

                TileDefinition def = tileDefs.get(tile);
                if (def == null) {
                    gc.setFill(Color.BLACK);
                    gc.fillRect(drawX, drawY, size, size);
                    continue;
                }

                if (def.image != null) {
                    gc.drawImage(def.image, drawX, drawY, size, size);
                } else {
                    gc.setFill(def.fallbackColor);
                    gc.fillRect(drawX, drawY, size, size);
                }
            }
        }
    }

    // ===================== COLLISION ======================
public boolean isBlocked(double px, double py, int size) {

    int tileSize = tileRendered;

    // posisi tile untuk keempat sudut
    int left   = (int) (px / tileSize);
    int right  = (int) ((px + size - 1) / tileSize);
    int top    = (int) (py / tileSize);
    int bottom = (int) ((py + size - 1) / tileSize);

    try {
        return isBlockedTile(tiles[top][left])   ||
               isBlockedTile(tiles[top][right])  ||
               isBlockedTile(tiles[bottom][left])||
               isBlockedTile(tiles[bottom][right]);
    } catch (Exception e) {
        return true; // di luar map = blocked
    }
}

    private boolean isBlockedTile(int tileId) {
        TileDefinition def = tileDefs.get(tileId);
        return def == null || def.blocked;
    }


    public int getTileSize() {
        return tileRendered;
    }

    private static class TileDefinition {
        final Image image;
        final Color fallbackColor;
        final boolean blocked;

        TileDefinition(Image image, Color fallbackColor, boolean blocked) {
            this.image = image;
            this.fallbackColor = fallbackColor;
            this.blocked = blocked;
        }
    }
}
