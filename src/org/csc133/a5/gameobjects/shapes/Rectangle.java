package org.csc133.a5.gameobjects.shapes;

import com.codename1.ui.Graphics;
import com.codename1.ui.geom.Point;
import org.csc133.a5.gameobjects.GameObject;

public class Rectangle extends GameObject {

    public Rectangle (int color, int w, int h,
                      float tx, float ty,
                      float sx, float sy,
                      float degreesRotation) {

        setColor(color);
        setDimensions(w, h);

        translate(tx, ty);
        scale(sx, sy);
        rotate(degreesRotation);
    }

    @Override
    public void localDraw(Graphics g, Point containerOrigin,
                                      Point screenOrigin) {
        g.fillRect(0, 0, getWidth(), getHeight());
    }
}
