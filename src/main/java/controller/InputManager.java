package controller;

import java.awt.event.KeyEvent;
import controller.StateManager.gameState;
import view.AdvancedInventoryScreen;
import model.Settings;

public class InputManager {
    GamePanel gp;
    InputHandler[] inputs;

    InputHandler currInput, prevInput;

    public InputManager(GamePanel gp) {
        this.gp = gp;

        inputs = new InputHandler[18];
        inputs[0] = new PlayInput(gp);
        inputs[1] = new PauseInput(gp);
        inputs[3] = inputs[2];
        inputs[4] = new TitleInput(gp);
        inputs[5] = new SettingsInput(gp);
        inputs[6] = new CreditsInput(gp);
        inputs[7] = inputs[4];
        inputs[8] = new StoryInput(gp);
        inputs[9] = new SaveLoadInput(gp);
        inputs[10] = new CharacterNameInput(gp);
        inputs[11] = new MapInput(gp);
        inputs[12] = new InventoryInput(gp);
        inputs[13] = new TeleportInput(gp);
        inputs[14] = inputs[0];
        inputs[15] = new DialogueInput(gp);
        inputs[16] = new HelpInput(gp);
        inputs[17] = new SceneInput(gp);

        currInput = inputs[4];
    }

    public void changeInput(gameState state) {
        prevInput = currInput;
        currInput = inputs[state.getValue()];
        currInput.resetKeyPress();

        gp.removeKeyListener(prevInput);
        gp.addKeyListener(currInput);
    }

    public InputHandler getCurrentInput() {
        return currInput;
    }

    public PlayInput getPlayInput() {
        return (PlayInput) inputs[0];
    }

    public static class TitleInput extends InputHandler {
        protected TitleInput(GamePanel gp) {
            super(gp);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();
            switch (keyCode) {
                case KeyEvent.VK_W:
                case KeyEvent.VK_UP:
                    if (!select) {
                        audio.playSound(4);
                        gp.uiM.moveSelectorUp();
                    }
                    select = true;
                    break;
                case KeyEvent.VK_S:
                case KeyEvent.VK_DOWN:
                    if (!select) {
                        audio.playSound(4);
                        gp.uiM.moveSelectorDown();
                    }
                    select = true;
                    break;
                case KeyEvent.VK_ENTER:
                    if (!enter) {
                        if (gp.ignoreNextEnterOnTitle) {
                            gp.ignoreNextEnterOnTitle = false;
                            enter = true;
                            break;
                        }
                        int position = gp.uiM.getSelectorPosition();
                        audio.playSound(5);
                        SaveManager saveM = SaveManager.getInstance();
                        if (position == 0) {
                            resetKeyPress();
                            gp.uiM.getCharacterNameScreen().reset();
                            gp.stateM.setCurrentState(gameState.CHARACTER_NAME);
                        } else if (position == 1) {
                            if (saveM.hasSaveFile()) {
                                resetKeyPress();
                                gp.uiM.openLoadScreen();
                                gp.stateM.setCurrentState(gameState.SAVE_LOAD);
                            }
                        } else if (position == 2) {
                            resetKeyPress();
                            gp.uiM.getHelpScreen().reset();
                            gp.stateM.setCurrentState(gameState.HELP);
                        } else if (position == 3) {
                            resetKeyPress();
                            gp.stateM.setCurrentState(gameState.SETTINGS);
                        } else if (position == 4) {
                            resetKeyPress();
                            gp.stateM.setCurrentState(gameState.CREDITS);
                        } else if (position == 5) {
                            System.exit(0);
                        }
                    }
                    enter = true;
                    break;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            int keyCode = e.getKeyCode();
            switch (keyCode) {
                case KeyEvent.VK_W:
                case KeyEvent.VK_UP:
                case KeyEvent.VK_S:
                case KeyEvent.VK_DOWN:
                    select = false;
                    break;
                case KeyEvent.VK_ENTER:
                    enter = false;
                    break;
            }
        }
    }

