package controller;

/**
 * Helper class untuk reset game state untuk New Game
 */
public class NewGameResetter {

    public static void resetToNewGame(GamePanel gp) {
        System.out.println("[NewGameResetter] Resetting game to new state...");

        // Reset player data
        gp.player.inventory.clear();
        gp.player.energy = gp.player.maxEnergy;
        gp.player.gold = 500;
        gp.player.score = 0;
        gp.player.getCollectedTrash().clear();
        gp.player.setTotalTrashInWorld(-1);

        // Reset chapter states
        gp.chapter1Active = true;
        gp.chapter2Active = false;
        gp.chapter2Finished = false;
        gp.chapter3Active = false;
        gp.tehDilaGiftGiven = false;
        gp.chapter1TrashCount = 0;
        gp.chapter2TrashCount = 0;
        gp.chapter3TrashCount = 0;

        // Reset time to day 1, 6 AM (360 minutes)
        TimeManager timeM = TimeManager.getInstance();
        timeM.setCurrentDay(1);
        timeM.setCurrentMinute(360); // 6 AM = 6 * 60 minutes

        // Reset achievements
        AchievementManager.getInstance(gp).reset();

        // Reset map to House Interior (starting map)
        gp.mapM.currMap = 5; // House interior
        gp.mapM.setupMap();

        // Spawn player at default location
        gp.player.setDefaultValues();

        // Clear/reset NPC states
        if (gp.npcM != null) {
            gp.npcM.resetAllNPCs();
        }

        System.out.println("[NewGameResetter] New game reset complete!");
        System.out.println("  - Inventory cleared");
        System.out.println("  - Energy: " + gp.player.energy + "/" + gp.player.maxEnergy);
        System.out.println("  - Gold: " + gp.player.gold);
        System.out.println("  - Chapter 1 Active: " + gp.chapter1Active);
        System.out.println("  - Time: Day " + timeM.getCurrentDay());
    }
}
