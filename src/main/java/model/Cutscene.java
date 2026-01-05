package model;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import controller.GamePanel;

public class Cutscene {
    private GamePanel gp;
    private CutsceneStep[] steps;
    private int currentStep;
    private boolean isPlaying;
    private int frameCounter;
    private Font font;
    
    public enum CutsceneType {
        FADE_IN,
        FADE_OUT,
        TEXT_DIALOG,
        CHARACTER_SPRITE,
        WAIT
    }
    
    public static class CutsceneStep {
        public CutsceneType type;
        public String text;
        public String speaker;
        public BufferedImage sprite;
        public int duration;
        public Color backgroundColor;
        
        public CutsceneStep(CutsceneType type, int duration) {
            this.type = type;
            this.duration = duration;
            this.backgroundColor = Color.BLACK;
        }
        
        public CutsceneStep setText(String speaker, String text) {
            this.speaker = speaker;
            this.text = text;
            return this;
        }
        
        public CutsceneStep setSprite(BufferedImage sprite) {
            this.sprite = sprite;
            return this;
        }
        
        public CutsceneStep setBgColor(Color color) {
            this.backgroundColor = color;
            return this;
        }
    }
    
    public Cutscene(GamePanel gp) {
        this.gp = gp;
        this.currentStep = 0;
        this.isPlaying = false;
        this.frameCounter = 0;
        
        try {
            this.font = Font.createFont(Font.TRUETYPE_FONT, 
                getClass().getResourceAsStream("/font/PressStart2P-Regular.ttf"))
                .deriveFont(Font.PLAIN, 14);
        } catch (Exception e) {
            this.font = new Font("Arial", Font.PLAIN, 14);
        }
    }
    
    public void start(CutsceneStep[] steps) {
        this.steps = steps;
        this.currentStep = 0;
        this.isPlaying = true;
        this.frameCounter = 0;
    }
    
    public void update() {
        if (!isPlaying || steps == null || currentStep >= steps.length) {
            return;
        }
        
        CutsceneStep step = steps[currentStep];
        frameCounter++;
        
        if (frameCounter >= step.duration) {
            nextStep();
        }
    }
    
    public void skip() {
        if (isPlaying && steps != null && currentStep < steps.length) {
            nextStep();
        }
    }
    
    private void nextStep() {
        frameCounter = 0;
        currentStep++;
        
        if (currentStep >= steps.length) {
            end();
        }
    }
    
    private void end() {
        isPlaying = false;
        currentStep = 0;
        frameCounter = 0;
    }
    
    public void draw(Graphics2D g2) {
        if (!isPlaying || steps == null || currentStep >= steps.length) {
            return;
        }
        
        CutsceneStep step = steps[currentStep];
        
        switch (step.type) {
            case FADE_IN:
            case FADE_OUT:
                int alpha = 0;
                float progress = (float) frameCounter / step.duration;
                
                if (step.type == CutsceneType.FADE_IN) {
                    alpha = (int) (255 * (1 - progress));
                } else {
                    alpha = (int) (255 * progress);
                }
                
                g2.setColor(new Color(step.backgroundColor.getRed(), 
                                     step.backgroundColor.getGreen(), 
                                     step.backgroundColor.getBlue(), 
                                     Math.max(0, Math.min(255, alpha))));
                g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
                break;
                
            case TEXT_DIALOG:
                drawDialog(g2, step);
                break;
                
            case CHARACTER_SPRITE:
                if (step.sprite != null) {
                    int x = (gp.screenWidth - step.sprite.getWidth()) / 2;
                    int y = (gp.screenHeight - step.sprite.getHeight()) / 2;
                    g2.drawImage(step.sprite, x, y, null);
                }
                break;
                
            case WAIT:
                break;
        }
        
        drawSkipPrompt(g2);
    }
    
    private void drawDialog(Graphics2D g2, CutsceneStep step) {
        int boxHeight = 150;
        int boxY = gp.screenHeight - boxHeight - 20;
        
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(20, boxY, gp.screenWidth - 40, boxHeight);
        
        g2.setColor(new Color(255, 255, 255, 100));
        g2.drawRect(20, boxY, gp.screenWidth - 40, boxHeight);
        
        g2.setColor(Color.WHITE);
        g2.setFont(font.deriveFont(Font.BOLD, 16));
        
        if (step.speaker != null) {
            g2.drawString(step.speaker, 40, boxY + 30);
        }
        
        g2.setFont(font.deriveFont(Font.PLAIN, 14));
        if (step.text != null) {
            String[] lines = step.text.split("\n");
            int yOffset = step.speaker != null ? 60 : 40;
            
            for (String line : lines) {
                g2.drawString(line, 40, boxY + yOffset);
                yOffset += 25;
            }
        }
    }
    
    private void drawSkipPrompt(Graphics2D g2) {
        g2.setFont(font.deriveFont(Font.PLAIN, 10));
        g2.setColor(new Color(200, 200, 200));
        String prompt = "SPACE: Skip";
        int x = gp.screenWidth - g2.getFontMetrics().stringWidth(prompt) - 20;
        g2.drawString(prompt, x, gp.screenHeight - 10);
    }
    
    public boolean isPlaying() {
        return isPlaying;
    }
}