    public static class PauseInput extends InputHandler {
        protected PauseInput(GamePanel gp) {
            super(gp);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();
            switch (keyCode) {
                case KeyEvent.VK_ESCAPE:
                    if (!paused) {
                        audio.playSound(5);
                        gp.stateM.setCurrentState(gameState.PLAY);
                    }
                    paused = true;
                    break;
                case KeyEvent.VK_W:
                case KeyEvent.VK_UP:
                    if (!select) {
                        audio.playSound(4);
                        gp.uiM.moveSelectorUp();
                    }
                    select = true;
                    break;
                case KeyEvent.VK_S:
                case KeyEvent.VK_DOWN:
                    if (!select) {
                        audio.playSound(4);
                        gp.uiM.moveSelectorDown();
                    }
                    select = true;
                    break;
                case KeyEvent.VK_ENTER:
                    if (!enter) {
                        int position = gp.uiM.getSelectorPosition();
                        audio.playSound(5);
                        if (position == 0) {
                            gp.stateM.setCurrentState(gameState.PLAY);
                        } else if (position == 1) {
                            gp.uiM.getSaveLoadScreen().setSaveMode(true);
                            gp.stateM.setCurrentState(gameState.SAVE_LOAD);
                        } else if (position == 2) {
                            gp.stateM.setCurrentState(gameState.SETTINGS);
                        } else if (position == 3) {
                            System.out.println("[PauseInput] Returning to Title langsung");
                            gp.stateM.setCurrentState(gameState.TITLE);
                        } else if (position == 4) {
                            System.exit(0);
                        }
                    }
                    enter = true;
                    break;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            int keyCode = e.getKeyCode();
            switch (keyCode) {
                case KeyEvent.VK_W:
                case KeyEvent.VK_UP:
                case KeyEvent.VK_S:
                case KeyEvent.VK_DOWN:
                    select = false;
                    break;
                case KeyEvent.VK_ENTER:
                    enter = false;
                    break;
                case KeyEvent.VK_ESCAPE:
                    paused = false;
                    break;
            }
        }
    }

    public static class SettingsInput extends InputHandler {
        private Settings settings;

        protected SettingsInput(GamePanel gp) {
            super(gp);
            settings = Settings.getInstance();
        }

        @Override
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();
            int position = gp.uiM.getSelectorPosition();
            switch (keyCode) {
                case KeyEvent.VK_ESCAPE:
                    audio.playSound(5);
                    gp.stateM.revertPreviousState();
                    break;
                case KeyEvent.VK_W:
                case KeyEvent.VK_UP:
                    if (!select) {
                        audio.playSound(4);
                        gp.uiM.moveSelectorUp();
                    }
                    select = true;
                    break;
                case KeyEvent.VK_S:
                case KeyEvent.VK_DOWN:
                    if (!select) {
                        audio.playSound(4);
                        gp.uiM.moveSelectorDown();
                    }
                    select = true;
                    break;
                case KeyEvent.VK_A:
                case KeyEvent.VK_LEFT:
                    if (position == 0) {
                        audio.lowerVolume();
                        audio.checkVolume();
                        settings.setMusicVolume(audio.getVolumeScale());
                        audio.playSound(4);
                    } else if (position == 1) {
                        audio.lowerVolume();
                        audio.checkVolume();
                        settings.setSoundVolume(audio.getVolumeScale());
                        audio.playSound(4);
                    } else if (position == 2) {
                    } else if (position == 3) {
                        audio.playSound(4);
                        String current = settings.getGameLanguage();
                        String next = current.equals("id") ? "en" : "id";
                        settings.setGameLanguage(next);
                        settings.setStoryLanguage(next);
                    }
                    break;
                case KeyEvent.VK_D:
                case KeyEvent.VK_RIGHT:
                    if (position == 0) {
                        audio.increaseVolume();
                        audio.checkVolume();
                        settings.setMusicVolume(audio.getVolumeScale());
                        audio.playSound(4);
                    } else if (position == 1) {
                        audio.increaseVolume();
                        audio.checkVolume();
                        settings.setSoundVolume(audio.getVolumeScale());
                        audio.playSound(4);
                    } else if (position == 2) {
                    } else if (position == 3) {
                        audio.playSound(4);
                        String current = settings.getGameLanguage();
                        String next = current.equals("id") ? "en" : "id";
                        settings.setGameLanguage(next);
                        settings.setStoryLanguage(next);
                    }
                    break;
                case KeyEvent.VK_ENTER:
                    if (!enter) {
                        if (position == 2) {
                            audio.playSound(5);
                            if (!gp.uiM.getFullScreen()) {
                                gp.uiM.setFullScreen(true);
                                gp.setFullScreen();
                            } else {
                                gp.uiM.setFullScreen(false);
                                gp.setWindowScreen();
                            }
                        } else if (position == 3) {
                            audio.playSound(5);
                            String current = settings.getGameLanguage();
                            String next = current.equals("id") ? "en" : "id";
                            settings.setGameLanguage(next);
                            settings.setStoryLanguage(next);
                        } else if (position == 4) {
                            audio.playSound(5);
                            gp.stateM.revertPreviousState();
                        }
                    }
                    enter = true;
                    break;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            int keyCode = e.getKeyCode();
            switch (keyCode) {
                case KeyEvent.VK_W:
                case KeyEvent.VK_UP:
                case KeyEvent.VK_S:
                case KeyEvent.VK_DOWN:
                    select = false;
                    break;
                case KeyEvent.VK_ENTER:
                    enter = false;
                    break;
            }
        }
    }

