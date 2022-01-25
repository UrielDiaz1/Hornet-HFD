package org.csc133.a5.commands;

import com.codename1.ui.Command;
import com.codename1.ui.events.ActionEvent;
import org.csc133.a5.GameWorld;
import org.csc133.a5.gameobjects.PlayerHelicopter;

public class FightCommand extends Command {
    public FightCommand() {
        super("Fight");
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        GameWorld.getInstance()
                 .attemptFightFire(PlayerHelicopter.getInstance());
    }
}
