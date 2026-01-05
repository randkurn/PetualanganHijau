package controller;

import java.awt.Graphics2D;
import view.AudioManager;
import model.GameObject;
import model.Settings;
import model.Map;

public class MapManager {
    Settings settings;
    AudioManager audio;
    GamePanel gamePanel;
    public Map mapList[];
    private String[] mapPaths;
    public int currMap;

    public MapManager(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        this.settings = Settings.getInstance();
        this.audio = AudioManager.getInstance();

        mapPaths = new String[] {
                "/maps/house.tmx",
                "/maps/farm.tmx",
                "/maps/park.tmx",
                "/maps/city.tmx",
                "/maps/fishing.tmx",
                "/maps/house_interior.tmx"
        };
        mapList = new Map[mapPaths.length];
        currMap = 5;
        ensureMapLoaded(currMap);
    }

    private void ensureMapLoaded(int index) {
        if (index < 0 || index >= mapPaths.length)
            return;
        if (mapList[index] == null) {
            mapList[index] = new Map(gamePanel, mapPaths[index]);
        }
    }

    public void setupMap() {
        ensureMapLoaded(currMap);
        mapList[currMap].setObject();
    }

    public void resetMap() {
        mapList = new Map[mapPaths.length];
        currMap = 5;
        ensureMapLoaded(currMap);
        setupMap();
    }

    public void draw(Graphics2D g2) {
        ensureMapLoaded(currMap);
        mapList[currMap].drawTiles(g2);
        mapList[currMap].drawObjects(g2);
    }

    public void nextArea() {
        if (currMap < mapList.length - 1) {
            currMap++;
            changeArea();
        }
    }

    public void previousArea() {
        if (currMap > 0) {
            currMap--;
            changeArea();
        }
    }

    public void changeToArea(int areaIndex) {
        if (areaIndex >= 0 && areaIndex < mapList.length) {
            currMap = areaIndex;
            changeArea();
        }
    }

    private void changeArea() {
        changeArea(true);
    }

    private void changeArea(boolean respawnPlayer) {
        audio.playSound(2);

        int previousMapIndex = currMap;
        boolean wasInteriorMap = (previousMapIndex == 5);

        setupMap();
        gamePanel.npcM.setupChapter1NPCs();
        if (respawnPlayer)
            gamePanel.player.spawnPlayer();

        boolean isNowInterior = (currMap == 5);

        if (isNowInterior && !wasInteriorMap) {
            audio.stopMusic();
            audio.playHouseInteriorMusic();
            System.out.println("[MapManager] Entering interior - playing house music");
        } else if (!isNowInterior && wasInteriorMap) {
            audio.stopMusic();
            audio.playMainBGM();
            System.out.println("[MapManager] Leaving interior - playing outdoor BGM");
        }

        gamePanel.uiM.showMessage("Area: " + mapList[currMap].levelName);
    }

    public Map getMap() {
        ensureMapLoaded(currMap);
        return mapList[currMap];
    }

    public int[][] getTileMap() {
        ensureMapLoaded(currMap);
        return mapList[currMap].tileMap;
    }

    public GameObject getObject(int index) {
        ensureMapLoaded(currMap);
        return mapList[currMap].objects[index];
    }

    public int getTotalAreas() {
        return mapList.length;
    }

    public int getCurrentAreaIndex() {
        return currMap;
    }

    public void changeToAreaWithoutRespawn(int areaIndex) {
        if (areaIndex >= 0 && areaIndex < mapList.length) {
            currMap = areaIndex;
            changeArea(false);
        }
    }

    public String getAreaName(int index) {
        if (index >= 0 && index < mapList.length) {
            ensureMapLoaded(index);
            return mapList[index].levelName;
        }
        return "Unknown";
    }

    public boolean isInterior() {
        if (currMap < 0 || currMap >= mapList.length)
            return false;
        ensureMapLoaded(currMap);
        if (mapList[currMap] == null || mapList[currMap].levelName == null)
            return false;
        return mapList[currMap].levelName.toLowerCase().contains("interior");
    }

    public int countTotalTrashInAllMaps() {
        int total = 0;
        for (int i = 0; i < mapPaths.length; i++) {
            ensureMapLoaded(i);
            Map m = mapList[i];
            if (m == null)
                continue;

            for (int r = 0; r < m.maxWorldRow; r++) {
                for (int c = 0; c < m.maxWorldCol; c++) {
                    int gid = m.objMap[r][c];
                    if (gid != 0) {
                        int objIndex = gid;
                        if (m.isTmx) {
                            Map.TilesetInfo chosen = m.getTilesetForGid(gid);
                            if (chosen != null) {
                                objIndex = gid - chosen.firstgid + 1;
                            }
                        }
                        if (objIndex == 1) {
                            total++;
                        }
                    }
                }
            }

            if (m.tmxObjects != null) {
                for (Map.TmxObjectInfo tmxObj : m.tmxObjects) {
                    String type = tmxObj.type != null ? tmxObj.type : "";
                    String name = tmxObj.name != null ? tmxObj.name : "";
                    if ("Trash".equalsIgnoreCase(type) || type.toLowerCase().contains("trash")
                            || name.toLowerCase().contains("trash") || name.toLowerCase().contains("sampah")) {
                        total++;
                    }
                }
            }

            if (m.getTrashPoints() != null) {
                total += m.getTrashPoints().size();
            }
        }
        return total;
    }
}
