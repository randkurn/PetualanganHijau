package model;

import controller.GamePanel;
import java.awt.Rectangle;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;

public class NengJia extends Entity {

    GamePanel gp;
    public String name;
    public int screenX, screenY;
    private boolean hasInteracted = false;

    public NengJia(GamePanel gp) {
        this.gp = gp;
        this.name = "Neng Jia";
        this.speed = 0;
        this.hitbox = new Rectangle(8, 16, 32, 32);
        this.direction = "down";
        getSprites();
    }

    private void getSprites() {
        try {
            down1 = loadSprite("/NPC/nengjia/down1.png");
            down2 = loadSprite("/NPC/nengjia/down2.png");
            up1 = loadSprite("/NPC/nengjia/up1.png");
            up2 = loadSprite("/NPC/nengjia/up2.png");
            left1 = loadSprite("/NPC/nengjia/left1.png");
            left2 = loadSprite("/NPC/nengjia/left2.png");
            right1 = loadSprite("/NPC/nengjia/right1.png");
            right2 = loadSprite("/NPC/nengjia/right2.png");

            if (down1 == null) {
                System.out.println("[Neng Jia] Sprites not found, loading fallback");
                loadFallbackSprites();
            }
        } catch (Exception e) {
            System.err.println("[Neng Jia] Sprite load failed: " + e.getMessage());
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
        spriteCounter++;
        if (spriteCounter > 30) {
            spriteNum = (spriteNum == 1) ? 2 : 1;
            spriteCounter = 0;
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
            // First meeting - cleaning park trash
            gp.uiM.getPlayScreen().showDialog(
                    sm.getDialog("jia_greeting"),
                    "Neng Jia", () -> {
                        gp.uiM.getPlayScreen().showDialog(
                                sm.getDialog("jia_complaint_1"),
                                "Neng Jia", () -> {
                                    gp.uiM.getPlayScreen().showDialog(
                                            sm.getDialog("jia_complaint_2"),
                                            "Neng Jia", () -> {
                                                hasInteracted = true;
                                                gp.uiM.showMessage(sm.getDialog("objective_clean_park"));
                                                gp.stateM.setCurrentState(controller.StateManager.gameState.PLAY);
                                            });
                                });
                    });
        } else {
            // Already helped
            gp.uiM.getPlayScreen().showDialog(
                    sm.getDialog("jia_thanks"),
                    "Neng Jia",
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
