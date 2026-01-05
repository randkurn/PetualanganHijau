package controller.util;

import java.io.*;
import java.nio.file.*;
import java.util.regex.*;

/**
 * Utility untuk membantu update TMX files dengan portal tiles yang benar.
 * 
 * Cara penggunaan:
 * 1. Jalankan class ini dari IDE atau command line
 * 2. Script akan scan semua TMX files
 * 3. Laporkan posisi yang perlu diupdate dengan portal tiles
 * 
 * PENTING: Ini hanya tool helper, update TMX file tetap manual menggunakan
 * Tiled Editor
 */
public class PortalTileHelper {

    // Portal tile IDs sesuai konfigurasi
    private static final String PORTAL_TILE_28 = "28";
    private static final String PORTAL_TILE_308 = "308";
    private static final String PORTAL_TILE_469 = "469";

    // Map files
    private static final String[] MAP_FILES = {
            "house.tmx", // 0
            "farm.tmx", // 1
            "park.tmx", // 2
            "city.tmx", // 3
            "fishing.tmx" // 4
    };

    public static void main(String[] args) {
        System.out.println("=== Portal Tile Helper ===");
        System.out.println("Scanning TMX files untuk portal configuration...\n");

        String mapPath = "src/main/resources/maps/";

        for (String mapFile : MAP_FILES) {
            analyzeMapFile(mapPath + mapFile);
        }

        System.out.println("\n=== Panduan Portal Tiles ===");
        printPortalGuide();
    }

    private static void analyzeMapFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("⚠ File tidak ditemukan: " + filePath);
            return;
        }

        System.out.println("\n📁 Analyzing: " + file.getName());
        System.out.println("─────────────────────────────");

        try {
            String content = new String(Files.readAllBytes(file.toPath()));

            // Cek apakah sudah ada portal tiles
            boolean has28 = content.contains(">" + PORTAL_TILE_28 + ",")
                    || content.contains("," + PORTAL_TILE_28 + "<");
            boolean has308 = content.contains(">" + PORTAL_TILE_308 + ",")
                    || content.contains("," + PORTAL_TILE_308 + "<");
            boolean has469 = content.contains(">" + PORTAL_TILE_469 + ",")
                    || content.contains("," + PORTAL_TILE_469 + "<");

            // Print map dimensions
            Pattern dimPattern = Pattern.compile("width=\"(\\d+)\" height=\"(\\d+)\"");
            Matcher dimMatcher = dimPattern.matcher(content);
            if (dimMatcher.find()) {
                int width = Integer.parseInt(dimMatcher.group(1));
                int height = Integer.parseInt(dimMatcher.group(2));
                System.out.println("📐 Ukuran Map: " + width + " x " + height + " tiles");
            }

            // Report portal tiles found
            if (has28) {
                System.out.println("✓ Ditemukan tile 28 (Portal Standar)");
            }
            if (has308) {
                System.out.println("✓ Ditemukan tile 308 (House/City Portal)");
            }
            if (has469) {
                System.out.println("✓ Ditemukan tile 469 (Park Portal)");
            }

            if (!has28 && !has308 && !has469) {
                System.out.println("⚠ Tidak ada portal tiles terdeteksi!");
                System.out.println("   Tambahkan portal tiles menggunakan Tiled Editor");
            }

            // Suggest portal locations based on map
            suggestPortalLocations(file.getName());

        } catch (IOException e) {
            System.out.println("❌ Error reading file: " + e.getMessage());
        }
    }

    private static void suggestPortalLocations(String fileName) {
        System.out.println("\n💡 Saran Lokasi Portal:");

        switch (fileName) {
            case "house.tmx":
                System.out.println("   - Kanan (→ City): Gunakan tile 308 dekat edge kanan");
                System.out.println("   - Bawah (↓ Fishing): Gunakan tile 28 dekat edge bawah");
                System.out.println("   - Atas (↑ Park): Gunakan tile 308 dekat edge atas");
                break;

            case "city.tmx":
                System.out.println("   - Kiri (← House): Gunakan tile 308 dekat edge kiri");
                break;

            case "fishing.tmx":
                System.out.println("   - Atas (↑ House): Gunakan tile 28 dekat edge atas");
                System.out.println("   - Bawah (↓ Farm): Gunakan tile 28 dekat edge bawah");
                break;

            case "farm.tmx":
                System.out.println("   - Atas (↑ Fishing): Gunakan tile 28 dekat edge atas");
                System.out.println("   - Bawah (↓ Park): Gunakan tile 28 dekat edge bawah");
                break;

            case "park.tmx":
                System.out.println("   - Atas (↑ Farm): Gunakan tile 469 dekat edge atas");
                System.out.println("   - Bawah (↓ House): Gunakan tile 469 dekat edge bawah");
                break;
        }
    }

    private static void printPortalGuide() {
        System.out.println("\n📋 PORTAL TILE IDs:");
        System.out.println("   • Tile 28  = Portal Standar (Farm, Fishing areas)");
        System.out.println("   • Tile 308 = House/City Portal");
        System.out.println("   • Tile 469 = Park Portal");

        System.out.println("\n🗺 MAPPING PERPINDAHAN:");
        System.out.println("   House → Kanan → City");
        System.out.println("   City → Kiri → House");
        System.out.println("   House → Bawah → Fishing");
        System.out.println("   Fishing → Atas → House");
        System.out.println("   Fishing → Bawah → Farm");
        System.out.println("   Farm → Atas → Fishing");
        System.out.println("   Farm → Bawah → Park");
        System.out.println("   Park → Atas → Farm");
        System.out.println("   Park → Bawah → House (loop)");
        System.out.println("   House → Atas → Park");

        System.out.println("\n📝 CARA UPDATE TMX:");
        System.out.println("   1. Buka file TMX di Tiled Editor");
        System.out.println("   2. Pilih layer 'Tiles'");
        System.out.println("   3. Cari dan pilih tile dengan ID yang sesuai dari tileset");
        System.out.println("   4. Letakkan di posisi edge map sesuai arah perpindahan");
        System.out.println("   5. Pastikan portal tile di-place di beberapa tile (2-3 tiles)");
        System.out.println("      untuk area detection yang lebih luas");
        System.out.println("   6. Save file TMX");

        System.out.println("\n⚠ CATATAN PENTING:");
        System.out.println("   - Portal tiles harus berada di edge/tepi map");
        System.out.println("   - Letakkan 2-3 tiles berjajar untuk area trigger lebih besar");
        System.out.println("   - Jangan place portal di area tengah map");
        System.out.println("   - Test in-game setelah update untuk memastikan transisi bekerja");
    }
}
