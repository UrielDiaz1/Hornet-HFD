package org.csc133.a5.commands;

import com.codename1.ui.Command;
import com.codename1.ui.events.ActionEvent;
import org.csc133.a5.views.ControlCluster;
import org.csc133.a5.views.MapView;

public class ZoomCommand extends Command {
    private final MapView mv;
    private final ControlCluster cc;
    private final int zoomWindows = 3;
    private final String[] zoomButtonText;

    public ZoomCommand(MapView mv, ControlCluster cc) {
        super("Zoom Out");
        this.mv = mv;
        this.cc = cc;
        zoomButtonText = new String[zoomWindows];

        initButtonTexts();
        mv.setNumZoomWindows(zoomWindows);
        cc.setZoomButtonText(zoomButtonText);
    }

    private void initButtonTexts() {
        for(int i = 0; i < zoomWindows - 1; i++) {
            zoomButtonText[i] = "Zoom Out";
        }
        zoomButtonText[zoomWindows - 1] = "Zoom In";
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        mv.zoom();
        cc.updateZoomButton();
    }
}
