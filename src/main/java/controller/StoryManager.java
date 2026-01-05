package controller;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class StoryManager {
    private static StoryManager instance;
    private Map<String, StoryData> stories;
    private Map<String, String> dialogs;
    private String playerName = "Pemain";

    private String currentStoryLang = "id";
    private String currentGameLang = "id";

    public static StoryManager getInstance() {
        if (instance == null) {
            instance = new StoryManager();
        }
        return instance;
    }

    private StoryManager() {
        stories = new HashMap<>();
        dialogs = new HashMap<>();
        loadStories(currentStoryLang);
        loadDialogs(currentGameLang);
    }

    public void setPlayerName(String name) {
        this.playerName = name;
    }

    public void setStoryLanguage(String lang) {
        this.currentStoryLang = lang;
        loadStories(lang);
    }

    public void setGameLanguage(String lang) {
        this.currentGameLang = lang;
        loadDialogs(lang);
    }

    private void loadStories(String lang) {
        stories.clear();
        try (InputStream is = getClass().getResourceAsStream("/lang/story_" + lang + ".txt")) {
            if (is == null) {
                System.err.println("Story language file not found: " + lang + ". Falling back to id.");
                if (!lang.equals("id"))
                    loadStories("id");
                return;
            }
            parseStoryFile(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseStoryFile(InputStream is) {
        Scanner scanner = new Scanner(is);
        Map<String, String> rawData = new HashMap<>();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.contains(":")) {
                String[] parts = line.split(":", 2);
                if (parts.length > 1) {
                    rawData.put(parts[0].trim(), parts[1].trim());
                }
            }
        }
        scanner.close();

        if (rawData.containsKey("prolog_title")) {
            List<String> paras = new ArrayList<>();
            int i = 1;
            while (rawData.containsKey("prolog_" + i)) {
                paras.add(rawData.get("prolog_" + i));
                i++;
            }
            stories.put("prolog", new StoryData(rawData.get("prolog_title"), paras.toArray(new String[0]),
                    StoryData.StoryType.PROLOG));
        }
    }

    private void loadDialogs(String lang) {
        dialogs.clear();
        try (InputStream is = getClass().getResourceAsStream("/lang/dialog_" + lang + ".txt")) {
            if (is == null) {
                System.err.println("Dialog language file not found: " + lang + ". Falling back to id.");
                if (!lang.equals("id"))
                    loadDialogs("id");
                return;
            }
            Scanner scanner = new Scanner(is);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.contains(":")) {
                    String[] parts = line.split(":", 2);
                    if (parts.length > 1) {
                        dialogs.put(parts[0].trim(), parts[1].trim().replace("\\n", "\n"));
                    }
                }
            }
            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public StoryData getStory(String key) {
        StoryData sd = stories.get(key);
        if (sd != null) {
            String[] originalParas = sd.getParagraphs();
            String[] replacedParas = new String[originalParas.length];
            for (int i = 0; i < originalParas.length; i++) {
                replacedParas[i] = originalParas[i].replace("%PLAYER%", playerName);
            }
            return new StoryData(sd.getTitle(), replacedParas, sd.getType());
        }
        return null;
    }

    public boolean hasStory(String key) {
        return stories.containsKey(key);
    }

    public String getDialog(String key) {
        String s = dialogs.getOrDefault(key, "MISSING_DIALOG:" + key);
        return s.replace("%PLAYER%", playerName);
    }

    public static class StoryData {
        public enum StoryType {
            PROLOG, CHAPTER, EPILOG
        }

        private String title;
        private String[] paragraphs;
        private StoryType type;

        public StoryData(String title, String[] paragraphs, StoryType type) {
            this.title = title;
            this.paragraphs = paragraphs;
            this.type = type;
        }

        public String getTitle() {
            return title;
        }

        public String[] getParagraphs() {
            return paragraphs;
        }

        public StoryType getType() {
            return type;
        }
    }
}
