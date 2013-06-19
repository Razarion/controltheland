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

package com.btxtech.game.services.history.impl;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.dialogs.guild.GuildMemberInfo;
import com.btxtech.game.jsre.client.dialogs.history.HistoryElement;
import com.btxtech.game.jsre.client.dialogs.history.HistoryElementInfo;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.PlanetLiteInfo;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotEnragementStateConfig;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBoxItem;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.common.ReadonlyListContentProvider;
import com.btxtech.game.services.history.*;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.user.DbGuild;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.DbLevel;
import com.btxtech.game.services.utg.DbLevelTask;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * User: beat
 * Date: Jul 5, 2009
 * Time: 7:27:37 PM
 */
@Component("historyService")
public class HistoryServiceImpl implements HistoryService {
    static private final int NEWEST_HISTORY_ELEMENT_COUNT = 30;
    private Log log = LogFactory.getLog(HistoryServiceImpl.class);
    @Autowired
    private UserService userService;
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private PlanetSystemService planetSystemService;
    @Autowired
    private com.btxtech.game.services.connection.Session session;

    private DbHistoryElement.Source determineSource(SimpleBase actor, SimpleBase target) {
        if (actor != null && !planetSystemService.getServerPlanetServices(actor).getBaseService().isBot(actor)) {
            return DbHistoryElement.Source.HUMAN;
        }
        if (target != null && !planetSystemService.getServerPlanetServices(target).getBaseService().isBot(target)) {
            return DbHistoryElement.Source.HUMAN;
        }
        return DbHistoryElement.Source.BOT;
    }

    @Override
    @Transactional
    public void addBaseStartEntry(SimpleBase simpleBase) {
        save(new DbHistoryElement(DbHistoryElement.Type.BASE_STARTED,
                userService.getUser(simpleBase),
                null,
                simpleBase,
                null,
                null,
                null,
                null,
                planetSystemService,
                getSessionId(simpleBase),
                determineSource(simpleBase, null), null, null, null, null, null, null, null, null, null, null, null));
    }

    @Override
    @Transactional
    public void addBaseDefeatedEntry(SimpleBase actor, SimpleBase target) {
        save(new DbHistoryElement(DbHistoryElement.Type.BASE_DEFEATED,
                userService.getUser(actor),
                userService.getUser(target),
                actor,
                target,
                null,
                null,
                null,
                planetSystemService,
                getSessionId(actor),
                determineSource(actor, target), null, null, null, null, null, null, null, null, null, null, null));
    }

    @Override
    @Transactional
    public void addBaseSurrenderedEntry(SimpleBase simpleBase) {
        save(new DbHistoryElement(DbHistoryElement.Type.BASE_SURRENDERED,
                userService.getUser(simpleBase),
                null,
                simpleBase,
                null,
                null,
                null,
                null,
                planetSystemService,
                getSessionId(simpleBase),
                determineSource(simpleBase, null), null, null, null, null, null, null, null, null, null, null, null));
    }

    @Override
    @Transactional
    public void addItemCreatedEntry(SyncBaseItem syncBaseItem) {
        save(new DbHistoryElement(DbHistoryElement.Type.ITEM_CREATED,
                userService.getUser(syncBaseItem.getBase()),
                null,
                syncBaseItem.getBase(),
                null,
                syncBaseItem,
                null,
                null,
                planetSystemService,
                getSessionId(syncBaseItem.getBase()),
                determineSource(syncBaseItem.getBase(), null), null, null, null, null, null, null, null, null, null, null, null));
    }

    @Override
    @Transactional
    public void addItemDestroyedEntry(SimpleBase actor, SyncBaseItem target) {
        save(new DbHistoryElement(DbHistoryElement.Type.ITEM_DESTROYED,
                actor != null ? userService.getUser(actor) : null,
                userService.getUser(target.getBase()),
                actor,
                target.getBase(),
                target,
                null,
                null,
                planetSystemService,
                getSessionId(actor),
                determineSource(actor, target.getBase()), null, null, null, null, null, null, null, null, null, null, null));
    }

    @Override
    @Transactional
    public void addLevelPromotionEntry(UserState userState, DbLevel level) {
        save(new DbHistoryElement(DbHistoryElement.Type.LEVEL_PROMOTION,
                userService.getUser(userState.getUser()),
                null,
                null,
                null,              // TODO
                null,
                level,
                null,
                planetSystemService,
                userState.getSessionId(),
                DbHistoryElement.Source.HUMAN, null, null, null, null, null, null, null, null, null, null, null));
    }

    @Override
    @Transactional
    public void addLevelTaskCompletedEntry(UserState userState, DbLevelTask levelTask) {
        save(new DbHistoryElement(DbHistoryElement.Type.LEVEL_TASK_COMPLETED,
                userService.getUser(userState.getUser()),
                null,
                null,
                null,              // TODO
                null,
                null,
                levelTask,
                planetSystemService,
                userState.getSessionId(),
                DbHistoryElement.Source.HUMAN, null, null, null, null, null, null, null, null, null, null, null));
    }

