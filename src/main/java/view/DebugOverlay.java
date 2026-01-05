package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import controller.GamePanel;

public class DebugOverlay {
    private GamePanel gp;
    private boolean enabled = false;
    private Font debugFont;

    public DebugOverlay(GamePanel gp) {
        this.gp = gp;
        this.debugFont = new Font("Monospaced", Font.PLAIN, 12);
    }

    public void toggle() {
        enabled = !enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void draw(Graphics2D g2) {
        if (!enabled) {
            return;
        }

        g2.setFont(debugFont);
        g2.setColor(Color.BLACK);

        int x = 10;
        int y = 20;
        int lineHeight = 16;

        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(5, 5, 300, 180);

        g2.setColor(Color.CYAN);

        int playerWorldX = gp.player.worldX;
        int playerWorldY = gp.player.worldY;
        int playerTileX = playerWorldX / gp.tileSize;
        int playerTileY = playerWorldY / gp.tileSize;

        g2.drawString("=== DEBUG INFO ===", x, y);
        y += lineHeight;

        g2.setColor(Color.WHITE);
        g2.drawString("Player World: (" + playerWorldX + ", " + playerWorldY + ")", x, y);
        y += lineHeight;

        g2.drawString("Player Tile: (" + playerTileX + ", " + playerTileY + ")", x, y);
        y += lineHeight;

        g2.drawString("Direction: " + gp.player.direction, x, y);
        y += lineHeight;

        g2.setColor(Color.YELLOW);
        if (gp.mapM != null && gp.mapM.getMap() != null) {
            model.Map currentMap = gp.mapM.getMap();
            g2.drawString("Map: " + gp.mapM.currMap + " - " + currentMap.levelName, x, y);
            y += lineHeight;

            g2.drawString("Map Size: " + currentMap.maxWorldCol + "x" + currentMap.maxWorldRow + " tiles", x, y);
            y += lineHeight;
        }

        g2.setColor(Color.GREEN);
        if (gp.stateM != null) {
            g2.drawString("State: " + gp.stateM.getCurrentState(), x, y);
            y += lineHeight;
        }

        g2.setColor(Color.ORANGE);
        g2.drawString("Energy: " + gp.player.energy + "/" + gp.player.maxEnergy, x, y);
        y += lineHeight;

        g2.drawString("Gold: " + gp.player.gold, x, y);
        y += lineHeight;

        g2.drawString("Score: " + gp.player.score, x, y);
        y += lineHeight;

        if (gp.npcM != null) {
            g2.setColor(Color.MAGENTA);
            int npcCount = gp.npcM.getNPCCount();
            g2.drawString("NPCs Active: " + npcCount, x, y);
            y += lineHeight;
        }

        g2.setColor(Color.LIGHT_GRAY);
        g2.drawString("Press F3 to toggle debug", x, y);
    }
}
