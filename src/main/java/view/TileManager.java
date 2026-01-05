package view;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import controller.GamePanel;

public class TileManager {
    private static TileManager tileInstance = null;

    GamePanel gp;
    private Tile[] tile;
    private ArrayList<String> fileNames = new ArrayList<>();
    private ArrayList<String> collisionStatus = new ArrayList<>();

    public static class Tile {
        protected BufferedImage image;
        protected boolean collision = false;
    }

    private TileManager() {
        InputStream is = getClass().getResourceAsStream("/tiles/tileData.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        String line;
        try {
            while ((line = br.readLine()) != null) {
                fileNames.add(line);
                collisionStatus.add(br.readLine());
            }
            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        tile = new Tile[fileNames.size()];
        getTileImage();
    }

    public static TileManager getInstance() {
        if (tileInstance == null) {
            tileInstance = new TileManager();
        }

        return tileInstance;
    }

    private void getTileImage() {
        for (int i = 0; i < fileNames.size(); i++) {

            String fileName;
            boolean collision;

            fileName = fileNames.get(i);

            if (collisionStatus.get(i).equals("true")) {
                collision = true;
            } else {
                collision = false;
            }
            setup(i, fileName, collision);
        }
    }

    private void setup(int index, String imageName, boolean collision) {
        try {
            tile[index] = new Tile();
            tile[index].image = ImageIO.read(getClass().getResourceAsStream("/tiles/" + imageName));
            tile[index].collision = collision;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getTileCount() {
        return tile != null ? tile.length : 0;
    }

    public BufferedImage getTileImage(int index) {
        if (tile == null || tile.length == 0) {
            return null;
        }
        if (index < 0 || index >= tile.length) {
            index = 0;
        }
        return tile[index].image;
    }

    public Boolean checkTileCollision(int index) {
        if (tile == null || tile.length == 0) {
            return false;
        }
        if (index < 0 || index >= tile.length) {
            index = 0;
        }
        return tile[index].collision;
    }
}
