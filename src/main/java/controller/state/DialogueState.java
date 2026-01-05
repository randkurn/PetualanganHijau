package controller.state;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import controller.GamePanel;

public class DialogueState extends AbstractGameState {

    @Override
    public String getStateName() {
        return "DIALOGUE";
    }

    @Override
    public void onEnter(GamePanel gp) {
        super.onEnter(gp);

        if (gp.inputM != null && gp.inputM.getCurrentInput() != null) {
            gp.inputM.getCurrentInput().resetKeyPress();
        }

        if (gp.timeM != null) {
            gp.timeM.setPaused(true);
        }
    }

    @Override
    public void onExit(GamePanel gp) {
        super.onExit(gp);

        if (gp.timeM != null) {
            gp.timeM.setPaused(false);
        }
    }

    @Override
    public void update(GamePanel gp) {
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
            case KeyEvent.VK_ENTER:
            case KeyEvent.VK_SPACE:
            case KeyEvent.VK_E:
                if (gp.uiM != null && gp.uiM.getDialogBox() != null) {
                    if (gp.uiM.getDialogBox().hasChoices()) {
                        gp.uiM.getDialogBox().confirmChoice();
                    } else {
                        gp.uiM.getDialogBox().advanceDialog();
                    }
                }
                return true;

            case KeyEvent.VK_ESCAPE:
                if (gp.uiM != null && gp.uiM.getDialogBox() != null) {
                    gp.uiM.getDialogBox().hideDialog();
                }
                return true;

            case KeyEvent.VK_W:
            case KeyEvent.VK_A:
            case KeyEvent.VK_S:
            case KeyEvent.VK_D:
            case KeyEvent.VK_UP:
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_RIGHT:
                return true;

            case KeyEvent.VK_I:
            case KeyEvent.VK_M:
                return true;

            default:
                return false;
        }
    }

    @Override
    public boolean handleKeyRelease(GamePanel gp, int keyCode) {
        return true;
    }
}
