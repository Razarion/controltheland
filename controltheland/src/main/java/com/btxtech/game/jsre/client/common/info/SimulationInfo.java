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

package com.btxtech.game.jsre.client.common.info;

import com.btxtech.game.jsre.common.tutorial.TutorialConfig;

/**
 * User: beat
 * Date: 16.07.2010
 * Time: 23:35:28
 */
public class SimulationInfo extends GameInfo {
    private TutorialConfig tutorialConfig;
    private int levelTaskId;
    private int levelNumber;
    private boolean abortable;
    private boolean sellAllowed;

    public void setTutorialConfig(TutorialConfig tutorialConfig) {
        this.tutorialConfig = tutorialConfig;
    }

    public TutorialConfig getTutorialConfig() {
        return tutorialConfig;
    }

    public int getLevelTaskId() {
        return levelTaskId;
    }

    public void setLevelTaskId(int levelTaskId) {
        this.levelTaskId = levelTaskId;
    }

    public int getLevelNumber() {
        return levelNumber;
    }

    public void setLevelNumber(int levelNumber) {
        this.levelNumber = levelNumber;
    }

    public boolean isAbortable() {
        return abortable;
    }

    public void setAbortable(boolean abortable) {
        this.abortable = abortable;
    }

    @Override
    public boolean isSellAllowed() {
        return sellAllowed;
    }

    public void setSellAllowed(boolean sellAllowed) {
        this.sellAllowed = sellAllowed;
    }
}
