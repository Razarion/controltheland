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
import com.btxtech.game.jsre.client.dialogs.history.HistoryFilter;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.PlanetLiteInfo;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotEnragementStateConfig;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBoxItem;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.common.ReadonlyListContentProvider;
import com.btxtech.game.services.history.DbHistoryElement;
import com.btxtech.game.services.history.DisplayHistoryElement;
import com.btxtech.game.services.history.GameHistoryFilter;
import com.btxtech.game.services.history.GameHistoryFrame;
import com.btxtech.game.services.history.HistoryService;
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

import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
    @Autowired
    private EntityManagerFactory entityManagerFactory;

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
    public void addCrystalsFromBox(UserState userState, int crystals) {
        save(new DbHistoryElement(DbHistoryElement.Type.CRYSTALS_FROM_BOX,
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
                crystals,
                userState.getCrystals(), null, null, null, null, null, null, null, null));
    }

    @Override
    public void addCrystalsBought(UserState userState, int crystalsBought) {
        save(new DbHistoryElement(DbHistoryElement.Type.CRYSTALS_BOUGHT,
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
                crystalsBought,
                userState.getCrystals(), null, null, null, null, null, null, null, null));
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
                userState.getCrystals(),
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
                userState.getCrystals(),
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
    public void addInventoryItemBought(UserState userState, String inventoryItemName, int crystals) {
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
                crystals,
                userState.getCrystals(),
                inventoryItemName, null, null, null, null, null, null, null));
    }

    @Override
    public void addInventoryArtifactBought(UserState userState, String inventoryArtifactName, int crystals) {
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
                crystals,
                userState.getCrystals(),
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
                baseItemType.getUnlockCrystals(),
                userState.getCrystals(),
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
                dbLevelTask.getUnlockCrystals(),
                userState.getCrystals(),
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
                planetLiteInfo.getUnlockCrystals(),
                userState.getCrystals(),
                null,
                null,
                null,
                null,
                planetLiteInfo, null, null, null));
    }

    @Override
    public void addGuildCreated(User user, int crystalCost, DbGuild dbGuild) {
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
                crystalCost,
                userService.getUserState(user).getCrystals(),
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
    public void addGuildDismissInvitation(User user, DbGuild dbGuild) {
        save(new DbHistoryElement(DbHistoryElement.Type.GUILD_DISMISSED_INVITATION,
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

    @Override
    public void addFriendInvitationMailSent(User user, String emailAddress) {
        save(new DbHistoryElement(DbHistoryElement.Type.FRIEND_INVITATION_EMAIL_SENT,
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
                null,
                null,
                emailAddress));
    }

    @Override
    public void addFriendInvitationFacebookSent(User user, String fbRequestId) {
        save(new DbHistoryElement(DbHistoryElement.Type.FRIEND_INVITATION_FACEBOOK_SENT,
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
                null,
                null,
                fbRequestId));
    }

    @Override
    public void addFriendInvitationBonus(User host, User invitee, int bonus, int crystals) {
        save(new DbHistoryElement(DbHistoryElement.Type.FRIEND_INVITATION_BONUS,
                host,
                invitee,
                null,
                null,
                null,
                null,
                null,
                planetSystemService,
                null,
                DbHistoryElement.Source.HUMAN,
                null,
                bonus,
                crystals,
                null,
                null,
                null,
                null,
                null,
                null,
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
            displayHistoryElements.add(convert2DisplayHistoryElement(user, null, dbHistoryElement));
        }
        return displayHistoryElements;
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
            displayHistoryElements.add(convert2DisplayHistoryElement(null, gameHistoryFrame.getBaseId(), dbHistoryElement));
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

    private String convert(User user, Integer baseId, DbHistoryElement dbHistoryElement) {
        Integer userId = (user != null ? user.getId() : null);
        switch (dbHistoryElement.getType()) {
            case BASE_STARTED:
                return "Base created: " + dbHistoryElement.getActorBaseName();
            case BASE_DEFEATED:
                if (userId != null) {
                    if (userId.equals(dbHistoryElement.getActorUserId())) {
                        return "You destroyed the base from " + dbHistoryElement.getTargetBaseName();
                    } else {
                        if (dbHistoryElement.getActorBaseName() != null) {
                            return "Your base has been destroyed by " + dbHistoryElement.getActorBaseName();
                        } else {
                            return "Your base has been destroyed";
                        }
                    }
                } else {
                    return dbHistoryElement.getActorBaseName() + " destroyed the base from " + dbHistoryElement.getTargetBaseName();
                }
            case BASE_SURRENDERED:
                return "Base surrendered";
            case ITEM_CREATED:
                return "Item created: " + dbHistoryElement.getItemTypeName();
            case ITEM_DESTROYED:
                if (userId != null) {
                    if (userId.equals(dbHistoryElement.getActorUserId())) {
                        return "Destroyed a " + dbHistoryElement.getItemTypeName() + " from " + dbHistoryElement.getTargetBaseName();
                    } else {
                        if (dbHistoryElement.getActorBaseName() != null) {
                            return dbHistoryElement.getActorBaseName() + " destroyed your " + dbHistoryElement.getItemTypeName();
                        } else {
                            return dbHistoryElement.getItemTypeName() + " has been sold";
                        }
                    }
                } else {
                    if (dbHistoryElement.getActorBaseName() != null) {
                        return dbHistoryElement.getActorBaseName() + " destroyed a " + dbHistoryElement.getItemTypeName() + " from " + dbHistoryElement.getTargetBaseName();
                    } else {
                        return dbHistoryElement.getTargetBaseName() + " sold a " + dbHistoryElement.getItemTypeName();
                    }
                }
            case LEVEL_PROMOTION:
                return "Level reached: " + dbHistoryElement.getLevelName();
            case LEVEL_TASK_COMPLETED:
                return "Level Task competed: " + dbHistoryElement.getLevelTaskName();
            case LEVEL_TASK_ACTIVATED:
                return "Level Task activated: " + dbHistoryElement.getLevelTaskName();
            case LEVEL_TASK_DEACTIVATED:
                return "Level Task deactivated: " + dbHistoryElement.getLevelTaskName();
            case ALLIANCE_OFFERED:
                if (userId != null) {
                    if (userId.equals(dbHistoryElement.getActorUserId())) {
                        return "You offered " + userService.getUser(dbHistoryElement.getTargetUserId()).getUsername() + " an alliance";
                    } else if (userId.equals(dbHistoryElement.getTargetUserId())) {
                        return userService.getUser(dbHistoryElement.getActorUserId()).getUsername() + " offered you an alliance";
                    } else {
                        log.error("Unknown state 6: " + userId + " " + dbHistoryElement.getActorUserId() + " " + dbHistoryElement.getTargetUserId());
                        return "Internal error 6";
                    }
                } else {
                    return "Alliance offered. From " + userService.getUser(dbHistoryElement.getActorUserId()).getUsername() + " to " + userService.getUser(dbHistoryElement.getTargetUserId()).getUsername();
                }
            case ALLIANCE_OFFER_ACCEPTED:
                if (userId != null) {
                    if (userId.equals(dbHistoryElement.getActorUserId())) {
                        return "You accepted an alliance with " + userService.getUser(dbHistoryElement.getTargetUserId()).getUsername();
                    } else if (userId.equals(dbHistoryElement.getTargetUserId())) {
                        return "Your alliance offer has been accepted by " + userService.getUser(dbHistoryElement.getActorUserId()).getUsername();
                    } else {
                        log.error("Unknown state 7: " + userId + " " + dbHistoryElement.getActorUserId() + " " + dbHistoryElement.getTargetUserId());
                        return "Internal error 7";
                    }
                } else {
                    return "Alliance offer accepted by " + userService.getUser(dbHistoryElement.getTargetUserId()).getUsername() + " offered by " + userService.getUser(dbHistoryElement.getActorUserId()).getUsername();
                }
            case ALLIANCE_OFFER_REJECTED:
                if (userId != null) {
                    if (userId.equals(dbHistoryElement.getActorUserId())) {
                        return "You rejected an alliance with " + userService.getUser(dbHistoryElement.getTargetUserId()).getUsername();
                    } else if (userId.equals(dbHistoryElement.getTargetUserId())) {
                        return "Your alliance offer has been rejected by " + userService.getUser(dbHistoryElement.getActorUserId()).getUsername();
                    } else {
                        log.error("Unknown state 8: " + userId + " " + dbHistoryElement.getActorUserId() + " " + dbHistoryElement.getTargetUserId());
                        return "Internal error 8";
                    }
                } else {
                    return "Alliance offer rejected by " + userService.getUser(dbHistoryElement.getActorUserId()).getUsername() + " offered by " + userService.getUser(dbHistoryElement.getTargetUserId()).getUsername();
                }
            case ALLIANCE_BROKEN:
                if (userId != null) {
                    if (userId.equals(dbHistoryElement.getActorUserId())) {
                        return "You broke the alliance with " + userService.getUser(dbHistoryElement.getTargetUserId()).getUsername();
                    } else if (userId.equals(dbHistoryElement.getTargetUserId())) {
                        return "Your alliance has been broken by " + userService.getUser(dbHistoryElement.getActorUserId()).getUsername();
                    } else {
                        log.error("Unknown state 9: " + userId + " " + userService.getUser(dbHistoryElement.getActorUserId()).getUsername() + " " + userService.getUser(dbHistoryElement.getTargetUserId()).getUsername());
                        return "Internal error 9";
                    }
                } else {
                    return "Alliance broken by " + dbHistoryElement.getActorUserId() + " ex partner " + dbHistoryElement.getTargetUserId();
                }
            case INVENTORY_ITEM_USED:
                return "Inventory used " + dbHistoryElement.getInventory();
            case INVENTORY_ARTIFACT_FROM_BOX:
                return "Found inventory artifact " + dbHistoryElement.getInventory();
            case INVENTORY_ITEM_FROM_BOX:
                return "Found inventory item " + dbHistoryElement.getInventory();
            case CRYSTALS_FROM_BOX:
                return "Found " + dbHistoryElement.getDeltaCrystals() + " crystals";
            case BOX_PICKED:
                return "Box picked";
            case INVENTORY_ITEM_BOUGHT:
                return "Inventory item bought: " + dbHistoryElement.getInventory();
            case INVENTORY_ARTIFACT_BOUGHT:
                return "Inventory artifact bought: " + dbHistoryElement.getInventory();
            case BOT_ENRAGE_UP:
                return "You have angered " + dbHistoryElement.getBotName() + ": " + dbHistoryElement.getBotInfo();
            case CRYSTALS_BOUGHT:
                return "Bought " + dbHistoryElement.getDeltaCrystals() + " crystals via PayPal";
            case UNLOCKED_ITEM:
                return "Item unlocked " + dbHistoryElement.getItemTypeName();
            case UNLOCKED_QUEST:
                return "Quest unlocked " + dbHistoryElement.getLevelTaskName();
            case UNLOCKED_PLANET:
                return "Planet unlocked " + dbHistoryElement.getPlanetName();
            case GUILD_CREATED:
                return dbHistoryElement.getActorUserName() + " created " + dbHistoryElement.getGuildName() + " guild";
            case GUILD_USER_INVITED:
                return dbHistoryElement.getActorUserName() + " invited " + dbHistoryElement.getTargetUserName() + " to the " + dbHistoryElement.getGuildName() + " guild";
            case GUILD_JOINED:
                return dbHistoryElement.getActorUserName() + " joined the " + dbHistoryElement.getGuildName() + " guild";
            case GUILD_DISMISSED_INVITATION:
                return dbHistoryElement.getActorUserName() + " dismissed the " + dbHistoryElement.getGuildName() + " guild invitation";
            case GUILD_MEMBERSHIP_REQUEST:
                return dbHistoryElement.getActorUserName() + " asked the " + dbHistoryElement.getGuildName() + " for a membership request";
            case GUILD_MEMBERSHIP_REQUEST_DISMISSED:
                return dbHistoryElement.getActorUserName() + " dismissed " + dbHistoryElement.getTargetUserName() + " membership request to the " + dbHistoryElement.getGuildName() + " guild";
            case GUILD_MEMBER_KICKED:
                return dbHistoryElement.getActorUserName() + " kicked " + dbHistoryElement.getTargetUserName() + " from the " + dbHistoryElement.getGuildName() + " guild";
            case GUILD_MEMBER_CHANGED:
                return dbHistoryElement.getActorUserName() + " changed " + dbHistoryElement.getTargetUserName() + " rank in the " + dbHistoryElement.getGuildName() + " guild";
            case GUILD_TEXT_CHANGED:
                return dbHistoryElement.getActorUserName() + " changed the text from the " + dbHistoryElement.getGuildName() + " guild";
            case GUILD_LEFT:
                return dbHistoryElement.getActorUserName() + " left the " + dbHistoryElement.getGuildName() + " guild";
            case GUILD_CLOSED:
                return dbHistoryElement.getActorUserName() + " closed the " + dbHistoryElement.getGuildName() + " guild";
            case GUILD_CLOSED_MEMBER_KICKED:
                return dbHistoryElement.getActorUserName() + " kicked " + dbHistoryElement.getTargetUserName() + " from the " + dbHistoryElement.getGuildName() + " guild. The guild will be closed";
            case FRIEND_INVITATION_FACEBOOK_SENT:
                return "You sent some friend invitations via Facebook";
            case FRIEND_INVITATION_EMAIL_SENT:
                return "You sent a friend invitation via mail to " + dbHistoryElement.getText();
            case FRIEND_INVITATION_BONUS:
                return "Friend invitation bonus received for " + dbHistoryElement.getTargetUserName() + ". Bonus: " + dbHistoryElement.getDeltaCrystals() + " crystals";
            default:
                log.warn("HistoryServiceImpl.convert() " + dbHistoryElement + " Unknown type: " + dbHistoryElement.getType());
                return "Internal error 999999";
        }
    }

    private DisplayHistoryElement convert2DisplayHistoryElement(User user, Integer baseId, DbHistoryElement dbHistoryElement) {
        DisplayHistoryElement displayHistoryElement = new DisplayHistoryElement(dbHistoryElement.getTimeStampMs(), dbHistoryElement.getId());
        displayHistoryElement.setMessage(convert(user, baseId, dbHistoryElement));
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
    public HistoryElementInfo getHistoryElements(HistoryFilter historyFilter) {
        User user = userService.getUser();
        if (user == null) {
            throw new IllegalStateException("User is not registered");
        }
        if (historyFilter.getType() == HistoryFilter.Type.USER) {
            historyFilter.setUserId(user.getId());
        }

        int total = getTotalDbHistoryElementCount(historyFilter);
        List<HistoryElement> historyElements = new ArrayList<>();
        if (historyFilter.getStart() < total) {
            List<DbHistoryElement> dbHistoryElements = getDbHistoryElement(historyFilter);
            for (DbHistoryElement dbHistoryElement : dbHistoryElements) {
                historyElements.add(new HistoryElement(dbHistoryElement.getTimeStamp(), convert(user, 0, dbHistoryElement)));
            }
        }
        return new HistoryElementInfo(historyElements, historyFilter.getStart(), total);
    }

    private Predicate createPredicate(HistoryFilter historyFilter, CriteriaBuilder criteriaBuilder, Root<DbHistoryElement> from) {
        switch (historyFilter.getType()) {
            case USER: {
                Predicate predicateActor = criteriaBuilder.equal(from.<String>get("actorUserId"), historyFilter.getUserId());
                Predicate predicateTarget = criteriaBuilder.equal(from.<String>get("targetUserId"), historyFilter.getUserId());
                return criteriaBuilder.or(predicateActor, predicateTarget);
            }
            case GUILD: {
                Expression<String> exp = from.get("type");
                Predicate predicateGuildType = exp.in(DbHistoryElement.ALL_GUILD_TYPES);
                Predicate predicateGuildId = criteriaBuilder.equal(from.<String>get("guildId"), historyFilter.getGuildId());
                return criteriaBuilder.and(predicateGuildType, predicateGuildId);
            }
            default:
                throw new IllegalArgumentException("Unknown Type: " + historyFilter.getType());
        }
    }

    private List<DbHistoryElement> getDbHistoryElement(HistoryFilter historyFilter) {
        CriteriaBuilder criteriaBuilder = entityManagerFactory.getCriteriaBuilder();
        CriteriaQuery<DbHistoryElement> dbHistoryElementQuery = criteriaBuilder.createQuery(DbHistoryElement.class);
        Root<DbHistoryElement> from = dbHistoryElementQuery.from(DbHistoryElement.class);
        CriteriaQuery<DbHistoryElement> select = dbHistoryElementQuery.select(from);
        select.where(createPredicate(historyFilter, criteriaBuilder, from));
        dbHistoryElementQuery.orderBy(criteriaBuilder.desc(from.<String>get("timeStampMs")), criteriaBuilder.desc(from.<String>get("id")));
        TypedQuery<DbHistoryElement> typedDbHistoryElementQuery = entityManagerFactory.createEntityManager().createQuery(select);
        typedDbHistoryElementQuery.setMaxResults(historyFilter.getLength());
        typedDbHistoryElementQuery.setFirstResult(historyFilter.getStart());
        return typedDbHistoryElementQuery.getResultList();
    }

    private int getTotalDbHistoryElementCount(HistoryFilter historyFilter) {
        CriteriaBuilder criteriaBuilder = entityManagerFactory.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<DbHistoryElement> from = countQuery.from(DbHistoryElement.class);
        CriteriaQuery<Long> select = countQuery.select(criteriaBuilder.count(from));
        select.where(createPredicate(historyFilter, criteriaBuilder, from));
        TypedQuery<Long> typedDbHistoryElementQuery = entityManagerFactory.createEntityManager().createQuery(select);
        return typedDbHistoryElementQuery.getSingleResult().intValue();
    }

}
