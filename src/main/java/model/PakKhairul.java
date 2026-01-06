package model;

import controller.GamePanel;
import java.awt.Rectangle;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;

public class PakKhairul extends Entity {

    GamePanel gp;
    public String name;
    public int screenX, screenY;
    private boolean hasInteracted = false;

    // Patrol system
    private boolean isPatrolling = true;
    private int currentWaypoint = 0;
    private int[] waypointX = { 1189 * 3, 1184 * 3, 268 * 3, 267 * 3 }; // farmer1, farmer2, farmer3, farmer4
    private int[] waypointY = { 198 * 3, 1106 * 3, 1107 * 3, 208 * 3 };
    private final int WAYPOINT_THRESHOLD = 32; // 2 tiles distance to consider "reached"
    private int patrolSpeed = 1;

    public PakKhairul(GamePanel gp) {
        this.gp = gp;
        this.name = "Pak Khairul";
        this.speed = patrolSpeed;
        this.hitbox = new Rectangle(8, 16, 32, 32);
        this.direction = "down";
        getSprites();
    }

    private void getSprites() {
        try {
            down1 = loadSprite("/NPC/khairul/down1.png");
            down2 = loadSprite("/NPC/khairul/down2.png");
            up1 = loadSprite("/NPC/khairul/up1.png");
            up2 = loadSprite("/NPC/khairul/up2.png");
            left1 = loadSprite("/NPC/khairul/left1.png");
            left2 = loadSprite("/NPC/khairul/left2.png");
            right1 = loadSprite("/NPC/khairul/right1.png");
            right2 = loadSprite("/NPC/khairul/right2.png");

            if (down1 == null) {
                System.out.println("[Pak Khairul] Sprites not found, loading fallback");
                loadFallbackSprites();
            }
        } catch (Exception e) {
            System.err.println("[Pak Khairul] Sprite load failed: " + e.getMessage());
            loadFallbackSprites();
        }
    }

    private void loadFallbackSprites() {
        down1 = loadSprite("/farmer/farmerdown1.png");
        down2 = loadSprite("/farmer/farmerdown2.png");
        up1 = loadSprite("/farmer/farmerup1.png");
        up2 = loadSprite("/farmer/farmerup2.png");
        left1 = loadSprite("/farmer/farmerleft1.png");
        left2 = loadSprite("/farmer/farmerleft2.png");
        right1 = loadSprite("/farmer/farmerright1.png");
        right2 = loadSprite("/farmer/farmerright2.png");
    }

    private BufferedImage loadSprite(String path) {
        try {
            java.io.InputStream is = getClass().getResourceAsStream(path);
            if (is == null)
                return null;
            return ImageIO.read(is);
        } catch (Exception e) {
            return null;
        }
    }

    public void setPosition(int x, int y) {
        this.worldX = x;
        this.worldY = y;
    }

    public void update() {
        // Patrol logic - only patrol if not interacted yet to make him easier to find
        if (isPatrolling && !hasInteracted) {
            patrol();
        }

        // Animate sprite
        spriteCounter++;
        if (spriteCounter > 30) {
            spriteNum = (spriteNum == 1) ? 2 : 1;
            spriteCounter = 0;
        }
    }

    private void patrol() {
        // Get target waypoint
        int targetX = waypointX[currentWaypoint];
        int targetY = waypointY[currentWaypoint];

        // Calculate distance
        int dx = targetX - worldX;
        int dy = targetY - worldY;
        double distance = Math.sqrt(dx * dx + dy * dy);

        // If reached waypoint, move to next
        if (distance < WAYPOINT_THRESHOLD) {
            currentWaypoint = (currentWaypoint + 1) % waypointX.length;
            // System.out.println("[PakKhairul] Reached waypoint, moving to next: " +
            // currentWaypoint);
            return;
        }

        // Move towards target
        if (distance > 0) {
            double moveX = (dx / distance) * patrolSpeed;
            double moveY = (dy / distance) * patrolSpeed;

            worldX += (int) moveX;
            worldY += (int) moveY;

            // Update direction for sprite animation
            if (Math.abs(dx) > Math.abs(dy)) {
                direction = dx > 0 ? "right" : "left";
            } else {
                direction = dy > 0 ? "down" : "up";
            }
        }
    }

