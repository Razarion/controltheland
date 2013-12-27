/*
 * Copyright (c) 2010.
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; version 2 of the License.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 */

package com.btxtech.game.jsre.common.tutorial;

import java.io.Serializable;
import java.util.List;

/**
 * User: beat
 * Date: 17.07.2010
 * Time: 23:25:25
 */
public class TutorialConfig implements Serializable {
    public static enum TYPE {
        TUTORIAL,
        TASK,
        TUTORIAL_FAILED
    }

    private List<AbstractTaskConfig> abstractTaskConfigs;
    private String ownBaseName;
    private boolean eventTracking;
    private boolean showTip;
    private boolean disableScroll;

    /**
     * Used by GWT
     */
    TutorialConfig() {
    }

    public TutorialConfig(List<AbstractTaskConfig> abstractTaskConfigs,
                          String ownBaseName,
                          boolean eventTracking,
                          boolean showTip,
                          boolean disableScroll) {
        this.abstractTaskConfigs = abstractTaskConfigs;
        this.ownBaseName = ownBaseName;
        this.eventTracking = eventTracking;
        this.showTip = showTip;
        this.disableScroll = disableScroll;
    }

    public List<AbstractTaskConfig> getTasks() {
        return abstractTaskConfigs;
    }

    public String getOwnBaseName() {
        return ownBaseName;
    }

    public boolean isEventTracking() {
        return eventTracking;
    }

    public boolean isShowTip() {
        return showTip;
    }

    public boolean isDisableScroll() {
        return disableScroll;
    }
}