    public static class CreditsInput extends InputHandler {
        protected CreditsInput(GamePanel gp) {
            super(gp);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                audio.playSound(5);
                gp.stateM.setCurrentState(gameState.TITLE);
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }
    }

    public static class HelpInput extends InputHandler {
        protected HelpInput(GamePanel gp) {
            super(gp);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();
            switch (keyCode) {
                case KeyEvent.VK_ESCAPE:
                    audio.playSound(5);
                    gp.stateM.setCurrentState(gameState.TITLE);
                    break;
                case KeyEvent.VK_A:
                case KeyEvent.VK_LEFT:
                    if (!left) {
                        gp.uiM.getHelpScreen().previousPage();
                    }
                    left = true;
                    break;
                case KeyEvent.VK_D:
                case KeyEvent.VK_RIGHT:
                    if (!right) {
                        gp.uiM.getHelpScreen().nextPage();
                    }
                    right = true;
                    break;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            int keyCode = e.getKeyCode();
            switch (keyCode) {
                case KeyEvent.VK_A:
                case KeyEvent.VK_LEFT:
                    left = false;
                    break;
                case KeyEvent.VK_D:
                case KeyEvent.VK_RIGHT:
                    right = false;
                    break;
            }
        }
    }

    public static class StoryInput extends InputHandler {
        protected StoryInput(GamePanel gp) {
            super(gp);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();
            switch (keyCode) {
                case KeyEvent.VK_SPACE -> {
                    if (!enter) {
                        if (gp.uiM.getStoryScreen().isLastCharacterTyped()) {
                            if (!gp.uiM.getStoryScreen().isLastParagraph()) {
                                gp.uiM.getStoryScreen().nextParagraph();
                            }
                        } else {
                            gp.uiM.getStoryScreen().skipTypewriter();
                        }
                    }
                    enter = true;
                }
                case KeyEvent.VK_UP, KeyEvent.VK_W -> {
                    if (!up)
                        gp.uiM.getStoryScreen().scrollUp();
                    up = true;
                }
                case KeyEvent.VK_DOWN, KeyEvent.VK_S -> {
                    if (!down)
                        gp.uiM.getStoryScreen().scrollDown();
                    down = true;
                }
                case KeyEvent.VK_ENTER -> {
                    if (!enter && gp.uiM.getStoryScreen().isLastParagraph()) {
                        audio.playSound(5);
                        if (gp.uiM.getStoryScreen().isStoryLoaded("prolog"))
                            gp.uiM.loadStory("chapter1");
                        else
                            startChapter1FromStory();
                    }
                    enter = true;
                }
                case KeyEvent.VK_ESCAPE -> {
                    if (!paused) {
                        audio.playSound(5);
                        startChapter1FromStory();
                    }
                    paused = true;
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_SPACE:
                case KeyEvent.VK_ENTER:
                    enter = false;
                    break;
                case KeyEvent.VK_UP:
                case KeyEvent.VK_W:
                    up = false;
                    break;
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_S:
                    down = false;
                    break;
                case KeyEvent.VK_ESCAPE:
                    paused = false;
                    break;
            }
        }

        private void startChapter1FromStory() {
            gp.uiM.getStoryScreen().setShowTitleCard(true);
        }
    }

