package view;

import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.InputStream;
import java.io.IOException;

import controller.GamePanel;
import model.Settings;

public abstract class UI {
    Settings settings;
    GamePanel gp;
    public Font pressStart2P;

    int selectPosition = 0;
    int totalOptions;

    protected UI(GamePanel gp) {
        this.gp = gp;
        settings = Settings.getInstance();

        try {
            InputStream input = getClass().getResourceAsStream("/fonts/PressStart2P-Regular.ttf");
            pressStart2P = Font.createFont(Font.TRUETYPE_FONT, input);
        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Font getScaledFont(int style, float size) {
        float ratio = (float) gp.scale / 4.0f;
        if (gp.scale > 4) {
            ratio = ratio * 0.75f;
        }
        return pressStart2P.deriveFont(style, size * ratio);
    }

    protected float getUIScale() {
        return (float) gp.scale / 4.0f;
    }

    protected int getHorizontalCenter(String text, Graphics2D g2, int screenWidth) {
        int textLength = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        int x = screenWidth / 2 - textLength / 2;
        return x;
    }

    public void moveSelectorUp() {
        selectPosition--;
        if (selectPosition < 0) {
            selectPosition = totalOptions - 1;
        }
    }

    public void moveSelectorDown() {
        selectPosition++;
        if (selectPosition >= totalOptions) {
            selectPosition = 0;
        }
    }

    public int getSelectorPosition() {
        return selectPosition;
    }

    public void resetSelectorPosition() {
        selectPosition = 0;
    }

    public void draw(Graphics2D g2) {
    }
}
