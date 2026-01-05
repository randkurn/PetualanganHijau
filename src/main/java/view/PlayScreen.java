package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

import controller.GamePanel;
import controller.StateManager.gameState;
import controller.TimeManager;

public class PlayScreen extends UI {
	private double playTime = 0;
	private String message = "";
	private int messageCounter = 0;

	private BufferedImage coinIcon;
	private BufferedImage sunIcon, moonIcon;

	private String[] dialogLines = new String[0];
	private int currentLineOffset = 0;
	private String speakerName = "";
	private boolean dialogActive = false;
	private boolean hasChoices = false;
	private String[] choices = new String[0];
	private int selectedChoice = 0;
	private DialogCallback choiceCallback;

	private boolean autoAdvance = false;
	private int autoAdvanceCounter = 0;
	private final int AUTO_ADVANCE_DELAY = 120;

	private int typingIndex = 0;
	private int typingSpeed = 2;
	private int typingCounter = 0;
	private boolean typingComplete = false;
	private int talkSoundCounter = 0;

	private Runnable dialogContinueCallback = null;

	public interface DialogCallback {
		void onChoiceSelected(int index, String text);
	}

	protected PlayScreen(GamePanel gp) {
		super(gp);
		loadUiTextures();
	}

	private void loadUiTextures() {
		coinIcon = loadImage("/ui/CoinIcon_16x18.png");
		sunIcon = loadImage("/ui/sun.png");
		moonIcon = loadImage("/ui/moon.png");
	}

