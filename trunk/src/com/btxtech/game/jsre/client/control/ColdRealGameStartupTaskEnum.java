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
import com.btxtech.game.jsre.client.control.task.RealStartupTask;
import com.btxtech.game.jsre.client.control.task.GuiStartupTask;
import com.btxtech.game.jsre.client.control.task.InitItemStartupTask;
import com.btxtech.game.jsre.client.control.task.LoadGameInfoStartupTask;
import com.btxtech.game.jsre.client.control.task.LoadMapImageStartupTask;
import com.btxtech.game.jsre.client.control.task.LoadStartJsAbstractStartupTask;
import com.btxtech.game.jsre.client.control.task.LoadSyncInfoStartupTask;
import com.btxtech.game.jsre.client.control.task.RunRealGameStartupTask;

/**
 * User: beat
 * Date: 19.06.2010
 * Time: 18:21:15
 */
public enum ColdRealGameStartupTaskEnum implements StartupTaskEnum {
    LOAD_JAVA_SCRIPT("Load JavaScript") {
        @Override
        public AbstractStartupTask createTask() {
            return new LoadStartJsAbstractStartupTask(this);
        }},
    INIT_GUI("Init GUI") {
        @Override
        public AbstractStartupTask createTask() {
            return new GuiStartupTask(this);
        }},
    DOWNLOAD_GAME_INFO("Load game information") {
        @Override
        public AbstractStartupTask createTask() {
            return new LoadGameInfoStartupTask(this);
        }},
    INIT_GAME("Init real Game") {
        @Override
        public AbstractStartupTask createTask() {
            return new RealStartupTask(this);
        }},
    LOAD_MAP("Load Map") {
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
    LOAD_MAP_IMAGES("Run real Game") {
        @Override
        public AbstractStartupTask createTask() {
            return new RunRealGameStartupTask(this);
        }};

    private StartupTaskEnumHtmlHelper startupTaskEnumHtmlHelper;

    ColdRealGameStartupTaskEnum(String niceText) {
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
