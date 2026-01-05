package controller;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

public class SceneManager {
    private GamePanel gamePanel;

    private Chapter1Scene currentScene;
    private Map<Chapter1Scene, BufferedImage> sceneImages;
    private int environmentScore = 0;
    private boolean awaitingChoice = false;
    private String[] currentChoices;
    private int selectedChoice = 0;

    private String[] narrationLines;
    private int narrationIndex = 0;
    private String currentDialogue = "";

    private float panOffset = 0f;
    private int panDirection = 1;
    private final float panSpeed = 0.3f;
    private final int MAX_PAN = 40;

    public enum Chapter1Scene {
        WAKE_UP,
        GREEN_BOOK, // Scene 2: Notebook close-up
        IBU_ROOM,
        MORNING_ACTIVITY,
        OUTSIDE
    }

    public SceneManager(GamePanel gp) {
        this.gamePanel = gp;
        this.sceneImages = new HashMap<>();
        this.currentScene = Chapter1Scene.WAKE_UP;
        loadSceneImages();
    }

    private void loadSceneImages() {
        String[] sceneFiles = {
                "/scenes/chap1sce1.png", // Wake up
                "/scenes/chap1sce2.png",
                "/scenes/scene3.png",
                "/scenes/scene4.png",
                "/scenes/chap1sce5.png"
        };

        Chapter1Scene[] scenes = {
                Chapter1Scene.WAKE_UP,
                Chapter1Scene.GREEN_BOOK,
                Chapter1Scene.IBU_ROOM,
                Chapter1Scene.MORNING_ACTIVITY,
                Chapter1Scene.OUTSIDE
        };

        for (int i = 0; i < scenes.length && i < sceneFiles.length; i++) {
            try {
                BufferedImage img = ImageIO.read(getClass().getResourceAsStream(sceneFiles[i]));
                if (img != null) {
                    sceneImages.put(scenes[i], img);
                }
            } catch (Exception e) {
                System.err.println("[SceneManager] Failed to load: " + sceneFiles[i]);
            }
        }
    }

    public void startChapter1() {
        currentScene = Chapter1Scene.WAKE_UP;
        environmentScore = 0;
        startScene(Chapter1Scene.WAKE_UP);
    }

    public void startScene(Chapter1Scene scene) {
        currentScene = scene;
        awaitingChoice = false;
        currentDialogue = "";
        narrationIndex = 0;
        panOffset = 0;
        panDirection = 1;

        switch (scene) {
            case WAKE_UP:
                narrationLines = new String[] {
                        "Cahaya matahari pagi perlahan menyelinap lewat celah jendela kamar.",
                        "Alarm ponselmu bergetar pelan...",
                        "Banjir kembali melanda beberapa titik di Purwakarta.",
                        "Sampah kembali disebut sebagai penyebab utama.",
                        "Hari baru dimulai."
                };
                break;

            case GREEN_BOOK:
                narrationLines = new String[] {
                        "Di meja belajarmu, sebuah buku catatan kecil tergeletak rapi.",
                        "Sampulnya hijau polos, bertuliskan: 'Mulai dari Rumah, Mulai dari Aku.'",
                        "Daftar ini kamu buat dua bulan lalu.",
                        "Namun pagi ini... rasanya berbeda.",
                        "Ada dorongan untuk tidak hanya melihat, tapi mulai bergerak."
                };
                break;

            case IBU_ROOM:
                narrationLines = new String[] {
                        "Ketukan pelan terdengar dari pintu kamar.",
                        "Ibumu berdiri di sana, tersenyum.",
                        "Ibu: 'Sudah bangun? Itu buku yang dulu sering kamu bawa, ya?'"
                };
                currentChoices = new String[] { "Aku kepikiran soal banjir, Bu.",
                        "Aku pengen mulai beresin lingkungan." };
                awaitingChoice = true;
                break;

            case MORNING_ACTIVITY:
                narrationLines = new String[] {
                        "Ibu: 'Kalau kamu mau mulai dari hal kecil, itu sudah bagus.'",
                        "Ibu: 'Siap-siap dulu. Mau mandi atau sarapan dulu?'"
                };
                currentChoices = new String[] { "Mandi dulu, baru sarapan", "Sarapan dulu, baru mandi" };
                awaitingChoice = true;
                break;

            case OUTSIDE:
                narrationLines = new String[] {
                        "Sinar matahari pagi menyambutmu lebih hangat dari yang kamu kira.",
                        "Namun pemandangan di depan rumah langsung menyadarkanmu.",
                        "Daun kering berserakan. Plastik nyangkut di pagar.",
                        "Botol minuman tergeletak di pinggir jalan. Sampah mulai menumpuk di sudut selokan.",
                        "Udara masih segar... tapi bau sampah tak bisa disembunyikan.",
                        "Kamu menggenggam buku catatan itu sekali lagi.",
                        "Baiklah, Mulai dari sini."
                };
                break;
        }
    }

