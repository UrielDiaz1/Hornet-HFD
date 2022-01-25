package org.csc133.a5.views;

import com.codename1.charts.util.ColorUtil;
import com.codename1.ui.*;
import com.codename1.ui.layouts.GridLayout;
import org.csc133.a5.GameWorld;
import org.csc133.a5.gameobjects.*;

public class GlassCockpit extends Container {
    private final GameWorld gw;
    private final DigitComponent heading;
    private final DigitComponent speed;
    private final DigitComponent fuel;
    private final DigitComponent numFires;
    private final DigitComponent totalFire;
    private final DigitComponent damagePercent;
    private final DigitComponent financialLoss;

    public GlassCockpit() {
        setLayout(new GridLayout(2, 7));
        this.gw = GameWorld.getInstance();
        this.getAllStyles().setBgTransparency(255);
        this.getAllStyles().setBgColor(ColorUtil.WHITE);

        heading       = digitComponentMaker(3, ColorUtil.rgb(255, 0, 0));
        speed         = digitComponentMaker(2, ColorUtil.rgb(252, 118, 41));
        fuel          = digitComponentMaker(5, ColorUtil.YELLOW);
        numFires      = digitComponentMaker(3, ColorUtil.GREEN);
        totalFire     = digitComponentMaker(4, ColorUtil.CYAN);
        damagePercent = digitComponentMaker(3, ColorUtil.BLUE);
        financialLoss = digitComponentMaker(4, ColorUtil.rgb(132, 41, 252));

        addHeaderLabels();
        addDigitComponents();
    }

    private DigitComponent digitComponentMaker(int numDigits, int ledColor) {
        DigitComponent dc = new DigitComponent(numDigits);
        dc.setLedColor(ledColor);
        return dc;
    }

    private void addHeaderLabels() {
        add(labelMaker("Heading"));
        add(labelMaker("Speed"));
        add(labelMaker("Fuel"));
        add(labelMaker("Fires"));
        add(labelMaker("Total Fire Size"));
        add(labelMaker("Damage %"));
        add(labelMaker("Financial Loss $"));
    }

    private Label labelMaker(String text) {
        Label lbl = new Label(text);
        lbl.getAllStyles().setAlignment(CENTER);
        lbl.getAllStyles().setFont(Font.createSystemFont(Font.FACE_SYSTEM,
                                                         Font.STYLE_BOLD,
                                                         Font.SIZE_MEDIUM));
        return lbl;
    }

    private void addDigitComponents() {
        add(heading);
        add(speed);
        add(fuel);
        add(numFires);
        add(totalFire);
        add(damagePercent);
        add(financialLoss);
    }

    public void updateDisplay() {
        double totalFireSize    = 0;
        double buildingDmg      = 0;
        double totalFinanceLoss = 0;

        for(Fire fire : gw.getGameObjectCollection().getFires()) {
            totalFireSize += fire.getSize();
        }

        for(Building building : gw.getGameObjectCollection().getBuildings()) {
            buildingDmg += building.getDamagePercentage();
            totalFinanceLoss += building.getFinancialLoss();
        }

        updateHelicopterLbl(PlayerHelicopter.getInstance());
        updateFireLbl((int) totalFireSize);
        updateBuildingLbl((int) buildingDmg, (int) totalFinanceLoss);
    }

    private void updateHelicopterLbl(Helicopter helicopter) {
        heading.setValue(helicopter.getHeading());
        Label hed = new Label();

        speed.setValue(helicopter.getSpeed());
        fuel.setValue(helicopter.getFuel());
    }

    private void updateFireLbl(int totalFireSize) {
        numFires.setValue(gw.getNumOfFires());
        totalFire.setValue(totalFireSize);
    }

    private void updateBuildingLbl(int buildingDmg, int totalFinanceLoss) {
        int percentage = buildingDmg/gw.getNumOfBuildings();
        damagePercent.setValue(percentage);
        financialLoss.setValue(totalFinanceLoss);
    }
}
