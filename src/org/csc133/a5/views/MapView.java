package org.csc133.a5.views;

import com.codename1.charts.util.ColorUtil;
import com.codename1.ui.Container;
import com.codename1.ui.Graphics;
import com.codename1.ui.Transform;
import com.codename1.ui.geom.Dimension;
import com.codename1.ui.geom.Point;
import com.codename1.ui.layouts.BorderLayout;
import org.csc133.a5.GameWorld;
import org.csc133.a5.gameobjects.*;

public class MapView extends Container {
    private final GameWorld gw;
    private final float[] winLeft   = {0, 0, 0};
    private final float[] winRight  = {0, 0, 0};
    private final float[] winTop    = {0, 0, 0};
    private final float[] winBottom = {0, 0, 0};
    private int zoomIndex  = 0;
    private int numWindows = 0;

    public MapView() {
        gw = GameWorld.getInstance();
        setLayout(new BorderLayout());
        this.getAllStyles().setBgTransparency(255);
        this.getAllStyles().setBgColor(ColorUtil.BLACK);
    }

    public void setNumZoomWindows(int numWindows) {
        this.numWindows = numWindows;
        initZoomWindows();
    }

    private void initZoomWindows() {
        // This value determines the scale percentage to zoom out.
        //
        float windowScale = 0.10f;

        winLeft[0]  = winBottom[0] = 0;
        winRight[0] = this.getWidth();
        winTop[0]   = this.getHeight();

        // Each zoom out is scaled outward by windowScale percentage times
        // the zoom iteration.
        //
        for(int i = 1; i < numWindows; i++) {
            winLeft[i]   = winRight[0] - winRight[0] * (1 + i * windowScale);
            winRight[i]  = winRight[0] * (1 + i * windowScale);

            winBottom[i] = winTop[0] - winTop[0] * (1 + i * windowScale);
            winTop[i]    = winTop[0] * (1 + i * windowScale);
        }
    }

    public void zoom() {
        zoomIndex++;
        if(zoomIndex >= numWindows) {
            zoomIndex = 0;
        }
    }

    // Set up the world to Normalized-Device transform.
    //
    private Transform buildWorldToNDXform(float winWidth, float winHeight,
                                          float winLeft, float winBottom) {
        Transform tempTransform = Transform.makeIdentity();
        tempTransform.scale(1 / winWidth, 1 / winHeight);
        tempTransform.translate(-winLeft, -winBottom);
        return tempTransform;
    }

    // Set up the Normalized-Device to Screen transform.
    //
    private Transform buildNDToDisplayXform(float displayWidth,
                                            float displayHeight) {
        Transform tempTransform = Transform.makeIdentity();
        tempTransform.translate(0, displayHeight);
        tempTransform.scale(displayWidth, -displayHeight);
        return tempTransform;
    }

    // Set up the Viewing Transformation Matrix.
    //
    private void setupVTM(Graphics g) {
        Transform worldToND, ndToDisplay, theVTM;

        // Changing these values move and resize the window from which we see
        // the world. With a smaller window, everyone looks bigger. With a
        // bigger window, everything looks smaller.
        //
        float winH = winTop[zoomIndex] - winBottom[zoomIndex];
        float winW = winRight[zoomIndex] - winLeft[zoomIndex];

        worldToND = buildWorldToNDXform(winW, winH,
                                        winLeft[zoomIndex],
                                        winBottom[zoomIndex]);
        ndToDisplay = buildNDToDisplayXform(getWidth(), getHeight());

        theVTM = ndToDisplay.copy();
        theVTM.concatenate(worldToND);

        Transform gXform = Transform.makeIdentity();
        g.getTransform(gXform);
        gXform.translate(getAbsoluteX(), getAbsoluteY());
        gXform.concatenate(theVTM);
        gXform.translate(-getAbsoluteX(), -getAbsoluteY());
        g.setTransform(gXform);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        setupVTM(g);

        Point containerOrigin = new Point(this.getX(), this.getY());
        Point screenOrigin = new Point(getAbsoluteX(), getAbsoluteY());

        for(GameObject go : gw.getGameObjectCollection()) {
            go.draw(g, containerOrigin, screenOrigin);
        }

        g.resetAffine();
    }

    // TODO: Fix coordinates only being accurate when the screen isn't
    //       zoomed out.
    @Override
    public void pointerPressed(int x, int y) {
        // Accounts for the container origin being located in the bottom left
        // corner for fires, and the height of the control cluster.
        //
        x += this.getX();
        y  = this.getY() + this.getHeight() + gw.getControlClusterHeight() - y;

        for(Fire fire : gw.getGameObjectCollection().getFires()) {
            fire.checkIfSelected(x, y);
        }
    }

    @Override
    public void laidOut() {
        super.laidOut();
        gw.layGameMap(new Dimension(this.getWidth(), this.getHeight()));
        initZoomWindows();
    }
}