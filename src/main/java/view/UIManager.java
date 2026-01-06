package view;

import java.awt.Graphics2D;
import controller.GamePanel;
import controller.StateManager.gameState;
import model.Settings;

public class UIManager {
    Settings settings;
    GamePanel gp;
    UI ui[];
    boolean fullScreen;

    public UIManager(GamePanel gp) {
        this.gp = gp;
        settings = Settings.getInstance();

        ui = new UI[19];
        ui[0] = new PlayScreen(gp);
        ui[1] = new PauseScreen(gp);
        ui[2] = new WinScreen(gp);
        ui[3] = ui[0];
        ui[4] = new TitleScreen(gp);
        ui[5] = new SettingsScreen(gp);
        ui[6] = new CreditsScreen(gp);
        ui[7] = new StoryScreen(gp);
        ui[8] = ui[7];
        ui[9] = new SaveLoadScreen(gp);
        ui[10] = new CharacterNameScreen(gp);
        ui[11] = new MinimapScreen(gp);
        ui[12] = new AdvancedInventoryScreen(gp);
        ui[13] = new TeleportScreen(gp);
        ui[14] = ui[0];
        ui[15] = ui[0];
        ui[16] = new HelpScreen(gp);
        ui[17] = ui[0];
        ui[18] = new QuantityInputScreen(gp);

        fullScreen = settings.getFullScreen();

        choiceDialogBox = new ChoiceDialogBox(gp);
        enviroMeter = new EnviroMeter(gp);
        objectiveTracker = new ObjectiveTracker(gp);
    }

    private ChoiceDialogBox choiceDialogBox;
    private EnviroMeter enviroMeter;
    private ObjectiveTracker objectiveTracker;

    public void draw(Graphics2D g2) {
        gameState state = gp.stateM.getCurrentState();

        switch (state) {
            case PAUSE -> {
                ui[0].draw(g2);
                ui[1].draw(g2);
            }
            case DIALOGUE -> {
                ui[0].draw(g2);
            }
            case STORY -> ui[7].draw(g2);
            case QUANTITY_INPUT -> ui[18].draw(g2);
            case MAP -> {
                ui[0].draw(g2);
                ui[11].draw(g2);
            }
            case INVENTORY -> {
                ui[0].draw(g2);
                ui[12].draw(g2);
            }
            case TELEPORT -> {
                ui[0].draw(g2);
                ui[13].draw(g2);
            }
            case PLAY, CUTSCENE -> {
                ui[0].draw(g2);
                if (state == gameState.PLAY) {
                    MinimapScreen minimap = (MinimapScreen) ui[11];
                    if (!minimap.isFullscreen())
                        minimap.draw(g2);

                    // Draw EnviroMeter during Chapter 3
                    if (gp.chapter3Active) {
                        enviroMeter.draw(g2);
                    }

                    // Draw ObjectiveTracker (shows for all chapters)
                    if (objectiveTracker != null) {
                        objectiveTracker.draw(g2);
                    }
                }
            }
            default -> {
                int val = state.getValue();
                if (val >= 0 && val < ui.length && ui[val] != null)
                    ui[val].draw(g2);
            }
        }
    }

    public void moveSelectorUp() {
        ui[gp.stateM.getCurrentState().getValue()].moveSelectorUp();
    }

    public void moveSelectorDown() {
        ui[gp.stateM.getCurrentState().getValue()].moveSelectorDown();
    }

    public int getSelectorPosition() {
        return ui[gp.stateM.getCurrentState().getValue()].getSelectorPosition();
    }

    public void resetSelectorPosition() {
        ui[gp.stateM.getCurrentState().getValue()].resetSelectorPosition();
    }

    public boolean getFullScreen() {
        return fullScreen;
    }

    public void setFullScreen(boolean fullScreen) {
        this.fullScreen = fullScreen;
        settings.setFullScreen(fullScreen);
        settings.saveConfigFile();
    }

    public void resetPlayScreen() {
        PlayScreen ps = (PlayScreen) ui[0];
        ps.resetTimer();
        ps.resetMessage();
    }

    public void showMessage(String message) {
        ((PlayScreen) ui[0]).showMessage(message);
    }

    public void showNewHighScore(boolean showHighScore) {
        if (gp.stateM.getCurrentState() == gameState.WIN) {
            ((WinScreen) ui[2]).showNewHighScore(showHighScore);
        }
    }

    public int getPlayTime() {
        return ((PlayScreen) ui[0]).getPlayTime();
    }

    public PlayScreen getPlayScreen() {
        return (PlayScreen) ui[0];
    }

    public PlayScreen getDialogBox() {
        return (PlayScreen) ui[0];
    }

    public StoryScreen getStoryScreen() {
        return (StoryScreen) ui[7];
    }

    public SaveLoadScreen getSaveLoadScreen() {
        return (SaveLoadScreen) ui[9];
    }

    public CharacterNameScreen getCharacterNameScreen() {
        return (CharacterNameScreen) ui[10];
    }

    public MinimapScreen getMinimapScreen() {
        return (MinimapScreen) ui[11];
    }

    public AdvancedInventoryScreen getInventoryScreen() {
        return (AdvancedInventoryScreen) ui[12];
    }

    public TeleportScreen getTeleportScreen() {
        return (TeleportScreen) ui[13];
    }

    public void loadStory(String key) {
        getStoryScreen().loadStory(key);
    }

    public void openSaveScreen() {
        getSaveLoadScreen().setSaveMode(true);
    }

    public void openLoadScreen() {
        getSaveLoadScreen().setSaveMode(false);
    }

    public HelpScreen getHelpScreen() {
        return (HelpScreen) ui[16];
    }

    public ChoiceDialogBox getChoiceDialogBox() {
        return choiceDialogBox;
    }

    public QuantityInputScreen getQuantityInputScreen() {
        return (QuantityInputScreen) ui[18];
    }
}