    public void nextDialogue() {
        if (awaitingChoice)
            return;

        if (narrationLines != null && narrationIndex < narrationLines.length - 1) {
            narrationIndex++;
            return;
        }

        switch (currentScene) {
            case WAKE_UP:
                gamePanel.csM.nextPhase();
                break;
            case GREEN_BOOK:
                gamePanel.csM.nextPhase();
                break;
            case IBU_ROOM:
                gamePanel.csM.nextPhase();
                break;
            case MORNING_ACTIVITY:
                gamePanel.csM.nextPhase();
                break;
            case OUTSIDE:
                gamePanel.csM.nextPhase();
                break;
            default:
                finishChapter1();
                break;
        }
    }

    public void handleChoice(int choiceIndex) {
        if (!awaitingChoice)
            return;

        selectedChoice = choiceIndex;
        awaitingChoice = false;

        if (currentScene == Chapter1Scene.IBU_ROOM) {
            environmentScore += 5;
            gamePanel.csM.nextPhase();
        } else if (currentScene == Chapter1Scene.MORNING_ACTIVITY) {
            environmentScore += 5;
            gamePanel.csM.nextPhase();
        } else {
            finishChapter1();
        }
    }

    private void finishChapter1() {
        gamePanel.uiM.showMessage("Chapter 1 Selesai! Score: " + environmentScore);
        gamePanel.stateM.setCurrentState(StateManager.gameState.PLAY);
    }

    public void draw(Graphics2D g2) {
        BufferedImage sceneImg = sceneImages.get(currentScene);
        if (sceneImg != null) {
            int drawW = gamePanel.screenWidth + MAX_PAN;
            int drawH = gamePanel.screenHeight + (MAX_PAN / 2);
            g2.drawImage(sceneImg, -(int) panOffset, -(MAX_PAN / 4), drawW, drawH, null);
        }

        if (gamePanel.uiM != null && gamePanel.uiM.getChoiceDialogBox() != null) {
            String displayNarration = (narrationLines != null && narrationIndex < narrationLines.length)
                    ? narrationLines[narrationIndex]
                    : "";

            gamePanel.uiM.getChoiceDialogBox().draw(g2, displayNarration, currentDialogue,
                    awaitingChoice ? currentChoices : null,
                    selectedChoice);
        }
    }

    public void update() {
        panOffset += panSpeed * panDirection;
        if (panOffset > MAX_PAN) {
            panDirection = -1;
        } else if (panOffset < 0) {
            panDirection = 1;
        }
    }

    public boolean isAwaitingChoice() {
        return awaitingChoice;
    }

    public void moveChoiceUp() {
        if (awaitingChoice && selectedChoice > 0) {
            selectedChoice--;
        }
    }

    public void moveChoiceDown() {
        if (awaitingChoice && currentChoices != null && selectedChoice < currentChoices.length - 1) {
            selectedChoice++;
        }
    }

    public void confirmChoice() {
        if (awaitingChoice) {
            handleChoice(selectedChoice);
        }
    }

    public int getEnvironmentScore() {
        return environmentScore;
    }
}
