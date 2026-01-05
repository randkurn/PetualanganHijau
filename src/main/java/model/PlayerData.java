package model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerData {
    public int worldX;
    public int worldY;
    public String direction;
    public int energy;
    public int maxEnergy;
    public int gold;
    public int score;
    public Map<String, Integer> inventoryItems;
    public String playerName;

    public boolean chapter1Active;
    public boolean chapter2Active;
    public int chapter1TrashCount;
    public int chapter2TrashCount;
    public int totalTrashInWorld;

    public Set<String> collectedTrash;

    public PlayerData() {
        this.worldX = 0;
        this.worldY = 0;
        this.direction = "down";
        this.energy = 100;
        this.maxEnergy = 100;
        this.gold = 500;
        this.score = 0;
        this.inventoryItems = new HashMap<>();
        this.playerName = "Player";
        this.chapter1Active = false;
        this.chapter2Active = false;
        this.chapter1TrashCount = 0;
        this.chapter2TrashCount = 0;
        this.collectedTrash = new HashSet<>();
    }
}
