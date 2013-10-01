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

package com.btxtech.game.services.forum;

import java.util.List;

/**
 * User: beat
 * Date: 21.03.2010
 * Time: 17:16:58
 */
public class Forum {
    private List<DbSubForum> subForums;

    public Forum(List<DbSubForum> subForums) {
        this.subForums = subForums;
    }

    public List<DbSubForum> getSubForums() {
        return subForums;
    }
}