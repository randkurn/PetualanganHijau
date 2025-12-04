package model;

/**
 * Mengelola backsound menu dan game.
 *
 * Catatan: Kelas ini SENGAJA tidak mengimport javafx.scene.media.* supaya
 * project tetap bisa dikompilasi walaupun modul JavaFX Media tidak tersedia.
 * Jika modul tersedia, kita akses lewat refleksi (Class.forName).
 */
public final class AudioManager {

    private static boolean mediaAvailable;
    private static Class<?> mediaClass;
    private static Class<?> mediaPlayerClass;

    private static Object menuPlayer;
    private static Object gamePlayer;
    private static Object activePlayer;

    static {
        try {
            mediaClass = Class.forName("javafx.scene.media.Media");
            mediaPlayerClass = Class.forName("javafx.scene.media.MediaPlayer");
            mediaAvailable = true;
        } catch (Throwable t) {
            mediaAvailable = false;
            System.out.println("JavaFX Media tidak tersedia. Backsound dimatikan.");
        }
    }

    private AudioManager() {}

    private static void ensurePlayers() {
        if (!mediaAvailable) return;
        if (menuPlayer == null) {
            menuPlayer = buildPlayer("/assets/audio/backsong-menu.mp3");
        }
        if (gamePlayer == null) {
            gamePlayer = buildPlayer("/assets/audio/backsong-game.mp3");
        }
    }

    private static Object buildPlayer(String resource) {
        try {
            var url = AudioManager.class.getResource(resource);
            if (url == null) {
                System.out.println("Audio tidak ditemukan: " + resource);
                return null;
            }
            Object media = mediaClass
                    .getConstructor(String.class)
                    .newInstance(url.toExternalForm());
            Object player = mediaPlayerClass
                    .getConstructor(mediaClass)
                    .newInstance(media);
            // coba set volume awal sesuai settings
            double volume = SettingsManager.isMuted() ? 0 : SettingsManager.getRawVolume();
            mediaPlayerClass.getMethod("setVolume", double.class).invoke(player, volume);
            return player;
        } catch (Throwable t) {
            System.out.println("Gagal inisialisasi audio: " + resource);
            t.printStackTrace();
            return null;
        }
    }

    public static void playMenuMusic() {
        if (!mediaAvailable) return;
        ensurePlayers();
        switchTo(menuPlayer);
    }

    public static void playGameMusic() {
        if (!mediaAvailable) return;
        ensurePlayers();
        switchTo(gamePlayer);
    }

    private static void switchTo(Object target) {
        if (!mediaAvailable || target == null) return;
        try {
            if (activePlayer == target) {
                refreshVolume();
                var status = mediaPlayerClass.getMethod("getStatus").invoke(activePlayer);
                if (!status.toString().equals("PLAYING")) {
                    mediaPlayerClass.getMethod("play").invoke(activePlayer);
                }
                return;
            }
            if (activePlayer != null) {
                mediaPlayerClass.getMethod("stop").invoke(activePlayer);
            }
            activePlayer = target;
            refreshVolume();
            mediaPlayerClass.getMethod("play").invoke(activePlayer);
        } catch (Throwable t) {
            System.out.println("Gagal memutar backsound.");
            t.printStackTrace();
        }
    }

    public static void refreshVolume() {
        if (!mediaAvailable) return;
        double volume = SettingsManager.isMuted() ? 0 : SettingsManager.getRawVolume();
        try {
            if (menuPlayer != null) {
                mediaPlayerClass.getMethod("setVolume", double.class).invoke(menuPlayer, volume);
            }
            if (gamePlayer != null) {
                mediaPlayerClass.getMethod("setVolume", double.class).invoke(gamePlayer, volume);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}

