package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import java.util.HashMap;
import java.util.Map;

import controller.GamePanel;
import controller.EnvironmentManager;
import model.TrashType;

public class AdvancedInventoryScreen extends UI {

    private int selectedSlot = 0;
    private int selectedTab = 0;
    private static final int SLOTS_PER_ROW = 4;
    private static final int MAX_ROWS = 5;

    private boolean sortingMode = false;
    private int selectedBin = 0;
    private Map<String, TrashType> sortedTrash = new HashMap<>();
    private Map<String, Boolean> sortingCorrectness = new HashMap<>();
    private int correctSorts = 0;
    private int incorrectSorts = 0;

    protected AdvancedInventoryScreen(GamePanel gp) {
        super(gp);
        loadUiTextures();
    }

    private void loadUiTextures() {
    }

    public void draw(Graphics2D g2) {
        g2.setColor(new Color(139, 119, 101, 240));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        if (sortingMode) {
            drawSortingInterface(g2);
        } else {
            float uiScale = (float) gp.scale / 4.0f;
            int margin = (int) (30 * uiScale);
            int panelWidth = (int) (280 * uiScale);
            int panelHeight = gp.screenHeight - margin * 2;

            drawStatusPanel(g2, margin, margin, panelWidth, panelHeight / 2 - (int) (10 * uiScale));
            drawAttributesPanel(g2, margin, margin + panelHeight / 2 + (int) (10 * uiScale), panelWidth,
                    panelHeight / 2 - (int) (10 * uiScale));

            int centerX = margin + panelWidth + (int) (20 * uiScale);
            drawEquipmentPanel(g2, centerX, margin, panelWidth, panelHeight);

            int rightX = centerX + panelWidth + (int) (20 * uiScale);
            int rightWidth = gp.screenWidth - rightX - margin;
            drawInventoryPanel(g2, rightX, margin, rightWidth, panelHeight);
        }

        drawControls(g2);
    }

    private void drawStatusPanel(Graphics2D g2, int x, int y, int width, int height) {
        float uiScale = (float) gp.scale / 4.0f;
        drawPanelBackground(g2, x, y, width, height, "STATUS");

        int contentY = y + (int) (60 * uiScale);
        int leftPad = x + (int) (20 * uiScale);

        g2.setFont(getScaledFont(Font.PLAIN, 14));
        g2.setColor(new Color(255, 240, 200));

        g2.drawString("Energy", leftPad, contentY);
        g2.drawString(gp.player.energy + " / " + gp.player.maxEnergy, leftPad + (int) (150 * uiScale), contentY);
        contentY += (int) (30 * uiScale);

        g2.drawString("Gold", leftPad, contentY);
        g2.setColor(new Color(255, 215, 0));
        g2.drawString(String.valueOf(gp.player.gold), leftPad + (int) (150 * uiScale), contentY);
        g2.setColor(new Color(255, 240, 200));
        contentY += (int) (30 * uiScale);

        g2.drawString("Score", leftPad, contentY);
        g2.drawString(String.valueOf(gp.player.score), leftPad + (int) (150 * uiScale), contentY);
    }

    private void drawAttributesPanel(Graphics2D g2, int x, int y, int width, int height) {
        float uiScale = getUIScale();
        drawPanelBackground(g2, x, y, width, height, "ATTRIBUTES");

        int contentY = y + (int) (60 * uiScale);
        int leftPad = x + (int) (20 * uiScale);

        g2.setFont(getScaledFont(Font.PLAIN, 14));
        g2.setColor(new Color(255, 240, 200));

        g2.drawString("Level", leftPad, contentY);
        g2.drawString("1", leftPad + (int) (150 * uiScale), contentY);
        contentY += (int) (30 * uiScale);

        g2.drawString("Cleaning", leftPad, contentY);
        drawProgressBar(g2, leftPad + (int) (150 * uiScale), contentY - (int) (12 * uiScale), (int) (80 * uiScale),
                (int) (14 * uiScale), 0.3f, new Color(0, 150, 255));
        contentY += (int) (30 * uiScale);

        g2.drawString("Sorting", leftPad, contentY);
        drawProgressBar(g2, leftPad + (int) (150 * uiScale), contentY - (int) (12 * uiScale), (int) (80 * uiScale),
                (int) (14 * uiScale), 0.5f, new Color(100, 200, 50));
    }

