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

package com.btxtech.game.jsre.client.control;

import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.control.task.AbstractStartupTask;
import com.btxtech.game.jsre.client.control.task.ClearGame;
import com.btxtech.game.jsre.client.control.task.LoadSimulationInfoStartupTask;
import com.btxtech.game.jsre.client.control.task.RunSimulationStartupTask;
import com.btxtech.game.jsre.client.control.task.SimulationDeltaStartupTask;

/**
 * User: beat
 * Date: 19.06.2010
 * Time: 18:21:15
 */
public enum WarmSimulatedGameStartupTaskEnum implements StartupTaskEnum {
    CLEAR_GAME("Clear game") {
        @Override
        public AbstractStartupTask createTask() {
            return new ClearGame(this);
        }

        @Override
        public String getI18nText() {
            return ClientI18nHelper.CONSTANTS.startupClearGame();
        }
    },
    DOWNLOAD_GAME_INFO("Load simulation game information") {
        @Override
        public AbstractStartupTask createTask() {
            return new LoadSimulationInfoStartupTask(this);
        }

        @Override
        public String getI18nText() {
            return ClientI18nHelper.CONSTANTS.startupLoadSimulationGameInfo();
        }
    },
    INIT_GAME("Delta start simulated Game") {
        @Override
        public AbstractStartupTask createTask() {
            return new SimulationDeltaStartupTask(this);
        }

        @Override
        public String getI18nText() {
            return ClientI18nHelper.CONSTANTS.startupDeltaStartSimulatedGame();
        }
    },
    RUN_SIMULATED_GAME("Run simulated Game") {
        @Override
        public AbstractStartupTask createTask() {
            return new RunSimulationStartupTask(this);
        }

        @Override
        public String getI18nText() {
            return ClientI18nHelper.CONSTANTS.startupRunSimulatedGame();
        }
    };

    private StartupTaskEnumHtmlHelper startupTaskEnumHtmlHelper;

    WarmSimulatedGameStartupTaskEnum(String niceText) {
        startupTaskEnumHtmlHelper = new StartupTaskEnumHtmlHelper(niceText, this);
    }

    @Override
    public boolean isFirstTask() {
        return ordinal() == 0;
    }

    @Override
    public StartupTaskEnumHtmlHelper getStartupTaskEnumHtmlHelper() {
        return startupTaskEnumHtmlHelper;
    }


}
