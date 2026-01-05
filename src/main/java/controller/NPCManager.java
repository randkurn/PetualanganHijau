package controller;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import model.Mother;
import model.PakBahlil;
import model.TehDila;
import model.Panjul;

public class NPCManager {
    private GamePanel gamePanel;
    private List<Mother> mothers;
    private List<PakBahlil> pakBahlils;
    private List<Panjul> panjuls;
    private List<TehDila> tehDilas;
    private int currentMapIndex = -1;

    private java.util.Random random = new java.util.Random();

    public NPCManager(GamePanel gp) {
        this.gamePanel = gp;
        this.mothers = new ArrayList<>();
        this.pakBahlils = new ArrayList<>();
        this.panjuls = new ArrayList<>();
        this.tehDilas = new ArrayList<>();
    }

    public Mother createMother(int worldX, int worldY) {
        Mother mother = new Mother(gamePanel);
        mother.setPosition(worldX, worldY);
        mothers.add(mother);
        return mother;
    }

    public PakBahlil createPakBahlil(int worldX, int worldY) {
        PakBahlil bahlil = new PakBahlil(gamePanel);
        bahlil.setPosition(worldX, worldY);
        pakBahlils.add(bahlil);
        return bahlil;
    }

    public Panjul createPanjul(int worldX, int worldY) {
        Panjul panjul = new Panjul(gamePanel);
        panjul.setPosition(worldX, worldY);
        panjuls.add(panjul);
        return panjul;
    }

    public TehDila createTehDila(int worldX, int worldY) {
        TehDila dila = new TehDila(gamePanel);
        dila.setPosition(worldX, worldY);
        tehDilas.add(dila);
        return dila;
    }

    public void clearAll() {
        mothers.clear();
        pakBahlils.clear();
        panjuls.clear();
        tehDilas.clear();
    }

    public void resetAllNPCs() {
        clearAll();
        currentMapIndex = -1; // Reset map tracking
    }

    public void update() {
        for (Mother mother : mothers) {
            mother.update();
            randomWander(mother);
        }
        for (PakBahlil bahlil : pakBahlils) {
            bahlil.update();
        }
        for (Panjul panjul : panjuls) {
            panjul.update();
        }
        for (TehDila dila : tehDilas) {
            dila.update();
        }
    }

    private void randomWander(model.Entity npc) {
        if (gamePanel.stateM.getCurrentState() == StateManager.gameState.CUTSCENE) {
            return;
        }

        if (npc.speed <= 0)
            return;

        // NPCs randomly change direction every ~3 seconds
        if (random.nextInt(180) == 0) {
            String[] directions = { "up", "down", "left", "right" };
            npc.direction = directions[random.nextInt(4)];
        }

        npc.collisionOn = false;
        gamePanel.checker.checkTileCollision(npc);
        gamePanel.checker.checkObjectCollision(npc, false);
        gamePanel.checker.checkPlayerCollision(npc);

        if (!npc.collisionOn) {
            if (random.nextInt(10) < 3) {
                switch (npc.direction) {
                    case "up" -> npc.worldY -= npc.speed;
                    case "down" -> npc.worldY += npc.speed;
                    case "left" -> npc.worldX -= npc.speed;
                    case "right" -> npc.worldX += npc.speed;
                }
            }
        } else {
            // If hit collision, change direction immediately
            String[] directions = { "up", "down", "left", "right" };
            npc.direction = directions[random.nextInt(4)];
        }
    }

    public void draw(Graphics2D g2) {
        for (Mother mother : mothers) {
            mother.draw(g2);
        }
        for (PakBahlil bahlil : pakBahlils) {
            bahlil.draw(g2);
        }
        for (Panjul panjul : panjuls) {
            panjul.draw(g2);
        }
        for (TehDila dila : tehDilas) {
            dila.draw(g2);
        }
    }

    public boolean tryInteractWithNearbyNPC(int interactDistance) {
        for (Mother mother : mothers) {
            if (mother.canInteract(interactDistance)) {
                mother.interact();
                return true;
            }
        }
        for (PakBahlil bahlil : pakBahlils) {
            if (bahlil.canInteract(interactDistance)) {
                bahlil.interact();
                return true;
            }
        }
        for (Panjul panjul : panjuls) {
            if (panjul.canInteract(interactDistance)) {
                panjul.interact();
                return true;
            }
        }
        for (TehDila dila : tehDilas) {
            if (dila.canInteract(interactDistance)) {
                dila.interact();
                return true;
            }
        }
        return false;
    }

