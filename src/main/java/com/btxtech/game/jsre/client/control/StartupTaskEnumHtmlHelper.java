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

import java.io.Serializable;

/**
 * User: beat
 * Date: 08.12.2010
 * Time: 14:20:27
 */
public class StartupTaskEnumHtmlHelper implements Serializable {
    public static final String NAME_ID_PREFIX = "startupTaskNameId";
    public static final String IMG_ID_PREFIX = "startupTaskImgId";
    public static final String TIME_ID_PREFIX = "startupTimeImgId";
    public static final String WORKING = "working";
    public static final String FINISHED = "finished";
    public static final String FAILED = "failed";

    private String niceText;
    private StartupTaskEnum startupTaskEnum;

    public StartupTaskEnumHtmlHelper(String niceText, StartupTaskEnum startupTaskEnum) {
        this.niceText = niceText;
        this.startupTaskEnum = startupTaskEnum;
    }

    public String getNiceText() {
        return niceText;
    }

    public String getNameId() {
        return NAME_ID_PREFIX + startupTaskEnum.name();
    }

    public String getImgIdWorking() {
        return NAME_ID_PREFIX + startupTaskEnum.name() + WORKING;
    }

    public String getImgIdFinished() {
        return NAME_ID_PREFIX + startupTaskEnum.name() + FINISHED;
    }

    public String getImgIdFailed() {
        return NAME_ID_PREFIX + startupTaskEnum.name() + FAILED;
    }

    public String getTimeId() {
        return TIME_ID_PREFIX + startupTaskEnum.name();
    }


    public String getI18nText() {
        return startupTaskEnum.getI18nText();
    }
}