    @Override
    public void addLevelTaskActivated(UserState userState, DbLevelTask dbLevelTask) {
        save(new DbHistoryElement(DbHistoryElement.Type.LEVEL_TASK_ACTIVATED,
                userService.getUser(userState.getUser()),
                null,
                null,
                null,              // TODO
                null,
                null,
                dbLevelTask,
                planetSystemService,
                userState.getSessionId(),
                DbHistoryElement.Source.HUMAN, null, null, null, null, null, null, null, null, null, null, null));
    }

    @Override
    public void addLevelTaskDeactivated(UserState userState, DbLevelTask dbLevelTask) {
        save(new DbHistoryElement(DbHistoryElement.Type.LEVEL_TASK_DEACTIVATED,
                userService.getUser(userState.getUser()),
                null,
                null,
                null,              // TODO
                null,
                null,
                dbLevelTask,
                planetSystemService,
                userState.getSessionId(),
                DbHistoryElement.Source.HUMAN, null, null, null, null, null, null, null, null, null, null, null));
    }

    @Override
    public void addAllianceOffered(User actor, User target) {
        save(new DbHistoryElement(DbHistoryElement.Type.ALLIANCE_OFFERED,
                actor,
                target,
                null,
                null,              // TODO
                null,
                null,
                null,
                null,
                userService.getUserState().getSessionId(),
                DbHistoryElement.Source.HUMAN, null, null, null, null, null, null, null, null, null, null, null));
    }

    @Override
    public void addAllianceOfferAccepted(User actor, User target) {
        save(new DbHistoryElement(DbHistoryElement.Type.ALLIANCE_OFFER_ACCEPTED,
                actor,
                target,
                null,
                null,              // TODO
                null,
                null,
                null,
                null,
                userService.getUserState().getSessionId(),
                DbHistoryElement.Source.HUMAN, null, null, null, null, null, null, null, null, null, null, null));
    }

    @Override
    public void addAllianceOfferRejected(User actor, User target) {
        save(new DbHistoryElement(DbHistoryElement.Type.ALLIANCE_OFFER_REJECTED,
                actor,
                target,
                null,
                null,              // TODO
                null,
                null,
                null,
                null,
                userService.getUserState().getSessionId(),
                DbHistoryElement.Source.HUMAN, null, null, null, null, null, null, null, null, null, null, null));
    }

    @Override
    public void addAllianceBroken(User actor, User target) {
        save(new DbHistoryElement(DbHistoryElement.Type.ALLIANCE_BROKEN,
                actor,
                target,
                null,
                null,              // TODO
                null,
                null,
                null,
                null,
                userService.getUserState().getSessionId(),
                DbHistoryElement.Source.HUMAN, null, null, null, null, null, null, null, null, null, null, null));
    }

    @Override
    public void addBoxExpired(SyncBoxItem boxItem) {
        if (HibernateUtil.hasOpenSession(sessionFactory)) {
            save(new DbHistoryElement(DbHistoryElement.Type.BOX_EXPIRED,
                    null,
                    null,
                    null,
                    null,
                    boxItem,
                    null,
                    null,
                    null,
                    null,
                    DbHistoryElement.Source.BOT,
                    boxItem.getSyncItemArea().getPosition(), null, null, null, null, null, null, null, null, null, null));
        } else {
            HibernateUtil.openSession4InternalCall(sessionFactory);
            try {
                save(new DbHistoryElement(DbHistoryElement.Type.BOX_EXPIRED,
                        null,
                        null,
                        null,
                        null,
                        boxItem,
                        null,
                        null,
                        null,
                        null,
                        DbHistoryElement.Source.BOT,
                        boxItem.getSyncItemArea().getPosition(), null, null, null, null, null, null, null, null, null, null));
            } finally {
                HibernateUtil.closeSession4InternalCall(sessionFactory);
            }
        }
    }

    @Override
    public void addBoxDropped(SyncBoxItem boxItem, Index position, SyncBaseItem dropper) {
        SimpleBase dropperBase = null;
        if (dropper != null) {
            dropperBase = dropper.getBase();
        }
        if (HibernateUtil.hasOpenSession(sessionFactory)) {
            save(new DbHistoryElement(DbHistoryElement.Type.BOX_DROPPED,
                    null,
                    null,
                    dropperBase,
                    null,
                    boxItem,
                    null,
                    null,
                    planetSystemService,
                    null,
                    DbHistoryElement.Source.BOT,
                    position, null, null, null, null, null, null, null, null, null, null));
        } else {
            HibernateUtil.openSession4InternalCall(sessionFactory);
            try {
                save(new DbHistoryElement(DbHistoryElement.Type.BOX_DROPPED,
                        null,
                        null,
                        dropperBase,
                        null,
                        boxItem,
                        null,
                        null,
                        planetSystemService,
                        null,
                        DbHistoryElement.Source.BOT,
                        position, null, null, null, null, null, null, null, null, null, null));
            } finally {
                HibernateUtil.closeSession4InternalCall(sessionFactory);
            }
        }
    }

