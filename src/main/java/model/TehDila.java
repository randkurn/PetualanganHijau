package model;

import controller.GamePanel;
import java.awt.Rectangle;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class TehDila extends Entity {

    GamePanel gamePanel;
    public int screenX, screenY;
    private static final int SEED_PRICE = 500;

    public TehDila(GamePanel gp) {
        this.gamePanel = gp;

        speed = 0;
        hitboxDefaultX = 8;
        hitboxDefaultY = 16;
        hitbox = new Rectangle(hitboxDefaultX, hitboxDefaultY, 32, 32);
        direction = "down";

        getDilaSprite();
    }

    private BufferedImage seedIcon;
    private static int interactionCounter = 0; // To prevent double calls in quick succession

    private void getDilaSprite() {
        // Load Seed Icon
        try {
            java.io.InputStream seedStream = getClass().getResourceAsStream("/objects/tree1.png");
            if (seedStream != null) {
                seedIcon = ImageIO.read(seedStream);
            }
        } catch (Exception e) {
            System.err.println("[TehDila] Failed to load seed icon");
        }

        try {
            down1 = loadSprite("/NPC/tehdila/down1.png");
            if (down1 != null) {
                down2 = loadSprite("/NPC/tehdila/down2.png");
                up1 = loadSprite("/NPC/tehdila/up1.png");
                up2 = loadSprite("/NPC/tehdila/up2.png");
                left1 = loadSprite("/NPC/tehdila/left1.png");
                left2 = loadSprite("/NPC/tehdila/left2.png");
                right1 = loadSprite("/NPC/tehdila/right1.png");
                right2 = loadSprite("/NPC/tehdila/right2.png");
                System.out.println("[TehDila] Sprites loaded successfully from /NPC/tehdila/");
            } else {
                System.out.println("[TehDila] Primary sprites missing, trying fallback...");
                loadFallback();
            }
        } catch (Exception e) {
            System.err.println("[TehDila] Error loading sprites: " + e.getMessage());
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
            System.out.println("[TehDila] Attempting to load fallback farmer sprites...");
            down1 = loadSprite("/farmer/farmerdown1.png");
            down2 = loadSprite("/farmer/farmerdown2.png");
            up1 = loadSprite("/farmer/farmerup1.png");
            up2 = loadSprite("/farmer/farmerup2.png");
            left1 = loadSprite("/farmer/farmerleft1.png");
            left2 = loadSprite("/farmer/farmerleft2.png");
            right1 = loadSprite("/farmer/farmerright1.png");
            right2 = loadSprite("/farmer/farmerright2.png");

            if (down1 == null) {
                System.err.println("[TehDila] Fallback sprites folder '/farmer/' not found!");
            }
        } catch (Exception ex) {
            System.err.println("[TehDila] Error in loadFallback: " + ex.getMessage());
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

        if (interactionCounter > 0) {
            interactionCounter--;
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
        if (interactionCounter > 0)
            return;
        interactionCounter = 30; // Prevent repeat calls for 0.5s

        if (gamePanel.uiM == null || gamePanel.uiM.getDialogBox() == null)
            return;
        view.PlayScreen dialog = gamePanel.uiM.getDialogBox();
        controller.StoryManager sm = controller.StoryManager.getInstance();

        if (!gamePanel.tehDilaGiftGiven) {
            dialog.showDialog(sm.getDialog("tehdila_intro_1"), "Teh Dila", () -> {
                dialog.showDialog(sm.getDialog("tehdila_intro_2"), "Teh Dila", () -> {
                    dialog.showDialog(sm.getDialog("tehdila_intro_3"), "Teh Dila", () -> {
                        if (gamePanel.player.inventory.addItem("Bibit Pohon", 1, seedIcon)) {
                            gamePanel.tehDilaGiftGiven = true;
                            showShop(dialog);
                        } else {
                            gamePanel.uiM.showMessage(sm.getDialog("tehdila_inv_full"));
                            interactionCounter = 0;
                        }
                    });
                });
            });
        } else {
            showShop(dialog);
        }
    }

    private void showShop(view.PlayScreen dialog) {
        controller.StoryManager sm = controller.StoryManager.getInstance();
        dialog.showDialogWithChoices(
                sm.getDialog("tehdila_welcome").replace("%VALUE%", String.valueOf(SEED_PRICE)),
                "Teh Dila",
                new String[] {
                        sm.getDialog("tehdila_choice_1").replace("%VALUE%", String.valueOf(SEED_PRICE)),
                        sm.getDialog("tehdila_choice_2")
                },
                (idx, txt) -> {
                    if (idx == 0) {
                        if (gamePanel.player.gold >= SEED_PRICE) {
                            if (gamePanel.player.inventory.addItem("Bibit Pohon", 1, seedIcon)) {
                                gamePanel.player.gold -= SEED_PRICE;
                                gamePanel.uiM.showMessage(sm.getDialog("tehdila_buy_confirm"));
                                // Show shop again for convenience
                                showShop(dialog);
                            } else {
                                gamePanel.uiM.showMessage(sm.getDialog("tehdila_inv_full"));
                            }
                        } else {
                            gamePanel.uiM.showMessage(
                                    sm.getDialog("tehdila_no_money").replace("%VALUE%", String.valueOf(SEED_PRICE)));
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
