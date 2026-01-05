package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import controller.GamePanel;
import controller.SaveManager;

public class TitleScreen extends UI {
	BufferedImage background;

	BufferedImage char1_1, char1_2, char1Image;
	BufferedImage char2_1, char2_2, char2Image;
	int spriteCount = 0;
	boolean hasSaveFile = false;

	java.util.Random random = new java.util.Random();

	protected TitleScreen(GamePanel gp) {
		super(gp);

		totalOptions = 6;

		try {
			char1_1 = ImageIO.read(getClass().getResourceAsStream("/boy/right1.png"));
			char1_2 = ImageIO.read(getClass().getResourceAsStream("/boy/right2.png"));

			String[] npcFolders = {
					"/NPC/adit",
					"/NPC/paksaman",
					"/NPC/busuci"
			};
			String npcPath = npcFolders[random.nextInt(npcFolders.length)];

			char2_1 = ImageIO.read(getClass().getResourceAsStream(npcPath + "/right1.png"));
			char2_2 = ImageIO.read(getClass().getResourceAsStream(npcPath + "/right2.png"));

			background = ImageIO.read(getClass().getResourceAsStream("/titlebg/background.png"));
		} catch (Exception e) {
			System.err.println("[TitleScreen] Critical error loading sprites: " + e.getMessage());
			e.printStackTrace();
			char1_1 = char1_2 = char2_1 = char2_2 = null;
		}

		char1Image = char1_1;
		char2Image = char2_1;

		this.hasSaveFile = SaveManager.getInstance().hasSaveFile();
	}

	public void draw(Graphics2D g2) {

		if (background != null) {
			g2.drawImage(background, 0, 0, gp.screenWidth, gp.screenHeight, null);
		} else {
			g2.setColor(new Color(50, 100, 50));
			g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
		}

		g2.setFont(getScaledFont(Font.PLAIN, 80));

		String title1 = "PETUALANGAN";
		String title2 = "HIJAU";

		g2.setColor(Color.BLACK);
		g2.drawString(title1, getHorizontalCenter(title1, g2, gp.screenWidth) + (5 / 2) * gp.scale,
				gp.tileSize * 2 + (5 / 2) * gp.scale);
		g2.drawString(title2, getHorizontalCenter(title2, g2, gp.screenWidth) + (5 / 2) * gp.scale,
				gp.tileSize * 4 + (5 / 2) * gp.scale);

		this.hasSaveFile = SaveManager.getInstance().hasSaveFile();

		spriteCount++;
		if (spriteCount > 15) {
			if (char1Image == char1_1)
				char1Image = char1_2;
			else
				char1Image = char1_1;

			if (char2Image == char2_1)
				char2Image = char2_2;
			else
				char2Image = char2_1;

			spriteCount = 0;
		}

		if (char1Image != null) {
			g2.drawImage(char1Image, gp.tileSize * 2, gp.screenHeight - gp.tileSize * 3, gp.tileSize * 2,
					gp.tileSize * 2, null);
		}
		if (char2Image != null) {
			g2.drawImage(char2Image, gp.screenWidth - gp.tileSize * 4, gp.screenHeight - gp.tileSize * 3,
					gp.tileSize * 2, gp.tileSize * 2, null);
		}

		g2.setColor(Color.WHITE);
		g2.drawString(title1, getHorizontalCenter(title1, g2, gp.screenWidth), gp.tileSize * 2);
		g2.drawString(title2, getHorizontalCenter(title2, g2, gp.screenWidth), gp.tileSize * 4);

		g2.setFont(getScaledFont(Font.PLAIN, 32));

		String play = "NEW GAME";
		String loadGame = "LOAD GAME";
		String help = "HELP";
		String settings = "SETTINGS";
		String credits = "CREDITS";
		String quit = "QUIT";

		int startY = gp.tileSize * 6;
		int spacing = (int) (gp.tileSize * 0.7);

		g2.drawString(play, getHorizontalCenter(play, g2, gp.screenWidth), startY);
		if (selectPosition == 0) {
			g2.drawString(">", getHorizontalCenter(play, g2, gp.screenWidth) - gp.tileSize, startY);
		}

		if (!hasSaveFile) {
			g2.setColor(Color.GRAY);
		}
		g2.drawString(loadGame, getHorizontalCenter(loadGame, g2, gp.screenWidth), startY + spacing);
		if (selectPosition == 1 && hasSaveFile) {
			g2.drawString(">", getHorizontalCenter(loadGame, g2, gp.screenWidth) - gp.tileSize, startY + spacing);
		}
		g2.setColor(Color.WHITE);

		g2.drawString(help, getHorizontalCenter(help, g2, gp.screenWidth), startY + spacing * 2);
		if (selectPosition == 2) {
			g2.drawString(">", getHorizontalCenter(help, g2, gp.screenWidth) - gp.tileSize, startY + spacing * 2);
		}

		g2.drawString(settings, getHorizontalCenter(settings, g2, gp.screenWidth), startY + spacing * 3);
		if (selectPosition == 3) {
			g2.drawString(">", getHorizontalCenter(settings, g2, gp.screenWidth) - gp.tileSize, startY + spacing * 3);
		}

		g2.drawString(credits, getHorizontalCenter(credits, g2, gp.screenWidth), startY + spacing * 4);
		if (selectPosition == 4) {
			g2.drawString(">", getHorizontalCenter(credits, g2, gp.screenWidth) - gp.tileSize, startY + spacing * 4);
		}

		g2.drawString(quit, getHorizontalCenter(quit, g2, gp.screenWidth), startY + spacing * 5);
		if (selectPosition == 5) {
			g2.drawString(">", getHorizontalCenter(quit, g2, gp.screenWidth) - gp.tileSize, startY + spacing * 5);
		}
	}
}
