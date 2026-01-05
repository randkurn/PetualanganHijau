package controller;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import javax.swing.JPanel;

import model.Player;
import model.Settings;
import model.PlayerData.PlantedTreeData;
import view.UIManager;
import java.util.ArrayList;
import java.util.List;
import view.DebugOverlay;

public class GamePanel extends JPanel implements Runnable {
    private final int originalTileSize = 16;
    public int scale = 3;
    public int tileSize = originalTileSize * scale;

    public final int maxScreenCol = 20;
    public final int maxScreenRow = 12;
    public int screenWidth = tileSize * maxScreenCol;
    public int screenHeight = tileSize * maxScreenRow;

    // FPS sedikit diturunkan untuk mengurangi beban CPU/GPU
    private final int FPS = 50;

    Settings settings = Settings.getInstance();
    public InputManager inputM = new InputManager(this);
    Thread gameThread;
    public UIManager uiM = new UIManager(this);
    public StateManager stateM = new StateManager(this);
    public TimeManager timeM = TimeManager.getInstance();
    public NPCManager npcM = new NPCManager(this);
    public EnvironmentManager envM = EnvironmentManager.getInstance();
    public CutsceneManager csM = new CutsceneManager(this);
    public DebugOverlay debugOverlay = new DebugOverlay(this);
    public SceneManager sceneM = new SceneManager(this);

    public MapManager mapM = new MapManager(this);
    public PortalSystem portalSystem = new PortalSystem(this);

    public CollisionChecker checker = new CollisionChecker(this);
    public Player player = new Player(this, inputM.getPlayInput());
    public Pathfinding pathFinder = new Pathfinding(this);

    // Biar ga langsung skip ke New Game pas balik dari pause
    public boolean ignoreNextEnterOnTitle = false;
    // Balik ke title setelah save (kalo pilih main menu dari pause)
    public boolean returnToTitleAfterSave = false;
    public boolean triggerSleepAfterSave = false;
    public boolean chapter1Active = true;
    public int chapter1TrashCount = 0;
    public boolean chapter2Active = false;
    public int chapter2TrashCount = 0;
    public boolean chapter2Finished = false;
    public boolean chapter3Active = false;
    public int chapter3TrashCount = 0;
    public boolean tehDilaGiftGiven = false;
    public List<PlantedTreeData> plantedTrees = new ArrayList<>();

