package com.btxtech.game.jsre.client.utg.tip.dialog;

import com.btxtech.game.jsre.client.terrain.MapWindow;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.client.utg.ClientUserTracker;

/**
 * User: beat
 * Date: 19.12.2011
 * Time: 17:33:45
 */
public class ImageTipEntry extends TipEntry {
    private String image;
    private TipPanel activeTipPanel;

    public ImageTipEntry(int duration, int showTime, String image) {
        super(duration, showTime);
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    @Override
    public void show() {
        activeTipPanel = new TipPanel(this);
        int left = (TerrainView.getInstance().getViewWidth() - TipPanel.WIDTH) / 2;
        int top = (TerrainView.getInstance().getViewHeight() - TipPanel.HEIGHT) / 2;
        MapWindow.getAbsolutePanel().add(activeTipPanel, left, top);
        ClientUserTracker.getInstance().onDialogAppears(activeTipPanel, "Tip: " + image);
    }

    @Override
    public void close() {
        if (activeTipPanel != null) {
            MapWindow.getAbsolutePanel().remove(activeTipPanel);
            ClientUserTracker.getInstance().onDialogDisappears(activeTipPanel);
            activeTipPanel = null;
        }
    }

}
