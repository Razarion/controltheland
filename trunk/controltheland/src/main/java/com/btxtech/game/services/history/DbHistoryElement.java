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
import com.btxtech.game.services.common.db.IndexUserType;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.utg.DbLevel;
import com.btxtech.game.services.utg.DbLevelTask;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
        BOX_DROPPED,
        BOX_EXPIRED,
        BOX_PICKED,
        RAZARION_FROM_BOX,
        RAZARION_BOUGHT,
        RAZARION_SPENT,
        INVENTORY_ITEM_FROM_BOX,
        INVENTORY_ARTIFACT_FROM_BOX,
        INVENTORY_ITEM_USED,
        INVENTORY_ITEM_BOUGHT,
        INVENTORY_ARTIFACT_BOUGHT,
        BOT_ENRAGE_NORMAL,
        BOT_ENRAGE_UP
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
    @Enumerated(EnumType.STRING)
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
    private Integer itemTypeId;
    private String levelName;
    private String levelTaskName;
    @Index(name = "GAME_HISTORY_INDEX_SESSION")
    private String sessionId;
    private Source source;
    @org.hibernate.annotations.Type(type = "index")
    @Columns(columns = {@Column(name = "xPos"), @Column(name = "yPos")})
    private com.btxtech.game.jsre.client.common.Index position;
    private Integer deltaRazarion;
    private Integer razarion;
    private String inventory;
    private String botName;
    private String botInfo;
    private String planetName;

    /**
     * Used by hibernate
     */
    protected DbHistoryElement() {
    }

    public DbHistoryElement(Type type, User actorUser, User targetUser, SimpleBase actorBase, SimpleBase targetBase, SyncItem syncItem, DbLevel level, DbLevelTask levelTask, PlanetSystemService planetSystemService, String sessionId, Source source, com.btxtech.game.jsre.client.common.Index position, Integer deltaRazarion, Integer razarion, String inventory, String botName, String botInfo) {
        this.sessionId = sessionId;
        this.deltaRazarion = deltaRazarion;
        this.razarion = razarion;
        this.botName = botName;
        this.botInfo = botInfo;
        timeStamp = new Date();
        timeStampMs = timeStamp.getTime();
        this.type = type;
        actorUserName = actorUser != null ? actorUser.getUsername() : null;
        targetUserName = targetUser != null ? targetUser.getUsername() : null;
        actorBaseId = actorBase != null ? actorBase.getBaseId() : null;
        actorBaseName = actorBase != null ? planetSystemService.getServerPlanetServices(actorBase).getBaseService().getBaseName(actorBase) : null;
        targetBaseId = targetBase != null ? targetBase.getBaseId() : null;
        targetBaseName = targetBase != null ? planetSystemService.getServerPlanetServices(targetBase).getBaseService().getBaseName(targetBase) : null;
        itemTypeName = syncItem != null ? syncItem.getItemType().getName() : null;
        itemTypeId =  syncItem != null ? syncItem.getId().getId() : null;
        levelName = level != null ? level.getName() : null;
        levelTaskName = levelTask != null ? levelTask.getName() : null;
        this.source = source;
        this.position = position;
        this.inventory = inventory;
        planetName = actorBase != null ? planetSystemService.getServerPlanetServices(actorBase).getPlanetInfo().getName() : null;
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

    public Integer getItemTypeId() {
        return itemTypeId;
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

    public Integer getDeltaRazarion() {
        return deltaRazarion;
    }

    public Integer getRazarion() {
        return razarion;
    }

    public String getInventory() {
        return inventory;
    }

    public String getBotName() {
        return botName;
    }

    public String getBotInfo() {
        return botInfo;
    }

    public String getPlanetName() {
        return planetName;
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
