package org.csc133.a5.gameobjects;

import com.codename1.ui.Font;
import com.codename1.ui.Graphics;
import com.codename1.ui.Transform;
import com.codename1.ui.geom.Dimension;
import com.codename1.ui.geom.Point;
import org.csc133.a5.GameWorld;
import org.csc133.a5.gameobjects.shapes.Arc;
import org.csc133.a5.gameobjects.shapes.DecoratedTrapezoid;
import org.csc133.a5.gameobjects.shapes.Rectangle;
import org.csc133.a5.interfaces.Steerable;
import java.util.ArrayList;

import static com.codename1.ui.CN.*;

public class Helicopter extends Movable implements Steerable {

    // Change COCKPIT RADIUS to scale helicopter's size.
    //
    private final int   COCKPIT_RADIUS           = 40;
    private final float COCKPIT_SHIFT_Y          = COCKPIT_RADIUS * 0.80f;
    private final float ENGINE_BLOCK_WIDTH       = COCKPIT_RADIUS * 1.8f;
    private final float ENGINE_BLOCK_HEIGHT      = ENGINE_BLOCK_WIDTH / 3;
    private final float ENGINE_BLOCK_SHIFT_Y     = ENGINE_BLOCK_HEIGHT / 2;
    private final float BLADE_WIDTH              = COCKPIT_RADIUS * 0.27f;
    private final float BLADE_LENGTH             = COCKPIT_RADIUS * 6.5f;
    private final float BLADE_SHIFT_Y            = ENGINE_BLOCK_HEIGHT / 2f;
    private final int   BLADE_STARTING_ANGLE     = 42;
    private final float BLADE_SHAFT_RADIUS       = BLADE_WIDTH * 2 / 3;
    private final float BLADE_SHAFT_SHIFT_Y      = ENGINE_BLOCK_HEIGHT / 2f;
    private final float SKIDS_WIDTH              = COCKPIT_RADIUS * 0.2f;
    private final float SKIDS_HEIGHT             = COCKPIT_RADIUS * 2.85f;
    private final float SKIDS_SHIFT_X            = SKIDS_HEIGHT * 0.48f;
    private final float SKIDS_SHIFT_Y            = SKIDS_HEIGHT * 0.13f;
    private final float UPPER_TUBE_WIDTH         = SKIDS_WIDTH * 1.8f;
    private final float UPPER_TUBE_HEIGHT        = SKIDS_WIDTH / 2;
    private final float UPPER_TUBE_SHIFT_X       = SKIDS_HEIGHT / 2.5f;
    private final float UPPER_TUBE_SHIFT_Y       = SKIDS_HEIGHT * 0.38f;
    private final float LOWER_TUBE_WIDTH         = SKIDS_WIDTH * 2.15f;
    private final float LOWER_TUBE_HEIGHT        = UPPER_TUBE_HEIGHT * 1.35f;
    private final float LOWER_TUBE_SHIFT_X       = UPPER_TUBE_SHIFT_X * 0.98f;
    private final float LOWER_TUBE_SHIFT_Y       = UPPER_TUBE_SHIFT_Y * 0.3f;
    private final float TAIL_BOOM_WIDTH          = COCKPIT_RADIUS * 0.48f;
    private final float TAIL_BOOM_HEIGHT         = COCKPIT_RADIUS * 2.7f;
    private final float TAIL_BOOM_SHIFT_Y        = TAIL_BOOM_HEIGHT * .28f;
    private final float TAIL_TUBE_WIDTH          = SKIDS_WIDTH / 2;
    private final float TAIL_TUBE_SHIFT_Y        = TAIL_BOOM_HEIGHT * -.72f;
    private final float TAIL_ENGINE_WIDTH        = TAIL_BOOM_WIDTH * 0.55f;
    private final float TAIL_ENGINE_HEIGHT       = ENGINE_BLOCK_HEIGHT / 3;
    private final float TAIL_ENGINE_SHIFT_Y      = TAIL_BOOM_HEIGHT * -1.25f;
    private final float TAIL_STABILIZER_WIDTH    = TAIL_ENGINE_WIDTH * 1.2f;
    private final float TAIL_STABILIZER_HEIGHT   = COCKPIT_RADIUS * 0.1f;
    private final float TAIL_STABILIZER_SHIFT_X  = TAIL_ENGINE_WIDTH;
    private final float TAIL_STABILIZER_SHIFT_Y  = TAIL_BOOM_HEIGHT * -1.25f;
    private final float TAIL_ROTOR_SHAFT_WIDTH   = TAIL_ENGINE_WIDTH / 4;
    private final float TAIL_ROTOR_SHAFT_HEIGHT  = TAIL_ENGINE_HEIGHT / 3;
    private final float TAIL_ROTOR_SHAFT_SHIFT_X = TAIL_ENGINE_WIDTH * 0.6f;
    private final float TAIL_ROTOR_SHAFT_SHIFT_Y = TAIL_BOOM_HEIGHT * -1.25f;
    private final float TAIL_ROTOR_WIDTH         = TAIL_ROTOR_SHAFT_WIDTH;
    private final float TAIL_ROTOR_HEIGHT        = TAIL_ENGINE_HEIGHT * 2.5f;
    private final float TAIL_ROTOR_SHIFT_X       = TAIL_ENGINE_WIDTH * 0.6f +
                                                   TAIL_ROTOR_SHAFT_WIDTH;
    private final float TAIL_ROTOR_SHIFT_Y       = TAIL_BOOM_HEIGHT * -1.25f;

