package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mengelola data save (hingga 3 slot), termasuk nama karakter dan posisi pemain.
 */
public final class GameStateManager {

    private static final double DEFAULT_X = 64 * 15;
    private static final double DEFAULT_Y = 64 * 14;
    private static final String DEFAULT_NAME = "Penjelajah";
    public static final int SLOT_COUNT = 3;

    private static int pendingSlot = 1;
    private static int activeSlot = 1;
    private static GameState pendingState =
            new GameState(pendingSlot, DEFAULT_X, DEFAULT_Y, DEFAULT_NAME);

    private GameStateManager() {}

    public static void prepareNewGame(int slot, String name) {
        pendingSlot = clampSlot(slot);
        pendingState = new GameState(pendingSlot, DEFAULT_X, DEFAULT_Y, name.trim().isEmpty() ? DEFAULT_NAME : name);
    }

    public static boolean prepareLoad(int slot) {
        slot = clampSlot(slot);
        Map<String, String> data = DataManager.loadCSV(fileForSlot(slot));
        if (data.isEmpty()) {
            return false;
        }
        double x = parseDouble(data.getOrDefault("x", String.valueOf(DEFAULT_X)), DEFAULT_X);
        double y = parseDouble(data.getOrDefault("y", String.valueOf(DEFAULT_Y)), DEFAULT_Y);
        String name = data.getOrDefault("name", DEFAULT_NAME);
        pendingSlot = slot;
        pendingState = new GameState(slot, x, y, name);
        return true;
    }

    public static GameState consumeState() {
        if (pendingState == null) {
            prepareNewGame(1, DEFAULT_NAME);
        }
        activeSlot = pendingSlot;
        GameState state = pendingState;
        pendingState = null;
        return state;
    }

    public static void saveActiveSlot(double x, double y, String name) {
        saveToSlot(activeSlot, x, y, name);
    }

    public static void saveToSlot(int slot, double x, double y, String name) {
        slot = clampSlot(slot);
        Map<String, String> data = new HashMap<>();
        data.put("x", String.valueOf(x));
        data.put("y", String.valueOf(y));
        data.put("name", name == null || name.isBlank() ? DEFAULT_NAME : name);
        data.put("timestamp", String.valueOf(System.currentTimeMillis()));
        DataManager.saveCSV(fileForSlot(slot), data);
    }

    public static List<SlotInfo> getAllSlotInfo() {
        List<SlotInfo> list = new ArrayList<>();
        for (int slot = 1; slot <= SLOT_COUNT; slot++) {
            Map<String, String> data = DataManager.loadCSV(fileForSlot(slot));
            boolean occupied = !data.isEmpty();
            String name = occupied ? data.getOrDefault("name", DEFAULT_NAME) : "Kosong";
            long timestamp = occupied ? parseLong(data.getOrDefault("timestamp", "0"), 0) : 0;
            list.add(new SlotInfo(slot, occupied, name, timestamp));
        }
        return list;
    }

    public static boolean hasAnySave() {
        return getAllSlotInfo().stream().anyMatch(SlotInfo::occupied);
    }

    public static int getActiveSlot() {
        return activeSlot;
    }

    private static String fileForSlot(int slot) {
        return "player_slot_" + slot + ".csv";
    }

    private static int clampSlot(int slot) {
        if (slot < 1) return 1;
        if (slot > SLOT_COUNT) return SLOT_COUNT;
        return slot;
    }

    private static double parseDouble(String value, double fallback) {
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            return fallback;
        }
    }

    private static long parseLong(String value, long fallback) {
        try {
            return Long.parseLong(value);
        } catch (Exception e) {
            return fallback;
        }
    }

    public record GameState(int slot, double playerX, double playerY, String playerName) {}

    public record SlotInfo(int slot, boolean occupied, String playerName, long timestamp) {}
}