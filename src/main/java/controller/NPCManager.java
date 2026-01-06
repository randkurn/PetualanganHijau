package controller;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import model.Mother;
import model.PakBahlil;
import model.TehDila;
import model.Panjul;
import model.Bisma;
import model.Randy;
import model.PakKhairul;
import model.NengJia;

public class NPCManager {
    private GamePanel gamePanel;
    private List<Mother> mothers;
    private List<PakBahlil> pakBahlils;
    private List<Panjul> panjuls;
    private List<TehDila> tehDilas;

    // Chapter 3 NPCs
    private List<Bisma> bismas;
    private List<Randy> randys;
    private List<PakKhairul> pakKhairuls;
    private List<NengJia> nengJias;

    private int currentMapIndex = -1;

    private java.util.Random random = new java.util.Random();

    public NPCManager(GamePanel gp) {
        this.gamePanel = gp;
        this.mothers = new ArrayList<>();
        this.pakBahlils = new ArrayList<>();
        this.panjuls = new ArrayList<>();
        this.tehDilas = new ArrayList<>();
        this.bismas = new ArrayList<>();
        this.randys = new ArrayList<>();
        this.pakKhairuls = new ArrayList<>();
        this.nengJias = new ArrayList<>();
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

    // Chapter 3 NPC creation methods
    public Bisma createBisma(int worldX, int worldY) {
        Bisma bisma = new Bisma(gamePanel);
        bisma.setPosition(worldX, worldY);
        bismas.add(bisma);
        return bisma;
    }

    public Randy createRandy(int worldX, int worldY) {
        Randy randy = new Randy(gamePanel);
        randy.setPosition(worldX, worldY);
        randys.add(randy);
        return randy;
    }

    private model.PakKhairul createPakKhairul(int worldX, int worldY) {
        model.PakKhairul khairul = new model.PakKhairul(gamePanel);
        khairul.setPosition(worldX, worldY);
        pakKhairuls.add(khairul);
        System.out.println("[NPCManager] Created Pak Khairul at (" + worldX + "," + worldY + ")");
        return khairul;
    }

    public NengJia createNengJia(int worldX, int worldY) {
        NengJia jia = new NengJia(gamePanel);
        jia.setPosition(worldX, worldY);
        nengJias.add(jia);
        return jia;
    }

    public void clearAll() {
        mothers.clear();
        pakBahlils.clear();
        panjuls.clear();
        tehDilas.clear();
        bismas.clear();
        randys.clear();
        pakKhairuls.clear();
        nengJias.clear();
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
        for (Bisma bisma : bismas) {
            bisma.update();
        }
        for (Randy randy : randys) {
            randy.update();
        }
        for (PakKhairul khairul : pakKhairuls) {
            khairul.update();
        }
        for (NengJia jia : nengJias) {
            jia.update();
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
        for (Bisma bisma : bismas) {
            bisma.draw(g2);
        }
        for (Randy randy : randys) {
            randy.draw(g2);
        }
        for (PakKhairul khairul : pakKhairuls) {
            khairul.draw(g2);
        }
        for (NengJia jia : nengJias) {
            jia.draw(g2);
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
        for (Bisma bisma : bismas) {
            if (bisma.canInteract(interactDistance)) {
                bisma.interact();
                return true;
            }
        }
        for (Randy randy : randys) {
            if (randy.canInteract(interactDistance)) {
                randy.interact();
                return true;
            }
        }
        for (PakKhairul khairul : pakKhairuls) {
            if (khairul.canInteract(interactDistance)) {
                khairul.interact();
                return true;
            }
        }
        for (NengJia jia : nengJias) {
            if (jia.canInteract(interactDistance)) {
                jia.interact();
                return true;
            }
        }
        return false;
    }

    public void setupChapter1NPCs() {
        int currMap = gamePanel.mapM.currMap;

        System.out.println("[NPCManager] setupChapter1NPCs called - currMap: " + currMap);

        if (currMap == currentMapIndex) {
            return;
        }

        reloadNPCs();
    }

    public void reloadNPCs() {
        int currMap = gamePanel.mapM.currMap;
        System.out.println("[NPCManager] Force reloading NPCs for map: " + currMap);

        clearAll();
        currentMapIndex = currMap;

        loadNPCsFromTMX();

        // Re-spawn Chapter 2/3 NPCs if active on correct maps
        // Re-spawn Chapter 2/3 NPCs if active on House map (exterior)
        if (gamePanel.chapter2Active || gamePanel.chapter2Finished || gamePanel.chapter3Active) {
            if (currMap == 0) {
                spawnChapter2NPCs(); // Panjul
                spawnTehDila(26 * gamePanel.tileSize, 58 * gamePanel.tileSize); // Teh Dila
            }
        }

        // Spawn Chapter 3 NPCs if active
        if (gamePanel.chapter3Active) {
            spawnChapter3NPCs();
        }

        System.out.println("[NPCManager] NPC setup complete. NPCCount: " + getNPCCount());
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
                    || obj.type == null || "".equals(obj.type);

            if (isNPC) {
                if (name.contains("ibu") || name.contains("mother")) {
                    if (!gamePanel.chapter1Active && !gamePanel.chapter2Active && !gamePanel.chapter2Finished) {
                        System.out.println("[NPCManager] Mother spawn skipped (handled by CutsceneManager)");
                    } else if (mothers.isEmpty() && gamePanel.mapM.currMap == 0) {
                        createMother(obj.x, obj.y);
                    }
                } else if (name.contains("pak") || name.contains("saman") || name.contains("bahlil")) {
                    // Pak Bahlil should only be skipped IF Chapter 1 is still early or specialized
                    // cutscene is running, OR if it's Chapter 3 (user: pak bahlil gausah muncul
                    // gapapa)
                    if ((gamePanel.csM.getPhase() < 13 && gamePanel.chapter1Active) || gamePanel.chapter3Active) {
                        System.out.println("[NPCManager] Pak Bahlil spawn skipped (Cutscene or Ch3)");
                    } else if (gamePanel.mapM.currMap == 0) {
                        // Spawn him manually at his house spot if map 0 and not already there
                        if (pakBahlils.isEmpty()) {
                            createPakBahlil(obj.x, obj.y);
                        }
                    }
                } else if (name.contains("danu") || name.contains("panjul")) {
                    // Panjul shows up in City or specific spots after Chapter 1
                    if (gamePanel.csM.getPhase() < 33 && !gamePanel.chapter2Active && !gamePanel.chapter2Finished) {
                        System.out.println("[NPCManager] Panjul spawn skipped (handled by Cutscene/Story)");
                    } else if (panjuls.isEmpty()) {
                        createPanjul(obj.x, obj.y);
                    }
                } else if (name.contains("bisma")) {
                    if (gamePanel.chapter3Active && bismas.isEmpty()) {
                        createBisma(obj.x, obj.y);
                    }
                } else if (name.contains("jia") || name.contains("nengjia")) {
                    if (gamePanel.chapter3Active && nengJias.isEmpty()) {
                        createNengJia(obj.x, obj.y);
                    }
                } else if (name.contains("randy")) {
                    if (gamePanel.chapter3Active && randys.isEmpty()) {
                        createRandy(obj.x, obj.y);
                    }
                } else if (name.contains("khairul") || name.contains("pak khairul")) {
                    if (gamePanel.chapter3Active && pakKhairuls.isEmpty()) {
                        createPakKhairul(obj.x, obj.y);
                    }
                } else if (name.contains("suci") || name.contains("dila") || name.contains("tehdila")
                        || name.contains("merchant")) {
                    // Teh Dila shows up in City or specific spots
                    if (gamePanel.csM.getPhase() < 37 && !gamePanel.chapter2Active && !gamePanel.chapter2Finished
                            && !gamePanel.chapter3Active) {
                        System.out.println("[NPCManager] Teh Dila spawn skipped (handled by Cutscene/Story)");
                    } else if (tehDilas.isEmpty()) {
                        createTehDila(obj.x, obj.y);
                    }
                } else if (name.contains("bank") || name.contains("waste") || name.contains("sampah")) {
                    System.out.println("[NPCManager] Bank Sampah spawn disabled (Chapter 2 now uses Panjul)");
                }
            }

        }
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
        return mothers.size() + pakBahlils.size() + panjuls.size() + tehDilas.size() +
                bismas.size() + randys.size() + pakKhairuls.size() + nengJias.size();
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

    public void spawnChapter3NPCs() {
        // Now handled mostly by loadNPCsFromTMX, but fallbacks can be added here if
        // needed
    }

    // Getter methods for Chapter 3 NPCs
    public Bisma getBisma() {
        return bismas.isEmpty() ? null : bismas.get(0);
    }

    public Randy getRandy() {
        return randys.isEmpty() ? null : randys.get(0);
    }

    public PakKhairul getPakKhairul() {
        return pakKhairuls.isEmpty() ? null : pakKhairuls.get(0);
    }

    public NengJia getNengJia() {
        return nengJias.isEmpty() ? null : nengJias.get(0);
    }

    public List<Mother> getMothers() {
        return mothers;
    }

    public List<PakBahlil> getPakBahlils() {
        return pakBahlils;
    }

    public List<Panjul> getPanjuls() {
        return panjuls;
    }

    public List<TehDila> getTehDilas() {
        return tehDilas;
    }

    public List<Bisma> getBismas() {
        return bismas;
    }

    public List<Randy> getRandys() {
        return randys;
    }

    public List<PakKhairul> getPakKhairuls() {
        return pakKhairuls;
    }

    public List<NengJia> getNengJias() {
        return nengJias;
    }
}
