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

package com.btxtech.game.jsre.client.utg.missions;

import com.btxtech.game.jsre.client.utg.missions.tasks.ScrollButtonTask;
import com.btxtech.game.jsre.client.utg.missions.tasks.ScrollTask;

/**
 * User: beat
 * Date: 26.01.2010
 * Time: 22:20:04
 */
public class ScrollMission extends Mission  {
    public ScrollMission() {
        super("ScrollMission", null);
        addTask(new ScrollTask());
        addTask(new ScrollButtonTask(HtmlConstants.SCROLL_HTML2));
        setAutoTaskChange(true);
    }
}