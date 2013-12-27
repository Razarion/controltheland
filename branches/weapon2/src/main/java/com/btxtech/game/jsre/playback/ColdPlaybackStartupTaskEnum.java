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

package com.btxtech.game.jsre.playback;

import com.btxtech.game.jsre.client.control.StartupTaskEnum;
import com.btxtech.game.jsre.client.control.StartupTaskEnumHtmlHelper;
import com.btxtech.game.jsre.client.control.task.AbstractStartupTask;
import com.btxtech.game.jsre.client.control.task.GuiStartupTask;

/**
 * User: beat
 * Date: 22.12.2010
 * Time: 14:18:24
 */
public enum ColdPlaybackStartupTaskEnum implements StartupTaskEnum {
    LOAD_PLAYBACK_INFO("Load Playback Info") {
        @Override
        public AbstractStartupTask createTask() {
            return new LoadStartupTask(this);
        }
    },
    INIT_GUI("Init GUI") {
        @Override
        public AbstractStartupTask createTask() {
            return new GuiStartupTask(this);
        }
    },
    INIT_PLAYBACK("Init Playback") {
        @Override
        public AbstractStartupTask createTask() {
            return new InitStartupTask(this);
        }
    },
    RUN("Run") {
        @Override
        public AbstractStartupTask createTask() {
            return new RunPlaybackStartupTask(this);
        }
    };


    private StartupTaskEnumHtmlHelper startupTaskEnumHtmlHelper;

    ColdPlaybackStartupTaskEnum(String niceText) {
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

    @Override
    public String getI18nText() {
        return startupTaskEnumHtmlHelper.getNiceText();
    }
}
