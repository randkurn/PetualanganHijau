package model;

import java.awt.image.BufferedImage;

class TilesetInfo {
    int firstgid;
    String name;
    BufferedImage image;
    int tileWidth;
    int tileHeight;
    int columns;
    int tileCount;
    java.util.Map<Integer, BufferedImage> tileImages = new java.util.HashMap<>();
}
