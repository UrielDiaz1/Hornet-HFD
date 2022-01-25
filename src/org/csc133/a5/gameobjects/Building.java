package org.csc133.a5.gameobjects;

import com.codename1.charts.util.ColorUtil;
import com.codename1.ui.Font;
import com.codename1.ui.Graphics;
import com.codename1.ui.geom.Dimension;
import com.codename1.ui.geom.Point;
import com.codename1.ui.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;
import static com.codename1.ui.CN.*;

public class Building extends Fixed {
    private final ArrayList<Fire> fires;
    private double damagePercentage;
    private int value;
    private double fireAreas;
    private final Random rand;
    private final double[] X_SHIFT = {0.30, 0.10, 0.80};
    private final double[] Y_SHIFT = {0.80, 0.10, 0.10};
    private final double[] WIDTHS  = {0.40, 0.10, 0.10};
    private final double[] HEIGHTS = {0.15, 0.40, 0.40};

    public Building(Dimension mapSize, int blueprint) {
        super(ColorUtil.rgb(255, 0, 0), mapSize, 0, 0);
        fireAreas = 0;
        damagePercentage = 0;
        rand = new Random();
        fires = new ArrayList<>();
        setFont(Font.createSystemFont(FACE_SYSTEM, STYLE_BOLD, SIZE_MEDIUM));

        build(blueprint);
    }

    private void build(int blueprint) {
        setDimensions((int) (WIDTHS[blueprint] * getMapSize().getWidth()),
                      (int) (HEIGHTS[blueprint] * getMapSize().getHeight()));

        double translationX = getMapSize().getWidth() * X_SHIFT[blueprint]
                            + getWidth() / 2f;
        double translationY = getMapSize().getHeight() * Y_SHIFT[blueprint]
                            + getHeight() / 2f;

        // Local transform.
        //
        this.translate(translationX, translationY);
        this.scale(1, -1);

        value = (blueprint + 1) * 400;
    }


    private double getArea() {
        return getWidth() * getHeight();
    }

    public void setFireInBuilding(Fire fire) {
        double x = getX() - getWidth() / 2f;
        double y = getY() - getHeight() / 2f;
        double randomX = rand.nextInt((getWidth()));
        double randomY = rand.nextInt((getHeight()));

        fire.spawn(x + randomX, y + randomY);
        fire.start();
        fires.add(fire);
    }

    public void setDamagePercentage() {
        double damagePercentage = (int) ((fireAreas / this.getArea()) * 100);

        if(damagePercentage > 100) {
            this.damagePercentage = 100;
        }
        else if(this.damagePercentage < damagePercentage) {
            this.damagePercentage = damagePercentage;
        }
        fireAreas = 0;
    }

    public double getDamagePercentage() {
        return damagePercentage;
    }

    public double getFinancialLoss() {
        return value * damagePercentage / 100f;
    }

    public boolean allFiresPutOut() {
        for(Fire fire : fires) {
            if(fire.getSize() > 0) {
                return false;
            }
        }
        return true;
    }

    public void accumulateFireAreas() {
        if(fires.isEmpty()) {
            fireAreas = 0;
            return;
        }

        int[][] index = new int[fires.size()][fires.size()];
        int i = 0, j = 0;

        for(Fire fireA : fires) {
            fireAreas += fireA.getArea();
            for(Fire fireB : fires) {
                // Skip if same fire or intersection already calculated.
                //
                if(fireA == fireB || index[i][j] == 1) {
                    j++;
                }
                else if(theyIntersect(fireA, fireB)) {
                    fireAreas -= findIntersectionArea(fireA, fireB);

                    // Mark as solved.
                    //
                    index[i][j] = index[j][i] = 1;
                    j++;
                }
            }
            i++;
            j = 0;
        }
    }

    private boolean theyIntersect(Fire fireA, Fire fireB) {
        return Rectangle2D.intersects(fireA.getX(),    fireA.getY(),
                                      fireA.getSize(), fireA.getSize(),
                                      fireB.getX(),    fireB.getY(),
                                      fireB.getSize(), fireB.getSize());
    }

    private double findIntersectionArea(Fire fireA, Fire fireB) {
        double distance = Math.hypot(fireB.getX() - fireA.getX(),
                                     fireB.getY() - fireA.getY());

        if(distance < fireA.getRadius() + fireB.getRadius()) {
            double a = fireA.getRadius() * fireA.getRadius();
            double b = fireB.getRadius() * fireB.getRadius();

            double x = (a - b + distance * distance) / (2 * distance);
            double z = x * x;
            double y = Math.sqrt(a - z);

            if(distance <= Math.abs(fireB.getRadius() - fireA.getRadius())) {
                return Math.PI * Math.min(a, b);
            }
            return  a * Math.asin(y / fireA.getRadius()) +
                    b * Math.asin(y / fireB.getRadius()) -
                    y * (x + Math.sqrt(z + b - a));
        }
        return 0;
    }

    @Override
    public void localDraw(Graphics g, Point containerOrigin,
                                      Point screenOrigin) {
        g.setFont(getFont());
        int textGap = 35;

        g.drawRect(0, 0, getWidth(), getHeight());

        g.drawString("V  : " + value,
                     getWidth() + textGap,
                     getHeight() - textGap * 2);
        g.drawString("D  : " + (int) damagePercentage + "%",
                     getWidth() + textGap,
                     getHeight() - textGap);
    }
}
