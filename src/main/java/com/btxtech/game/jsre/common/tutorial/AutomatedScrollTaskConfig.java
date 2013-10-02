package com.btxtech.game.jsre.common.tutorial;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.simulation.AbstractTask;
import com.btxtech.game.jsre.client.simulation.AutomatedScrollTask;
import com.btxtech.game.jsre.client.utg.tip.GameTipConfig;

/**
 * User: beat
 * Date: 25.09.13
 * Time: 16:36
 */
public class AutomatedScrollTaskConfig extends AbstractAutomatedTaskConfig{
    private Index scrollToPosition;

    /**
     * Used by GWT
     */
    AutomatedScrollTaskConfig() {
    }

    public AutomatedScrollTaskConfig(Index scrollToPosition, GameTipConfig gameTipConfig) {
        super(gameTipConfig, null);
        this.scrollToPosition = scrollToPosition;
    }

    public Index getScrollToPosition() {
        return scrollToPosition;
    }

    @Override
    public AbstractTask createTask() {
        return new AutomatedScrollTask(this);
    }
}