    private void drawEquipmentPanel(Graphics2D g2, int x, int y, int width, int height) {
        float uiScale = (float) gp.scale / 4.0f;
        drawPanelBackground(g2, x, y, width, height, "IKHTISAR PEMAIN");

        g2.setFont(getScaledFont(Font.BOLD, 18));
        g2.setColor(Color.WHITE);
        String pName = gp.player.getPlayerName();
        int nameX = x + (width - g2.getFontMetrics().stringWidth(pName)) / 2;
        g2.drawString(pName, nameX, y + (int) (80 * uiScale));

        g2.setFont(getScaledFont(Font.PLAIN, 10));
        g2.setColor(new Color(255, 215, 0));
        String pTitle = "Pelindung Lingkungan";
        int titleX = x + (width - g2.getFontMetrics().stringWidth(pTitle)) / 2;
        g2.drawString(pTitle, titleX, y + (int) (105 * uiScale));

        int spriteSize = (int) (120 * uiScale);
        int spriteX = x + (width - spriteSize) / 2;
        int spriteY = y + (int) (130 * uiScale);

        g2.setColor(new Color(20, 20, 25, 150));
        g2.fillOval(spriteX - 10, spriteY + spriteSize - 20, spriteSize + 20, 30);

        BufferedImage playerImg = getPlayerSprite();
        if (playerImg != null) {
            g2.drawImage(playerImg, spriteX, spriteY, spriteSize, spriteSize, null);
        }

        int statsY = y + height - (int) (150 * uiScale);
        g2.setFont(getScaledFont(Font.BOLD, 12));
        g2.setColor(new Color(150, 150, 200));
        g2.drawString("REKAP STATS", x + (int) (30 * uiScale), statsY);

        g2.setFont(getScaledFont(Font.PLAIN, 11));
        g2.setColor(Color.WHITE);
        statsY += (int) (30 * uiScale);
        g2.drawString("Score: " + gp.player.score, x + (int) (30 * uiScale), statsY);
        statsY += (int) (25 * uiScale);
        g2.drawString("Energy: " + gp.player.energy + "/" + gp.player.maxEnergy, x + (int) (30 * uiScale), statsY);
        statsY += (int) (25 * uiScale);
        g2.drawString("Gold: " + gp.player.gold, x + (int) (30 * uiScale), statsY);
    }

    private BufferedImage getPlayerSprite() {
        if (gp.player == null)
            return null;

        switch (gp.player.direction) {
            case "up":
                return gp.player.up1;
            case "down":
                return gp.player.down1;
            case "left":
                return gp.player.left1;
            case "right":
                return gp.player.right1;
            default:
                return gp.player.down1;
        }
    }

    private void drawInventoryPanel(Graphics2D g2, int x, int y, int width, int height) {
        drawPanelBackground(g2, x, y, width, height, "TAS / INVENTORI");

        int contentY = y + 60;

        g2.setFont(pressStart2P.deriveFont(Font.PLAIN, 11));
        String slotInfo = "Kapasitas: " + gp.player.inventory.getUsedSlots() + "/" + gp.player.inventory.getMaxSlots();
        g2.setColor(new Color(180, 180, 180));
        g2.drawString(slotInfo, x + 25, contentY);

        String goldText = "Gold: " + gp.player.gold;
        int goldX = x + width - g2.getFontMetrics().stringWidth(goldText) - 25;
        g2.setColor(new Color(255, 215, 0));
        g2.drawString(goldText, goldX, contentY);

        contentY += 20;
        drawInventoryGrid(g2, x + (width - (SLOTS_PER_ROW * 76)) / 2, contentY, width - 40);

        int detailY = y + height - 120;
        drawSelectedItemDetails(g2, x + 15, detailY, width - 30);

        drawEnvironmentalMeter(g2, x + 15, detailY - 90, width - 30);
    }

