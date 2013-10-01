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

package com.btxtech.game.services.utg;

import com.btxtech.game.services.utg.tracker.DbEventTrackingStart;
import com.btxtech.game.services.utg.tracker.DbTutorialProgress;

import java.io.Serializable;
import java.util.List;

/**
 * User: beat
 * Date: 08.08.2010
 * Time: 13:15:17
 */
public class TutorialTrackingInfo implements Serializable {
    private DbEventTrackingStart dbEventTrackingStart;
    private List<DbTutorialProgress> dbTutorialProgresss;

    public void setDbEventTrackingStart(DbEventTrackingStart dbEventTrackingStart) {
        this.dbEventTrackingStart = dbEventTrackingStart;
    }

    public void setDbTutorialProgresss(List<DbTutorialProgress> dbTutorialProgresss) {
        this.dbTutorialProgresss = dbTutorialProgresss;
    }

    public DbEventTrackingStart getDbEventTrackingStart() {
        return dbEventTrackingStart;
    }

    public List<DbTutorialProgress> getDbTutorialProgresss() {
        return dbTutorialProgresss;
    }
}