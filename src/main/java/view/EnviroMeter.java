package view;

import controller.GamePanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

/**
 * Enviro-Meter UI - Shows environmental health status
 * Displayed at top-right corner during gameplay
 */
public class EnviroMeter {

    private GamePanel gp;
    private Font smallFont;
    private Font tinyFont;

    public EnviroMeter(GamePanel gp) {
        this.gp = gp;
        this.smallFont = new Font("Arial", Font.BOLD, 16);
        this.tinyFont = new Font("Arial", Font.PLAIN, 12);
    }

    public void draw(Graphics2D g2) {
        // Only show during Chapter 3
        if (!gp.chapter3Active) {
            return;
        }

        // Respect Show UI setting
        if (!model.Settings.getInstance().getShowUI()) {
            return;
        }

        // Position lower to not block time display
        int x = gp.screenWidth - 220;
        int y = 100; // Lowered from 20 to 100
        int width = 200;
        int height = 100;

        // Draw semi-transparent background
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRoundRect(x, y, width, height, 10, 10);

        // Title
        g2.setColor(Color.WHITE);
        g2.setFont(smallFont);
        g2.drawString("ENVIRO-METER", x + 10, y + 20);

        // Calculate percentages based on player inventory
        int totalTrashInWorld = gp.player.getTotalTrashInWorld();
        int trashCollected = gp.player.getCollectedTrash().size();

        // Calculate CLEANLINESS percentage (NOT trash percentage)
        // 0% = dirty (no trash collected), 100% = clean (all trash collected)
        int cleanPercent = 0;
        if (totalTrashInWorld > 0) {
            cleanPercent = (int) ((trashCollected / (double) totalTrashInWorld) * 100);
        }

        // Count trees from planted trees list
        int treeCount = gp.plantedTrees.size();

        // Draw Kebersihan (Cleanliness) bar
        g2.setFont(tinyFont);
        g2.setColor(Color.WHITE);
        g2.drawString("Kebersihan:", x + 10, y + 40);

        // Determine color based on CLEANLINESS (inverse of trash)
        Color cleanColor;
        if (cleanPercent < 30) {
            cleanColor = new Color(220, 20, 20); // Red - very dirty
        } else if (cleanPercent < 70) {
            cleanColor = new Color(255, 200, 0); // Yellow - improving
        } else {
            cleanColor = new Color(40, 200, 40); // Green - clean
        }

        // Draw cleanliness bar
        int barX = x + 10;
        int barY = y + 45;
        int barWidth = width - 20;
        int barHeight = 15;

        g2.setColor(new Color(60, 60, 60));
        g2.fillRect(barX, barY, barWidth, barHeight);

        g2.setColor(cleanColor);
        int cleanBarWidth = (int) (barWidth * (cleanPercent / 100.0));
        g2.fillRect(barX, barY, cleanBarWidth, barHeight);

        g2.setColor(Color.WHITE);
        g2.drawRect(barX, barY, barWidth, barHeight);
        g2.drawString(cleanPercent + "%", barX + barWidth + 5, barY + 12);

        // Draw Pohon count
        g2.setColor(Color.WHITE);
        g2.drawString("Pohon:", x + 10, y + 75);

        Color treeColor = new Color(40, 180, 40);
        if (treeCount < 3) {
            treeColor = new Color(220, 20, 20);
        } else if (treeCount < 8) {
            treeColor = new Color(255, 200, 0);
        }

        g2.setColor(treeColor);
        g2.setFont(smallFont);
        g2.drawString(String.valueOf(treeCount), x + 70, y + 75);

        // Status indicator
        g2.setFont(tinyFont);
        String status;
        Color statusColor;

        if (cleanPercent < 30 || treeCount < 3) {
            status = "KRITIS";
            statusColor = new Color(220, 20, 20);
        } else if (cleanPercent < 70 || treeCount < 8) {
            status = "MEMBAIK";
            statusColor = new Color(255, 200, 0);
        } else {
            status = "SEHAT";
            statusColor = new Color(40, 200, 40);
        }

        g2.setColor(statusColor);
        g2.fillOval(x + 10, y + 82, 8, 8);
        g2.setColor(Color.WHITE);
        g2.drawString(status, x + 25, y + 90);
    }
}