    private void drawEnvironmentalMeter(Graphics2D g2, int x, int y, int width) {
        EnvironmentManager em = EnvironmentManager.getInstance();
        float cleanliness = em.getScoreCleanliness();

        g2.setFont(pressStart2P.deriveFont(Font.BOLD, 12));
        g2.setColor(new Color(255, 255, 255));
        g2.drawString("KESEHATAN LINGKUNGAN: " + String.format("%.1f", cleanliness) + "%", x, y);

        Color textColor;
        String status;
        if (cleanliness > 80) {
            textColor = new Color(50, 255, 50);
            status = "Sangat Bersih";
        } else if (cleanliness > 50) {
            textColor = new Color(255, 255, 50);
            status = "Sedikit Tercemar";
        } else if (cleanliness > 20) {
            textColor = new Color(255, 128, 0);
            status = "Tercemar";
        } else {
            textColor = new Color(255, 50, 50);
            status = "Kritis / Rusak";
        }

        g2.setFont(pressStart2P.deriveFont(Font.BOLD, 18));
        g2.setColor(textColor);
        String percentText = (int) cleanliness + "%";
        g2.drawString(percentText, x, y + 25);

        g2.setFont(pressStart2P.deriveFont(Font.PLAIN, 10));
        g2.setColor(new Color(200, 200, 200));
        g2.drawString("Status: " + status, x + 70, y + 22);
    }

    private void drawSelectedItemDetails(Graphics2D g2, int x, int y, int width) {
        Map<String, Integer> items = gp.player.inventory.getAllItems();
        String[] itemNames = items.keySet().toArray(new String[0]);

        g2.setColor(new Color(25, 25, 30, 180));
        g2.fillRoundRect(x, y, width, 100, 10, 10);
        g2.setColor(new Color(100, 100, 120));
        g2.drawRoundRect(x, y, width, 100, 10, 10);

        if (selectedSlot < itemNames.length) {
            String name = itemNames[selectedSlot];
            model.TrashType type = gp.player.inventory.getItemType(name);
            int qty = items.get(name);

            BufferedImage icon = gp.player.inventory.getItemIcon(name);
            if (icon != null) {
                g2.drawImage(icon, x + 10, y + 10, 48, 48, null);
            }

            g2.setFont(pressStart2P.deriveFont(Font.BOLD, 14));
            g2.setColor(new Color(255, 255, 100));
            g2.drawString(name, x + 70, y + 30);

            if (type != null) {
                g2.setFont(pressStart2P.deriveFont(Font.PLAIN, 10));
                g2.setColor(new Color(180, 220, 255));
                g2.drawString("Kategori: " + type.toString(), x + 70, y + 55);
            }

            g2.setFont(pressStart2P.deriveFont(Font.PLAIN, 10));
            g2.setColor(new Color(200, 200, 200));
            g2.drawString("Jumlah di tas: " + qty, x + 70, y + 75);
        } else {
            g2.setFont(pressStart2P.deriveFont(Font.PLAIN, 11));
            g2.setColor(new Color(100, 100, 100));
            g2.drawString("Pilih item untuk detail",
                    x + (width - g2.getFontMetrics().stringWidth("Pilih item untuk detail")) / 2, y + 55);
        }
    }

    private void drawInventoryGrid(Graphics2D g2, int startX, int startY, int maxWidth) {
        int slotSize = 64;
        int gap = 12;

        Map<String, Integer> items = gp.player.inventory.getAllItems();
        String[] itemNames = items.keySet().toArray(new String[0]);

        for (int row = 0; row < MAX_ROWS; row++) {
            for (int col = 0; col < SLOTS_PER_ROW; col++) {
                int index = row * SLOTS_PER_ROW + col;
                int x = startX + col * (slotSize + gap);
                int y = startY + row * (slotSize + gap);

                g2.setColor(new Color(45, 45, 50, 255));
                g2.fillRoundRect(x, y, slotSize, slotSize, 10, 10);
                g2.setColor(new Color(80, 80, 90));
                g2.drawRoundRect(x, y, slotSize, slotSize, 10, 10);

                if (index == selectedSlot && selectedTab == 0) {
                    g2.setColor(new Color(0, 255, 255, 100));
                    g2.setStroke(new java.awt.BasicStroke(3));
                    g2.drawRoundRect(x - 2, y - 2, slotSize + 4, slotSize + 4, 12, 12);

                    g2.setColor(new Color(255, 215, 0));
                    g2.setStroke(new java.awt.BasicStroke(2));
                    g2.drawRoundRect(x, y, slotSize, slotSize, 10, 10);
                }

                if (index < itemNames.length) {
                    String itemName = itemNames[index];
                    int quantity = items.get(itemName);

                    BufferedImage icon = gp.player.inventory.getItemIcon(itemName);
                    if (icon != null) {
                        g2.drawImage(icon, x + 8, y + 8, slotSize - 16, slotSize - 16, null);
                    } else {
                        g2.setFont(pressStart2P.deriveFont(Font.PLAIN, 9));
                        g2.setColor(Color.WHITE);
                        String displayName = itemName;
                        if (g2.getFontMetrics().stringWidth(displayName) > slotSize - 8) {
                            String[] words = itemName.split(" ");
                            displayName = (words.length > 0
                                    && g2.getFontMetrics().stringWidth(words[0]) <= slotSize - 8)
                                            ? words[0] + ".."
                                            : itemName.substring(0, Math.min(5, itemName.length())) + "..";
                        }
                        int nameX = x + (slotSize - g2.getFontMetrics().stringWidth(displayName)) / 2;
                        g2.drawString(displayName, nameX, y + slotSize / 2 + 4);
                    }

                    g2.setFont(pressStart2P.deriveFont(Font.PLAIN, 11));
                    String qtyText = "x" + quantity;
                    int qtyWidth = g2.getFontMetrics().stringWidth(qtyText);
                    int qtyX = x + slotSize - qtyWidth - 6;
                    int qtyY = y + slotSize - 18;

                    g2.setColor(new Color(0, 0, 0, 220));
                    g2.fillRoundRect(qtyX - 3, qtyY - 12, qtyWidth + 6, 16, 4, 4);
                    g2.setColor(new Color(255, 255, 100));
                    g2.drawString(qtyText, qtyX, y + slotSize - 6);
                }
            }
        }
    }

