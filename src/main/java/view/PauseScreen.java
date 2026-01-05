package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import controller.GamePanel;

public class PauseScreen extends UI {

	protected PauseScreen(GamePanel gp) {
		super(gp);

		totalOptions = 5;
	}

	public void draw(Graphics2D g2) {
		g2.setColor(new Color(0, 0, 0, 150));
		g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

		g2.setFont(getScaledFont(Font.PLAIN, 80));
		g2.setColor(Color.white);

		String text = "PAUSED";
		g2.drawString(text, getHorizontalCenter(text, g2, gp.screenWidth), gp.tileSize * 2);

		g2.setFont(getScaledFont(Font.PLAIN, 32));

		String resume = "RESUME";
		String save = "SAVE GAME";
		String settings = "SETTINGS";
		String menu = "MAIN MENU";
		String quit = "QUIT";

		int startY = gp.tileSize * 4;
		int spacing = (int) (gp.tileSize * 1.2);

		g2.drawString(resume, getHorizontalCenter(resume, g2, gp.screenWidth), startY);
		if (selectPosition == 0) {
			g2.drawString(">", getHorizontalCenter(resume, g2, gp.screenWidth) - gp.tileSize, startY);
		}

		g2.drawString(save, getHorizontalCenter(save, g2, gp.screenWidth), startY + spacing);
		if (selectPosition == 1) {
			g2.drawString(">", getHorizontalCenter(save, g2, gp.screenWidth) - gp.tileSize, startY + spacing);
		}

		g2.drawString(settings, getHorizontalCenter(settings, g2, gp.screenWidth), startY + spacing * 2);
		if (selectPosition == 2) {
			g2.drawString(">", getHorizontalCenter(settings, g2, gp.screenWidth) - gp.tileSize, startY + spacing * 2);
		}

		g2.drawString(menu, getHorizontalCenter(menu, g2, gp.screenWidth), startY + spacing * 3);
		if (selectPosition == 3) {
			g2.drawString(">", getHorizontalCenter(menu, g2, gp.screenWidth) - gp.tileSize, startY + spacing * 3);
		}

		g2.drawString(quit, getHorizontalCenter(quit, g2, gp.screenWidth), startY + spacing * 4);
		if (selectPosition == 4) {
			g2.drawString(">", getHorizontalCenter(quit, g2, gp.screenWidth) - gp.tileSize, startY + spacing * 4);
		}

	}
}
