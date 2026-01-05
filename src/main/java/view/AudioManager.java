package view;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.net.URL;
import model.Settings;

public class AudioManager {
    private static AudioManager instance;
    private Settings settings;

    private Clip musicClip;
    private URL[] musicURLs;
    private int musicVolumeScale;

    private URL[] soundURLs;
    private int soundVolumeScale;

    private Clip talkingClip;

    public static AudioManager getInstance() {
        if (instance == null)
            instance = new AudioManager();
        return instance;
    }

    private AudioManager() {
        settings = Settings.getInstance();
        loadMusic();
        loadSounds();
    }

    private void loadMusic() {
        musicURLs = new URL[6];
        musicURLs[0] = getClass().getResource("/sounds/time.wav");
        musicURLs[1] = getClass().getResource("/sounds/prolog.wav");
        musicURLs[2] = getClass().getResource("/music/bgmusic1.wav");
        musicURLs[3] = getClass().getResource("/music/bgmusic2.wav");
        musicURLs[4] = getClass().getResource("/sounds/menu.wav");
        musicURLs[5] = getClass().getResource("/sounds/house_interior.wav");
        musicVolumeScale = settings.getMusicVolume();

        System.out.println("[AudioManager] Music loaded: time.wav, prolog.wav, menu.wav, house_interior.wav");
    }

    private void loadSounds() {
        soundURLs = new URL[14];
        soundURLs[0] = getClass().getResource("/sounds/cluck1.wav");
        soundURLs[1] = getClass().getResource("/sounds/cluck2.wav");
        soundURLs[2] = getClass().getResource("/sounds/pickup.wav");
        soundURLs[3] = getClass().getResource("/sounds/chaching.wav");
        soundURLs[4] = getClass().getResource("/sounds/select.wav");
        soundURLs[5] = getClass().getResource("/sounds/enter.wav");
        soundURLs[6] = getClass().getResource("/sounds/eggcracking.wav");
        soundURLs[7] = getClass().getResource("/sounds/trap.wav");
        soundURLs[8] = getClass().getResource("/sounds/unlockgate.wav");
        soundURLs[9] = getClass().getResource("/sounds/konamicode.wav");
        soundURLs[10] = getClass().getResource("/sounds/resetscore.wav");
        soundURLs[11] = getClass().getResource("/sounds/error.wav");
        soundURLs[12] = getClass().getResource("/sounds/talking.wav");
        soundVolumeScale = settings.getSoundVolume();

        System.out.println("[AudioManager] Sounds loaded including talking.wav and chaching.wav");
    }

    public void playMusic(int i) {
        try {
            if (i < 0 || i >= musicURLs.length || musicURLs[i] == null) {
                System.err.println("Music index " + i + " not available, using fallback");
                i = 2;
            }
            if (musicClip != null)
                musicClip.stop();
            AudioInputStream ais = AudioSystem.getAudioInputStream(musicURLs[i]);
            musicClip = AudioSystem.getClip();
            musicClip.open(ais);
            setVolume(musicClip, musicVolumeScale);
            musicClip.loop(Clip.LOOP_CONTINUOUSLY);
            musicClip.start();
        } catch (Exception e) {
            System.err.println("Failed to play music " + i + ", trying fallback: " + e.getMessage());
            if (i != 2) {
                playMusic(2);
            }
        }
    }

    public void playPrologMusic() {
        playMusic(1);
    }

    public void playMainBGM() {
        playMusic(0);
    }

    public void playMenuMusic() {
        playMusic(4);
    }

    public void playHouseInteriorMusic() {
        playMusic(5);
    }

    public void stopMusic() {
        if (musicClip != null)
            musicClip.stop();
    }

    public void playTalkingSound() {
        try {
            if (soundURLs[12] == null)
                return;

            if (talkingClip != null && talkingClip.isRunning()) {
                talkingClip.stop();
            }

            AudioInputStream ais = AudioSystem.getAudioInputStream(soundURLs[12]);
            talkingClip = AudioSystem.getClip();
            talkingClip.open(ais);
            setVolume(talkingClip, Math.max(1, soundVolumeScale - 1));
            talkingClip.start();
        } catch (Exception e) {
        }
    }

    public void playSound(int i) {
        try {
            if (i < 0 || i >= soundURLs.length || soundURLs[i] == null) {
                System.err.println("[AudioManager] Sound index " + i + " not available");
                return;
            }
            AudioInputStream ais = AudioSystem.getAudioInputStream(soundURLs[i]);
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            setVolume(clip, soundVolumeScale);
            clip.start();
        } catch (Exception e) {
            System.err.println("[AudioManager] Error playing sound " + i + ": " + e.getMessage());
        }
    }

    private void setVolume(Clip clip, int scale) {
        FloatControl fc = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        float vol = -80f;
        switch (scale) {
            case 1:
                vol = -20f;
                break;
            case 2:
                vol = -12f;
                break;
            case 3:
                vol = -5f;
                break;
            case 4:
                vol = 1f;
                break;
            case 5:
                vol = 6f;
                break;
        }
        fc.setValue(vol);
    }

    public void lowerMusicVolume() {
        if (musicVolumeScale > 0)
            musicVolumeScale--;
    }

    public void increaseMusicVolume() {
        if (musicVolumeScale < 5)
            musicVolumeScale++;
    }

    public void lowerSoundVolume() {
        if (soundVolumeScale > 0)
            soundVolumeScale--;
    }

    public void increaseSoundVolume() {
        if (soundVolumeScale < 5)
            soundVolumeScale++;
    }

    public int getMusicVolumeScale() {
        return musicVolumeScale;
    }

    public int getSoundVolumeScale() {
        return soundVolumeScale;
    }

    public void play(int i) {
        playSound(i);
    }

    public void lowerVolume() {
        lowerMusicVolume();
    }

    public void increaseVolume() {
        increaseMusicVolume();
    }

    public void checkVolume() {
        settings.setMusicVolume(musicVolumeScale);
        settings.setSoundVolume(soundVolumeScale);
        settings.saveConfigFile();
    }

    public int getVolumeScale() {
        return musicVolumeScale;
    }
}
