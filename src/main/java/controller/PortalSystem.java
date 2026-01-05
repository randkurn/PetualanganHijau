package controller;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PortalSystem {
    private GamePanel gamePanel;
    private boolean isLoading = false;
    private float loadingProgress = 0f;
    private int targetMap = -1;
    private String targetSpawnName = null;
    private boolean isConfirming = false;
    private int portalCooldown = 0;
    private int lastX = -1, lastY = -1;

    // Loading animation
    private int animationFrame = 0;
    private int animationCounter = 0;

    // Educational content
    private List<EducationalContent> educationalContent;
    private EducationalContent currentContent;
    private Random random;

    // Portal tile IDs
    public static final int PORTAL_TILE_28 = 28;
    public static final int PORTAL_TILE_308 = 308;
    public static final int PORTAL_TILE_469 = 469;
    public static final int PORTAL_TILE_309 = 309;

    // Map indices
    public static final int MAP_HOUSE = 0;
    public static final int MAP_FARM = 1;
    public static final int MAP_PARK = 2;
    public static final int MAP_CITY = 3;
    public static final int MAP_FISHING = 4;
    public static final int MAP_HOUSE_INTERIOR = 5;

    public PortalSystem(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        this.random = new Random();
        initEducationalContent();
    }

    private void initEducationalContent() {
        educationalContent = new ArrayList<>();

        // Konten edukasi tentang sampah
        educationalContent.add(new EducationalContent(
                "Tahukah Kamu?",
                "Indonesia menghasilkan 64 juta ton sampah per tahun, dan hanya 10% yang didaur ulang!",
                Color.GREEN));

        educationalContent.add(new EducationalContent(
                "Fakta Sampah Plastik",
                "Plastik membutuhkan 450 tahun untuk terurai. Yuk, kurangi penggunaan plastik sekali pakai!",
                Color.BLUE));

        educationalContent.add(new EducationalContent(
                "Bahaya Sampah Organik",
                "Sampah organik yang tidak dikelola menghasilkan gas metana yang berbahaya bagi lingkungan.",
                Color.ORANGE));

        // Konten edukasi tentang banjir
        educationalContent.add(new EducationalContent(
                "Banjir di Indonesia",
                "Sampah yang menumpuk di sungai menyumbat aliran air dan menyebabkan banjir!",
                Color.RED));

        educationalContent.add(new EducationalContent(
                "Cegah Banjir!",
                "Membuang sampah pada tempatnya dapat mengurangi risiko banjir hingga 40%.",
                Color.CYAN));

        educationalContent.add(new EducationalContent(
                "Dampak Banjir",
                "Banjir di Jakarta tahun 2020 menyebabkan kerugian lebih dari 5 triliun rupiah.",
                Color.MAGENTA));

        educationalContent.add(new EducationalContent(
                "Solusi Bersama",
                "Dengan memilah sampah, kita bisa menyelamatkan lingkungan dan mencegah banjir!",
                Color.GREEN));
    }

    public void checkPortalTile(int playerWorldX, int playerWorldY) {
        if (isConfirming || isLoading || portalCooldown > 0 || gamePanel.mapM == null)
            return;

        int tileSize = gamePanel.tileSize;
        // If player hasn't moved significant distance from last confirmation spot, skip
        if (lastX != -1 && Math.abs(playerWorldX - lastX) < tileSize && Math.abs(playerWorldY - lastY) < tileSize) {
            return;
        }

        int tileX = playerWorldX / tileSize;
        int tileY = playerWorldY / tileSize;

        model.Map currentMap = gamePanel.mapM.getMap();
        if (currentMap == null || currentMap.tileMap == null)
            return;

        if (tileY < 0 || tileY >= currentMap.tileMap.length ||
                tileX < 0 || tileX >= currentMap.tileMap[0].length) {
            return;
        }

        // Read tile ID and ignore flip bits
        int tileNum = currentMap.tileMap[tileY][tileX] & 0x1FFFFFFF;

        // Deteksi portal berdasarkan tile ID dan map saat ini
        int currentMapIndex = gamePanel.mapM.currMap;
        int nextMap = -1;

        // Check for portal tiles
        boolean isPortalTile = (tileNum == PORTAL_TILE_28 || tileNum == PORTAL_TILE_308 ||
                tileNum == PORTAL_TILE_469 || tileNum == PORTAL_TILE_309);

        if (isPortalTile) {
            nextMap = determineNextMap(currentMapIndex, playerWorldX, playerWorldY);

            if (nextMap != -1 && nextMap != currentMapIndex) {
                String spawnName = determineSpawnName(currentMapIndex, nextMap);

                // Track location to prevent instant re-trigger
                lastX = playerWorldX;
                lastY = playerWorldY;

                isConfirming = true;
                final int finalNextMap = nextMap;
                final String finalSpawnName = spawnName;

                gamePanel.uiM.getPlayScreen().showDialogWithChoices(
                        "Apakah kamu ingin pindah ke area lain?",
                        "Sistem",
                        new String[] { "Ya", "Tidak" },
                        (idx, text) -> {
                            if (idx == 0) {
                                initiateLoading(finalNextMap, finalSpawnName);
                            }
                            isConfirming = false;
                            portalCooldown = 60; // 1 second safety
                        });
            }
        }
    }

    private String determineSpawnName(int fromMap, int toMap) {
        if (toMap == MAP_HOUSE) {
            if (fromMap == MAP_FISHING)
                return "DariFishing";
            if (fromMap == MAP_PARK)
                return "DariPark";
        }
        if (toMap == MAP_FISHING) {
            if (fromMap == MAP_HOUSE)
                return "Spawn Atas";
            if (fromMap == MAP_FARM)
                return "Spawn Bawah";
        }
        if (toMap == MAP_FARM) {
            if (fromMap == MAP_FISHING)
                return "PlayerStart Atas";
            if (fromMap == MAP_PARK)
                return "PlayerStart Bawah";
        }
        if (toMap == MAP_PARK) {
            if (fromMap == MAP_FARM)
                return "PlayerStart Atas";
            if (fromMap == MAP_HOUSE)
                return "PlayerStart Bawah";
        }
        if (toMap == MAP_HOUSE_INTERIOR) {
            return "Spawn Utama";
        }

        return null;
    }

    private int determineNextMap(int currentMap, int playerWorldX, int playerWorldY) {
        model.Map map = gamePanel.mapM.getMap();
        int mapWidth = map.maxWorldCol * gamePanel.tileSize;
        int mapHeight = map.maxWorldRow * gamePanel.tileSize;

        // Determine direction based on player position
        boolean isTop = playerWorldY < gamePanel.tileSize * 3;
        boolean isBottom = playerWorldY > mapHeight - gamePanel.tileSize * 3;
        boolean isLeft = playerWorldX < gamePanel.tileSize * 3;
        boolean isRight = playerWorldX > mapWidth - gamePanel.tileSize * 3;

        switch (currentMap) {
            case MAP_HOUSE:
                // Check for door tile (309) specifically FIRST to avoid map boundary overlap
                if (getTileID(playerWorldX, playerWorldY) == PORTAL_TILE_309)
                    return MAP_HOUSE_INTERIOR;

                if (isRight || playerWorldX > mapWidth - gamePanel.tileSize * 6)
                    return MAP_CITY;
                if (isBottom || playerWorldY > mapHeight - gamePanel.tileSize * 6)
                    return MAP_FISHING;
                if (isTop || playerWorldY < gamePanel.tileSize * 10)
                    return MAP_PARK;
                break;

            case MAP_CITY:
                if (isLeft || playerWorldX < gamePanel.tileSize * 6)
                    return MAP_HOUSE;
                break;

            case MAP_FISHING:
                if (isTop || playerWorldY < gamePanel.tileSize * 15)
                    return MAP_HOUSE;
                if (isBottom || playerWorldY > mapHeight - gamePanel.tileSize * 10)
                    return MAP_FARM;
                break;

            case MAP_FARM:
                if (isTop || playerWorldY < gamePanel.tileSize * 20)
                    return MAP_FISHING;
                if (isBottom || playerWorldY > mapHeight - gamePanel.tileSize * 10)
                    return MAP_PARK;
                break;

            case MAP_PARK:
                if (isTop || playerWorldY < gamePanel.tileSize * 10)
                    return MAP_FARM;
                if (isBottom || playerWorldY > mapHeight - gamePanel.tileSize * 10)
                    return MAP_HOUSE;
                break;

            case MAP_HOUSE_INTERIOR:
                // If at the very bottom of the interior, exit to house
                if (isBottom || playerWorldY > mapHeight - gamePanel.tileSize * 3)
                    return MAP_HOUSE;
                break;
        }

        return -1;
    }

    public void initiateLoading(int targetMapIndex) {
        initiateLoading(targetMapIndex, null);
    }

    public void initiateLoading(int targetMapIndex, String spawnName) {
        isLoading = true;
        loadingProgress = 0f;
        targetMap = targetMapIndex;
        targetSpawnName = spawnName;
        animationFrame = 0;
        animationCounter = 0;

        // Pick random educational content
        currentContent = educationalContent.get(random.nextInt(educationalContent.size()));

        System.out.println("[PortalSystem] Initiating loading to map " + targetMapIndex + " spawn: " + spawnName);
    }

    public void update() {
        if (portalCooldown > 0)
            portalCooldown--;

        if (!isLoading)
            return;

        // Update loading progress
        loadingProgress += 0.008f;

        // Update animation
        animationCounter++;
        if (animationCounter > 10) {
            animationFrame = (animationFrame + 1) % 8;
            animationCounter = 0;
        }

        // When loading complete, change map
        if (loadingProgress >= 1.0f) {
            gamePanel.mapM.setPendingSpawnName(targetSpawnName);
            gamePanel.mapM.changeToArea(targetMap);
            isLoading = false;
            loadingProgress = 0f;
            targetMap = -1;
            targetSpawnName = null;
            System.out.println("[PortalSystem] Loading complete!");
        }
    }

    private int getTileID(int x, int y) {
        int tx = x / gamePanel.tileSize;
        int ty = y / gamePanel.tileSize;
        model.Map m = gamePanel.mapM.getMap();
        if (m != null && m.tileMap != null && ty >= 0 && ty < m.tileMap.length && tx >= 0 && tx < m.tileMap[0].length) {
            return m.tileMap[ty][tx] & 0x1FFFFFFF;
        }
        return -1;
    }

    public void draw(Graphics2D g2) {
        if (!isLoading)
            return;

        int screenWidth = gamePanel.screenWidth;
        int screenHeight = gamePanel.screenHeight;

        // Draw dark overlay (Full black as requested)
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, screenWidth, screenHeight);

        // Draw loading percentage
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 48));
        String percentText = (int) (loadingProgress * 100) + "%";
        int percentWidth = g2.getFontMetrics().stringWidth(percentText);
        int centerY = screenHeight / 2;
        g2.drawString(percentText, screenWidth / 2 - percentWidth / 2, centerY);

        // Draw loading text
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        String loadingText = "Memindahkan Area";
        int textWidth = g2.getFontMetrics().stringWidth(loadingText);
        g2.drawString(loadingText, screenWidth / 2 - textWidth / 2, centerY - 80);

        // Draw dots
        String dots = "";
        for (int i = 0; i < (animationFrame % 4); i++) {
            dots += ".";
        }
        g2.drawString(dots, screenWidth / 2 + textWidth / 2 + 5, centerY - 80);

        // Draw educational content
        if (currentContent != null) {
            drawEducationalContent(g2);
        }
    }

    private void drawEducationalContent(Graphics2D g2) {
        int screenWidth = gamePanel.screenWidth;
        int screenHeight = gamePanel.screenHeight;

        // Draw content box
        int boxWidth = screenWidth - 100;
        int boxHeight = 150;
        int boxX = 50;
        int boxY = screenHeight - boxHeight - 50;

        // Draw box background
        g2.setColor(new Color(currentContent.color.getRed(),
                currentContent.color.getGreen(),
                currentContent.color.getBlue(), 180));
        g2.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 15, 15);

        // Draw border
        g2.setColor(currentContent.color);
        g2.setStroke(new java.awt.BasicStroke(3));
        g2.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 15, 15);

        // Draw title
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 22));
        g2.drawString(currentContent.title, boxX + 20, boxY + 35);

        // Draw description
        g2.setFont(new Font("Arial", Font.PLAIN, 16));
        drawWrappedText(g2, currentContent.description, boxX + 20, boxY + 65, boxWidth - 40);
    }

    private void drawWrappedText(Graphics2D g2, String text, int x, int y, int maxWidth) {
        String[] words = text.split(" ");
        String line = "";
        int lineY = y;

        for (String word : words) {
            String testLine = line + word + " ";
            int testWidth = g2.getFontMetrics().stringWidth(testLine);

            if (testWidth > maxWidth && !line.isEmpty()) {
                g2.drawString(line, x, lineY);
                line = word + " ";
                lineY += 25;
            } else {
                line = testLine;
            }
        }

        if (!line.isEmpty()) {
            g2.drawString(line, x, lineY);
        }
    }

    public boolean isLoading() {
        return isLoading;
    }

    private static class EducationalContent {
        String title;
        String description;
        Color color;

        EducationalContent(String title, String description, Color color) {
            this.title = title;
            this.description = description;
            this.color = color;
        }
    }
}