    private void drawPanelBackground(Graphics2D g2, int x, int y, int width, int height, String title) {
        float uiScale = getUIScale();
        g2.setColor(new Color(30, 30, 35, 230));
        g2.fillRoundRect(x, y, width, height, (int) (15 * uiScale), (int) (15 * uiScale));

        g2.setColor(new Color(100, 150, 160, 200));
        g2.setStroke(new java.awt.BasicStroke(2 * uiScale));
        g2.drawRoundRect(x, y, width, height, (int) (15 * uiScale), (int) (15 * uiScale));

        g2.setColor(new Color(45, 45, 55));
        g2.fillRoundRect(x + (int) (5 * uiScale), y + (int) (5 * uiScale), width - (int) (10 * uiScale),
                (int) (35 * uiScale), (int) (10 * uiScale), (int) (10 * uiScale));

        g2.setColor(new Color(255, 215, 0, 150));
        g2.fillRect(x + (int) (15 * uiScale), y + (int) (36 * uiScale), width - (int) (30 * uiScale),
                (int) (2 * uiScale));

        g2.setFont(getScaledFont(Font.BOLD, 14));
        g2.setColor(new Color(255, 230, 180));
        int titleX = x + (width - g2.getFontMetrics().stringWidth(title)) / 2;
        g2.drawString(title, titleX, y + (int) (28 * uiScale));
    }

    private void drawProgressBar(Graphics2D g2, int x, int y, int width, int height, float progress, Color barColor) {
        g2.setColor(new Color(40, 30, 25));
        g2.fillRect(x, y, width, height);

        int fillWidth = (int) (width * progress);
        g2.setColor(barColor);
        g2.fillRect(x, y, fillWidth, height);

        g2.setColor(new Color(200, 180, 150));
        g2.drawRect(x, y, width, height);
    }

    private void drawControls(Graphics2D g2) {
        float uiScale = getUIScale();
        g2.setFont(getScaledFont(Font.PLAIN, 12));
        g2.setColor(new Color(255, 255, 255, 200));
        String controls = "Arrow: Navigasi | ESC/I: Keluar | TAB: Ganti Panel | BUANG (X)";
        int controlsX = (gp.screenWidth - g2.getFontMetrics().stringWidth(controls)) / 2;
        g2.drawString(controls, controlsX, gp.screenHeight - (int) (20 * uiScale));
    }

    public void moveSelection(int direction) {
        int row = selectedSlot / SLOTS_PER_ROW;
        int col = selectedSlot % SLOTS_PER_ROW;

        switch (direction) {
            case 0:
                row = Math.max(0, row - 1);
                break;
            case 1:
                col = Math.min(SLOTS_PER_ROW - 1, col + 1);
                break;
            case 2:
                row = Math.min(MAX_ROWS - 1, row + 1);
                break;
            case 3:
                col = Math.max(0, col - 1);
                break;
        }

        selectedSlot = row * SLOTS_PER_ROW + col;
    }

