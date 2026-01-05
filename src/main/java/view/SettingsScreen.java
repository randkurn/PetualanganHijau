package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import controller.GamePanel;
import model.Settings;

public class SettingsScreen extends UI {
	AudioManager audio;
	BufferedImage settingsBackground;

	protected SettingsScreen(GamePanel gp) {
		super(gp);
		audio = AudioManager.getInstance();
		totalOptions = 7; // Increased from 5 to include minimap and UI toggles

		try {
			settingsBackground = ImageIO.read(getClass().getResourceAsStream("/settingsbg/settings.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void draw(Graphics2D g2) {
		if (settingsBackground != null) {
			g2.drawImage(settingsBackground, 0, 0, gp.screenWidth, gp.screenHeight, null);
		} else {
			g2.setColor(new Color(40, 40, 60));
			g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
		}

		g2.setFont(getScaledFont(Font.PLAIN, 80));
		g2.setColor(Color.white);

		String settingsTitle = "PENGATURAN";
		int titleY = gp.tileSize;
		g2.drawString(settingsTitle, getHorizontalCenter(settingsTitle, g2, gp.screenWidth), titleY);

		String music = "MUSIC VOLUME";
		String sound = "SOUND VOLUME";
		String fullscreen = "FULL SCREEN";
		String language = "BAHASA";
		String back = "RETURN";

		g2.setFont(getScaledFont(Font.PLAIN, 28));

		int leftX = (int) (gp.screenWidth * 0.2);
		int rightX = (int) (gp.screenWidth * 0.6);
		int startY = (int) (gp.screenHeight * 0.28);
		int lineHeight = (int) (gp.tileSize * 0.85);

		g2.drawString(music, leftX, startY);
		g2.drawRect(rightX, startY - (int) (15 * gp.scale / 4.0), (int) (100 * gp.scale / 4.0 * 1.5),
				(int) (20 * gp.scale / 4.0));
		int volWidth = (int) ((100 * gp.scale / 4.0 * 1.5) * (audio.getMusicVolumeScale() / 5.0));
		g2.fillRect(rightX, startY - (int) (15 * gp.scale / 4.0), volWidth, (int) (20 * gp.scale / 4.0));
		if (selectPosition == 0)
			g2.drawString(">", leftX - (int) (20 * gp.scale / 4.0), startY);

		g2.drawString(sound, leftX, startY + lineHeight);
		g2.drawRect(rightX, startY + lineHeight - (int) (15 * gp.scale / 4.0), (int) (100 * gp.scale / 4.0 * 1.5),
				(int) (20 * gp.scale / 4.0));
		int soundWidth = (int) ((100 * gp.scale / 4.0 * 1.5) * (audio.getSoundVolumeScale() / 5.0));
		g2.fillRect(rightX, startY + lineHeight - (int) (15 * gp.scale / 4.0), soundWidth, (int) (20 * gp.scale / 4.0));
		if (selectPosition == 1)
			g2.drawString(">", leftX - (int) (20 * gp.scale / 4.0), startY + lineHeight);

		g2.drawString(fullscreen, leftX, startY + lineHeight * 2);
		String fsStatus = gp.uiM.getFullScreen() ? "ON" : "OFF";
		g2.drawString(fsStatus, rightX, startY + lineHeight * 2);
		if (selectPosition == 2)
			g2.drawString(">", leftX - (int) (20 * gp.scale / 4.0), startY + lineHeight * 2);

		g2.drawString(language, leftX, startY + lineHeight * 3);
		String langStatus = Settings.getInstance().getGameLanguage().equals("id") ? "INDONESIA" : "ENGLISH";
		g2.drawString(langStatus, rightX, startY + lineHeight * 3);
		if (selectPosition == 3)
			g2.drawString(">", leftX - (int) (20 * gp.scale / 4.0), startY + lineHeight * 3);

		// 4: Show Minimap
		String minimap = "SHOW MINIMAP";
		g2.drawString(minimap, leftX, startY + lineHeight * 4);
		String minimapStatus = settings.getShowMinimap() ? "ON" : "OFF";
		g2.drawString(minimapStatus, rightX, startY + lineHeight * 4);
		if (selectPosition == 4)
			g2.drawString(">", leftX - (int) (20 * gp.scale / 4.0), startY + lineHeight * 4);

		String showUI = "SHOW UI";
		g2.drawString(showUI, leftX, startY + lineHeight * 5);
		String uiStatus = settings.getShowUI() ? "ON" : "OFF";
		g2.drawString(uiStatus, rightX, startY + lineHeight * 5);
		if (selectPosition == 5)
			g2.drawString(">", leftX - (int) (20 * gp.scale / 4.0), startY + lineHeight * 5);

		g2.drawString(back, leftX, startY + lineHeight * 6);
		if (selectPosition == 6)
			g2.drawString(">", leftX - (int) (20 * gp.scale / 4.0), startY + lineHeight * 6);
	}
}
