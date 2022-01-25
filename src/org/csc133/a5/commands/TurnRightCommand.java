package org.csc133.a5.commands;

import com.codename1.ui.Command;
import com.codename1.ui.events.ActionEvent;
import org.csc133.a5.GameWorld;

public class TurnRightCommand extends Command {

    public TurnRightCommand() {
        super("Right");
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        GameWorld.getInstance().turnRight();
    }
}
