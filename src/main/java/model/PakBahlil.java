package model;

import controller.GamePanel;
import java.awt.Rectangle;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class PakBahlil extends Entity {

    GamePanel gamePanel;
    public int screenX, screenY;

    public int goalCol = -1;
    public int goalRow = -1;
    private boolean goalReached = false;
    private boolean followingGoal = false;

    public PakBahlil(GamePanel gp) {
        this.gamePanel = gp;

        speed = 1;
        hitboxDefaultX = 8;
        hitboxDefaultY = 16;
        hitbox = new Rectangle(hitboxDefaultX, hitboxDefaultY, 32, 32);
        direction = "down";

        getBahlilSprite();
    }

    private void getBahlilSprite() {
        try {
            down1 = loadSprite("/NPC/paksaman/down1.png");
            down2 = loadSprite("/NPC/paksaman/down2.png");
            up1 = loadSprite("/NPC/paksaman/up1.png");
            up2 = loadSprite("/NPC/paksaman/up2.png");
            right1 = loadSprite("/NPC/paksaman/right1.png");
            right2 = loadSprite("/NPC/paksaman/right2.png");
            left1 = loadSprite("/NPC/paksaman/left1.png");
            left2 = loadSprite("/NPC/paksaman/left2.png");

            if (down1 == null) {
                System.out.println("Pak Bahlil sprites not found, fallback to placeholder.");
                loadFallback();
            }
        } catch (Exception e) {
            loadFallback();
        }
    }

    private BufferedImage loadSprite(String path) throws IOException {
        java.io.InputStream is = getClass().getResourceAsStream(path);
        if (is == null)
            return null;
        return ImageIO.read(is);
    }

    private void loadFallback() {
        try {
            down1 = ImageIO.read(getClass().getResourceAsStream("/farmer/farmerdown1.png"));
            down2 = ImageIO.read(getClass().getResourceAsStream("/farmer/farmerdown2.png"));
        } catch (IOException ex) {
        }
    }

    public void setPosition(int x, int y) {
        this.worldX = x;
        this.worldY = y;
    }

    public void update() {
        if (followingGoal) {
            int currentCol = (worldX + hitbox.x) / gamePanel.tileSize;
            int currentRow = (worldY + hitbox.y) / gamePanel.tileSize;

            if (currentCol == goalCol && currentRow == goalRow) {
                goalReached = true;
                followingGoal = false;
                speed = 0;
                return;
            }

            gamePanel.pathFinder.searchPath(goalCol, goalRow, this);

            collisionOn = false;
            gamePanel.checker.checkTileCollision(this);
            if (!collisionOn) {
                switch (direction) {
                    case "up" -> worldY -= speed;
                    case "down" -> worldY += speed;
                    case "left" -> worldX -= speed;
                    case "right" -> worldX += speed;
                }
            }

            animate();
            return;
        }

        if (gamePanel.stateM.getCurrentState() == controller.StateManager.gameState.CUTSCENE) {
            return;
        }

        speed = 0;
        if (speed > 0) {
            animate();
        } else {
            spriteNum = 1;
        }
    }

    private void animate() {
        spriteCounter++;
        if (spriteCounter > 15) {
            spriteNum = (spriteNum == 1) ? 2 : 1;
            spriteCounter = 0;
        }
    }

    public void setGoal(int col, int row) {
        this.goalCol = col;
        this.goalRow = row;
        this.followingGoal = true;
        this.goalReached = false;
        this.speed = 4;
    }

    public boolean isGoalReached() {
        return goalReached;
    }

    public void clearGoal() {
        this.followingGoal = false;
        this.speed = 1;
    }

    public boolean canInteract(int interactDistance) {
        double playerMiddleX = gamePanel.player.worldX + gamePanel.player.hitbox.x
                + (gamePanel.player.hitbox.width / 2);
        double playerMiddleY = gamePanel.player.worldY + gamePanel.player.hitbox.y
                + (gamePanel.player.hitbox.height / 2);
        double npcMiddleX = this.worldX + this.hitbox.x + (this.hitbox.width / 2);
        double npcMiddleY = this.worldY + this.hitbox.y + (this.hitbox.height / 2);

        return Math.hypot(playerMiddleX - npcMiddleX, playerMiddleY - npcMiddleY) <= interactDistance;
    }

    public void interact() {
        if (gamePanel.uiM == null || gamePanel.uiM.getPlayScreen() == null)
            return;

        // Jika sedang dalam cutscene, biarkan CutsceneManager yang handle dialognya
        if (gamePanel.stateM.getCurrentState() == controller.StateManager.gameState.CUTSCENE) {
            return;
        }

        controller.StoryManager sm = controller.StoryManager.getInstance();
        if (gamePanel.chapter2Active || gamePanel.chapter2Finished) {
            gamePanel.uiM.getPlayScreen().showDialog(sm.getDialog("bahlil_advice_1"), "Pak Bahlil", () -> {
                gamePanel.uiM.getPlayScreen().showDialog(sm.getDialog("bahlil_advice_2"), "Pak Bahlil");
            });
        } else {
            gamePanel.uiM.getPlayScreen().showDialog(sm.getDialog("bahlil_greeting"), "Pak Bahlil");
        }
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
                if (gamePanel.mapM.currMap == 6) {
                    drawSize = (int) (gamePanel.tileSize * 1.25);
                }

                int yOffset = drawSize - gamePanel.tileSize;
                g2.drawImage(image, screenX, screenY - yOffset, drawSize, drawSize, null);
            }
        }
    }
}
