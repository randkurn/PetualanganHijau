package model;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.awt.Rectangle;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import controller.GamePanel;

import view.TileManager;

public class Map {
    GamePanel gp;
    TileManager tileM;
    Random randGen = new Random();

    public int[][] tileMap, objMap;
    private boolean[][] collisionMap;
    public GameObject objects[];

    public String levelName;
    public int maxWorldCol, maxWorldRow, playerStartX, playerStartY, objectNum, entityNum, keyNum;
    public int gateIndex;

    boolean drawPath = false;
    boolean hitboxTest = false;
    boolean showCoords = false;

    public List<TmxObjectInfo> tmxObjects = new ArrayList<>();
    private List<TilesetInfo> tilesetInfos = new ArrayList<>();
    public boolean isTmx = false;

    public static class TmxObjectInfo {
        public String name;
        public String type;
        public int x, y, width, height;
        public int targetArea = -1;
    }

    public static class TilesetInfo {
        public int firstgid;
        public String name;
        public int tileWidth, tileHeight;
        public int tileCount, columns;
        public BufferedImage image;
        public java.util.Map<Integer, BufferedImage> tileImages = new java.util.HashMap<>();
    }

    private static class TmxLayerInfo {
        int[][] data;
        boolean visible;
    }

    private List<TmxLayerInfo> tileLayers = new ArrayList<>();
    private List<java.awt.Point> trashPoints = new ArrayList<>();
    private List<java.awt.Point> plantingPoints = new ArrayList<>();

    public Map(GamePanel gp, String mapFile) {
        this.gp = gp;
        tileM = TileManager.getInstance();

        loadMap(mapFile);
    }

