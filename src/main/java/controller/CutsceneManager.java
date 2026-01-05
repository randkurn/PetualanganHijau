package controller;

import java.awt.Color;
import java.awt.Graphics2D;

public class CutsceneManager {
    private GamePanel gp;
    private int phase = 0;
    private int counter = 0;

    private int barHeight = 0;
    private final int MAX_BAR_HEIGHT = 80;
    private boolean fullBlack = false;
    private String currentBranch = "";
    private String narrativeText = "";
    private int narrativeTimer = 0;

    public CutsceneManager(GamePanel gp) {
        this.gp = gp;
    }

    public int getPhase() {
        return phase;
    }

    public void setPhase(int phase) {
        this.phase = phase;
        this.counter = 0;
    }

    public void update() {
        updateBlackBars();

        if (gp.stateM.getCurrentState() == StateManager.gameState.PLAY && phase == 0) {
            if (gp.chapter1Active) {
                phase = 20;
            } else if (gp.chapter2Active) {
                phase = 26;
            }
        }

        if (gp.stateM.getCurrentState() == StateManager.gameState.CUTSCENE ||
                (gp.stateM.getCurrentState() == StateManager.gameState.DIALOGUE && phase > 0) ||
                (gp.stateM.getCurrentState() == StateManager.gameState.INVENTORY && phase > 0) ||
                (gp.stateM.getCurrentState() == StateManager.gameState.PLAY && phase > 0)) {
            playScene();
        }
    }

    private void updateBlackBars() {
        if (gp.stateM.getCurrentState() == StateManager.gameState.CUTSCENE) {
            if (barHeight < MAX_BAR_HEIGHT) {
                barHeight += 4;
            }
        } else {
            if (barHeight > 0) {
                barHeight -= 4;
            }
        }
    }

