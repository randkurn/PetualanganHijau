package controller;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.AudioManager;
import model.GameStateManager;
import model.Player;
import model.TileMap;

public class GameController {

    @FXML
    private AnchorPane root;

    @FXML
    private Canvas canvas;

    @FXML
    private AnchorPane pauseOverlay;

    @FXML
    private Label lblPauseInfo;

    private GraphicsContext gc;

    private Player player;
    private TileMap map;

    // Camera
    private double camX = 0;
    private double camY = 0;

    // Input flags
    private boolean up, down, left, right;

    private long lastTime;
    private AnimationTimer loop;
    private boolean paused;

    // =============================================================
    // INITIALIZE
    // =============================================================
    @FXML
    public void initialize() {

        gc = canvas.getGraphicsContext2D();

        GameStateManager.GameState state = GameStateManager.consumeState();
        player = new Player(state.playerX(), state.playerY());
        player.setName(state.playerName());
        map = new TileMap("map1.txt");

        pauseOverlay.setVisible(false);
        pauseOverlay.setManaged(false);
        pauseOverlay.setMouseTransparent(true);

        // Bind input setelah Scene siap
        bindSceneForInput();

        AudioManager.playGameMusic();

        startLoop();
    }

    // =============================================================
    // INPUT SYSTEM (Bekerja 100%)
    // =============================================================
    private void bindSceneForInput() {

        root.sceneProperty().addListener((obs, oldScene, newScene) -> {

            if (newScene != null) {

                newScene.setOnKeyPressed(e -> {
                    switch (e.getCode()) {
                        case W -> { if (!paused) up = true; }
                        case S -> { if (!paused) down = true; }
                        case A -> { if (!paused) left = true; }
                        case D -> { if (!paused) right = true; }
                        case ESCAPE, P -> togglePause();
                    }
                });

                newScene.setOnKeyReleased(e -> {
                    switch (e.getCode()) {
                        case W -> up = false;
                        case S -> down = false;
                        case A -> left = false;
                        case D -> right = false;
                    }
                });
            }
        });
    }

    // =============================================================
    // GAME LOOP
    // =============================================================
    private void startLoop() {

        lastTime = System.nanoTime();

        loop = new AnimationTimer() {
            @Override
            public void handle(long now) {

                double dt = (now - lastTime) / 1e9;
                if (dt > 0.03) dt = 0.03; // stabilizer
                lastTime = now;

                if (!paused) {
                    update(dt);
                }
                render();
            }
        };

        loop.start();
    }

    // =============================================================
    // UPDATE
    // =============================================================
    private void update(double dt) {

        player.handleInput(up, down, left, right);
        player.update(dt, map);

        // camera follow player
        camX = player.getX() - (canvas.getWidth() / 2 - 32);
        camY = player.getY() - (canvas.getHeight() / 2 - 32);
    }

    // =============================================================
    // RENDER
    // =============================================================
    private void render() {

        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // draw tilemap
        map.render(gc, camX, camY);

        // draw player in center (64x64)
        gc.drawImage(
            player.getCurrentFrame(),
            canvas.getWidth() / 2 - 32,
            canvas.getHeight() / 2 - 32,
            64, 64
        );
    }

    private void togglePause() {
        paused = !paused;
        if (paused) {
            up = down = left = right = false;
        }
        pauseOverlay.setVisible(paused);
        pauseOverlay.setManaged(paused);
        pauseOverlay.setMouseTransparent(!paused);
        lblPauseInfo.setText(paused ? "Permainan dijeda" : "");
    }

    @FXML
    private void resumeGame() {
        if (paused) {
            togglePause();
        }
    }

    @FXML
    private void openOptions() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/options.fxml"));
            Parent dialogRoot = loader.load();
            Stage dialog = new Stage();
            dialog.setTitle("Options - Audio");
            dialog.initOwner(root.getScene().getWindow());
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setScene(new Scene(dialogRoot));
            dialog.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void exitToMenu() {
        GameStateManager.saveActiveSlot(player.getX(), player.getY(), player.getName());
        goToMainMenu();
    }

    private void goToMainMenu() {
        try {
            if (loop != null) {
                loop.stop();
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main_menu.fxml"));
            Parent menuRoot = loader.load();
            Scene scene = root.getScene();
            paused = false;
            pauseOverlay.setVisible(false);
            pauseOverlay.setManaged(false);
            pauseOverlay.setMouseTransparent(true);
            scene.setRoot(menuRoot);
            AudioManager.playMenuMusic();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