    public static class SaveLoadInput extends InputHandler {
        protected SaveLoadInput(GamePanel gp) {
            super(gp);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();
            switch (keyCode) {
                case KeyEvent.VK_W:
                case KeyEvent.VK_UP:
                    if (!select) {
                        audio.playSound(4);
                        gp.uiM.moveSelectorUp();
                    }
                    select = true;
                    break;
                case KeyEvent.VK_S:
                case KeyEvent.VK_DOWN:
                    if (!select) {
                        audio.playSound(4);
                        gp.uiM.moveSelectorDown();
                    }
                    select = true;
                    break;
                case KeyEvent.VK_ENTER:
                    if (!enter) {
                        int slot = gp.uiM.getSelectorPosition();
                        audio.playSound(5);
                        SaveManager saveM = SaveManager.getInstance();
                        saveM.setCurrentSlot(slot);
                        if (gp.uiM.getSaveLoadScreen().isSaveMode()) {
                            saveM.saveGame(gp, slot);
                            gp.uiM.showMessage("Game saved to slot " + (slot + 1) + "!");
                            if (gp.returnToTitleAfterSave) {
                                gp.ignoreNextEnterOnTitle = true;
                                gp.returnToTitleAfterSave = false;
                                gp.stateM.setCurrentState(gameState.TITLE);
                            } else if (gp.triggerSleepAfterSave) {
                                gp.triggerSleepAfterSave = false;
                                gp.triggerSleep();
                            } else {
                                gp.stateM.setCurrentState(gameState.PAUSE);
                            }
                        } else {
                            if (saveM.hasSaveFile(slot)) {
                                saveM.loadGame(gp, slot);
                                controller.StoryManager.getInstance().setPlayerName(gp.player.getPlayerName());
                                gp.stateM.setCurrentState(gameState.PLAY);
                            }
                        }
                    }
                    enter = true;
                    break;
                case KeyEvent.VK_ESCAPE:
                    if (!paused) {
                        audio.playSound(5);
                        if (gp.uiM.getSaveLoadScreen().isSaveMode()) {
                            if (gp.returnToTitleAfterSave) {
                                gp.ignoreNextEnterOnTitle = true;
                                gp.returnToTitleAfterSave = false;
                                gp.stateM.setCurrentState(gameState.TITLE);
                            } else if (gp.triggerSleepAfterSave) {
                                gp.triggerSleepAfterSave = false;
                                gp.stateM.setCurrentState(gameState.PLAY);
                            } else {
                                gp.stateM.setCurrentState(gameState.PAUSE);
                            }
                        } else {
                            gp.stateM.setCurrentState(gameState.TITLE);
                        }
                    }
                    paused = true;
                    break;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_W:
                case KeyEvent.VK_UP:
                case KeyEvent.VK_S:
                case KeyEvent.VK_DOWN:
                    select = false;
                    break;
                case KeyEvent.VK_ENTER:
                    enter = false;
                    break;
                case KeyEvent.VK_ESCAPE:
                    paused = false;
                    break;
            }
        }
    }

    public static class CharacterNameInput extends InputHandler {
        protected CharacterNameInput(GamePanel gp) {
            super(gp);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();
            switch (keyCode) {
                case KeyEvent.VK_ENTER:
                    if (!enter) {
                        if (gp.uiM.getCharacterNameScreen().isNameValid()) {
                            audio.playSound(5);
                            String name = gp.uiM.getCharacterNameScreen().getName();
                            gp.player.setPlayerName(name);
                            controller.StoryManager.getInstance().setPlayerName(name);
                            gp.uiM.loadStory("prolog");
                            gp.stateM.setCurrentState(gameState.STORY);
                        } else {
                            audio.playSound(11);
                        }
                    }
                    enter = true;
                    break;
                case KeyEvent.VK_BACK_SPACE:
                    if (!enter) {
                        gp.uiM.getCharacterNameScreen().removeCharacter();
                        audio.playSound(4);
                    }
                    enter = true;
                    break;
                case KeyEvent.VK_ESCAPE:
                    if (!paused) {
                        gp.stateM.setCurrentState(gameState.TITLE);
                    }
                    paused = true;
                    break;
                default:
                    char c = e.getKeyChar();
                    if (Character.isLetterOrDigit(c) || c == ' ') {
                        gp.uiM.getCharacterNameScreen().addCharacter(c);
                    }
                    break;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_ENTER:
                case KeyEvent.VK_BACK_SPACE:
                    enter = false;
                    break;
                case KeyEvent.VK_ESCAPE:
                    paused = false;
                    break;
            }
        }
    }