	private BufferedImage loadImage(String path) {
		try (InputStream stream = getClass().getResourceAsStream(path)) {
			if (stream != null)
				return ImageIO.read(stream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void update() {
		gameState currentState = gp.stateM.getCurrentState();

		if (currentState == gameState.PLAY) {
			playTime += (double) 1 / 60;
			if (messageCounter > 0) {
				messageCounter--;
				if (messageCounter == 0)
					message = "";
			}
		}

		if (dialogActive && autoAdvance && typingComplete) {
			autoAdvanceCounter++;
			if (autoAdvanceCounter >= AUTO_ADVANCE_DELAY) {
				autoAdvanceCounter = 0;
				advanceDialog();
			}
		}

		if (dialogActive && !typingComplete && !autoAdvance) {
			typingCounter++;
			if (typingCounter >= typingSpeed) {
				typingCounter = 0;
				int totalChars = 0;
				for (int i = 0; i <= currentLineOffset && i < dialogLines.length; i++) {
					totalChars += dialogLines[i].length();
				}
				typingIndex++;

				talkSoundCounter++;
				if (talkSoundCounter >= 3) {
					talkSoundCounter = 0;
					AudioManager.getInstance().playTalkingSound();
				}

				if (typingIndex >= totalChars) {
					typingComplete = true;
				}
			}
		}
	}

	public void draw(Graphics2D g2) {
		boolean showUI = model.Settings.getInstance().getShowUI();

		if (showUI && !speakerName.equals("Narasi")) {
			drawPlayerStats(g2);
			drawTimeDisplay(g2);
		}

		if (!message.isEmpty()) {
			drawNotification(g2);
		}

		if (dialogActive)
			drawDialogBox(g2);
	}

	public void drawNotification(Graphics2D g2) {
		float uiScale = (float) gp.scale / 4.0f;
		g2.setFont(getScaledFont(Font.BOLD, 12));
		String[] lines = message.split("\n");

		int maxLineWidth = 0;
		for (String line : lines) {
			maxLineWidth = Math.max(maxLineWidth, g2.getFontMetrics().stringWidth(line));
		}

		int x = (gp.screenWidth - maxLineWidth) / 2;
		int y = (int) (100 * uiScale);
		int padding = (int) (15 * uiScale);
		int lineHeight = (int) (20 * uiScale);
		int boxHeight = (lines.length * lineHeight) + padding;

		g2.setColor(new Color(0, 0, 0, 180));
		g2.fillRoundRect(x - padding, y - (int) (25 * uiScale), maxLineWidth + (padding * 2), boxHeight, 15, 15);
		g2.setColor(new Color(100, 255, 100));
		g2.setStroke(new java.awt.BasicStroke(2 * uiScale));
		g2.drawRoundRect(x - padding, y - (int) (25 * uiScale), maxLineWidth + (padding * 2), boxHeight, 15, 15);

		g2.setColor(Color.WHITE);
		for (int i = 0; i < lines.length; i++) {
			g2.drawString(lines[i], x, y + (i * lineHeight));
		}
	}

	private void drawPlayerStats(Graphics2D g2) {
		float uiScale = (float) gp.scale / 4.0f;
		g2.setFont(getScaledFont(Font.PLAIN, 12));
		g2.setColor(Color.WHITE);
		if (coinIcon != null)
			g2.drawImage(coinIcon, (int) (25 * uiScale), (int) (25 * uiScale), (int) (32 * uiScale),
					(int) (36 * uiScale), null);
		g2.drawString("x " + gp.player.gold, (int) (65 * uiScale), (int) (52 * uiScale));
	}

	private void drawTimeDisplay(Graphics2D g2) {
		float uiScale = (float) gp.scale / 4.0f;
		TimeManager tm = TimeManager.getInstance();
		int x = gp.screenWidth - (int) (180 * uiScale);
		int y = (int) (25 * uiScale);

		g2.setColor(new Color(0, 0, 0, 150));
		g2.fillRoundRect(x, y, (int) (160 * uiScale), (int) (60 * uiScale), 10, 10);

		BufferedImage icon = tm.isNight() ? moonIcon : sunIcon;
		if (icon != null)
			g2.drawImage(icon, x + (int) (10 * uiScale), y + (int) (15 * uiScale), (int) (30 * uiScale),
					(int) (30 * uiScale), null);

		g2.setFont(getScaledFont(Font.BOLD, 12));
		g2.setColor(Color.WHITE);
		g2.drawString(tm.getTimeString(), x + (int) (50 * uiScale), y + (int) (30 * uiScale));
		g2.setFont(getScaledFont(Font.PLAIN, 9));
		g2.drawString("Hari " + tm.getCurrentDay(), x + (int) (50 * uiScale), y + (int) (50 * uiScale));
	}

	private void drawDialogBox(Graphics2D g2) {
		float uiScale = (float) gp.scale / 4.0f;
		int maxHeight = (int) (gp.screenHeight * 0.25);
		int boxHeight = maxHeight;
		int boxY = speakerName.equals("Narasi") ? (gp.screenHeight - boxHeight) / 2
				: gp.screenHeight - boxHeight - (int) (20 * uiScale);
		int padding = (int) (20 * uiScale);

		g2.setColor(new Color(0, 0, 0, speakerName.equals("Narasi") ? 0 : 220));
		if (!speakerName.equals("Narasi")) {
			g2.fillRoundRect(padding, boxY, gp.screenWidth - padding * 2, boxHeight, 15, 15);
			g2.setColor(Color.WHITE);
			g2.drawRoundRect(padding, boxY, gp.screenWidth - padding * 2, boxHeight, 15, 15);
		}

		int textX = padding * 2 + (int) (10 * uiScale);
		int textY = boxY + (speakerName.equals("Narasi") ? (int) (60 * uiScale) : (int) (40 * uiScale));

		if (!speakerName.isEmpty() && !speakerName.equals("Narasi")) {
			g2.setFont(getScaledFont(Font.BOLD, 14));
			g2.setColor(new Color(100, 200, 255));
			g2.drawString(speakerName + ":", textX, textY);
			textY += (int) (30 * uiScale);
		}

		g2.setFont(getScaledFont(Font.PLAIN, 12));
		g2.setColor(Color.WHITE);
		int maxLines = hasChoices ? 2 : 4;
		int linesToShow = Math.min(maxLines, dialogLines.length - currentLineOffset);

		for (int i = 0; i < linesToShow; i++) {
			String line = dialogLines[currentLineOffset + i];
			String displayText = line;

			if (!typingComplete && !autoAdvance) {
				int lineStartIndex = 0;
				for (int j = 0; j < currentLineOffset + i && j < dialogLines.length; j++) {
					lineStartIndex += dialogLines[j].length();
				}
				int charsToShow = typingIndex - lineStartIndex;
				if (charsToShow < 0)
					charsToShow = 0;
				if (charsToShow < line.length()) {
					displayText = line.substring(0, Math.min(charsToShow, line.length()));
				}
			}

			int drawX = textX;
			if (speakerName.equals("Narasi")) {
				int stringWidth = g2.getFontMetrics().stringWidth(displayText);
				drawX = (gp.screenWidth - stringWidth) / 2;
			}
			g2.drawString(displayText, drawX, textY);
			textY += (int) (25 * uiScale);
		}

		if (hasChoices) {
			for (int i = 0; i < choices.length; i++) {
				g2.setColor(i == selectedChoice ? Color.YELLOW : Color.WHITE);
				g2.drawString((i == selectedChoice ? "> " : "  ") + choices[i], textX + (int) (20 * uiScale), textY);
				textY += (int) (25 * uiScale);
			}
		}
	}

	public void drawInteractionHint(Graphics2D g2, int worldX, int worldY, String text) {
		int screenX = worldX - gp.player.worldX + gp.player.screenX;
		int screenY = worldY - gp.player.worldY + gp.player.screenY;

		g2.setFont(pressStart2P.deriveFont(Font.BOLD, 10));
		g2.setColor(new Color(0, 0, 0, 180));
		int width = g2.getFontMetrics().stringWidth(text);
		g2.fillRoundRect(screenX + (gp.tileSize / 2) - (width / 2) - 5, screenY - 20, width + 10, 20, 5, 5);
		g2.setColor(Color.WHITE);
		g2.drawString(text, screenX + (gp.tileSize / 2) - (width / 2), screenY - 5);
	}

	public void showMessage(String text) {
		String formatted = text;
		if (text.length() > 30) {
			formatted = formatted.replace(". ", ".\n");
			formatted = formatted.replace(".  ", ".\n");

			StringBuilder sb = new StringBuilder();
			String[] lines = formatted.split("\n");
			for (String line : lines) {
				if (line.length() > 45) {
					int mid = line.indexOf(" ", 30);
					if (mid != -1) {
						sb.append(line.substring(0, mid)).append("\n").append(line.substring(mid + 1)).append("\n");
						continue;
					}
				}
				sb.append(line).append("\n");
			}
			formatted = sb.toString().trim();
		}

		this.message = formatted;
		if (text.contains("TUTORIAL") || text.contains("OBJEKTIF") || text.contains("TERBUKA")) {
			this.messageCounter = 300;
		} else if (text.contains("Berhasil memungut") || text.contains("Mendapatkan")) {
			this.messageCounter = 120;
		} else if (text.contains("Total:") || text.contains("Gold") || text.contains("Rp ") || text.contains("saldo")) {
			this.messageCounter = 180;
		} else {
			this.messageCounter = 120;
		}
	}

	public void resetMessage() {
		this.message = "";
		this.messageCounter = 0;
	}

	public void resetTimer() {
		this.playTime = 0;
	}

	public int getPlayTime() {
		return (int) playTime;
	}

	public PlayScreen getDialogBox() {
		return this;
	}

	public void showDialog(String text, String speaker) {
		showDialog(text, speaker, null);
	}

	public void showDialog(String text, String speaker, Runnable onComplete) {
		this.speakerName = speaker;
		this.dialogLines = text.split("\n");
		this.currentLineOffset = 0;
		this.dialogActive = true;
		this.hasChoices = false;
		this.dialogContinueCallback = onComplete;
		this.autoAdvance = false;

		this.typingIndex = 9999;
		this.typingCounter = 0;
		this.typingComplete = true;
		this.talkSoundCounter = 0;

		if (gp.inputM != null && gp.inputM.getPlayInput() != null) {
			gp.inputM.getPlayInput().interact = false;
			gp.inputM.getPlayInput().enter = false;
		}

		gp.stateM.setCurrentState(gameState.DIALOGUE);

		AudioManager.getInstance().playTalkingSound();
	}

	public void showDialogAutoNext(String text, String speaker) {
		showDialog(text, speaker, null);
		this.autoAdvance = true;
		this.autoAdvanceCounter = 0;
	}

	public void clearDialog() {
		this.dialogActive = false;
		this.speakerName = "";
		this.autoAdvance = false;
	}

	public void showDialogWithChoices(String text, String speaker, String[] options, DialogCallback cb) {
		showDialog(text, speaker);
		// But for now, we'll just allow it. Note: having choices limits lines to 2.
		this.hasChoices = true;
		this.choices = options;
		this.selectedChoice = 0;
		this.choiceCallback = cb;
	}

	public void advanceDialog() {
		if (!typingComplete) {
			typingComplete = true;
			int totalChars = 0;
			for (int i = 0; i < dialogLines.length; i++) {
				totalChars += dialogLines[i].length();
			}
			typingIndex = totalChars;
			return;
		}

		int maxLines = hasChoices ? 2 : 4;
		currentLineOffset += maxLines;

		if (currentLineOffset >= dialogLines.length) {
			hideDialog();
		} else {
			this.typingIndex = 0;
			this.typingCounter = 0;
			this.typingComplete = autoAdvance;
			this.talkSoundCounter = 0;
			this.autoAdvanceCounter = 0;
		}
	}

	public void hideDialog() {
		this.dialogActive = false;
		this.speakerName = "";
		this.autoAdvance = false;
		this.autoAdvanceCounter = 0;

		if (gp.inputM != null && gp.inputM.getCurrentInput() != null) {
			gp.inputM.getCurrentInput().resetKeyPress();
		}

		Runnable callback = this.dialogContinueCallback;
		this.dialogContinueCallback = null;

		if (gp.stateM.getCurrentState() == gameState.DIALOGUE) {
			gp.stateM.revertPreviousState();
		} else {
			gp.stateM.setCurrentState(gameState.PLAY);
		}

		if (callback != null) {
			callback.run();
		}
	}

	public boolean isActive() {
		return dialogActive;
	}

	public boolean hasChoices() {
		return hasChoices;
	}

	public void moveChoiceUp() {
		selectedChoice = (selectedChoice > 0) ? selectedChoice - 1 : choices.length - 1;
	}

	public void moveChoiceDown() {
		selectedChoice = (selectedChoice < choices.length - 1) ? selectedChoice + 1 : 0;
	}

	public void confirmChoice() {
		hideDialog();
		if (choiceCallback != null)
			choiceCallback.onChoiceSelected(selectedChoice, choices[selectedChoice]);
	}
}
