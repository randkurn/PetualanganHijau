package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SaveData {
    public PlayerData player;
    public TimeData time;
    public int currentAreaIndex;

    public SaveData() {
        this.player = new PlayerData();
        this.time = new TimeData();
        this.currentAreaIndex = 0;
    }
}
