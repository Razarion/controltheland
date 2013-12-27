package com.btxtech.game.jsre.common.tutorial;

import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.simulation.AbstractTask;
import com.btxtech.game.jsre.client.simulation.ScrollToEventTask;

/**
 * User: beat
 * Date: 11.09.13
 * Time: 15:38
 */
public class ScrollToEventTaskConfig extends AbstractTaskConfig {
    private Rectangle scrollTargetRectangle;

    @Override
    public AbstractTask createTask() {
        return new ScrollToEventTask(this);
    }

    public void setScrollTargetRectangle(Rectangle scrollTargetRectangle) {
        this.scrollTargetRectangle = scrollTargetRectangle;
    }

    public Rectangle getScrollTargetRectangle() {
        return scrollTargetRectangle;
    }
}