    @Override
    public void addBoxPicked(SyncBoxItem boxItem, SyncBaseItem picker) {
        save(new DbHistoryElement(DbHistoryElement.Type.BOX_PICKED,
                userService.getUser(picker.getBase()),
                null,
                picker.getBase(),
                null,
                boxItem,
                null,
                null,
                planetSystemService,
                getSessionId(picker.getBase()),
                DbHistoryElement.Source.BOT,
                boxItem.getSyncItemArea().getPosition(), null, null, null, null, null, null, null, null, null, null));
    }

    @Override
    public void addRazarionFromBox(UserState userState, int razarion) {
        save(new DbHistoryElement(DbHistoryElement.Type.RAZARION_FROM_BOX,
                userService.getUser(userState),
                null,
                userState.getBase().getSimpleBase(),
                null,
                null,
                null,
                null,
                planetSystemService,
                null,
                DbHistoryElement.Source.HUMAN,
                null,
                razarion,
                userState.getRazarion(), null, null, null, null, null, null, null, null));
    }

    @Override
    public void addRazarionBought(UserState userState, int razarionBought) {
        save(new DbHistoryElement(DbHistoryElement.Type.RAZARION_BOUGHT,
                userService.getUser(userState),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                razarionBought,
                userState.getRazarion(), null, null, null, null, null, null, null, null));
    }

    @Override
    public void addInventoryItemFromBox(UserState userState, String inventoryItemName) {
        save(new DbHistoryElement(DbHistoryElement.Type.INVENTORY_ITEM_FROM_BOX,
                userService.getUser(userState),
                null,
                userState.getBase().getSimpleBase(),
                null,
                null,
                null,
                null,
                planetSystemService,
                null,
                DbHistoryElement.Source.HUMAN,
                null,
                null,
                userState.getRazarion(),
                inventoryItemName, null, null, null, null, null, null, null));
    }

    @Override
    public void addInventoryArtifactFromBox(UserState userState, String inventoryArtifactName) {
        save(new DbHistoryElement(DbHistoryElement.Type.INVENTORY_ARTIFACT_FROM_BOX,
                userService.getUser(userState),
                null,
                userState.getBase().getSimpleBase(),
                null,
                null,
                null,
                null,
                planetSystemService,
                null,
                DbHistoryElement.Source.HUMAN,
                null,
                null,
                userState.getRazarion(),
                inventoryArtifactName, null, null, null, null, null, null, null));
    }

    @Override
    public void addInventoryItemUsed(UserState userState, String inventoryItemName) {
        save(new DbHistoryElement(DbHistoryElement.Type.INVENTORY_ITEM_USED,
                userService.getUser(userState),
                null,
                userState.getBase().getSimpleBase(),
                null,
                null,
                null,
                null,
                planetSystemService,
                null,
                DbHistoryElement.Source.HUMAN,
                null,
                null,
                null,
                inventoryItemName, null, null, null, null, null, null, null));
    }

    @Override
    public void addInventoryItemBought(UserState userState, String inventoryItemName, int razarion) {
        save(new DbHistoryElement(DbHistoryElement.Type.INVENTORY_ITEM_BOUGHT,
                userService.getUser(userState),
                null,
                userState.getBase() != null ? userState.getBase().getSimpleBase() : null,
                null,
                null,
                null,
                null,
                planetSystemService,
                null,
                DbHistoryElement.Source.HUMAN,
                null,
                razarion,
                userState.getRazarion(),
                inventoryItemName, null, null, null, null, null, null, null));
    }

    @Override
    public void addInventoryArtifactBought(UserState userState, String inventoryArtifactName, int razarion) {
        save(new DbHistoryElement(DbHistoryElement.Type.INVENTORY_ARTIFACT_BOUGHT,
                userService.getUser(userState),
                null,
                userState.getBase() != null ? userState.getBase().getSimpleBase() : null,
                null,
                null,
                null,
                null,
                planetSystemService,
                null,
                DbHistoryElement.Source.HUMAN,
                null,
                razarion,
                userState.getRazarion(),
                inventoryArtifactName, null, null, null, null, null, null, null));
    }

    @Override
    public void addBotEnrageUp(String botName, BotEnragementStateConfig botEnragementState, SimpleBase actor) {
        User user = userService.getUser(actor);
        HibernateUtil.openSession4InternalCall(sessionFactory);
        try {
            save(new DbHistoryElement(DbHistoryElement.Type.BOT_ENRAGE_UP,
                    user,
                    null,
                    actor,
                    null,
                    null,
                    null,
                    null,
                    planetSystemService,
                    null,
                    DbHistoryElement.Source.BOT,
                    null,
                    null,
                    null,
                    null,
                    botName,
                    botEnragementState.getName(), null, null, null, null, null));
        } finally {
            HibernateUtil.closeSession4InternalCall(sessionFactory);
        }
    }

