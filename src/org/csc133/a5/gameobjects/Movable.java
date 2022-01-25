package org.csc133.a5.gameobjects;

import com.codename1.ui.geom.Dimension;

public abstract class Movable extends GameObject {
    private int speed = 0;
    private int heading = 0;

    public Movable(int color, Dimension mapSize, int width, int height) {
        super(color, mapSize, width, height);
    }

    public void move(long elapsedTimeInMillis) {
        double speedMultiplier = calcSpeedMultiplier(elapsedTimeInMillis);
        double angle = Math.toRadians(heading + 90);

        // The speed is multiplied by SPEED_MULTIPLIER to indirectly reduce
        // the fuel cost of the helicopter's movement.
        //
        this.translate(speed * speedMultiplier * Math.cos(angle),
                       speed * speedMultiplier * Math.sin(angle));
    }

    private double calcSpeedMultiplier(long elapsedTime) {
        return (elapsedTime / 100f) * 4;
    }

    public int getSpeed() {
        return speed;
    }

    public int getHeading() {
        return heading;
    }

    void setSpeed(int speed) {
        this.speed = speed;
    }

    void setHeading(int heading) {
        this.heading = Math.floorMod(heading, 360);
    }
}
