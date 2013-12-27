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

package com.btxtech.game.services.utg.tracker;

import com.btxtech.game.jsre.common.packets.SyncItemInfo;
import org.hibernate.annotations.Index;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * User: beat
 * Date: Jul 26, 2009
 * Time: 11:09:27 AM
 */
// Tutorial command
@Entity(name = "TRACKER_SYNC_INFOS")
public class DbSyncItemInfo implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(nullable = false)
    private Date niceTimeStamp;
    @Column(nullable = false)
    private long clientTimeStamp;
    @Column(nullable = false, length = 100000)
    private SyncItemInfo syncItemInfo;
    @Index(name = "TRACKER_SYNC_INFOS_INDEX_START_UUID")
    private String startUuid;

    /**
     * Used by hibernate
     */
    public DbSyncItemInfo() {
    }

    public DbSyncItemInfo(SyncItemInfo syncItemInfo) {
        clientTimeStamp = syncItemInfo.getClientTimeStamp();
        niceTimeStamp = new Date();
        this.syncItemInfo = syncItemInfo;
        startUuid = syncItemInfo.getStartUuid();
    }

    public Date getNiceTimeStamp() {
        return niceTimeStamp;
    }

    public long getClientTimeStamp() {
        return clientTimeStamp;
    }

    public SyncItemInfo getSyncItemInfo() {
        return syncItemInfo;
    }

    public String getStartUuid() {
        return startUuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbSyncItemInfo that = (DbSyncItemInfo) o;

        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }
}