    @Override
    public void addBotEnrageNormal(String botName, BotEnragementStateConfig botEnragementState) {
        HibernateUtil.openSession4InternalCall(sessionFactory);
        try {
            save(new DbHistoryElement(DbHistoryElement.Type.BOT_ENRAGE_NORMAL,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    planetSystemService,
                    null,
                    DbHistoryElement.Source.BOT,
                    null,
                    null,
                    null,
                    null,
                    botName,
                    botEnragementState.getName(), null, null, null, null, null));
        } finally {
            HibernateUtil.closeSession4InternalCall(sessionFactory);
        }
    }

    @Override
    public void addItemUnlocked(UserState userState, BaseItemType baseItemType) {
        save(new DbHistoryElement(DbHistoryElement.Type.UNLOCKED_ITEM,
                userService.getUser(userState),
                null,
                userState.getBase() != null ? userState.getBase().getSimpleBase() : null,
                null,
                null,
                null,
                null,
                planetSystemService,
                null,
                DbHistoryElement.Source.HUMAN,
                null,
                baseItemType.getUnlockRazarion(),
                userState.getRazarion(),
                null,
                null,
                null,
                baseItemType, null, null, null, null));
    }

    @Override
    public void addQuestUnlocked(UserState userState, DbLevelTask dbLevelTask) {
        save(new DbHistoryElement(DbHistoryElement.Type.UNLOCKED_QUEST,
                userService.getUser(userState),
                null,
                userState.getBase() != null ? userState.getBase().getSimpleBase() : null,
                null,
                null,
                null,
                dbLevelTask,
                planetSystemService,
                null,
                DbHistoryElement.Source.HUMAN,
                null,
                dbLevelTask.getUnlockRazarion(),
                userState.getRazarion(),
                null,
                null,
                null,
                null, null, null, null, null));
    }

    @Override
    public void addPlanetUnlocked(UserState userState, PlanetLiteInfo planetLiteInfo) {
        save(new DbHistoryElement(DbHistoryElement.Type.UNLOCKED_PLANET,
                userService.getUser(userState),
                null,
                userState.getBase() != null ? userState.getBase().getSimpleBase() : null,
                null,
                null,
                null,
                null,
                planetSystemService,
                null,
                DbHistoryElement.Source.HUMAN,
                null,
                planetLiteInfo.getUnlockRazarion(),
                userState.getRazarion(),
                null,
                null,
                null,
                null,
                planetLiteInfo, null, null, null));
    }

    @Override
    public void addGuildCreated(User user, int razarionCost, DbGuild dbGuild) {
        save(new DbHistoryElement(DbHistoryElement.Type.GUILD_CREATED,
                user,
                null,
                null,
                null,
                null,
                null,
                null,
                planetSystemService,
                userService.getUserState(user).getSessionId(),
                DbHistoryElement.Source.HUMAN,
                null,
                razarionCost,
                userService.getUserState(user).getRazarion(),
                null,
                null,
                null,
                null,
                null,
                dbGuild, null, null));
    }

    @Override
    public void addGuildInvitation(User invitingUser, User invitee, DbGuild hostGuild) {
        save(new DbHistoryElement(DbHistoryElement.Type.GUILD_USER_INVITED,
                invitingUser,
                invitee,
                null,
                null,
                null,
                null,
                null,
                planetSystemService,
                userService.getUserState(invitingUser).getSessionId(),
                DbHistoryElement.Source.HUMAN,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                hostGuild, null, null));
    }

    @Override
    public void addGuildJoined(User user, DbGuild dbGuild) {
        save(new DbHistoryElement(DbHistoryElement.Type.GUILD_JOINED,
                user,
                null,
                null,
                null,
                null,
                null,
                null,
                planetSystemService,
                userService.getUserState(user).getSessionId(),
                DbHistoryElement.Source.HUMAN,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                dbGuild, null, null));
    }

    @Override
    public void addGuildDismiss(User user, DbGuild dbGuild) {
        save(new DbHistoryElement(DbHistoryElement.Type.GUILD_DISMISSED,
                user,
                null,
                null,
                null,
                null,
                null,
                null,
                planetSystemService,
                userService.getUserState(user).getSessionId(),
                DbHistoryElement.Source.HUMAN,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                dbGuild, null, null));
    }

    @Override
    public void addGuildMembershipRequest(User user, DbGuild dbGuild) {
        save(new DbHistoryElement(DbHistoryElement.Type.GUILD_MEMBERSHIP_REQUEST,
                user,
                null,
                null,
                null,
                null,
                null,
                null,
                planetSystemService,
                userService.getUserState(user).getSessionId(),
                DbHistoryElement.Source.HUMAN,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                dbGuild, null, null));
    }

