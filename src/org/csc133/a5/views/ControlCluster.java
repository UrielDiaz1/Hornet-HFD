package org.csc133.a5.views;

import com.codename1.charts.util.ColorUtil;
import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.Container;
import com.codename1.ui.Font;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.GridLayout;
import com.codename1.ui.plaf.Style;
import org.csc133.a5.GameWorld;
import org.csc133.a5.commands.*;

public class ControlCluster extends Container {
    private final Container stackLeft  = new Container(new GridLayout(1, 3));
    private final Container stackMid   = new Container(new GridLayout(1, 5));
    private final Container stackRight = new Container(new GridLayout(1, 3));
    private final Button bLeft;
    private final Button bRight;
    private final Button bFight;
    private final Button bEngine;
    private final Button bZoom;
    private final Button bExit;
    private final Button bDrink;
    private final Button bBrake;
    private final Button bAccel;
    private final Button bVolume;
    private final Button bRestart;
    private String[] zoomButtonText;
    private int zoomIndex = 0;

    public ControlCluster(MapView mv) {
        setLayout(new BorderLayout());

        // Component's background is automatically transparent (0). Removing
        // the transparency allows the change of color to be visible.
        //
        this.getAllStyles().setBgColor(ColorUtil.WHITE);
        this.getAllStyles().setBgTransparency(255);

        bLeft    = buttonMaker(new TurnLeftCommand(), "Left");
        bRight   = buttonMaker(new TurnRightCommand(), "Right");
        bFight   = buttonMaker(new FightCommand(), "Fight");
        bEngine  = buttonMaker(new StartEngineCommand(), "Start Engine");
        bZoom    = buttonMaker(new ZoomCommand(mv, this), "Zoom Out");
        bExit    = buttonMaker(new ExitCommand(), "Exit");
        bDrink   = buttonMaker(new DrinkCommand(), "Drink");
        bBrake   = buttonMaker(new BrakeCommand(), "Brake");
        bAccel   = buttonMaker(new AccelerateCommand(), "Accel");
        bVolume  = buttonMaker(new SoundVolumeCommand(this), "Sound Off");
        bRestart = buttonMaker(new RestartCommand(), "Restart");

        Button.setSameWidth(bLeft, bRight, bFight, bDrink, bAccel, bBrake);
        bEngine.getDisabledStyle().setBgColor(ColorUtil.GRAY);
        bEngine.getDisabledStyle().setFgColor(ColorUtil.LTGRAY);

        addButtons();
    }

    // Binds command to button and changes appearance.
    //
    private Button buttonMaker(Command cmd, String btnText) {
        Button button = new Button(btnText);
        button.setCommand(cmd);

        Style settingsStyle = button.getAllStyles();
        settingsStyle.setFont(Font.createSystemFont(Font.FACE_SYSTEM,
                                                    Font.STYLE_BOLD,
                                                    Font.SIZE_MEDIUM));
        settingsStyle.setFgColor(ColorUtil.BLUE);
        settingsStyle.setBgColor(ColorUtil.LTGRAY);
        settingsStyle.setBgTransparency(255);

        // Gives feedback to user by changing pressed buttons to white.
        //
        button.getPressedStyle().setBgColor(ColorUtil.WHITE, true);

        return button;
    }

    private void addButtons() {
        // Group buttons in a stack to make them easier to place.
        //
        stackLeft.add(bLeft);
        stackLeft.add(bRight);
        stackLeft.add(bFight);
        stackMid.add(bExit);
        stackMid.add(bVolume);
        stackMid.add(bEngine);
        stackMid.add(bZoom);
        stackMid.add(bRestart);
        stackRight.add(bDrink);
        stackRight.add(bBrake);
        stackRight.add(bAccel);

        // This paints over those annoying gaps between the buttons that
        // happen due to button width's rounding error.
        //
        stackLeft.getAllStyles().setBgColor(ColorUtil.LTGRAY);
        stackLeft.getAllStyles().setBgTransparency(255);
        stackRight.getAllStyles().setBgColor(ColorUtil.LTGRAY);
        stackRight.getAllStyles().setBgTransparency(255);

        // Prevents the centered button from being scaled to fill the
        // available space.
        //
        ((BorderLayout)this.getLayout()).setCenterBehavior(
                                        BorderLayout.CENTER_BEHAVIOR_CENTER);
        add(BorderLayout.WEST, stackLeft);
        add(BorderLayout.CENTER, stackMid);
        add(BorderLayout.EAST, stackRight);
    }

    public void updateEngineButton() {
        String state = GameWorld.getInstance().getHelicopterState();
        switch (state) {
            case "Off":
            case "Stopping":
                editEngineButtonState("Start Engine", state);
                break;
            case "Starting":
            case "Can land":
            case "Ready":
                editEngineButtonState("Stop Engine", state);
                break;
        }
    }

    private void editEngineButtonState(String text, String state) {
        boolean isEnabled = !state.equals("Ready");
        bEngine.setText(text);
        bEngine.setEnabled(isEnabled);
    }

    public void setZoomButtonText(String[] zoomButtonText) {
        this.zoomButtonText = zoomButtonText;
    }

    public void updateZoomButton() {
        zoomIndex++;

        // Resets the zoom.
        //
        if(zoomIndex == zoomButtonText.length) {
            zoomIndex = 0;
        }
        bZoom.setText(zoomButtonText[zoomIndex]);
    }

    public void updateVolumeButton(String text) {
        bVolume.setText(text);
    }

    @Override
    public void laidOut() {
        super.laidOut();
        GameWorld.getInstance().layButtons(this.getWidth(), this.getHeight());
    }
}
