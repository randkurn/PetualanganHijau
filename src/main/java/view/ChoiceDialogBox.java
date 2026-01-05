package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import controller.GamePanel;

public class ChoiceDialogBox extends UI {

    private int padding = 20;
    private Color boxColor = new Color(0, 0, 0, 200);
    private Color textColor = Color.WHITE;
    private Color highlightColor = new Color(100, 200, 100);

    protected ChoiceDialogBox(GamePanel gp) {
        super(gp);
    }

    public void draw(Graphics2D g2, String narration, String dialogue, String[] choices, int selectedIndex) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        float uiScale = getUIScale();
        int scaledPadding = (int) (padding * uiScale);
        int scaledBoxHeight = (int) (gp.screenHeight * 0.25);

        int boxY = gp.screenHeight - scaledBoxHeight - scaledPadding;
        int boxWidth = gp.screenWidth - (scaledPadding * 2);

        g2.setColor(boxColor);
        g2.fillRoundRect(scaledPadding, boxY, boxWidth, scaledBoxHeight, (int) (20 * uiScale), (int) (20 * uiScale));

        g2.setColor(highlightColor);
        g2.drawRoundRect(scaledPadding, boxY, boxWidth, scaledBoxHeight, (int) (20 * uiScale), (int) (20 * uiScale));

        g2.setFont(getScaledFont(Font.PLAIN, 16));
        g2.setColor(textColor);

        int textX = scaledPadding + (int) (20 * uiScale);
        int textY = boxY + (int) (30 * uiScale);
        int scaledLineHeight = (int) (22 * uiScale);

        if (narration != null && !narration.isEmpty()) {
            String[] lines = narration.split("\\n");
            for (String line : lines) {
                g2.drawString(line, textX, textY);
                textY += scaledLineHeight;
            }
            textY += (int) (10 * uiScale);
        }

        if (dialogue != null && !dialogue.isEmpty()) {
            g2.setColor(new Color(255, 255, 150));
            String[] lines = dialogue.split("\\n");
            for (String line : lines) {
                g2.drawString(line, textX, textY);
                textY += scaledLineHeight;
            }
        }

        if (choices != null && choices.length > 0) {
            textY += (int) (15 * uiScale);
            g2.setFont(getScaledFont(Font.BOLD, 14));

            for (int i = 0; i < choices.length; i++) {
                if (i == selectedIndex) {
                    g2.setColor(highlightColor);
                    g2.fillRoundRect(textX - (int) (10 * uiScale), textY - (int) (16 * uiScale),
                            boxWidth - (int) (40 * uiScale),
                            (int) (20 * uiScale), (int) (5 * uiScale), (int) (5 * uiScale));
                    g2.setColor(Color.BLACK);
                    g2.drawString("> " + choices[i], textX, textY);
                    g2.setColor(textColor);
                } else {
                    g2.setColor(new Color(180, 180, 180));
                    g2.drawString("  " + choices[i], textX, textY);
                }
                textY += scaledLineHeight + (int) (5 * uiScale);
            }

            g2.setFont(getScaledFont(Font.PLAIN, 10));
            g2.setColor(new Color(150, 150, 150));
            String instr = "[W/S: Pilih | ENTER: Konfirmasi]";
            int instrX = gp.screenWidth - g2.getFontMetrics().stringWidth(instr) - (int) (30 * uiScale);
            g2.drawString(instr, instrX, gp.screenHeight - (int) (25 * uiScale));
        } else {
            g2.setFont(getScaledFont(Font.PLAIN, 10));
            g2.setColor(new Color(150, 150, 150));
            String instr = "[ENTER/SPACE/E: Lanjut]";
            int instrX = gp.screenWidth - g2.getFontMetrics().stringWidth(instr) - (int) (30 * uiScale);
            g2.drawString(instr, instrX, gp.screenHeight - (int) (25 * uiScale));
        }
    }

    @Override
    public void draw(Graphics2D g2) {
    }
}
