package model;

import controller.GamePanel;
import java.awt.Rectangle;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.image.BufferedImage;
import view.PlayScreen;

public class Mother extends Entity {

    GamePanel gamePanel;
    public int screenX, screenY;
    private int currentSentence = 0;
    private boolean dialogueCompleted = false;
    private int finalChoice = -1;

    private String[][] dialogues;

    public Mother(GamePanel gp) {
        this.gamePanel = gp;
        loadDialogues();

        speed = 0;
        hitboxDefaultX = 8;
        hitboxDefaultY = 16;
        hitbox = new Rectangle(hitboxDefaultX, hitboxDefaultY, 32, 32);
        direction = "down";

        getMotherSprite();
    }

    private void loadDialogues() {
        dialogues = new String[][] {
                { "Eh, udah melek?" },
                { "Kirain masih ngebo mentang-mentang libur." },
                { "Tumben pegang buku itu lagi? Biasanya cuma jadi pajangan di laci." },

                { "Haaah... sama." },
                { "Di grup WA RT juga lagi heboh. Serem ya." },
                { "Kalau alam udah ngamuk mah, manusia bisa apa?" },

                { "Alhamdulillah... kesambet apa anak Ibu pagi-pagi?" },
                { "Ya udah, bagus." },
                { "Asal jangan anget-anget tahi ayam aja ya." },

                { "Ibu ngerti... Muka kamu tuh nggak bisa bohong." },
                { "Pasti ruwet mikirin berita TV kan?" },
                { "Kamu tuh persis Bapakmu. Kalau kepikiran sesuatu, diem aja terus dicatet." },

                { "Intinya, nggak usah mikir kejauhan mau nyelamatin dunia." },
                { "Mulai aja dari depan pager sendiri. Itu udah cukup." },
                { "Udah ah, sana mandi biar cengghar. Bau acem!" }
        };
    }

