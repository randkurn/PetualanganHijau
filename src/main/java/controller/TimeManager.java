package controller;

import java.awt.Color;
import java.awt.Graphics2D;

public class TimeManager {

    private static TimeManager instance;

    public enum TimeOfDay {
        DAWN("Subuh"),
        MORNING("Pagi"),
        NOON("Siang"),
        AFTERNOON("Sore"),
        DUSK("Petang"),
        NIGHT("Malam");

        private final String displayName;

        TimeOfDay(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public static final int REAL_SECONDS_PER_DAY = 1080;
    public static final int GAME_MINUTES_PER_DAY = 1440;
    public static final double REAL_SECONDS_PER_GAME_MINUTE = (double) REAL_SECONDS_PER_DAY / GAME_MINUTES_PER_DAY;

    public static final int DAWN_START = 5 * 60;
    public static final int MORNING_START = 7 * 60;
    public static final int NOON_START = 12 * 60;
    public static final int AFTERNOON_START = 15 * 60;
    public static final int DUSK_START = 18 * 60;
    public static final int NIGHT_START = 20 * 60;

    private int currentMinute;
    private int currentDay;
    private double secondsAccumulator;
    private boolean paused;
    private int timeSpeed = 1;

    private Color overlayColor;
    private int overlayAlpha;
    private long lastUpdateTime;
    private GamePanel gp;

    private TimeManager() {
        currentMinute = 6 * 60;
        currentDay = 1;
        lastUpdateTime = System.nanoTime();
        updateOverlay();
    }

    public static TimeManager getInstance() {
        if (instance == null)
            instance = new TimeManager();
        return instance;
    }

    public void setGamePanel(GamePanel gp) {
        this.gp = gp;
    }

    public void update() {
        if (paused) {
            lastUpdateTime = System.nanoTime();
            return;
        }
        long currentTime = System.nanoTime();
        double deltaSeconds = (currentTime - lastUpdateTime) / 1_000_000_000.0;
        lastUpdateTime = currentTime;
        secondsAccumulator += deltaSeconds * timeSpeed;
        while (secondsAccumulator >= REAL_SECONDS_PER_GAME_MINUTE) {
            secondsAccumulator -= REAL_SECONDS_PER_GAME_MINUTE;
            advanceMinute();
        }
    }

    private void advanceMinute() {
        currentMinute++;

        if (currentMinute % 60 == 0 && gp != null && gp.player != null) {
            gp.player.energy--;
            if (gp.player.energy < 0)
                gp.player.energy = 0;

            if (gp.player.energy < 5) {
                gp.triggerExhaustion();
            }
        }

        if (currentMinute >= GAME_MINUTES_PER_DAY) {
            currentMinute = 0;
            advanceDay();
        }
        updateOverlay();
    }

    public void advanceDay() {
        currentDay++;
        currentMinute = 6 * 60;
        updateOverlay();
    }

    private void updateOverlay() {
        int m = currentMinute;
        if (m >= NIGHT_START || m < DAWN_START) {
            overlayColor = new Color(0, 0, 50);
            overlayAlpha = 210;
        } else if (m >= DAWN_START && m < MORNING_START) {
            overlayColor = new Color(50, 50, 100);
            double progress = (double) (m - DAWN_START) / (MORNING_START - DAWN_START);
            overlayAlpha = (int) (210 * (1.0 - progress));
        } else if (m >= MORNING_START && m < AFTERNOON_START) {
            overlayColor = null;
            overlayAlpha = 0;
        } else if (m >= AFTERNOON_START && m < DUSK_START) {
            overlayColor = new Color(100, 50, 0);
            double progress = (double) (m - AFTERNOON_START) / (DUSK_START - AFTERNOON_START);
            overlayAlpha = (int) (80 * progress);
        } else if (m >= DUSK_START && m < NIGHT_START) {
            overlayColor = new Color(50, 25, 50);
            double progress = (double) (m - DUSK_START) / (NIGHT_START - DUSK_START);
            overlayAlpha = 80 + (int) (70 * progress);
        }
    }

    public void drawOverlay(Graphics2D g2, int w, int h) {
        if (gp == null || gp.player == null)
            return;

        boolean isInterior = gp.mapM != null && gp.mapM.isInterior();

        // Interior always has limited visibility - V-shaped field of view like eyes
        if (isInterior) {
            drawInteriorVisionCone(g2, w, h);
            return;
        }

        if (overlayColor != null && overlayAlpha > 0) {
            if (isNight()) {
                int centerX = gp.player.screenX + (gp.tileSize / 2);
                int centerY = gp.player.screenY + (gp.tileSize / 2);

                float radius = gp.tileSize * 2.5f;

                float[] fractions = { 0.0f, 0.4f, 1.0f };
                Color transparent = new Color(0, 0, 0, 0);
                Color nightColor = new Color(overlayColor.getRed(), overlayColor.getGreen(), overlayColor.getBlue(),
                        overlayAlpha);

                Color[] colors = { transparent, new Color(nightColor.getRed(), nightColor.getGreen(),
                        nightColor.getBlue(), (int) (nightColor.getAlpha() * 0.3)), nightColor };

                java.awt.RadialGradientPaint rgp = new java.awt.RadialGradientPaint(
                        centerX, centerY, radius, fractions, colors);

                g2.setPaint(rgp);
                g2.fillRect(0, 0, w, h);
            } else {
                g2.setColor(new Color(overlayColor.getRed(), overlayColor.getGreen(), overlayColor.getBlue(),
                        overlayAlpha));
                g2.fillRect(0, 0, w, h);
            }
        }
    }

    public void sleep() {
        currentMinute = MORNING_START;
        advanceDay();
        secondsAccumulator = 0;
        updateOverlay();
        if (gp != null && gp.player != null)
            gp.player.energy = gp.player.maxEnergy;
    }

    public TimeOfDay getCurrentTimeOfDay() {
        if (currentMinute >= NIGHT_START || currentMinute < DAWN_START)
            return TimeOfDay.NIGHT;
        if (currentMinute < MORNING_START)
            return TimeOfDay.DAWN;
        if (currentMinute < NOON_START)
            return TimeOfDay.MORNING;
        if (currentMinute < AFTERNOON_START)
            return TimeOfDay.NOON;
        if (currentMinute < DUSK_START)
            return TimeOfDay.AFTERNOON;
        return TimeOfDay.DUSK;
    }

    public int getHour() {
        return currentMinute / 60;
    }

    public int getMinute() {
        return currentMinute % 60;
    }

    public int getCurrentDay() {
        return currentDay;
    }

    public int getCurrentMinute() {
        return currentMinute;
    }

    public void setCurrentMinute(int minute) {
        this.currentMinute = minute;
        updateOverlay();
    }

    public void setCurrentDay(int day) {
        this.currentDay = day;
    }

    public String getTimeString() {
        return String.format("%02d:%02d", getHour(), getMinute());
    }

    public boolean isNight() {
        return currentMinute >= NIGHT_START || currentMinute < DAWN_START;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
        if (!paused)
            lastUpdateTime = System.nanoTime();
    }

    public void setTimeSpeed(int speed) {
        this.timeSpeed = speed;
    }

    public void reset() {
        currentMinute = 6 * 60;
        currentDay = 1;
        paused = false;
        updateOverlay();
    }

    private void drawInteriorVisionCone(Graphics2D g2, int w, int h) {
        int centerX = gp.player.screenX + (gp.tileSize / 2);
        int centerY = gp.player.screenY + (gp.tileSize / 2);

        float radius = gp.tileSize * 4.0f;

        float[] fractions = { 0.0f, 0.5f, 1.0f };
        Color transparent = new Color(0, 0, 0, 0);
        Color midDark = new Color(0, 0, 0, 90);
        Color fullDark = new Color(0, 0, 0, 179);

        Color[] colors = { transparent, midDark, fullDark };

        java.awt.RadialGradientPaint rgp = new java.awt.RadialGradientPaint(
                centerX, centerY, radius, fractions, colors);

        g2.setPaint(rgp);
        g2.fillRect(0, 0, w, h);

        float[] vignetteFractions = { 0.0f, 0.7f, 1.0f };
        Color[] vignetteColors = {
                new Color(0, 0, 0, 0),
                new Color(0, 0, 0, 30),
                new Color(0, 0, 0, 80)
        };
        java.awt.RadialGradientPaint vignette = new java.awt.RadialGradientPaint(
                centerX, centerY, Math.max(w, h) * 0.7f, vignetteFractions, vignetteColors);
        g2.setPaint(vignette);
        g2.fillRect(0, 0, w, h);
    }
}
