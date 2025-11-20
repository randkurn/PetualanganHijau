package model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class TileMap {

    private int cols, rows;
    private final int tileRendered = 64;

    private int[][] tiles;

    // Hanya dua PNG
    private Image dirtTile;
    private Image stoneTile;

    public TileMap(String fileName) {
        loadMap(fileName);
        loadTileImages();
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

    // ===================== LOAD PNG (Hanya Tanah & Batu) ======================
    private void loadTileImages() {
        dirtTile  = safeLoad("/assets/tiles/tanah.png");
        stoneTile = safeLoad("/assets/tiles/batu.png");
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

                switch (tile) {

                    case 0: // rumput (warna hijau)
                        gc.setFill(Color.web("#7BD67A"));
                        gc.fillRect(drawX, drawY, size, size);
                        break;

                    case 1: // tanah (PNG)
                        if (dirtTile != null) {
                            gc.drawImage(dirtTile, drawX, drawY, size, size);
                        } else {
                            gc.setFill(Color.web("#B89D72"));
                            gc.fillRect(drawX, drawY, size, size);
                        }
                        break;

                    case 2: // air (warna)
                        gc.setFill(Color.web("#4DA0FF"));
                        gc.fillRect(drawX, drawY, size, size);
                        break;

                    case 3: // batu (PNG)
                        if (stoneTile != null) {
                            gc.drawImage(stoneTile, drawX, drawY, size, size);
                        } else {
                            gc.setFill(Color.web("#777777"));
                            gc.fillRect(drawX, drawY, size, size);
                        }
                        break;

                    default: // fallback
                        gc.setFill(Color.BLACK);
                        gc.fillRect(drawX, drawY, size, size);
                        break;
                }
            }
        }
    }

    // ===================== COLLISION ======================
    public boolean isBlocked(int r, int c) {
        try {
            return tiles[r][c] == 3; // batu = tembok
        } catch (Exception e) {
            return true;
        }
    }

    public int getTileSize() {
        return tileRendered;
    }
}
