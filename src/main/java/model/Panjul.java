package model;

import controller.GamePanel;
import java.awt.Rectangle;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;

public class Panjul extends Entity {

    GamePanel gp;
    public String name;
    public int screenX, screenY;

    public Panjul(GamePanel gp) {
        this.gp = gp;
        this.name = "Panjul";
        this.speed = 0;
        this.hitbox = new Rectangle(8, 16, 32, 32);
        this.direction = "down";
        getSprites();
    }

    private void getSprites() {
        try {
            down1 = ImageIO.read(getClass().getResourceAsStream("/NPC/panjul/down1.png"));
            down2 = ImageIO.read(getClass().getResourceAsStream("/NPC/panjul/down2.png"));
            up1 = ImageIO.read(getClass().getResourceAsStream("/NPC/panjul/up1.png"));
            up2 = ImageIO.read(getClass().getResourceAsStream("/NPC/panjul/up2.png"));
            left1 = ImageIO.read(getClass().getResourceAsStream("/NPC/panjul/left1.png"));
            left2 = ImageIO.read(getClass().getResourceAsStream("/NPC/panjul/left2.png"));
            right1 = ImageIO.read(getClass().getResourceAsStream("/NPC/panjul/right1.png"));
            right2 = ImageIO.read(getClass().getResourceAsStream("/NPC/panjul/right2.png"));

            if (left2 == null)
                left2 = left1;
            if (right2 == null)
                right2 = right1;

            if (down1 == null || up1 == null) {
                System.err.println("[Panjul] Critical sprites missing, loading fallback");
                loadFallbackSprites();
            } else {
                System.out.println("[Panjul] Sprites loaded successfully from /NPC/panjul/");
            }
        } catch (Exception e) {
            System.err.println("[Panjul] Sprite load failed: " + e.getMessage());
            loadFallbackSprites();
        }
    }

    private void loadFallbackSprites() {
        try {
            System.out.println("[Panjul] Attempting to load fallback farmer sprites...");
            down1 = loadSprite("/farmer/farmerdown1.png");
            down2 = loadSprite("/farmer/farmerdown2.png");
            up1 = loadSprite("/farmer/farmerup1.png");
            up2 = loadSprite("/farmer/farmerup2.png");
            left1 = loadSprite("/farmer/farmerleft1.png");
            left2 = loadSprite("/farmer/farmerleft2.png");
            right1 = loadSprite("/farmer/farmerright1.png");
            right2 = loadSprite("/farmer/farmerright2.png");

            if (down1 == null) {
                System.err.println("[Panjul] Fallback sprites folder '/farmer/' not found!");
            }
        } catch (Exception ex) {
            System.err.println("[Panjul] Error in loadFallbackSprites: " + ex.getMessage());
        }
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

    public int goalCol = -1;
    public int goalRow = -1;
    public boolean onPath = false;

    public void update() {
        if (onPath) {
            int nextX = goalCol * gp.tileSize;
            int nextY = goalRow * gp.tileSize;

            if (worldX < nextX) {
                worldX += speed;
                direction = "right";
            } else if (worldX > nextX) {
                worldX -= speed;
                direction = "left";
            }

            if (worldY < nextY) {
                worldY += speed;
                direction = "down";
            } else if (worldY > nextY) {
                worldY -= speed;
                direction = "up";
            }

            if (Math.abs(worldX - nextX) < speed && Math.abs(worldY - nextY) < speed) {
                worldX = nextX;
                worldY = nextY;
                onPath = false;
            }

            spriteCounter++;
            if (spriteCounter > 12) {
                spriteNum = (spriteNum == 1) ? 2 : 1;
                spriteCounter = 0;
            }
        } else {
            spriteCounter++;
            if (spriteCounter > 30) {
                spriteNum = (spriteNum == 1) ? 2 : 1;
                spriteCounter = 0;
            }
        }
    }

    public boolean canInteract(int dist) {
        double dx = (worldX + 24) - (gp.player.worldX + 24);
        double dy = (worldY + 24) - (gp.player.worldY + 24);
        return Math.sqrt(dx * dx + dy * dy) <= dist;
    }

    public void interact() {
        if (gp.uiM == null || gp.uiM.getPlayScreen() == null)
            return;

        controller.StoryManager sm = controller.StoryManager.getInstance();

        // Kalimat 1: Salam pembuka
        gp.uiM.getPlayScreen().showDialog(
                sm.getDialog("panjul_greeting"),
                "Panjul", () -> {
                    // Step 2: Langsung tawarin setor sampah
                    gp.uiM.getPlayScreen().showDialogWithChoices(
                            sm.getDialog("panjul_ask_exchange"),
                            "Panjul (Bank Sampah)",
                            new String[] {
                                    sm.getDialog("panjul_choice_1"),
                                    sm.getDialog("panjul_choice_2")
                            },
                            (idx, txt) -> {
                                if (idx == 0) {
                                    if (gp.player.getCollectedTrash().isEmpty()) {
                                        gp.uiM.getPlayScreen().showDialog(
                                                sm.getDialog("panjul_no_trash"),
                                                "Panjul",
                                                () -> gp.stateM
                                                        .setCurrentState(controller.StateManager.gameState.PLAY));
                                    } else {
                                        gp.uiM.getPlayScreen().showDialog(
                                                sm.getDialog("panjul_start_exchange"),
                                                "Panjul", () -> {
                                                    gp.uiM.getInventoryScreen().resetSorting();
                                                    gp.uiM.getInventoryScreen().toggleSortingMode();
                                                    gp.stateM.setCurrentState(
                                                            controller.StateManager.gameState.INVENTORY);
                                                });
                                    }
                                } else {
                                    // Branch: Nanti Saja -> Kasih saran pohon & Teh Dila
                                    gp.uiM.getPlayScreen().showDialog(sm.getDialog("panjul_advice_trees_1"), "Panjul",
                                            () -> {
                                                gp.uiM.getPlayScreen().showDialog(sm.getDialog("panjul_advice_trees_2"),
                                                        "Panjul", () -> {
                                                            gp.uiM.getPlayScreen().showDialog(
                                                                    sm.getDialog("panjul_find_dila"),
                                                                    "Panjul (Bank Sampah)", () -> {
                                                                        if (gp.npcM.getTehDila() == null) {
                                                                            gp.npcM.spawnTehDila(26 * gp.tileSize,
                                                                                    58 * gp.tileSize);
                                                                            gp.uiM.showMessage(
                                                                                    sm.getDialog("obj_tehdila"));
                                                                        }
                                                                        gp.stateM.setCurrentState(
                                                                                controller.StateManager.gameState.PLAY);
                                                                    });
                                                        });
                                            });
                                }
                            });
                });
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

            g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
        }
    }
}
