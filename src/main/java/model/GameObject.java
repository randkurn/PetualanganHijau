package model;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Map;
import javax.imageio.ImageIO;
import controller.GamePanel;
import controller.StateManager.gameState;
import view.AudioManager;

public class GameObject {

    public enum Type {
        TRASH, BIN, KEY, GATE, PORTAL, DECORATION, BED
    }

    public String name;
    public Type type;
    public BufferedImage image;
    public boolean collision = false;
    public boolean pickable = true;
    public int worldX, worldY;
    public Rectangle hitbox = new Rectangle(0, 0, 48, 48);
    public int drawWidth = 48;
    public int drawHeight = 48;
    public int hitboxDefaultX = 0;
    public int hitboxDefaultY = 0;
    public int index;

    public String displayName;
    public String description;
    public int goldValue;
    public String category;
    public int targetArea = -1;
    public int targetX = -1;
    public int targetY = -1;
    public String targetDirection = null;
    public TrashType binType;

    public GameObject(Type type) {
        this.type = type;
        this.hitboxDefaultX = hitbox.x;
        this.hitboxDefaultY = hitbox.y;
    }

    public void draw(Graphics2D g2, GamePanel gp) {
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        if (image != null) {
            g2.drawImage(image, screenX, screenY, drawWidth, drawHeight, null);
        }
    }

    public void interact(GamePanel gp) {
        switch (type) {
            case TRASH:
                AudioManager.getInstance().playSound(2);
                if (gp.player.inventory.addItem(displayName, 1, image, trashType)) {
                    gp.uiM.showMessage("Berhasil memungut: " + displayName);
                    gp.player.collectTrash(gp.mapM.getMap().levelName, worldX, worldY);
                    gp.mapM.getMap().objects[index] = null;

                    gp.player.energy--;
                    if (gp.player.energy < 0)
                        gp.player.energy = 0;

                    if (gp.player.energy < 5) {
                        gp.triggerExhaustion();
                    }

                    if (gp.chapter1Active) {
                        gp.chapter1TrashCount++;
                        System.out.println("[GameObject] Chapter1 trash count: " + gp.chapter1TrashCount);
                    }
                    if (gp.chapter2Active) {
                        gp.chapter2TrashCount++;
                    }
                } else {
                    gp.uiM.showMessage("Inventory penuh!");
                }
                break;
            case BIN:
                sortTrashIntoBin(gp);
                break;
            case KEY:
                AudioManager.getInstance().playSound(2);
                gp.player.gold += 50;
                gp.uiM.showMessage("Kunci Berharga! +50 Gold");
                gp.mapM.getMap().objects[index] = null;
                break;
            case GATE:
            case PORTAL:
                if (displayName != null && displayName.contains("Pintu Masuk")) {
                    gp.uiM.getDialogBox().showDialogWithChoices(
                            "Ingin masuk ke dalam rumah?",
                            "Pintu",
                            new String[] { "Ya, Masuk", "Tidak" },
                            (idx, txt) -> {
                                if (idx == 0) {
                                    executeTeleport(gp);
                                } else {
                                    if (gp.mapM.currMap == 5)
                                        pushPlayerBack(gp);
                                }
                            });
                } else if (displayName != null && displayName.contains("Pintu Keluar")) {
                    gp.uiM.getDialogBox().showDialogWithChoices(
                            "Ingin keluar rumah?",
                            "Pintu",
                            new String[] { "Ya, Keluar", "Tidak" },
                            (idx, txt) -> {
                                if (idx == 0) {
                                    executeTeleport(gp);
                                } else {
                                    if (gp.mapM.currMap == 5)
                                        pushPlayerBack(gp);
                                }
                            });
                } else {
                    executeTeleport(gp);
                }
                break;
            case DECORATION:
                // No message for generic decorations to avoid movement bugs
                break;
            case BED:
                int currentHour = controller.TimeManager.getInstance().getHour();
                boolean isEvening = currentHour >= 18 || currentHour < 5;

                if ((gp.chapter1Active || gp.chapter2Active) && !isEvening) {
                    gp.uiM.showMessage("Sekarang belum waktunya tidur. (Bisa tidur mulai jam 18:00)");
                    return;
                }

                gp.uiM.getDialogBox().showDialogWithChoices(
                        "Ingin tidur untuk menyimpan progres dan memulai hari baru?",
                        "Tempat Tidur",
                        new String[] { "Ya, Tidur & Simpan", "Tidak" },
                        (choiceIndex, text) -> {
                            if (choiceIndex == 0) {
                                gp.triggerSleepAfterSave = true;
                                gp.uiM.getSaveLoadScreen().setSaveMode(true);
                                gp.stateM.setCurrentState(gameState.SAVE_LOAD);
                            } else {
                                if (gp.mapM.currMap == 5)
                                    pushPlayerBack(gp);
                            }
                        });
                break;
        }
    }