    public boolean canInteract(int dist) {
        double dx = (worldX + 24) - (gp.player.worldX + 24);
        double dy = (worldY + 24) - (gp.player.worldY + 24);
        return Math.sqrt(dx * dx + dy * dy) <= dist;
    }

    public boolean hasInteracted() {
        return hasInteracted;
    }

    public void interact() {
        if (gp.uiM == null || gp.uiM.getPlayScreen() == null)
            return;

        controller.StoryManager sm = controller.StoryManager.getInstance();

        if (!hasInteracted) {
            // First meeting - planting trees at farm
            gp.uiM.getPlayScreen().showDialog(
                    sm.getDialog("khairul_greeting"),
                    "Pak Khairul", () -> {
                        gp.uiM.getPlayScreen().showDialog(
                                sm.getDialog("khairul_problem_1"),
                                "Pak Khairul", () -> {
                                    gp.uiM.getPlayScreen().showDialog(
                                            sm.getDialog("khairul_problem_2"),
                                            "Pak Khairul", () -> {
                                                // Show choices
                                                gp.uiM.getPlayScreen().showDialogWithChoices(
                                                        "Bagaimana saya bisa bantu Pak?",
                                                        "Pak Khairul",
                                                        new String[] {
                                                                sm.getDialog("khairul_choice_1"),
                                                                sm.getDialog("khairul_choice_2")
                                                        },
                                                        (idx, text) -> {
                                                            if (idx == 0) {
                                                                // Offer to plant trees
                                                                gp.uiM.getPlayScreen().showDialog(
                                                                        sm.getDialog("khairul_response"),
                                                                        "Pak Khairul", () -> {
                                                                            hasInteracted = true;
                                                                            gp.uiM.showMessage(sm.getDialog(
                                                                                    "objective_plant_trees"));
                                                                            gp.stateM.setCurrentState(
                                                                                    controller.StateManager.gameState.PLAY);
                                                                        });
                                                            } else {
                                                                // Clean irrigation
                                                                gp.uiM.getPlayScreen().showDialog(
                                                                        "Terima kasih, Nak. Tapi yang paling penting adalah pohon penahan air.",
                                                                        "Pak Khairul", () -> {
                                                                            gp.uiM.getPlayScreen().showDialog(
                                                                                    sm.getDialog("khairul_response"),
                                                                                    "Pak Khairul", () -> {
                                                                                        hasInteracted = true;
                                                                                        gp.uiM.showMessage(sm.getDialog(
                                                                                                "objective_plant_trees"));
                                                                                        gp.stateM.setCurrentState(
                                                                                                controller.StateManager.gameState.PLAY);
                                                                                    });
                                                                        });
                                                            }
                                                        });
                                            });
                                });
                    });
        } else {
            // Already helped
            gp.uiM.getPlayScreen().showDialog(
                    "Alhamdulillah, tanahnya sudah mulai lebih lembab. Terima kasih ya, Nak.",
                    "Pak Khairul",
                    () -> gp.stateM.setCurrentState(controller.StateManager.gameState.PLAY));
        }
    }

    public void draw(Graphics2D g2) {
        screenX = worldX - gp.player.worldX + gp.player.screenX;
        screenY = worldY - gp.player.worldY + gp.player.screenY;

        if (worldX + gp.tileSize > gp.player.worldX - gp.player.screenX &&
                worldX - gp.tileSize < gp.player.worldX + gp.player.screenX &&
                worldY + gp.tileSize > gp.player.worldY - gp.player.screenY &&
                worldY - gp.tileSize < gp.player.worldY + gp.player.screenY) {

            BufferedImage image = switch (direction) {
                case "up" -> (spriteNum == 1) ? up1 : up2;
                case "down" -> (spriteNum == 1) ? down1 : down2;
                case "left" -> (spriteNum == 1) ? left1 : left2;
                case "right" -> (spriteNum == 1) ? right1 : right2;
                default -> down1;
            };

            if (image != null) {
                g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
            }
        }
    }
}
