package controller;

import java.awt.Color;
import java.awt.Graphics2D;

public class EnvironmentManager {
    private static EnvironmentManager instance;
    private GamePanel gp;

    private float scoreCleanliness = 100.0f;
    private float scoreHappiness = 100.0f;

    private Color filterColor = new Color(0, 0, 0, 0);

    private EnvironmentManager() {
    }

    public static EnvironmentManager getInstance() {
        if (instance == null) {
            instance = new EnvironmentManager();
        }
        return instance;
    }

    public void setGamePanel(GamePanel gp) {
        this.gp = gp;
    }

    public void update() {
        if (gp != null && gp.player != null) {
            int collected = gp.player.getCollectedTrash().size();
            int total = gp.player.getTotalTrashInWorld();

            if (total > 0) {
                scoreCleanliness = (float) collected / total * 100.0f;
            } else {
                scoreCleanliness = 0.0f;
            }
        }

        if (scoreCleanliness < 50) {
            int alpha = (int) ((50 - scoreCleanliness) * 2);
            filterColor = new Color(50, 50, 0, alpha);
        } else {
            filterColor = new Color(0, 0, 0, 0);
        }
    }

    public void draw(Graphics2D g2) {
        if (filterColor.getAlpha() > 0) {
            g2.setColor(filterColor);
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        }
    }

    public void changeCleanliness(float delta) {
        scoreCleanliness += delta;
        if (scoreCleanliness > 100)
            scoreCleanliness = 100;
        if (scoreCleanliness < 0)
            scoreCleanliness = 0;
    }

    public void changeHappiness(float delta) {
        scoreHappiness += delta;
        if (scoreHappiness > 100)
            scoreHappiness = 100;
        if (scoreHappiness < 0)
            scoreHappiness = 0;
    }

    public float getScoreCleanliness() {
        return scoreCleanliness;
    }

    public float getScoreHappiness() {
        return scoreHappiness;
    }
}
