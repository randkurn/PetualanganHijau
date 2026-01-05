package controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import view.AudioManager;
import model.Settings;

public abstract class InputHandler implements KeyListener {
    Settings settings;
    AudioManager audio;
    GamePanel gp;

    public boolean up, down, left, right;
    public boolean select, enter;
    public boolean cluck;
    public boolean paused;
    public boolean toolUse, toolSwitch;
    public boolean interact;

    protected InputHandler(GamePanel gp) {
        this.gp = gp;
        audio = AudioManager.getInstance();
        settings = Settings.getInstance();
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();

        switch (keyCode) {
            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:
            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:
                up = false;
                left = false;
                down = false;
                right = false;
                select = false;
                break;

            case KeyEvent.VK_H:
                cluck = false;
                break;

            case KeyEvent.VK_ESCAPE:
                paused = false;
                break;
            case KeyEvent.VK_ENTER:
                enter = false;
                break;
        }
    }

    public void resetKeyPress() {
        up = false;
        down = false;
        left = false;
        right = false;
        select = false;
        enter = false;
        cluck = false;
        paused = false;
        interact = false;
        toolUse = false;
        toolSwitch = false;
    }
}
