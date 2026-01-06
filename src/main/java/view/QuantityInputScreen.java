package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.function.Consumer;
import controller.GamePanel;

public class QuantityInputScreen extends UI {
    private StringBuilder input;
    private final int MAX_LENGTH = 3; // MAX 999
    private String title = "JUMLAH PEMBELIAN";
    private String prompt = "Masukkan jumlah bibit yang ingin dibeli:";
    private Consumer<Integer> onConfirm;
    private Runnable onCancel;

    public QuantityInputScreen(GamePanel gp) {
        super(gp);
        this.input = new StringBuilder();
    }

    public void startInput(Consumer<Integer> onConfirm, Runnable onCancel) {
        this.onConfirm = onConfirm;
        this.onCancel = onCancel;
        this.input.setLength(0);
        gp.stateM.setCurrentState(controller.StateManager.gameState.QUANTITY_INPUT);
    }

    public void addCharacter(char c) {
        if (input.length() < MAX_LENGTH) {
            if (Character.isDigit(c)) {
                input.append(c);
            }
        }
    }

    public void removeCharacter() {
        if (input.length() > 0) {
            input.deleteCharAt(input.length() - 1);
        }
    }

    public void confirm() {
        if (input.length() > 0 && onConfirm != null) {
            try {
                int qty = Integer.parseInt(input.toString());
                if (qty > 0) {
                    onConfirm.accept(qty);
                }
            } catch (NumberFormatException e) {
                // Ignore
            }
        }
    }

    public void cancel() {
        if (onCancel != null) {
            onCancel.run();
        }
    }

    public void draw(Graphics2D g2) {
        g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                java.awt.RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(0, 0, 0, 220));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        g2.setColor(Color.WHITE);
        g2.setFont(getScaledFont(Font.BOLD, 24));
        int titleX = getHorizontalCenter(title, g2, gp.screenWidth);
        g2.drawString(title, titleX, gp.tileSize * 2);

        g2.setFont(getScaledFont(Font.PLAIN, 18));
        int promptX = getHorizontalCenter(prompt, g2, gp.screenWidth);
        g2.drawString(prompt, promptX, gp.tileSize * 4);

        float uiScale = getUIScale();
        int boxWidth = (int) (200 * uiScale);
        int boxHeight = (int) (80 * uiScale);
        int boxX = (gp.screenWidth - boxWidth) / 2;
        int boxY = gp.tileSize * 5;

        g2.setColor(new Color(255, 255, 255, 50));
        g2.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 15, 15);
        g2.setColor(Color.WHITE);
        g2.setStroke(new java.awt.BasicStroke(2));
        g2.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 15, 15);

        g2.setFont(getScaledFont(Font.BOLD, 36));
        String val = input.toString();
        if (val.isEmpty())
            val = "0";
        int valX = getHorizontalCenter(val, g2, gp.screenWidth);
        g2.drawString(val, valX, boxY + (int) (55 * uiScale));

        g2.setFont(getScaledFont(Font.PLAIN, 14));
        g2.setColor(new Color(200, 200, 200));
        String inst = "Hanya angka (1-999) | ENTER: Oke | ESC: Batal";
        int instX = getHorizontalCenter(inst, g2, gp.screenWidth);
        g2.drawString(inst, instX, gp.screenHeight - gp.tileSize * 2);
    }
}