    @Override
    public void addDismissGuildMemberRequest(User user, User dismissUser, DbGuild dbGuild) {
        save(new DbHistoryElement(DbHistoryElement.Type.GUILD_MEMBERSHIP_REQUEST_DISMISSED,
                user,
                dismissUser,
                null,
                null,
                null,
                null,
                null,
                planetSystemService,
                userService.getUserState(user).getSessionId(),
                DbHistoryElement.Source.HUMAN,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                dbGuild, null, null));
    }

    @Override
    public void addGuildMemberKicked(User user, User userToKick, DbGuild dbGuild) {
        save(new DbHistoryElement(DbHistoryElement.Type.GUILD_MEMBER_KICKED,
                user,
                userToKick,
                null,
                null,
                null,
                null,
                null,
                planetSystemService,
                userService.getUserState(user).getSessionId(),
                DbHistoryElement.Source.HUMAN,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                dbGuild, null, null));
    }

    @Override
    public void addChangeGuildMemberRank(User user, User userToChange, GuildMemberInfo.Rank rank, DbGuild dbGuild) {
        save(new DbHistoryElement(DbHistoryElement.Type.GUILD_MEMBER_CHANGED,
                user,
                userToChange,
                null,
                null,
                null,
                null,
                null,
                planetSystemService,
                userService.getUserState(user).getSessionId(),
                DbHistoryElement.Source.HUMAN,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                dbGuild,
                rank, null));
    }

    @Override
    public void addGuildTextChanged(User user, String text, DbGuild dbGuild) {
        save(new DbHistoryElement(DbHistoryElement.Type.GUILD_TEXT_CHANGED,
                user,
                null,
                null,
                null,
                null,
                null,
                null,
                planetSystemService,
                userService.getUserState(user).getSessionId(),
                DbHistoryElement.Source.HUMAN,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                dbGuild,
                null,
                text));
    }

    @Override
    public void addGuildLeft(User user, DbGuild dbGuild) {
        save(new DbHistoryElement(DbHistoryElement.Type.GUILD_LEFT,
                user,
                null,
                null,
                null,
                null,
                null,
                null,
                planetSystemService,
                userService.getUserState(user).getSessionId(),
                DbHistoryElement.Source.HUMAN,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                dbGuild,
                null,
                null));
    }

    @Override
    public void addGuildClosed(User user, DbGuild dbGuild) {
        save(new DbHistoryElement(DbHistoryElement.Type.GUILD_CLOSED,
                user,
                null,
                null,
                null,
                null,
                null,
                null,
                planetSystemService,
                userService.getUserState(user).getSessionId(),
                DbHistoryElement.Source.HUMAN,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                dbGuild,
                null,
                null));
    }

    @Override
    public void addKickedGuildClosed(User actorUser, User targetUser, DbGuild dbGuild) {
        save(new DbHistoryElement(DbHistoryElement.Type.GUILD_CLOSED_MEMBER_KICKED,
                actorUser,
                targetUser,
                null,
                null,
                null,
                null,
                null,
                planetSystemService,
                userService.getUserState(actorUser).getSessionId(),
                DbHistoryElement.Source.HUMAN,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                dbGuild,
                null,
                null));
    }

    private String getSessionId(SimpleBase simpleBase) {
        if (simpleBase == null) {
            return null;
        }
        UserState userState = planetSystemService.getServerPlanetServices(simpleBase).getBaseService().getUserState(simpleBase);
        if (userState != null && userState.getSessionId() != null) {
            return userState.getSessionId();
        }
        try {
            return session.getSessionId();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DisplayHistoryElement> getNewestHistoryElements(final User user, int start, final int count) {
        ArrayList<DisplayHistoryElement> displayHistoryElements = new ArrayList<>();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbHistoryElement.class);
        criteria.setMaxResults(count);
        criteria.setFirstResult(start);
        criteria.add(Restrictions.or(Restrictions.eq("actorUserId", user.getId()), Restrictions.eq("targetUserId", user.getId())));
        criteria.addOrder(Property.forName("timeStampMs").desc());
        criteria.addOrder(Property.forName("id").desc()); // If Timestamp is equals, assume id is in ascending form
        for (DbHistoryElement dbHistoryElement : (Collection<DbHistoryElement>) criteria.list()) {
            displayHistoryElements.add(convert(user, null, dbHistoryElement));
        }
        return displayHistoryElements;
    }

    private int getHistoryElementCount(User user) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbHistoryElement.class);
        criteria.add(Restrictions.or(Restrictions.eq("actorUserId", user.getId()), Restrictions.eq("targetUserId", user.getId())));
        criteria.setProjection(Projections.rowCount());
        return ((Number) criteria.list().get(0)).intValue();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DisplayHistoryElement> getHistoryElements(GameHistoryFrame gameHistoryFrame, GameHistoryFilter gameHistoryFilter) {
        ArrayList<DisplayHistoryElement> displayHistoryElements = new ArrayList<>();
        if (!gameHistoryFilter.hasTypes()) {
            return displayHistoryElements;
        }
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbHistoryElement.class);

