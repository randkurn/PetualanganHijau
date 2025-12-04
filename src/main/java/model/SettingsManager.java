package model;

import java.util.HashMap;
import java.util.Map;

/**
 * Menyimpan pengaturan sederhana seperti volume/master mute.
 */
public class SettingsManager {

    private static final String SETTINGS_FILE = "settings.csv";

    private static double volume = 0.8; // 0..1
    private static boolean muted = false;

    private SettingsManager() {}

    public static void load() {
        Map<String, String> data = DataManager.loadCSV(SETTINGS_FILE);
        if (data.isEmpty()) {
            return;
        }
        volume = clamp(parseDouble(data.getOrDefault("volume", "0.8"), 0.8));
        muted = Boolean.parseBoolean(data.getOrDefault("muted", "false"));
    }

    public static void save() {
        Map<String, String> data = new HashMap<>();
        data.put("volume", String.valueOf(volume));
        data.put("muted", String.valueOf(muted));
        DataManager.saveCSV(SETTINGS_FILE, data);
    }

    public static double getVolume() {
        return muted ? 0.0 : volume;
    }

    public static double getRawVolume() {
        return volume;
    }

    public static void setVolume(double value) {
        volume = clamp(value);
        save();
    }

    public static boolean isMuted() {
        return muted;
    }

    public static void setMuted(boolean value) {
        muted = value;
        save();
    }

    private static double clamp(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }

    private static double parseDouble(String value, double fallback) {
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            return fallback;
        }
    }
}

