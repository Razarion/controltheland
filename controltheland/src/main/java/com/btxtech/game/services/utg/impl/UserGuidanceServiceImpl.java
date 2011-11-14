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

package com.btxtech.game.services.utg.impl;

import com.btxtech.game.jsre.client.common.Level;
import com.btxtech.game.jsre.client.common.Message;
import com.btxtech.game.jsre.client.control.GameStartupSeq;
import com.btxtech.game.jsre.common.LevelPacket;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.Territory;
import com.btxtech.game.jsre.common.utg.ConditionServiceListener;
import com.btxtech.game.services.base.Base;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.collision.CollisionService;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.connection.ConnectionService;
import com.btxtech.game.services.connection.Session;
import com.btxtech.game.services.history.HistoryService;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.market.ServerMarketService;
import com.btxtech.game.services.statistics.StatisticsService;
import com.btxtech.game.services.territory.TerritoryService;
import com.btxtech.game.services.tutorial.TutorialService;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.DbAbstractLevel;
import com.btxtech.game.services.utg.DbItemTypeLimitation;
import com.btxtech.game.services.utg.DbRealGameLevel;
import com.btxtech.game.services.utg.DbResurrection;
import com.btxtech.game.services.utg.DbSimulationLevel;
import com.btxtech.game.services.utg.LevelActivationException;
import com.btxtech.game.services.utg.ServerConditionService;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.UserTrackingService;
import com.btxtech.game.services.utg.condition.DbAbstractComparisonConfig;
import com.btxtech.game.services.utg.condition.DbConditionConfig;
import com.btxtech.game.services.utg.condition.DbSyncItemTypeComparisonConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: beat
 * Date: 29.01.2010
 * Time: 22:04:02
 */
@Component("userGuidanceService")
public class UserGuidanceServiceImpl implements UserGuidanceService, ConditionServiceListener<UserState> {
    public static final String NO_MISSION_TARGET = "<center>There are no new mission targets.<br><h1>Please check back later</h1></center>";
    @Autowired
    private BaseService baseService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private CollisionService collisionService;
    @Autowired
    private ConnectionService connectionService;
    @Autowired
    private ServerMarketService serverMarketService;
    @Autowired
    private UserTrackingService userTrackingService;
    @Autowired
    private Session session;
    @Autowired
    private UserService userService;
    @Autowired
    private ServerConditionService serverConditionService;
    @Autowired
    private TutorialService tutorialService;
    @Autowired
    private ServerMarketService marketService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private CrudRootServiceHelper<DbAbstractLevel> crudServiceHelperHibernate;
    @Autowired
    private CrudRootServiceHelper<DbResurrection> crudRootDbResurrection;
    @Autowired
    private TerritoryService territoryService;
    @Autowired
    private StatisticsService statisticsService;
    private HibernateTemplate hibernateTemplate;
    private Log log = LogFactory.getLog(UserGuidanceServiceImpl.class);
    private List<DbAbstractLevel> dbAbstractLevels = new ArrayList<DbAbstractLevel>();
    private DbRealGameLevel dummyRealGameLevel;

    @PostConstruct
    public void init() {
        crudRootDbResurrection.init(DbResurrection.class);
        crudServiceHelperHibernate.init(DbAbstractLevel.class, "orderIndex", true, true, null);
        serverConditionService.setConditionServiceListener(this);
    }

