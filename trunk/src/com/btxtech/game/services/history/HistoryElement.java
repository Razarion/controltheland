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
import javax.persistence.ManyToOne;

/**
 * User: beat
 * Date: Jul 5, 2009
 * Time: 7:28:46 PM
 */
@Entity(name = "HISTORY")
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
    @Column(nullable = false)
    private String baseName;
    @ManyToOne()
    private User user;
    private String itemName;
    private String targetBaseName;
    @ManyToOne()
    private User targetUser;
    private String targetItemName;


    /**
     * Used by hibernate
     */
    protected HistoryElement() {
    }

    public HistoryElement(Type type, String base, User user, SyncBaseItem item, String targetBase, User targetUser, SyncBaseItem targetItem) {
        timeStamp = new Date();
        timeStampMs = timeStamp.getTime();
        this.type = type;
        baseName = base;
        this.user = user;
        if (item != null) {
            itemName = item.getBaseItemType().getName();
        }
        targetBaseName = targetBase;
        this.targetUser = targetUser;
        if (targetItem != null) {
            targetItemName = targetItem.getBaseItemType().getName();
        }
    }

    public String getBaseName() {
        return baseName;
    }

    public User getUser() {
        return user;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public Type getType() {
        return type;
    }

    public String getItemName() {
        return itemName;
    }

    public String getTargetBaseName() {
        return targetBaseName;
    }

    public User getTargetUser() {
        return targetUser;
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
