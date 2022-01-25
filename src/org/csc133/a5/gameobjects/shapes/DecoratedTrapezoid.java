package org.csc133.a5.gameobjects.shapes;

import com.codename1.ui.Graphics;
import com.codename1.ui.geom.Point;
import org.csc133.a5.gameobjects.GameObject;

public class DecoratedTrapezoid extends GameObject {
    private final double LEFT_SIDE_X_OFFSET  = 0.25;
    private final double RIGHT_SIDE_X_OFFSET = 0.75;
    private final double LEFT_X_MIDPOINT     = 1 / 7f;
    private final double RIGHT_X_MIDPOINT    = 6 / 7f;
    private final double HEIGHT_MIDPOINT     = 0.5;

    public DecoratedTrapezoid(int color, int w, int h,
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
        drawShape(g);
        drawDecoration(g);
    }

    private void drawShape(Graphics g) {
        // Top base.
        //
        g.drawRect(0,
                   0,
                   getWidth(),
                   0);

        // Left side.
        //
        g.drawLine(0,
                   0,
                   (int) (getWidth() * LEFT_SIDE_X_OFFSET),
                  -getHeight());

        // Right side.
        //
        g.drawLine(getWidth(),
                   0,
                   (int) (getWidth() * RIGHT_SIDE_X_OFFSET),
                  -getHeight());

        // Lower base.
        //
        g.drawLine((int) (getWidth() * LEFT_SIDE_X_OFFSET),
                  -getHeight(),
                   (int) (getWidth() * RIGHT_SIDE_X_OFFSET),
                  -getHeight());
    }

    private void drawDecoration(Graphics g) {

        // Top-Left -> Right Midpoint decoration line.
        //
        g.drawLine(0,
                   0,
                   (int) (getWidth() * RIGHT_X_MIDPOINT),
                   (int) (-getHeight() * HEIGHT_MIDPOINT));

        // Right Midpoint -> Bottom-Left decoration line.
        //
        g.drawLine((int) (getWidth() * RIGHT_X_MIDPOINT),
                   (int) (-getHeight() * HEIGHT_MIDPOINT),
                   (int) (getWidth() * LEFT_SIDE_X_OFFSET),
                   -getHeight());

        // Top-Right -> Left Midpoint decoration line.
        //
        g.drawLine(getWidth(),
                   0,
                   (int) (getWidth() * LEFT_X_MIDPOINT),
                   (int) (-getHeight() * HEIGHT_MIDPOINT));

        // Left Midpoint -> Bottom-Right decoration line.
        //
        g.drawLine((int) (getWidth() * LEFT_X_MIDPOINT),
                   (int) (-getHeight() * HEIGHT_MIDPOINT),
                   (int) (getWidth() * RIGHT_SIDE_X_OFFSET),
                   -getHeight());
    }
}
