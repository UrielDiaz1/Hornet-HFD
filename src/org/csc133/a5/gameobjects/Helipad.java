package org.csc133.a5.gameobjects;

import com.codename1.charts.util.ColorUtil;
import com.codename1.ui.Graphics;
import com.codename1.ui.Transform;
import com.codename1.ui.geom.Dimension;
import com.codename1.ui.geom.Point;

public class Helipad extends Fixed {
    private final int BORDER_THICKNESS         = 5;
    private final int HELIPAD_GAP              = 20;
    private final double X_ORIGIN_SHIFT        = 0.5;
    private final double Y_ORIGIN_SHIFT        = 0.15;
    private final static double LENGTH_PERCENT = 0.083;

    public Helipad(Dimension mapSize) {
        super(ColorUtil.GRAY,
              mapSize,
              (int) (mapSize.getWidth() * LENGTH_PERCENT),
              (int) (mapSize.getWidth() * LENGTH_PERCENT));

        this.translate(mapSize.getWidth() * X_ORIGIN_SHIFT,
                       mapSize.getHeight() * Y_ORIGIN_SHIFT);

    }

    public Transform takeoffSpot() {
        return getTranslation();
    }

    @Override
    public void localDraw(Graphics g, Point containerOrigin,
                                      Point screenOrigin) {
        // Draws the edges of the helipad.
        //
        g.drawRect(0, 0, getWidth(), getWidth(), BORDER_THICKNESS);

        // Draws the circle inside the helipad.
        //
        g.drawArc(HELIPAD_GAP, HELIPAD_GAP,
                  getWidth() - HELIPAD_GAP * 2,
                  getHeight() - HELIPAD_GAP * 2,
                  0, 360);
    }
}