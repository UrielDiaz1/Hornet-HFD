package org.csc133.a5.commands;

import com.codename1.ui.Command;
import com.codename1.ui.events.ActionEvent;
import org.csc133.a5.GameWorld;
import org.csc133.a5.views.ControlCluster;

public class SoundVolumeCommand extends Command {
    private final ControlCluster cc;
    private final String[] state = {"Sound On", "Sound Off"};
    private int soundToggle = 1;

    public SoundVolumeCommand(ControlCluster cc) {
        super("Sound Off");
        this.cc = cc;
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        GameWorld.getInstance().toggleVolume(state[soundToggle]);
        toggle();
        cc.updateVolumeButton(state[soundToggle]);
    }

    private void toggle() {
        soundToggle++;
        if(soundToggle >= state.length) {
            soundToggle = 0;
        }
    }
}
