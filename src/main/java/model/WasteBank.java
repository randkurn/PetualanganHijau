package model;

import controller.GamePanel;
import java.awt.Rectangle;
import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Map;

public class WasteBank extends Entity {

    GamePanel gamePanel;
    public int screenX, screenY;

    public WasteBank(GamePanel gp) {
        this.gamePanel = gp;
        speed = 0;
        hitbox = new Rectangle(8, 16, 32, 32);
        direction = "down";
        getWasteBankSprite();
    }

    private void getWasteBankSprite() {
        try {
            down1 = ImageIO.read(getClass().getResourceAsStream("/NPC/bangsampah/down1.png"));
            down2 = ImageIO.read(getClass().getResourceAsStream("/NPC/bangsampah/down2.png"));
            up1 = down1;
            up2 = down2;
            right1 = down1;
            right2 = down2;
            left1 = down1;
            left2 = down2;
        } catch (Exception e) {
            System.err.println("WasteBank sprite load failed.");
        }
    }

    public void setPosition(int x, int y) {
        this.worldX = x;
        this.worldY = y;
    }

    public void update() {
        spriteCounter++;
        if (spriteCounter > 60) {
            if (spriteNum == 1) {
                spriteNum = 2;
            } else if (spriteNum == 2) {
                spriteNum = 1;
            }
            spriteCounter = 0;
        }
    }

    public boolean canInteract(int interactDistance) {
        double dx = (worldX + 24) - (gamePanel.player.worldX + 24);
        double dy = (worldY + 24) - (gamePanel.player.worldY + 24);
        return Math.sqrt(dx * dx + dy * dy) <= interactDistance;
    }

    public void interact() {
        String[] options = { "Pilah Sampah", "Keluar" };
        gamePanel.uiM.getPlayScreen().showDialogWithChoices(
                "Halo! Saya operator Bank Sampah. \nIngin memilah sampahmu dengan Gold di Bank Sampah?",
                "Bank Sampah",
                options,
                (index, text) -> {
                    if (index == 0) {
                        openSortingMode();
                    }
                });
    }

    private void openSortingMode() {
        gamePanel.uiM.getInventoryScreen().resetSorting();

        gamePanel.uiM.getInventoryScreen().toggleSortingMode();

        gamePanel.stateM.setCurrentState(controller.StateManager.gameState.INVENTORY);

        gamePanel.uiM.showMessage("Pilah sampah ke tempat yang benar!");
    }

    public boolean exchangeTrash(String category) {
        Map<String, Integer> playerItems = gamePanel.player.inventory.getAllItems();
        Map<String, TrashType> sortedItems = gamePanel.uiM.getInventoryScreen().getSortedTrash();

        int totalGold = 0;
        int totalItems = 0;
        int bonusGold = 0;
        int sortedCorrectly = 0;

        for (Map.Entry<String, Integer> entry : playerItems.entrySet()) {
            String name = entry.getKey();
            int qty = entry.getValue();
            TrashType type = gamePanel.player.inventory.getItemType(name);

            if (type != null) {
                int basePrice = 10;
                switch (type) {
                    case ORGANIC:
                        basePrice = 5;
                        break;
                    case ANORGANIC:
                        basePrice = 15;
                        break;
                    case TOXIC:
                        basePrice = 20;
                        break;
                }

                int itemTotal = basePrice * qty;

                if (sortedItems.containsKey(name) && sortedItems.get(name) == type) {
                    int bonus = (int) (itemTotal * 0.5);
                    bonusGold += bonus;
                    sortedCorrectly += qty;
                }

                totalGold += itemTotal;
                totalItems += qty;
                gamePanel.player.inventory.removeItem(name, qty);
            }
        }

        if (totalItems > 0) {
            int finalGold = totalGold + bonusGold;
            gamePanel.player.addGold(finalGold);

            String message = "Berhasil menukar " + totalItems + " sampah!\n" +
                    "Harga dasar: " + totalGold + " Gold\n";
            if (sortedCorrectly > 0) {
                message += "Bonus pemilahan (" + sortedCorrectly + " item): +" + bonusGold + " Gold\n" +
                        "-----------------------\n" +
                        "TOTAL: " + finalGold + " Gold\n\n" +
                        "Terima kasih sudah memilah dengan benar!";
            } else {
                message += "Total: " + finalGold + " Gold\n\n" +
                        "Tip: Pilah sampah dulu for bonus 50%!";
            }

            float boost = (float) (totalItems * 0.5);
            controller.EnvironmentManager.getInstance().changeCleanliness(boost);

            gamePanel.uiM.getPlayScreen().showDialog(message, "Bank Sampah");

            gamePanel.uiM.getInventoryScreen().clearSortedTrash();

            return true;
        } else {
            gamePanel.uiM.getPlayScreen().showDialog(
                    "Sepertinya kamu tidak membawa sampah apapun.",
                    "Bank Sampah");
            return false;
        }
    }

    public void draw(Graphics2D g2) {
        screenX = worldX - gamePanel.player.worldX + gamePanel.player.screenX;
        screenY = worldY - gamePanel.player.worldY + gamePanel.player.screenY;

        if (worldX + gamePanel.tileSize > gamePanel.player.worldX - gamePanel.player.screenX &&
                worldX - gamePanel.tileSize < gamePanel.player.worldX + gamePanel.player.screenX &&
                worldY + gamePanel.tileSize > gamePanel.player.worldY - gamePanel.player.screenY &&
                worldY - gamePanel.tileSize < gamePanel.player.worldY + gamePanel.player.screenY) {

            BufferedImage img = (spriteNum == 1) ? down1 : down2;
            if (img != null) {
                int drawSize = gamePanel.tileSize;
                if (gamePanel.mapM.currMap == 6) {
                    drawSize = (int) (gamePanel.tileSize * 1.25);
                }
                g2.drawImage(img, screenX, screenY, drawSize, drawSize, null);

                String tag = "BANG SAMPAH";
                g2.setFont(gamePanel.uiM.getPlayScreen().pressStart2P.deriveFont(java.awt.Font.BOLD, 10f));
                int textW = g2.getFontMetrics().stringWidth(tag);
                int tagX = screenX + (drawSize / 2) - (textW / 2);
                int tagY = screenY - 10;

                g2.setColor(new java.awt.Color(0, 0, 0, 150));
                g2.fillRect(tagX - 4, tagY - 12, textW + 8, 16);

                g2.setColor(java.awt.Color.YELLOW);
                g2.drawString(tag, tagX, tagY);
            }
        }
    }
}
