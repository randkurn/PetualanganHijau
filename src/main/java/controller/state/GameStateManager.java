package controller.state;

import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import controller.GamePanel;

public class GameStateManager {

    private final GamePanel gamePanel;
    private final Map<GameStateType, GameState> states;
    private final Stack<GameStateType> stateHistory;

    private GameStateType currentStateType;
    private GameState currentState;

    private boolean isTransitioning = false;

    public GameStateManager(GamePanel gp) {
        this.gamePanel = gp;
        this.states = new HashMap<>();
        this.stateHistory = new Stack<>();

        initializeStates();

        // Set initial state
        setState(GameStateType.TITLE);
    }

    private void initializeStates() {
        // Note: Only states with separate files are registered

        states.put(GameStateType.PLAY, new PlayState());
        states.put(GameStateType.PAUSE, new PauseState());
        states.put(GameStateType.DIALOGUE, new DialogueState());
        states.put(GameStateType.CUTSCENE, new CutsceneState());

    }

    public void setState(GameStateType newStateType) {
        if (isTransitioning) {
            System.err.println("WARNING: Attempting to change state during transition!");
            return;
        }

        if (newStateType == currentStateType) {
            return;
        }

        isTransitioning = true;

        try {
            if (currentState != null) {
                currentState.onExit(gamePanel);
                stateHistory.push(currentStateType);
            }

            GameState newState = states.get(newStateType);
            if (newState == null) {
                System.err.println("ERROR: State not found: " + newStateType);
                isTransitioning = false;
                return;
            }

            GameStateType previousStateType = currentStateType;
            currentStateType = newStateType;
            currentState = newState;

            currentState.onEnter(gamePanel);

            if (gamePanel.inputM != null) {
                gamePanel.inputM.changeInput(convertToOldStateEnum(newStateType));
            }

            // Handle audio/music changes
            handleStateMusic(previousStateType, newStateType);

        } finally {
            isTransitioning = false;
        }
    }

    public void returnToPreviousState() {
        if (!stateHistory.isEmpty()) {
            GameStateType previousState = stateHistory.pop();
            setState(previousState);
        }
    }

    public GameStateType getCurrentStateType() {
        return currentStateType;
    }

    public GameState getCurrentState() {
        return currentState;
    }

    public boolean isInState(GameStateType stateType) {
        return currentStateType == stateType;
    }

    public boolean canPlayerMove() {
        return currentStateType != null && currentStateType.allowsPlayerMovement();
    }

    public void update() {
        if (currentState != null && !isTransitioning) {
            currentState.update(gamePanel);
        }
    }

    public void draw(Graphics2D g2) {
        if (currentState != null) {
            currentState.draw(gamePanel, g2);
        }
    }

    public boolean handleKeyPress(int keyCode) {
        if (currentState != null && currentState.canProcessInput()) {
            return currentState.handleKeyPress(gamePanel, keyCode);
        }
        return false;
    }

    public boolean handleKeyRelease(int keyCode) {
        if (currentState != null && currentState.canProcessInput()) {
            return currentState.handleKeyRelease(gamePanel, keyCode);
        }
        return false;
    }

    private void handleStateMusic(GameStateType from, GameStateType to) {
        if (gamePanel.stateM == null)
            return;

        view.AudioManager audio = view.AudioManager.getInstance();

        switch (to) {
            case WIN:
                audio.playMusic(1);
                break;
            case TITLE:
                audio.playMusic(0);
                break;
            default:
                break;
        }
    }

    private controller.StateManager.gameState convertToOldStateEnum(GameStateType newType) {
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
