package controller;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import model.Player;
import model.TileMap;

public class GameController {

    @FXML
    private AnchorPane root;

    @FXML
    private Canvas canvas;

    private GraphicsContext gc;

    private Player player;
    private TileMap map;

    // Camera
    private double camX = 0;
    private double camY = 0;

    // Input flags
    private boolean up, down, left, right;

    private long lastTime;

    // =============================================================
    // INITIALIZE
    // =============================================================
    @FXML
    public void initialize() {

        gc = canvas.getGraphicsContext2D();

        player = new Player(64 * 15, 64 * 14);
        map = new TileMap("map1.txt");

        // Bind input setelah Scene siap
        bindSceneForInput();

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
                        case W -> up = true;
                        case S -> down = true;
                        case A -> left = true;
                        case D -> right = true;
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

        AnimationTimer loop = new AnimationTimer() {
            @Override
            public void handle(long now) {

                double dt = (now - lastTime) / 1e9;
                if (dt > 0.03) dt = 0.03; // stabilizer
                lastTime = now;

                update(dt);
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
}
