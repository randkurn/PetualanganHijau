package model;

import controller.GamePanel;
import java.awt.Rectangle;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;

public class DanuSaputra extends Entity {

    GamePanel gp;
    public String name;
    public int screenX, screenY;

    public DanuSaputra(GamePanel gp) {
        this.gp = gp;
        this.name = "Danu Saputra";
        this.speed = 0;
        this.hitbox = new Rectangle(8, 16, 32, 32);
        this.direction = "down";
        getSprites();
    }

    private void getSprites() {
        try {
            down1 = ImageIO.read(getClass().getResourceAsStream("/NPC/danusaputra/down1.png"));
            down2 = ImageIO.read(getClass().getResourceAsStream("/NPC/danusaputra/down2.png"));
            up1 = ImageIO.read(getClass().getResourceAsStream("/NPC/danusaputra/up1.png"));
            up2 = ImageIO.read(getClass().getResourceAsStream("/NPC/danusaputra/up2.png"));
            left1 = ImageIO.read(getClass().getResourceAsStream("/NPC/danusaputra/left1.png"));
            left2 = ImageIO.read(getClass().getResourceAsStream("/NPC/danusaputra/left2.png"));
            right1 = ImageIO.read(getClass().getResourceAsStream("/NPC/danusaputra/right1.png"));
            right2 = ImageIO.read(getClass().getResourceAsStream("/NPC/danusaputra/right2.png"));

            if (left2 == null)
                left2 = left1;
            if (right2 == null)
                right2 = right1;

            if (down1 == null || up1 == null) {
                System.err.println("[DanuSaputra] Critical sprites missing, loading fallback");
                loadFallbackSprites();
            } else {
                System.out.println("[DanuSaputra] Sprites loaded successfully");
            }
        } catch (Exception e) {
            System.err.println("[DanuSaputra] Sprite load failed: " + e.getMessage());
            loadFallbackSprites();
        }
    }

    private void loadFallbackSprites() {
        try {
            down1 = ImageIO.read(getClass().getResourceAsStream("/farmer/farmerdown1.png"));
            down2 = ImageIO.read(getClass().getResourceAsStream("/farmer/farmerdown2.png"));
            up1 = ImageIO.read(getClass().getResourceAsStream("/farmer/farmerup1.png"));
            up2 = ImageIO.read(getClass().getResourceAsStream("/farmer/farmerup2.png"));
            left1 = ImageIO.read(getClass().getResourceAsStream("/farmer/farmerleft1.png"));
            left2 = ImageIO.read(getClass().getResourceAsStream("/farmer/farmerleft2.png"));
            right1 = ImageIO.read(getClass().getResourceAsStream("/farmer/farmerright1.png"));
            right2 = ImageIO.read(getClass().getResourceAsStream("/farmer/farmerright2.png"));
            System.out.println("[DanuSaputra] Using fallback farmer sprites");
        } catch (Exception ex) {
            System.err.println("[DanuSaputra] Fallback sprites also failed");
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

        // Kalimat 1: Salam pembuka
        gp.uiM.getPlayScreen().showDialog(
                "Halo! Keren udah rajin mungut sampah.",
                "Danu Saputra", () -> {
                    // Step 2: Langsung tawarin setor sampah (sesuai request: nyaranin tukar sampah
                    // dulu)
                    gp.uiM.getPlayScreen().showDialogWithChoices(
                            "Mau setor sampah sekarang? Pilah sampahnya dulu ya di sini biar gue kasih harga pas.",
                            "Danu Saputra",
                            new String[] {
                                    "Setor Sampah",
                                    "Nanti Saja"
                            },
                            (idx, txt) -> {
                                if (idx == 0) {
                                    // Branch: Setor Sampah
                                    if (gp.player.getCollectedTrash().isEmpty()) {
                                        gp.uiM.getPlayScreen().showDialog(
                                                "Eh, kamu belum punya sampah yang bisa disetor. Kumpulin dulu ya!",
                                                "Danu Saputra",
                                                () -> gp.stateM
                                                        .setCurrentState(controller.StateManager.gameState.PLAY));
                                    } else {
                                        gp.uiM.getPlayScreen().showDialog(
                                                "Oke sip! Pilah sampahnya dulu ya biar gue bisa hitung totalnya.",
                                                "Danu Saputra", () -> {
                                                    gp.uiM.getInventoryScreen().resetSorting();
                                                    gp.uiM.getInventoryScreen().toggleSortingMode();
                                                    gp.stateM.setCurrentState(
                                                            controller.StateManager.gameState.INVENTORY);
                                                });
                                    }
                                } else {
                                    // Branch: Nanti Saja -> Kasih saran pohon & Bu Suci
                                    gp.uiM.getPlayScreen().showDialog(
                                            "Oke. Tapi ingat, jangan cuma mungut sampah aja, kita juga harus mulai menanam pohon biar lingkungan makin hijau.",
                                            "Danu Saputra", () -> {
                                                gp.uiM.getPlayScreen().showDialog(
                                                        "Coba cari Bu Suci di daerah selatan (area bawah map), dia jual bibit pohon. Samperin aja ke sana ya!",
                                                        "Danu Saputra",
                                                        () -> gp.stateM.setCurrentState(
                                                                controller.StateManager.gameState.PLAY));
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

            BufferedImage img = switch (direction) {
                case "up" -> (spriteNum == 1) ? up1 : up2;
                case "down" -> (spriteNum == 1) ? down1 : down2;
                case "left" -> (spriteNum == 1) ? left1 : left2;
                case "right" -> (spriteNum == 1) ? right1 : right2;
                default -> down1;
            };
            if (img != null) {
                g2.drawImage(img, screenX, screenY, gp.tileSize, gp.tileSize, null);

                // Add BANK SAMPAH tag
                String tag = "DANU (BANK SAMPAH)";
                g2.setFont(gp.uiM.getPlayScreen().pressStart2P.deriveFont(java.awt.Font.BOLD, 9f));
                int textW = g2.getFontMetrics().stringWidth(tag);
                int tagX = screenX + (gp.tileSize / 2) - (textW / 2);
                int tagY = screenY - 10;

                g2.setColor(new java.awt.Color(0, 0, 0, 150));
                g2.fillRect(tagX - 4, tagY - 12, textW + 8, 16);

                g2.setColor(java.awt.Color.YELLOW);
                g2.drawString(tag, tagX, tagY);
            }
        }
    }
}
