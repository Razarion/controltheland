package com.btxtech.game.jsre.common.tutorial;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.simulation.AbstractTask;
import com.btxtech.game.jsre.client.simulation.AutomatedScrollTask;

/**
 * User: beat
 * Date: 25.09.13
 * Time: 16:36
 */
public class AutomatedScrollTaskConfig extends AbstractTaskConfig {
    private Index scrollToPosition;

    public Index getScrollToPosition() {
        return scrollToPosition;
    }

    public void setScrollToPosition(Index scrollToPosition) {
        this.scrollToPosition = scrollToPosition;
    }

    @Override
    public AbstractTask createTask() {
        return new AutomatedScrollTask(this);
    }
}