    public void discardSelectedItem() {
        Map<String, Integer> items = gp.player.inventory.getAllItems();
        String[] itemNames = items.keySet().toArray(new String[0]);

        if (selectedSlot < itemNames.length) {
            String name = itemNames[selectedSlot];
            TrashType type = gp.player.inventory.getItemType(name);
            int qty = items.get(name);

            if (type != null) {
                float penalty = -2.0f * qty;
                EnvironmentManager.getInstance().changeCleanliness(penalty);
                gp.uiM.showMessage("Kamu membuang " + name + " sembarangan.\nLingkungan tercemar! (" + penalty + ")");
                view.AudioManager.getInstance().playSound(11);
            } else {
                gp.uiM.showMessage("Kamu membuang " + name + ".");
            }

            gp.player.inventory.removeItem(name, qty);
        }
    }

    private void drawSortingInterface(Graphics2D g2) {
        float uiScale = (float) gp.scale / 4.0f;
        if (gp.scale > 4)
            uiScale *= 0.85f;

        int margin = (int) (25 * uiScale);

        g2.setColor(new Color(20, 15, 10, 240));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        g2.setFont(pressStart2P.deriveFont(Font.BOLD, (float) (20 * uiScale)));
        g2.setColor(new Color(255, 215, 0));
        String title = "PEMILAHAN SAMPAH";
        int titleX = (gp.screenWidth - g2.getFontMetrics().stringWidth(title)) / 2;
        g2.drawString(title, titleX, (int) (40 * uiScale));

        g2.setFont(pressStart2P.deriveFont(Font.PLAIN, (float) (10 * uiScale)));
        g2.setColor(new Color(255, 240, 200));
        String inst = "1/2/3: Pilih Tempat | Arrow: Navigasi | ENTER: Buang | T: TUKAR | S: Keluar";
        int instX = (gp.screenWidth - g2.getFontMetrics().stringWidth(inst)) / 2;
        g2.drawString(inst, instX, (int) (65 * uiScale));

        int binWidth = (int) (170 * uiScale);
        int binHeight = (int) (125 * uiScale);
        int binY = (int) (90 * uiScale);
        int totalWidth = binWidth * 3 + margin * 2;
        int binStartX = (gp.screenWidth - totalWidth) / 2;

        drawBin(g2, binStartX, binY, binWidth, binHeight, "ORGANIK", TrashType.ORGANIC, selectedBin == 0);
        drawBin(g2, binStartX + binWidth + margin, binY, binWidth, binHeight, "ANORGANIK", TrashType.ANORGANIC,
                selectedBin == 1);
        drawBin(g2, binStartX + (binWidth + margin) * 2, binY, binWidth, binHeight, "B3/B", TrashType.TOXIC,
                selectedBin == 2);

        int gridY = binY + binHeight + (int) (35 * uiScale);
        int slotSize = (int) (50 * uiScale);
        int gap = (int) (8 * uiScale);
        int totalGridWidth = (SLOTS_PER_ROW * (slotSize + gap)) - gap;
        int gridX = (gp.screenWidth - totalGridWidth) / 2;
        drawCompactInventoryGrid(g2, gridX, gridY, slotSize, gap);

        drawSortingStats(g2, (int) (40 * uiScale), gridY + (int) (10 * uiScale));
        drawBonusInfo(g2, gp.screenWidth - (int) (250 * uiScale), gridY + (int) (10 * uiScale));

        drawExchangeButton(g2, gp.screenHeight - (int) (100 * uiScale));
    }