        if (gameHistoryFrame.getBaseId() != null) {
            criteria.add(Restrictions.or(Restrictions.eq("sessionId", gameHistoryFrame.getSessionId()), Restrictions.or(Restrictions.eq("actorBaseId", gameHistoryFrame.getBaseId()), Restrictions.eq("targetBaseId", gameHistoryFrame.getBaseId()))));
        } else {
            criteria.add(Restrictions.eq("sessionId", gameHistoryFrame.getSessionId()));
        }

        if (gameHistoryFrame.hasStartTime()) {
            criteria.add(Restrictions.ge("timeStampMs", gameHistoryFrame.getStartTime()));
        }
        if (gameHistoryFrame.hasEndTimeExclusive()) {
            criteria.add(Restrictions.lt("timeStampMs", gameHistoryFrame.getEndTimeExclusive()));
        }
        criteria.add(Restrictions.in("type", gameHistoryFilter.getTypes()));
        criteria.addOrder(Property.forName("timeStampMs").desc());

        criteria.addOrder(Property.forName("id").desc()); // If Timestamp is equals, assume id is in ascending form
        for (DbHistoryElement dbHistoryElement : (Collection<DbHistoryElement>) criteria.list()) {
            displayHistoryElements.add(convert(null, gameHistoryFrame.getBaseId(), dbHistoryElement));
        }
        return displayHistoryElements;
    }

    @Override
    public int getLevelPromotionCount(final String sessionId) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbHistoryElement.class);
        criteria.add(Restrictions.eq("type", DbHistoryElement.Type.LEVEL_PROMOTION));
        criteria.add(Restrictions.eq("sessionId", sessionId));
        criteria.setProjection(Projections.rowCount());
        return ((Number) criteria.list().get(0)).intValue();
    }

    private DisplayHistoryElement convert(User user, Integer baseId, DbHistoryElement dbHistoryElement) {
        DisplayHistoryElement displayHistoryElement = new DisplayHistoryElement(dbHistoryElement.getTimeStampMs(), dbHistoryElement.getId());
        Integer userId = null;
        String userName = null;
        if (user != null) {
            userId = user.getId();
            userName = user.getUsername();
        }
        switch (dbHistoryElement.getType()) {
            case BASE_STARTED:
                displayHistoryElement.setMessage("Base created: " + dbHistoryElement.getActorBaseName());
                break;
            case BASE_DEFEATED:
                if (userId != null) {
                    if (userId.equals(dbHistoryElement.getActorUserId())) {
                        displayHistoryElement.setMessage("Base destroyed: " + dbHistoryElement.getTargetBaseName());
                    } else if (userName.equals(dbHistoryElement.getTargetBaseName())) {
                        if (dbHistoryElement.getActorBaseName() != null) {
                            displayHistoryElement.setMessage("Your base has been destroyed by " + dbHistoryElement.getActorBaseName());
                        } else {
                            displayHistoryElement.setMessage("Your base has been destroyed");
                        }
                    } else {
                        displayHistoryElement.setMessage("Internal error 1");
                        log.error("Unknown state 1: " + userId + " " + dbHistoryElement.getActorUserId() + " " + dbHistoryElement.getTargetBaseName());
                    }
                } else if (baseId != null) {
                    if (baseId.equals(dbHistoryElement.getActorBaseId())) {
                        displayHistoryElement.setMessage("Base destroyed: " + dbHistoryElement.getTargetBaseName());
                    } else if (baseId.equals(dbHistoryElement.getTargetBaseId())) {
                        displayHistoryElement.setMessage("Base was destroyed by: " + dbHistoryElement.getActorBaseName());
                    } else {
                        displayHistoryElement.setMessage("Internal error 2");
                        log.error("Unknown state 2: " + baseId + " " + dbHistoryElement.getActorBaseId() + " " + dbHistoryElement.getTargetBaseId());
                    }
                } else {
                    displayHistoryElement.setMessage("Internal error 5/1");
                    log.warn("HistoryServiceImpl.convert() " + dbHistoryElement + " user and baseId are null (1)");
                }
                break;
            case BASE_SURRENDERED:
                displayHistoryElement.setMessage("Base surrendered");
                break;
            case ITEM_CREATED:
                displayHistoryElement.setMessage("Item created: " + dbHistoryElement.getItemTypeName());
                break;
            case ITEM_DESTROYED:
                if (userId != null) {
                    if (userId.equals(dbHistoryElement.getActorUserId())) {
                        displayHistoryElement.setMessage("Destroyed a " + dbHistoryElement.getItemTypeName() + " from " + dbHistoryElement.getTargetBaseName());
                    } else if (userName.equals(dbHistoryElement.getTargetBaseName())) {
                        if (dbHistoryElement.getActorBaseName() != null) {
                            displayHistoryElement.setMessage(dbHistoryElement.getActorBaseName() + " destroyed your " + dbHistoryElement.getItemTypeName());
                        } else {
                            displayHistoryElement.setMessage(dbHistoryElement.getItemTypeName() + " has been sold");
                        }
                    } else {
                        displayHistoryElement.setMessage("Internal error 3");
                        log.error("Unknown state 3: " + userId + " " + dbHistoryElement.getActorUserId() + " " + dbHistoryElement.getTargetBaseName());
                    }
                } else if (baseId != null) {
                    if (baseId.equals(dbHistoryElement.getActorBaseId())) {
                        displayHistoryElement.setMessage("Destroyed a " + dbHistoryElement.getItemTypeName() + " from " + dbHistoryElement.getTargetBaseName());
                    } else if (baseId.equals(dbHistoryElement.getTargetBaseId())) {
                        if (dbHistoryElement.getActorBaseName() != null) {
                            displayHistoryElement.setMessage(dbHistoryElement.getActorBaseName() + " destroyed a " + dbHistoryElement.getItemTypeName());
                        } else {
                            displayHistoryElement.setMessage(dbHistoryElement.getItemTypeName() + " has been sold");
                        }
                    } else {
                        displayHistoryElement.setMessage("Internal error 4");
                        log.error("Unknown state 4: " + baseId + " " + dbHistoryElement.getActorBaseId() + " " + dbHistoryElement.getTargetBaseId());
                    }
                } else {
                    displayHistoryElement.setMessage("Internal error 5/2");
                    log.warn("HistoryServiceImpl.convert() " + dbHistoryElement + " user and baseId are null (2)");
                }
                break;
            case LEVEL_PROMOTION:
                displayHistoryElement.setMessage("Level reached: " + dbHistoryElement.getLevelName());
                break;
            case LEVEL_TASK_COMPLETED:
                displayHistoryElement.setMessage("Level Task competed: " + dbHistoryElement.getLevelTaskName());
                break;
            case LEVEL_TASK_ACTIVATED:
                displayHistoryElement.setMessage("Level Task activated: " + dbHistoryElement.getLevelTaskName());
                break;
            case LEVEL_TASK_DEACTIVATED:
                displayHistoryElement.setMessage("Level Task deactivated: " + dbHistoryElement.getLevelTaskName());
                break;
            case ALLIANCE_OFFERED:
                if (userId != null) {
                    if (userId.equals(dbHistoryElement.getActorUserId())) {
                        displayHistoryElement.setMessage("You offered " + userService.getUser(dbHistoryElement.getTargetUserId()).getUsername() + " an alliance");
                    } else if (userId.equals(dbHistoryElement.getTargetUserId())) {
                        displayHistoryElement.setMessage(userService.getUser(dbHistoryElement.getActorUserId()).getUsername() + " offered you an alliance");
                    } else {
                        displayHistoryElement.setMessage("Internal error 6");
                        log.error("Unknown state 6: " + userId + " " + dbHistoryElement.getActorUserId() + " " + dbHistoryElement.getTargetUserId());
                    }
                } else {
                    displayHistoryElement.setMessage("Alliance offered. From " + userService.getUser(dbHistoryElement.getActorUserId()).getUsername() + " to " + userService.getUser(dbHistoryElement.getTargetUserId()).getUsername());
                }
                break;
            case ALLIANCE_OFFER_ACCEPTED:
                if (userId != null) {
                    if (userId.equals(dbHistoryElement.getActorUserId())) {
                        displayHistoryElement.setMessage("You accepted an alliance with " + userService.getUser(dbHistoryElement.getTargetUserId()).getUsername());
                    } else if (userId.equals(dbHistoryElement.getTargetUserId())) {
                        displayHistoryElement.setMessage("Your alliance offer has been accepted by " + userService.getUser(dbHistoryElement.getActorUserId()).getUsername());
                    } else {
                        displayHistoryElement.setMessage("Internal error 7");
                        log.error("Unknown state 7: " + userId + " " + dbHistoryElement.getActorUserId() + " " + dbHistoryElement.getTargetUserId());
                    }
                } else {
                    displayHistoryElement.setMessage("Alliance offer accepted by " + userService.getUser(dbHistoryElement.getTargetUserId()).getUsername() + " offered by " + userService.getUser(dbHistoryElement.getActorUserId()).getUsername());
                }

                break;
            case ALLIANCE_OFFER_REJECTED:
                if (userId != null) {
                    if (userId.equals(dbHistoryElement.getActorUserId())) {
                        displayHistoryElement.setMessage("You rejected an alliance with " + userService.getUser(dbHistoryElement.getTargetUserId()).getUsername());
                    } else if (userId.equals(dbHistoryElement.getTargetUserId())) {
                        displayHistoryElement.setMessage("Your alliance offer has been rejected by " + userService.getUser(dbHistoryElement.getActorUserId()).getUsername());
                    } else {
                        displayHistoryElement.setMessage("Internal error 8");
                        log.error("Unknown state 8: " + userId + " " + dbHistoryElement.getActorUserId() + " " + dbHistoryElement.getTargetUserId());
                    }
                } else {
                    displayHistoryElement.setMessage("Alliance offer rejected by " + userService.getUser(dbHistoryElement.getActorUserId()).getUsername() + " offered by " + userService.getUser(dbHistoryElement.getTargetUserId()).getUsername());
                }
                break;
            case ALLIANCE_BROKEN:
                if (userId != null) {
                    if (userId.equals(dbHistoryElement.getActorUserId())) {
                        displayHistoryElement.setMessage("You broke the alliance with " + userService.getUser(dbHistoryElement.getTargetUserId()).getUsername());
                    } else if (userId.equals(dbHistoryElement.getTargetUserId())) {
                        displayHistoryElement.setMessage("Your alliance has been broken by " + userService.getUser(dbHistoryElement.getActorUserId()).getUsername());
                    } else {
                        displayHistoryElement.setMessage("Internal error 9");
                        log.error("Unknown state 9: " + userId + " " + userService.getUser(dbHistoryElement.getActorUserId()).getUsername() + " " + userService.getUser(dbHistoryElement.getTargetUserId()).getUsername());
                    }
                } else {
                    displayHistoryElement.setMessage("Alliance broken by " + dbHistoryElement.getActorUserId() + " ex partner " + dbHistoryElement.getTargetUserId());
                }
                break;
            case INVENTORY_ITEM_USED:
                displayHistoryElement.setMessage("Inventory used " + dbHistoryElement.getInventory());
                break;
            case INVENTORY_ARTIFACT_FROM_BOX:
                displayHistoryElement.setMessage("Found inventory artifact " + dbHistoryElement.getInventory());
                break;
            case INVENTORY_ITEM_FROM_BOX:
                displayHistoryElement.setMessage("Found inventory item " + dbHistoryElement.getInventory());
                break;
            case RAZARION_FROM_BOX:
                displayHistoryElement.setMessage("Found razarion " + dbHistoryElement.getDeltaRazarion());
                break;
            case BOX_PICKED:
                displayHistoryElement.setMessage("Box picked");
                break;
            case INVENTORY_ITEM_BOUGHT:
                displayHistoryElement.setMessage("Inventory item bought: " + dbHistoryElement.getInventory());
                break;
            case INVENTORY_ARTIFACT_BOUGHT:
                displayHistoryElement.setMessage("Inventory artifact bought: " + dbHistoryElement.getInventory());
                break;
            case BOT_ENRAGE_UP:
                displayHistoryElement.setMessage("You have angered " + dbHistoryElement.getBotName() + ": " + dbHistoryElement.getBotInfo());
                break;
            case RAZARION_BOUGHT:
                displayHistoryElement.setMessage("Bought Razarion " + dbHistoryElement.getDeltaRazarion() + " via PayPal");
                break;
            case UNLOCKED_ITEM:
                displayHistoryElement.setMessage("Item unlocked " + dbHistoryElement.getItemTypeName());
                break;
            case UNLOCKED_QUEST:
                displayHistoryElement.setMessage("Quest unlocked " + dbHistoryElement.getLevelTaskName());
                break;
            case UNLOCKED_PLANET:
                displayHistoryElement.setMessage("Planet unlocked " + dbHistoryElement.getPlanetName());
                break;
            default:
                displayHistoryElement.setMessage("Internal error 10");
                log.warn("HistoryServiceImpl.convert() " + dbHistoryElement + " Unknown type: " + dbHistoryElement.getType());
        }
        return displayHistoryElement;
    }

    private void save(DbHistoryElement dbHistoryElement) {
        try {
            sessionFactory.getCurrentSession().save(dbHistoryElement);
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    public ReadonlyListContentProvider<DisplayHistoryElement> getNewestHistoryElements() {
        User user = userService.getUser();
        if (user != null) {
            return new ReadonlyListContentProvider<>(getNewestHistoryElements(user, 0, NEWEST_HISTORY_ELEMENT_COUNT));
        } else {
            DisplayHistoryElement displayHistoryElement = new DisplayHistoryElement(System.currentTimeMillis(), 0);
            displayHistoryElement.setMessage("History only visible to registered users");
            return new ReadonlyListContentProvider<>(Collections.singletonList(displayHistoryElement));
        }
    }

    @Override
    public HistoryElementInfo getHistoryElements(int start, int length) {
        User user = userService.getUser();
        if (user == null) {
            throw new IllegalStateException("User is not registered");
        }
        int total = getHistoryElementCount(user);
        List<HistoryElement> historyElements = new ArrayList<>();
        if (start < total) {
            int fixedLength = Math.min(length, total - start);
            for (DisplayHistoryElement displayHistoryElement : getNewestHistoryElements(user, start, fixedLength)) {
                historyElements.add(new HistoryElement(new Date(displayHistoryElement.getTimeStamp()), displayHistoryElement.getMessage()));
            }
        }
        return new HistoryElementInfo(historyElements, start, total);
    }
}
