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

package com.btxtech.game.wicket.pages.info;

import com.btxtech.game.wicket.pages.BorderPanel;
import com.btxtech.game.wicket.pages.statistics.BaseKills;
import com.btxtech.game.wicket.pages.statistics.BaseSize;
import com.btxtech.game.wicket.pages.statistics.BaseUpTime;

/**
 * User: beat
 * Date: Oct 14, 2009
 * Time: 2:04:48 PM
 */
public class InfoStatistics extends BorderPanel {
    public InfoStatistics(String id) {
        super(id);
        add(new BaseUpTime(5));
        add(new BaseKills(5));
        add(new BaseSize(5));
    }
}