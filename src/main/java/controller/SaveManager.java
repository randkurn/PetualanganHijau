package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.io.IOException;
import model.SaveData;
import model.PlayerData;
import model.TimeData;

public class SaveManager {
    private static SaveManager instance;
    private final ObjectMapper mapper;
    private final String saveDirectory;
    private int currentSlot = 0;

    private SaveManager() {
        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.saveDirectory = System.getProperty("user.home") + File.separator + "PetualanganHijau";
        File dir = new File(saveDirectory);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    public static SaveManager getInstance() {
        if (instance == null) {
            instance = new SaveManager();
        }
        return instance;
    }

    private String getSlotFilePath(int slot) {
        return saveDirectory + File.separator + "save_slot_" + slot + ".json";
    }

    public void setCurrentSlot(int slot) {
        this.currentSlot = slot;
    }

    public int getCurrentSlot() {
        return currentSlot;
    }

    public void saveGame(SaveData data, int slot) {
        try {
            File saveFile = new File(getSlotFilePath(slot));
            mapper.writeValue(saveFile, data);
            System.out.println("Game saved successfully to slot " + slot);
        } catch (IOException e) {
            System.err.println("Failed to save game: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public SaveData loadGame(int slot) {
        try {
            File saveFile = new File(getSlotFilePath(slot));
            if (!saveFile.exists()) {
                System.out.println("No save file found in slot " + slot);
                return null;
            }
            SaveData data = mapper.readValue(saveFile, SaveData.class);
            System.out.println("Game loaded successfully from slot " + slot);
            return data;
        } catch (IOException e) {
            System.err.println("Failed to load game: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public boolean hasSaveFile(int slot) {
        File saveFile = new File(getSlotFilePath(slot));
        return saveFile.exists();
    }

    public void deleteSaveFile(int slot) {
        File saveFile = new File(getSlotFilePath(slot));
        if (saveFile.exists()) {
            saveFile.delete();
            System.out.println("Save file in slot " + slot + " deleted.");
        }
    }

    public SaveData getSavePreview(int slot) {
        return loadGame(slot);
    }

    public void saveGame(GamePanel gp) {
        saveGame(gp, currentSlot);
    }

    public void saveGame(GamePanel gp, int slot) {
        SaveData data = new SaveData();

        PlayerData playerData = new PlayerData();
        playerData.worldX = gp.player.worldX;
        playerData.worldY = gp.player.worldY;
        playerData.energy = gp.player.energy;
        playerData.gold = gp.player.gold;
        playerData.score = gp.player.score;
        playerData.inventoryItems = gp.player.inventory.getAllItems();
        playerData.playerName = gp.player.getPlayerName();

        playerData.chapter1Active = gp.chapter1Active;
        playerData.chapter2Active = gp.chapter2Active;
        playerData.chapter1TrashCount = gp.chapter1TrashCount;
        playerData.chapter2TrashCount = gp.chapter2TrashCount;
        playerData.totalTrashInWorld = gp.player.getTotalTrashInWorld();

        playerData.collectedTrash = gp.player.getCollectedTrash();

        TimeManager timeM = TimeManager.getInstance();
        TimeData timeData = new TimeData();
        timeData.currentDay = timeM.getCurrentDay();
        timeData.currentMinute = timeM.getCurrentMinute();

        data.player = playerData;
        data.time = timeData;
        data.currentAreaIndex = gp.mapM.getCurrentAreaIndex();

        saveGame(data, slot);
        System.out.println("[SaveManager] Saved - Chapter1: " + gp.chapter1Active + ", Chapter2: " + gp.chapter2Active
                + ", Trash collected: " + playerData.collectedTrash.size());
    }

    public void loadGame(GamePanel gp) {
        loadGame(gp, currentSlot);
    }

    public void loadGame(GamePanel gp, int slot) {
        SaveData data = loadGame(slot);
        if (data == null) {
            return;
        }

        // Pindah ke area yang tersimpan tanpa memaksa spawn ke titik awal
        if (data.currentAreaIndex >= 0) {
            gp.mapM.changeToAreaWithoutRespawn(data.currentAreaIndex);
        }

        if (data.player != null) {
            gp.player.worldX = data.player.worldX;
            gp.player.worldY = data.player.worldY;
            gp.player.energy = data.player.energy;
            gp.player.gold = data.player.gold;
            gp.player.score = data.player.score;
            if (data.player.playerName != null) {
                gp.player.setPlayerName(data.player.playerName);
            }

            if (data.player.inventoryItems != null) {
                gp.player.inventory.clear();
                data.player.inventoryItems.forEach((item, quantity) -> {
                    java.awt.image.BufferedImage icon = model.TrashRegistry.getIcon(item);
                    model.TrashType type = model.TrashRegistry.getType(item);

                    if (icon != null && type != null) {
                        gp.player.inventory.addItem(item, quantity, icon, type);
                        System.out.println("[SaveManager] Loaded trash: " + item + " (" + type + ")");
                    } else {
                        // Non-trash item (like tree seeds)
                        gp.player.inventory.addItem(item, quantity);
                        System.out.println("[SaveManager] Loaded item: " + item);
                    }
                });
            }

            gp.chapter1Active = data.player.chapter1Active;
            gp.chapter2Active = data.player.chapter2Active;
            gp.chapter1TrashCount = data.player.chapter1TrashCount;
            gp.chapter2TrashCount = data.player.chapter2TrashCount;
            gp.player.setTotalTrashInWorld(data.player.totalTrashInWorld);

            if (data.player.collectedTrash != null) {
                gp.player.setCollectedTrash(data.player.collectedTrash);
            }

            System.out.println("[SaveManager] Loaded - Chapter1: " + gp.chapter1Active + ", Chapter2: "
                    + gp.chapter2Active + ", Trash collected: " + gp.player.getCollectedTrash().size());
        }

        if (data.time != null) {
            TimeManager timeM = TimeManager.getInstance();
            timeM.setCurrentDay(data.time.currentDay);
            timeM.setCurrentMinute(data.time.currentMinute);
        }

        // Setelah load game, reload objects untuk pastikan trash yang sudah dikumpulkan
        if (gp.mapM != null && gp.mapM.getMap() != null) {
            gp.mapM.getMap().setObject();
            System.out.println("[SaveManager] Reloaded map objects to hide collected trash");
        }

        // Setelah load game, pastikan NPC Chapter 1 juga aktif di map sekarang
        if (gp.npcM != null) {
            gp.npcM.setupChapter1NPCs();
        }
    }

    public boolean hasSaveFile() {
        return hasSaveFile(0) || hasSaveFile(1) || hasSaveFile(2);
    }
}
