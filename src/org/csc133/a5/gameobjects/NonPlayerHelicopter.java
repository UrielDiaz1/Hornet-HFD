package org.csc133.a5.gameobjects;

import com.codename1.charts.util.ColorUtil;
import com.codename1.ui.Transform;
import com.codename1.ui.geom.Dimension;
import com.codename1.ui.geom.Point2D;
import org.csc133.a5.GameWorld;
import org.csc133.a5.interfaces.Strategy;
import java.util.ArrayList;

public class NonPlayerHelicopter extends Helicopter {
    private FlightPath.BezierCurve currentBezierCurve;
    private Point2D tailTracker;
    private Strategy strategy;
    private boolean reachedRiverFirstTime = false;
    private final FlightPath flightPath;
    private final GameWorld gw;
    private double angle;
    private double t = 0;
    private final double angleFix = 90;
    private final static int extraFuel = 25000;
    private final double[] collisionScale = {1.50,    // Enter AvoidStrategy
                                             2.00,    // Exit  AvoidStrategy
                                             0.75};   // Crashing mechanic.
    private final double radiusPH;
    private final double radiusNPH;
    private static NonPlayerHelicopter instance;
    private static final int[] color = {
            ColorUtil.rgb(179, 0, 0),       // RightSkids
            ColorUtil.rgb(179, 0, 0),       // LeftSkids
            ColorUtil.LTGRAY,               // UpperCTubes
            ColorUtil.LTGRAY,               // LowerCTubes
            ColorUtil.rgb(179, 0, 0),       // Cockpit
            ColorUtil.rgb(216, 216, 216),   // TailBoom
            ColorUtil.rgb(179, 0, 0),       // TailTube
            ColorUtil.rgb(216, 216, 216),   // EngineBlock
            ColorUtil.rgb(27, 141, 43),     // Blade
            ColorUtil.BLACK,                // BladeShaft
            ColorUtil.rgb(226, 235, 143),   // TRotorShaft
            ColorUtil.rgb(37, 201, 190),    // TStabilizer
            ColorUtil.GRAY,                 // TRotorEngine
            ColorUtil.LTGRAY,               // TailRotor
            ColorUtil.rgb(216, 216, 216)};  // HUD

    private NonPlayerHelicopter(Dimension map, int initFuel, Transform startP) {
        super(map, initFuel + extraFuel, color, startP);

        gw         = GameWorld.getInstance();
        radiusPH   = PlayerHelicopter.getInstance().getBladeArcRadius();
        radiusNPH  = getBladeArcRadius();
        flightPath = GameWorld.getInstance().getFlightPath();
        currentBezierCurve = flightPath.getPathToRiver();
        tailTracker        = currentBezierCurve.getLastControlPoint();

        setStrategy(new FlightPathStrategy());
    }

    public static NonPlayerHelicopter getInstance() {
        if(instance == null) {
            Dimension mapSize    = GameWorld.getInstance().getMapSize();
            int initFuel         = GameWorld.getInstance().getInitialFuel();
            Transform startPoint = GameWorld.getInstance().getTakeOffPoint();

            instance = new NonPlayerHelicopter(mapSize, initFuel, startPoint);
        }
        return instance;
    }

    private void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    public void invokeStrategy() {
        if(notSpawned() && readyToSpawn()) {
            GameWorld.getInstance().spawnNPH();
            startOrStopEngine();
        }
        else if(active()) {
            speedUpToMax();
            updateStrategy();
            strategy.apply();
        }
    }

    private boolean notSpawned() {
        return currentState().equals("Off");
    }

    private boolean readyToSpawn() {
        return !collision(radiusPH, radiusNPH);
    }

    private boolean active() {
        return  currentState().equals("Ready") ||
                currentState().equals("Can land");
    }

    private void speedUpToMax() {
        if (getSpeed() < 4) {
            accelerate();
        }
    }

    private void updateStrategy() {
        if(flightPathStrategy()) {
            proximityDetection();
        }
        if(correctionNeeded()) {
            setStrategy(new PathCorrectionStrategy());
        }
        else if(canExitAvoidStrategy()) {
            createBackupPath();
        }
    }

    private boolean flightPathStrategy() {
        return strategy.getClass().getSimpleName().equals("FlightPathStrategy");
    }

    private boolean collision(double radiusPH, double radiusNPH) {
        float c1y = PlayerHelicopter.getInstance().getY();
        float c1x = PlayerHelicopter.getInstance().getX();
        float c2y = this.getY();
        float c2x = this.getX();

        double distance = Math.pow(c2y - c1y, 2) + Math.pow(c2x - c1x, 2);
        return distance <= Math.pow(radiusPH + radiusNPH, 2);
    }

    private void proximityDetection() {
        if(!currentState().equals("Off")
           && collision(radiusPH * collisionScale[0],
                        radiusNPH * collisionScale[0])) {
            setStrategy(new AvoidStrategy());
            GameWorld.getInstance().initiateCrashWarning();
            setAvoidanceAngle();
        }
    }