    public static class MapInput extends InputHandler {
        protected MapInput(GamePanel gp) {
            super(gp);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();
            switch (keyCode) {
                case KeyEvent.VK_M:
                case KeyEvent.VK_ESCAPE:
                    if (!enter) {
                        audio.playSound(5);
                        gp.uiM.getMinimapScreen().setFullscreen(false);
                        gp.stateM.revertPreviousState();
                    }
                    enter = true;
                    break;
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_A:
                    if (!left) {
                        audio.playSound(4);
                    }
                    left = true;
                    break;
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_D:
                    if (!right) {
                        audio.playSound(4);
                    }
                    right = true;
                    break;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_M:
                case KeyEvent.VK_ESCAPE:
                    enter = false;
                    break;
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_A:
                    left = false;
                    break;
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_D:
                    right = false;
                    break;
            }
        }
    }

    public static class InventoryInput extends InputHandler {
        public InventoryInput(GamePanel gp) {
            super(gp);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            int code = e.getKeyCode();
            AdvancedInventoryScreen inventoryScreen = gp.uiM.getInventoryScreen();

            if (inventoryScreen.isSortingMode()) {
                handleSortingModeInput(code, inventoryScreen);
            } else {
                handleNormalInventoryInput(code, inventoryScreen);
            }
        }

        private void handleSortingModeInput(int code, AdvancedInventoryScreen inventoryScreen) {
            switch (code) {
                case KeyEvent.VK_1:
                    if (!select) {
                        inventoryScreen.selectBin(0);
                        audio.playSound(4);
                    }
                    select = true;
                    break;
                case KeyEvent.VK_2:
                    if (!select) {
                        inventoryScreen.selectBin(1);
                        audio.playSound(4);
                    }
                    select = true;
                    break;
                case KeyEvent.VK_3:
                    if (!select) {
                        inventoryScreen.selectBin(2);
                        audio.playSound(4);
                    }
                    select = true;
                    break;
                case KeyEvent.VK_ENTER:
                    if (!enter) {
                        inventoryScreen.sortSelectedItem();
                        audio.playSound(5);
                    }
                    enter = true;
                    break;
                case KeyEvent.VK_S:
                    if (!paused) {
                        inventoryScreen.toggleSortingMode();
                        audio.playSound(5);
                    }
                    paused = true;
                    break;
                case KeyEvent.VK_T:
                    if (!enter) {
                        // Exchange/Sell trash directly from sorting mode
                        inventoryScreen.exchangeTrashInSortingMode();
                    }
                    enter = true;
                    break;
                
                case KeyEvent.VK_UP:
                case KeyEvent.VK_W:
                    if (!select) {
                        inventoryScreen.moveSelection(0);
                        audio.playSound(4);
                    }
                    select = true;
                    break;
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_D:
                    if (!select) {
                        inventoryScreen.moveSelection(1);
                        audio.playSound(4);
                    }
                    select = true;
                    break;
                case KeyEvent.VK_DOWN:
                    if (!select) {
                        inventoryScreen.moveSelection(2);
                        audio.playSound(4);
                    }
                    select = true;
                    break;
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_A:
                    if (!select) {
                        inventoryScreen.moveSelection(3);
                        audio.playSound(4);
                    }
                    select = true;
                    break;
            }
        }

        private void handleNormalInventoryInput(int code, AdvancedInventoryScreen inventoryScreen) {
            switch (code) {
                case KeyEvent.VK_ESCAPE:
                case KeyEvent.VK_I:
                    if (!paused) {
                        gp.stateM.setCurrentState(gameState.PLAY);
                        audio.playSound(5);
                    }
                    paused = true;
                    break;
                case KeyEvent.VK_UP:
                case KeyEvent.VK_W:
                    if (!select) {
                        inventoryScreen.moveSelection(0);
                        audio.playSound(4);
                    }
                    select = true;
                    break;
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_D:
                    if (!select) {
                        inventoryScreen.moveSelection(1);
                        audio.playSound(4);
                    }
                    select = true;
                    break;
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_S:
                    if (!select) {
                        inventoryScreen.moveSelection(2);
                        audio.playSound(4);
                    }
                    select = true;
                    break;
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_A:
                    if (!select) {
                        inventoryScreen.moveSelection(3);
                        audio.playSound(4);
                    }
                    select = true;
                    break;
                case KeyEvent.VK_X:
                    if (!enter) {
                        inventoryScreen.discardSelectedItem();
                    }
                    enter = true;
                    break;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            int code = e.getKeyCode();
            switch (code) {
                case KeyEvent.VK_ESCAPE:
                case KeyEvent.VK_I:
                case KeyEvent.VK_S:
                    paused = false;
                    break;
                case KeyEvent.VK_UP:
                case KeyEvent.VK_W:
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_A:
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_D:
                    select = false;
                    break;
                case KeyEvent.VK_1:
                case KeyEvent.VK_2:
                case KeyEvent.VK_3:
                    select = false;
                    break;
                case KeyEvent.VK_ENTER:
                case KeyEvent.VK_T:
                    enter = false;
                    break;
            }
        }
    }