    //````````````````````````````````````````````````````````````````````````
    private class Cockpit extends Arc {
        public Cockpit(int partColor) {
            super(  partColor,
                    2 * COCKPIT_RADIUS,
                    2 * COCKPIT_RADIUS,
                    0, COCKPIT_SHIFT_Y,
                    1, 1,
                    0, 0, 360);
        }
    }

    //````````````````````````````````````````````````````````````````````````
    private class HelicopterEngineBlock extends Rectangle {
        public HelicopterEngineBlock(int partColor) {
            super(  partColor,
                    (int) ENGINE_BLOCK_WIDTH,
                    (int) ENGINE_BLOCK_HEIGHT,
                    0, -ENGINE_BLOCK_SHIFT_Y,
                    1, 1, 0);
        }
    }

    //````````````````````````````````````````````````````````````````````````
    private class HelicopterBlade extends Rectangle {
        public HelicopterBlade(int partColor) {
            super(  partColor,
                    (int) BLADE_LENGTH,
                    (int) BLADE_WIDTH,
                    0, -BLADE_SHIFT_Y,
                    1, 1, BLADE_STARTING_ANGLE);
        }

        @Override
        public void localDraw(Graphics g, Point containerOrigin,
                              Point screenOrigin) {
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        private void updateLocalTransforms(double rotationSpeed) {
            this.rotate(rotationSpeed);
        }
    }
    //````````````````````````````````````````````````````````````````````````
    private class HelicopterBladeShaft extends Arc {
        public HelicopterBladeShaft(int partColor) {
            super(  partColor,
                    (int) (BLADE_SHAFT_RADIUS),
                    (int) (BLADE_SHAFT_RADIUS),
                    0, -BLADE_SHAFT_SHIFT_Y,
                    1, 1,
                    0, 0, 360);
        }
    }

    //````````````````````````````````````````````````````````````````````````
    private class RightHelicopterSkid extends Rectangle {
        public RightHelicopterSkid(int partColor) {
            super(  partColor,
                    (int) SKIDS_WIDTH,
                    (int) SKIDS_HEIGHT,
                    SKIDS_SHIFT_X, SKIDS_SHIFT_Y,
                    1, 1,
                    0);
        }
    }

    //````````````````````````````````````````````````````````````````````````
    private class LeftHelicopterSkid extends Rectangle {
        public LeftHelicopterSkid(int partColor) {
            super(  partColor,
                    (int) SKIDS_WIDTH,
                    (int) SKIDS_HEIGHT,
                    -SKIDS_SHIFT_X, SKIDS_SHIFT_Y,
                    1, 1,
                    0);
        }
    }

