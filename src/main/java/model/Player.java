package model;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import controller.GamePanel;
import view.AudioManager;
import controller.InputHandler;
import controller.AchievementManager;
import controller.StateManager;

public class Player extends Entity {
    Settings settings;
    AudioManager audio;
    GamePanel gamePanel;
    InputHandler input;

    public int screenX, screenY;
    public int score = 0;
    public int energy = 100;
    public int maxEnergy = 100;
    public int gold = 500;
    private int totalTrashInWorld = -1;
    public Inventory inventory;
    public String playerName = "Pemain";
    private java.util.Set<String> collectedTrash = new java.util.HashSet<>();

    public boolean onPath = false;
    public int goalCol, goalRow;
    public int nearObjectWorldX = -1;
    public int nearObjectWorldY = -1;
    public GameObject.Type nearObjectType;

    public void spawnPlayer() {
        worldX = gamePanel.mapM.getMap().playerStartX;
        worldY = gamePanel.mapM.getMap().playerStartY;
    }

    public Player(GamePanel gamePanel, InputHandler input) {
        this.gamePanel = gamePanel;
        this.input = input;
        this.settings = Settings.getInstance();
        this.audio = AudioManager.getInstance();

        screenX = gamePanel.screenWidth / 2 - (gamePanel.tileSize / 2);
        screenY = gamePanel.screenHeight / 2 - (gamePanel.tileSize / 2);

        hitbox = new Rectangle(12, 12, 24, 24);
        hitboxDefaultX = hitbox.x;
        hitboxDefaultY = hitbox.y;

        inventory = new Inventory(20);
        setDefaultValues();
        getPlayerImage();
    }

    public void setDefaultValues() {
        worldX = gamePanel.mapM.getMap().playerStartX;
        worldY = gamePanel.mapM.getMap().playerStartY;
        speed = 4;
        direction = "down";
        energy = maxEnergy;
    }

    public int getTotalTrashInWorld() {
        return totalTrashInWorld;
    }

    public void setTotalTrashInWorld(int count) {
        this.totalTrashInWorld = count;
    }

    public void setPlayerName(String name) {
        this.playerName = name;
    }

    public String getPlayerName() {
        return this.playerName;
    }

    private void getPlayerImage() {
        try {
            System.out.println("[Player] Loading sprites from /boy/ folder...");

            down1 = ImageIO.read(getClass().getResourceAsStream("/boy/down1.png"));
            down2 = ImageIO.read(getClass().getResourceAsStream("/boy/down2.png"));
            up1 = ImageIO.read(getClass().getResourceAsStream("/boy/up1.png"));
            up2 = ImageIO.read(getClass().getResourceAsStream("/boy/up2.png"));
            right1 = ImageIO.read(getClass().getResourceAsStream("/boy/right1.png"));
            right2 = ImageIO.read(getClass().getResourceAsStream("/boy/right2.png"));
            left1 = ImageIO.read(getClass().getResourceAsStream("/boy/left1.png"));
            left2 = ImageIO.read(getClass().getResourceAsStream("/boy/left2.png"));
            standSprite = ImageIO.read(getClass().getResourceAsStream("/boy/stand.png"));

            System.out.println("[Player] Sprites loaded successfully.");

        } catch (IOException | NullPointerException e) {
            System.out.println("[Player] Gagal load sprites dari /boy/, menggunakan default farmer.");
            loadDefaultSprites();
        }
    }

