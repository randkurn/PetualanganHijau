package model;

import javafx.scene.image.Image;

public class Player {

    private double x, y;
    private double vx, vy;

    private int dir = 0;   // 0=down,1=left,2=right,3=up
    private int frame = 0;

    private double timer = 0;

    private Image[][] frames; // [direction][frame]

    private String name = "";

    public Player(double x, double y) {
        this.x = x;
        this.y = y;
        loadSprites();
    }

    // =============================================================
    // LOAD SPRITES
    // =============================================================
    private void loadSprites() {

        frames = new Image[4][2];

        frames[0][0] = new Image(getClass().getResourceAsStream("/assets/character/down_0.png"));
        frames[0][1] = new Image(getClass().getResourceAsStream("/assets/character/down_1.png"));

        frames[1][0] = new Image(getClass().getResourceAsStream("/assets/character/left_0.png"));
        frames[1][1] = new Image(getClass().getResourceAsStream("/assets/character/left_1.png"));

        frames[2][0] = new Image(getClass().getResourceAsStream("/assets/character/right_0.png"));
        frames[2][1] = new Image(getClass().getResourceAsStream("/assets/character/right_1.png"));

        frames[3][0] = new Image(getClass().getResourceAsStream("/assets/character/up_0.png"));
        frames[3][1] = new Image(getClass().getResourceAsStream("/assets/character/up_1.png"));
    }

    // =============================================================
    // INPUT HANDLER
    // =============================================================
    public void handleInput(boolean up, boolean down, boolean left, boolean right) {

        double speed = 200;

        vx = 0;
        vy = 0;

        if (up)    vy = -speed;
        if (down)  vy =  speed;
        if (left)  vx = -speed;
        if (right) vx =  speed;
    }

    // =============================================================
    // UPDATE PLAYER
    // =============================================================
    public void update(double dt, TileMap map) {

        double newX = x + vx * dt;
        double newY = y + vy * dt;

        int tileSize = map.getTileSize();

        int col = (int)(newX / tileSize);
        int row = (int)(newY / tileSize);

        // collision check
int playerSize = 64; // ukuran sprite

// Coba geser X dulu
if (!map.isBlocked(newX, y, playerSize)) {
    x = newX;
}

// Coba geser Y
if (!map.isBlocked(x, newY, playerSize)) {
    y = newY;
}

        // direction setting
        if (vy > 0) dir = 0;
        else if (vy < 0) dir = 3;
        else if (vx < 0) dir = 1;
        else if (vx > 0) dir = 2;

        // animation
        if (vx != 0 || vy != 0) {
            timer += dt;
            if (timer > 0.2) {
                frame = (frame + 1) % 2;
                timer = 0;
            }
        } else {
            frame = 0;
        }
    }

    // =============================================================
    // GETTERS
    // =============================================================
    public Image getCurrentFrame() {
        return frames[dir][frame];
    }

    public double getX() { return x; }
    public double getY() { return y; }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
