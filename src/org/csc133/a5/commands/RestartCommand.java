package org.csc133.a5.commands;

import com.codename1.ui.Command;
import com.codename1.ui.events.ActionEvent;
import org.csc133.a5.GameWorld;

// Additional command I added to make it easier to test and debug the game.
//
public class RestartCommand extends Command {
    public RestartCommand() {
        super("Restart");
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        GameWorld.getInstance().restartGame();
    }
}
