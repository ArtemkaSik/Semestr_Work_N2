package util;

import javax.sound.sampled.*;
import java.io.IOException;
import java.util.Objects;

public class SoundPlayer {

    private Clip mainMusic;

    public SoundPlayer() {
        loadSounds();
    }

    private void loadSounds() {
        try {
            mainMusic = loadSound("/sound/main_sound.wav");
        } catch (Exception e) {
            System.out.println("Warning: Could not load game music. Game will continue without sound.");
            mainMusic = null;
        }
    }

    private Clip loadSound(String path) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(
                Objects.requireNonNull(getClass().getResource(path))
        );
        Clip clip = AudioSystem.getClip();
        clip.open(audioInputStream);
        return clip;
    }

    public void playMusic() {
        if (mainMusic != null) {
            try {
                mainMusic.loop(Clip.LOOP_CONTINUOUSLY);
                mainMusic.start();
            } catch (Exception e) {
                System.out.println("Warning: Could not play game music. Game will continue without sound.");
            }
        }
    }
}
