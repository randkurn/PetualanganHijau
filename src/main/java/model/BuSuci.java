package model;

import controller.GamePanel;
import java.awt.Rectangle;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import view.PlayScreen;

public class BuSuci extends Entity {

    GamePanel gamePanel;
    public int screenX, screenY;
    private static final int SEED_PRICE = 500;

    public BuSuci(GamePanel gp) {
        this.gamePanel = gp;

        speed = 0;
        hitboxDefaultX = 8;
        hitboxDefaultY = 16;
        hitbox = new Rectangle(hitboxDefaultX, hitboxDefaultY, 32, 32);
        direction = "down";

        getSuciSprite();
    }

    private void getSuciSprite() {
        try {
            down1 = loadSprite("/NPC/busuci/down1.png");
            if (down1 != null) {
                down2 = loadSprite("/NPC/busuci/down2.png");
                up1 = loadSprite("/NPC/busuci/up1.png");
                up2 = loadSprite("/NPC/busuci/up2.png");
                left1 = loadSprite("/NPC/busuci/left1.png");
                left2 = loadSprite("/NPC/busuci/left2.png");
                right1 = loadSprite("/NPC/busuci/right1.png");
                right2 = loadSprite("/NPC/busuci/right2.png");
                System.out.println("[BuSuci] Loaded custom sprites");
                return;
            }

            System.out.println("[BuSuci] Using fallback sprites");
            loadFallback();
        } catch (Exception e) {
            loadFallback();
        }
    }

    private BufferedImage loadSprite(String path) {
        try {
            java.io.InputStream is = getClass().getResourceAsStream(path);
            if (is == null)
                return null;
            return ImageIO.read(is);
        } catch (IOException e) {
            return null;
        }
    }

    private void loadFallback() {
        try {
            down1 = ImageIO.read(getClass().getResourceAsStream("/farmer/farmerdown1.png"));
            down2 = ImageIO.read(getClass().getResourceAsStream("/farmer/farmerdown2.png"));
            up1 = ImageIO.read(getClass().getResourceAsStream("/farmer/farmerup1.png"));
            up2 = ImageIO.read(getClass().getResourceAsStream("/farmer/farmerup2.png"));
            left1 = ImageIO.read(getClass().getResourceAsStream("/farmer/farmerleft1.png"));
            left2 = ImageIO.read(getClass().getResourceAsStream("/farmer/farmerleft2.png"));
            right1 = ImageIO.read(getClass().getResourceAsStream("/farmer/farmerright1.png"));
            right2 = ImageIO.read(getClass().getResourceAsStream("/farmer/farmerright2.png"));
        } catch (IOException ex) {
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

    public boolean canInteract(int interactDistance) {
        double playerMiddleX = gamePanel.player.worldX + (gamePanel.tileSize / 2.0);
        double playerMiddleY = gamePanel.player.worldY + (gamePanel.tileSize / 2.0);
        double npcMiddleX = this.worldX + (gamePanel.tileSize / 2.0);
        double npcMiddleY = this.worldY + (gamePanel.tileSize / 2.0);

        return Math.hypot(playerMiddleX - npcMiddleX, playerMiddleY - npcMiddleY) <= interactDistance;
    }

    public void interact() {
        if (gamePanel.uiM == null || gamePanel.uiM.getDialogBox() == null)
            return;
        PlayScreen dialog = gamePanel.uiM.getDialogBox();

        dialog.showDialogWithChoices(
                "Selamat datang! Mau beli bibit pohon? Harganya " + SEED_PRICE + " gold per bibit.",
                "Bu Suci",
                new String[] {
                        "Beli 1 Bibit Pohon (" + SEED_PRICE + " gold)",
                        "Tidak, terima kasih"
                },
                (idx, txt) -> {
                    if (idx == 0) {
                        if (gamePanel.player.gold >= SEED_PRICE) {
                            if (gamePanel.player.inventory.addItem("Bibit Pohon", 1)) {
                                gamePanel.player.gold -= SEED_PRICE;
                                gamePanel.uiM.showMessage("Berhasil membeli 1 Bibit Pohon!");
                            } else {
                                gamePanel.uiM.showMessage("Inventory penuh!");
                            }
                        } else {
                            gamePanel.uiM.showMessage("Uang tidak cukup! Butuh " + SEED_PRICE + " gold.");
                        }
                    }
                });
    }

    public void draw(Graphics2D g2) {
        screenX = worldX - gamePanel.player.worldX + gamePanel.player.screenX;
        screenY = worldY - gamePanel.player.worldY + gamePanel.player.screenY;

        if (worldX + gamePanel.tileSize > gamePanel.player.worldX - gamePanel.player.screenX &&
                worldX - gamePanel.tileSize < gamePanel.player.worldX + gamePanel.player.screenX &&
                worldY + gamePanel.tileSize > gamePanel.player.worldY - gamePanel.player.screenY &&
                worldY - gamePanel.tileSize < gamePanel.player.worldY + gamePanel.player.screenY) {

            BufferedImage image = switch (direction) {
                case "up" -> (spriteNum == 1) ? up1 : up2;
                case "down" -> (spriteNum == 1) ? down1 : down2;
                case "left" -> (spriteNum == 1) ? left1 : left2;
                case "right" -> (spriteNum == 1) ? right1 : right2;
                default -> down1;
            };

            if (image != null) {
                int drawSize = gamePanel.tileSize;
                if (gamePanel.mapM.currMap == 5) {
                    drawSize = (int) (gamePanel.tileSize * 1.25);
                }

                int yOffset = drawSize - gamePanel.tileSize;
                g2.drawImage(image, screenX, screenY - yOffset, drawSize, drawSize, null);
            }
        }
    }
}