    private void loadMap(String mapFile) {
        trashPoints.clear();
        try {
            // Prefer TMX (Tiled) map if available, fallback ke format .txt lama
            String tmxPath = null;

            if (mapFile != null) {
                if (mapFile.endsWith(".tmx")) {
                    tmxPath = mapFile;
                } else if (mapFile.endsWith(".txt")) {
                    tmxPath = mapFile.substring(0, mapFile.length() - 4) + ".tmx";
                }
            }

            if (tmxPath != null) {
                InputStream tmxStream = getClass().getResourceAsStream(tmxPath);
                if (tmxStream != null) {
                    System.out.println("[Map] Loading TMX map: " + tmxPath);
                    loadTmxMap(tmxStream, tmxPath);
                    return;
                }
            }

            InputStream input = getClass().getResourceAsStream(mapFile);
            if (input == null) {
                throw new IOException("Map file not found: " + mapFile);
            }
            System.out.println("[Map] Loading TXT map: " + mapFile);
            loadTxtMap(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadTxtMap(InputStream input) throws IOException {

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            String firstLine = reader.readLine();
            String settings[] = firstLine.split(",");

            // save level data (trim setiap nilai untuk mencegah NumberFormatException
            levelName = settings[0].trim();
            maxWorldRow = Integer.parseInt(settings[1].trim());
            maxWorldCol = Integer.parseInt(settings[2].trim());
            playerStartX = Integer.parseInt(settings[3].trim()) * gp.tileSize;
            playerStartY = Integer.parseInt(settings[4].trim()) * gp.tileSize;
            objectNum = Integer.parseInt(settings[5].trim());
            entityNum = Integer.parseInt(settings[6].trim());
            keyNum = Integer.parseInt(settings[7].trim());

            tileMap = new int[maxWorldRow][maxWorldCol];
            collisionMap = new boolean[maxWorldRow][maxWorldCol];
            readArray(reader, tileMap);

            for (int r = 0; r < maxWorldRow; r++) {
                for (int c = 0; c < maxWorldCol; c++) {
                    collisionMap[r][c] = tileM.checkTileCollision(tileMap[r][c]);
                }
            }

            reader.readLine();
            objMap = new int[maxWorldRow][maxWorldCol];
            objects = new GameObject[Math.max(1, objectNum)];
            // farmers initialization removed
            readArray(reader, objMap);
        }
    }

    private void loadTmxMap(InputStream input, String resourcePath) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringComments(true);
        factory.setIgnoringElementContentWhitespace(true);

        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(input);
        Element mapElem = doc.getDocumentElement();

        int width = Integer.parseInt(mapElem.getAttribute("width"));
        int height = Integer.parseInt(mapElem.getAttribute("height"));
        int tileWidth = Integer.parseInt(mapElem.getAttribute("tilewidth"));
        int tileHeight = Integer.parseInt(mapElem.getAttribute("tileheight"));

        // Nilai 16 harus sama dengan originalTileSize di GamePanel.
        final int GAME_ORIGINAL_TILE_SIZE = 16;
        if (tileWidth != GAME_ORIGINAL_TILE_SIZE || tileHeight != GAME_ORIGINAL_TILE_SIZE) {
            // Tidak fatal, hanya peringatan di console supaya layout tetap konsisten.
            System.out.println("[TMX] Peringatan: tilewidth/tileheight di " + resourcePath +
                    " berbeda dengan ukuran tile game (" + GAME_ORIGINAL_TILE_SIZE + ").");
        }

        maxWorldCol = width;
        maxWorldRow = height;

        // Nama level dari nama file (misal farm.tmx → FARM)
        String name = resourcePath;
        int slash = name.lastIndexOf('/');
        if (slash >= 0 && slash < name.length() - 1) {
            name = name.substring(slash + 1);
        }
        if (name.endsWith(".tmx")) {
            name = name.substring(0, name.length() - 4);
        }
        levelName = name.toUpperCase();
        isTmx = true;

        tileMap = new int[maxWorldRow][maxWorldCol];
        objMap = new int[maxWorldRow][maxWorldCol];
        collisionMap = new boolean[maxWorldRow][maxWorldCol];

        objects = new GameObject[500];
        objectNum = 0;

        // Baca semua tileset untuk mengetahui firstgid dan jenis tsx yang dipakai
        tilesetInfos = new ArrayList<>();
        NodeList tilesetNodes = mapElem.getElementsByTagName("tileset");
        for (int i = 0; i < tilesetNodes.getLength(); i++) {
            Element tsElem = (Element) tilesetNodes.item(i);
            String firstgidStr = tsElem.getAttribute("firstgid");
            if (firstgidStr == null || firstgidStr.isEmpty()) {
                continue;
            }

            int firstgid = Integer.parseInt(firstgidStr);
            String sourceTsx = tsElem.getAttribute("source");
            Element targetTsElem = tsElem;
            String currentBase = resourcePath;

            if (sourceTsx != null && !sourceTsx.isEmpty()) {
                String tsxPath = resolvePath(resourcePath, sourceTsx);
                try {
                    InputStream tsxIs = getClass().getResourceAsStream(tsxPath);
                    if (tsxIs != null) {
                        Document tsxDoc = builder.parse(tsxIs);
                        targetTsElem = tsxDoc.getDocumentElement();
                        currentBase = tsxPath;
                    }
                } catch (Exception e) {
                    System.out.println("[TMX] Gagal load eksternal tileset: " + tsxPath);
                }
            }

            TilesetInfo info = new TilesetInfo();
            info.firstgid = firstgid;
            info.name = targetTsElem.getAttribute("name");

            String twStr = targetTsElem.getAttribute("tilewidth");
            String thStr = targetTsElem.getAttribute("tileheight");
            info.tileWidth = (twStr == null || twStr.isEmpty()) ? 16 : Integer.parseInt(twStr);
            info.tileHeight = (thStr == null || thStr.isEmpty()) ? 16 : Integer.parseInt(thStr);

            NodeList imgNodes = targetTsElem.getElementsByTagName("image");
            if (imgNodes.getLength() > 0 && targetTsElem.hasAttribute("columns")
                    && !"0".equals(targetTsElem.getAttribute("columns"))) {
                Element imgElem = (Element) imgNodes.item(0);
                String imgSrc = imgElem.getAttribute("source");
                String path = resolvePath(currentBase, imgSrc);
                try {
                    InputStream is = getClass().getResourceAsStream(path);
                    if (is != null) {
                        info.image = ImageIO.read(is);
                        String tcStr = targetTsElem.getAttribute("tilecount");
                        String colStr = targetTsElem.getAttribute("columns");
                        info.tileCount = (tcStr == null || tcStr.isEmpty()) ? 0 : Integer.parseInt(tcStr);
                        info.columns = (colStr == null || colStr.isEmpty()) ? 0 : Integer.parseInt(colStr);
                    }
                } catch (Exception e) {
                    System.out.println("[TMX] Gagal load image tileset: " + path);
                }
            }

            NodeList tileNodes = targetTsElem.getElementsByTagName("tile");
            for (int t = 0; t < tileNodes.getLength(); t++) {
                Element tileElem = (Element) tileNodes.item(t);
                int localId = Integer.parseInt(tileElem.getAttribute("id"));

                NodeList tImgNodes = tileElem.getElementsByTagName("image");
                if (tImgNodes.getLength() > 0) {
                    Element tImgElem = (Element) tImgNodes.item(0);
                    String tImgSrc = tImgElem.getAttribute("source");
                    String tPath = resolvePath(currentBase, tImgSrc);
                    try {
                        InputStream tis = getClass().getResourceAsStream(tPath);
                        if (tis != null) {
                            info.tileImages.put(localId, ImageIO.read(tis));
                        }
                    } catch (Exception e) {
                    }
                }
            }
            tilesetInfos.add(info);
        }

        tileLayers = new ArrayList<>();
        NodeList layerNodes = mapElem.getElementsByTagName("layer");
        for (int i = 0; i < layerNodes.getLength(); i++) {
            Element layerElem = (Element) layerNodes.item(i);
            String layerName = layerElem.getAttribute("name");
            boolean visible = !layerElem.hasAttribute("visible") || "1".equals(layerElem.getAttribute("visible"));

            NodeList dataNodes = layerElem.getElementsByTagName("data");
            if (dataNodes.getLength() == 0) {
                continue;
            }
            Element dataElem = (Element) dataNodes.item(0);
            String encoding = dataElem.getAttribute("encoding");
            if (!"csv".equalsIgnoreCase(encoding)) {
                throw new IOException("TMX layer \"" + layerName + "\" harus memakai encoding=\"csv\"");
            }

            // Ambil semua angka gid dari CSV
            String csvText = dataElem.getTextContent();
            int[][] lData = new int[maxWorldRow][maxWorldCol];
            fillArrayFromCsv(csvText, lData, true, tilesetInfos);

            if (layerName.toLowerCase().contains("titik") || layerName.toLowerCase().contains("sampah")) {
                visible = false;
                for (int r = 0; r < maxWorldRow; r++) {
                    for (int c = 0; c < maxWorldCol; c++) {
                        if (lData[r][c] != 0) {
                            trashPoints.add(new java.awt.Point(c * gp.tileSize, r * gp.tileSize));
                            objectNum++;
                        }
                    }
                }
            }

            if (layerName.toLowerCase().contains("tanam") || layerName.toLowerCase().contains("plant")) {
                visible = false;
                for (int r = 0; r < maxWorldRow; r++) {
                    for (int c = 0; c < maxWorldCol; c++) {
                        if (lData[r][c] != 0) {
                            plantingPoints.add(new java.awt.Point(c * gp.tileSize, r * gp.tileSize));
                        }
                    }
                }
            }

            TmxLayerInfo layerInfo = new TmxLayerInfo();
            layerInfo.data = lData;
            layerInfo.visible = visible;
            tileLayers.add(layerInfo);

            // Jika nama layer mengandung 'Object', isi objMap untuk spawn items/NPCs
            if (layerName.toLowerCase().contains("object")) {
                fillArrayFromCsv(csvText, objMap, false, tilesetInfos);
            }

            // Gabungkan ke tileMap (untuk collision)
            // Prioritaskan tile yang memiliki collision
            for (int r = 0; r < maxWorldRow; r++) {
                for (int c = 0; c < maxWorldCol; c++) {
                    int rawGid = lData[r][c];
                    int gid = rawGid & 0x1FFFFFFF;
                    if (gid > 0) {
                        TilesetInfo chosen = getTilesetForGid(gid);
                        int localId = gid - (chosen != null ? chosen.firstgid : 0);

                        // Cek collision hanya jika tileset adalah tiles game standard
                        boolean hasCollision = false;
                        if (chosen != null && (chosen.name.toLowerCase().contains("tiles")
                                || chosen.name.toLowerCase().contains("game"))) {
                            // Untuk tileset game, pakai TileManager (yang diload dari tileData.txt)
                            hasCollision = tileM.checkTileCollision(localId);
                        }

                        // Jika tileMap masih kosong, atau tile baru ini memiliki collision, update
                        // Kita simpan gid di tileMap supaya draw() bisa merender dengan benar
                        if (tileMap[r][c] == 0 || hasCollision) {
                            tileMap[r][c] = gid;
                        }

                        if (hasCollision) {
                            collisionMap[r][c] = true;
                        }
                    }
                }
            }
        }

        // Hitung jumlah object, entity (farmer) dan key
        objectNum = 0;
        entityNum = 0;
        keyNum = 0;
        // Hitung jumlah object, entity (farmer) dan key dari layer Objects
        for (int row = 0; row < maxWorldRow; row++) {
            for (int col = 0; col < maxWorldCol; col++) {
                int v = objMap[row][col];
                if (v == 0) {
                    continue;
                }
                if (v == 99) {
                    entityNum++;
                } else {
                    objectNum++;
                    if (v == 2) {
                        keyNum++;
                    }
                }
            }
        }

        // Cari objek lain dari objectgroup (Portal, Trigger, Collusion)
        NodeList groups = mapElem.getElementsByTagName("objectgroup");
        tmxObjects = new ArrayList<>(); // Initialize the list for TMX objects
        for (int i = 0; i < groups.getLength(); i++) {
            Node gNode = groups.item(i);
            if (gNode.getNodeType() != Node.ELEMENT_NODE)
                continue;
            Element groupElem = (Element) gNode;
            NodeList objNodes = groupElem.getElementsByTagName("object");
            for (int j = 0; j < objNodes.getLength(); j++) {
                Element objElem = (Element) objNodes.item(j);
                String oType = objElem.getAttribute("type");
                String oName = objElem.getAttribute("name");

                objectNum++;
                TmxObjectInfo info = new TmxObjectInfo();
                info.name = oName;
                String oClass = objElem.getAttribute("class");
                info.type = (oType != null && !oType.isEmpty()) ? oType
                        : (oClass != null && !oClass.isEmpty()) ? oClass : "Decoration";

                System.out.println("[TMX] Found object: " + oName + " type/class: " + info.type);

                double scaleFactor = (double) gp.tileSize / GAME_ORIGINAL_TILE_SIZE;

                info.x = (int) (Double.parseDouble(objElem.getAttribute("x")) * scaleFactor);
                info.y = (int) (Double.parseDouble(objElem.getAttribute("y")) * scaleFactor);
                info.width = objElem.hasAttribute("width")
                        ? (int) (Double.parseDouble(objElem.getAttribute("width")) * scaleFactor)
                        : gp.tileSize;
                info.height = objElem.hasAttribute("height")
                        ? (int) (Double.parseDouble(objElem.getAttribute("height")) * scaleFactor)
                        : gp.tileSize;

                NodeList props = objElem.getElementsByTagName("property");
                for (int k = 0; k < props.getLength(); k++) {
                    Element p = (Element) props.item(k);
                    if ("targetArea".equals(p.getAttribute("name"))) {
                        info.targetArea = Integer.parseInt(p.getAttribute("value"));
                    }
                }
                tmxObjects.add(info);

                if (oName != null && (oName.toUpperCase().contains("PLANT") || oName.toUpperCase().contains("TANAM")
                        || oName.toUpperCase().contains("TITIK") || oName.toUpperCase().contains("POHON")
                        || oName.toUpperCase().contains("SPOT")
                        || info.type.toUpperCase().contains("PLANT") || info.type.toUpperCase().contains("TANAM"))) {
                    plantingPoints.add(new java.awt.Point(info.x, info.y));
                    System.out.println("[Map] Registered planting spot at: " + info.x + "," + info.y);
                }
            }
        }

        objects = new GameObject[Math.max(1, objectNum)];
        // farmers initialization removed

        // Cari spawn player dari objectgroup manapun
        boolean spawnFound = false;
        for (int i = 0; i < groups.getLength() && !spawnFound; i++) {
            Node gNode = groups.item(i);
            if (gNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            Element groupElem = (Element) gNode;

            NodeList objNodes = groupElem.getElementsByTagName("object");
            for (int j = 0; j < objNodes.getLength(); j++) {
                Element objElem = (Element) objNodes.item(j);
                String objName = objElem.getAttribute("name");

                if ("PlayerStart".equalsIgnoreCase(objName)) {
                    double x = Double.parseDouble(objElem.getAttribute("x"));
                    double y = Double.parseDouble(objElem.getAttribute("y"));

                    double scaleFactor = (double) gp.tileSize / GAME_ORIGINAL_TILE_SIZE;
                    playerStartX = (int) (x * scaleFactor);
                    playerStartY = (int) (y * scaleFactor);

                    spawnFound = true;
                    System.out.println("[Map] Found PlayerStart at (" + playerStartX + ", " + playerStartY
                            + ") in group: " + groupElem.getAttribute("name"));
                    break;
                }
            }
        }

        // Kalau tidak ada spawn di TMX, default ke (0,0)
        if (!spawnFound) {
            playerStartX = 0;
            playerStartY = 0;
        }
    }

    private void fillArrayFromCsv(String csvText, int[][] target, boolean isTileLayer, List<?> tilesetInfos) {
        if (csvText == null) {
            return;
        }
        String normalized = csvText.replace("\r", "").replace("\n", "").trim();
        if (normalized.isEmpty()) {
            return;
        }
        String[] tokens = normalized.split("\\s*,\\s*");
        int idx = 0;
        for (int row = 0; row < maxWorldRow; row++) {
            for (int col = 0; col < maxWorldCol; col++) {
                if (idx >= tokens.length) {
                    target[row][col] = 0;
                    continue;
                }
                String t = tokens[idx++];
                if (t.isEmpty()) {
                    target[row][col] = 0;
                    continue;
                }

                long gidLong = Long.parseLong(t);
                int gid = (int) gidLong;
                target[row][col] = isTileLayer ? gid : (int) (gidLong & 0x1FFFFFFF);
            }
        }
    }

    private void readArray(BufferedReader reader, int[][] arr) {
        try {
            for (int row = 0; row < maxWorldRow; row++) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                line = line.trim();
                if (line.isEmpty()) {
                    for (int col = 0; col < maxWorldCol; col++) {
                        arr[row][col] = 0;
                    }
                    continue;
                }

                String numbers[] = line.split("\\s+");
                for (int col = 0; col < maxWorldCol && col < numbers.length; col++) {
                    String token = numbers[col].trim();
                    if (token.isEmpty()) {
                        arr[row][col] = 0;
                    } else {
                        arr[row][col] = Integer.parseInt(token);
                    }
                }

                // jika kolom di file lebih sedikit dari maxWorldCol, sisanya isi 0
                for (int col = numbers.length; col < maxWorldCol; col++) {
                    arr[row][col] = 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void drawTiles(Graphics2D graphic2) {
        if (levelName.contains("INTERIOR")) {
            graphic2.setColor(Color.BLACK);
            graphic2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        }

        int margin = 10;
        int startCol = Math.max(0, (gp.player.worldX - gp.player.screenX) / gp.tileSize - margin);
        int endCol = Math.min(maxWorldCol, (gp.player.worldX + gp.player.screenX) / gp.tileSize + margin + 1);
        int startRow = Math.max(0, (gp.player.worldY - gp.player.screenY) / gp.tileSize - margin);
        int endRow = Math.min(maxWorldRow, (gp.player.worldY + gp.player.screenY) / gp.tileSize + margin + 1);

        for (int r = startRow; r < endRow; r++) {
            for (int c = startCol; c < endCol; c++) {
                int worldX = c * gp.tileSize;
                int worldY = r * gp.tileSize;
                int screenX = worldX - gp.player.worldX + gp.player.screenX;
                int screenY = worldY - gp.player.worldY + gp.player.screenY;

                if (isTmx) {
                    int layerIdx = 0;
                    for (TmxLayerInfo layer : tileLayers) {
                        if (!layer.visible) {
                            layerIdx++;
                            continue;
                        }

                        if (layerIdx >= 1) {
                            if (levelName.contains("INTERIOR") || levelName.contains("HOUSE")) {
                            } else if (levelName.contains("FISH") ||
                                    levelName.contains("FARM") ||
                                    levelName.contains("FOREST") || levelName.contains("PARK") ||
                                    levelName.contains("BARN")) {
                                layerIdx++;
                                continue;
                            }
                        }

                        int gid = layer.data[r][c];
                        if (gid > 0) {
                            drawTmxTile(graphic2, gid, screenX, screenY);
                        }
                        layerIdx++;
                    }
                } else {
                    int gid = tileMap[r][c];
                    if (gid > 0) {
                        graphic2.drawImage(tileM.getTileImage(gid), screenX, screenY, gp.tileSize, gp.tileSize, null);
                    }
                }
            }
        }
        useDeveloperTools(graphic2);
    }

    private void drawTmxTile(Graphics2D g2, int rawGid, int screenX, int screenY) {
        boolean flipH = (rawGid & 0x80000000) != 0;
        boolean flipV = (rawGid & 0x40000000) != 0;
        boolean flipD = (rawGid & 0x20000000) != 0;
        int gid = rawGid & 0x1FFFFFFF;

        TilesetInfo chosen = getTilesetForGid(gid);
        if (chosen == null)
            return;

        int localId = gid - chosen.firstgid;

        BufferedImage img = null;
        int srcX = 0, srcY = 0;
        int srcW = chosen.tileWidth, srcH = chosen.tileHeight;

        if (chosen.tileImages.containsKey(localId)) {
            img = chosen.tileImages.get(localId);
            srcW = img.getWidth();
            srcH = img.getHeight();
        } else if (chosen.image != null && chosen.columns > 0) {
            img = chosen.image;
            srcX = (localId % chosen.columns) * chosen.tileWidth;
            srcY = (localId / chosen.columns) * chosen.tileHeight;
        } else {
            img = tileM.getTileImage(localId);
            if (img != null) {
                srcW = img.getWidth();
                srcH = img.getHeight();
            }
        }

        if (img == null)
            return;

        java.awt.geom.AffineTransform oldAT = g2.getTransform();
        g2.translate(screenX, screenY);

        if (flipD) {
            g2.rotate(Math.toRadians(90), gp.tileSize / 2.0, gp.tileSize / 2.0);
            g2.scale(-1, 1);
            g2.translate(-gp.tileSize, 0);
        }

        if (flipH) {
            g2.scale(-1, 1);
            g2.translate(-gp.tileSize, 0);
        }
        if (flipV) {
            g2.scale(1, -1);
            g2.translate(0, -gp.tileSize);
        }

        g2.drawImage(img,
                0, 0, gp.tileSize, gp.tileSize,
                srcX, srcY, srcX + srcW, srcY + srcH,
                null);

        g2.setTransform(oldAT);
    }

    public TilesetInfo getTilesetForGid(int gid) {
        TilesetInfo chosen = null;
        for (TilesetInfo info : tilesetInfos) {
            if (info.firstgid <= gid) {
                if (chosen == null || info.firstgid > chosen.firstgid) {
                    chosen = info;
                }
            }
        }
        return chosen;
    }

    private String resolvePath(String base, String relative) {
        if (relative.startsWith("/"))
            return relative;

        int lastSlash = base.lastIndexOf('/');
        String folder = (lastSlash >= 0) ? base.substring(0, lastSlash) : "";
        String combined = folder + "/" + relative;

        combined = combined.replace('\\', '/');

        String[] parts = combined.split("/");
        java.util.LinkedList<String> stack = new java.util.LinkedList<>();

        for (String part : parts) {
            if (part.isEmpty() || part.equals(".")) {
                continue;
            }
            if (part.equals("..")) {
                if (!stack.isEmpty()) {
                    stack.removeLast();
                }
            } else {
                stack.add(part);
            }
        }

        StringBuilder key = new StringBuilder();
        for (String s : stack) {
            key.append("/").append(s);
        }

        return key.toString();
    }

    public void setObject() {
        int col = 0, row = 0, i = 0;

        boolean skipObjMap = levelName != null && levelName.equals("HOUSE_INTERIOR");

        while (!skipObjMap && col < maxWorldCol && row < maxWorldRow) {
            int gid = objMap[row][col];
            if (gid != 0 && i < objects.length) {
                int objIndex = gid;
                if (isTmx) {
                    TilesetInfo chosen = getTilesetForGid(gid);
                    if (chosen != null) {
                        objIndex = gid - chosen.firstgid + 1;
                    }
                }

                if (objIndex != 99 && objects[i] == null) {
                    GameObject dummy = ObjectManager.createObject(objIndex);
                    if (dummy != null && dummy.type == GameObject.Type.TRASH) {
                        if (gp.player.isTrashCollected(levelName, col * gp.tileSize, row * gp.tileSize)) {
                            col++;
                            if (col == maxWorldCol) {
                                col = 0;
                                row++;
                            }
                            continue;
                        }
                    }

                    GameObject created = ObjectManager.createObject(objIndex);
                    if (created != null) {
                        objects[i] = created;
                        objects[i].index = i;
                        objects[i].worldX = col * gp.tileSize;
                        objects[i].worldY = row * gp.tileSize;
                        if ("Gate".equals(objects[i].name))
                            gateIndex = i;
                        i++;
                    }
                }
            }
            col++;
            if (col == maxWorldCol) {
                col = 0;
                row++;
            }
        }

        if (tmxObjects != null) {
        }
        for (TmxObjectInfo tmxObj : tmxObjects) {
            if ("NPC".equalsIgnoreCase(tmxObj.type)) {
                continue;
            }

            if (i < objects.length) {
                GameObject obj = null;
                String type = tmxObj.type != null ? tmxObj.type : "";
                String name = tmxObj.name != null ? tmxObj.name : "";

                if (type.equalsIgnoreCase("Spawn") || type.equalsIgnoreCase("PlayerStart") ||
                        name.equalsIgnoreCase("Spawn") || name.equalsIgnoreCase("PlayerStart") ||
                        name.toLowerCase().contains("spawn")) {
                    continue;
                }

                if ("Portal".equalsIgnoreCase(type) || "Portal".equalsIgnoreCase(name)
                        || name.toLowerCase().contains("pintu")) {
                    obj = ObjectManager.createObject(4);
                } else if ("Trash".equalsIgnoreCase(type) || type.toLowerCase().contains("trash")
                        || name.toLowerCase().contains("trash") || name.toLowerCase().contains("sampah")) {
                    obj = ObjectManager.createObject(1);
                } else if ("Bin".equalsIgnoreCase(type) || name.toLowerCase().contains("bin")) {
                    if (name.toLowerCase().contains("organik") || name.toLowerCase().contains("hijau")) {
                        obj = ObjectManager.createObject(8);
                    } else if (name.toLowerCase().contains("anorganik") || name.toLowerCase().contains("kuning")) {
                        obj = ObjectManager.createObject(9);
                    } else if (name.toLowerCase().contains("toxic") || name.toLowerCase().contains("merah")
                            || name.toLowerCase().contains("hazardous")) {
                        obj = ObjectManager.createObject(10);
                    } else {
                        obj = ObjectManager.createObject(8);
                    }
                }

                if (obj != null && obj.type == GameObject.Type.TRASH) {
                    if (gp.player.isTrashCollected(levelName, tmxObj.x, tmxObj.y)) {
                        continue;
                    }
                }

                if (obj != null) {
                    if (!name.isEmpty() && obj.type != GameObject.Type.TRASH && !"Trash".equalsIgnoreCase(name)) {
                        obj.displayName = name;
                    }
                    obj.worldX = tmxObj.x;
                    obj.worldY = tmxObj.y;
                    obj.hitbox = new Rectangle(0, 0, tmxObj.width, tmxObj.height);
                    obj.drawWidth = tmxObj.width;
                    obj.drawHeight = tmxObj.height;
                    obj.targetArea = tmxObj.targetArea;
                    obj.index = i;
                    objects[i] = obj;
                    i++;
                }
            }
        }

        System.out.println("[Map.setObject] Total trash points to spawn: " + trashPoints.size());
        for (java.awt.Point p : trashPoints) {
            if (i < objects.length) {
                if (gp.player.isTrashCollected(levelName, p.x, p.y)) {
                    continue;
                }

                OBJ_Trash trash = new OBJ_Trash(gp);
                trash.worldX = p.x;
                trash.worldY = p.y;
                trash.index = i;
                objects[i] = trash;
                System.out.println("[Map.setObject] Spawned trash at (" + p.x + "," + p.y + ") as objects[" + i + "]");
                i++;
                objectNum++;
            } else {
                System.out.println("[Map.setObject] WARNING: Max objects reached, cannot spawn trash at (" + p.x + ","
                        + p.y + ")");
            }
        }
        for (int r = 0; r < maxWorldRow; r++) {
            for (int c = 0; c < maxWorldCol; c++) {
                int gid = tileMap[r][c] & 0x1FFFFFFF;
                if (gid == 309) {
                    boolean exists = false;
                    for (GameObject o : objects) {
                        if (o != null && o.worldX == c * gp.tileSize && o.worldY == r * gp.tileSize) {
                            exists = true;
                            break;
                        }
                    }
                    if (!exists && i < objects.length) {
                        GameObject autoDoor = ObjectManager.createObject(4);
                        if (autoDoor != null) {
                            autoDoor.worldX = c * gp.tileSize;
                            autoDoor.worldY = r * gp.tileSize;
                            autoDoor.displayName = "Pintu Masuk";
                            autoDoor.index = i;
                            objects[i] = autoDoor;
                            i++;
                        }
                    }
                }
            }
        }

        if (levelName.equals("HOUSE")) {
            System.out.println("[Map] Applying manual overrides for HOUSE");
            playerStartX = 5 * gp.tileSize;
            playerStartY = 8 * gp.tileSize;

            GameObject door = ObjectManager.createObject(4);
            if (door != null) {
                door.worldX = 11 * gp.tileSize;
                door.worldY = 7 * gp.tileSize;
                door.hitbox = new Rectangle(0, 0, gp.tileSize, gp.tileSize);
                door.drawWidth = gp.tileSize;
                door.displayName = "Pintu Masuk";
                door.targetArea = 5;
                door.targetX = 15;
                door.targetY = 20;
                door.targetDirection = "up";
                boolean alreadyExists = false;
                for (int j = 0; j < objects.length; j++) {
                    if (objects[j] != null && objects[j].worldX == door.worldX && objects[j].worldY == door.worldY) {
                        objects[j] = door;
                        door.index = j;
                        alreadyExists = true;
                        break;
                    }
                }
                if (!alreadyExists) {
                    for (int j = 0; j < objects.length; j++) {
                        if (objects[j] == null) {
                            objects[j] = door;
                            door.index = j;
                            break;
                        }
                    }
                }
            }

        }

        if (levelName.equals("HOUSE_INTERIOR")) {
            System.out.println("[Map] Applying manual overrides for HOUSE_INTERIOR");
            playerStartX = 8 * gp.tileSize;
            playerStartY = 4 * gp.tileSize;

            GameObject bed = ObjectManager.createObject(7);
            if (bed != null) {
                bed.worldX = 6 * gp.tileSize;
                bed.worldY = 3 * gp.tileSize;
                bed.image = null;
                bed.hitbox = new Rectangle(0, 0, gp.tileSize * 2, gp.tileSize * 2);
                bed.drawWidth = gp.tileSize * 2;
                bed.drawHeight = gp.tileSize * 2;
                for (int j = 0; j < objects.length; j++) {
                    if (objects[j] == null) {
                        objects[j] = bed;
                        break;
                    }
                }
            }

            GameObject door = ObjectManager.createObject(4);
            if (door != null) {
                door.worldX = 15 * gp.tileSize;
                door.worldY = 22 * gp.tileSize;
                door.hitbox = new Rectangle(0, 0, gp.tileSize, gp.tileSize);
                door.drawWidth = gp.tileSize;
                door.drawHeight = gp.tileSize;
                door.displayName = "Pintu Keluar";
                door.targetArea = 0;
                door.targetX = 11;
                door.targetY = 9;
                door.targetDirection = "down";
                for (int j = 0; j < objects.length; j++) {
                    if (objects[j] == null) {
                        objects[j] = door;
                        break;
                    }
                }
            }
        }
    }

    public boolean isSolid(int r, int c) {
        if (collisionMap == null || r < 0 || r >= maxWorldRow || c < 0 || c >= maxWorldCol) {
            return false;
        }
        return collisionMap[r][c];
    }

    public void spawnManualObject(GameObject obj, int col, int row, int targetArea, int targetX, int targetY,
            String targetDir) {
        if (obj == null)
            return;

        for (int i = 0; i < objects.length; i++) {
            if (objects[i] == null) {
                obj.worldX = col * gp.tileSize;
                obj.worldY = row * gp.tileSize;
                obj.index = i;
                obj.targetArea = targetArea;
                obj.targetX = targetX;
                obj.targetY = targetY;
                obj.targetDirection = targetDir;
                objects[i] = obj;
                System.out.println("[Map] Manual spawn: " + obj.displayName + " at tile (" + col + "," + row
                        + ") -> world (" + obj.worldX + "," + obj.worldY + ")");
                break;
            }
        }
    }

    public void drawObjects(Graphics2D graphic2) {
        for (int i = 0; i < objects.length; i++) {
            if (objects[i] != null) {
                objects[i].update(gp);
                objects[i].draw(graphic2, gp);
            }
        }
    }

    protected void drawFarmers(Graphics2D graphic2) {
    }

    private void useDeveloperTools(Graphics2D graphic2) {
        if (showCoords) {
            int col = 0;
            int row = 0;

            while (col < maxWorldCol && row < maxWorldRow) {
                int worldX = (col * gp.tileSize) + 8;
                int worldY = (row * gp.tileSize) + 24;
                int screenX = worldX - gp.player.worldX + gp.player.screenX;
                int screenY = worldY - gp.player.worldY + gp.player.screenY;
                String toPrint = "(" + col + ", " + row + ")";

                graphic2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                graphic2.drawString(toPrint, screenX, screenY);

                col++;
                if (col == maxWorldCol) {
                    col = 0;
                    row++;
                }
            }
        }

        if (drawPath) {
            graphic2.setColor(new Color(255, 0, 0, 70));

            for (int i = 0; i < gp.pathFinder.pathList.size(); i++) {
                int worldX = gp.pathFinder.pathList.get(i).col * gp.tileSize;
                int worldY = gp.pathFinder.pathList.get(i).row * gp.tileSize;
                int screenX = worldX - gp.player.worldX + gp.player.screenX;
                int screenY = worldY - gp.player.worldY + gp.player.screenY;

                graphic2.fillRect(screenX, screenY, gp.tileSize, gp.tileSize);
            }
        }

        if (hitboxTest) {
            graphic2.setColor(new Color(0, 0, 255, 100));

            graphic2.fillRect(gp.player.screenX + gp.player.hitbox.x, gp.player.screenY + gp.player.hitbox.y,
                    gp.player.hitbox.width, gp.player.hitbox.height);
        }
    }

    public boolean isPlantingSpot(int worldX, int worldY) {
        for (java.awt.Point p : plantingPoints) {
            if (Math.abs(worldX - p.x) < gp.tileSize && Math.abs(worldY - p.y) < gp.tileSize) {
                return true;
            }
        }

        // If no points defined in TMX, allow everywhere (legacy/debug)
        if (plantingPoints.isEmpty() && levelName.contains("FARM")) {
            return true;
        }

        return false;
    }

    public List<java.awt.Point> getTrashPoints() {
        return trashPoints;
    }
}
