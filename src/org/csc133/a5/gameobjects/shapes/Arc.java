package org.csc133.a5.gameobjects.shapes;

import com.codename1.ui.Graphics;
import com.codename1.ui.geom.Point;
import org.csc133.a5.gameobjects.GameObject;

public class Arc extends GameObject {
    private final int startAngle;
    private final int arcAngle;

    public Arc(int color, int w, int h,
               float tx, float ty,
               float sx, float sy,
               float degreesRotation,
               int startAngle, int arcAngle) {

        setColor(color);
        setDimensions(w, h);
        this.startAngle = startAngle;
        this.arcAngle = arcAngle;

        translate(tx, ty);
        scale(sx, sy);
        rotate(degreesRotation);
    }

    @Override
    public void localDraw(Graphics g, Point containerOrigin,
                                      Point screenOrigin) {
        g.fillArc(0, 0, getWidth(), getHeight(), startAngle, arcAngle);
    }
}
