package controller.state;

import java.awt.Graphics2D;
import controller.GamePanel;

public interface GameState {

    void onEnter(GamePanel gp);

    void onExit(GamePanel gp);

    void update(GamePanel gp);

    void draw(GamePanel gp, Graphics2D g2);

    boolean handleKeyPress(GamePanel gp, int keyCode);

    boolean handleKeyRelease(GamePanel gp, int keyCode);

    String getStateName();

    boolean canProcessInput();
}
