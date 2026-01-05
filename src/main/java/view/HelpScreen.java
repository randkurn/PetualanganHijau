package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import controller.GamePanel;

public class HelpScreen extends UI {

    private int currentPage = 0;
    private final int totalPages = 3;

    private final Color[] pageColors = {
            new Color(50, 120, 50),
            new Color(120, 80, 40),
            new Color(40, 80, 120)
    };

    protected HelpScreen(GamePanel gp) {
        super(gp);
        totalOptions = 0;
    }

    public void draw(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawBackground(g2);

        switch (currentPage) {
            case 0 -> drawControlsPage(g2);
            case 1 -> drawTrashTypesPage(g2);
            case 2 -> drawTipsPage(g2);
        }

        drawNavigation(g2);
    }

    private void drawBackground(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 230));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        float uiScale = getUIScale();
        Color themeColor = pageColors[currentPage];
        g2.setColor(themeColor);
        g2.drawRoundRect((int) (20 * uiScale), (int) (20 * uiScale), gp.screenWidth - (int) (40 * uiScale),
                gp.screenHeight - (int) (40 * uiScale), (int) (20 * uiScale), (int) (20 * uiScale));
        g2.drawRoundRect((int) (22 * uiScale), (int) (22 * uiScale), gp.screenWidth - (int) (44 * uiScale),
                gp.screenHeight - (int) (44 * uiScale), (int) (18 * uiScale), (int) (18 * uiScale));

        g2.setColor(new Color(themeColor.getRed(), themeColor.getGreen(), themeColor.getBlue(), 30));
        g2.fillRoundRect((int) (25 * uiScale), (int) (25 * uiScale), gp.screenWidth - (int) (50 * uiScale),
                gp.screenHeight - (int) (50 * uiScale), (int) (15 * uiScale), (int) (15 * uiScale));
    }

    private void drawControlsPage(Graphics2D g2) {
        float uiScale = getUIScale();
        int y = (int) (55 * uiScale);

        drawTitle(g2, "GAME CONTROLS / KONTROL", y, new Color(100, 255, 100));
        y += (int) (45 * uiScale);

        drawSectionTitle(g2, "== MOVEMENT / GERAKAN ==", y, new Color(255, 220, 100));
        y += (int) (28 * uiScale);

        drawControlRowBilingual(g2, "W / ↑", "Move Up", "Atas", y);
        y += (int) (22 * uiScale);
        drawControlRowBilingual(g2, "A / ←", "Move Left", "Kiri", y);
        y += (int) (22 * uiScale);
        drawControlRowBilingual(g2, "S / ↓", "Move Down", "Bawah", y);
        y += (int) (22 * uiScale);
        drawControlRowBilingual(g2, "D / →", "Move Right", "Kanan", y);
        y += (int) (35 * uiScale);

        drawSectionTitle(g2, "== ACTIONS / AKSI ==", y, new Color(255, 220, 100));
        y += (int) (28 * uiScale);

        drawControlRowBilingual(g2, "E / ENTER", "Interact", "Interaksi", y);
        y += (int) (22 * uiScale);
        drawControlRowBilingual(g2, "SPACE", "Pick Up Trash", "Ambil Sampah", y);
        y += (int) (22 * uiScale);
        drawControlRowBilingual(g2, "F", "Plant Tree", "Tanam Pohon", y);
        y += (int) (35 * uiScale);

        drawSectionTitle(g2, "== MENU ==", y, new Color(255, 220, 100));
        y += (int) (28 * uiScale);

        drawControlRowBilingual(g2, "I", "Inventory", "Inventaris", y);
        y += (int) (22 * uiScale);
        drawControlRowBilingual(g2, "M", "Map", "Peta", y);
        y += (int) (22 * uiScale);
        drawControlRowBilingual(g2, "ESC", "Pause/Back", "Jeda/Kembali", y);
    }

    private void drawTrashTypesPage(Graphics2D g2) {
        float uiScale = getUIScale();
        int y = (int) (55 * uiScale);

        drawTitle(g2, "TRASH TYPES / JENIS SAMPAH", y, new Color(255, 200, 100));
        y += (int) (42 * uiScale);

        drawTrashSectionBilingual(g2, "ORGANIC / ORGANIK",
                new Color(100, 200, 100),
                "Biodegradable waste from living things",
                "Sampah yang mudah terurai alami",
                new String[] { "Food scraps", "Leaves", "Fruit peels" },
                new String[] { "Sisa makanan", "Daun", "Kulit buah" },
                y, (int) (50 * uiScale));
        y += (int) (130 * uiScale);

        drawTrashSectionBilingual(g2, "ANORGANIC / ANORGANIK",
                new Color(100, 150, 255),
                "Non-biodegradable, recyclable waste",
                "Sampah sulit terurai, dapat didaur ulang",
                new String[] { "Plastic bottles", "Paper/Cardboard", "Glass/Metal" },
                new String[] { "Botol plastik", "Kertas/Kardus", "Kaca/Logam" },
                y, (int) (50 * uiScale));
        y += (int) (130 * uiScale);

        drawTrashSectionBilingual(g2, "HAZARDOUS / B3 (BERBAHAYA)",
                new Color(255, 100, 100),
                "Toxic materials - handle with care!",
                "Bahan beracun - harus hati-hati!",
                new String[] { "Batteries", "Expired medicine", "E-waste" },
                new String[] { "Baterai", "Obat kadaluarsa", "Limbah elektronik" },
                y, (int) (50 * uiScale));
    }

    private void drawTipsPage(Graphics2D g2) {
        float uiScale = getUIScale();
        int y = (int) (55 * uiScale);

        drawTitle(g2, "TIPS & GUIDE / PANDUAN", y, new Color(100, 200, 255));
        y += (int) (45 * uiScale);

        drawSectionTitle(g2, "== HOW TO PLAY / CARA BERMAIN ==", y, new Color(255, 220, 100));
        y += (int) (32 * uiScale);

        String[][] tips = {
                { "1. Explore to find trash", "Jelajahi untuk menemukan sampah" },
                { "2. Press SPACE to collect", "Tekan SPACE untuk mengambil" },
                { "3. Bring trash to WASTE BANK", "Bawa ke BANK SAMPAH" },
                { "4. Sort correctly for +50% bonus!", "Pilah dengan benar untuk bonus!" },
                { "5. Talk to NPCs for missions", "Bicara NPC untuk misi" }
        };

        g2.setFont(getScaledFont(Font.PLAIN, 9));
        for (String[] tip : tips) {
            g2.setColor(Color.WHITE);
            g2.drawString(tip[0], (int) (50 * uiScale), y);
            g2.setColor(new Color(180, 180, 180));
            g2.drawString(tip[1], (int) (480 * uiScale), y);
            y += (int) (24 * uiScale);
        }
        y += (int) (15 * uiScale);

        drawSectionTitle(g2, "== TRASH VALUE / HARGA SAMPAH ==", y, new Color(255, 215, 0));
        y += (int) (32 * uiScale);

        g2.setFont(getScaledFont(Font.BOLD, 10));

        g2.setColor(new Color(100, 200, 100));
        g2.drawString("Organic/Organik: 5 Gold", (int) (80 * uiScale), y);
        y += (int) (24 * uiScale);
        g2.setColor(new Color(100, 150, 255));
        g2.drawString("Anorganic/Anorganik: 15 Gold", (int) (80 * uiScale), y);
        y += (int) (24 * uiScale);
        g2.setColor(new Color(255, 100, 100));
        g2.drawString("Hazardous/B3: 20 Gold", (int) (80 * uiScale), y);
        y += (int) (30 * uiScale);

        g2.setFont(getScaledFont(Font.PLAIN, 9));
        g2.setColor(new Color(200, 200, 255));
        g2.drawString("TIP: Sleep on bed to skip day / Tidur di kasur untuk skip hari", (int) (50 * uiScale), y);
    }

    private void drawTitle(Graphics2D g2, String text, int y, Color color) {
        float uiScale = getUIScale();
        g2.setFont(getScaledFont(Font.BOLD, 16));

        g2.setColor(Color.BLACK);
        g2.drawString(text, getHorizontalCenter(text, g2, gp.screenWidth) + (int) (2 * uiScale),
                y + (int) (2 * uiScale));

        g2.setColor(color);
        g2.drawString(text, getHorizontalCenter(text, g2, gp.screenWidth), y);
    }

    private void drawSectionTitle(Graphics2D g2, String text, int y, Color color) {
        g2.setFont(getScaledFont(Font.BOLD, 10));
        g2.setColor(color);
        g2.drawString(text, getHorizontalCenter(text, g2, gp.screenWidth), y);
    }

    private void drawControlRowBilingual(Graphics2D g2, String key, String actionEN, String actionID, int y) {
        float uiScale = getUIScale();
        g2.setFont(getScaledFont(Font.BOLD, 10));

        g2.setColor(new Color(255, 200, 100));
        g2.drawString(key, (int) (60 * uiScale), y);

        g2.setColor(new Color(150, 150, 150));
        g2.drawString("→", (int) (200 * uiScale), y);

        g2.setColor(Color.WHITE);
        g2.drawString(actionEN, (int) (230 * uiScale), y);

        g2.setColor(new Color(150, 200, 150));
        g2.drawString("(" + actionID + ")", (int) (450 * uiScale), y);
    }

    private void drawTrashSectionBilingual(Graphics2D g2, String title, Color titleColor,
            String descEN, String descID,
            String[] examplesEN, String[] examplesID,
            int startY, int leftMargin) {

        float uiScale = getUIScale();
        g2.setFont(getScaledFont(Font.BOLD, 10));
        g2.setColor(titleColor);
        g2.drawString(title, leftMargin, startY);

        int y = startY + (int) (18 * uiScale);

        g2.setFont(getScaledFont(Font.PLAIN, 8));
        g2.setColor(new Color(200, 200, 200));
        g2.drawString(descEN, leftMargin + (int) (15 * uiScale), y);
        y += (int) (14 * uiScale);
        g2.setColor(new Color(150, 180, 150));
        g2.drawString(descID, leftMargin + (int) (15 * uiScale), y);
        y += (int) (20 * uiScale);

        g2.setColor(new Color(180, 180, 180));
        g2.drawString("Examples / Contoh:", leftMargin + (int) (15 * uiScale), y);
        y += (int) (14 * uiScale);

        g2.setFont(getScaledFont(Font.PLAIN, 7));
        for (int i = 0; i < examplesEN.length; i++) {
            g2.setColor(new Color(200, 200, 200));
            g2.drawString("• " + examplesEN[i], leftMargin + (int) (25 * uiScale), y);
            g2.setColor(new Color(150, 180, 150));
            g2.drawString("/ " + examplesID[i], leftMargin + (int) (200 * uiScale), y);
            y += (int) (12 * uiScale);
        }
    }

    private void drawNavigation(Graphics2D g2) {
        float uiScale = getUIScale();
        int bottomY = gp.screenHeight - (int) (40 * uiScale);

        g2.setFont(getScaledFont(Font.PLAIN, 10));

        String pageIndicator = "Page/Hal " + (currentPage + 1) + "/" + totalPages;
        g2.setColor(Color.WHITE);
        g2.drawString(pageIndicator, getHorizontalCenter(pageIndicator, g2, gp.screenWidth), bottomY);

        g2.setFont(getScaledFont(Font.PLAIN, 9));
        g2.setColor(new Color(150, 150, 150));

        if (currentPage > 0) {
            g2.drawString("< A/Left: Prev", (int) (40 * uiScale), bottomY);
        }

        if (currentPage < totalPages - 1) {
            String next = "Next: D/Right >";
            int nextWidth = g2.getFontMetrics().stringWidth(next);
            g2.drawString(next, gp.screenWidth - (int) (40 * uiScale) - nextWidth, bottomY);
        }

        g2.setColor(new Color(200, 100, 100));
        String escHint = "ESC: Back/Kembali";
        g2.drawString(escHint, getHorizontalCenter(escHint, g2, gp.screenWidth), bottomY + (int) (20 * uiScale));
    }

    public void nextPage() {
        if (currentPage < totalPages - 1) {
            currentPage++;
            AudioManager.getInstance().playSound(4);
        }
    }

    public void previousPage() {
        if (currentPage > 0) {
            currentPage--;
            AudioManager.getInstance().playSound(4);
        }
    }

    public void reset() {
        currentPage = 0;
    }
}
