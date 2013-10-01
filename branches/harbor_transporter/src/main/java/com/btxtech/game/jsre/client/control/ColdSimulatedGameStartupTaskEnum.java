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
import com.btxtech.game.jsre.client.control.task.CompatibilityCheckerStartupTask;
import com.btxtech.game.jsre.client.control.task.GuiStartupTask;
import com.btxtech.game.jsre.client.control.task.ImageSpriteMapPreloaderStartupTask;
import com.btxtech.game.jsre.client.control.task.LoadSimulationInfoStartupTask;
import com.btxtech.game.jsre.client.control.task.LoadStartJsAbstractStartupTask;
import com.btxtech.game.jsre.client.control.task.RunSimulationStartupTask;
import com.btxtech.game.jsre.client.control.task.SimulationStartupTask;

/**
 * User: beat
 * Date: 19.06.2010
 * Time: 18:21:15
 */
public enum ColdSimulatedGameStartupTaskEnum implements StartupTaskEnum {
    LOAD_JAVA_SCRIPT("Load JavaScript") {
        @Override
        public AbstractStartupTask createTask() {
            return new LoadStartJsAbstractStartupTask(this);
        }

        @Override
        public String getI18nText() {
            return ClientI18nHelper.CONSTANTS.startupLoadJavaScript();
        }
    },
    COMPATIBILITY_CHECK("Check compatibility") {
        @Override
        public AbstractStartupTask createTask() {
            return new CompatibilityCheckerStartupTask(this);
        }

        @Override
        public String getI18nText() {
            return ClientI18nHelper.CONSTANTS.startupCheckCompatibility();
        }
    },
    INIT_GUI("Init GUI") {
        @Override
        public AbstractStartupTask createTask() {
            return new GuiStartupTask(this);
        }
        @Override
        public String getI18nText() {
            return ClientI18nHelper.CONSTANTS.startupInitGui();
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
    INIT_GAME("Init simulated Game") {
        @Override
        public AbstractStartupTask createTask() {
            return new SimulationStartupTask(this);
        }
        @Override
        public String getI18nText() {
            return ClientI18nHelper.CONSTANTS.startupInitSimulatedGame();
        }
    },
    PRELOAD_IMAGE_SPRITE_MAPS("Preload image sprite maps") {
        @Override
        public AbstractStartupTask createTask() {
            return new ImageSpriteMapPreloaderStartupTask(this);
        }
        @Override
        public String getI18nText() {
            return ClientI18nHelper.CONSTANTS.startupPreloadImageSpriteMaps();
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

    ColdSimulatedGameStartupTaskEnum(String niceText) {
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
