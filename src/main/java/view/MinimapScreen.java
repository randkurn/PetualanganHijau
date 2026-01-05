package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import controller.GamePanel;

public class MinimapScreen extends UI {
    private boolean isFullscreen = false;
    private int minimapSize = 100;
    private int minimapPadding = 10;
    private final int TILE_ZOOM = 20;

    protected MinimapScreen(GamePanel gp) {
        super(gp);
    }

    public void toggleFullscreen() {
        isFullscreen = !isFullscreen;
    }

    public boolean isFullscreen() {
        return isFullscreen;
    }

    public void setFullscreen(boolean fullscreen) {
        this.isFullscreen = fullscreen;
    }

    public void draw(Graphics2D g2) {
        // Check if minimap should be shown
        if (!model.Settings.getInstance().getShowMinimap()) {
            return;
        }

        // Don't draw minimap in interior maps as requested by user
        String levelName = gp.mapM.getMap().levelName;
        if (levelName != null && levelName.contains("INTERIOR")) {
            return;
        }

        if (isFullscreen) {
            drawFullscreenMap(g2);
        } else {
            drawMinimap(g2);
        }
    }

    private void drawMinimap(Graphics2D g2) {
        float uiScale = (float) gp.scale / 4.0f;
        int scaledSize = (int) (minimapSize * uiScale);
        int scaledPadding = (int) (minimapPadding * uiScale);

        // Position minimap at bottom-right corner
        int minimapX = gp.screenWidth - scaledSize - scaledPadding;
        int minimapY = gp.screenHeight - scaledSize - scaledPadding - (int) (40 * uiScale);

        // Draw minimap background
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRoundRect(minimapX, minimapY, scaledSize, scaledSize, 10, 10);

        // Draw minimap border
        g2.setColor(Color.WHITE);
        g2.drawRoundRect(minimapX, minimapY, scaledSize, scaledSize, 10, 10);

        // Set clipping area for the minimap content
        java.awt.Shape oldClip = g2.getClip();
        g2.setClip(new java.awt.geom.RoundRectangle2D.Float(minimapX, minimapY, scaledSize, scaledSize, 10, 10));

        float zoomFactor = (float) scaledSize / (TILE_ZOOM * gp.tileSize);

        int playerWorldX = gp.player.worldX;
        int playerWorldY = gp.player.worldY;

        int offsetX = minimapX + (scaledSize / 2) - (int) (playerWorldX * zoomFactor);
        int offsetY = minimapY + (scaledSize / 2) - (int) (playerWorldY * zoomFactor);

        drawMapTiles(g2, offsetX, offsetY, zoomFactor, scaledSize);

        g2.setColor(new Color(255, 50, 50));
        g2.fillOval(minimapX + (scaledSize / 2) - 3, minimapY + (scaledSize / 2) - 3, 6, 6);

        g2.setClip(oldClip);

        g2.setColor(Color.WHITE);
        g2.setFont(getScaledFont(Font.PLAIN, 10));
        String areaName = gp.mapM.getMap().levelName;
        g2.drawString(areaName, minimapX + (int) (5 * uiScale), minimapY + scaledSize + (int) (15 * uiScale));

        g2.setFont(getScaledFont(Font.PLAIN, 8));
        g2.setColor(new Color(200, 200, 200));
        g2.drawString("M: Peta Penuh", minimapX + (int) (5 * uiScale), minimapY + scaledSize + (int) (27 * uiScale));
    }

