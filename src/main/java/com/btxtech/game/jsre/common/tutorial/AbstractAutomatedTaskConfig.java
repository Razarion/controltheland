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

import com.btxtech.game.jsre.client.common.RadarMode;
import com.btxtech.game.jsre.client.utg.tip.GameTipConfig;
import com.btxtech.game.jsre.client.utg.tip.PraiseSplashPopupInfo;

import java.util.Collections;

/**
 * User: beat
 * Date: 11.09.2013
 * Time: 17:25:27
 */
abstract public class AbstractAutomatedTaskConfig extends AbstractTaskConfig {

    /**
     * Used by GWT
     */
    AbstractAutomatedTaskConfig() {
    }

    public AbstractAutomatedTaskConfig(GameTipConfig gameTipConfig, PraiseSplashPopupInfo praiseSplashPopupInfo) {
        super(Collections.<ItemTypeAndPosition>emptyList(),
                null,
                0,
                0,
                0,
                "",
                null,
                Collections.<Integer, Integer>emptyMap(),
                RadarMode.MAP_AND_UNITS,
                gameTipConfig,
                false,
                null,
                praiseSplashPopupInfo); // TODO bad just random values used -> make configurable
    }
}
