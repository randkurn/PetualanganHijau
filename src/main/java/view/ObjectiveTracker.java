package view;

import controller.GamePanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

/**
 * ObjectiveTracker - Shows current chapter objectives below EnviroMeter
 */
public class ObjectiveTracker {

    private GamePanel gp;
    private Font font;

    public ObjectiveTracker(GamePanel gp) {
        this.gp = gp;
        this.font = new Font("Arial", Font.PLAIN, 16);
    }

    public void draw(Graphics2D g2) {
        // Position below EnviroMeter (Y=100+100+10 = 210)
        int x = gp.screenWidth - 220;
        int y = 220;
        int width = 200;

        // Only show if there's an active objective
        String objective = getCurrentObjective();
        if (objective == null || objective.isEmpty()) {
            return;
        }

        // Semi-transparent background
        g2.setColor(new Color(0, 0, 0, 180));
        int height = 60 + (objective.length() / 25) * 20; // Dynamic height
        g2.fillRoundRect(x, y, width, height, 10, 10);

        // Title
        g2.setColor(Color.YELLOW);
        g2.setFont(font.deriveFont(Font.BOLD));
        g2.drawString("OBJEKTIF:", x + 10, y + 20);

        // Objective text (word wrap)
        g2.setColor(Color.WHITE);
        g2.setFont(font);
        drawWrappedText(g2, objective, x + 10, y + 40, width - 20);
    }

    private String getCurrentObjective() {
        // Chapter 1: Kumpulkan 5 sampah
        if (gp.chapter1Active && !gp.chapter2Active) {
            int collected = gp.player.getCollectedTrash().size();
            if (collected < 5) {
                return "Kumpulkan sampah (" + collected + "/5)";
            }
        }

        // Chapter 2: Tanam bibit pertama
        if (gp.chapter2Active && !gp.chapter2Finished) {
            if (gp.plantedTrees.isEmpty()) {
                return "Tanam bibit Mahoni pertama";
            }
        }

        // Chapter 3: Complete all tasks
        if (gp.chapter3Active) {
            int tasksCompleted = 0;
            int totalTasks = 4;

            // Check NPC interactions via Player persistent status
            if (gp.player.hasInteractedWithNPC("Bisma")) {
                tasksCompleted++;
            }
            if (gp.player.hasInteractedWithNPC("Randy")) {
                tasksCompleted++;
            }
            if (gp.player.hasInteractedWithNPC("Pak Khairul")) {
                tasksCompleted++;
            }
            if (gp.player.hasInteractedWithNPC("Neng Jia")) {
                tasksCompleted++;
            }

            if (tasksCompleted < totalTasks) {
                return "Bantu warga (" + tasksCompleted + "/" + totalTasks + ")";
            } else {
                return "Pulang & tidur untuk selesaikan chapter";
            }
        }

        return null;
    }

    private void drawWrappedText(Graphics2D g2, String text, int x, int y, int maxWidth) {
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        int lineY = y;

        for (String word : words) {
            String testLine = line + word + " ";
            int testWidth = g2.getFontMetrics().stringWidth(testLine);

            if (testWidth > maxWidth && line.length() > 0) {
                g2.drawString(line.toString().trim(), x, lineY);
                line = new StringBuilder(word + " ");
                lineY += 20;
            } else {
                line.append(word).append(" ");
            }
        }

        if (line.length() > 0) {
            g2.drawString(line.toString().trim(), x, lineY);
        }
    }
}
