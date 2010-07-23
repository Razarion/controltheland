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

package com.btxtech.game.jsre.client;

/**
 * User: beat
 * Date: 19.06.2010
 * Time: 18:21:15
 */
public enum StartupTask {
    LOAD_JAVA_SCRIPT("Load JavaScript"),
    INIT_GUI("Start engine"),
    DOWNLOAD_GAME_INFO("Load game information"),
    INIT_GAME("Initialize engine"),
    LOAD_UNITS("Load units"),
    START_ACTION_HANDLER("Initialize units"),
    LOAD_MAP_IMAGES("Load map");

    private static final String NAME_ID_PRFIX = "startupTaskNameId";
    private static final String IMG_ID_PRFIX = "startupTaskImgId";
    private static final String TIME_ID_PRFIX = "startupTimeImgId";

    public static StartupTask getFirstTask() {
        return LOAD_JAVA_SCRIPT;
    }

    public static boolean isFirstTask(StartupTask startupTask) {
        return startupTask == getFirstTask();
    }

    private String niceText;

    StartupTask(String niceText) {
        this.niceText = niceText;
    }

    public String getNiceText() {
        return niceText;
    }

    public String getNameId() {
        return NAME_ID_PRFIX + name();
    }

    public String getImgIdWorking() {
        return NAME_ID_PRFIX + name() + "working";
    }

    public String getImgIdFinished() {
        return NAME_ID_PRFIX + name() + "finished";
    }

    public String getImgIdFailed() {
        return NAME_ID_PRFIX + name() + "failed";
    }

    public String getTimeId() {
        return TIME_ID_PRFIX + name();
    }
}