    private void loadDefaultSprites() {
        try {
            down1 = ImageIO.read(getClass().getResourceAsStream("/player/farmerdown1.png"));
            down2 = ImageIO.read(getClass().getResourceAsStream("/player/farmerdown2.png"));
            up1 = ImageIO.read(getClass().getResourceAsStream("/player/farmerup1.png"));
            up2 = ImageIO.read(getClass().getResourceAsStream("/player/farmerup2.png"));
            right1 = ImageIO.read(getClass().getResourceAsStream("/player/farmerright1.png"));
            right2 = ImageIO.read(getClass().getResourceAsStream("/player/farmerright2.png"));
            left1 = ImageIO.read(getClass().getResourceAsStream("/player/farmerleft1.png"));
            left2 = ImageIO.read(getClass().getResourceAsStream("/player/farmerleft2.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update() {
        if (totalTrashInWorld == -1 && gamePanel.mapM != null) {
            totalTrashInWorld = gamePanel.mapM.countTotalTrashInAllMaps();
            if (totalTrashInWorld > 0) {
                System.out.println("[Player] Calculated total trash in world: " + totalTrashInWorld);
            } else if (totalTrashInWorld == 0) {
                System.out.println("[Player] No trash found in world maps.");
            }
        }

        if (onPath) {
            int currentCol = (worldX + hitbox.x) / gamePanel.tileSize;
            int currentRow = (worldY + hitbox.y) / gamePanel.tileSize;

            if (currentCol == goalCol && currentRow == goalRow) {
                onPath = false;
                return;
            }

            gamePanel.pathFinder.searchPath(goalCol, goalRow, this);

            collisionOn = false;
            gamePanel.checker.checkTileCollision(this);
            gamePanel.checker.checkObjectCollision(this, true);
            gamePanel.npcM.tryInteractWithNearbyNPC(gamePanel.tileSize);

            if (!collisionOn) {
                switch (direction) {
                    case "up" -> worldY -= speed;
                    case "down" -> worldY += speed;
                    case "left" -> worldX -= speed;
                    case "right" -> worldX += speed;
                }
            } else {
                onPath = false;
                System.out.println("[Player] Path blocked at " + currentCol + "," + currentRow);
            }
            updatePlayerSprite();
            return;
        }

        // Don't allow movement during portal transitions
        if (gamePanel.portalSystem != null && gamePanel.portalSystem.isLoading()) {
            return;
        }

        if (gamePanel.stateM.getCurrentState() == StateManager.gameState.CUTSCENE ||
                gamePanel.stateM.getCurrentState() == StateManager.gameState.DIALOGUE ||
                gamePanel.stateM.getCurrentState() == StateManager.gameState.SCENE) {
            return;
        }

        if (input.up || input.left || input.down || input.right) {
            updatePlayerDirection();
            collisionOn = false;
            gamePanel.checker.checkTileCollision(this);

            int objIndex = gamePanel.checker.checkObjectCollision(this, true);
            if (objIndex != 999) {
                GameObject obj = gamePanel.mapM.getMap().objects[objIndex];
                if (obj != null && (obj.type == GameObject.Type.TRASH || obj.type == GameObject.Type.KEY)) {
                    obj.interact(gamePanel);
                }
            }

            if (!collisionOn) {
                switch (direction) {
                    case "up":
                        worldY -= speed;
                        break;
                    case "down":
                        worldY += speed;
                        break;
                    case "left":
                        worldX -= speed;
                        break;
                    case "right":
                        worldX += speed;
                        break;
                }
            }
            updatePlayerSprite();
        }

        int hintIndex = gamePanel.checker.checkObjectInteraction(this);
        if (hintIndex != 999) {
            GameObject obj = gamePanel.mapM.getMap().objects[hintIndex];
            if (obj != null && (obj.type == GameObject.Type.PORTAL || obj.type == GameObject.Type.GATE
                    || obj.type == GameObject.Type.BED)) {
                // Simpan koordinat objek untuk digambar di draw()
                this.nearObjectWorldX = obj.worldX;
                this.nearObjectWorldY = obj.worldY;
                this.nearObjectType = obj.type;
            } else {
                this.nearObjectWorldX = -1;
            }
        } else {
            this.nearObjectWorldX = -1;
        }

        // Auto-detect portal tiles for seamless map transitions
        if (gamePanel.portalSystem != null && !gamePanel.portalSystem.isLoading()) {
            gamePanel.portalSystem.checkPortalTile(worldX, worldY);
        }

        // Interaction is now handled by the current GameState (e.g., PlayState)
        // to prevent double-calling and flickering.

        if (gamePanel.mapM.currMap == 5 && !gamePanel.uiM.getPlayScreen().isActive()) {
            int autoIndex = gamePanel.checker.checkObjectInteraction(this);
            if (autoIndex != 999) {
                GameObject obj = gamePanel.mapM.getMap().objects[autoIndex];
                if (obj != null && (obj.type == GameObject.Type.PORTAL || obj.type == GameObject.Type.BED)) {
                    obj.interact(gamePanel);
                }
            }
        }
    }

    private void updatePlayerDirection() {
        if (input.up)
            direction = "up";
        else if (input.left)
            direction = "left";
        else if (input.down)
            direction = "down";
        else if (input.right)
            direction = "right";
    }

    private void updatePlayerSprite() {
        spriteCounter++;
        if (spriteCounter > 12) {
            spriteNum = (spriteNum == 1) ? 2 : 1;
            spriteCounter = 0;
        }
    }

    public void draw(Graphics2D g2) {
        BufferedImage image;
        if (speed == 0 && !input.up && !input.down && !input.left && !input.right && standSprite != null) {
            image = standSprite;
        } else {
            image = switch (direction) {
                case "up" -> (spriteNum == 1) ? up1 : up2;
                case "down" -> (spriteNum == 1) ? down1 : down2;
                case "left" -> (spriteNum == 1) ? left1 : left2;
                case "right" -> (spriteNum == 1) ? right1 : right2;
                default -> down1;
            };
        }

        int drawSize = gamePanel.tileSize;
        if (gamePanel.mapM.currMap == 6) {
            drawSize = (int) (gamePanel.tileSize * 1.25);
        }
        g2.drawImage(image, screenX, screenY, drawSize, drawSize, null);

        if (nearObjectWorldX != -1) {
            String hintText = "[E] Interaksi";
            if (nearObjectType == GameObject.Type.BED) {
                hintText = "[E] Tidur";
            } else if (nearObjectType == GameObject.Type.PORTAL || nearObjectType == GameObject.Type.GATE) {
                if (gamePanel.mapM.currMap == 5) {
                    hintText = "[E] Keluar";
                } else {
                    hintText = "[E] Masuk";
                }
            }
            gamePanel.uiM.getPlayScreen().drawInteractionHint(g2, nearObjectWorldX, nearObjectWorldY, hintText);
        }
    }

    public void restoreEnergy(int amount) {
        energy = Math.min(maxEnergy, energy + amount);
    }

    public void addGold(int amount) {
        gold += amount;
    }

    public boolean spendGold(int amount) {
        if (gold >= amount) {
            gold -= amount;
            return true;
        }
        return false;
    }

    public boolean plantTree() {
        String bibitName = "Bibit Mahoni";
        if (!inventory.hasItem(bibitName) && !inventory.hasItem("Bibit Pohon")) {
            gamePanel.uiM.showMessage("Kamu tidak punya bibit pohon!");
            return false;
        }
        if (!inventory.hasItem(bibitName))
            bibitName = "Bibit Pohon";

        int currentMap = gamePanel.mapM.currMap;
        boolean isChapter2Ending = gamePanel.chapter2Active && currentMap == 0;
        if (currentMap != 2 && !isChapter2Ending) {
            gamePanel.uiM.showMessage("Bukan area yang tepat untuk menanam!");
            return false;
        }

        int plantX = worldX;
        int plantY = worldY;

        switch (direction) {
            case "up" -> plantY -= gamePanel.tileSize;
            case "down" -> plantY += gamePanel.tileSize;
            case "left" -> plantX -= gamePanel.tileSize;
            case "right" -> plantX += gamePanel.tileSize;
        }

        if (!gamePanel.mapM.getMap().isPlantingSpot(plantX, plantY)) {
            gamePanel.uiM.showMessage("Hanya bisa menanam di titik yang telah ditentukan!");
            return false;
        }

        GameObject[] objects = gamePanel.mapM.getMap().objects;
        for (GameObject obj : objects) {
            if (obj != null) {
                int objLeft = obj.worldX;
                int objRight = obj.worldX + gamePanel.tileSize;
                int objTop = obj.worldY;
                int objBottom = obj.worldY + gamePanel.tileSize;

                if (plantX >= objLeft && plantX < objRight && plantY >= objTop && plantY < objBottom) {
                    gamePanel.uiM.showMessage("Tidak bisa menanam di sini!");
                    return false;
                }
            }
        }

        for (int i = 0; i < objects.length; i++) {
            if (objects[i] == null) {
                int currentDay = gamePanel.timeM.getCurrentDay();
                OBJ_Tree tree = new OBJ_Tree(currentDay);
                tree.worldX = plantX;
                tree.worldY = plantY;
                tree.index = i;
                objects[i] = tree;

                // Record tree in global list for saving
                gamePanel.plantedTrees.add(new model.PlayerData.PlantedTreeData(
                        plantX, plantY, gamePanel.mapM.currMap, currentDay));

                inventory.removeItem(bibitName, 1);

                gamePanel.triggerPlanting();

                if (gamePanel.chapter2Active && gamePanel.mapM.currMap == 0 && gamePanel.csM.getPhase() == 49) {
                    gamePanel.csM.nextPhase();
                }

                audio.playSound(2);
                gamePanel.uiM.showMessage("Pohon berhasil ditanam!");

                return true;
            }
        }

        gamePanel.uiM.showMessage("Tidak ada ruang untuk objek baru!");
        return false;
    }

    public void collectTrash(String mapName, int x, int y) {
        String key = mapName + "_" + x + "_" + y;
        collectedTrash.add(key);
        System.out.println("[Player] Collected trash: " + key + " (Total: " + collectedTrash.size() + ")");

        // Achievement Logic
        AchievementManager am = AchievementManager.getInstance(gamePanel);
        if (collectedTrash.size() == 1) {
            am.unlockAchievement("Pekerja Pemula", "Mengambil sampah pertama kali.");
        } else if (collectedTrash.size() == 10) {
            am.unlockAchievement("Lingkungan Bersih", "Mengumpulkan 10 sampah.");
        }
    }

    public boolean isTrashCollected(String mapName, int x, int y) {
        String key = mapName + "_" + x + "_" + y;
        return collectedTrash.contains(key);
    }

    public java.util.Set<String> getCollectedTrash() {
        return collectedTrash;
    }

    public void setCollectedTrash(java.util.Set<String> collectedTrash) {
        this.collectedTrash = collectedTrash;
    }
}