    private final String[] treeFunFacts = {
            "Tercatat 257.384 ha deforestasi pada 2023, meningkat dari 2022, dengan sebagian besar terjadi di Kalimantan dan Sulawesi.",
            "Indonesia memiliki sekitar 81 miliar pohon yang berperan penting sebagai 'paru-paru' dunia, menyerap karbon, dan menghasilkan oksigen.",
            "Satu pohon berukuran sedang bisa menghasilkan oksigen yang cukup untuk 4 orang bernapas.",
            "Akar pohon membantu menyaring air dan mencegah tanah longsor, menjadikannya penjaga sumber daya air bersih.",
            "Hutan Indonesia membantu mendinginkan udara dan mengurangi stres, menjadi benteng utama dalam menghadapi perubahan iklim."
    };

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(inputM.getCurrentInput());
        this.setFocusable(true);
    }

    protected void setupGame() {
        // Baca preferensi fullscreen dari settings
        if (settings.getFullScreen() != null && settings.getFullScreen()) {
            setFullScreen();
        } else {
            setWindowScreen();
        }

        envM.setGamePanel(this);
        timeM.setGamePanel(this);

        this.requestFocusInWindow();
    }

    public void setFullScreen() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();

        // Make window truly fullscreen (no title bar)
        App.window.dispose();
        App.window.setUndecorated(true);
        App.window.setVisible(true);
        gd.setFullScreenWindow(App.window);

        screenWidth = App.window.getWidth();
        screenHeight = App.window.getHeight();

        scale = 5;
        tileSize = scale * originalTileSize;
        // Sesuaikan ukuran panel dengan resolusi fullscreen saat ini
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.revalidate();

        player.screenX = screenWidth / 2 - (tileSize / 2);
        player.screenY = screenHeight / 2 - (tileSize / 2);

        uiM.resetPlayScreen();
        mapM.resetMap();
        player.setDefaultValues();

        this.requestFocusInWindow();
    }

    public void setWindowScreen() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();

        scale = 3;
        tileSize = scale * originalTileSize;

        screenWidth = tileSize * maxScreenCol;
        screenHeight = tileSize * maxScreenRow;

        // Keluar dari fullscreen
        gd.setFullScreenWindow(null);

        App.window.dispose();
        App.window.setUndecorated(false);
        App.window.setVisible(true);

        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.revalidate();
        if (App.window != null) {
            App.window.pack();
            App.window.setLocationRelativeTo(null);
        }

        player.screenX = screenWidth / 2 - (tileSize / 2);
        player.screenY = screenHeight / 2 - (tileSize / 2);

        uiM.resetPlayScreen();
        mapM.resetMap();
        player.setDefaultValues();

        this.requestFocusInWindow();
    }

    protected void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (gameThread != null) {
            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                updateSleepTransition();
                updateExhaustion();
                updatePlantingTransition();
                portalSystem.update();
                stateM.update();
                repaint();
                delta--;
            }
        }
    }

    protected void paintComponent(Graphics graphic) {
        super.paintComponent(graphic);

        Graphics2D graphic2 = (Graphics2D) graphic;

        graphic2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        graphic2.setRenderingHint(java.awt.RenderingHints.KEY_TEXT_ANTIALIASING,
                java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        stateM.draw(graphic2);

        drawSleepTransition(graphic2);
        drawExhaustionTransition(graphic2);
        drawPlantingTransition(graphic2);

        // Draw portal loading screen on top of other transitions
        portalSystem.draw(graphic2);

        // Draw debug overlay on top of everything
        debugOverlay.draw(graphic2);

        graphic2.dispose();
    }

    private boolean isSleeping = false;
    private int sleepFadeAlpha = 0;
    private int sleepPhase = 0;

    public void triggerSleep() {
        if (!isSleeping) {
            isSleeping = true;
            sleepPhase = 0;
            sleepFadeAlpha = 0;
            stateM.setCurrentState(StateManager.gameState.PLAY);
        }
    }

    public void updateSleepTransition() {
        if (!isSleeping)
            return;

        switch (sleepPhase) {
            case 0:
                sleepFadeAlpha += 5;
                if (sleepFadeAlpha >= 255) {
                    sleepFadeAlpha = 255;
                    sleepPhase = 1;
                }
                break;
            case 1:
                timeM.advanceDay();
                player.restoreEnergy(player.maxEnergy);

                // If Chapter 2 is finished, start Chapter 3 next to the bed
                if (chapter2Finished && !chapter3Active) {
                    chapter3Active = true;
                    mapM.changeToAreaWithoutRespawn(5); // Player Room
                    player.worldX = 7 * tileSize;
                    player.worldY = 4 * tileSize;
                    player.direction = "down";
                    uiM.showMessage("Chapter 3 Dimulai: Hari Baru, Semangat Baru!");
                } else {
                    uiM.showMessage("Hari baru dimulai!");
                }

                sleepPhase = 2;
                break;
            case 2:
                sleepFadeAlpha -= 5;
                if (sleepFadeAlpha <= 0) {
                    sleepFadeAlpha = 0;
                    isSleeping = false;
                }
                break;
        }
    }

    public void drawSleepTransition(java.awt.Graphics2D g2) {
        if (isSleeping && sleepFadeAlpha > 0) {
            g2.setColor(new java.awt.Color(0, 0, 0, sleepFadeAlpha));
            g2.fillRect(0, 0, screenWidth, screenHeight);

            if (sleepPhase == 1 || sleepFadeAlpha > 200) {
                g2.setColor(new java.awt.Color(255, 255, 255, Math.min(sleepFadeAlpha, 200)));
                g2.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 48));
                String zzz = "Zzz...";
                int textWidth = g2.getFontMetrics().stringWidth(zzz);
                g2.drawString(zzz, (screenWidth - textWidth) / 2, screenHeight / 2);
            }
        }
    }

    public boolean isSleeping() {
        return isSleeping;
    }

    private boolean isExhausted = false;
    private int exhaustionPhase = 0;
    private int exhaustionFadeAlpha = 0;

    public void triggerExhaustion() {
        if (!isExhausted && !isSleeping) {
            isExhausted = true;
            exhaustionPhase = 0;
            exhaustionFadeAlpha = 0;
            player.speed = 0;
            System.out.println("[GamePanel] Exhaustion triggered - energy below 5");
        }
    }

    public void updateExhaustion() {
        if (!isExhausted)
            return;

        switch (exhaustionPhase) {
            case 0:
                exhaustionFadeAlpha += 5;
                if (exhaustionFadeAlpha >= 255) {
                    exhaustionFadeAlpha = 255;
                    exhaustionPhase = 1;
                }
                break;
            case 1:
                uiM.getSaveLoadScreen().setSaveMode(true);
                stateM.setCurrentState(StateManager.gameState.SAVE_LOAD);
                exhaustionPhase = 2;
                break;
            case 2: // Wait for save screen to finish
                if (stateM.getCurrentState() != StateManager.gameState.SAVE_LOAD) {
                    exhaustionPhase = 3;
                }
                break;
            case 3:
                timeM.advanceDay();
                player.energy = player.maxEnergy;
                mapM.changeToAreaWithoutRespawn(5);
                player.worldX = 7 * tileSize;
                player.worldY = 4 * tileSize;
                player.direction = "down";
                exhaustionPhase = 4;
                break;
            case 4:
                stateM.setCurrentState(StateManager.gameState.DIALOGUE);
                uiM.getPlayScreen().showDialog(
                        "Kemarin kamu kelelahan tidak mengingat caramu bisa kembali ke rumah dan tertidur di kasur.",
                        "Narasi");
                exhaustionPhase = 5;
                break;
            case 5:
                if (stateM.getCurrentState() != StateManager.gameState.DIALOGUE) {
                    exhaustionPhase = 6;
                }
                break;
            case 6:
                exhaustionFadeAlpha -= 5;
                if (exhaustionFadeAlpha <= 0) {
                    exhaustionFadeAlpha = 0;
                    isExhausted = false;
                    player.speed = 4;
                    stateM.setCurrentState(StateManager.gameState.PLAY);
                }
                break;
        }
    }

    public void drawExhaustionTransition(java.awt.Graphics2D g2) {
        if (isExhausted && exhaustionFadeAlpha > 0) {
            g2.setColor(new java.awt.Color(0, 0, 0, exhaustionFadeAlpha));
            g2.fillRect(0, 0, screenWidth, screenHeight);

            if (exhaustionPhase >= 3 && exhaustionFadeAlpha > 200) {
                g2.setColor(new java.awt.Color(255, 255, 255, Math.min(exhaustionFadeAlpha, 200)));
                g2.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 32));
                String text = "Kelelahan...";
                int textWidth = g2.getFontMetrics().stringWidth(text);
                g2.drawString(text, (screenWidth - textWidth) / 2, screenHeight / 2);
            }
        }
    }

    public boolean isExhausted() {
        return isExhausted;
    }

    public void startChapter1Scenes() {
        chapter1TrashCount = 0;
        chapter1Active = true;
        sceneM.startChapter1();
        stateM.setCurrentState(StateManager.gameState.SCENE);
    }

    private boolean isPlanting = false;
    private int plantingFadeAlpha = 0;
    private int plantingPhase = 0;
    private int plantingTimer = 0;
    private int currentFactIndex = 0;

    public void triggerPlanting() {
        if (!isPlanting) {
            isPlanting = true;
            plantingPhase = 0;
            plantingFadeAlpha = 0;
            plantingTimer = 0;
            currentFactIndex = new java.util.Random().nextInt(treeFunFacts.length);
        }
    }

    public void updatePlantingTransition() {
        if (!isPlanting)
            return;

        switch (plantingPhase) {
            case 0:
                plantingFadeAlpha += 10;
                if (plantingFadeAlpha >= 255) {
                    plantingFadeAlpha = 255;
                    plantingPhase = 1;
                    plantingTimer = 0;
                }
                break;
            case 1:
                plantingTimer++;
                if (plantingTimer > 200) {
                    plantingPhase = 2;
                }
                break;
            case 2:
                plantingFadeAlpha -= 10;
                if (plantingFadeAlpha <= 0) {
                    plantingFadeAlpha = 0;
                    isPlanting = false;
                }
                break;
        }
    }

    public void drawPlantingTransition(Graphics2D g2) {
        if (!isPlanting)
            return;

        g2.setColor(new Color(0, 0, 0, plantingFadeAlpha));
        g2.fillRect(0, 0, screenWidth, screenHeight);

        if (plantingPhase == 1 || plantingFadeAlpha > 200) {
            int alpha = Math.min(plantingFadeAlpha, 255);
            g2.setColor(new Color(255, 255, 255, alpha));

            g2.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 32));
            String title = "Menanam Pohon...";
            int titleWidth = g2.getFontMetrics().stringWidth(title);
            g2.drawString(title, (screenWidth - titleWidth) / 2, screenHeight / 3);

            int barWidth = 400;
            int barHeight = 20;
            int barX = (screenWidth - barWidth) / 2;
            int barY = screenHeight / 2;
            g2.drawRect(barX, barY, barWidth, barHeight);
            int progress = (int) (barWidth * (plantingTimer / 200.0));
            g2.fillRect(barX, barY, progress, barHeight);

            g2.setFont(new java.awt.Font("Arial", java.awt.Font.ITALIC, 18));
            String fact = treeFunFacts[currentFactIndex];
            drawWrappedText(g2, fact, screenWidth / 2, screenHeight / 2 + 80, barWidth + 100);
        }
    }

    private void drawWrappedText(Graphics2D g2, String text, int x, int y, int maxWidth) {
        java.awt.FontMetrics fm = g2.getFontMetrics();
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        int currentY = y;

        for (String word : words) {
            if (fm.stringWidth(line + " " + word) < maxWidth) {
                line.append(word).append(" ");
            } else {
                int lineX = x - (fm.stringWidth(line.toString()) / 2);
                g2.drawString(line.toString(), lineX, currentY);
                line = new StringBuilder(word).append(" ");
                currentY += fm.getHeight();
            }
        }
        int lineX = x - (fm.stringWidth(line.toString()) / 2);
        g2.drawString(line.toString(), lineX, currentY);
    }

    public boolean isPlanting() {
        return isPlanting;
    }

    /**
     * Force unlock player movement and reset states.
     * Use this as a panic button if the player gets stuck in cutscenes or
     * dialogues.
     */
    public void forceUnlock() {
        System.out.println("[GamePanel] Force Unlock triggered!");

        // Reset game states
        stateM.setCurrentState(StateManager.gameState.PLAY);

        // Reset player movement
        player.onPath = false;
        player.speed = 4;
        player.direction = "down";

        // Reset cutscene manager
        csM.reset();

        // Clear any active UI/Dialogs
        uiM.getPlayScreen().clearDialog();
        uiM.getPlayScreen().hideDialog();
        uiM.getPlayScreen().resetMessage();

        uiM.showMessage("SAFETY: Player movement force-unlocked.");
    }
}
