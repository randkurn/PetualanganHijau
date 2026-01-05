package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import controller.GamePanel;

public class CreditsScreen extends UI {
	BufferedImage creditsBackground;

	protected CreditsScreen(GamePanel gp) {
		super(gp);

		try {
			creditsBackground = ImageIO.read(getClass().getResourceAsStream("/creditsbg/credits.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void draw(Graphics2D g2) {
		if (creditsBackground != null) {
			g2.drawImage(creditsBackground, 0, 0, gp.screenWidth, gp.screenHeight, null);
		} else {
			g2.setColor(new Color(20, 40, 20));
			g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
		}

		g2.setColor(new Color(0, 0, 0, 120));
		g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

		String title = "PETUALANGAN HIJAU";
		// Perkecil sedikit supaya muat di layar pendek
		g2.setFont(getScaledFont(Font.BOLD, 24));

		g2.setColor(Color.BLACK);
		g2.drawString(title, getHorizontalCenter(title, g2, gp.screenWidth) + 3, gp.tileSize * 2 + 3);

		g2.setColor(new Color(100, 200, 100));
		g2.drawString(title, getHorizontalCenter(title, g2, gp.screenWidth), gp.tileSize * 2);

		String credits = "CREDITS";
		g2.setFont(getScaledFont(Font.PLAIN, 16));
		g2.setColor(Color.WHITE);
		g2.drawString(credits, getHorizontalCenter(credits, g2, gp.screenWidth), (int) (gp.tileSize * 2.5));

		float uiScale = getUIScale();
		int startY = (int) (gp.tileSize * 3.0);
		int lineHeight = (int) (20 * uiScale);
		int sectionGap = (int) (26 * uiScale);
		int currentY = startY;

		g2.setFont(getScaledFont(Font.BOLD, 12));
		g2.setColor(new Color(255, 215, 0));
		String role1 = "Visual Artist & Content Designer";
		g2.drawString(role1, getHorizontalCenter(role1, g2, gp.screenWidth), currentY);
		currentY += lineHeight;

		g2.setFont(getScaledFont(Font.PLAIN, 11));
		g2.setColor(Color.WHITE);
		String name1 = "Faiz Bisma Alfarid (2407903)";
		g2.drawString(name1, getHorizontalCenter(name1, g2, gp.screenWidth), currentY);
		currentY += sectionGap;

		g2.setFont(getScaledFont(Font.BOLD, 12));
		g2.setColor(new Color(255, 215, 0));
		String role2 = "Gameplay Story";
		g2.drawString(role2, getHorizontalCenter(role2, g2, gp.screenWidth), currentY);
		currentY += lineHeight;

		g2.setFont(getScaledFont(Font.PLAIN, 11));
		g2.setColor(Color.WHITE);
		String name2 = "Muhammad Khairul Irham M (2402023)";
		g2.drawString(name2, getHorizontalCenter(name2, g2, gp.screenWidth), currentY);
		currentY += sectionGap;

		g2.setFont(getScaledFont(Font.BOLD, 12));
		g2.setColor(new Color(100, 200, 255));
		String role3 = "PROGRAMMER";
		g2.drawString(role3, getHorizontalCenter(role3, g2, gp.screenWidth), currentY);
		currentY += lineHeight;

		g2.setFont(getScaledFont(Font.BOLD, 11));
		g2.setColor(new Color(200, 200, 255));
		String role4 = "Database Developer";
		g2.drawString(role4, getHorizontalCenter(role4, g2, gp.screenWidth), currentY);
		currentY += lineHeight - 5;

		g2.setFont(getScaledFont(Font.PLAIN, 11));
		g2.setColor(Color.WHITE);
		String name3 = "Navanza Varel A.M (2409181)";
		g2.drawString(name3, getHorizontalCenter(name3, g2, gp.screenWidth), currentY);
		currentY += lineHeight + 5;

		g2.setFont(getScaledFont(Font.BOLD, 11));
		g2.setColor(new Color(200, 200, 255));
		String role5 = "UI/UX Designer";
		g2.drawString(role5, getHorizontalCenter(role5, g2, gp.screenWidth), currentY);
		currentY += lineHeight - 5;

		g2.setFont(getScaledFont(Font.PLAIN, 11));
		g2.setColor(Color.WHITE);
		String name4 = "Nur Adila Putri (2402916)";
		g2.drawString(name4, getHorizontalCenter(name4, g2, gp.screenWidth), currentY);
		currentY += lineHeight + 5;

		g2.setFont(getScaledFont(Font.BOLD, 12));
		g2.setColor(new Color(200, 200, 255));
		String role6 = "Gameplay Logic Programmer";
		g2.drawString(role6, getHorizontalCenter(role6, g2, gp.screenWidth), currentY);
		currentY += lineHeight - 5;

		g2.setFont(getScaledFont(Font.PLAIN, 12));
		g2.setColor(Color.WHITE);
		String name5 = "Nurjiha (2400515)";
		g2.drawString(name5, getHorizontalCenter(name5, g2, gp.screenWidth), currentY);
		currentY += sectionGap;

		g2.setFont(getScaledFont(Font.BOLD, 12));
		g2.setColor(new Color(255, 150, 150));
		String role7 = "Project Manager";
		g2.drawString(role7, getHorizontalCenter(role7, g2, gp.screenWidth), currentY);
		currentY += lineHeight - 5;

		g2.setFont(getScaledFont(Font.PLAIN, 10));
		g2.setColor(new Color(255, 200, 200));
		String role7b = "Story & Logic Designer";
		g2.drawString(role7b, getHorizontalCenter(role7b, g2, gp.screenWidth), currentY);
		currentY += lineHeight;

		g2.setFont(getScaledFont(Font.PLAIN, 12));
		g2.setColor(Color.WHITE);
		String name6 = "Muhammad Randy Kurniawan (2405315)";
		g2.drawString(name6, getHorizontalCenter(name6, g2, gp.screenWidth), currentY);
		currentY += sectionGap;

		g2.setFont(getScaledFont(Font.ITALIC, 12));
		g2.setColor(new Color(150, 255, 150));
		String thanks = "Terima Kasih telah Bermain!";
		g2.drawString(thanks, getHorizontalCenter(thanks, g2, gp.screenWidth), currentY);
		currentY += sectionGap;

		g2.setFont(getScaledFont(Font.BOLD, 10));
		g2.setColor(new Color(180, 180, 255));
		String musicTitle = "=== MUSIC CREDITS ===";
		g2.drawString(musicTitle, getHorizontalCenter(musicTitle, g2, gp.screenWidth), currentY);
		currentY += lineHeight - 8;

		g2.setFont(getScaledFont(Font.PLAIN, 7));
		g2.setColor(new Color(200, 200, 200));

		String menuMusic = "MENU: 'Indonesia Pusaka' - Ismail Marzuki";
		g2.drawString(menuMusic, getHorizontalCenter(menuMusic, g2, gp.screenWidth), currentY);
		currentY += lineHeight - 12;

		String menuMusic2 = "Piano Cover by RIANWINDY";
		g2.drawString(menuMusic2, getHorizontalCenter(menuMusic2, g2, gp.screenWidth), currentY);
		currentY += lineHeight - 10;

		String music1 = "BGM: 'Time' - Hans Zimmer (Inception)";
		g2.drawString(music1, getHorizontalCenter(music1, g2, gp.screenWidth), currentY);
		currentY += lineHeight - 12;

		String music2 = "SFX: Undertale Character Voice Beeps";
		g2.drawString(music2, getHorizontalCenter(music2, g2, gp.screenWidth), currentY);

		g2.setFont(getScaledFont(Font.PLAIN, 11));
		g2.setColor(new Color(200, 200, 200));
		String esc = "Tekan ESC untuk kembali";
		g2.drawString(esc, getHorizontalCenter(esc, g2, gp.screenWidth), gp.screenHeight - 24);
	}
}