    public void setupChapter1NPCs() {
        int currMap = gamePanel.mapM.currMap;

        System.out.println("[NPCManager] setupChapter1NPCs called - currMap: " + currMap);

        if (currMap == currentMapIndex && !mothers.isEmpty()) {
            return;
        }

        clearAll();
        currentMapIndex = currMap;

        loadNPCsFromTMX();

        // Re-spawn Chapter 2/3 NPCs if active on correct maps
        if (gamePanel.chapter2Active || gamePanel.chapter2Finished || gamePanel.chapter3Active) {
            if (gamePanel.mapM.currMap == 3) { // CITY map
                spawnChapter2NPCs(); // Panjul at (52, 33)
                spawnTehDila(26 * gamePanel.tileSize, 58 * gamePanel.tileSize); // Teh Dila
            }
        }

        // Re-spawn Mother if in House map
        if (gamePanel.mapM.currMap == 0) {
            if (mothers.isEmpty()) {
                createMother(5 * gamePanel.tileSize, 9 * gamePanel.tileSize);
            }
        }

        System.out.println("[NPCManager] Chapter 1 NPC setup complete. NPCs spawned: " + getNPCCount());
    }

    private void loadNPCsFromTMX() {
        model.Map currentMap = gamePanel.mapM.getMap();
        if (currentMap == null || !currentMap.isTmx) {
            return;
        }

        List<model.Map.TmxObjectInfo> tmxObjects = currentMap.tmxObjects;
        if (tmxObjects == null) {
            return;
        }

        for (model.Map.TmxObjectInfo obj : tmxObjects) {
            if (obj.type == null) {
                continue;
            }

            String name = obj.name != null ? obj.name.toLowerCase() : "";
            boolean isNPC = "NPC".equalsIgnoreCase(obj.type) || "Decoration".equalsIgnoreCase(obj.type)
                    || obj.type == null;

            if (isNPC) {
                if (name.contains("ibu") || name.contains("mother")) {
                    if (!gamePanel.chapter1Active && !gamePanel.chapter2Active && !gamePanel.chapter2Finished) {
                        System.out.println("[NPCManager] Mother spawn skipped (handled by CutsceneManager)");
                    }
                } else if (name.contains("pak") || name.contains("saman") || name.contains("bahlil")) {
                    // Pak Bahlil should only be skipped IF Chapter 1 is still early or specialized
                    // cutscene is running, OR if it's Chapter 3 (user: pak bahlil gausah muncul
                    // gapapa)
                    if ((gamePanel.csM.getPhase() < 13 && gamePanel.chapter1Active) || gamePanel.chapter3Active) {
                        System.out.println("[NPCManager] Pak Bahlil spawn skipped (Cutscene or Ch3)");
                    } else if (gamePanel.mapM.currMap == 0) {
                        // Spawn him manually at his house spot if map 0 and not already there
                        if (gamePanel.npcM.getPakBahlil() == null) {
                            gamePanel.npcM.createPakBahlil(obj.x, obj.y);
                        }
                    }
                } else if (name.contains("danu") || name.contains("panjul")) {
                    // Panjul shows up in City or specific spots after Chapter 1
                    if (gamePanel.csM.getPhase() < 33 && !gamePanel.chapter2Active && !gamePanel.chapter2Finished) {
                        System.out.println("[NPCManager] Panjul spawn skipped (handled by Cutscene/Story)");
                    }
                } else if (name.contains("suci") || name.contains("dila") || name.contains("merchant")) {
                    // Teh Dila shows up in City or specific spots
                    if (gamePanel.csM.getPhase() < 37 && !gamePanel.chapter2Active && !gamePanel.chapter2Finished) {
                        System.out.println("[NPCManager] Teh Dila spawn skipped (handled by Cutscene/Story)");
                    }
                } else if (name.contains("bank") || name.contains("waste") || name.contains("sampah")) {
                    System.out.println("[NPCManager] Bank Sampah spawn disabled (Chapter 2 now uses Panjul)");
                }
            }
        }
    }

    public List<Mother> getMothers() {
        return mothers;
    }

    public PakBahlil getPakBahlil() {
        return pakBahlils.isEmpty() ? null : pakBahlils.get(0);
    }

    public Mother getMother() {
        return mothers.isEmpty() ? null : mothers.get(0);
    }

    public Panjul getPanjul() {
        return panjuls.isEmpty() ? null : panjuls.get(0);
    }

    public TehDila getTehDila() {
        return tehDilas.isEmpty() ? null : tehDilas.get(0);
    }

    public int getNPCCount() {
        return mothers.size() + pakBahlils.size() + panjuls.size() + tehDilas.size();
    }

    public void spawnChapter2NPCs() {
        if (panjuls.isEmpty()) {
            int panjulX = 52 * gamePanel.tileSize;
            int panjulY = 33 * gamePanel.tileSize;
            System.out.println("[NPCManager] Spawning Panjul at tile (52, 33) in City");
            Panjul panjul = createPanjul(panjulX, panjulY);
            panjul.direction = "down";
        }
    }

    public void spawnTehDila(int worldX, int worldY) {
        if (tehDilas.isEmpty()) {
            System.out.println("[NPCManager] Spawning Teh Dila at (" + worldX + "," + worldY + ")");
            createTehDila(worldX, worldY);
        }
    }
}
