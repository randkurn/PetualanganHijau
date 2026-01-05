package util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Quick utility to test if background image can be loaded
 */
public class ImageTester {
    public static void main(String[] args) {
        System.out.println("=== Image Loading Test ===\n");

        // Test 1: Load from resources
        System.out.println("Test 1: Loading from classpath resources");
        try {
            java.io.InputStream stream = ImageTester.class.getResourceAsStream("/titlebg/background.png");
            if (stream != null) {
                System.out.println("✓ Resource stream found");
                BufferedImage img = ImageIO.read(stream);
                if (img != null) {
                    System.out.println("✓ Image loaded successfully!");
                    System.out.println("  Dimensions: " + img.getWidth() + "x" + img.getHeight());
                    System.out.println("  Type: " + img.getType());
                } else {
                    System.out.println("✗ ImageIO.read returned null");
                }
                stream.close();
            } else {
                System.out.println("✗ Resource stream is null (file not found in classpath)");
            }
        } catch (Exception e) {
            System.out.println("✗ Exception: " + e.getMessage());
            e.printStackTrace();
        }

        // Test 2: Load from file system
        System.out.println("\nTest 2: Loading from file system");
        String filePath = "src/main/resources/titlebg/background.png";
        File file = new File(filePath);
        if (file.exists()) {
            System.out.println("✓ File exists at: " + file.getAbsolutePath());
            System.out.println("  File size: " + file.length() + " bytes");
            try {
                BufferedImage img = ImageIO.read(file);
                if (img != null) {
                    System.out.println("✓ Image loaded from file system!");
                    System.out.println("  Dimensions: " + img.getWidth() + "x" + img.getHeight());
                } else {
                    System.out.println("✗ ImageIO.read returned null (possibly corrupted)");
                }
            } catch (Exception e) {
                System.out.println("✗ Exception reading file: " + e.getMessage());
            }
        } else {
            System.out.println("✗ File not found at: " + file.getAbsolutePath());
        }
    }
}
