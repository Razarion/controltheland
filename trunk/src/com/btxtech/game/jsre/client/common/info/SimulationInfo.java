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

    @Override
    public boolean hasServerCommunication() {
        return false;
    }

    public void setTutorialConfig(TutorialConfig tutorialConfig) {
        this.tutorialConfig = tutorialConfig;
    }

    public TutorialConfig getTutorialConfig() {
        return tutorialConfig;
    }
}
