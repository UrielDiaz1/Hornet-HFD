package org.csc133.a5.gameobjects;

import com.codename1.charts.util.ColorUtil;
import com.codename1.ui.Transform;
import com.codename1.ui.geom.Dimension;
import org.csc133.a5.GameWorld;

public class PlayerHelicopter extends Helicopter {
    private static PlayerHelicopter instance;
    private static final int[] color = {
            ColorUtil.rgb(71, 66, 98),     // RightSkids
            ColorUtil.rgb(71, 66, 98),     // LeftSkids
            ColorUtil.rgb(100, 201, 190),  // UpperCTubes
            ColorUtil.rgb(100, 201, 190),  // LowerCTubes
            ColorUtil.rgb(71, 66, 98),     // Cockpit
            ColorUtil.rgb(37, 201, 190),   // TailBoom
            ColorUtil.YELLOW,              // TailTube
            ColorUtil.rgb(34, 154, 190),   // EngineBlock
            ColorUtil.rgb(182, 183, 128),  // Blade
            ColorUtil.BLACK,               // BladeShaft
            ColorUtil.rgb(226, 235, 143),  // TRotorShaft
            ColorUtil.rgb(37, 201, 190),   // TStabilizer
            ColorUtil.GRAY,                // TRotorEngine
            ColorUtil.LTGRAY,              // TailRotor
            ColorUtil.rgb(34, 154, 190)};  // HUD

    private PlayerHelicopter(Dimension map, int initFuel, Transform startP) {
        super(map, initFuel, color, startP);
    }

    public static PlayerHelicopter getInstance() {
        if(instance == null) {
            Dimension mapSize    = GameWorld.getInstance().getMapSize();
            int initFuel         = GameWorld.getInstance().getInitialFuel();
            Transform startPoint = GameWorld.getInstance().getTakeOffPoint();

            instance = new PlayerHelicopter(mapSize, initFuel, startPoint);
        }
        return instance;
    }

    public void reset() {
        instance = null;
    }
}