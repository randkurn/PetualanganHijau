package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.imageio.ImageIO;

import controller.GamePanel;

public class InventoryScreen extends UI {
    
    private BufferedImage itemBox;
    private BufferedImage highlightSlot;
    private BufferedImage bottomPatternBG;
    
    private int selectedSlot = 0;
    private static final int SLOTS_PER_ROW = 5;
    private static final int MAX_ROWS = 4;
    
    protected InventoryScreen(GamePanel gp) {
        super(gp);
        loadUiTextures();
    }
    
    private void loadUiTextures() {
        itemBox = loadImage("/ui/ItemBox_24x24.png");
        highlightSlot = loadImage("/ui/HighlightSlot_26x26.png");
        bottomPatternBG = loadImage("/ui/BottomPatternBG_128x112.png");
    }
    
    private BufferedImage loadImage(String path) {
        try (InputStream stream = getClass().getResourceAsStream(path)) {
            if (stream != null) {
                return ImageIO.read(stream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void draw(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 220));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        
        int panelWidth = 700;
        int panelHeight = 580;
        int panelX = (gp.screenWidth - panelWidth) / 2;
        int panelY = (gp.screenHeight - panelHeight) / 2;
        
        if (bottomPatternBG != null) {
            for (int y = 0; y < panelHeight; y += 112) {
                for (int x = 0; x < panelWidth; x += 128) {
                    g2.drawImage(bottomPatternBG, panelX + x, panelY + y, 
                               Math.min(128, panelWidth - x), Math.min(112, panelHeight - y), null);
                }
            }
        } else {
            g2.setColor(new Color(40, 40, 60, 240));
            g2.fillRoundRect(panelX, panelY, panelWidth, panelHeight, 20, 20);
        }
        
        g2.setColor(new Color(150, 150, 200));
        g2.setStroke(new java.awt.BasicStroke(3));
        g2.drawRoundRect(panelX, panelY, panelWidth, panelHeight, 20, 20);
        
        g2.setFont(pressStart2P.deriveFont(Font.PLAIN, 28));
        String title = "INVENTORY";
        int titleX = panelX + (panelWidth - g2.getFontMetrics().stringWidth(title)) / 2;
        
        g2.setColor(new Color(0, 0, 0, 180));
        g2.drawString(title, titleX + 2, panelY + 47);
        
        g2.setColor(new Color(255, 230, 100));
        g2.drawString(title, titleX, panelY + 45);
        
        g2.setFont(pressStart2P.deriveFont(Font.PLAIN, 16));
        String slotInfo = gp.player.inventory.getUsedSlots() + "/" + gp.player.inventory.getMaxSlots() + " Slots";
        int infoX = panelX + (panelWidth - g2.getFontMetrics().stringWidth(slotInfo)) / 2;
        g2.setColor(Color.WHITE);
        g2.drawString(slotInfo, infoX, panelY + 80);
        
        drawInventoryGrid(g2, panelX, panelY, panelWidth);
        
        g2.setFont(pressStart2P.deriveFont(Font.PLAIN, 14));
        g2.setColor(new Color(220, 220, 220));
        String instructions = "Arrow Keys: Navigate | ESC/I: Close";
        int instX = panelX + (panelWidth - g2.getFontMetrics().stringWidth(instructions)) / 2;
        g2.drawString(instructions, instX, panelY + panelHeight - 25);
    }
    
    private void drawInventoryGrid(Graphics2D g2, int panelX, int panelY, int panelWidth) {
        int startX = panelX + 60;
        int startY = panelY + 110;
        int slotSize = 72;
        int gap = 24;
        
        Map<String, Integer> items = gp.player.inventory.getAllItems();
        String[] itemNames = items.keySet().toArray(new String[0]);
        
        for (int row = 0; row < MAX_ROWS; row++) {
            for (int col = 0; col < SLOTS_PER_ROW; col++) {
                int index = row * SLOTS_PER_ROW + col;
                int x = startX + col * (slotSize + gap);
                int y = startY + row * (slotSize + gap);
                
                if (itemBox != null) {
                    g2.drawImage(itemBox, x, y, slotSize, slotSize, null);
                } else {
                    g2.setColor(new Color(60, 60, 80, 200));
                    g2.fillRoundRect(x, y, slotSize, slotSize, 8, 8);
                    g2.setColor(new Color(120, 120, 140));
                    g2.drawRoundRect(x, y, slotSize, slotSize, 8, 8);
                }
                
                if (index == selectedSlot) {
                    if (highlightSlot != null) {
                        g2.drawImage(highlightSlot, x - 2, y - 2, slotSize + 4, slotSize + 4, null);
                    } else {
                        g2.setColor(new Color(255, 215, 0, 180));
                        g2.setStroke(new java.awt.BasicStroke(3));
                        g2.drawRoundRect(x - 2, y - 2, slotSize + 4, slotSize + 4, 8, 8);
                    }
                }
                
                if (index < itemNames.length) {
                    String itemName = itemNames[index];
                    int quantity = items.get(itemName);
                    
                    g2.setFont(pressStart2P.deriveFont(Font.PLAIN, 10));
                    g2.setColor(Color.WHITE);
                    String displayName = itemName;
                    
                    if (g2.getFontMetrics().stringWidth(displayName) > slotSize - 8) {
                        String[] words = itemName.split(" ");
                        if (words.length > 0 && g2.getFontMetrics().stringWidth(words[0]) <= slotSize - 8) {
                            displayName = words[0];
                            if (words.length > 1) {
                                displayName += "..";
                            }
                        } else {
                            displayName = itemName.substring(0, Math.min(6, itemName.length())) + "..";
                        }
                    }
                    
                    int nameX = x + (slotSize - g2.getFontMetrics().stringWidth(displayName)) / 2;
                    g2.drawString(displayName, nameX, y + slotSize / 2 + 4);
                    
                    g2.setFont(pressStart2P.deriveFont(Font.PLAIN, 12));
                    String qtyText = "x" + quantity;
                    int qtyWidth = g2.getFontMetrics().stringWidth(qtyText);
                    int qtyX = x + slotSize - qtyWidth - 6;
                    int qtyY = y + slotSize - 18;
                    
                    g2.setColor(new Color(0, 0, 0, 200));
                    g2.fillRoundRect(qtyX - 3, qtyY - 12, qtyWidth + 6, 16, 4, 4);
                    
                    g2.setColor(new Color(255, 255, 100));
                    g2.drawString(qtyText, qtyX, y + slotSize - 6);
                }
            }
        }
        
        if (selectedSlot < itemNames.length) {
            String selectedItem = itemNames[selectedSlot];
            int quantity = items.get(selectedItem);
            
            int detailY = startY + MAX_ROWS * (slotSize + gap) + 20;
            int detailBoxX = panelX + 40;
            int detailBoxY = detailY - 5;
            int detailBoxWidth = panelWidth - 80;
            int detailBoxHeight = 70;
            
            g2.setColor(new Color(30, 30, 50, 200));
            g2.fillRoundRect(detailBoxX, detailBoxY, detailBoxWidth, detailBoxHeight, 12, 12);
            g2.setColor(new Color(100, 150, 200));
            g2.setStroke(new java.awt.BasicStroke(2));
            g2.drawRoundRect(detailBoxX, detailBoxY, detailBoxWidth, detailBoxHeight, 12, 12);
            
            g2.setFont(pressStart2P.deriveFont(Font.PLAIN, 14));
            g2.setColor(new Color(200, 200, 255));
            g2.drawString("Selected Item:", detailBoxX + 15, detailY + 20);
            
            g2.setFont(pressStart2P.deriveFont(Font.PLAIN, 16));
            g2.setColor(Color.WHITE);
            g2.drawString(selectedItem, detailBoxX + 15, detailY + 43);
            
            g2.setFont(pressStart2P.deriveFont(Font.PLAIN, 14));
            g2.setColor(new Color(255, 255, 100));
            g2.drawString("Quantity: " + quantity, detailBoxX + 15, detailY + 63);
        }
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
    
    public void reset() {
        selectedSlot = 0;
    }
}

