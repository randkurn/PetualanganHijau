package controller.state;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import controller.GamePanel;

public class PauseState extends AbstractGameState {

    @Override
    public String getStateName() {
        return "PAUSE";
    }

    @Override
    public void draw(GamePanel gp, Graphics2D g2) {
        if (gp.mapM != null) {
            gp.mapM.draw(g2);
        }
        if (gp.npcM != null) {
            gp.npcM.draw(g2);
        }
        if (gp.player != null) {
            gp.player.draw(g2);
        }
        if (gp.envM != null) {
            gp.envM.draw(g2);
        }

        if (gp.timeM != null) {
            gp.timeM.drawOverlay(g2, gp.screenWidth, gp.screenHeight);
        }

        if (gp.uiM != null) {
            gp.uiM.draw(g2);
        }
    }

    @Override
    public boolean handleKeyPress(GamePanel gp, int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                if (gp.uiM != null) {
                    gp.uiM.moveSelectorUp();
                }
                return true;

            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                if (gp.uiM != null) {
                    gp.uiM.moveSelectorDown();
                }
                return true;

            case KeyEvent.VK_ENTER:
            case KeyEvent.VK_SPACE:
                int sel = gp.uiM.getSelectorPosition();
                if (sel == 0) {
                    transitionTo(gp, GameStateType.PLAY);
                } else if (sel == 1) {
                    transitionTo(gp, GameStateType.SETTINGS);
                } else if (sel == 2) {
                    System.out.println("[PauseState] Going to Main Menu directly");
                    transitionTo(gp, GameStateType.TITLE);
                } else if (sel == 3) {
                    System.exit(0);
                }
                return true;

            case KeyEvent.VK_ESCAPE:
                transitionTo(gp, GameStateType.PLAY);
                return true;
        }
        return false;
    }
}
