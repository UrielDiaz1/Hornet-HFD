package org.csc133.a5.gameobjects;

import com.codename1.charts.util.ColorUtil;
import com.codename1.ui.Graphics;
import com.codename1.ui.Transform;
import com.codename1.ui.geom.Dimension;
import com.codename1.ui.geom.Point;
import com.codename1.ui.geom.Point2D;
import org.csc133.a5.GameWorld;
import java.util.ArrayList;

public class FlightPath {
    private final BezierCurve pathToRiver;
    private final BezierCurve pathToFire;
    private final BezierCurve pathFromFire;
    private final Dimension mapSize;
    private final Transform startPoint;
    private final Transform river;
    private Transform selectedFire;
    private final Point2D lowerScreenCP;
    private final Point2D upperScreenCP;
    private final Point2D belowRiverCP;

    public FlightPath(Transform startPoint, Dimension mapSize) {
        Transform defaultLocation = Transform.makeIdentity();
        defaultLocation.translate(mapSize.getWidth() * 0.85f,
                                  mapSize.getHeight() * 0.15f);

        this.selectedFire = defaultLocation;
        this.startPoint   = startPoint;
        this.mapSize      = mapSize;
        this.river        = GameWorld.getInstance().getRiverOrigin();

        lowerScreenCP = new Point2D( mapSize.getWidth() * 0.01,
                                     mapSize.getHeight() * 0.02);
        upperScreenCP = new Point2D(-mapSize.getWidth() * 0.10,
                                     mapSize.getHeight() * 0.98);
        belowRiverCP  = new Point2D(-mapSize.getWidth() * 0.03,
                                     mapSize.getHeight() / 4f);

        pathToRiver   = new BezierCurve(initPathToRiver());
        pathToFire    = new BezierCurve(initPathToFire());
        pathFromFire  = new BezierCurve(initPathFromFire(lowerScreenCP));
    }

    private ArrayList<Point2D> initPathToRiver() {
        ArrayList<Point2D> temp = new ArrayList<>();
        int directionStabilizer = 1;

        // Helipad endpoint.
        temp.add(new Point2D(startPoint.getTranslateX(),
                             startPoint.getTranslateY() + directionStabilizer));

        // Center of screen control point.
        temp.add(new Point2D(mapSize.getWidth() / 2f,
                             mapSize.getHeight() / 2f));

        // River's leftmost side control point.
        temp.add(new Point2D(river.getTranslateX() * 0.03,
                             river.getTranslateY()));

        // River's origin endpoint.
        temp.add(new Point2D(river.getTranslateX(),
                             river.getTranslateY()));
        return temp;
    }

    private ArrayList<Point2D> initPathToFire() {
        ArrayList<Point2D> temp = new ArrayList<>();

        // River's origin endpoint.
        temp.add(new Point2D(river.getTranslateX(),
                             river.getTranslateY()));

        // River's rightmost side control point.
        temp.add(new Point2D(river.getTranslateX() * 1.97,
                             river.getTranslateY()));

        // Endpoint dependent by selected fire's location.
        temp.add(new Point2D(selectedFire.getTranslateX(),
                             selectedFire.getTranslateY()));
        return temp;
    }

    private ArrayList<Point2D> initPathFromFire(Point2D controlPoint) {
        ArrayList<Point2D> temp = new ArrayList<>();

        // Starting endpoint dependent by selected fire's location.
        temp.add(new Point2D(selectedFire.getTranslateX(),
                             selectedFire.getTranslateY()));

        // Control point dependent of fire's quadrant.
        temp.add(controlPoint);

        // Control point located in the far left side of the river.
        temp.add(new Point2D(-river.getTranslateX() * 0.03,
                              river.getTranslateY()));

        // River's origin endpoint.
        temp.add(new Point2D(river.getTranslateX(),
                             river.getTranslateY()));
        return temp;
    }

    public BezierCurve getPathToRiver() {
        recreatePath();
        return pathToRiver;
    }

    public BezierCurve getPathToFire() {
        recreatePath();
        return pathToFire;
    }

    public BezierCurve getPathFromFire() {
        recreatePath();
        return pathFromFire;
    }

