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

import com.btxtech.game.jsre.client.dialogs.guild.GuildMemberInfo;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.PlanetInfo;
import com.btxtech.game.jsre.common.gameengine.services.PlanetLiteInfo;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.services.common.db.IndexUserType;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.user.DbGuild;
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
import java.util.Arrays;
import java.util.Collection;
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
        @Deprecated
        ALLIANCE_OFFERED,
        @Deprecated
        ALLIANCE_OFFER_ACCEPTED,
        @Deprecated
        ALLIANCE_OFFER_REJECTED,
        @Deprecated
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
        BOT_ENRAGE_UP,
        UNLOCKED_ITEM,
        UNLOCKED_QUEST,
        UNLOCKED_PLANET,
        GUILD_CREATED,
        GUILD_USER_INVITED,
        GUILD_JOINED,
        GUILD_DISMISSED_INVITATION,
        GUILD_MEMBERSHIP_REQUEST,
        GUILD_MEMBERSHIP_REQUEST_DISMISSED,
        GUILD_MEMBER_KICKED,
        GUILD_MEMBER_CHANGED,
        GUILD_TEXT_CHANGED,
        GUILD_LEFT,
        GUILD_CLOSED,
        GUILD_CLOSED_MEMBER_KICKED,
        FRIEND_INVITATION_EMAIL_SENT,
        FRIEND_INVITATION_FACEBOOK_SENT,
        FRIEND_INVITATION_BONUS
    }

    public static final Collection<?> ALL_GUILD_TYPES = Arrays.asList(Type.GUILD_CREATED,
            Type.GUILD_USER_INVITED,
            Type.GUILD_JOINED,
            Type.GUILD_DISMISSED_INVITATION,
            Type.GUILD_MEMBERSHIP_REQUEST,
            Type.GUILD_MEMBERSHIP_REQUEST_DISMISSED,
            Type.GUILD_MEMBER_KICKED,
            Type.GUILD_MEMBER_CHANGED,
            Type.GUILD_TEXT_CHANGED,
            Type.GUILD_LEFT,
            Type.GUILD_CLOSED,
            Type.GUILD_CLOSED_MEMBER_KICKED);


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
    @Index(name = "GAME_HISTORY_INDEX_TYPE")
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Type type;
    @Index(name = "GAME_HISTORY_INDEX_ACTOR_USER")
    private Integer actorUserId;
    private String actorUserName;
    @Index(name = "GAME_HISTORY_INDEX_TARGET_USER")
    private Integer targetUserId;
    private String targetUserName;
    private Integer actorBaseId;
    private String actorBaseName;
    private Integer targetBaseId;
    private String targetBaseName;
    private String itemTypeName;
    private Integer itemId;
    private Integer itemTypeId;
    private String levelName;
    private String levelTaskName;
    private Integer levelTaskId;
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
    private String text;
    private Integer planetId;
    private String planetName;
    @Index(name = "GAME_HISTORY_INDEX_GUILD_ID")
    private Integer guildId;
    private String guildName;
    private String rank;

    /**
     * Used by hibernate
     */
    protected DbHistoryElement() {
    }

    public DbHistoryElement(Type type, User actorUser, User targetUser, SimpleBase actorBase, SimpleBase targetBase, SyncItem syncItem, DbLevel level, DbLevelTask levelTask, PlanetSystemService planetSystemService, String sessionId, Source source, com.btxtech.game.jsre.client.common.Index position, Integer deltaRazarion, Integer razarion, String inventory, String botName, String botInfo, ItemType itemType, PlanetLiteInfo planetLiteInfo, DbGuild dbGuild, GuildMemberInfo.Rank rank, String text) {
        this.sessionId = sessionId;
        this.deltaRazarion = deltaRazarion;
        this.razarion = razarion;
        this.botName = botName;
        this.botInfo = botInfo;
        this.text = text;
        timeStamp = new Date();
        timeStampMs = timeStamp.getTime();
        this.type = type;
        if (actorUser != null) {
            actorUserId = actorUser.getId();
            actorUserName = actorUser.getUsername();
        }
        if (targetUser != null) {
            targetUserId = targetUser.getId();
            targetUserName = targetUser.getUsername();
        }
        if (actorBase != null) {
            actorBaseId = actorBase.getBaseId();
            actorBaseName = planetSystemService.getServerPlanetServices(actorBase).getBaseService().getBaseName(actorBase);
        }
        if (targetBase != null) {
            targetBaseId = targetBase.getBaseId();
            targetBaseName = planetSystemService.getServerPlanetServices(targetBase).getBaseService().getBaseName(targetBase);
        }
        if (syncItem != null) {
            itemTypeName = syncItem.getItemType().getName();
            itemTypeId = syncItem.getItemType().getId();
            itemId = syncItem.getId().getId();
        } else if (itemType != null) {
            itemTypeName = itemType.getName();
            itemTypeId = itemType.getId();
        }
        levelName = level != null ? level.getName() : null;
        if (levelTask != null) {
            levelTaskName = levelTask.getName();
            levelTaskId = levelTask.getId();
        }
        this.source = source;
        this.position = position;
        this.inventory = inventory;
        if (planetLiteInfo != null) {
            planetName = planetLiteInfo.getName();
            planetId = planetLiteInfo.getPlanetId();
        } else if (actorBase != null) {
            PlanetInfo planetInfo = planetSystemService.getServerPlanetServices(actorBase).getPlanetInfo();
            planetName = planetInfo.getName();
            planetId = planetInfo.getPlanetId();
        }
        if (dbGuild != null) {
            guildId = dbGuild.getId();
            guildName = dbGuild.getName();
        }
        if (rank != null) {
            this.rank = rank.name();
        }
    }

    public Integer getId() {
        return id;
    }

    public String getActorBaseName() {
        return actorBaseName;
    }

    public Integer getActorUserId() {
        return actorUserId;
    }

    public Integer getTargetUserId() {
        return targetUserId;
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

    public Integer getItemId() {
        return itemId;
    }

    public String getLevelName() {
        return levelName;
    }

    public String getLevelTaskName() {
        return levelTaskName;
    }

    public Integer getLevelTaskId() {
        return levelTaskId;
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

    public Integer getPlanetId() {
        return planetId;
    }

    public String getPlanetName() {
        return planetName;
    }

    public Integer getGuildId() {
        return guildId;
    }

    public String getGuildName() {
        return guildName;
    }

    public String getRank() {
        return rank;
    }

    public String getText() {
        return text;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public String getActorUserName() {
        return actorUserName;
    }

    public String getTargetUserName() {
        return targetUserName;
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
