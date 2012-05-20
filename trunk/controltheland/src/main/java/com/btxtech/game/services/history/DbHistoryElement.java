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
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.common.db.IndexUserType;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.utg.DbLevel;
import com.btxtech.game.services.utg.DbLevelTask;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.TypeDef;

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
@TypeDef(name = "index", typeClass = IndexUserType.class)
public class DbHistoryElement implements Serializable {
    public enum Type {
        BASE_STARTED,
        BASE_DEFEATED,
        BASE_SURRENDERED,
        ITEM_CREATED,
        ITEM_DESTROYED,
        LEVEL_PROMOTION,
        LEVEL_TASK_COMPLETED,
        LEVEL_TASK_ACTIVATED,
        LEVEL_TASK_DEACTIVATED,
        ALLIANCE_OFFERED,
        ALLIANCE_OFFER_ACCEPTED,
        ALLIANCE_OFFER_REJECTED,
        ALLIANCE_BROKEN,
        BOX_DROPPER,
        BOX_EXPIRED,
        BOX_PICKED
    }

    public enum Source {
        HUMAN,
        BOT
    }

    @Id
    @GeneratedValue
    private Integer id;
    @Column(nullable = false)
    private Date timeStamp;
    private long timeStampMs;
    @Column(nullable = false)
    private Type type;
    @Index(name = "GAME_HISTORY_INDEX_ACTOR_USER")
    private String actorUserName;
    @Index(name = "GAME_HISTORY_INDEX_TARGET_USER")
    private String targetUserName;
    private Integer actorBaseId;
    private String actorBaseName;
    private Integer targetBaseId;
    private String targetBaseName;
    private String itemTypeName;
    private String levelName;
    private String levelTaskName;
    @Index(name = "GAME_HISTORY_INDEX_SESSION")
    private String sessionId;
    private Source source;
    @org.hibernate.annotations.Type(type = "index")
    @Columns(columns = {@Column(name = "xPos"), @Column(name = "yPos")})
    private com.btxtech.game.jsre.client.common.Index position;

    /**
     * Used by hibernate
     */
    protected DbHistoryElement() {
    }

    public DbHistoryElement(Type type, User actorUser, User targetUser, SimpleBase actorBase, SimpleBase targetBase, SyncItem syncItem, DbLevel level, DbLevelTask levelTask, BaseService baseService, String sessionId, Source source, com.btxtech.game.jsre.client.common.Index position) {
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
        itemTypeName = syncItem != null ? syncItem.getItemType().getName() : null;
        levelName = level != null ? level.getName() : null;
        levelTaskName = levelTask != null ? levelTask.getName() : null;
        this.source = source;
        this.position = position;
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

    public long getTimeStampMs() {
        return timeStampMs;
    }

    public String getItemTypeName() {
        return itemTypeName;
    }

    public String getLevelName() {
        return levelName;
    }

    public String getLevelTaskName() {
        return levelTaskName;
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

    public Source getSource() {
        return source;
    }

    public com.btxtech.game.jsre.client.common.Index getPosition() {
        return position;
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

    @Override
    public String toString() {
        return "DbHistoryElement: " + type + " timeStampe: " + timeStamp;
    }
}