    public static class TeleportInput extends InputHandler {
        protected TeleportInput(GamePanel gp) {
            super(gp);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();
            switch (keyCode) {
                case KeyEvent.VK_W:
                case KeyEvent.VK_UP:
                    if (!up) {
                        gp.uiM.getTeleportScreen().moveUp();
                        audio.playSound(5);
                    }
                    up = true;
                    break;
                case KeyEvent.VK_S:
                case KeyEvent.VK_DOWN:
                    if (!down) {
                        gp.uiM.getTeleportScreen().moveDown();
                        audio.playSound(5);
                    }
                    down = true;
                    break;
                case KeyEvent.VK_ENTER:
                case KeyEvent.VK_SPACE:
                    if (!enter) {
                        gp.uiM.getTeleportScreen().teleportToSelected();
                        audio.playSound(3);
                    }
                    enter = true;
                    break;
                case KeyEvent.VK_ESCAPE:
                case KeyEvent.VK_T:
                    if (!paused) {
                        gp.stateM.setCurrentState(gameState.PLAY);
                        audio.playSound(5);
                    }
                    paused = true;
                    break;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            int keyCode = e.getKeyCode();
            switch (keyCode) {
                case KeyEvent.VK_W:
                case KeyEvent.VK_UP:
                    up = false;
                    break;
                case KeyEvent.VK_S:
                case KeyEvent.VK_DOWN:
                    down = false;
                    break;
                case KeyEvent.VK_ENTER:
                case KeyEvent.VK_SPACE:
                    enter = false;
                    break;
                case KeyEvent.VK_ESCAPE:
                case KeyEvent.VK_T:
                    paused = false;
                    break;
            }
        }
    }

    public static class DialogueInput extends InputHandler {
        protected DialogueInput(GamePanel gp) {
            super(gp);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();
            if (gp.uiM != null && gp.uiM.getDialogBox() != null && gp.uiM.getDialogBox().isActive()) {
                if (gp.uiM.getDialogBox().hasChoices()) {
                    switch (keyCode) {
                        case KeyEvent.VK_W, KeyEvent.VK_UP -> {
                            audio.playSound(4);
                            gp.uiM.getDialogBox().moveChoiceUp();
                        }
                        case KeyEvent.VK_S, KeyEvent.VK_DOWN -> {
                            audio.playSound(4);
                            gp.uiM.getDialogBox().moveChoiceDown();
                        }
                        case KeyEvent.VK_SPACE, KeyEvent.VK_ENTER -> {
                            audio.playSound(5);
                            gp.uiM.getDialogBox().confirmChoice();
                        }
                    }
                } else {
                    if (keyCode == KeyEvent.VK_SPACE || keyCode == KeyEvent.VK_ENTER || keyCode == KeyEvent.VK_E) {
                        audio.playSound(5);
                        gp.uiM.getDialogBox().advanceDialog();
                    }
                }
                e.consume();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }
    }

    public static class SceneInput extends InputHandler {
        protected SceneInput(GamePanel gp) {
            super(gp);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();

            if (gp.sceneM.isAwaitingChoice()) {
                switch (keyCode) {
                    case KeyEvent.VK_W:
                    case KeyEvent.VK_UP:
                        audio.playSound(4);
                        gp.sceneM.moveChoiceUp();
                        break;
                    case KeyEvent.VK_S:
                    case KeyEvent.VK_DOWN:
                        audio.playSound(4);
                        gp.sceneM.moveChoiceDown();
                        break;
                    case KeyEvent.VK_ENTER:
                    case KeyEvent.VK_SPACE:
                        audio.playSound(5);
                        gp.sceneM.confirmChoice();
                        break;
                    case KeyEvent.VK_ESCAPE:
                        audio.playSound(5);
                        gp.stateM.setCurrentState(gameState.PLAY);
                        break;
                }
            } else {
                if (keyCode == KeyEvent.VK_ENTER || keyCode == KeyEvent.VK_SPACE || keyCode == KeyEvent.VK_E) {
                    audio.playSound(5);
                    gp.sceneM.nextDialogue();
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }
    }
}
