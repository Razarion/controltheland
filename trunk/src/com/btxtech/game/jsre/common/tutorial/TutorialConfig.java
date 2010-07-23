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

import com.btxtech.game.jsre.common.SimpleBase;
import java.io.Serializable;
import java.util.List;

/**
 * User: beat
 * Date: 17.07.2010
 * Time: 23:25:25
 */
public class TutorialConfig implements Serializable {
    private List<TaskConfig> taskConfigs;
    private SimpleBase ownBase;
    private String description;

    /**
     * Used by GWT
     */
    public TutorialConfig() {
    }

    public TutorialConfig(List<TaskConfig> taskConfigs, SimpleBase ownBase, String description) {
        this.taskConfigs = taskConfigs;
        this.ownBase = ownBase;
        this.description = description;
    }

    public List<TaskConfig> getTasks() {
        return taskConfigs;
    }

    public SimpleBase getOwnBase() {
        return ownBase;
    }

    public String getDescription() {
        return description;
    }
}
