package controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TiledMapExporter {

    private static class ParsedMap {
        int rows;
        int cols;
        int playerStartCol;
        int playerStartRow;
        int[][] tiles;
        int[][] objects;
    }

    private static ParsedMap loadTxtMap(Path path) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String header = reader.readLine();
            if (header == null) {
                throw new IOException("Header kosong: " + path);
            }

            String[] parts = header.split(",");
            if (parts.length < 8) {
                throw new IOException("Header tidak valid di " + path + " -> " + header);
            }

            ParsedMap m = new ParsedMap();
            m.rows = Integer.parseInt(parts[1].trim());
            m.cols = Integer.parseInt(parts[2].trim());
            m.playerStartCol = Integer.parseInt(parts[3].trim());
            m.playerStartRow = Integer.parseInt(parts[4].trim());

            m.tiles = new int[m.rows][m.cols];
            m.objects = new int[m.rows][m.cols];

            readArray(reader, m.rows, m.cols, m.tiles);

            reader.readLine();

            readArray(reader, m.rows, m.cols, m.objects);

            return m;
        }
    }

    private static void readArray(BufferedReader reader, int rows, int cols, int[][] out) throws IOException {
        for (int row = 0; row < rows; row++) {
            String line = reader.readLine();
            if (line == null) {
                break;
            }
            line = line.trim();
            if (line.isEmpty()) {
                for (int c = 0; c < cols; c++) {
                    out[row][c] = 0;
                }
                continue;
            }

            String[] nums = line.split("\\s+");
            for (int c = 0; c < cols && c < nums.length; c++) {
                String token = nums[c].trim();
                if (token.isEmpty()) {
                    out[row][c] = 0;
                } else {
                    out[row][c] = Integer.parseInt(token);
                }
            }
            for (int c = nums.length; c < cols; c++) {
                out[row][c] = 0;
            }
        }
    }

    private static void writeTmx(ParsedMap map, Path tilesetTsx, Path outPath) throws IOException {
        int tileWidth = 16;
        int tileHeight = 16;

        try (BufferedWriter w = Files.newBufferedWriter(outPath, StandardCharsets.UTF_8)) {
            w.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            w.write("<map version=\"1.9\" tiledversion=\"1.10.2\" orientation=\"orthogonal\" renderorder=\"right-down\" ");
            w.write("width=\"" + map.cols + "\" height=\"" + map.rows + "\" ");
            w.write("tilewidth=\"" + tileWidth + "\" tileheight=\"" + tileHeight + "\" ");
            w.write("infinite=\"0\" nextlayerid=\"3\" nextobjectid=\"1\">\n");

            // Referensikan tileset eksternal yang di-generate dari tiles/tileData.txt
            Path relTsx = outPath.getParent().relativize(tilesetTsx);
            w.write("  <tileset firstgid=\"1\" source=\"" + relTsx.toString().replace('\\', '/') + "\"/>\n");

            // Layer tiles: index di file txt adalah 0-based index ke tileData,
            // sedangkan TMX memakai gid 1-based. Offset +1 untuk semua nilai
            // supaya tile index 0 di game muncul sebagai tile pertama di Tiled.
            w.write("  <layer id=\"1\" name=\"Tiles\" width=\"" + map.cols + "\" height=\"" + map.rows + "\">\n");
            w.write("    <data encoding=\"csv\">\n");
            writeCsvData(w, map.tiles, 1, false);
            w.write("    </data>\n");
            w.write("  </layer>\n");

            w.write("  <objectgroup id=\"2\" name=\"Spawn\">\n");
            int spawnX = map.playerStartCol * tileWidth;
            int spawnY = map.playerStartRow * tileHeight;
            w.write("    <object id=\"1\" name=\"PlayerStart\" x=\"" + spawnX + "\" y=\"" + spawnY + "\" width=\""
                    + tileWidth + "\" height=\"" + tileHeight + "\"/>\n");
            w.write("  </objectgroup>\n");

            w.write("</map>\n");
        }
    }

    private static void writeCsvData(BufferedWriter w, int[][] data, int gidOffset, boolean zeroIsEmpty)
            throws IOException {
        int rows = data.length;
        if (rows == 0) {
            return;
        }
        int cols = data[0].length;
        for (int r = 0; r < rows; r++) {
            StringBuilder line = new StringBuilder();
            for (int c = 0; c < cols; c++) {
                if (c > 0) {
                    line.append(',');
                }
                int v = data[r][c];
                int gid;
                if (zeroIsEmpty && v == 0) {
                    gid = 0;
                } else {
                    gid = v + gidOffset;
                }
                line.append(gid);
            }
            w.write("      " + line.toString());
            if (r < rows - 1) {
                w.write(",\n");
            } else {
                w.write("\n");
            }
        }
    }

    private static Path generateTileset(Path tilesDir) throws IOException {
        Path tileData = tilesDir.resolve("tileData.txt");
        if (!Files.exists(tileData)) {
            throw new IOException("Tidak menemukan tiles/tileData.txt di " + tileData.toAbsolutePath());
        }
        Path tsxPath = tilesDir.resolve("tiles_from_game.tsx");
        if (Files.exists(tsxPath)) {
            return tsxPath;
        }

        int tileWidth = 16;
        int tileHeight = 16;

        try (BufferedReader reader = Files.newBufferedReader(tileData, StandardCharsets.UTF_8);
                BufferedWriter w = Files.newBufferedWriter(tsxPath, StandardCharsets.UTF_8)) {
            w.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            w.write("<tileset version=\"1.9\" tiledversion=\"1.10.2\" name=\"tiles_from_game\" tilewidth=\""
                    + tileWidth + "\" tileheight=\"" + tileHeight + "\" tilecount=\"0\" columns=\"0\">\n");

            String imageLine;
            int id = 0;
            while ((imageLine = reader.readLine()) != null) {
                String collisionLine = reader.readLine(); // true/false di baris berikutnya
                String imageFile = imageLine.trim();
                boolean collides = collisionLine != null && Boolean.parseBoolean(collisionLine.trim());

                w.write("  <tile id=\"" + id + "\">\n");
                w.write("    <image width=\"" + tileWidth + "\" height=\"" + tileHeight + "\" source=\"" + imageFile
                        + "\"/>\n");
                if (collides) {
                    w.write("    <properties><property name=\"collision\" type=\"bool\" value=\"true\"/></properties>\n");
                }
                w.write("  </tile>\n");
                id++;
            }

            w.write("</tileset>\n");
        }

        return tsxPath;
    }

    public static void main(String[] args) throws Exception {
        Path mapsDir = Paths.get("src/main/resources/maps");
        Path tilesDir = Paths.get("src/main/resources/tiles");
        Path tilesetTsx = generateTileset(tilesDir);

        String[] mapNames;
        if (args != null && args.length > 0) {
            mapNames = args;
        } else {
            mapNames = new String[] { "farm", "barn", "forest", "fishing", "park", "city", "house" };
        }

        System.out.println("=== TiledMapExporter ===");
        for (String name : mapNames) {
            Path src = mapsDir.resolve(name + ".txt");
            if (!Files.exists(src)) {
                System.out.println("  [SKIP] Tidak menemukan: " + src);
                continue;
            }

            ParsedMap m = loadTxtMap(src);
            Path out = mapsDir.resolve(name + ".tmx");
            writeTmx(m, tilesetTsx, out);
            System.out.println("  [OK] " + src + " -> " + out);
        }
        System.out.println("Selesai. Silakan buka file .tmx di Tiled dan atur tileset sesuai kebutuhan.");
    }
}
