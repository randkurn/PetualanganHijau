package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Settings {
    private static Settings settingsInstance = null;

    ObjectMapper mapper;
    ConfigFile file;
    String directory;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ConfigFile {
        int musicVolume, soundVolume;
        Boolean fullScreen;
        int highScore;
        String gameLanguage;
        String storyLanguage;
        Boolean showMinimap;
        Boolean showUI;

        protected ConfigFile() {
            musicVolume = 3;
            soundVolume = 3;
            fullScreen = false;
            highScore = 0;
            gameLanguage = "id";
            storyLanguage = "id";
            showMinimap = true;
            showUI = true;
        }

        protected int getMusicVolume() {
            return musicVolume;
        }

        protected int getSoundVolume() {
            return soundVolume;
        }

        protected Boolean getFullScreen() {
            return fullScreen;
        }

        protected int getHighScore() {
            return highScore;
        }

        protected void setMusicVolume(int volume) {
            musicVolume = volume;
        }

        protected void setSoundVolume(int volume) {
            soundVolume = volume;
        }

        protected void setFullScreen(Boolean fullScreen) {
            this.fullScreen = fullScreen;
        }

        protected void setHighScore(int score) {
            highScore = score;
        }
    }

    private Settings() {
        mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);

        findConfigDirectory();

        try {
            file = mapper.readValue(new File(directory), ConfigFile.class);
        } catch (FileNotFoundException e) {
            createNewConfigFile();
            saveConfigFile();
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Settings getInstance() {
        if (settingsInstance == null) {
            settingsInstance = new Settings();
        }

        return settingsInstance;
    }

    private void createNewConfigFile() {
        file = new ConfigFile();
    }

    public void saveConfigFile() {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(directory), file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getMusicVolume() {
        return file.getMusicVolume();
    }

    public int getSoundVolume() {
        return file.getSoundVolume();
    }

    public Boolean getFullScreen() {
        return file.getFullScreen();
    }

    public int getHighScore() {
        return file.getHighScore();
    }

    public void setMusicVolume(int volume) {
        file.setMusicVolume(volume);
        saveConfigFile();
    }

    public void setSoundVolume(int volume) {
        file.setSoundVolume(volume);
        saveConfigFile();
    }

    public void setFullScreen(Boolean fullScreen) {
        file.setFullScreen(fullScreen);
        saveConfigFile();
    }

    public void setHighScore(int score) {
        file.setHighScore(score);
        saveConfigFile();
    }

    private void findConfigDirectory() {
        String os = (System.getProperty("os.name")).toUpperCase();

        if (os.contains("WIN")) {
            directory = System.getenv("AppData") + "\\UntitledFarmGame";
        } else if (os.contains("MAC")) {
            directory = System.getProperty("user.home") + "/Library/Application Support/UntitledFarmGame";
        } else if (os.contains("NUX")) {
            directory = System.getProperty("user.home") + "/UntitledFarmGame";
        }

        File directoryCheck = new File(directory);
        if (!directoryCheck.exists()) {
            directoryCheck.mkdir();
        }

        directory += "/settings.json";
    }

    public String getGameLanguage() {
        if (file.gameLanguage == null)
            file.gameLanguage = "id";
        return file.gameLanguage;
    }

    public String getStoryLanguage() {
        if (file.storyLanguage == null)
            file.storyLanguage = "id";
        return file.storyLanguage;
    }

    public void setGameLanguage(String lang) {
        file.gameLanguage = lang;
        controller.StoryManager.getInstance().setGameLanguage(lang);
        saveConfigFile();
    }

    public void setStoryLanguage(String lang) {
        file.storyLanguage = lang;
        controller.StoryManager.getInstance().setStoryLanguage(lang);
        saveConfigFile();
    }

    public boolean getShowMinimap() {
        if (file.showMinimap == null)
            file.showMinimap = true;
        return file.showMinimap;
    }

    public void setShowMinimap(boolean show) {
        file.showMinimap = show;
        saveConfigFile();
    }

    public boolean getShowUI() {
        if (file.showUI == null)
            file.showUI = true;
        return file.showUI;
    }

    public void setShowUI(boolean show) {
        file.showUI = show;
        saveConfigFile();
    }
}
