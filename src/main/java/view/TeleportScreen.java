package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import controller.GamePanel;
import controller.StateManager.gameState;

public class TeleportScreen extends UI {

    Font titleFont, menuFont;

    private int selectedIndex = 0;
    private String[] areaNames;
    private boolean[] unlockedAreas;

    public TeleportScreen(GamePanel gp) {
        super(gp);

        // Don't initialize area list here - will be done lazily when screen opens
        // to avoid NullPointerException (MapManager not initialized yet)
    }

    public void updateAreaList() {
        if (gp.mapM == null) {
            return;
        }

        int totalAreas = gp.mapM.getTotalAreas();
        areaNames = new String[totalAreas];
        unlockedAreas = new boolean[totalAreas];

        for (int i = 0; i < totalAreas; i++) {
            areaNames[i] = gp.mapM.getAreaName(i);
            unlockedAreas[i] = true;
        }
    }

    public void reset() {
        selectedIndex = 0;
        updateAreaList();
    }

    public void moveUp() {
        if (areaNames == null || areaNames.length == 0)
            return;
        selectedIndex--;
        if (selectedIndex < 0) {
            selectedIndex = areaNames.length - 1;
        }
    }

    public void moveDown() {
        if (areaNames == null || areaNames.length == 0)
            return;
        selectedIndex++;
        if (selectedIndex >= areaNames.length) {
            selectedIndex = 0;
        }
    }

    public void teleportToSelected() {
        if (areaNames == null || unlockedAreas == null)
            return;
        if (selectedIndex >= 0 && selectedIndex < unlockedAreas.length && unlockedAreas[selectedIndex]) {
            gp.mapM.changeToArea(selectedIndex);
            gp.stateM.setCurrentState(gameState.PLAY);
        }
    }

    public void draw(Graphics2D g2) {
        // Ensure area list is initialized
        if (areaNames == null || areaNames.length == 0) {
            updateAreaList();
            if (areaNames == null || areaNames.length == 0) {
                g2.setColor(Color.WHITE);
                g2.setFont(getScaledFont(Font.BOLD, 32));
                String msg = "Loading areas...";
                int x = getHorizontalCenter(msg, g2, gp.screenWidth);
                g2.drawString(msg, x, gp.screenHeight / 2);
                return;
            }
        }

        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        float uiScale = getUIScale();
        g2.setFont(getScaledFont(Font.BOLD, 32));
        g2.setColor(Color.WHITE);
        String title = "TELEPORT";
        int titleX = getHorizontalCenter(title, g2, gp.screenWidth);
        int titleY = gp.tileSize * 2;

        g2.setColor(Color.BLACK);
        g2.drawString(title, titleX + (int) (3 * uiScale), titleY + (int) (3 * uiScale));

        g2.setColor(new Color(100, 200, 255));
        g2.drawString(title, titleX, titleY);

        g2.setFont(getScaledFont(Font.PLAIN, 12));
        g2.setColor(Color.LIGHT_GRAY);
        String instructions = "[W/S] Navigasi | [Enter] Teleport | [Esc] Kembali";
        int instrX = getHorizontalCenter(instructions, g2, gp.screenWidth);
        g2.drawString(instructions, instrX, titleY + gp.tileSize);

        g2.setFont(getScaledFont(Font.PLAIN, 16));
        int itemHeight = (int) (40 * uiScale);
        int menuStartY = gp.screenHeight / 2 - (areaNames.length * itemHeight) / 2;

        for (int i = 0; i < areaNames.length; i++) {
            int y = menuStartY + i * itemHeight;

            boolean isCurrentArea = (i == gp.mapM.getCurrentAreaIndex());

            if (i == selectedIndex) {
                g2.setColor(Color.YELLOW);
                String arrow = ">";
                g2.drawString(arrow, gp.screenWidth / 2 - (int) (200 * uiScale), y);
                g2.drawString("<", gp.screenWidth / 2 + (int) (180 * uiScale), y);
            }

            if (!unlockedAreas[i]) {
                g2.setColor(Color.DARK_GRAY);
                g2.drawString("??? Locked", gp.screenWidth / 2 - (int) (150 * uiScale), y);
            } else {
                String areaName = areaNames[i];
                if (areaName == null) {
                    areaName = "Area " + i;
                }

                if (isCurrentArea) {
                    g2.setColor(new Color(100, 255, 100));
                    g2.drawString(areaName + " (Current)", gp.screenWidth / 2 - (int) (150 * uiScale), y);
                } else {
                    if (i == selectedIndex) {
                        g2.setColor(Color.WHITE);
                    } else {
                        g2.setColor(Color.LIGHT_GRAY);
                    }
                    g2.drawString(areaName, gp.screenWidth / 2 - (int) (150 * uiScale), y);
                }
            }
        }

        g2.setFont(getScaledFont(Font.PLAIN, 10));
        g2.setColor(Color.CYAN);
        String footer = "Lokasi Terbuka: " + countUnlockedAreas() + "/" + areaNames.length;
        int footerX = getHorizontalCenter(footer, g2, gp.screenWidth);
        g2.drawString(footer, footerX, gp.screenHeight - gp.tileSize);
    }

    private int countUnlockedAreas() {
        int count = 0;
        for (boolean unlocked : unlockedAreas) {
            if (unlocked)
                count++;
        }
        return count;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }
}