    private void getMotherSprite() {
        try {
            String[] possibleFolders = {
                    "/NPC/ibunadia/",
                    "/NPC/mother/"
            };

            for (String folder : possibleFolders) {
                down1 = loadSprite(folder + "down1.png");
                if (down1 != null) {
                    down2 = loadSprite(folder + "down2.png");
                    up1 = loadSprite(folder + "up1.png");
                    up2 = loadSprite(folder + "up2.png");
                    left1 = loadSprite(folder + "left1.png");
                    left2 = loadSprite(folder + "left2.png");
                    right1 = loadSprite(folder + "right1.png");
                    right2 = loadSprite(folder + "right2.png");
                    standSprite = loadSprite(folder + "stand.png");
                    BufferedImage standUp = loadSprite(folder + "standup.png");
                    if (standUp != null) {
                        up1 = standUp;
                    }
                    System.out.println("[Mother] Loaded sprites from: " + folder);
                    return;
                }
            }

            System.out.println("[Mother] Custom sprites not found, using farmer fallback.");
            loadFarmerFallbackSprites();
        } catch (Exception e) {
            System.err.println("[Mother] Loading failed: " + e.getMessage());
            loadFarmerFallbackSprites();
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

    private void loadFarmerFallbackSprites() {
        try {
            up1 = loadSprite("/farmer/farmerup1.png");
            up2 = loadSprite("/farmer/farmerup2.png");
            down1 = loadSprite("/farmer/farmerdown1.png");
            down2 = loadSprite("/farmer/farmerdown2.png");
            left1 = loadSprite("/farmer/farmerleft1.png");
            left2 = loadSprite("/farmer/farmerleft2.png");
            right1 = loadSprite("/farmer/farmerright1.png");
            right2 = loadSprite("/farmer/farmerright2.png");
        } catch (Exception ex) {
        }
    }

    public void setPosition(int x, int y) {
        this.worldX = x;
        this.worldY = y;
    }

    public void update() {
        spriteCounter++;
        if (spriteCounter > 15) {
            spriteNum = (spriteNum == 1) ? 2 : 1;
            spriteCounter = 0;
        }
    }

    public boolean canInteract(int interactDistance) {
        int playerMiddleX = gamePanel.player.worldX + (gamePanel.tileSize / 2);
        int playerMiddleY = gamePanel.player.worldY + (gamePanel.tileSize / 2);
        int motherMiddleX = this.worldX + (gamePanel.tileSize / 2);
        int motherMiddleY = this.worldY + (gamePanel.tileSize / 2);

        double dist = Math
                .sqrt(Math.pow(playerMiddleX - motherMiddleX, 2) + Math.pow(playerMiddleY - motherMiddleY, 2));
        return dist <= interactDistance;
    }

    public void interact() {
        System.out.println("[Mother] Interaction! Sentence: " + currentSentence);
        if (gamePanel.uiM == null || gamePanel.uiM.getDialogBox() == null) {
            return;
        }
        PlayScreen dialog = gamePanel.uiM.getDialogBox();

        // Special dialogue if Chapter 1 is finished
        if (!gamePanel.chapter1Active) {
            dialog.showDialog(controller.StoryManager.getInstance().getDialog("mother_post_ch1"), "Ibu");
            return;
        }

        if (gamePanel.mapM.currMap == 0) {
            dialog.showDialog(
                    "Tuh lihat, sampahnya masih banyak berserakan di depan pagar.\nAmbilin gih! Pakai buku catatanmu buat periksa apa aja yang perlu diberesin.",
                    "Ibu");
            dialogueCompleted = true;
            return;
        }

        if (currentSentence >= 0 && currentSentence < 3) {
            dialog.showDialog(dialogues[currentSentence][0], "Ibu", () -> {
                currentSentence++;
                if (currentSentence < 3) {
                    interact();
                } else {
                    showInitialChoice(dialog);
                }
            });
            return;
        }

        if (currentSentence == 3) {
            showInitialChoice(dialog);
            return;
        }

        if (currentSentence >= 4 && currentSentence < 7) {
            int idx = currentSentence - 4 + 3;
            dialog.showDialog(dialogues[idx][0], "Ibu", () -> {
                currentSentence++;
                if (currentSentence < 7) {
                    interact();
                } else {
                    currentSentence = 15;
                    interact();
                }
            });
            return;
        }

        if (currentSentence >= 7 && currentSentence < 10) {
            int idx = currentSentence - 7 + 6;
            dialog.showDialog(dialogues[idx][0], "Ibu", () -> {
                currentSentence++;
                if (currentSentence < 10) {
                    interact();
                } else {
                    currentSentence = 15;
                    interact();
                }
            });
            return;
        }

        if (currentSentence >= 10 && currentSentence < 13) {
            int idx = currentSentence - 10 + 9;
            dialog.showDialog(dialogues[idx][0], "Ibu", () -> {
                currentSentence++;
                if (currentSentence < 13) {
                    interact();
                } else {
                    currentSentence = 15;
                    interact();
                }
            });
            return;
        }

        if (currentSentence >= 15 && currentSentence < 18) {
            int idx = currentSentence - 15 + 12;
            dialog.showDialog(dialogues[idx][0], "Ibu", () -> {
                currentSentence++;
                if (currentSentence < 18) {
                    interact();
                } else {
                    showFinalChoice(dialog);
                }
            });
            return;
        }

        dialog.showDialog("Ayo buruan mandi atau sarapan!", "Ibu");
    }

    private void showFinalChoice(PlayScreen dialog) {
        dialog.showDialogWithChoices(
                "Kamu memutuskan...",
                "Ibu",
                new String[] {
                        "Iya Bu, mandi dulu!",
                        "Nanti Bu, sarapan dulu..."
                },
                (idx, txt) -> {
                    if (idx == 0) {
                        gamePanel.uiM.getPlayScreen().showMessage("Kamu memutuskan untuk mandi dulu.");
                        finalChoice = 0;
                    } else {
                        gamePanel.uiM.getPlayScreen().showMessage("Kamu memutuskan untuk sarapan dulu.");
                        finalChoice = 1;
                    }
                    dialogueCompleted = true;
                    System.out.println("[Mother] Dialogue complete with choice: " + finalChoice);
                });
    }

    public boolean isDialogueCompleted() {
        return dialogueCompleted;
    }

    public int getFinalChoice() {
        return finalChoice;
    }

    private void showInitialChoice(PlayScreen dialog) {
        dialog.showDialogWithChoices(
                "Pilih responmu:",
                "Ibu",
                new String[] {
                        "Aku kepikiran berita banjir, Bu.",
                        "Mau mulai beresin rumah, Bu.",
                        "(Diam, lalu tunjukkan isi catatan)"
                },
                (idx, txt) -> {
                    if (idx == 0) {
                        currentSentence = 4;
                    } else if (idx == 1) {
                        currentSentence = 7;
                    } else {
                        currentSentence = 10;
                    }
                    interact();
                });
    }

    public void draw(Graphics2D g2) {
        screenX = worldX - gamePanel.player.worldX + gamePanel.player.screenX;
        screenY = worldY - gamePanel.player.worldY + gamePanel.player.screenY;

        BufferedImage image;
        if (speed == 0 && standSprite != null) {
            image = (direction.equals("up") && up1 != null) ? up1 : standSprite;
        } else {
            image = switch (direction) {
                case "up" -> (spriteNum == 1) ? up1 : up2;
                case "left" -> (spriteNum == 1) ? left1 : left2;
                case "right" -> (spriteNum == 1) ? right1 : right2;
                default -> (spriteNum == 1) ? down1 : down2;
            };
        }

        if (image != null) {
            int drawSize = (gamePanel.mapM.currMap == 5) ? (int) (gamePanel.tileSize * 1.25) : gamePanel.tileSize;
            g2.drawImage(image, screenX, screenY, drawSize, drawSize, null);
        }

        if (canInteract((int) (gamePanel.tileSize * 1.5))) {
            g2.setColor(Color.YELLOW);
            g2.drawString("[E] Bicara", screenX, screenY - 5);
        }
    }
}
