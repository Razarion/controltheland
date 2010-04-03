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

package com.btxtech.game.services.forum.impl;

import com.btxtech.game.services.forum.AbstractForumEntry;
import java.util.Comparator;
import java.util.Date;

/**
 * User: beat
 * Date: 03.04.2010
 * Time: 17:29:45
 */
public class LastPostComparator implements Comparator<AbstractForumEntry> {
    @Override
    public int compare(AbstractForumEntry o1, AbstractForumEntry o2) {
        Date date1 = o1.getLastPost();
        Date date2 = o2.getLastPost();

        if (date1 == null && date2 == null) {
            return 0;
        } else if (date1 != null && date2 == null) {
            return -1;
        } else if (date1 == null && date2 != null) {
            return 1;
        } else {
            return date2.compareTo(date1);
        }
    }
}
