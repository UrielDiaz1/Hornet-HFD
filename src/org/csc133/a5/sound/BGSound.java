package org.csc133.a5.sound;

public class BGSound extends Sound implements Runnable {

    public BGSound(String fileName, int volume) {
        super(fileName, volume);
        new Thread(this).start();
    }

    @Override
    public void run() {
        this.play();
    }
}