    private void recreatePath() {
        updateSelectedFire(selectedFire);
    }

    public void updateSelectedFire(Transform selectedFire) {
        this.selectedFire = selectedFire;
        pathToFire.updateControlPoints(initPathToFire());

        if(inPositiveYQuadrant()) {
            pathFromFire.updateControlPoints(initPathFromFire(upperScreenCP));
        }
        else if(inNegativeXQuadrant()) {
            pathFromFire.updateControlPoints((initPathFromFire(belowRiverCP)));
        }
        else {
            pathFromFire.updateControlPoints(initPathFromFire(lowerScreenCP));
        }
    }

    private boolean inPositiveYQuadrant() {
        return selectedFire.getTranslateY() > mapSize.getHeight() * 0.5;
    }

    private boolean inNegativeXQuadrant() {
        return selectedFire.getTranslateX() < mapSize.getWidth() * 0.5;
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    static class BezierCurve extends GameObject {
        private ArrayList<Point2D> controlPoints;
        private boolean active = true;

        public BezierCurve(ArrayList<Point2D> controlPoints) {
            super(ColorUtil.GREEN, GameWorld.getInstance().getMapSize(), 0, 0);
            this.controlPoints = controlPoints;
        }

        void updateControlPoints(ArrayList<Point2D> controlPoints) {
            this.controlPoints = controlPoints;
        }

        ArrayList<Point2D> getControlPoints() {
            return controlPoints;
        }

        Point2D getLastControlPoint() {
            return controlPoints.get(controlPoints.size() - 1);
        }

        void setInactive() {
            active = false;
        }

        Point2D evaluateCurve(double t) {
            Point2D point = new Point2D(0, 0);
            int degree = controlPoints.size() - 1;
            double cX, cY;

            for(int i = 0; i < controlPoints.size(); i++) {
                cX = controlPoints.get(i).getX();
                cY = controlPoints.get(i).getY();
                point.setX(point.getX() + bernsteinD(degree, i, t) * cX);
                point.setY(point.getY() + bernsteinD(degree, i, t) * cY);
            }
            return point;
        }

        private void drawBezierCurve(Graphics g,
                                     ArrayList<Point2D> controlPoints) {
            final double smallFloatIncrement = 0.001;
            g.setColor(ColorUtil.GRAY);

            for(Point2D point : controlPoints) {
                g.fillArc((int) point.getX() - 15,
                          (int) point.getY() - 15,
                          30, 30, 0, 360);
            }

            g.setColor(ColorUtil.GREEN);

            Point2D currentPoint = controlPoints.get(0);
            Point2D nextPoint;
            double t = 0;

            while(t < 1) {
                nextPoint = evaluateCurve(t);

                g.drawLine((int) currentPoint.getX(), (int) currentPoint.getY(),
                           (int) nextPoint.getX(), (int) nextPoint.getY());
                currentPoint = nextPoint;
                t += smallFloatIncrement;
            }

            nextPoint = controlPoints.get(controlPoints.size() - 1);
            g.drawLine((int) currentPoint.getX(), (int) currentPoint.getY(),
                       (int) nextPoint.getX(), (int) nextPoint.getY());
        }

        private double bernsteinD(int d, int i, double t) {
            return choose(d, i) * Math.pow(t, i) * Math.pow(1 - t, d - i);
        }

        private double choose(int n, int k) {
            double[][] choose = new double[n + 1][k + 1];

            // Solves the problem bottom-up. Handles overlapping sub-problems
            // by saving solutions.
            for(int i = 0; i <= n; i++) {
                for(int j = 0; j <= Math.min(i, k); j++) {
                    // Base Cases.
                    if (j == 0 || j == i) {
                        choose[i][j] = 1;
                    }
                    // Uses saved solutions to sub-problems to calculate
                    // current solution.
                    else {
                        choose[i][j] = choose[i - 1][j - 1] + choose[i - 1][j];
                    }
                }
            }
            return choose[n][k];
        }

        @Override
        public void localDraw(Graphics g, Point containerOrigin,
                                          Point screenOrigin) {
            if(active) {
                drawBezierCurve(g, controlPoints);
            }
        }
    }
}
