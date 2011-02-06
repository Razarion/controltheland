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

package com.btxtech.game.services.mgmt;

import java.util.Date;
import java.io.Serializable;

/**
 * User: beat
 * Date: Sep 23, 2009
 * Time: 3:39:22 PM
 */
public class BackupSummary implements Serializable{
    private Date date;
    private int itemCount;
    private int baseCount;

    public BackupSummary(Date date, int itemCount, int baseCount) {
        this.date = date;
        this.itemCount = itemCount;
        this.baseCount = baseCount;
    }

    public Date getDate() {
        return date;
    }

    public int getItemCount() {
        return itemCount;
    }

    public int getBaseCount() {
        return baseCount;
    }
}