    private void playScene() {
        switch (phase) {
            case 0:
                counter++;
                if (counter > 30) {
                    System.out.println("[Cutscene] Starting Scene 1 (WAKE_UP)");
                    gp.startChapter1Scenes();
                    phase = 1;
                    counter = 0;
                }
                break;

            case 1:
                break;

            case 2:
                gp.stateM.setCurrentState(StateManager.gameState.CUTSCENE);
                boolean moved = moveEntityTo(gp.player, 3 * gp.tileSize, 9 * gp.tileSize, 2);
                if (moved) {
                    gp.player.direction = "down";
                    gp.player.spriteNum = 1;
                    gp.player.speed = 0;
                    System.out.println("[Cutscene] Player reached (3,9), starting Scene 2");
                    counter++;
                    if (counter > 20) {
                        gp.sceneM.startScene(SceneManager.Chapter1Scene.GREEN_BOOK);
                        gp.stateM.setCurrentState(StateManager.gameState.SCENE);
                        phase = 3;
                        counter = 0;
                    }
                }
                break;

            case 3:
                break;

            case 4:
                counter++;

                if (counter == 1) {
                    gp.player.direction = "up";
                    gp.player.speed = 0;
                }

                if (counter > 60) {
                    counter = 0;
                    phase = 10;
                }
                break;

            case 10:
                counter++;
                if (counter == 1) {
                    System.out.println("[Cutscene] Spawning Mother at (5,9)");

                    if (gp.npcM != null) {
                        if (gp.npcM.getMothers().isEmpty()) {
                            gp.npcM.createMother(5 * gp.tileSize, 9 * gp.tileSize);
                        } else {
                            model.Mother mother = gp.npcM.getMothers().get(0);
                            mother.setPosition(5 * gp.tileSize, 9 * gp.tileSize);
                            mother.direction = "down";
                        }
                    }

                    gp.player.direction = "right";
                    gp.player.speed = 0;
                }

                if (counter > 30) {
                    System.out.println("[Cutscene] Starting Mother dialogue (in-game)");
                    gp.stateM.setCurrentState(StateManager.gameState.DIALOGUE);

                    if (gp.npcM != null && !gp.npcM.getMothers().isEmpty()) {
                        model.Mother mother = gp.npcM.getMothers().get(0);
                        mother.interact();
                    }

                    phase = 5;
                    counter = 0;
                }
                break;

            case 5:
                boolean motherDone = false;
                if (gp.npcM != null && !gp.npcM.getMothers().isEmpty()) {
                    model.Mother mother = gp.npcM.getMothers().get(0);
                    motherDone = mother.isDialogueCompleted();
                }

                if (motherDone) {
                    System.out.println("[Cutscene] Mother dialogue completed - moving to activity transition");
                    phase = 11;
                    counter = 0;
                }
                break;

            case 11:
                counter++;
                if (counter == 1) {
                    fullBlack = true;
                    gp.player.speed = 0;
                    gp.stateM.setCurrentState(StateManager.gameState.CUTSCENE);
                }

                if (counter == 30) {
                    gp.uiM.getPlayScreen().showDialogAutoNext(
                            "Kamu sudah selesai mandi dan sarapan.\n\nPakaian rapi. Pikiran lebih tenang.",
                            "Narasi");
                }

                // Wait for dialog to finish, then show next line
                if (counter == 180) {
                    gp.uiM.getPlayScreen().showDialogAutoNext(
                            "Tanpa kamu sadari, langkahmu membawamu keluar rumah.",
                            "Narasi");
                }

                if (counter > 300) {
                    System.out.println("[Cutscene] Narration finished - teleporting to outside (hidden)");
                    phase = 12;
                    counter = 0;
                }
                break;

            case 12:
                fullBlack = true;

                // IMPORTANT: Clear any remaining narrative text from previous dialog
                narrativeText = "";
                gp.uiM.getPlayScreen().clearDialog();
                gp.uiM.resetPlayScreen();
                gp.npcM.clearAll();

                gp.mapM.changeToAreaWithoutRespawn(0);
                gp.player.worldX = 11 * gp.tileSize;
                gp.player.worldY = 9 * gp.tileSize;
                gp.player.speed = 0;
                gp.player.direction = "down";

                System.out.println("[Cutscene] Player teleported to (11,9) - Starting Scene 5");

                // Start Scene 5 using SceneManager (like Scene 1 and 2)
                fullBlack = false;
                gp.sceneM.startScene(SceneManager.Chapter1Scene.OUTSIDE);
                gp.stateM.setCurrentState(StateManager.gameState.SCENE);
                phase = 14; // Wait for Scene 5 to finish
                counter = 0;
                break;

            case 14:
                break;

            case 15:
                phase = 13;
                counter = 0;
                break;

            case 13:
                System.out.println("[Cutscene] Entering main game loop Chapter 1");

                gp.mapM.getMap().setObject();

                gp.uiM.getPlayScreen()
                        .showMessage("TUTORIAL: Gerak [WASD].\nAmbil sampah otomatis dengan mendekatinya.");
                gp.player.speed = 4;
                gp.stateM.setCurrentState(StateManager.gameState.PLAY);
                phase = 20;
                counter = 0;
                break;

            case 20:
                if (gp.chapter1TrashCount >= 5) {
                    System.out.println("[Cutscene] Chapter 1 Objective Met! Starting Ending Narrative");
                    gp.player.speed = 0;
                    gp.stateM.setCurrentState(StateManager.gameState.CUTSCENE);
                    fullBlack = true;

                    gp.chapter1Active = false;
                    gp.chapter2Active = true;

                    phase = 211;
                    counter = 0;
                }
                break;

            case 211:
                counter++;
                if (counter == 1) {
                    fullBlack = true;
                    narrativeText = "Kamu mulai bergerak.";
                    narrativeTimer = 100;
                }
                if (counter > narrativeTimer) {
                    phase = 212;
                    counter = 0;
                }
                break;
            case 212:
                counter++;
                if (counter == 1) {
                    narrativeText = StoryManager.getInstance().getDialog("ch1_narrative_1");
                }
                if (counter > 100) {
                    phase = 213;
                    counter = 0;
                }
                break;
            case 213:
                counter++;
                if (counter == 1) {
                    narrativeText = StoryManager.getInstance().getDialog("ch1_narrative_2");
                }
                if (counter > 100) {
                    phase = 214;
                    counter = 0;
                }
                break;
            case 214:
                counter++;
                if (counter == 1) {
                    narrativeText = StoryManager.getInstance().getDialog("ch1_narrative_3");
                }
                if (counter > 100) {
                    phase = 215;
                    counter = 0;
                }
                break;
            case 215:
                counter++;
                if (counter == 1) {
                    narrativeText = StoryManager.getInstance().getDialog("ch1_narrative_end");
                }
                if (counter > 180) {
                    AchievementManager.getInstance(gp).unlockAchievement("Pahlawan Warga", "Menyelesaikan Chapter 1.");
                    narrativeText = "";
                    phase = 24;
                    counter = 0;
                }
                break;

            case 22:
            case 221:
            case 23:
                phase = 24;
                break;

            case 24:
                counter++;
                if (counter == 1) {
                    gp.chapter1Active = false;
                    gp.chapter2Active = true;
                    gp.player.onPath = false;

                    gp.mapM.changeToArea(0);
                    gp.player.worldX = 11 * gp.tileSize;
                    gp.player.worldY = 8 * gp.tileSize;
                    gp.player.direction = "down";
                    gp.player.speed = 0;
                    gp.uiM.getSaveLoadScreen().setSaveMode(true);
                    gp.stateM.setCurrentState(StateManager.gameState.SAVE_LOAD);
                    System.out.println(
                            "[Cutscene] Triggering Chapter 1 End Save and Map 0 Teleport. Chapter 2 Active set.");
                }

                if (gp.stateM.getCurrentState() != StateManager.gameState.SAVE_LOAD && counter > 1) {
                    phase = 25;
                    counter = 0;
                }
                break;

            case 25:
                counter++;
                fullBlack = true;
                if (counter == 1) {
                    narrativeText = StoryManager.getInstance().getDialog("ch2_title");
                }

                if (counter > 150) {
                    narrativeText = "";
                    fullBlack = false;
                    gp.chapter1Active = false;
                    gp.chapter2Active = true;
                    gp.player.onPath = false;
                    gp.player.speed = 4;
                    gp.stateM.setCurrentState(StateManager.gameState.PLAY);
                    phase = 26;
                    counter = 0;
                    System.out
                            .println("[Cutscene] Chapter 2 started - Waiting for 5 trash total (10 overall). Current: "
                                    + gp.player.getCollectedTrash().size());
                }
                break;

            case 26:
                if (gp.chapter2TrashCount >= 5) {
                    gp.stateM.setCurrentState(StateManager.gameState.CUTSCENE);
                    phase = 30;
                    counter = 0;
                }
                break;

            case 30:
                counter++;
                model.PakBahlil bahlil_ref = gp.npcM.getPakBahlil();

                if (counter == 1) {
                    fullBlack = false;

                    if (bahlil_ref == null) {
                        System.out.println("[Cutscene] Creating Pak Bahlil off-screen for walk-in");
                        bahlil_ref = gp.npcM.createPakBahlil(
                                gp.player.worldX,
                                gp.player.worldY + (6 * gp.tileSize));
                    } else {
                        System.out.println("[Cutscene] Moving Pak Bahlil off-screen for walk-in");
                        bahlil_ref.worldX = gp.player.worldX;
                        bahlil_ref.worldY = gp.player.worldY + (6 * gp.tileSize);
                    }

                    bahlil_ref.clearGoal();
                    bahlil_ref.direction = "up";
                    bahlil_ref.speed = 4;

                    int targetCol = (gp.player.worldX + gp.player.hitbox.x) / gp.tileSize;
                    int targetRow = (gp.player.worldY + gp.player.hitbox.y) / gp.tileSize + 1;
                    bahlil_ref.setGoal(targetCol, targetRow);
                    System.out
                            .println("[Cutscene] Pak Bahlil approaching from (" + bahlil_ref.worldX / gp.tileSize + ","
                                    + bahlil_ref.worldY / gp.tileSize + ") to (" + targetCol + "," + targetRow + ")");
                }

                if (bahlil_ref != null && bahlil_ref.isGoalReached()) {
                    System.out.println("[Cutscene] Pak Bahlil reached player");
                    bahlil_ref.direction = "up";
                    bahlil_ref.speed = 0;
                    bahlil_ref.spriteNum = 1;
                    bahlil_ref.clearGoal();
                    phase = 305;
                    counter = 0;
                } else if (counter > 150) {
                    System.out.println("[Cutscene] Pak Bahlil took too long, force start dialog");
                    if (bahlil_ref != null) {
                        bahlil_ref.worldX = gp.player.worldX;
                        bahlil_ref.worldY = gp.player.worldY + gp.tileSize;
                        bahlil_ref.direction = "up";
                        bahlil_ref.speed = 0;
                        bahlil_ref.spriteNum = 1;
                        bahlil_ref.clearGoal();
                    }
                    phase = 305;
                    counter = 0;
                }
                break;

            case 305:
                counter++;
                if (counter == 1) {
                    gp.stateM.setCurrentState(StateManager.gameState.DIALOGUE);
                    gp.uiM.getPlayScreen().showDialog(
                            StoryManager.getInstance().getDialog("bahlil_greeting_ch2_1"),
                            "Pak Bahlil");
                }
                if (gp.stateM.getCurrentState() != StateManager.gameState.DIALOGUE && counter > 1) {
                    phase = 306;
                    counter = 0;
                }
                break;

            case 306:
                counter++;
                if (counter == 1) {
                    gp.stateM.setCurrentState(StateManager.gameState.DIALOGUE);
                    gp.uiM.getPlayScreen().showDialog(
                            StoryManager.getInstance().getDialog("bahlil_greeting_ch2_2"),
                            "Pak Bahlil");
                }
                if (gp.stateM.getCurrentState() != StateManager.gameState.DIALOGUE && counter > 1) {
                    phase = 31;
                    counter = 0;
                }
                break;

            case 31:
                counter++;
                if (counter == 1) {
                    gp.stateM.setCurrentState(StateManager.gameState.DIALOGUE);
                    gp.uiM.getDialogBox().showDialogWithChoices(
                            StoryManager.getInstance().getDialog("choice_label"),
                            "",
                            new String[] {
                                    StoryManager.getInstance().getDialog("bahlil_choice_1"),
                                    StoryManager.getInstance().getDialog("bahlil_choice_2")
                            },
                            (idx, txt) -> currentBranch = (idx == 0) ? "A" : "B");
                }
                if (gp.stateM.getCurrentState() != StateManager.gameState.DIALOGUE && counter > 1) {
                    phase = 32;
                    counter = 0;
                }
                break;

            case 32:
                counter++;
                if (counter == 1) {
                    String resp = currentBranch.equals("A")
                            ? StoryManager.getInstance().getDialog("bahlil_resp_a_1")
                            : StoryManager.getInstance().getDialog("bahlil_resp_b_1");
                    gp.stateM.setCurrentState(StateManager.gameState.DIALOGUE);
                    gp.uiM.getPlayScreen().showDialog(resp, "Pak Bahlil");
                }
                if (gp.stateM.getCurrentState() != StateManager.gameState.DIALOGUE && counter > 1) {
                    phase = 325;
                    counter = 0;
                }
                break;

            case 325:
                counter++;
                if (counter == 1) {
                    String resp = currentBranch.equals("A")
                            ? StoryManager.getInstance().getDialog("bahlil_resp_a_2a")
                            : StoryManager.getInstance().getDialog("bahlil_resp_b_2a");
                    gp.stateM.setCurrentState(StateManager.gameState.DIALOGUE);
                    gp.uiM.getPlayScreen().showDialog(resp, "Pak Bahlil");
                }
                if (gp.stateM.getCurrentState() != StateManager.gameState.DIALOGUE && counter > 1) {
                    phase = 326;
                    counter = 0;
                }
                break;

            case 326:
                counter++;
                if (counter == 1) {
                    String resp = currentBranch.equals("A")
                            ? StoryManager.getInstance().getDialog("bahlil_resp_a_2b")
                            : StoryManager.getInstance().getDialog("bahlil_resp_b_2b");
                    gp.stateM.setCurrentState(StateManager.gameState.DIALOGUE);
                    gp.uiM.getPlayScreen().showDialog(resp, "Pak Bahlil");
                }
                if (gp.stateM.getCurrentState() != StateManager.gameState.DIALOGUE && counter > 1) {
                    phase = 33;
                    counter = 0;
                }
                break;

            case 33:
                counter++;
                if (counter == 1) {
                    gp.stateM.setCurrentState(StateManager.gameState.DIALOGUE);
                    gp.uiM.getPlayScreen().showDialog(
                            StoryManager.getInstance().getDialog("bahlil_advice_1"),
                            "Pak Bahlil");
                }
                if (gp.stateM.getCurrentState() != StateManager.gameState.DIALOGUE && counter > 1) {
                    phase = 331;
                    counter = 0;
                }
                break;

            case 331:
                counter++;
                if (counter == 1) {
                    gp.stateM.setCurrentState(StateManager.gameState.DIALOGUE);
                    gp.uiM.getPlayScreen().showDialog(
                            StoryManager.getInstance().getDialog("bahlil_advice_2"),
                            "Pak Bahlil");
                }
                if (gp.stateM.getCurrentState() != StateManager.gameState.DIALOGUE && counter > 1) {
                    gp.uiM.showMessage(StoryManager.getInstance().getDialog("obj_panjul"));

                    gp.npcM.spawnChapter2NPCs();

                    model.Panjul panjul = gp.npcM.getPanjul();
                    if (panjul != null) {
                        panjul.worldX = 52 * gp.tileSize;
                        panjul.worldY = 32 * gp.tileSize;
                        panjul.direction = "down";
                    }

                    gp.player.onPath = true;
                    gp.player.goalCol = 52;
                    gp.player.goalRow = 33;
                    gp.player.speed = 3;

                    // Transition to PLAY state so player.update() can handle onPath movement
                    gp.stateM.setCurrentState(StateManager.gameState.PLAY);

                    phase = 34;
                    counter = 0;
                }
                break;

            case 34:
                if (!gp.player.onPath) {
                    phase = 35;
                    counter = 0;
                }
                break;

            case 35:
                counter++;
                if (counter == 1) {
                    gp.player.speed = 0;
                    gp.player.direction = "right";
                    gp.stateM.setCurrentState(StateManager.gameState.DIALOGUE);

                    gp.uiM.getPlayScreen().showDialog(
                            StoryManager.getInstance().getDialog("panjul_intro_1"),
                            "Panjul (Bank Sampah)");
                }
                if (gp.stateM.getCurrentState() != StateManager.gameState.DIALOGUE && counter > 1) {
                    phase = 351;
                    counter = 0;
                }
                break;

            case 351:
                counter++;
                if (counter == 1) {
                    gp.stateM.setCurrentState(StateManager.gameState.DIALOGUE);
                    gp.uiM.getPlayScreen().showDialog(
                            StoryManager.getInstance().getDialog("panjul_intro_2"),
                            "Panjul (Bank Sampah)", () -> {
                                gp.uiM.getInventoryScreen().resetSorting();
                                gp.uiM.getInventoryScreen().toggleSortingMode();
                                gp.stateM.setCurrentState(StateManager.gameState.INVENTORY);
                                phase = 36;
                                counter = 0;
                            });
                }
                break;

            case 36:
                if (gp.stateM.getCurrentState() == StateManager.gameState.PLAY) {
                    counter++;
                    if (counter == 1) {
                        gp.stateM.setCurrentState(StateManager.gameState.DIALOGUE);
                        gp.uiM.getPlayScreen().showDialog(
                                StoryManager.getInstance().getDialog("panjul_after_sort_1"),
                                "Panjul (Bank Sampah)");
                    }
                    if (gp.stateM.getCurrentState() != StateManager.gameState.DIALOGUE && counter > 1) {
                        phase = 361;
                        counter = 0;
                    }
                }
                break;

            case 361:
                counter++;
                if (counter == 1) {
                    gp.stateM.setCurrentState(StateManager.gameState.DIALOGUE);
                    gp.uiM.getPlayScreen().showDialog(
                            StoryManager.getInstance().getDialog("panjul_after_sort_2"),
                            "Panjul (Bank Sampah)");
                }
                if (gp.stateM.getCurrentState() != StateManager.gameState.DIALOGUE && counter > 1) {
                    phase = 362;
                    counter = 0;
                }
                break;

            case 362:
                counter++;
                if (counter == 1) {
                    gp.stateM.setCurrentState(StateManager.gameState.DIALOGUE);
                    gp.uiM.getPlayScreen().showDialog(
                            StoryManager.getInstance().getDialog("panjul_advice_trees_1"),
                            "Panjul (Bank Sampah)");
                }
                if (gp.stateM.getCurrentState() != StateManager.gameState.DIALOGUE && counter > 1) {
                    phase = 363;
                    counter = 0;
                }
                break;

            case 363:
                counter++;
                if (counter == 1) {
                    gp.stateM.setCurrentState(StateManager.gameState.DIALOGUE);
                    gp.uiM.getPlayScreen().showDialog(
                            StoryManager.getInstance().getDialog("panjul_advice_trees_2"),
                            "Panjul (Bank Sampah)");
                }
                if (gp.stateM.getCurrentState() != StateManager.gameState.DIALOGUE && counter > 1) {
                    phase = 365;
                    counter = 0;
                }
                break;

            case 365:
                counter++;
                if (counter == 1) {
                    gp.stateM.setCurrentState(StateManager.gameState.DIALOGUE);
                    gp.uiM.getPlayScreen().showDialog(
                            StoryManager.getInstance().getDialog("panjul_find_dila"),
                            "Panjul (Bank Sampah)");
                }
                if (gp.stateM.getCurrentState() != StateManager.gameState.DIALOGUE && counter > 1) {
                    phase = 37;
                    counter = 0;
                }
                break;

            case 37:
                // Kembalikan kontrol dan kecepatan ke pemain
                gp.player.speed = 4;
                gp.player.onPath = false;

                gp.stateM.setCurrentState(StateManager.gameState.PLAY);
                gp.uiM.showMessage("LOKASI BARU TERBUKA: Teh Dila\nOBJEKTIF: Temui Teh Dila di Selatan");

                // Spawn Teh Dila if not already there
                if (gp.npcM.getTehDila() == null) {
                    gp.npcM.spawnTehDila(26 * gp.tileSize, 58 * gp.tileSize);
                }

                // We set phase to a waiting state for when player reaches Bu Suci
                // or just end it if Teh Dila interaction is handled separately.
                // For now, let's transition to phase 38 to wait for player.
                phase = 38;
                counter = 0;
                break;

            case 38:
                // Allow phase 38 to update during PLAY state.
                // Wait for player to reach Teh Dila area (approx 26, 58)
                double distToDila = Math.sqrt(Math.pow(gp.player.worldX - 26 * gp.tileSize, 2) +
                        Math.pow(gp.player.worldY - 58 * gp.tileSize, 2));
                if (distToDila < gp.tileSize * 3) {
                    phase = 45;
                    counter = 0;
                }
                break;

            // Removing following/pathing phases as player moves manually
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
                phase = 0;
                break;

            case 45:
                counter++;
                if (counter == 1) {
                    gp.player.speed = 0;
                    gp.stateM.setCurrentState(StateManager.gameState.DIALOGUE);
                    gp.uiM.getPlayScreen().showDialog(
                            StoryManager.getInstance().getDialog("tehdila_intro"),
                            "Teh Dila");
                }
                if (gp.stateM.getCurrentState() != StateManager.gameState.DIALOGUE && counter > 1) {
                    phase = 46;
                    counter = 0;
                }
                break;

            case 46:
                counter++;
                if (counter == 1) {
                    gp.stateM.setCurrentState(StateManager.gameState.DIALOGUE);
                    gp.uiM.getPlayScreen().showDialog(
                            StoryManager.getInstance().getDialog("tehdila_welcome").replace("%VALUE%", "10"),
                            "Teh Dila");
                }
                if (gp.stateM.getCurrentState() != StateManager.gameState.DIALOGUE && counter > 1) {
                    java.awt.image.BufferedImage treeIcon = null;
                    try {
                        treeIcon = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/objects/tree1.png"));
                    } catch (Exception ex) {
                    }
                    gp.player.inventory.addItem("Bibit Mahoni", 1, treeIcon);
                    phase = 47;
                    counter = 0;
                }
                break;

            case 47:
                counter++;
                if (counter == 1) {
                    gp.stateM.setCurrentState(StateManager.gameState.DIALOGUE);
                    gp.uiM.getDialogBox().showDialogWithChoices(
                            "Responmu:",
                            "Pemain",
                            new String[] {
                                    StoryManager.getInstance().getDialog("tehdila_choice_1").replace("%VALUE%", "10"),
                                    StoryManager.getInstance().getDialog("tehdila_choice_2")
                            },
                            (idx, txt) -> currentBranch = (idx == 0) ? "A" : "B");
                }
                if (gp.stateM.getCurrentState() != StateManager.gameState.DIALOGUE && counter > 1) {
                    phase = 48;
                    counter = 0;
                }
                break;

            case 48:
                counter++;
                if (counter == 1) {
                    String resp = currentBranch.equals("A")
                            ? StoryManager.getInstance().getDialog("tehdila_resp_1")
                            : StoryManager.getInstance().getDialog("tehdila_resp_2");
                    gp.stateM.setCurrentState(StateManager.gameState.DIALOGUE);
                    gp.uiM.getPlayScreen().showDialog(resp, "Teh Dila");
                }
                if (gp.stateM.getCurrentState() != StateManager.gameState.DIALOGUE && counter > 1) {
                    gp.player.speed = 4;
                    gp.stateM.setCurrentState(StateManager.gameState.PLAY);
                    phase = 49;
                    counter = 0;
                }
                break;

            case 49:
                counter++;
                if (counter == 1) {
                    gp.uiM.getPlayScreen()
                            .showMessage(StoryManager.getInstance().getDialog("obj_plant"));
                }
                break;

            case 50:
                counter++;
                if (counter == 1) {
                    gp.player.speed = 0;
                    gp.stateM.setCurrentState(StateManager.gameState.DIALOGUE);
                    gp.uiM.getPlayScreen().showDialog(
                            StoryManager.getInstance().getDialog("ch2_monolog_1"),
                            gp.player.getPlayerName());
                }
                if (gp.stateM.getCurrentState() != StateManager.gameState.DIALOGUE && counter > 1) {
                    phase = 501;
                    counter = 0;
                }
                break;

            case 501:
                counter++;
                if (counter == 1) {
                    gp.stateM.setCurrentState(StateManager.gameState.DIALOGUE);
                    gp.uiM.getPlayScreen().showDialog(
                            StoryManager.getInstance().getDialog("ch2_monolog_2"),
                            gp.player.getPlayerName());
                }
                if (gp.stateM.getCurrentState() != StateManager.gameState.DIALOGUE && counter > 1) {
                    phase = 51;
                    counter = 0;
                }
                break;

            case 51:
                counter++;
                if (counter == 1) {
                    fullBlack = true;
                    narrativeText = StoryManager.getInstance().getDialog("ch2_end_narrative");
                }
                if (counter > 240) { // Sekitar 4 detik
                    phase = 0;
                    counter = 0;
                    narrativeText = "";
                    reset();
                    gp.player.speed = 4;
                    gp.player.onPath = false;
                    gp.chapter2Active = false;
                    gp.chapter2Finished = true;

                    // Trigger Save Screen and then Sleep for Chapter 3
                    gp.uiM.getSaveLoadScreen().setSaveMode(true);
                    gp.triggerSleepAfterSave = true;
                    gp.stateM.setCurrentState(StateManager.gameState.SAVE_LOAD);
                }
                break;

            case 8:
                if (counter == 0) {
                    System.out.println("[Cutscene] Showing Tutorial");
                    gp.uiM.getPlayScreen().showMessage(StoryManager.getInstance().getDialog("tutorial_move"));
                }
                if (counter > 60) {
                    System.out.println("[Cutscene] All Scenes Complete - PLAY state");
                    gp.player.speed = 4;
                    gp.stateM.setCurrentState(StateManager.gameState.PLAY);
                    phase = 0;
                    counter = 0;
                    reset();
                }
                break;
        }
    }

