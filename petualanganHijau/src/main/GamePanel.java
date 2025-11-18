package main;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {

    //SCREEN SETTINGS
    final int originalTileSize = 32; // 32x32 tile
    final int scale = 2;
    final int tileSize = originalTileSize * scale; // 64x64 tile

    final int maxScreenCol = 20;
    final int maxScreenRow = 12;
    final int screenWidth = tileSize * maxScreenCol; // 1280 pixel
    final int screenHeight = tileSize * maxScreenRow; // 768 pixel

    public GamePanel(){

        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);

    }
}
