/*
 * Name: Uriel Diaz Quintero
 * Student ID: ...8442
 */

package org.csc133.a5;

import com.codename1.charts.util.ColorUtil;
import com.codename1.ui.Form;
import com.codename1.ui.Graphics;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.util.UITimer;
import org.csc133.a5.commands.*;
import org.csc133.a5.views.ControlCluster;
import org.csc133.a5.views.GlassCockpit;
import org.csc133.a5.views.MapView;

public class Game extends Form implements Runnable {
    private final GameWorld gw;
    private final MapView mv;
    private final GlassCockpit gc;
    private final ControlCluster cc;

    public Game() {
        setLayout(new BorderLayout());
        this.getAllStyles().setBgColor(ColorUtil.BLACK);

        gw = GameWorld.getInstance();
        mv = new MapView();
        gc = new GlassCockpit();
        cc = new ControlCluster(mv);

        setUpCommands();
        setUpViews();
        setUpTimer();
        show();
        gw.init();
        setUpThreads();
    }

    private void setUpCommands() {
        addKeyListener('Q', new ExitCommand());
        addKeyListener('f', new FightCommand());
        addKeyListener('d', new DrinkCommand());
        addKeyListener('s', new StartEngineCommand());
        addKeyListener('r', new RestartCommand());
        addKeyListener('z', new ZoomCommand(mv, cc));
        addKeyListener('v', new SoundVolumeCommand(cc));
        addKeyListener(-93, new TurnLeftCommand());
        addKeyListener(-94, new TurnRightCommand());
        addKeyListener(-91, new AccelerateCommand());
        addKeyListener(-92, new BrakeCommand());
    }

    private void setUpViews() {
        this.add(BorderLayout.NORTH,  gc);
        this.add(BorderLayout.CENTER, mv);
        this.add(BorderLayout.SOUTH,  cc);
    }

    private void setUpTimer() {
        UITimer timer = new UITimer(this);
        timer.schedule(100, true, this);
    }

    private void setUpThreads() {
        new Thread(gw::startSoundThreads).start();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
    }

    @Override
    public void run() {
        gw.updateLocalTransforms();
        cc.updateEngineButton();
        gc.updateDisplay();
        gw.tick();
        repaint();
    }
}