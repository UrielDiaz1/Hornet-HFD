package org.csc133.a5.sound;

import com.codename1.media.Media;
import com.codename1.media.MediaManager;
import com.codename1.ui.Display;
import java.io.IOException;
import java.io.InputStream;

public class Sound {
    private final int originalVolume;
    private int volume;
    private Media m;

    public Sound(String fileName, int originalVolume) {
        this.originalVolume = originalVolume;
        volume = originalVolume;
        String type;

        if(fileName.contains(".wav")) {
            type = "audio/wav";
        }
        else
            type = "audio/mp3";

        while(m == null) {
            try {
                // If it fails to initialize, keep trying.
                //
                InputStream is = Display.getInstance()
                        .getResourceAsStream(getClass(), "/" + fileName);
                m = MediaManager.createMedia(is, type);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void turnOffVolume() {
        volume = 0;
        m.setVolume(volume);
    }

    public void turnOnVolume() {
        volume = originalVolume;
        m.setVolume(volume);
    }

    public void stop() {
        m.cleanup();
    }

    public void play() {
        play(0);
    }

    public void play(int startTime) {
        m.setVolume(volume);
        m.setTime(startTime);
        m.play();
    }
}