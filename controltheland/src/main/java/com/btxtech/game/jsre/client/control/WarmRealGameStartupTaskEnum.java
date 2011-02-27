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

import com.btxtech.game.jsre.client.control.task.AbstractStartupTask;
import com.btxtech.game.jsre.client.control.task.ClearGame;
import com.btxtech.game.jsre.client.control.task.InitItemStartupTask;
import com.btxtech.game.jsre.client.control.task.LoadGameInfoStartupTask;
import com.btxtech.game.jsre.client.control.task.LoadMapImageStartupTask;
import com.btxtech.game.jsre.client.control.task.LoadSyncInfoStartupTask;
import com.btxtech.game.jsre.client.control.task.RealDeltaStartupTask;
import com.btxtech.game.jsre.client.control.task.RunRealGameStartupTask;

/**
 * User: beat
 * Date: 12.12.2010
 * Time: 18:21:15
 */
public enum WarmRealGameStartupTaskEnum implements StartupTaskEnum {
    CLEAR_GAME("Clear game") {
        @Override
        public AbstractStartupTask createTask() {
            return new ClearGame(this);
        }},
    DOWNLOAD_GAME_INFO("Load game information") {
        @Override
        public AbstractStartupTask createTask() {
            return new LoadGameInfoStartupTask(this);
        }},
    INIT_GAME("Delta init real Game") {
        @Override
        public AbstractStartupTask createTask() {
            return new RealDeltaStartupTask(this);
        }},
    LOAD_MAP("Load delta Map") {
        @Override
        public AbstractStartupTask createTask() {
            return new LoadMapImageStartupTask(this);
        }},
    LOAD_UNITS("Load Units") {
        @Override
        public AbstractStartupTask createTask() {
            return new LoadSyncInfoStartupTask(this);
        }},
    START_ACTION_HANDLER("Initialize Units") {
        @Override
        public AbstractStartupTask createTask() {
            return new InitItemStartupTask(this);
        }},
    RUN_REAL_GAME("Run real Game") {
        @Override
        public AbstractStartupTask createTask() {
            return new RunRealGameStartupTask(this);
        }};

    private StartupTaskEnumHtmlHelper startupTaskEnumHtmlHelper;

    WarmRealGameStartupTaskEnum(String niceText) {
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