    //````````````````````````````````````````````````````````````````````````
    private class UpperCrossTubes extends Rectangle {
        public UpperCrossTubes(int partColor) {
            super(  partColor,
                    (int) UPPER_TUBE_WIDTH,
                    (int) UPPER_TUBE_HEIGHT,
                    UPPER_TUBE_SHIFT_X, UPPER_TUBE_SHIFT_Y,
                    1, 1,
                    0);
        }

        @Override
        public void localDraw(Graphics g, Point containerOrigin,
                                          Point screenOrigin) {
            g.fillRect(0, 0, getWidth(), getHeight());

            // Draws parallel tube on the opposite side of helicopter.
            //
            g.fillRect((int) (-UPPER_TUBE_SHIFT_X * 2), 0,
                       getWidth(), getHeight());
        }
    }

    //````````````````````````````````````````````````````````````````````````
    private class LowerCrossTubes extends Rectangle {
        public LowerCrossTubes(int partColor) {
            super(  partColor,
                    (int) LOWER_TUBE_WIDTH,
                    (int) LOWER_TUBE_HEIGHT,
                    LOWER_TUBE_SHIFT_X, -LOWER_TUBE_SHIFT_Y,
                    1, 1,
                    0);
        }

        @Override
        public void localDraw(Graphics g, Point containerOrigin,
                                          Point screenOrigin) {
            g.fillRect(0, 0, getWidth(), getHeight());

            // Draws parallel tube on the opposite side of helicopter.
            //
            g.fillRect((int) (-LOWER_TUBE_SHIFT_X * 2), 0,
                       getWidth(), getHeight());
        }
    }

    //````````````````````````````````````````````````````````````````````````
    private class TailBoom extends DecoratedTrapezoid {
        public TailBoom(int partColor) {
            super(  partColor,
                    (int) TAIL_BOOM_WIDTH,
                    (int) TAIL_BOOM_HEIGHT,
                    0, TAIL_BOOM_SHIFT_Y,
                    1, 1,
                    0);
        }
    }

    //````````````````````````````````````````````````````````````````````````
    private class TailTube extends Rectangle {
        public TailTube(int partColor) {
            super(  partColor,
                    (int) TAIL_TUBE_WIDTH,
                    (int) TAIL_BOOM_HEIGHT,
                    0, TAIL_TUBE_SHIFT_Y,
                    1, 1,
                    0);
        }
    }

    //````````````````````````````````````````````````````````````````````````
    private class TailRotorEngine extends Rectangle {
        public TailRotorEngine(int partColor) {
            super(  partColor,
                    (int) TAIL_ENGINE_WIDTH,
                    (int) TAIL_ENGINE_HEIGHT,
                    0, TAIL_ENGINE_SHIFT_Y,
                    1, 1,
                    0);
        }
    }

    //````````````````````````````````````````````````````````````````````````
    private class TailStabilizer extends Rectangle {
        public TailStabilizer(int partColor) {
            super(  partColor,
                    (int) TAIL_STABILIZER_WIDTH,
                    (int) TAIL_STABILIZER_HEIGHT,
                    -TAIL_STABILIZER_SHIFT_X, TAIL_STABILIZER_SHIFT_Y,
                    1, 1,
                    0);
        }
    }

    //````````````````````````````````````````````````````````````````````````
    private class TailRotorShaft extends Rectangle {
        public TailRotorShaft(int partColor) {
            super(  partColor,
                    (int) TAIL_ROTOR_SHAFT_WIDTH,
                    (int) TAIL_ROTOR_SHAFT_HEIGHT,
                    TAIL_ROTOR_SHAFT_SHIFT_X, TAIL_ROTOR_SHAFT_SHIFT_Y,
                    1, 1,
                    0);
        }
    }

