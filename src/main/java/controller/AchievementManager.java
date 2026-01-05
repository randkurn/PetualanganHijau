package controller;

import java.util.HashSet;
import java.util.Set;

public class AchievementManager {
    private static AchievementManager instance;
    private GamePanel gp;
    private Set<String> unlockedAchievements = new HashSet<>();

    private AchievementManager(GamePanel gp) {
        this.gp = gp;
    }

    public static AchievementManager getInstance(GamePanel gp) {
        if (instance == null) {
            instance = new AchievementManager(gp);
        }
        return instance;
    }

    public void unlockAchievement(String name, String description) {
        if (!unlockedAchievements.contains(name)) {
            unlockedAchievements.add(name);
            System.out.println("[Achievement] Unlocked: " + name);

            // Show popup in game UI
            if (gp.uiM != null) {
                gp.uiM.showMessage("ACHIEVEMENT UNLOCKED:\n" + name.toUpperCase() + "\n(" + description + ")");
            }

            // Play a sound if possible (sound 5 is often used for success/confirm)
            view.AudioManager.getInstance().playSound(5);
        }
    }

    public boolean isUnlocked(String name) {
        return unlockedAchievements.contains(name);
    }

    public Set<String> getUnlockedAchievements() {
        return unlockedAchievements;
    }

    public void setUnlockedAchievements(Set<String> unlocked) {
        this.unlockedAchievements = unlocked;
    }

    public void reset() {
        this.unlockedAchievements.clear();
    }
}
