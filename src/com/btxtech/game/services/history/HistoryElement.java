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

package com.btxtech.game.services.history;

import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.services.user.User;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * User: beat
 * Date: Jul 5, 2009
 * Time: 7:28:46 PM
 */
@Entity(name = "TRACKER_HISTORY")
public class HistoryElement implements Serializable {
    public enum Type {
        BASE_STARTED,
        BASE_DEFEATED,
        BASE_SURRENDERED,
        ITEM_CREATED,
        ITEM_DESTROYED
    }

    @Id
    @GeneratedValue
    private Integer id;
    @Column(nullable = false)
    private Date timeStamp;
    private long timeStampMs;
    @Column(nullable = false)
    private Type type;
    private String actorBaseName;
    private String actorUserName;
    private String actorItemName;
    private String targetBaseName;
    private String targetUserName;
    private String targetItemName;

    /**
     * Used by hibernate
     */
    protected HistoryElement() {
    }

    public HistoryElement(Type type, String actorBaseName, User actorUser, SyncBaseItem actorItem, String targetBase, User targetUser, SyncBaseItem targetItem) {
        timeStamp = new Date();
        timeStampMs = timeStamp.getTime();
        this.type = type;
        this.actorUserName = actorUser.getName();
        this.actorBaseName = actorBaseName;
        if (actorItem != null) {
            actorItemName = actorItem.getBaseItemType().getName();
        }
        this.targetBaseName = targetBase;
        if(targetUser != null) {
           this.targetUserName = targetUser.getName();
        }
        if (targetItem != null) {
            targetItemName = targetItem.getBaseItemType().getName();
        }
    }
    
    public String getActorBaseName() {
        return actorBaseName;
    }

    public String getActorUserName() {
        return actorUserName;
    }

    public String getTargetUserName() {
        return targetUserName;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public Type getType() {
        return type;
    }

    public String getActorItemName() {
        return actorItemName;
    }

    public String getTargetBaseName() {
        return targetBaseName;
    }

    public String getTargetUser() {
        return targetUserName;
    }

    public String getTargetItemName() {
        return targetItemName;
    }

    public long getTimeStampMs() {
        return timeStampMs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HistoryElement that = (HistoryElement) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