    //````````````````````````````````````````````````````````````````````````
    private class TailRotor extends Rectangle {
        public TailRotor(int partColor) {
            super(  partColor,
                    (int) TAIL_ROTOR_WIDTH,
                    (int) TAIL_ROTOR_HEIGHT,
                    TAIL_ROTOR_SHIFT_X, TAIL_ROTOR_SHIFT_Y,
                    1, 1,
                    0);
        }
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Helicopter State Pattern
    //
    private HelicopterState helicopterState;
    private final int partialFuelConsumption = 3;
    private final int maxRotationalSpeed     = 80;
    private int rotationalSpeed              = 0;

    private void changeState(HelicopterState helicopterState) {
        this.helicopterState = helicopterState;
    }

    public String currentState() {
        String currentState = helicopterState.getClass().getSimpleName();

        if(currentState.equals("Ready") && getSpeed() == 0) {
            return "Can land";
        }
        else {
            return currentState;
        }
    }

    //````````````````````````````````````````````````````````````````````````
    // Helicopter State Pattern
    //
    private abstract class HelicopterState {
        Helicopter getHelicopter() {
            return Helicopter.this;
        }

        abstract void startOrStopEngine();

        void steerLeft() {}

        void steerRight() {}

        void accelerate() {}

        void brake() {}

        void drink(Transform river, int w, int h) {}

        void dumpWater() {}

        void depleteFuel() {}

        void updateLocalTransforms() {}
    }

    //````````````````````````````````````````````````````````````````````````
    private class Off extends HelicopterState {
        @Override
        void startOrStopEngine() {
            getHelicopter().changeState(new Starting());
        }
    }

    //````````````````````````````````````````````````````````````````````````
    private class Starting extends HelicopterState {
        @Override
        void startOrStopEngine() {
            getHelicopter().changeState(new Stopping());
        }

        @Override
        void depleteFuel() {
            fuel -= partialFuelConsumption;
        }

        @Override
        void updateLocalTransforms() {
            helicopterBlade.updateLocalTransforms(rotationalSpeed += 2);

            if(rotationalSpeed >= maxRotationalSpeed) {
                takeOff();
            }
        }

        private void takeOff() {
            getHelicopter().changeState(new Ready());
            GameWorld.getInstance().initiateChopper();
        }
    }

    //````````````````````````````````````````````````````````````````````````
    private class Stopping extends HelicopterState {
        @Override
        void startOrStopEngine() {
            getHelicopter().changeState(new Starting());
        }

        @Override
        void updateLocalTransforms() {
            helicopterBlade.updateLocalTransforms(rotationalSpeed -= 1);

            if(rotationalSpeed <= 0) {
                // Prevents a negative speed restarting speed.
                //
                rotationalSpeed = 0;
                turnOffEngine();
            }
        }

        private void turnOffEngine() {
            getHelicopter().changeState(new Off());
        }
    }

    //````````````````````````````````````````````````````````````````````````
    private class Ready extends HelicopterState {
        @Override
        void startOrStopEngine() {
            if(getSpeed() == 0) {
                getHelicopter().changeState(new Stopping());
                GameWorld.getInstance().stopChopper();
            }
        }

        @Override
        void steerLeft() {
            setHeading(getHeading() + 15);
            getHelicopter().rotate(15);
        }

        @Override
        void steerRight() {
            setHeading(getHeading() - 15);
            getHelicopter().rotate(-15);
        }

        @Override
        void accelerate() {
            if(getSpeed() < 10) {
                setSpeed(getSpeed() + 1);
            }
        }

        @Override
        void brake() {
            if(getSpeed() > 0) {
                setSpeed(getSpeed() - 1);
            }
        }

        @Override
        void drink(Transform river, int w, int h) {
            if(water < 1000 && getSpeed() <= 2 && onTopOfObject(river, w, h)) {
                water += 100;
            }
        }

        @Override
        void dumpWater() {
            water = 0;
        }

        @Override
        void depleteFuel() {
            fuel -= Math.pow(getSpeed(), 2) + 5;
        }