    @Override
    public void init2() {
        try {
            activateLevels();
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    @Override
    public void onBaseDeleted(SimpleBase actorBase, SimpleBase targetBase) {
        serverConditionService.onBaseDeleted(actorBase);

        if (connectionService.hasConnection(targetBase)) {
            connectionService.closeConnection(targetBase);
        }
    }

    @Override
    public void executeResurrection(UserState userState) {
        DbRealGameLevel lastRealGameLevel = toHighestPossibleRealGameLevel(userState.getCurrentAbstractLevel());
        DbResurrection dbResurrection = lastRealGameLevel.getDbResurrection();
        if (dbResurrection == null) {
            throw new IllegalStateException("No DbResurrection found on DbRealGameLevel: " + lastRealGameLevel);
        }

        Territory territory = territoryService.getTerritory(dbResurrection.getDbTerritory());

        try {
            baseService.createNewBase(userState, dbResurrection.getStartItemType(), territory, dbResurrection.getStartItemFreeRange());
        } catch (Exception e) {
            log.error("Can not create base for user: " + userState, e);
        }

        Base base = baseService.getBase(userState);
        base.setAccountBalance(dbResurrection.getMoney());
        baseService.sendAccountBaseUpdate(base);

        log.debug("User: " + userState + " will be resurrected: " + dbResurrection);

        // Prepare next level
        activateCondition(userState, userState.getCurrentAbstractLevel());

    }

    @Override
    public void sendResurrectionMessage(SimpleBase simpleBase) {
        Message message = new Message();
        message.setMessage("You lost your base. A new base was created.");
        connectionService.sendPacket(simpleBase, message);
    }

    @Override
    public void promote(UserState userState, int newDbLevelId) {
        DbAbstractLevel dbNextAbstractLevel = null;
        for (DbAbstractLevel dbAbstractLevel : dbAbstractLevels) {
            if (dbAbstractLevel.getId() == newDbLevelId) {
                dbNextAbstractLevel = dbAbstractLevel;
                break;
            }
        }
        if (dbNextAbstractLevel == null) {
            throw new IllegalArgumentException("DBLevel Id is unknown: " + newDbLevelId);
        }

        promote(userState, dbNextAbstractLevel);
    }


    @Override
    public void conditionPassed(UserState userState) {
        DbAbstractLevel dbOldAbstractLevel = userState.getCurrentAbstractLevel();
        DbAbstractLevel dbNextAbstractLevel = getNextDbLevel(dbOldAbstractLevel);
        promote(userState, dbNextAbstractLevel);
    }

    private void promote(UserState userState, DbAbstractLevel dbNextAbstractLevel) {
        // Prepare
        DbAbstractLevel dbOldAbstractLevel = userState.getCurrentAbstractLevel();
        userState.setCurrentAbstractLevel(dbNextAbstractLevel);
        // Tracking
        historyService.addLevelPromotionEntry(userState, dbNextAbstractLevel);
        statisticsService.onLevelPromotion(userState);
        log.debug("User: " + userState + " has been promoted: " + dbOldAbstractLevel + " to " + dbNextAbstractLevel);

        if (dbNextAbstractLevel instanceof DbRealGameLevel) {
            DbRealGameLevel dbRealGameLevel = (DbRealGameLevel) dbNextAbstractLevel;
            if (dbRealGameLevel.isCreateRealBase()) {
                try {
                    Territory territory = territoryService.getTerritory(dbRealGameLevel.getStartTerritory());
                    baseService.createNewBase(userState, dbRealGameLevel.getStartItemType(), territory, dbRealGameLevel.getStartItemFreeRange());
                } catch (Exception e) {
                    log.error("Can not create base for user: " + userState, e);
                }
            }
            Base base = baseService.getBase(userState);
            if (base != null) {
                // Offline and base was killed -> No rewards
                handleRewards(base, dbRealGameLevel);
            }
        }

        // Prepare next level
        activateCondition(userState, dbNextAbstractLevel);

        // Send level update packet
        if (dbOldAbstractLevel instanceof DbRealGameLevel && baseService.getBase(userState) != null) {
            Base base = baseService.getBase(userState);
            LevelPacket levelPacket = new LevelPacket();
            levelPacket.setLevel(dbNextAbstractLevel.getLevel());
            connectionService.sendPacket(base.getSimpleBase(), levelPacket);
        }
    }

    private void handleRewards(Base base, DbRealGameLevel dbRealGameLevel) {
        if (dbRealGameLevel.getDeltaMoney() != 0) {
            base.depositMoney(dbRealGameLevel.getDeltaMoney());
            statisticsService.onMoneyEarned(base.getSimpleBase(), dbRealGameLevel.getDeltaMoney());
            baseService.sendAccountBaseUpdate(base);
        }
        if (dbRealGameLevel.getDeltaXp() != 0) {
            marketService.increaseXp(base, dbRealGameLevel.getDeltaXp());
        }
    }

    @Override
    public void setLevelForNewUser(UserState userState) {
        DbAbstractLevel dbAbstractLevel = dbAbstractLevels.get(0);
        userState.setCurrentAbstractLevel(dbAbstractLevel);
        activateCondition(userState, dbAbstractLevel);
    }

    private void activateCondition(UserState userState, DbAbstractLevel dbAbstractLevel) {
        serverConditionService.activateCondition(dbAbstractLevel.getConditionConfig(), userState);
    }

    @Override
    public GameStartupSeq getColdStartupSeq() {
        DbAbstractLevel dbAbstractLevel = getDbAbstractLevel();
        if (dbAbstractLevel instanceof DbRealGameLevel) {
            return GameStartupSeq.COLD_REAL;
        } else if (dbAbstractLevel instanceof DbSimulationLevel) {
            return GameStartupSeq.COLD_SIMULATED;
        } else {
            throw new IllegalArgumentException("Unknown level  " + dbAbstractLevel);
        }
    }

    private DbAbstractLevel getNextDbLevel(DbAbstractLevel dbAbstractLevel) {
        int index = dbAbstractLevels.indexOf(dbAbstractLevel);
        if (index < 0) {
            throw new IllegalArgumentException("DbLevel not found: " + dbAbstractLevel);
        }
        index++;
        if (index >= dbAbstractLevels.size()) {
            throw new IllegalStateException("No next level for: " + dbAbstractLevel);
        }
        return dbAbstractLevels.get(index);
    }

    private DbRealGameLevel getDummyRealGameLevel() {
        if (dummyRealGameLevel == null) {
            dummyRealGameLevel = new DbRealGameLevel();
            dummyRealGameLevel.setName("Dummy Level");
            dummyRealGameLevel.setHtml("Dummy Level");
            dummyRealGameLevel.setItemTypeLimitation(Collections.<DbItemTypeLimitation>emptySet());
            try {
                dummyRealGameLevel.setLevel(dummyRealGameLevel.createLevel());
            } catch (LevelActivationException e) {
                throw new RuntimeException(e);
            }
        }
        return dummyRealGameLevel;
    }

    @Override
    public DbRealGameLevel getDbLevel() {
        return toHighestPossibleRealGameLevel(userService.getUserState().getCurrentAbstractLevel());
    }

    @Override
    public DbRealGameLevel getDbLevel(SimpleBase simpleBase) {
        if (baseService.isAbandoned(simpleBase)) {
            return getDummyRealGameLevel();
        }
        return toHighestPossibleRealGameLevel(baseService.getUserState(simpleBase).getCurrentAbstractLevel());
    }

    @Override
    public DbAbstractLevel getDbAbstractLevel() {
        return userService.getUserState().getCurrentAbstractLevel();
    }

    @Override
    public DbAbstractLevel getDbAbstractLevelCms() {
        // Prevent creating a UserState -> search engine
        if (userService.hasUserState()) {
            return userService.getUserState().getCurrentAbstractLevel();
        } else {
            return null;
        }
    }


    @Override
    public DbAbstractLevel getDbLevel(String levelName) {
        for (DbAbstractLevel dbAbstractLevel : dbAbstractLevels) {
            if (dbAbstractLevel.getName().equals(levelName)) {
                return dbAbstractLevel;
            }
        }
        throw new IllegalArgumentException("No DbLevel for levelName: " + levelName);
    }

    @Override
    public DbAbstractLevel getDbLevel(int id) {
        for (DbAbstractLevel dbAbstractLevel : dbAbstractLevels) {
            if (dbAbstractLevel.getId() == id) {
                return dbAbstractLevel;
            }
        }
        throw new IllegalArgumentException("No DbLevel for id: " + id);
    }


    private DbRealGameLevel toHighestPossibleRealGameLevel(DbAbstractLevel dbAbstractLevel) {
        if (dbAbstractLevel instanceof DbRealGameLevel) {
            return (DbRealGameLevel) dbAbstractLevel;
        }
        DbRealGameLevel dbRealGameLevel = getHighestPossibleRealGameLevel(dbAbstractLevel);
        if (dbRealGameLevel != null) {
            return dbRealGameLevel;
        }
        return getDummyRealGameLevel();
    }

    private DbRealGameLevel getHighestPossibleRealGameLevel(DbAbstractLevel dbAbstractLevel) {
        int index = dbAbstractLevels.indexOf(dbAbstractLevel);
        if (index == -1) {
            log.error("DbAbstractLevel can not be found: " + dbAbstractLevel);
            return null;
        }
        index--;
        for (int i = index; i >= 0; i--) {
            DbAbstractLevel abstractLevel = dbAbstractLevels.get(i);
            if (abstractLevel instanceof DbRealGameLevel) {
                return (DbRealGameLevel) abstractLevel;
            }
        }
        return null;
    }

    @Override
    public List<DbAbstractLevel> getDbLevels() {
        return dbAbstractLevels;
    }

    @Override
    public String getDbLevelHtml() {
        return getDbAbstractLevel().getHtml();
    }

    @Override
    public CrudRootServiceHelper<DbAbstractLevel> getDbLevelCrudServiceHelper() {
        return crudServiceHelperHibernate;
    }

    @Override
    @Transactional
    public void updateDbConditionConfig(DbConditionConfig dbConditionConfig) {
        hibernateTemplate.saveOrUpdate(dbConditionConfig);
    }

    @Override
    @Transactional
    public void createDbComparisonItemCount(DbSyncItemTypeComparisonConfig dbSyncItemTypeComparisonConfigId) {
        dbSyncItemTypeComparisonConfigId.getCrudDbComparisonItemCount().createDbChild();
        hibernateTemplate.save(dbSyncItemTypeComparisonConfigId);
    }

    @Override
    @Transactional
    public void createDbItemTypeLimitation(DbRealGameLevel dbRealGameLevel) {
        dbRealGameLevel.getDbItemTypeLimitationCrudServiceHelper().createDbChild();
        hibernateTemplate.update(dbRealGameLevel);
    }

    @Override
    public DbAbstractComparisonConfig getDbAbstractComparisonConfig(int dbAbstractComparisonConfigId) {
        return hibernateTemplate.get(DbAbstractComparisonConfig.class, dbAbstractComparisonConfigId);
    }

    @Override
    public DbSyncItemTypeComparisonConfig getDbSyncItemTypeComparisonConfig(int dbSyncItemTypeComparisonConfigId) {
        return hibernateTemplate.get(DbSyncItemTypeComparisonConfig.class, dbSyncItemTypeComparisonConfigId);
    }

    @Override
    public void activateLevels() throws LevelActivationException {
        dbAbstractLevels = (List<DbAbstractLevel>) crudServiceHelperHibernate.readDbChildren();
        for (DbAbstractLevel dbAbstractLevel : dbAbstractLevels) {
            dbAbstractLevel.activate(itemService);
        }
        tutorialService.activate();
    }

    @Override
    public boolean isBaseItemTypeAllowedInLevel(DbBaseItemType itemType) {
        Level level = getDbLevel().getLevel();
        return level.getLimitation4ItemType(itemType.getId()) > 0;
    }

    @Override
    public DbAbstractLevel copyDbAbstractLevel(Serializable copyFromId) {
        return crudServiceHelperHibernate.copyDbChild(copyFromId);
    }

    @Override
    public CrudRootServiceHelper<DbResurrection> getCrudRootDbResurrection() {
        return crudRootDbResurrection;
    }
}
