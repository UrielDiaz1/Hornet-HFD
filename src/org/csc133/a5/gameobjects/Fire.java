package org.csc133.a5.gameobjects;

import com.codename1.charts.util.ColorUtil;
import com.codename1.ui.Font;
import com.codename1.ui.Graphics;
import com.codename1.ui.geom.Dimension;
import com.codename1.ui.geom.Point;
import org.csc133.a5.interfaces.Observer;
import java.util.Random;
import static com.codename1.ui.CN.*;

public class Fire extends Fixed implements Observer {
    private FireState fireState;
    private double size;
    private boolean selected = false;
    private final Random rand;
    private final FireDispatch subject;

    //````````````````````````````````````````````````````````````````````````
    // Observer Pattern
    @Override
    public void update(Observer o) {
        fireState.update(o);
    }

    //````````````````````````````````````````````````````````````````````````
    // Fire State Pattern
    //
    private abstract class FireState {
        Fire getFire() {
            return Fire.this;
        }

        void start() {}

        void grow() {}

        void shrink(int water) {}

        void update(Observer o) {}

        void checkIfSelected(int x, int y) {}

        void localDraw(Graphics g, Point containerOrigin, Point screenOrigin) {}
    }

    //````````````````````````````````````````````````````````````````````````
    private class UnStarted extends FireState {
        @Override
        void start() {
            getFire().changeState(new Burning());
        }
    }

    //````````````````````````````````````````````````````````````````````````
    private class Burning extends FireState {
        @Override
        void grow() {
            if(size > 0) {
                int growthRate = 4;

                // This is to deal with the exponential growth, where
                // it grows too fast once it becomes a certain size.
                //
                if(size > 200) {
                    growthRate /= 2;
                }

                double randomScaling = 1 + rand.nextInt(growthRate) / 100f;
                scale(randomScaling, randomScaling);
                size *= randomScaling;
            }
        }

        @Override
        void shrink(int water) {
            if(size <= (water / 5f)) {
                setDimensions(0, 0);
                size = 0;
                subject.detach(Fire.this);
                getFire().changeState(new Extinguished());
            }
            else {
                scale(50f / water, 50f / water);
                size *= 50f / water;
            }
        }

        @Override
        void checkIfSelected(int x, int y) {
            if(y >= getY() - getHeight() * 2 &&
               y <= getY() + getHeight() * 2 &&
               x >= getX() - getWidth() * 2 &&
               x <= getX() + getWidth() * 2) {
                subject.setSelectedFire(Fire.this);
            }
        }

        @Override
        void update(Observer o) {
            if(o != Fire.this) {
                selected = false;
                setColor(ColorUtil.MAGENTA);
            }
            else {
                selected = true;
                setColor(ColorUtil.GREEN);
            }
        }

        @Override
        void localDraw(Graphics g, Point containerOrigin, Point screenOrigin) {
            g.fillArc(0, 0, getWidth(), getWidth(), 0, 360);

            applyTextTransforms(g, containerOrigin, screenOrigin);
            g.drawString("" + (int) size, (int) size / 2, (int) size / 2);
        }
    }

    //````````````````````````````````````````````````````````````````````````
    private class Extinguished extends FireState {}

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public Fire(Dimension mapSize, int radius, FireDispatch subject) {
        super(ColorUtil.MAGENTA, mapSize, radius * 2, radius * 2);
        setFont(Font.createSystemFont(FACE_MONOSPACE, STYLE_BOLD, SIZE_SMALL));
        rand = new Random();
        size = radius * 2;
        this.scale(1, -1);

        fireState = new UnStarted();
        this.subject = subject;
        this.subject.attach(this);
    }

    private void changeState(FireState fireState) {
        this.fireState = fireState;
    }

    public String currentState() {
        return fireState.getClass().getSimpleName();
    }

    void spawn(double x, double y) {
        this.translate(x, y);
    }

    void start() {
        fireState.start();
    }

    public void grow() {
        fireState.grow();
    }

    public void shrink(int water) {
        fireState.shrink(water);
    }

    double getRadius() {
        return size / 2f;
    }

    public double getArea() {
        return (Math.pow(getRadius(), 2) * Math.PI);
    }

    public int getSize() {
        return (int) size;
    }

    public void checkIfSelected(int x, int y) {
        fireState.checkIfSelected(x, y);
    }

    @Override
    public void localDraw(Graphics g, Point containerOrigin,
                                      Point screenOrigin) {
        fireState.localDraw(g, containerOrigin, screenOrigin);
    }
}