    private boolean moveEntityTo(model.Entity e, int targetX, int targetY, int speed) {
        boolean xReached = false;
        boolean yReached = false;

        e.speed = speed;

        e.collisionOn = false;
        gp.checker.checkTileCollision(e);

        if (Math.abs(e.worldX - targetX) <= speed) {
            e.worldX = targetX;
            xReached = true;
        } else {
            if (e.worldX < targetX) {
                if (!e.collisionOn) {
                    e.worldX += speed;
                }
                e.direction = "right";
            } else {
                if (!e.collisionOn) {
                    e.worldX -= speed;
                }
                e.direction = "left";
            }
        }

        if (xReached) {
            e.collisionOn = false;
            gp.checker.checkTileCollision(e);

            if (Math.abs(e.worldY - targetY) <= speed) {
                e.worldY = targetY;
                yReached = true;
            } else {
                if (e.worldY < targetY) {
                    if (!e.collisionOn) {
                        e.worldY += speed;
                    }
                    e.direction = "down";
                } else {
                    if (!e.collisionOn) {
                        e.worldY -= speed;
                    }
                    e.direction = "up";
                }
            }
        }

        e.spriteCounter++;
        if (e.spriteCounter > 12) {
            e.spriteNum = (e.spriteNum == 1) ? 2 : 1;
            e.spriteCounter = 0;
        }

        if (xReached && yReached) {
            e.speed = 0;
        }

        return xReached && yReached;
    }

