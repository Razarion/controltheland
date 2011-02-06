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

import com.btxtech.game.wicket.pages.basepage.BasePage;

/**
 * User: beat
 * Date: 21.03.2010
 * Time: 15:02:07
 */
public class Info extends BasePage {
    public Info() {
        add(new InfoMainContent("infoMain"));
        add(new InfoStatistics("infoStatistics"));
    }
}