    public TrashType trashType;

    public static GameObject createTrash(String name, String path, int gold, TrashType type) {
        GameObject obj = new GameObject(Type.TRASH);
        obj.displayName = name;
        obj.image = setupImage(path);
        obj.collision = false;
        obj.goldValue = gold;
        obj.trashType = type;
        return obj;
    }

    public static GameObject createBin(String name, String path, String desc, TrashType type) {
        GameObject obj = new GameObject(Type.BIN);
        obj.displayName = name;
        obj.description = desc;
        obj.binType = type;
        obj.image = setupImage(path);
        obj.collision = true;
        obj.pickable = false;
        return obj;
    }

    private void sortTrashIntoBin(GamePanel gp) {
        Map<String, Integer> items = gp.player.inventory.getAllItems();
        boolean found = false;
        int count = 0;

        for (String itemName : items.keySet()) {
            TrashType itype = gp.player.inventory.getItemType(itemName);
            if (itype == this.binType) {
                int qty = items.get(itemName);
                count += qty;
                gp.player.inventory.removeItem(itemName, qty);
                gp.player.score += 10 * qty;
                gp.player.gold += 5 * qty;
                found = true;
            }
        }

        if (found) {
            AudioManager.getInstance().playSound(2);
            gp.uiM.getPlayScreen().showDialog(
                    "Berhasil memilah " + count + " sampah. Score + " + (10 * count),
                    displayName);
        } else {
            gp.uiM.getPlayScreen().showDialog(description + "\n(Kamu tidak punya sampah jenis ini)", displayName);
        }
    }

    public static GameObject createSimple(Type type, String name, String path, boolean coll) {
        GameObject obj = new GameObject(type);
        obj.displayName = name;
        obj.image = setupImage(path);
        obj.collision = coll;
        return obj;
    }

    private static BufferedImage setupImage(String path) {
        try {
            return ImageIO.read(GameObject.class.getResourceAsStream(path));
        } catch (Exception e) {
            return null;
        }
    }

    public void update(GamePanel gp) {
    }

    private void executeTeleport(GamePanel gp) {
        if (targetArea != -1) {
            gp.mapM.changeToArea(targetArea);
            if (targetX != -1 && targetY != -1) {
                gp.player.worldX = targetX * gp.tileSize;
                gp.player.worldY = targetY * gp.tileSize;
            }
            if (targetDirection != null) {
                gp.player.direction = targetDirection;
            }
        } else if (displayName != null && displayName.contains("Keluar")) {
            gp.mapM.previousArea();
        } else {
            gp.mapM.nextArea();
        }
        gp.uiM.showMessage("Pindah Area...");
    }

    private void pushPlayerBack(GamePanel gp) {
        switch (gp.player.direction) {
            case "up" -> gp.player.worldY += 5;
            case "down" -> gp.player.worldY -= 5;
            case "left" -> gp.player.worldX += 5;
            case "right" -> gp.player.worldX -= 5;
        }
    }
}
