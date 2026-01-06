package view;

import controller.GamePanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * River Catch Minigame - Move net to catch floating trash, avoid fish
 * Target: Catch 10 trash items
 */
public class RiverCatchMinigame {

    private GamePanel gp;
    private boolean isActive = false;
    private boolean isComplete = false;

    private int netX;
    private int netY;
    private final int NET_SIZE = 60;
    private final int NET_SPEED = 8;

    private List<FloatingObject> objects;
    private int trashCaught = 0;
    private final int TARGET_TRASH = 10;

    private Font titleFont;
    private Font textFont;
    private Random rand;

    private Runnable onComplete;
    private int frameCounter = 0;

    public RiverCatchMinigame(GamePanel gp) {
        this.gp = gp;
        this.titleFont = new Font("Arial", Font.BOLD, 32);
        this.textFont = new Font("Arial", Font.PLAIN, 20);
        this.objects = new ArrayList<>();
        this.rand = new Random();
    }

    public void start(Runnable onComplete) {
        this.isActive = true;
        this.isComplete = false;
        this.trashCaught = 0;
        this.onComplete = onComplete;
        this.frameCounter = 0;

        // Position net at left center
        this.netX = 100;
        this.netY = gp.screenHeight / 2;

        // Clear and spawn initial objects
        objects.clear();
        spawnInitialObjects();

        System.out.println("[RiverCatchMinigame] Started - Target: " + TARGET_TRASH + " trash");
    }

    private void spawnInitialObjects() {
        // Spawn some trash and fish
        for (int i = 0; i < 5; i++) {
            spawnObject();
        }
    }

    private void spawnObject() {
        int x = gp.screenWidth + rand.nextInt(200);
        int y = 150 + rand.nextInt(gp.screenHeight - 300);
        boolean isTrash = rand.nextDouble() < 0.7; // 70% trash, 30% fish
        objects.add(new FloatingObject(x, y, isTrash));
    }

    public void update() {
        if (!isActive || isComplete)
            return;

        frameCounter++;

        // Spawn new objects periodically
        if (frameCounter % 60 == 0) { // Every ~1 second
            spawnObject();
        }

        // Move net with A/D (Left/Right only)
        if (gp.isKeyPressed("a") && netX > 50) {
            netX -= NET_SPEED;
        }
        if (gp.isKeyPressed("d") && netX < gp.screenWidth - 100) {
            netX += NET_SPEED;
        }
        // Move objects from right to left
        List<FloatingObject> toRemove = new ArrayList<>();
        for (FloatingObject obj : objects) {
            obj.x -= 3; // Move left

            // Remove if off-screen
            if (obj.x < -50) {
                toRemove.add(obj);
            }

            // Check collision with net
            if (!obj.isCaught && checkCollision(obj)) {
                obj.isCaught = true;
                if (obj.isTrash) {
                    trashCaught++;
                    System.out.println("[RiverCatchMinigame] Trash caught! (" + trashCaught + "/" + TARGET_TRASH + ")");
                    toRemove.add(obj);
                } else {
                    // Caught a fish - penalty (lose 1 trash count if any)
                    if (trashCaught > 0) {
                        trashCaught--;
                        System.out.println("[RiverCatchMinigame] Oops! Caught a fish. Trash count reduced.");
                    }
                    toRemove.add(obj);
                }
            }
        }

        objects.removeAll(toRemove);

        // Check win condition
        if (trashCaught >= TARGET_TRASH) {
            complete();
        }
    }

    private boolean checkCollision(FloatingObject obj) {
        return obj.x >= netX - NET_SIZE / 2 && obj.x <= netX + NET_SIZE / 2 &&
                obj.y >= netY - NET_SIZE / 2 && obj.y <= netY + NET_SIZE / 2;
    }

    public void draw(Graphics2D g2) {
        if (!isActive)
            return;

        // Semi-transparent background (water)
        g2.setColor(new Color(30, 144, 255, 200));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        // Title
        g2.setFont(titleFont);
        g2.setColor(Color.WHITE);
        String title = controller.StoryManager.getInstance().getDialog("minigame_river_title");
        int titleWidth = g2.getFontMetrics().stringWidth(title);
        g2.drawString(title, (gp.screenWidth - titleWidth) / 2, 60);

        // Instructions
        g2.setFont(textFont);
        String desc = controller.StoryManager.getInstance().getDialog("minigame_river_desc");
        int descWidth = g2.getFontMetrics().stringWidth(desc);
        g2.drawString(desc, (gp.screenWidth - descWidth) / 2, 100);

        // Progress
        String progress = "Target: " + trashCaught + "/" + TARGET_TRASH + " Sampah";
        g2.drawString(progress, 20, gp.screenHeight - 50);

        // Controls hint
        g2.drawString("Kontrol: A/D (Kiri/Kanan)", gp.screenWidth - 250, gp.screenHeight - 50);

        // Draw floating objects
        for (FloatingObject obj : objects) {
            if (!obj.isCaught) {
                if (obj.isTrash) {
                    // Trash (brown/gray bag)
                    g2.setColor(new Color(128, 128, 128));
                    g2.fillRect(obj.x - 15, obj.y - 15, 30, 30);
                    g2.setColor(Color.BLACK);
                    g2.drawRect(obj.x - 15, obj.y - 15, 30, 30);
                } else {
                    // Fish (orange/red)
                    g2.setColor(new Color(255, 140, 0));
                    g2.fillOval(obj.x - 15, obj.y - 10, 30, 20);
                    // Fish tail
                    int[] xPoints = { obj.x - 15, obj.x - 25, obj.x - 15 };
                    int[] yPoints = { obj.y - 5, obj.y, obj.y + 5 };
                    g2.fillPolygon(xPoints, yPoints, 3);
                }
            }
        }

        // Draw net (green circle with grid)
        g2.setColor(new Color(0, 255, 0, 150));
        g2.fillOval(netX - NET_SIZE / 2, netY - NET_SIZE / 2, NET_SIZE, NET_SIZE);
        g2.setColor(new Color(0, 200, 0));
        g2.drawOval(netX - NET_SIZE / 2, netY - NET_SIZE / 2, NET_SIZE, NET_SIZE);
        // Grid pattern
        g2.drawLine(netX - NET_SIZE / 2, netY, netX + NET_SIZE / 2, netY);
        g2.drawLine(netX, netY - NET_SIZE / 2, netX, netY + NET_SIZE / 2);

        // Completion message
        if (isComplete) {
            g2.setFont(titleFont);
            g2.setColor(Color.GREEN);
            String completeMsg = controller.StoryManager.getInstance().getDialog("minigame_river_complete");
            int msgWidth = g2.getFontMetrics().stringWidth(completeMsg);
            g2.drawString(completeMsg, (gp.screenWidth - msgWidth) / 2, gp.screenHeight / 2);
        }
    }

    private void complete() {
        isComplete = true;
        System.out.println("[RiverCatchMinigame] Completed!");

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

    // Inner class for floating objects
    private static class FloatingObject {
        int x, y;
        boolean isTrash;
        boolean isCaught = false;

        FloatingObject(int x, int y, boolean isTrash) {
            this.x = x;
            this.y = y;
            this.isTrash = isTrash;
        }
    }
}