    private void drawBin(Graphics2D g2, int x, int y, int width, int height, String label, TrashType type,
            boolean selected) {
        Color binColor;
        switch (type) {
            case ORGANIC:
                binColor = new Color(34, 139, 34);
                break;
            case ANORGANIC:
                binColor = new Color(30, 144, 255);
                break;
            case TOXIC:
                binColor = new Color(220, 20, 60);
                break;
            default:
                binColor = Color.GRAY;
        }

        g2.setColor(new Color(binColor.getRed(), binColor.getGreen(), binColor.getBlue(), 150));
        g2.fillRoundRect(x, y, width, height, 20, 20);

        if (selected) {
            g2.setColor(new Color(255, 215, 0));
            g2.setStroke(new java.awt.BasicStroke(6));
        } else {
            g2.setColor(binColor.brighter());
            g2.setStroke(new java.awt.BasicStroke(3));
        }
        g2.drawRoundRect(x, y, width, height, 20, 20);

        g2.setFont(pressStart2P.deriveFont(Font.BOLD, 15));
        g2.setColor(Color.WHITE);
        int labelX = x + (width - g2.getFontMetrics().stringWidth(label)) / 2;
        g2.drawString(label, labelX, y + 35);

        g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 50));
        String icon = "🗑️";
        int iconX = x + (width - g2.getFontMetrics().stringWidth(icon)) / 2;
        g2.drawString(icon, iconX, y + 105);

        g2.setFont(pressStart2P.deriveFont(Font.PLAIN, 13));
        long count = sortedTrash.values().stream().filter(t -> t == type).count();
        String countText = count + " item" + (count != 1 ? "s" : "");
        int countX = x + (width - g2.getFontMetrics().stringWidth(countText)) / 2;
        g2.setColor(new Color(255, 255, 200));
        g2.drawString(countText, countX, y + height - 18);
    }

    private void drawSortingStats(Graphics2D g2, int x, int y) {
        g2.setFont(pressStart2P.deriveFont(Font.BOLD, 14));
        g2.setColor(new Color(255, 215, 0));
        g2.drawString("STATISTIK", x, y);

        g2.setFont(pressStart2P.deriveFont(Font.PLAIN, 12));
        g2.setColor(new Color(150, 255, 150));
        g2.drawString("✓ Benar: " + correctSorts, x, y + 25);

        g2.setColor(new Color(255, 150, 150));
        g2.drawString("✗ Salah: " + incorrectSorts, x, y + 45);

        int total = correctSorts + incorrectSorts;
        int accuracy = total > 0 ? (correctSorts * 100) / total : 0;
        g2.setColor(Color.WHITE);
        g2.drawString("★ Akurasi: " + accuracy + "%", x, y + 65);
    }

    public void sortSelectedItem() {
        Map<String, Integer> items = gp.player.inventory.getAllItems();
        String[] itemNames = items.keySet().toArray(new String[0]);

        if (selectedSlot >= itemNames.length)
            return;

        String selectedItem = itemNames[selectedSlot];

        if (sortedTrash.containsKey(selectedItem)) {
            gp.uiM.showMessage("Item ini sudah dipilah!");
            return;
        }

        TrashType actualType = gp.player.inventory.getItemType(selectedItem);
        if (actualType == null)
            return;

        TrashType selectedBinType = getBinType(selectedBin);
        int quantity = items.get(selectedItem);

        if (actualType == selectedBinType) {
            correctSorts += quantity;
            sortedTrash.put(selectedItem, selectedBinType);
            sortingCorrectness.put(selectedItem, true);
            gp.player.score += (10 * quantity);

            gp.uiM.showMessage("✓ BENAR! " + quantity + " " + selectedItem +
                    "\ndipilah ke " + getBinName(selectedBinType));
            view.AudioManager.getInstance().playSound(3);
        } else {
            incorrectSorts += quantity;
            sortedTrash.put(selectedItem, selectedBinType);
            sortingCorrectness.put(selectedItem, false);
            if (gp.player.score > (5 * quantity)) {
                gp.player.score -= (5 * quantity);
            }

            gp.uiM.showMessage("✗ SALAH! " + selectedItem +
                    "\nHarusnya ke " + getBinName(actualType) +
                    "\nbukan " + getBinName(selectedBinType));
            view.AudioManager.getInstance().playSound(11);
        }
    }

    private String getBinName(TrashType type) {
        switch (type) {
            case ORGANIC:
                return "ORGANIK";
            case ANORGANIC:
                return "ANORGANIK";
            case TOXIC:
                return "B3";
            default:
                return "?";
        }
    }

    private TrashType getBinType(int binIndex) {
        switch (binIndex) {
            case 0:
                return TrashType.ORGANIC;
            case 1:
                return TrashType.ANORGANIC;
            case 2:
                return TrashType.TOXIC;
            default:
                return TrashType.ORGANIC;
        }
    }

    public void toggleSortingMode() {
        sortingMode = !sortingMode;
        if (sortingMode) {
            selectedSlot = 0;
            selectedBin = 0;
        }
    }

    public boolean isSortingMode() {
        return sortingMode;
    }

    public void selectBin(int binIndex) {
        if (binIndex >= 0 && binIndex < 3) {
            selectedBin = binIndex;
        }
    }

    public int getSortingScore() {
        return correctSorts * 10 - incorrectSorts * 5;
    }

    public void resetSorting() {
        sortedTrash.clear();
        sortingCorrectness.clear();
        correctSorts = 0;
        incorrectSorts = 0;
        selectedBin = 0;
    }

    public void reset() {
        selectedSlot = 0;
        selectedTab = 0;
    }

    public Map<String, TrashType> getSortedTrash() {
        return new HashMap<>(sortedTrash);
    }

    public void clearSortedTrash() {
        sortedTrash.clear();
        sortingCorrectness.clear();
    }

    private void drawExchangeButton(Graphics2D g2, int y) {
        float uiScale = (float) gp.scale / 4.0f;
        int buttonWidth = (int) (280 * uiScale);
        int buttonHeight = (int) (50 * uiScale);
        int buttonX = (gp.screenWidth - buttonWidth) / 2;

        g2.setColor(new Color(34, 139, 34, 220));
        g2.fillRoundRect(buttonX, y, buttonWidth, buttonHeight, (int) (15 * uiScale), (int) (15 * uiScale));

        g2.setColor(new Color(255, 215, 0));
        g2.setStroke(new java.awt.BasicStroke(3 * uiScale));
        g2.drawRoundRect(buttonX, y, buttonWidth, buttonHeight, (int) (15 * uiScale), (int) (15 * uiScale));

        g2.setFont(pressStart2P.deriveFont(Font.BOLD, (float) (15 * uiScale)));
        g2.setColor(Color.WHITE);
        String text = "[T] TUKAR SAMPAH";
        int textX = buttonX + (buttonWidth - g2.getFontMetrics().stringWidth(text)) / 2;
        g2.drawString(text, textX, y + (int) (32 * uiScale));
    }

    private void drawCompactInventoryGrid(Graphics2D g2, int startX, int startY, int slotSize, int gap) {
        Map<String, Integer> items = gp.player.inventory.getAllItems();
        String[] itemNames = items.keySet().toArray(new String[0]);

        for (int row = 0; row < MAX_ROWS; row++) {
            for (int col = 0; col < SLOTS_PER_ROW; col++) {
                int index = row * SLOTS_PER_ROW + col;
                int x = startX + col * (slotSize + gap);
                int y = startY + row * (slotSize + gap);

                g2.setColor(new Color(60, 50, 40, 200));
                g2.fillRoundRect(x, y, slotSize, slotSize, 5, 5);
                g2.setColor(new Color(120, 100, 80));
                g2.drawRoundRect(x, y, slotSize, slotSize, 5, 5);

                if (index == selectedSlot) {
                    g2.setColor(new Color(255, 215, 0, 180));
                    g2.setStroke(new java.awt.BasicStroke(3));
                    g2.drawRoundRect(x - 1, y - 1, slotSize + 2, slotSize + 2, 5, 5);
                }

                if (index < itemNames.length) {
                    String itemName = itemNames[index];
                    BufferedImage icon = gp.player.inventory.getItemIcon(itemName);

                    if (icon != null) {
                        g2.drawImage(icon, x + 4, y + 4, slotSize - 8, slotSize - 8, null);
                    }

                    // Done indicator - green for correct, orange for incorrect
                    if (sortedTrash.containsKey(itemName)) {
                        boolean isCorrect = sortingCorrectness.getOrDefault(itemName, true);
                        if (isCorrect) {
                            g2.setColor(new Color(0, 255, 0, 150));
                        } else {
                            g2.setColor(new Color(255, 165, 0, 150)); // Orange for incorrect
                        }
                        g2.fillRect(x, y, slotSize, slotSize);
                        g2.setColor(Color.WHITE);
                        g2.setFont(pressStart2P.deriveFont(Font.BOLD, 14));
                        g2.drawString("v", x + slotSize / 2 - 5, y + slotSize / 2 + 5);
                    }
                }
            }
        }
    }

    public void exchangeTrashInSortingMode() {
        Map<String, Integer> playerItems = gp.player.inventory.getAllItems();

        if (playerItems.isEmpty()) {
            gp.uiM.showMessage("Tidak ada sampah untuk ditukar!");
            return;
        }

        int totalTrashTypes = 0;
        for (Map.Entry<String, Integer> entry : playerItems.entrySet()) {
            TrashType type = gp.player.inventory.getItemType(entry.getKey());
            if (type != null) {
                totalTrashTypes++;
            }
        }

        int sortedTypes = sortedTrash.size();

        if (sortedTypes < totalTrashTypes) {
            int remaining = totalTrashTypes - sortedTypes;
            gp.uiM.showMessage(
                    "━━━ PEMILAHAN BELUM SELESAI! ━━━\n\n" +
                            "Masih ada " + remaining + " jenis sampah\n" +
                            "yang belum dipilah!\n\n" +
                            "Pilah SEMUA sampah dulu\n" +
                            "sebelum bisa ditukar.\n\n" +
                            "Gunakan:\n" +
                            "1/2/3 = Pilih tempat\n" +
                            "Arrow = Pilih sampah\n" +
                            "ENTER = Buang");
            view.AudioManager.getInstance().playSound(11);
            return;
        }

        int totalGold = 0;
        int totalItems = 0;
        int bonusGold = 0;

        for (Map.Entry<String, Integer> entry : playerItems.entrySet()) {
            String name = entry.getKey();
            int qty = entry.getValue();
            TrashType type = gp.player.inventory.getItemType(name);

            if (type != null) {
                int basePrice = 10;
                switch (type) {
                    case ORGANIC:
                        basePrice = 15;
                        if (name.equals("Sisa Pisang") || name.equals("Sisa Apel"))
                            basePrice = 20;
                        break;
                    case ANORGANIC:
                        basePrice = 30;
                        if (name.equals("Botol Minum") || name.equals("Pop Es"))
                            basePrice = 40;
                        if (name.equals("Botol Tertanam"))
                            basePrice = 50;
                        break;
                    case TOXIC:
                        basePrice = 50;
                        if (name.equals("Baterai Bekas") || name.equals("Charger Rusak"))
                            basePrice = 70;
                        if (name.equals("Masker Bekas"))
                            basePrice = 35;
                        break;
                }

                int itemTotal = basePrice * qty;

                if (sortedTrash.containsKey(name) && sortedTrash.get(name) == type) {
                    int bonus = (int) (itemTotal * 0.5);
                    bonusGold += bonus;
                }

                totalGold += itemTotal;
                totalItems += qty;
                gp.player.inventory.removeItem(name, qty);
            }
        }

        if (totalItems > 0) {
            int finalGold = totalGold + bonusGold;
            gp.player.addGold(finalGold);

            // Achievement Logic
            controller.AchievementManager.getInstance(gp).unlockAchievement("Ahli Pemilah",
                    "Pertama kali memilah dan menyetorkan sampah.");

            clearSortedTrash();
            resetSorting();
            toggleSortingMode();

            view.AudioManager.getInstance().playSound(3);

            // Dialog Panjul setelah transaksi
            gp.stateM.setCurrentState(controller.StateManager.gameState.DIALOGUE);
            String msg = controller.StoryManager.getInstance().getDialog("danu_exchange_done")
                    .replace("%VALUE%", String.valueOf(finalGold));
            gp.uiM.getPlayScreen().showDialog(msg, "Panjul (Bank Sampah)");

            if (gp.chapter2Active) {
                gp.npcM.spawnTehDila(26 * gp.tileSize, 58 * gp.tileSize);
            }
        }
    }

    private void drawBonusInfo(Graphics2D g2, int x, int y) {
        int width = 200;
        int height = 70;
        g2.setColor(new Color(40, 40, 40, 220));
        g2.fillRoundRect(x, y, width, height, 10, 10);
        g2.setColor(new Color(255, 215, 0));
        g2.setStroke(new java.awt.BasicStroke(2));
        g2.drawRoundRect(x, y, width, height, 10, 10);

        g2.setFont(pressStart2P.deriveFont(Font.BOLD, 10));
        g2.setColor(new Color(255, 215, 0));
        g2.drawString("BONUS PILAH", x + 10, y + 20);

        g2.setFont(pressStart2P.deriveFont(Font.PLAIN, 9));
        g2.setColor(new Color(200, 255, 200));
        g2.drawString("BENAR: +50% Gold", x + 10, y + 40);

        g2.setColor(new Color(180, 180, 180));
        g2.drawString("Selesaikan semua", x + 10, y + 55);
        g2.drawString("sebelum ditukar.", x + 10, y + 65);
    }
}
