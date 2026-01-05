package controller.state;

public enum GameStateType {
    TITLE(0, false, true),
    SETTINGS(5, false, true),
    CREDITS(6, false, true),
    CHARACTER_NAME(10, false, true),

    SAVE_LOAD(9, false, true),

    STORY(8, false, false),

    PLAY(0, true, false),
    PAUSE(1, false, true),

    MAP(11, true, true),
    INVENTORY(12, true, true),
    TELEPORT(13, true, true),

    DIALOGUE(15, true, false),
    CUTSCENE(14, true, false),

    WIN(2, false, true),
    LOSE(3, false, true);

    private final int value;
    private final boolean showGameWorld;
    private final boolean pauseGameLogic;

    GameStateType(int value, boolean showGameWorld, boolean pauseGameLogic) {
        this.value = value;
        this.showGameWorld = showGameWorld;
        this.pauseGameLogic = pauseGameLogic;
    }

    public int getValue() {
        return value;
    }

    public boolean shouldShowGameWorld() {
        return showGameWorld;
    }

    public boolean shouldPauseGameLogic() {
        return pauseGameLogic;
    }

    public boolean allowsPlayerMovement() {
        return this == PLAY;
    }

    public boolean allowsEscapeKey() {
        return this != TITLE && this != WIN && this != LOSE && this != CHARACTER_NAME;
    }
}
