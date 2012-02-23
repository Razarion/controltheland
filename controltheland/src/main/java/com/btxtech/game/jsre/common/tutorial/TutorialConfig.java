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

    private List<TaskConfig> taskConfigs;
    private String ownBaseName;
    private int width;
    private int height;
    private boolean eventTracking;
    private String inGameHtml;
    private boolean showTip;

    /**
     * Used by GWT
     */
    TutorialConfig() {
    }

    public TutorialConfig(List<TaskConfig> taskConfigs,
                          String ownBaseName,
                          int width,
                          int height,
                          boolean eventTracking,
                          String inGameHtml,
                          boolean showTip) {
        this.taskConfigs = taskConfigs;
        this.ownBaseName = ownBaseName;
        this.width = width;
        this.height = height;
        this.eventTracking = eventTracking;
        this.inGameHtml = inGameHtml;
        this.showTip = showTip;
    }

    public List<TaskConfig> getTasks() {
        return taskConfigs;
    }

    public String getOwnBaseName() {
        return ownBaseName;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isEventTracking() {
        return eventTracking;
    }

    public String getInGameHtml() {
        return inGameHtml;
    }

    public boolean isShowTip() {
        return showTip;
    }
}
