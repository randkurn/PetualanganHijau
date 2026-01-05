package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import controller.GamePanel;
import controller.SaveManager;
import model.SaveData;

public class SaveLoadScreen extends UI {
    private boolean isSaveMode;
    private SaveData[] cachedSlots;
    private boolean[] slotHasData;

    protected SaveLoadScreen(GamePanel gp) {
        super(gp);
        totalOptions = 3;
        cachedSlots = new SaveData[3];
        slotHasData = new boolean[3];
    }

    public void setSaveMode(boolean saveMode) {
        this.isSaveMode = saveMode;
        resetSelectorPosition();
        refreshSlotCache();
    }

    private void refreshSlotCache() {
        SaveManager saveM = SaveManager.getInstance();
        for (int i = 0; i < 3; i++) {
            slotHasData[i] = saveM.hasSaveFile(i);
            if (slotHasData[i]) {
                cachedSlots[i] = saveM.getSavePreview(i);
            } else {
                cachedSlots[i] = null;
            }
        }
    }

    public boolean isSaveMode() {
        return isSaveMode;
    }

    public void draw(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        g2.setColor(Color.WHITE);
        g2.setFont(getScaledFont(Font.BOLD, 32));

        String title = isSaveMode ? "SAVE GAME" : "LOAD GAME";
        int titleX = getHorizontalCenter(title, g2, gp.screenWidth);
        g2.drawString(title, titleX, gp.tileSize * 2);

        g2.setFont(getScaledFont(Font.PLAIN, 24));

        int startY = gp.tileSize * 4;
        int spacing = gp.tileSize * 2;

        for (int i = 0; i < 3; i++) {
            int slotY = startY + (i * spacing);

            String slotText = "SLOT " + (i + 1) + ": ";
            boolean hasData = slotHasData[i];

            if (hasData) {
                SaveData data = cachedSlots[i];
                if (data != null) {
                    if (data.time != null) {
                        slotText += "Day " + data.time.currentDay;
                    }
                    if (data.player != null) {
                        // Tambah nama karakter & gold supaya pemain yakin slot mana yang dipilih
                        String name = data.player.playerName != null ? data.player.playerName : "Tanpa Nama";
                        slotText += " | " + name + " | Gold: " + data.player.gold;
                    }
                } else {
                    slotText += "Corrupted";
                }
            } else {
                g2.setColor(Color.GRAY);
                slotText += "Empty";
            }

            g2.drawString(slotText, gp.tileSize * 2, slotY);

            if (selectPosition == i) {
                g2.setColor(Color.WHITE);
                g2.drawString(">", gp.tileSize, slotY);
            }

            g2.setColor(Color.WHITE);
        }

        g2.setFont(getScaledFont(Font.PLAIN, 16));
        String instruction = "ENTER: Select | ESC: Cancel";
        int instrX = getHorizontalCenter(instruction, g2, gp.screenWidth);
        g2.drawString(instruction, instrX, gp.screenHeight - gp.tileSize);
    }
}
