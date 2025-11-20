package model;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class DataManager {

    private static final String SAVE_FOLDER = "resources/assets/save/";

    public static Map<String, String> loadCSV(String filename) {
        Map<String, String> data = new HashMap<>();
        File file = new File(SAVE_FOLDER + filename);

        if (!file.exists()) return data;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] sp = line.split(",");
                if (sp.length == 2)
                    data.put(sp[0], sp[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    public static void saveCSV(String filename, Map<String, String> data) {
        File file = new File(SAVE_FOLDER + filename);
        file.getParentFile().mkdirs();

        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            for (String key : data.keySet())
                pw.println(key + "," + data.get(key));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
