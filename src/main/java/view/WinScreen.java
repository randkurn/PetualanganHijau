package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

import controller.GamePanel;

public class WinScreen extends UI {
	BufferedImage bgImage[] = new BufferedImage[64];
	BufferedImage currentBGImage;
	int bgPosition = 0, bgCount = 0;

	BufferedImage chicken1, chicken2, chicken3, chicken4, chickenImage;
	int spriteCount = 0;

	boolean showHighScore = false;

	protected WinScreen(GamePanel gp) {
		super(gp);

		totalOptions = 2;

		try {
			for (int i = 0; i < 4; i++) {
				URL url = getClass().getResource("/winbg/" + i + ".png");
				if (url != null) {
					bgImage[i] = ImageIO.read(url);
				}
			}

			if (bgImage[0] == null) {
				URL url = getClass().getResource("/winbg/winScreen.png");
				if (url != null)
					bgImage[0] = ImageIO.read(url);
			}

			chicken1 = setupImage("/chicken/chickenup1.png");
			chicken2 = setupImage("/chicken/chickenupstill1.png");
			chicken3 = setupImage("/chicken/chickenup2.png");
			chicken4 = setupImage("/chicken/chickenupstill2.png");
		} catch (IOException e) {
			e.printStackTrace();
		}

		chickenImage = chicken1;
		currentBGImage = bgImage[0];
	}

	private BufferedImage setupImage(String path) {
		try {
			URL url = getClass().getResource(path);
			if (url != null) {
				return ImageIO.read(url);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void draw(Graphics2D g2) {

		if (currentBGImage != null) {
			g2.drawImage(currentBGImage, 0, 0, gp.screenWidth, gp.screenHeight, null);
		}

		if (bgCount == 15) {
			bgPosition++;
			if (bgPosition >= 4 || bgImage[bgPosition] == null) {
				bgPosition = 0;
			}
			currentBGImage = bgImage[bgPosition];
			bgCount = 0;
		}
		bgCount++;

		int spriteSize = gp.tileSize * gp.scale;

		if (chickenImage != null) {
			g2.drawImage(chickenImage, (gp.screenWidth - spriteSize) / 2, (gp.screenHeight - spriteSize) / 2,
					spriteSize, spriteSize, null);
		}

		// Changes the sprite for the chicken every 15 frames or 1/4 second.
		switch (spriteCount) {
			case 15:
				chickenImage = chicken1;
				break;
			case 30:
				chickenImage = chicken2;
				break;
			case 45:
				chickenImage = chicken3;
				break;
			case 60:
				chickenImage = chicken4;
				spriteCount = 0;
		}
		spriteCount++;

		g2.setFont(getScaledFont(Font.PLAIN, 80));

		float cleanliness = controller.EnvironmentManager.getInstance().getScoreCleanliness();
		String gameOver;
		Color titleColor;
		String envMessage;

		if (cleanliness > 80) {
			gameOver = "PAHLAWAN LINGKUNGAN!";
			titleColor = new Color(50, 255, 50);
			envMessage = "Desa sangat asri dan bersih berkat kamu!";
		} else if (cleanliness > 50) {
			gameOver = "BERHASIL MENYELESAIKAN!";
			titleColor = Color.WHITE;
			envMessage = "Terima kasih sudah membantu menjaga desa.";
		} else if (cleanliness > 25) {
			gameOver = "DESA TERANCAM...";
			titleColor = new Color(255, 200, 50);
			envMessage = "Desa mulai tercemar. Mari lebih peduli lagi.";
		} else {
			gameOver = "LINGKUNGAN RUSAK!";
			titleColor = new Color(255, 50, 50);
			envMessage = "Sampah menumpuk... Masa depan desa suram.";
		}

		g2.setColor(titleColor);
		g2.drawString(gameOver, getHorizontalCenter(gameOver, g2, gp.screenWidth), gp.tileSize * 2);

		g2.setFont(getScaledFont(Font.PLAIN, 12));
		g2.setColor(Color.WHITE);
		g2.drawString(envMessage, getHorizontalCenter(envMessage, g2, gp.screenWidth), gp.tileSize * 3);

		String envScore = "Kesehatan Lingkungan: " + (int) cleanliness + "%";
		g2.setFont(getScaledFont(Font.BOLD, 14));
		g2.setColor(titleColor);
		g2.drawString(envScore, getHorizontalCenter(envScore, g2, gp.screenWidth), (int) (gp.tileSize * 4.2));

		g2.setFont(getScaledFont(Font.PLAIN, 15));
		g2.setColor(Color.WHITE);

		String highScore = "HIGH SCORE";
		String savedHighScore = String.valueOf(settings.getHighScore());

		g2.drawString(highScore, getHorizontalCenter(highScore, g2, gp.screenWidth / 2), gp.tileSize * 6);
		g2.drawString(savedHighScore, getHorizontalCenter(savedHighScore, g2, gp.screenWidth / 2),
				(int) (gp.tileSize * 6.5));

		String yourScore = "YOUR SCORE";
		String playerScore = String.valueOf(gp.player.score);

		g2.drawString(yourScore, getHorizontalCenter(yourScore, g2, gp.screenWidth / 2) + gp.screenWidth / 2,
				gp.tileSize * 6);
		g2.drawString(playerScore, getHorizontalCenter(playerScore, g2, gp.screenWidth / 2) + gp.screenWidth / 2,
				(int) (gp.tileSize * 6.5));

		if (showHighScore) {
			g2.setColor(Color.RED);
			String newHighScore = "NEW HIGH SCORE!";
			g2.drawString(newHighScore, getHorizontalCenter(newHighScore, g2, gp.screenWidth / 2),
					(int) (gp.tileSize * 7.2));
			g2.setColor(Color.WHITE);
		}

		g2.setFont(getScaledFont(Font.PLAIN, 40));

		String retry = "RETRY";
		String menu = "MAIN MENU";
		String quit = "QUIT";

		g2.drawString(retry, getHorizontalCenter(retry, g2, gp.screenWidth), gp.tileSize * 9);
		if (selectPosition == 0) {
			g2.drawString(">", getHorizontalCenter(retry, g2, gp.screenWidth) - gp.tileSize, gp.tileSize * 9);
		}

		g2.drawString(menu, getHorizontalCenter(menu, g2, gp.screenWidth), gp.tileSize * 10);
		if (selectPosition == 1) {
			g2.drawString(">", getHorizontalCenter(menu, g2, gp.screenWidth) - gp.tileSize, gp.tileSize * 10);
		}

		g2.drawString(quit, getHorizontalCenter(quit, g2, gp.screenWidth), gp.tileSize * 11);
		if (selectPosition == 2) {
			g2.drawString(">", getHorizontalCenter(quit, g2, gp.screenWidth) - gp.tileSize, gp.tileSize * 11);
		}
	}

	protected void showNewHighScore(boolean showHighScore) {
		this.showHighScore = showHighScore;
	}
}
