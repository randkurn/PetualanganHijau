package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import controller.GamePanel;

public class CharacterNameScreen extends UI {
    private StringBuilder playerName;
    private final int MAX_NAME_LENGTH = 12;

    protected CharacterNameScreen(GamePanel gp) {
        super(gp);
        this.playerName = new StringBuilder();
    }

    public void addCharacter(char c) {
        if (playerName.length() < MAX_NAME_LENGTH) {
            if (Character.isLetterOrDigit(c) || c == ' ') {
                playerName.append(c);
            }
        }
    }

    public void removeCharacter() {
        if (playerName.length() > 0) {
            playerName.deleteCharAt(playerName.length() - 1);
        }
    }

    public String getName() {
        return playerName.toString();
    }

    public boolean isNameValid() {
        return playerName.length() > 0;
    }

    public void reset() {
        playerName.setLength(0);
    }

    public void draw(Graphics2D g2) {
        g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(java.awt.RenderingHints.KEY_TEXT_ANTIALIASING,
                java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        g2.setColor(Color.WHITE);
        g2.setFont(getScaledFont(Font.BOLD, 24));
        String title = "PETUALANGAN HIJAU";
        int titleX = getHorizontalCenter(title, g2, gp.screenWidth);
        g2.drawString(title, titleX, gp.tileSize * 2);

        g2.setFont(getScaledFont(Font.PLAIN, 16));
        String prompt = "Masukkan Nama Karaktermu:";
        int promptX = getHorizontalCenter(prompt, g2, gp.screenWidth);
        g2.drawString(prompt, promptX, gp.tileSize * 4);

        float uiScale = getUIScale();
        int boxWidth = (int) (400 * uiScale);
        int boxHeight = (int) (60 * uiScale);
        int boxX = (gp.screenWidth - boxWidth) / 2;
        int boxY = gp.tileSize * 5;

        g2.setColor(new Color(255, 255, 255, 30));
        g2.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 10, 10);
        g2.setColor(Color.WHITE);
        g2.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 10, 10);

        g2.setFont(getScaledFont(Font.PLAIN, 20));
        String displayName = playerName.toString();
        if (displayName.isEmpty()) {
            displayName = "_";
        }
        int nameX = getHorizontalCenter(displayName, g2, gp.screenWidth);
        g2.drawString(displayName, nameX, boxY + (int) (38 * uiScale));

        g2.setFont(getScaledFont(Font.PLAIN, 12));
        g2.setColor(new Color(200, 200, 200));

        String instr1 = "Ketik nama (max " + MAX_NAME_LENGTH + " karakter)";
        int instr1X = getHorizontalCenter(instr1, g2, gp.screenWidth);
        g2.drawString(instr1, instr1X, gp.screenHeight - gp.tileSize * 3);

        String instr2 = "BACKSPACE: Hapus | ENTER: Konfirmasi";
        int instr2X = getHorizontalCenter(instr2, g2, gp.screenWidth);
        g2.drawString(instr2, instr2X, gp.screenHeight - gp.tileSize * 2);

        if (!isNameValid()) {
            g2.setColor(new Color(255, 100, 100));
            String warning = "Nama harus diisi!";
            int warningX = getHorizontalCenter(warning, g2, gp.screenWidth);
            g2.drawString(warning, warningX, boxY + boxHeight + (int) (30 * uiScale));
        }
    }
}
