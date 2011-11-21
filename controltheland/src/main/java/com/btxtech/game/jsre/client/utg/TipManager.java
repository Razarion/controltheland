package com.btxtech.game.jsre.client.utg;

import com.btxtech.game.jsre.client.terrain.MapWindow;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.SimpleEntry;
import com.google.gwt.user.client.Timer;

import java.util.ArrayList;
import java.util.List;

/**
 * User: beat
 * Date: 20.11.2011
 * Time: 23:26:54
 */
public class TipManager {
    private static final TipManager INSTANCE = new TipManager();
    private static final int SHOW_TIME = 3000;
    private Timer timer;
    private TipPanel activeTipPanel;
    private List<SimpleEntry<Integer, String>> tips = new ArrayList<SimpleEntry<Integer, String>>();

    public static TipManager getInstance() {
        return INSTANCE;
    }

    /**
     * Singleton
     */
    private TipManager() {
        tips.add(new SimpleEntry<Integer, String>(20000, "Maximize.jpg"));
        tips.add(new SimpleEntry<Integer, String>(20000, "ArrowKeys.jpg"));
    }

    public void activate() {
        deactivate();
        displayNextTip();
    }

    private void displayNextTip() {
        if (tips.isEmpty()) {
            deactivate();
            return;
        }
        final SimpleEntry<Integer, String> tip = tips.remove(0);
        timer = new Timer() {
            @Override
            public void run() {
                showActiveTipPanel(tip.getValue());
                timer = new Timer() {

                    @Override
                    public void run() {
                        closeActiveTipPanel();
                        displayNextTip();
                    }
                };
                timer.schedule(SHOW_TIME);
            }
        };
        timer.schedule(tip.getKey());
    }

    public void deactivate() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        closeActiveTipPanel();
    }

    private void showActiveTipPanel(String tip) {
        closeActiveTipPanel();
        activeTipPanel = new TipPanel(tip);
        int left = (TerrainView.getInstance().getViewWidth() - TipPanel.WIDTH) / 2;
        int top = (TerrainView.getInstance().getViewHeight() - TipPanel.HEIGHT) / 2;
        MapWindow.getAbsolutePanel().add(activeTipPanel, left, top);
        ClientUserTracker.getInstance().onDialogAppears(activeTipPanel, "Tip: " + activeTipPanel.getTip());
    }


    private void closeActiveTipPanel() {
        if (activeTipPanel != null) {
            MapWindow.getAbsolutePanel().remove(activeTipPanel);
            ClientUserTracker.getInstance().onDialogDisappears(activeTipPanel);
            activeTipPanel = null;
        }
    }

}
