package controller.state;

import java.awt.Graphics2D;
import controller.GamePanel;

public abstract class AbstractGameState implements GameState {

    protected boolean inputEnabled = true;

    @Override
    public void onEnter(GamePanel gp) {
        inputEnabled = true;
    }

    @Override
    public void onExit(GamePanel gp) {
        inputEnabled = false;
    }

    @Override
    public void update(GamePanel gp) {
    }

    @Override
    public void draw(GamePanel gp, Graphics2D g2) {
    }

    @Override
    public boolean handleKeyPress(GamePanel gp, int keyCode) {
        return false;
    }

    @Override
    public boolean handleKeyRelease(GamePanel gp, int keyCode) {
        return false;
    }

    @Override
    public boolean canProcessInput() {
        return inputEnabled;
    }

    protected void transitionTo(GamePanel gp, GameStateType newState) {
        try {
            java.lang.reflect.Field field = gp.getClass().getField("gameStateManager");
            Object stateManager = field.get(gp);
            if (stateManager != null) {
                java.lang.reflect.Method method = stateManager.getClass().getMethod("setState", GameStateType.class);
                method.invoke(stateManager, newState);
                return;
            }
        } catch (Exception e) {
        }

        if (gp.stateM != null) {
            controller.StateManager.gameState oldState = convertToOldState(newState);
            if (oldState != null) {
                gp.stateM.setCurrentState(oldState);
            }
        }
    }

    private controller.StateManager.gameState convertToOldState(GameStateType newType) {
        return switch (newType) {
            case PLAY -> controller.StateManager.gameState.PLAY;
            case PAUSE -> controller.StateManager.gameState.PAUSE;
            case WIN -> controller.StateManager.gameState.WIN;
            case LOSE -> controller.StateManager.gameState.WIN;
            case TITLE -> controller.StateManager.gameState.TITLE;
            case SETTINGS -> controller.StateManager.gameState.SETTINGS;
            case CREDITS -> controller.StateManager.gameState.CREDITS;
            case STORY -> controller.StateManager.gameState.STORY;
            case SAVE_LOAD -> controller.StateManager.gameState.SAVE_LOAD;
            case CHARACTER_NAME -> controller.StateManager.gameState.CHARACTER_NAME;
            case MAP -> controller.StateManager.gameState.MAP;
            case INVENTORY -> controller.StateManager.gameState.INVENTORY;
            case TELEPORT -> controller.StateManager.gameState.TELEPORT;
            case CUTSCENE -> controller.StateManager.gameState.CUTSCENE;
            case DIALOGUE -> controller.StateManager.gameState.DIALOGUE;
        };
    }
}
