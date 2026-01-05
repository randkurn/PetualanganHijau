package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

import controller.GamePanel;
import controller.StoryManager;
import controller.StoryManager.StoryData;

public class StoryScreen extends UI {
    private StoryData currentStory;
    private int currentParagraph;
    private int scrollOffset;
    private final int lineHeight = 25;
    private int maxScroll = 0;
    private int typewriterIndex = 0;
    private int typewriterSpeed = 3;
    private int frameCounter = 0;

    private BufferedImage[] sceneImages;
    private int currentSceneIndex = 0;

    private float panOffset = 0f;
    private int panDirection = 1;
    private float panSpeed = 0.2f;

    private int fadeAlpha = 0;
    private boolean isFadingToBlack = false;
    private boolean isFadingFromBlack = false;

    private boolean showingTitleCard = false;
    private int titleCardPhase = 0;
    private int titleCardAlpha = 0;
    private int titleCardCounter = 0;
    private static final int TITLE_FADE_SPEED = 4;
    private static final int TITLE_DISPLAY_DURATION = 180;

    protected StoryScreen(GamePanel gp) {
        super(gp);
        this.currentParagraph = 0;
        this.scrollOffset = 0;
        loadSceneImages();
    }

    private void loadSceneImages() {
        sceneImages = new BufferedImage[5];
        int loadedCount = 0;

        System.out.println("[StoryScreen] Attempting to load scene images from /scenes/");

        for (int i = 0; i < sceneImages.length; i++) {
            String filename = "/scenes/scene" + (i + 1) + ".png";
            try {
                System.out.println("[StoryScreen] Loading " + filename + "...");
                InputStream stream = getClass().getResourceAsStream(filename);

                if (stream == null) {
                    System.err.println("[StoryScreen] ERROR: Stream is NULL for " + filename);
                    System.err.println("[StoryScreen] File not found in resources!");
                } else {
                    sceneImages[i] = ImageIO.read(stream);
                    if (sceneImages[i] != null) {
                        loadedCount++;
                        System.out.println("[StoryScreen] ✓ Loaded " + filename +
                                " (" + sceneImages[i].getWidth() + "x" + sceneImages[i].getHeight() + ")");
                    } else {
                        System.err.println("[StoryScreen] ERROR: ImageIO.read returned NULL for " + filename);
                    }
                    stream.close();
                }
            } catch (IOException e) {
                System.err.println("[StoryScreen] ERROR loading " + filename + ": " + e.getMessage());
                e.printStackTrace();
            } catch (Exception e) {
                System.err.println("[StoryScreen] UNEXPECTED ERROR loading " + filename + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        System.out.println("[StoryScreen] Scene loading complete: " + loadedCount + "/5 images loaded");

        if (loadedCount == 0) {
            System.err.println("[StoryScreen] WARNING: NO SCENES LOADED! Prologue will show black screen.");
        }
    }

    public void loadStory(String storyKey) {
        StoryManager storyM = StoryManager.getInstance();
        if (storyM.hasStory(storyKey)) {
            reset();

            this.currentStory = storyM.getStory(storyKey);
            this.currentParagraph = 0;
            this.scrollOffset = 0;
            this.typewriterIndex = 0;
            this.maxScroll = 0;

            System.out.println("[StoryScreen] Story loaded: " + storyKey + " (Resetting cinematic states)");

            if ("prolog".equals(storyKey)) {
                AudioManager.getInstance().playPrologMusic();
            }
        }
    }

    public void nextParagraph() {
        if (currentStory != null && currentParagraph < currentStory.getParagraphs().length - 1) {
            int oldScene = getSceneIndexForParagraph(currentParagraph);
            currentParagraph++;
            int newScene = getSceneIndexForParagraph(currentParagraph);

            if (oldScene != newScene) {
                switch (newScene) {
                    case 0:
                        panOffset = 0;
                        panDirection = 1;
                        break;
                    case 1:
                        panOffset = getMaxPanOffset();
                        panDirection = -1;
                        break;
                    case 2:
                        panOffset = 0;
                        panDirection = 1;
                        break;
                    case 3:
                        panOffset = getMaxPanOffset();
                        panDirection = -1;
                        isFadingToBlack = false;
                        break;
                    case 4:
                        panOffset = 0;
                        panDirection = 0;
                        isFadingToBlack = false;
                        isFadingFromBlack = true;
                        fadeAlpha = 255;
                        break;
                }
            }

            scrollOffset = 0;
            typewriterIndex = 0;
            maxScroll = 0;
        }
    }

    private int getSceneIndexForParagraph(int paragraph) {
        if (paragraph <= 1)
            return 0;
        else if (paragraph <= 3)
            return 1;
        else if (paragraph <= 5)
            return 2;
        else if (paragraph <= 9)
            return 3;
        else
            return 4;
    }

    private float getMaxPanOffset() {
        return 200f;
    }

    public void previousParagraph() {
        if (currentParagraph > 0) {
            currentParagraph--;
            scrollOffset = 0;
            typewriterIndex = 0;
            maxScroll = 0;
        }
    }

    public void scrollUp() {
        if (scrollOffset > 0) {
            scrollOffset--;
        }
    }

    public void scrollDown() {
        if (scrollOffset < maxScroll) {
            scrollOffset++;
        }
    }

    public boolean isLastParagraph() {
        return currentStory != null && currentParagraph >= currentStory.getParagraphs().length - 1;
    }

    public void skipTypewriter() {
        if (currentStory != null) {
            typewriterIndex = currentStory.getParagraphs()[currentParagraph].length();
        }
    }

    public boolean isLastCharacterTyped() {
        if (currentStory == null)
            return true;
        return typewriterIndex >= currentStory.getParagraphs()[currentParagraph].length();
    }

    private int autoNextCounter = 0;
    private int autoNextMax = 0;
    private boolean isWaitingForAutoNext = false;

    public void draw(Graphics2D g2) {
        if (showingTitleCard) {
            drawTitleCard(g2);
            return;
        }

        if (currentStory == null) {
            return;
        }

        drawBackground(g2);

        // Kita gunakan bayangan teks (text shadow) untuk keterbacaan

        // Opsional: Berikan gradasi hitam sangat tipis di bagian bawah saja agar
        g2.setColor(new Color(0, 0, 0, 100));
        g2.fillRect(0, gp.screenHeight - 60, gp.screenWidth, 60);

        g2.setColor(Color.WHITE);
        g2.setFont(getScaledFont(Font.BOLD, 18));

        String title = currentStory.getTitle();
        if (title != null && !title.isEmpty()) {
            int titleX = getHorizontalCenter(title, g2, gp.screenWidth);
            g2.setColor(Color.BLACK);
            g2.drawString(title, titleX + 2, 82);
            g2.setColor(new Color(255, 220, 100));
            g2.drawString(title, titleX, 80);
            g2.setColor(Color.WHITE);
        }

        g2.setFont(getScaledFont(Font.PLAIN, 14));

        String fullParagraph = currentStory.getParagraphs()[currentParagraph];

        frameCounter++;
        if (frameCounter >= typewriterSpeed && typewriterIndex < fullParagraph.length()) {
            typewriterIndex++;
            frameCounter = 0;
            isWaitingForAutoNext = false;
            autoNextCounter = 0;

            if (typewriterIndex % 3 == 0) {
                AudioManager.getInstance().playTalkingSound();
            }
        }

        String displayText = fullParagraph.substring(0, Math.min(typewriterIndex, fullParagraph.length()));
        String[] lines = wrapTextCentered(displayText, g2, gp.screenWidth - 100);

        // Hitung posisi vertikal agar teks berada di tengah layar secara sinematik
        int scaledLineHeight = (int) (lineHeight * gp.scale / 4.0f);
        int totalTextHeight = lines.length * scaledLineHeight;
        int startY = (gp.screenHeight / 2) - (totalTextHeight / 2) + (int) (50 * gp.scale / 4.0f);

        for (int i = 0; i < lines.length; i++) {
            int lineX = getHorizontalCenter(lines[i], g2, gp.screenWidth);
            int lineY = startY + (i * scaledLineHeight);

            g2.setColor(Color.BLACK);
            g2.drawString(lines[i], lineX + 2, lineY + 2);

            g2.setColor(Color.WHITE);
            g2.drawString(lines[i], lineX, lineY);
        }

        if (typewriterIndex >= fullParagraph.length()) {
            if (!isWaitingForAutoNext) {
                isWaitingForAutoNext = true;
                int wordCount = fullParagraph.split("\\s+").length;
                autoNextMax = Math.min(450, 240 + (wordCount * 4));
                autoNextCounter = autoNextMax;
            }

            if (autoNextCounter > 0) {
                autoNextCounter--;
                g2.setColor(new Color(150, 255, 150, 150));
                int barWidth = (int) ((gp.screenWidth - 100) * (autoNextCounter / (double) autoNextMax));
                g2.fillRect(50, gp.screenHeight - 5, barWidth, 2);
            } else {
                handleAutoNext();
            }
        }

        // Instructions di bagian paling bawah
        g2.setFont(getScaledFont(Font.PLAIN, 10));

        String instruction = isLastParagraph() ? "ENTER: Mulai | ESC: Skip" : "SPACE: Lanjut | ESC: Skip";
        int instrX = getHorizontalCenter(instruction, g2, gp.screenWidth);

        g2.setColor(Color.BLACK);
        g2.drawString(instruction, instrX + 1, gp.screenHeight - 59);

        g2.setColor(new Color(200, 200, 200));
        g2.drawString(instruction, instrX, gp.screenHeight - 60);
    }

    private void handleAutoNext() {
        if (!isLastParagraph()) {
            nextParagraph();
        } else {
            showingTitleCard = true;
            titleCardPhase = 0;
            titleCardAlpha = 0;
            titleCardCounter = 0;
        }
    }

    private void drawTitleCard(Graphics2D g2) {
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        switch (titleCardPhase) {
            case 0:
                titleCardAlpha += TITLE_FADE_SPEED;
                if (titleCardAlpha >= 255) {
                    titleCardAlpha = 255;
                    titleCardPhase = 1;
                    titleCardCounter = 0;
                }
                break;
            case 1:
                titleCardCounter++;
                if (titleCardCounter >= TITLE_DISPLAY_DURATION) {
                    titleCardPhase = 2;
                }
                break;
            case 2:
                titleCardAlpha -= TITLE_FADE_SPEED;
                if (titleCardAlpha <= 0) {
                    titleCardAlpha = 0;
                    showingTitleCard = false;
                    startChapter1();
                    return;
                }
                break;
        }

        Color titleColor = new Color(100, 200, 100, titleCardAlpha);
        Color shadowColor = new Color(0, 0, 0, titleCardAlpha);
        Color subtitleColor = new Color(200, 200, 200, titleCardAlpha);

        g2.setFont(getScaledFont(Font.BOLD, 36));
        String title = "PETUALANGAN HIJAU";
        int titleX = getHorizontalCenter(title, g2, gp.screenWidth);
        int titleY = gp.screenHeight / 2 - 30;

        g2.setColor(shadowColor);
        g2.drawString(title, titleX + 3, titleY + 3);

        g2.setColor(titleColor);
        g2.drawString(title, titleX, titleY);

        g2.setFont(getScaledFont(Font.PLAIN, 14));
        String subtitle = "Sebuah Petualangan Menjaga Lingkungan";
        int subtitleX = getHorizontalCenter(subtitle, g2, gp.screenWidth);
        int subtitleY = titleY + 50;

        g2.setColor(shadowColor);
        g2.drawString(subtitle, subtitleX + 2, subtitleY + 2);

        g2.setColor(subtitleColor);
        g2.drawString(subtitle, subtitleX, subtitleY);

        g2.setColor(new Color(100, 200, 100, titleCardAlpha / 2));
        int lineY = titleY + 70;
        int lineWidth = 300;
        int lineX = (gp.screenWidth - lineWidth) / 2;
        g2.fillRect(lineX, lineY, lineWidth, 2);
    }

    private void startChapter1() {
        gp.mapM.resetMap();
        gp.player.setDefaultValues();
        if (gp.npcM != null)
            gp.npcM.setupChapter1NPCs();

        AudioManager.getInstance().stopMusic();
        AudioManager.getInstance().playHouseInteriorMusic();

        gp.stateM.setCurrentState(controller.StateManager.gameState.CUTSCENE);
        gp.csM.reset();
    }

    private void drawBackground(Graphics2D g2) {
        if (currentStory != null && "prolog".equals(getStoryKey())) {
            int sceneIndex = getSceneIndexForParagraph(currentParagraph);

            // Debug: check scene loading status
            if (sceneIndex != currentSceneIndex || sceneImages == null || sceneImages[sceneIndex] == null) {
                System.out.println("[DEBUG] Para " + currentParagraph + " -> Scene " + (sceneIndex + 1));
                System.out.println("[DEBUG] sceneImages null? " + (sceneImages == null));
                if (sceneImages != null && sceneIndex < sceneImages.length) {
                    System.out.println(
                            "[DEBUG] sceneImages[" + sceneIndex + "] null? " + (sceneImages[sceneIndex] == null));
                }
            }

            if (sceneImages != null && sceneIndex < sceneImages.length && sceneImages[sceneIndex] != null) {
                BufferedImage sceneImg = sceneImages[sceneIndex];

                if (panDirection != 0) {
                    panOffset += panSpeed * panDirection;

                    float maxPan = getMaxPanOffset();
                    if (panOffset < 0)
                        panOffset = 0;
                    if (panOffset > maxPan)
                        panOffset = maxPan;
                }

                if (sceneIndex == 3) {
                    if (currentParagraph == 9 && panOffset <= 50 && !isFadingToBlack) {
                        isFadingToBlack = true;
                        fadeAlpha = 0;
                    }
                }

                if (isFadingToBlack && !isFadingFromBlack) {
                    fadeAlpha += 4;
                    if (fadeAlpha >= 255) {
                        fadeAlpha = 255;
                    }
                }

                if (isFadingFromBlack) {
                    fadeAlpha -= 3;
                    if (fadeAlpha <= 0) {
                        fadeAlpha = 0;
                        isFadingFromBlack = false;
                        isFadingToBlack = false;
                    }
                }

                int imgW = sceneImg.getWidth();
                int imgH = sceneImg.getHeight();

                double scale = (double) gp.screenHeight / imgH;
                int scaledW = (int) (imgW * scale);

                float actualMaxPan = Math.max(0, scaledW - gp.screenWidth);
                if (panOffset > actualMaxPan)
                    panOffset = actualMaxPan;

                g2.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION,
                        java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2.drawImage(sceneImg, -(int) panOffset, 0, scaledW, gp.screenHeight, null);

                if (fadeAlpha > 0) {
                    g2.setColor(new Color(0, 0, 0, fadeAlpha));
                    g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
                }

                currentSceneIndex = sceneIndex;
                return;
            }
        }

        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
    }

    private String getStoryKey() {
        if (currentStory == null)
            return "";

        if (currentStory.getType() == StoryManager.StoryData.StoryType.PROLOG) {
            return "prolog";
        }

        if (currentStory.getTitle() != null && currentStory.getTitle().toLowerCase().contains("prolog")) {
            return "prolog";
        }

        return "";
    }

    private String[] wrapTextCentered(String text, Graphics2D g2, int maxWidth) {
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (word.contains("\n")) {
                String[] parts = word.split("\n");
                for (int i = 0; i < parts.length; i++) {
                    String testLine = line + parts[i] + " ";
                    int width = g2.getFontMetrics().stringWidth(testLine);

                    if (width > maxWidth && line.length() > 0) {
                        result.append(line.toString().trim()).append("\n");
                        line = new StringBuilder(parts[i] + " ");
                    } else {
                        line.append(parts[i]).append(" ");
                    }

                    if (i < parts.length - 1) {
                        result.append(line.toString().trim()).append("\n");
                        line = new StringBuilder();
                    }
                }
            } else {
                String testLine = line + word + " ";
                int width = g2.getFontMetrics().stringWidth(testLine);

                if (width > maxWidth && line.length() > 0) {
                    result.append(line.toString().trim()).append("\n");
                    line = new StringBuilder(word + " ");
                } else {
                    line.append(word).append(" ");
                }
            }
        }

        if (line.length() > 0) {
            result.append(line.toString().trim());
        }

        return result.toString().split("\n");
    }

    public void reset() {
        currentStory = null;
        currentParagraph = 0;
        scrollOffset = 0;
        typewriterIndex = 0;
        frameCounter = 0;
        maxScroll = 0;
        currentSceneIndex = 0;
        panOffset = 0;
        panDirection = 1;
        fadeAlpha = 0;
        isFadingToBlack = false;
        isFadingFromBlack = false;
        isWaitingForAutoNext = false;
        autoNextCounter = 0;
        autoNextMax = 0;
        if (!showingTitleCard) {
            titleCardPhase = 0;
            titleCardAlpha = 0;
            titleCardCounter = 0;
        }
    }

    public void setShowTitleCard(boolean showing) {
        this.showingTitleCard = showing;
        if (showing) {
            this.titleCardPhase = 0;
            this.titleCardAlpha = 0;
            this.titleCardCounter = 0;
        }
    }

    public StoryData getCurrentStory() {
        return currentStory;
    }

    public boolean isStoryLoaded(String storyKey) {
        if (currentStory == null)
            return false;

        StoryManager storyM = StoryManager.getInstance();
        if (!storyM.hasStory(storyKey))
            return false;

        StoryData checkStory = storyM.getStory(storyKey);
        return currentStory.getTitle().equals(checkStory.getTitle());
    }
}
