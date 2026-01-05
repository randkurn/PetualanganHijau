package controller;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import model.Mother;
import model.PakSaman;

public class NPCManager {
    private GamePanel gamePanel;
    private List<Mother> mothers;
    private List<PakSaman> pakSamans;
    private List<model.DanuSaputra> danus;
    private List<model.BuSuci> buSucis;
    private int currentMapIndex = -1;

    private java.util.Random random = new java.util.Random();

    public NPCManager(GamePanel gp) {
        this.gamePanel = gp;
        this.mothers = new ArrayList<>();
        this.pakSamans = new ArrayList<>();
        this.danus = new ArrayList<>();
        this.buSucis = new ArrayList<>();
    }

    public Mother createMother(int worldX, int worldY) {
        Mother mother = new Mother(gamePanel);
        mother.setPosition(worldX, worldY);
        mothers.add(mother);
        return mother;
    }

    public PakSaman createPakSaman(int worldX, int worldY) {
        PakSaman saman = new PakSaman(gamePanel);
        saman.setPosition(worldX, worldY);
        pakSamans.add(saman);
        return saman;
    }

    public model.DanuSaputra createDanuSaputra(int worldX, int worldY) {
        model.DanuSaputra danu = new model.DanuSaputra(gamePanel);
        danu.setPosition(worldX, worldY);
        danus.add(danu);
        return danu;
    }

    public model.BuSuci createBuSuci(int worldX, int worldY) {
        model.BuSuci suci = new model.BuSuci(gamePanel);
        suci.setPosition(worldX, worldY);
        buSucis.add(suci);
        return suci;
    }

    public void clearAll() {
        mothers.clear();
        pakSamans.clear();
        danus.clear();
        buSucis.clear();
    }

    public void update() {
        for (Mother mother : mothers) {
            mother.update();
            randomWander(mother);
        }
        for (PakSaman saman : pakSamans) {
            saman.update();
        }
        for (model.DanuSaputra danu : danus) {
            danu.update();
        }
        for (model.BuSuci suci : buSucis) {
            suci.update();
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
            drawDebugNPC(g2, mother, Color.RED);
        }
        for (PakSaman saman : pakSamans) {
            saman.draw(g2);
            drawDebugNPC(g2, saman, Color.GREEN);
        }
        for (model.DanuSaputra danu : danus) {
            danu.draw(g2);
        }
        for (model.BuSuci suci : buSucis) {
            suci.draw(g2);
        }
    }

    private void drawDebugNPC(Graphics2D g2, model.Entity npc, Color color) {
        int screenX = npc.worldX - gamePanel.player.worldX + gamePanel.player.screenX;
        int screenY = npc.worldY - gamePanel.player.worldY + gamePanel.player.screenY;

        // Always draw a small colored square for debugging
        g2.setColor(color);
        g2.fillRect(screenX + 10, screenY + 10, 10, 10);

        g2.setColor(Color.WHITE);
        g2.drawString(npc.getClass().getSimpleName(), screenX, screenY + 40);
    }

    public boolean tryInteractWithNearbyNPC(int interactDistance) {
        for (Mother mother : mothers) {
            if (mother.canInteract(interactDistance)) {
                mother.interact();
                return true;
            }
        }
        for (PakSaman saman : pakSamans) {
            if (saman.canInteract(interactDistance)) {
                saman.interact();
                return true;
            }
        }
        for (model.DanuSaputra danu : danus) {
            if (danu.canInteract(interactDistance)) {
                danu.interact();
                return true;
            }
        }
        for (model.BuSuci suci : buSucis) {
            if (suci.canInteract(interactDistance)) {
                suci.interact();
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

        if (gamePanel.mapM.currMap == 5) {
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

            int worldX = obj.x;
            int worldY = obj.y;

            String name = obj.name != null ? obj.name.toLowerCase() : "";
            boolean isNPC = "NPC".equalsIgnoreCase(obj.type) || "Decoration".equalsIgnoreCase(obj.type)
                    || obj.type == null;

            if (isNPC) {
                if (name.contains("ibu") || name.contains("mother")) {
                    System.out.println("[NPCManager] Mother spawn skipped (handled by CutsceneManager)");
                } else if (name.contains("pak") || name.contains("saman")) {
                    if (gamePanel.chapter2Active) {
                        System.out.println("[NPCManager] Spawning Pak Saman at (" + worldX + "," + worldY + ")");
                        PakSaman s = createPakSaman(worldX, worldY);
                        s.direction = "down";
                    } else {
                        System.out.println("[NPCManager] Pak Saman spawn skipped (Chapter 2 only)");
                    }
                } else if (name.contains("danu")) {
                    if (gamePanel.chapter2Active) {
                        System.out.println("[NPCManager] Spawning Danu Saputra at (" + worldX + "," + worldY + ")");
                        createDanuSaputra(worldX, worldY);
                    } else {
                        System.out.println("[NPCManager] Danu Saputra spawn skipped (Chapter 2 only)");
                    }
                } else if (name.contains("suci") || name.contains("merchant")) {
                    System.out.println("[NPCManager] Bu Suci spawn skipped (spawns after trash exchange)");
                } else if (name.contains("bank") || name.contains("waste") || name.contains("sampah")) {
                    System.out.println("[NPCManager] Bank Sampah spawn disabled (Chapter 2 now uses Danu)");
                }
            }
        }
    }

    public List<Mother> getMothers() {
        return mothers;
    }

    public PakSaman getPakSaman() {
        return pakSamans.isEmpty() ? null : pakSamans.get(0);
    }

    public Mother getMother() {
        return mothers.isEmpty() ? null : mothers.get(0);
    }

    public model.DanuSaputra getDanuSaputra() {
        return danus.isEmpty() ? null : danus.get(0);
    }

    public model.BuSuci getBuSuci() {
        return buSucis.isEmpty() ? null : buSucis.get(0);
    }

    public int getNPCCount() {
        return mothers.size() + pakSamans.size() + danus.size() + buSucis.size();
    }

    public void spawnChapter2NPCs() {
        if (danus.isEmpty()) {
            int danuX = 54 * gamePanel.tileSize;
            int danuY = 32 * gamePanel.tileSize;
            System.out.println("[NPCManager] Spawning Danu Saputra at tile (54, 32) = (" + danuX + "," + danuY + ")");
            model.DanuSaputra danu = createDanuSaputra(danuX, danuY);
            danu.direction = "left";
        }
    }

    public void spawnBuSuci(int worldX, int worldY) {
        if (buSucis.isEmpty()) {
            System.out.println("[NPCManager] Spawning Bu Suci at (" + worldX + "," + worldY + ")");
            createBuSuci(worldX, worldY);
        }
    }
}