    private void setAvoidanceAngle() {
        int heading;
        Transform ph = PlayerHelicopter.getInstance().getTranslation();

        // If nph is on the upper-right side.
        //
        if(getX() > ph.getTranslateX() && getY() > ph.getTranslateY()) {
            heading = 45;
        }
        // if nph is on the bottom-right side.
        //
        else if(getX() > ph.getTranslateX() && getY() < ph.getTranslateY()){
            heading = 315;
        }
        // If nph is on the upper-left side.
        else if(getX() < ph.getTranslateX() && getY() > ph.getTranslateY()) {
            heading = 135;
        }
        // If ph is on the bottom-left side.
        else {
            heading = 225;
        }
        angle = Math.toRadians(heading);
    }

    private boolean correctionNeeded() {
        return traversing() && goalChanged() && flightPathStrategy();
    }

    private boolean traversing() {
        return t > 0 && t < 1;
    }

    private boolean goalChanged() {
        return tailTracker != currentBezierCurve.getLastControlPoint();
    }

    private boolean canExitAvoidStrategy() {
        return strategy.getClass().getSimpleName().equals("AvoidStrategy") &&
                !collision(radiusPH  * collisionScale[1],
                        radiusNPH * collisionScale[1]);
    }

    private void createBackupPath() {
        ArrayList<Point2D> tempPath = new ArrayList<>();

        tempPath.add(new Point2D(getX(), getY()));
        tempPath.add(currentBezierCurve.getControlPoints().get(1));
        tempPath.add(currentBezierCurve.getLastControlPoint());

        currentBezierCurve.updateControlPoints(tempPath);
        setStrategy(new FlightPathStrategy());
        resetBezierCurvePosition();
    }

    private void resetBezierCurvePosition() {
        t = 0;
    }

    private boolean reachedEndPoint() {
        return t > 1;
    }

    public boolean crashed() {
        if(notSpawned()) {
            return false;
        }
        return collision(radiusPH  * collisionScale[2],
                         radiusNPH * collisionScale[2]);
    }

    private void updateTailTracker() {
        tailTracker = currentBezierCurve.getLastControlPoint();
    }

    public void reset() {
        instance = null;
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    class FlightPathStrategy implements Strategy {
        @Override
        public void apply() {
            if(reachedEndPoint()) {
                attemptAction();
            }
            else {
                travel();
            }
        }

        private void attemptAction() {
            if(readyToDrink()) {
                attemptToDrink();

                if(doneDrinking()) {
                    if(!reachedRiverFirstTime) {
                        currentBezierCurve.setInactive();
                        reachedRiverFirstTime = true;
                    }

                    currentBezierCurve = flightPath.getPathToFire();
                    resetBezierCurvePosition();
                    updateTailTracker();
                }
            }
            else if(readyToFightFire()) {
                gw.attemptFightFire(NonPlayerHelicopter.getInstance());
                currentBezierCurve = flightPath.getPathFromFire();
                resetBezierCurvePosition();
                updateTailTracker();
            }
        }

        private boolean readyToDrink() {
            return getWater() < 1000;
        }

        private void attemptToDrink() {
            setSpeed(0);
            Dimension river = new Dimension(gw.getRiverDimension().getWidth(),
                                            gw.getRiverDimension().getHeight());

            drink(GameWorld.getInstance().getRiverOrigin(), river.getWidth(),
                                                            river.getHeight());
        }

        private boolean doneDrinking() {
            return getWater() >= 1000;
        }

        private boolean readyToFightFire() {
            return getWater() >= 1000;
        }

        private void travel() {
            Point2D currentPoint = new Point2D(getX(), getY());
            Point2D nextPoint = currentBezierCurve.evaluateCurve(t);

            // Translate from current to next point.
            //
            double tx = nextPoint.getX() - currentPoint.getX();
            double ty = nextPoint.getY() - currentPoint.getY();

            // Angle offset accounts for which direction is 0 degrees.
            //
            int theta = (int) (angleFix - Math.toDegrees(Math.atan2(ty, tx)));
            NonPlayerHelicopter.this.translate(tx, ty);

            if(!reachedEndPoint()) {
                t = t + getSpeed() * 0.003;
                rotate(getHeading() - theta);
                setHeading(theta);
            }
        }
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    class AvoidStrategy implements Strategy {
        @Override
        public void apply() {
            avoid();
        }

        private void avoid() {
            int speedMultiplier = 2;
            double tx = getSpeed() * speedMultiplier * Math.cos(angle);
            double ty = getSpeed() * speedMultiplier * Math.sin(angle);
            translate(tx, ty);

            int theta = (int) (angleFix - Math.toDegrees(Math.atan2(ty, tx)));
            rotate(getHeading() - theta);
            setHeading(theta);
        }
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    class PathCorrectionStrategy implements Strategy {
        @Override
        public void apply() {
            updateTailTracker();
            createBackupPath();
        }
    }
}