    private void drawFullscreenMap(Graphics2D g2) {
        float uiScale = (float) gp.scale / 4.0f;
        g2.setColor(new Color(0, 0, 0, 230));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        int mapDisplayWidth = (int) (gp.screenWidth * 0.7);
        int mapDisplayHeight = (int) (gp.screenHeight * 0.7);
        int mapX = (gp.screenWidth - mapDisplayWidth) / 2;
        int mapY = (gp.screenHeight - mapDisplayHeight) / 2;

        g2.setColor(new Color(20, 20, 20));
        g2.fillRect(mapX, mapY, mapDisplayWidth, mapDisplayHeight);

        g2.setColor(Color.WHITE);
        g2.drawRect(mapX, mapY, mapDisplayWidth, mapDisplayHeight);

        int mapWidth = gp.mapM.getMap().maxWorldCol * gp.tileSize;
        int mapHeight = gp.mapM.getMap().maxWorldRow * gp.tileSize;

        float scaleX = (float) mapDisplayWidth / mapWidth;
        float scaleY = (float) mapDisplayHeight / mapHeight;
        float scale = Math.min(scaleX, scaleY);

        int actualMapWidth = (int) (mapWidth * scale);
        int actualMapHeight = (int) (mapHeight * scale);
        int offsetX = mapX + (mapDisplayWidth - actualMapWidth) / 2;
        int offsetY = mapY + (mapDisplayHeight - actualMapHeight) / 2;

        drawMapTiles(g2, offsetX, offsetY, scale, mapDisplayWidth);

        int playerMapX = (int) (gp.player.worldX * scale) + offsetX;
        int playerMapY = (int) (gp.player.worldY * scale) + offsetY;

        g2.setColor(new Color(255, 50, 50));
        g2.fillOval(playerMapX - 5, playerMapY - 5, 10, 10);
        g2.setColor(Color.WHITE);
        g2.drawOval(playerMapX - 5, playerMapY - 5, 10, 10);

        g2.setFont(getScaledFont(Font.BOLD, 24));
        String title = "PETA: " + gp.mapM.getMap().levelName;
        int titleX = getHorizontalCenter(title, g2, gp.screenWidth);
        g2.drawString(title, titleX, mapY - (int) (20 * uiScale));

        drawLegend(g2, mapX, mapY + mapDisplayHeight + (int) (20 * uiScale));

        g2.setFont(getScaledFont(Font.PLAIN, 12));
        g2.setColor(new Color(200, 200, 200));
        String instruction = "Tekan M untuk menutup | Arrow Keys: Ganti Area";
        int instrX = getHorizontalCenter(instruction, g2, gp.screenWidth);
        g2.drawString(instruction, instrX, gp.screenHeight - (int) (30 * uiScale));
    }

    private void drawMapTiles(Graphics2D g2, int offsetX, int offsetY, float scale, int maxSize) {
        int[][] tileMap = gp.mapM.getTileMap();
        int maxCol = gp.mapM.getMap().maxWorldCol;
        int maxRow = gp.mapM.getMap().maxWorldRow;

        for (int row = 0; row < maxRow; row++) {
            for (int col = 0; col < maxCol; col++) {
                int tileNum = tileMap[row][col];
                int x = (int) (col * gp.tileSize * scale) + offsetX;
                int y = (int) (row * gp.tileSize * scale) + offsetY;
                int size = Math.max(1, (int) (gp.tileSize * scale));

                Color tileColor = getTileColor(tileNum);
                g2.setColor(tileColor);
                g2.fillRect(x, y, size, size);
            }
        }

        for (int i = 0; i < gp.mapM.getMap().objects.length; i++) {
            if (gp.mapM.getMap().objects[i] != null) {
                int x = (int) (gp.mapM.getMap().objects[i].worldX * scale) + offsetX;
                int y = (int) (gp.mapM.getMap().objects[i].worldY * scale) + offsetY;

                g2.setColor(Color.YELLOW);
                g2.fillRect(x - 2, y - 2, 4, 4);
            }
        }
    }

    private Color getTileColor(int tileNum) {

        int gid = tileNum & 0x1FFFFFFF;

        if (gid == 0)
            return new Color(50, 50, 50);

        if (gid < 50)
            return new Color(40, 100, 40);
        if (gid >= 50 && gid < 100)
            return new Color(120, 120, 100);
        if (gid >= 150 && gid < 200)
            return new Color(40, 60, 150);
        if (gid >= 200)
            return new Color(80, 80, 80);

        String level = gp.mapM.getMap().levelName;
        if (level.contains("FISH"))
            return new Color(30, 50, 100);
        if (level.contains("FOREST") || level.contains("PARK"))
            return new Color(30, 70, 30);

        return new Color(70, 70, 70);
    }

    private void drawLegend(Graphics2D g2, int x, int y) {
        float uiScale = getUIScale();
        g2.setFont(getScaledFont(Font.PLAIN, 10));

        g2.setColor(new Color(255, 50, 50));
        g2.fillOval(x, y, (int) (8 * uiScale), (int) (8 * uiScale));
        g2.setColor(Color.WHITE);
        g2.drawString("Posisi Anda", x + (int) (15 * uiScale), y + (int) (8 * uiScale));

        g2.setColor(Color.YELLOW);
        g2.fillRect(x, y + (int) (15 * uiScale), (int) (8 * uiScale), (int) (8 * uiScale));
        g2.setColor(Color.WHITE);
        g2.drawString("Objek/Item", x + (int) (15 * uiScale), y + (int) (23 * uiScale));

        g2.setColor(new Color(100, 150, 255));
        g2.fillRect(x, y + (int) (30 * uiScale), (int) (8 * uiScale), (int) (8 * uiScale));
        g2.setColor(Color.WHITE);
        g2.drawString("Portal/Gerbang", x + (int) (15 * uiScale), y + (int) (38 * uiScale));
    }
}