        @Override
        void updateLocalTransforms() {
            helicopterBlade.updateLocalTransforms(maxRotationalSpeed);
        }
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private final ArrayList<GameObject> helicopterParts;
    private HelicopterBlade helicopterBlade;
    private int fuel;
    private int water;
    private final static int size = 70;
    private final int[] partColors;

    public Helicopter(Dimension mapSize, int initFuel, int[] partColors,
                                                       Transform startPoint) {
        super(partColors[14], mapSize, size, size);
        water = 0;
        fuel = initFuel;
        this.partColors = partColors;
        setFont(Font.createSystemFont(FACE_SYSTEM, STYLE_BOLD, SIZE_MEDIUM));

        this.translate(startPoint.getTranslateX(), startPoint.getTranslateY());

        helicopterState = new Off();
        helicopterParts = new ArrayList<>();
        buildHelicopter();
    }

    private void buildHelicopter() {
        // Added in order of which they must be drawn.
        //
        helicopterParts.add(new RightHelicopterSkid(partColors[0]));
        helicopterParts.add(new LeftHelicopterSkid(partColors[1]));
        helicopterParts.add(new UpperCrossTubes(partColors[2]));
        helicopterParts.add(new LowerCrossTubes(partColors[3]));
        helicopterParts.add(new Cockpit(partColors[4]));
        helicopterParts.add(new TailBoom(partColors[5]));
        helicopterParts.add(new TailTube(partColors[6]));
        helicopterParts.add(new HelicopterEngineBlock(partColors[7]));
        helicopterBlade   = new HelicopterBlade(partColors[8]);
        helicopterParts.add(helicopterBlade);
        helicopterParts.add(new HelicopterBladeShaft(partColors[9]));
        helicopterParts.add(new TailRotorShaft(partColors[10]));
        helicopterParts.add(new TailStabilizer(partColors[11]));
        helicopterParts.add(new TailRotorEngine(partColors[12]));
        helicopterParts.add(new TailRotor(partColors[13]));
    }

    double getBladeArcRadius() {
        return BLADE_LENGTH / 2;
    }

    public void startOrStopEngine() {
        helicopterState.startOrStopEngine();
    }

    @Override
    public void steerLeft() {
        helicopterState.steerLeft();
    }

    @Override
    public void steerRight() {
        helicopterState.steerRight();
    }

    public void accelerate() {
        helicopterState.accelerate();
    }

    public void brake() {
        helicopterState.brake();
    }

    public void drink(Transform river, int width, int height) {
        helicopterState.drink(river, width, height);
    }

    public void dumpWater() {
        helicopterState.dumpWater();
    }

    public int getWater() {
        return water;
    }

    public void depleteFuel() {
        helicopterState.depleteFuel();
    }

    public int getFuel() {
        return fuel;
    }

    public void updateLocalTransforms() {
        helicopterState.updateLocalTransforms();
    }

    public boolean onTopOfObject(Transform object, int w, int h) {
        int offset = getDimension().getWidth() / 2;

        return getY() + offset >= object.getTranslateY() - h / 2f &&
               getY() - offset <= object.getTranslateY() + h / 2f &&
               getX() + offset >= object.getTranslateX() - w / 2f &&
               getX() - offset <= object.getTranslateX() + w / 2f;
    }

    @Override
    public void localDraw(Graphics g, Point containerOrigin,
                                      Point screenOrigin) {
        reversePrimitiveTranslate(g, getDimension());
        reverseContainerTranslate(g, containerOrigin);

        for(GameObject go : helicopterParts) {
            go.draw(g, containerOrigin, screenOrigin);
        }

        int textGap = 35;
        int xOffset = 60;

        applyTextTransforms(g, containerOrigin, screenOrigin);

        g.drawString("F   : " + fuel,
                     getWidth() + textGap + xOffset,
                     getHeight() + textGap);
        g.drawString("W : " + water,
                     getWidth() + textGap + xOffset,
                     getHeight() + textGap * 2);
    }
}