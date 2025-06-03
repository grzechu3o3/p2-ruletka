package org.ruletka.guiClient;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;

public class Soundplayer {
    private Clip audio;

    public Soundplayer(String path) {
        try {
            InputStream audioSrc = GuiClient.class.getResourceAsStream(path);
            if (audioSrc == null) {
                throw new IllegalArgumentException("Nie znaleziono pliku dzwiek.wav w resources!");
            }

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(audioSrc);

            audio = AudioSystem.getClip();
            audio.open(audioIn);
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
            e.printStackTrace();
        }

    }
    public void play() {
        try {
            if(audio == null) return;
            if(audio.isRunning()) audio.stop();
            audio.setFramePosition(0);
            audio.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void stop() {
        if(audio == null) return;
        audio.stop();
    }
    public void close() {
        if(audio == null) return;
        audio.close();
    }
}
