package org.csc133.a5.gameobjects;

import com.codename1.charts.util.ColorUtil;
import com.codename1.ui.Graphics;
import com.codename1.ui.geom.Dimension;
import com.codename1.ui.geom.Point;

public class River extends Fixed {
    private final double X_ORIGIN_SHIFT         = 0.50;
    private final double Y_ORIGIN_SHIFT         = 0.65;
    private final static double HEIGHT_PERCENT  = 0.10;

    public River(Dimension mapSize) {
        super(ColorUtil.BLUE,
              mapSize,
              mapSize.getWidth(),
              (int) (mapSize.getHeight() * HEIGHT_PERCENT));

        this.translate(mapSize.getWidth() * X_ORIGIN_SHIFT,
                       mapSize.getHeight() * Y_ORIGIN_SHIFT);

    }

    @Override
    public void localDraw(Graphics g, Point containerOrigin,
                                      Point screenOrigin) {
        g.fillRect(0, 0, getWidth(), getHeight());
    }
}