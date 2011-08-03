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

import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.utg.DbAbstractLevel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * User: beat
 * Date: Jul 5, 2009
 * Time: 7:28:46 PM
 */
@Entity(name = "GAME_HISTORY")
public class DbHistoryElement implements Serializable {
    public enum Type {
        BASE_STARTED,
        BASE_DEFEATED,
        BASE_SURRENDERED,
        ITEM_CREATED,
        ITEM_DESTROYED,
        LEVEL_PROMOTION
    }

    @Id
    @GeneratedValue
    private Integer id;
    @Column(nullable = false)
    private Date timeStamp;
    private long timeStampMs;
    @Column(nullable = false)
    private Type type;
    private String actorUserName;
    private String targetUserName;
    private Integer actorBaseId;
    private String actorBaseName;
    private Integer targetBaseId;
    private String targetBaseName;
    private String itemTypeName;
    private String levelName;
    private String sessionId;

    /**
     * Used by hibernate
     */
    protected DbHistoryElement() {
    }

    public DbHistoryElement(Type type, User actorUser, User targetUser, SimpleBase actorBase, SimpleBase targetBase, SyncBaseItem syncBaseItem, DbAbstractLevel level, BaseService baseService, String sessionId) {
        this.sessionId = sessionId;
        timeStamp = new Date();
        timeStampMs = timeStamp.getTime();
        this.type = type;
        actorUserName = actorUser != null ? actorUser.getUsername() : null;
        targetUserName = targetUser != null ? targetUser.getUsername() : null;
        actorBaseId = actorBase != null ? actorBase.getId() : null;
        actorBaseName = actorBase != null ? baseService.getBaseName(actorBase) : null;
        targetBaseId = targetBase != null ? targetBase.getId() : null;
        targetBaseName = targetBase != null ? baseService.getBaseName(targetBase) : null;
        itemTypeName = syncBaseItem != null ? syncBaseItem.getBaseItemType().getName() : null;
        levelName = level != null ? level.getName() : null;
    }

    public Integer getId() {
        return id;
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

    public Type getType() {
        return type;
    }

    public String getTargetBaseName() {
        return targetBaseName;
    }

    public String getTargetUser() {
        return targetUserName;
    }

    public long getTimeStampMs() {
        return timeStampMs;
    }

    public String getItemTypeName() {
        return itemTypeName;
    }

    public String getLevelName() {
        return levelName;
    }

    public String getSessionId() {
        return sessionId;
    }

    public Integer getActorBaseId() {
        return actorBaseId;
    }

    public Integer getTargetBaseId() {
        return targetBaseId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbHistoryElement that = (DbHistoryElement) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