    public void nextPhase() {
        phase++;
        counter = 0;
        gp.stateM.setCurrentState(StateManager.gameState.CUTSCENE);
        System.out.println("[CutsceneManager] Moving to phase: " + phase);
    }

    public void draw(Graphics2D g2) {
        if (gp.stateM.getCurrentState() == StateManager.gameState.CUTSCENE ||
                (gp.stateM.getCurrentState() == StateManager.gameState.DIALOGUE && phase > 0)) {
            if (fullBlack) {
                g2.setColor(Color.BLACK);
                g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

                if (narrativeText != null && !narrativeText.isEmpty()) {
                    g2.setFont(gp.uiM.getDialogBox().pressStart2P.deriveFont(16f));
                    g2.setColor(Color.WHITE);
                    String[] lines = narrativeText.split("\n");
                    int y = gp.screenHeight / 2 - (lines.length * 30 / 2);
                    for (String line : lines) {
                        int textWidth = (int) g2.getFontMetrics().getStringBounds(line, g2).getWidth();
                        int x = gp.screenWidth / 2 - textWidth / 2;
                        g2.drawString(line, x, y);
                        y += 40;
                    }
                }
            } else {
                drawBlackBars(g2);
            }
        }
    }

    private void drawBlackBars(Graphics2D g2) {
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, gp.screenWidth, barHeight);
        g2.fillRect(0, gp.screenHeight - barHeight, gp.screenWidth, barHeight);
    }

    public void reset() {
        phase = 0;
        counter = 0;
        barHeight = 0;
        fullBlack = false;
    }

    public boolean isFinished() {
        return phase == 0 && gp.stateM.getCurrentState() != StateManager.gameState.CUTSCENE;
    }

    public boolean canSkip() {
        // Allow skipping after initial frames
        return counter > 30;
    }

    public void skip() {
        phase = 0;
        counter = 0;
        gp.stateM.setCurrentState(StateManager.gameState.PLAY);
    }
}
