package controller;

import java.awt.event.KeyEvent;
import controller.StateManager.gameState;
import view.AudioManager;

public class PlayInput extends InputHandler {

    protected PlayInput(GamePanel gp) {
        super(gp);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        AudioManager audio = AudioManager.getInstance();

        switch (keyCode) {
            case KeyEvent.VK_W, KeyEvent.VK_UP -> up = true;
            case KeyEvent.VK_A, KeyEvent.VK_LEFT -> left = true;
            case KeyEvent.VK_S, KeyEvent.VK_DOWN -> down = true;
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> right = true;
            case KeyEvent.VK_E -> interact = true;
            case KeyEvent.VK_SPACE -> toolUse = true;
            case KeyEvent.VK_ENTER -> enter = true;

            case KeyEvent.VK_ESCAPE -> {
                if (gp.stateM.getCurrentState() != gameState.CUTSCENE) {
                    audio.playSound(4);
                    gp.stateM.setCurrentState(gameState.PAUSE);
                }
            }
            case KeyEvent.VK_M -> {
                audio.playSound(4);
                gp.uiM.getMinimapScreen().setFullscreen(true);
                gp.stateM.setCurrentState(gameState.MAP);
            }
            case KeyEvent.VK_I -> {
                audio.playSound(4);
                gp.uiM.getInventoryScreen().reset();
                gp.stateM.setCurrentState(gameState.INVENTORY);
            }
            case KeyEvent.VK_T -> {
                audio.playSound(4);
                gp.uiM.getTeleportScreen().reset();
                gp.stateM.setCurrentState(gameState.TELEPORT);
            }
            case KeyEvent.VK_P -> {
                gp.player.plantTree();
            }
            case KeyEvent.VK_F2 -> {
                // Panic button to unlock movement
                gp.forceUnlock();
            }
            case KeyEvent.VK_F3 -> {
                // Toggle debug overlay
                gp.debugOverlay.toggle();
            }
            case KeyEvent.VK_G -> {
                // Give tree seeds (cheat/debug)
                java.awt.image.BufferedImage treeIcon = null;
                try {
                    treeIcon = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/objects/tree1.png"));
                } catch (Exception ex) {
                }

                if (!gp.player.inventory.hasItem("Bibit Pohon")) {
                    gp.player.inventory.addItem("Bibit Pohon", 10, treeIcon);
                    gp.uiM.showMessage("Diberikan 10 bibit pohon!");
                } else {
                    int current = gp.player.inventory.getItemCount("Bibit Pohon");
                    gp.player.inventory.addItem("Bibit Pohon", 5, treeIcon);
                    gp.uiM.showMessage("Ditambahkan 5 bibit pohon! Total: " + (current + 5));
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        switch (keyCode) {
            case KeyEvent.VK_W, KeyEvent.VK_UP -> up = false;
            case KeyEvent.VK_A, KeyEvent.VK_LEFT -> left = false;
            case KeyEvent.VK_S, KeyEvent.VK_DOWN -> down = false;
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> right = false;
            case KeyEvent.VK_E -> interact = false;
            case KeyEvent.VK_SPACE -> toolUse = false;
            case KeyEvent.VK_ENTER -> enter = false;
        }
    }
}
