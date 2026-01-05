package controller.state;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import controller.GamePanel;

public class PlayState extends AbstractGameState {

    @Override
    public String getStateName() {
        return "PLAY";
    }

    @Override
    public void onEnter(GamePanel gp) {
        super.onEnter(gp);
        if (gp.timeM != null) {
            gp.timeM.setPaused(false);
        }
    }

    @Override
    public void update(GamePanel gp) {
        gp.updateSleepTransition();

        if (gp.isSleeping())
            return;

        if (gp.player != null) {
            gp.player.update();
        }

        if (gp.timeM != null) {
            gp.timeM.update();
        }

        if (gp.npcM != null) {
            gp.npcM.update();
        }

        if (gp.envM != null) {
            gp.envM.update();
        }

        if (gp.csM != null) {
            gp.csM.update();
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

        if (gp.uiM != null) {
            gp.uiM.draw(g2);
        }

        gp.drawSleepTransition(g2);
    }

    @Override
    public boolean handleKeyPress(GamePanel gp, int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_ESCAPE:
                transitionTo(gp, GameStateType.PAUSE);
                return true;

            case KeyEvent.VK_I:
                transitionTo(gp, GameStateType.INVENTORY);
                return true;

            case KeyEvent.VK_M:
                transitionTo(gp, GameStateType.MAP);
                return true;

            case KeyEvent.VK_E:
            case KeyEvent.VK_ENTER:
                // Check interactive objects first
                int objIndex = gp.checker.checkObjectCollision(gp.player, true);
                if (objIndex != 999) {
                    model.GameObject obj = gp.mapM.getMap().objects[objIndex];
                    if (obj != null && (obj.type == model.GameObject.Type.PORTAL ||
                            obj.type == model.GameObject.Type.GATE ||
                            obj.type == model.GameObject.Type.BED)) {
                        obj.interact(gp);
                        return true;
                    }
                }

                // If no object, try NPCs
                if (gp.npcM != null) {
                    boolean interacted = gp.npcM.tryInteractWithNearbyNPC((int) (gp.tileSize * 1.5));
                    if (interacted) {
                        return true;
                    }
                }
                return false;

            default:
                return false;
        }
    }

    @Override
    public boolean handleKeyRelease(GamePanel gp, int keyCode) {
        return false;
    }
}
