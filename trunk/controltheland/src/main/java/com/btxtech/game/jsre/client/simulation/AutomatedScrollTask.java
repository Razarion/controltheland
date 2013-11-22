package com.btxtech.game.jsre.client.simulation;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.tutorial.AutomatedScrollTaskConfig;
import com.google.gwt.user.client.Timer;

/**
 * User: beat
 * Date: 11.09.13
 * Time: 16:01
 */
public class AutomatedScrollTask extends AbstractTask {
    private Timer timer;
    private Index scrollToPosition;

    public AutomatedScrollTask(AutomatedScrollTaskConfig automatedScrollTaskConfig) {
        super(automatedScrollTaskConfig);
        scrollToPosition = automatedScrollTaskConfig.getScrollToPosition();
    }

    public void internStart() {
        timer = new Timer() {
            @Override
            public void run() {
                // TODO make smoother with elapsed time
                Index currentPosition = TerrainView.getInstance().getViewOrigin();
                if (currentPosition.equals(scrollToPosition)) {
                    timer.cancel();
                    timer = null;
                    onTaskSucceeded();
                }
                TerrainView.getInstance().moveAbsolute(currentPosition.getPointWithDistance(40, scrollToPosition, false));
            }
        };
        timer.scheduleRepeating(25);
    }

    @Override
    public void internCleanup() {
        // Do nothing
    }
}
