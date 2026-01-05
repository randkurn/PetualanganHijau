package controller.state;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import controller.GamePanel;

public class CutsceneState extends AbstractGameState {

    @Override
    public String getStateName() {
        return "CUTSCENE";
    }

    @Override
    public void onEnter(GamePanel gp) {
        super.onEnter(gp);

        // Block all input initially
        inputEnabled = true;

        if (gp.inputM != null && gp.inputM.getCurrentInput() != null) {
            gp.inputM.getCurrentInput().resetKeyPress();
        }
    }

    @Override
    public void update(GamePanel gp) {
        if (gp.csM != null) {
            gp.csM.update();

            // Check if cutscene finished
            if (gp.csM.isFinished()) {
                transitionTo(gp, GameStateType.PLAY);
            }
        }
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

        if (gp.csM != null) {
            gp.csM.draw(g2);
        }

        if (gp.uiM != null) {
            gp.uiM.draw(g2);
        }
    }

    @Override
    public boolean handleKeyPress(GamePanel gp, int keyCode) {
        boolean canSkip = (gp.csM != null && gp.csM.canSkip());

        switch (keyCode) {
            case KeyEvent.VK_ESCAPE:
            case KeyEvent.VK_SPACE:
            case KeyEvent.VK_ENTER:
                if (canSkip) {
                    if (gp.csM != null) {
                        gp.csM.skip();
                    }
                    transitionTo(gp, GameStateType.PLAY);
                }
                return true;

            default:
                return true;
        }
    }

    @Override
    public boolean handleKeyRelease(GamePanel gp, int keyCode) {
        return true;
    }
}
