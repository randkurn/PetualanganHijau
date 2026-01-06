package view;

import controller.GamePanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Drainage Minigame - Click on trash piles to clean the drainage
 * Target: Remove 5 trash piles
 */
public class DrainageMinigame {

    private GamePanel gp;
    private boolean isActive = false;
    private boolean isComplete = false;

    private List<TrashPile> trashPiles;
    private int trashRemoved = 0;
    private final int TARGET_TRASH = 5;

    private Font titleFont;
    private Font textFont;

    private Runnable onComplete;

    public DrainageMinigame(GamePanel gp) {
        this.gp = gp;
        this.titleFont = new Font("Arial", Font.BOLD, 32);
        this.textFont = new Font("Arial", Font.PLAIN, 20);
        this.trashPiles = new ArrayList<>();
    }

    private int selectedTrash = 0; // Currently selected trash index

    public void start(Runnable onComplete) {
        this.isActive = true;
        this.isComplete = false;
        this.trashRemoved = 0;
        this.selectedTrash = 0;
        this.onComplete = onComplete;

        // Generate 5 trash piles at random positions
        trashPiles.clear();
        Random rand = new Random();
        for (int i = 0; i < TARGET_TRASH; i++) {
            int x = 100 + rand.nextInt(gp.screenWidth - 200);
            int y = 150 + rand.nextInt(gp.screenHeight - 300);
            trashPiles.add(new TrashPile(x, y));
        }

        // Ensure selectedTrash points to an active trash pile if any exist
        if (!trashPiles.isEmpty()) {
            moveSelection(0); // Initialize selection to the first non-removed trash
        }

        System.out.println("[DrainageMinigame] Started with " + TARGET_TRASH + " trash piles");
        System.out.println("[DrainageMinigame] Controls: Arrow Keys to select, SPACE/ENTER to remove");
    }

    public void update() {
        if (!isActive || isComplete)
            return;

        // Keyboard controls
        if (gp.isKeyPressed("left")) {
            moveSelection(-1);
            gp.resetKey("left");
        }
        if (gp.isKeyPressed("right")) {
            moveSelection(1);
            gp.resetKey("right");
        }
        if (gp.isKeyPressed("up")) {
            moveSelection(-2);
            gp.resetKey("up");
        }
        if (gp.isKeyPressed("down")) {
            moveSelection(2);
            gp.resetKey("down");
        }

        // Space or Enter to remove selected trash
        if (gp.isKeyPressed("space") || gp.isKeyPressed("enter")) {
            removeSelectedTrash();
            gp.resetKey("space");
            gp.resetKey("enter");
        }

        // Check if all trash removed
        if (trashRemoved >= TARGET_TRASH) {
            complete();
        }
    }

    private void moveSelection(int direction) {
        if (trashPiles.isEmpty())
            return;

        // Find next non-removed trash
        int startIndex = selectedTrash;
        int attempts = 0;
        do {
            selectedTrash = (selectedTrash + direction + trashPiles.size()) % trashPiles.size();
            attempts++;
            if (!trashPiles.get(selectedTrash).isRemoved) {
                return; // Found valid trash
            }
        } while (selectedTrash != startIndex && attempts < trashPiles.size());
    }

    private void removeSelectedTrash() {
        if (selectedTrash >= 0 && selectedTrash < trashPiles.size()) {
            TrashPile trash = trashPiles.get(selectedTrash);
            if (!trash.isRemoved) {
                trash.isRemoved = true;
                trashRemoved++;
                System.out.println("[DrainageMinigame] Trash removed! (" + trashRemoved + "/" + TARGET_TRASH + ")");

                // Move to next trash
                moveSelection(1);
            }
        }
    }

    public void draw(Graphics2D g2) {
        if (!isActive)
            return;

        // Semi-transparent background
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        // Title
        g2.setFont(titleFont);
        g2.setColor(Color.WHITE);
        String title = controller.StoryManager.getInstance().getDialog("minigame_drain_title");
        int titleWidth = g2.getFontMetrics().stringWidth(title);
        g2.drawString(title, (gp.screenWidth - titleWidth) / 2, 60);

        // Instructions
        g2.setFont(textFont);
        String desc = controller.StoryManager.getInstance().getDialog("minigame_drain_desc");
        int descWidth = g2.getFontMetrics().stringWidth(desc);
        g2.drawString(desc, (gp.screenWidth - descWidth) / 2, 100);

        // Controls
        g2.setColor(Color.YELLOW);
        String controls = "Kontrol: Arrow Keys = Pilih, SPACE/ENTER = Angkat";
        int controlWidth = g2.getFontMetrics().stringWidth(controls);
        g2.drawString(controls, (gp.screenWidth - controlWidth) / 2, 130);

        // Progress
        g2.setColor(Color.WHITE);
        String progress = "Target: " + trashRemoved + "/" + TARGET_TRASH + " Sampah terangkat";
        int progWidth = g2.getFontMetrics().stringWidth(progress);
        g2.drawString(progress, (gp.screenWidth - progWidth) / 2, gp.screenHeight - 50);

        // Draw trash piles
        for (int i = 0; i < trashPiles.size(); i++) {
            TrashPile trash = trashPiles.get(i);
            if (!trash.isRemoved) {
                boolean isSelected = (i == selectedTrash);

                // Trash pile (brown rectangle with darker outline)
                g2.setColor(new Color(139, 69, 19));
                g2.fillRect(trash.x - 25, trash.y - 25, 50, 50);

                // Selection highlight
                if (isSelected) {
                    g2.setColor(Color.YELLOW);
                    g2.setStroke(new java.awt.BasicStroke(3));
                    g2.drawRect(trash.x - 28, trash.y - 28, 56, 56);
                    g2.setStroke(new java.awt.BasicStroke(1));
                } else {
                    g2.setColor(new Color(101, 50, 15));
                    g2.drawRect(trash.x - 25, trash.y - 25, 50, 50);
                }

                // Number label
                g2.setColor(Color.WHITE);
                g2.setFont(textFont);
                String label = String.valueOf(i + 1);
                int labelWidth = g2.getFontMetrics().stringWidth(label);
                g2.drawString(label, trash.x - labelWidth / 2, trash.y + 5);
            }
        }
        // Completion message
        if (isComplete) {
            g2.setFont(titleFont);
            g2.setColor(Color.GREEN);
            String completeMsg = controller.StoryManager.getInstance().getDialog("minigame_drain_complete");
            int msgWidth = g2.getFontMetrics().stringWidth(completeMsg);
            g2.drawString(completeMsg, (gp.screenWidth - msgWidth) / 2, gp.screenHeight / 2);
        }
    }

    private void complete() {
        isComplete = true;
        System.out.println("[DrainageMinigame] Completed!");

        // Wait 2 seconds before closing
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                isActive = false;
                if (onComplete != null) {
                    onComplete.run();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public boolean isActive() {
        return isActive;
    }

    public void close() {
        isActive = false;
    }

    // Inner class for trash pile
    private static class TrashPile {
        int x, y;
        boolean isRemoved = false;

        TrashPile(int x, int y) {
            this.x = x;
            this.y = y;
        }

    }
}
