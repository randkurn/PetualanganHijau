package controller;

import java.awt.Graphics2D;
import view.AudioManager;
import model.Settings;

public class StateManager {
    public enum gameState {
        PLAY(0), PAUSE(1), WIN(2), TITLE(4), SETTINGS(5), CREDITS(6),
        STORY(8), SAVE_LOAD(9), CHARACTER_NAME(10), MAP(11),
        INVENTORY(12), TELEPORT(13), CUTSCENE(14), DIALOGUE(15), HELP(16), SCENE(17);

        private int value;

        private gameState(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    gameState currState, prevState;
    AudioManager audio;
    Settings settings;
    GamePanel gp;

    public StateManager(GamePanel gp) {
        this.gp = gp;
        this.audio = AudioManager.getInstance();
        this.settings = Settings.getInstance();
        this.currState = gameState.TITLE;
        audio.playMenuMusic();
    }

    public void setCurrentState(gameState state) {
        if (state == currState)
            return;
        prevState = currState;
        currState = state;

        if (gp.inputM != null && gp.inputM.getCurrentInput() != null) {
            gp.inputM.getCurrentInput().resetKeyPress();
        }

        gp.inputM.changeInput(state);

        switch (state) {
            case WIN:
                audio.playMusic(1);
                int score = calculateScore();
                gp.player.score = score;
                if (gp.player.score > settings.getHighScore()) {
                    settings.setHighScore(gp.player.score);
                    gp.uiM.showNewHighScore(true);
                } else {
                    gp.uiM.showNewHighScore(false);
                }
                break;
            case TITLE:
                audio.playMusic(0);
                break;
            default:
                break;
        }
        gp.uiM.resetSelectorPosition();
    }

    public void retryGame() {
        gp.mapM.resetMap();
        gp.uiM.resetPlayScreen();
        gp.player.setDefaultValues();
        setCurrentState(gameState.PLAY);
        audio.playMusic(0);
    }

    public gameState getCurrentState() {
        return currState;
    }

    public gameState getPreviousState() {
        return prevState;
    }

    public void revertPreviousState() {
        gameState temp = currState;
        currState = prevState;
        prevState = temp;
        gp.inputM.changeInput(currState);
    }

    public void draw(Graphics2D g2) {
        if (currState == gameState.SCENE) {
            gp.sceneM.draw(g2);
        } else if (currState == gameState.PLAY || currState == gameState.PAUSE ||
                currState == gameState.MAP || currState == gameState.INVENTORY ||
                currState == gameState.TELEPORT || currState == gameState.CUTSCENE ||
                currState == gameState.DIALOGUE) {

            gp.mapM.draw(g2);
            if (gp.npcM != null)
                gp.npcM.draw(g2);
            gp.player.draw(g2);
            if (gp.envM != null)
                gp.envM.draw(g2);
            TimeManager.getInstance().drawOverlay(g2, gp.screenWidth, gp.screenHeight);
            if (currState == gameState.CUTSCENE || currState == gameState.DIALOGUE)
                gp.csM.draw(g2);
        }
        gp.uiM.draw(g2);
    }

    public void update() {
        if (gp.isSleeping() || gp.isExhausted() || gp.isPlanting()) {
            return;
        }

        if (currState == gameState.SCENE) {
            gp.sceneM.update();
        } else if (currState == gameState.PLAY) {
            gp.player.update();
            TimeManager.getInstance().update();
            if (gp.npcM != null)
                gp.npcM.update();
            if (gp.envM != null)
                gp.envM.update();
            gp.csM.update();
        } else if (currState == gameState.CUTSCENE || currState == gameState.DIALOGUE) {
            gp.player.update();
            if (gp.npcM != null)
                gp.npcM.update();
            gp.csM.update();
        }
    }

    public int calculateScore() {
        int score = gp.player.score;
        int playTime = gp.uiM.getPlayTime();
        double multiplier = (playTime <= 900) ? (2.0 - (playTime / 900.0)) : 1.0;
        return (int) (score * multiplier);
    }
}
