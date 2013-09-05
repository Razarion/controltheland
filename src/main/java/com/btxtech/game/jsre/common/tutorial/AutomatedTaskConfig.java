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

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.LevelScope;
import com.btxtech.game.jsre.client.common.RadarMode;
import com.btxtech.game.jsre.client.utg.tip.GameTipConfig;
import com.btxtech.game.jsre.common.gameengine.services.PlanetInfo;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotConfig;
import com.btxtech.game.jsre.common.utg.config.ConditionConfig;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * User: beat
 * Date: 17.07.2010
 * Time: 17:25:27
 */
public class AutomatedTaskConfig extends TaskConfig {
    private Index scrollToPosition;

    /**
     * Used by GWT
     */
    public AutomatedTaskConfig() {
    }

    public AutomatedTaskConfig(Index scrollToPosition, GameTipConfig gameTipConfig) {
        super(Collections.<ItemTypeAndPosition>emptyList(),
                null,
                null,
                0,
                0,
                0,
                "",
                null,
                Collections.<Integer, Integer>emptyMap(),
                RadarMode.MAP_AND_UNITS,
                gameTipConfig,
                false); // TODO bad just random values used -> macke configurable
        this.scrollToPosition = scrollToPosition;
    }

    public Index getScrollToPosition() {
        return scrollToPosition;
    }
}
