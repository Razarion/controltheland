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

import com.btxtech.game.jsre.client.control.GameStartupSeq;
import com.btxtech.game.jsre.common.LevelPacket;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.utg.config.ConditionConfig;
import com.btxtech.game.jsre.common.utg.config.ConditionTrigger;
import com.btxtech.game.services.base.Base;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.collision.CollisionService;
import com.btxtech.game.services.common.CrudServiceHelper;
import com.btxtech.game.services.common.CrudServiceHelperHibernateImpl;
import com.btxtech.game.services.connection.ConnectionService;
import com.btxtech.game.services.connection.Session;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.market.ServerMarketService;
import com.btxtech.game.services.tutorial.TutorialService;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.utg.DbAbstractLevel;
import com.btxtech.game.services.utg.DbRealGameLevel;
import com.btxtech.game.services.utg.DbScope;
import com.btxtech.game.services.utg.DbSimulationLevel;
import com.btxtech.game.services.utg.ServerConditionService;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.UserLevelStatus;
import com.btxtech.game.services.utg.UserTrackingService;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.PostConstruct;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * User: beat
 * Date: 29.01.2010
 * Time: 22:04:02
 */
@Component("userGuidanceService")
public class UserGuidanceServiceImpl implements UserGuidanceService {
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
    private HibernateTemplate hibernateTemplate;
    private Log log = LogFactory.getLog(UserGuidanceServiceImpl.class);
    @Deprecated
    private List<DbAbstractLevel> dbAbstractLevels = new ArrayList<DbAbstractLevel>();
    private CrudServiceHelper<DbAbstractLevel> crudServiceHelperHibernate;

    @PostConstruct
    public void init() {
        try {
            crudServiceHelperHibernate = new CrudServiceHelperHibernateImpl<DbAbstractLevel>(hibernateTemplate, DbAbstractLevel.class) {
                @Override
                protected void addAdditionalReadCriteria(Criteria criteria) {
                    criteria.addOrder(Order.asc("orderIndex"));
                }

                @Override
                protected void initChild(DbAbstractLevel dbAbstractLevel) {
                    int rowCount = (Integer) hibernateTemplate.execute(new HibernateCallback() {
                        @Override
                        public Object doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                            Criteria criteria = session.createCriteria(DbAbstractLevel.class);
                            criteria.setProjection(Projections.rowCount());
                            return criteria.list().get(0);
                        }
                    });
                    dbAbstractLevel.setOrderIndex(rowCount);
                }
            };
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    public void init2() {
        SessionFactoryUtils.initDeferredClose(hibernateTemplate.getSessionFactory());
        try {
            activateLevels();
        } catch (Throwable t) {
            log.error("", t);
        } finally {
            SessionFactoryUtils.processDeferredClose(hibernateTemplate.getSessionFactory());
        }
    }

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    @Override
    public void promote(User user) {
        // Prepare
        UserLevelStatus userLevelStatus = user.getUserLevelStatus();

        // Get next level
        DbAbstractLevel dbOldAbstractLevel = userLevelStatus.getCurrentLevel();
        DbAbstractLevel dbNextAbstractLevel = getNextDbLevel(dbOldAbstractLevel);

        // Prepare next level
        userLevelStatus.setCurrentLevel(dbNextAbstractLevel);
        if(dbNextAbstractLevel instanceof DbRealGameLevel && ((DbRealGameLevel)dbNextAbstractLevel).isCreateRealBase()) {
            try {
                baseService.createNewBase();
            } catch (Exception e) {
                log.error("Can not create base for user: " + user, e);
            }
        }
        activateCondition(user, dbNextAbstractLevel);

        // TODO save user
        // Send level update packet
        if (dbOldAbstractLevel instanceof DbRealGameLevel) {
            Base base = baseService.getBase(user);
            LevelPacket levelPacket = new LevelPacket();
            levelPacket.setLevel(dbNextAbstractLevel.getLevel());
            connectionService.sendPacket(base.getSimpleBase(), levelPacket);
            // TODO baseService.sendHouseSpacePacket(base);
        }
        // Tracking
        userTrackingService.levelPromotion(user, dbOldAbstractLevel);
        log.debug("User: " + user + " has been promeoted: " + dbOldAbstractLevel + " to " + dbNextAbstractLevel);
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

    @Override
    public void setLevelForNewUser(User user) {
        DbAbstractLevel dbAbstractLevel = dbAbstractLevels.get(0);
        UserLevelStatus userLevelStatus = new UserLevelStatus();
        userLevelStatus.setCurrentLevel(dbAbstractLevel);
        user.setUserLevelStatus(userLevelStatus);
        activateCondition(user, dbAbstractLevel);
    }

    private void activateCondition(User user, DbAbstractLevel dbAbstractLevel) {
        if (dbAbstractLevel instanceof DbRealGameLevel) {
            serverConditionService.activateCondition(((DbRealGameLevel) dbAbstractLevel).getDbConditionConfig().createConditionConfig(itemService), user);
        } else if (dbAbstractLevel instanceof DbSimulationLevel) {
            serverConditionService.activateCondition(new ConditionConfig(ConditionTrigger.TUTORIAL, null), user);
        } else {
            throw new IllegalArgumentException("Unknown level  " + dbAbstractLevel);
        }
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

    @Override
    public DbRealGameLevel getDbLevel() {
        return (DbRealGameLevel) getDbAbstractLevel();
    }

    private DbAbstractLevel getDbAbstractLevel() {
        return userService.getUser().getUserLevelStatus().getCurrentLevel();
    }

    @Override
    public DbRealGameLevel getDbLevel(SimpleBase simpleBase) {
        return (DbRealGameLevel) baseService.getUser(simpleBase).getUserLevelStatus().getCurrentLevel();
    }

    @Override
    public DbAbstractLevel getDbLevel(String levelName) {
        // TODO
        return null;
    }

    @Override
    public String getDbLevelHtml() {
        return getDbLevel().getHtml();
    }

    @Override
    public void restore(Collection<Base> bases) {
        // TODO
    }

    @Override
    public List<DbAbstractLevel> getDbLevels() {
        return dbAbstractLevels;
    }

    @Override
    @Transactional
    public void saveDbLevels(List<DbAbstractLevel> dbAbstractLevels) {
        int orderIndex = 0;
        for (DbAbstractLevel dbAbstractLevel : dbAbstractLevels) {
            dbAbstractLevel.setOrderIndex(orderIndex++);
        }
        crudServiceHelperHibernate.updateDbChildren(dbAbstractLevels);
    }

    @Override
    @Transactional
    public void saveDbLevel(DbAbstractLevel dbAbstractLevel) {
        crudServiceHelperHibernate.updateDbChild(dbAbstractLevel);
    }

    @Override
    @Transactional
    public void createDbLevel() {
        crudServiceHelperHibernate.createDbChild();
    }

    @Override
    @Transactional
    public void deleteDbLevel(DbAbstractLevel dbAbstractLevel) {
        crudServiceHelperHibernate.deleteDbChild(dbAbstractLevel);
    }

    @Override
    public CrudServiceHelper<DbAbstractLevel> getDbLevelCrudServiceHelper() {
        return crudServiceHelperHibernate;
    }

    @Override
    public void activateLevels() {
        /***************/
        /*dbAbstractLevels = new ArrayList<DbAbstractLevel>();
        DbAbstractLevel dbLevel1 = new DbAbstractLevel();
        dbLevel1.setHtml("Html 1");
        dbLevel1.setName("Name 1");
        dbLevel1.setRealGame(false);
        dbLevel1.setDbTutorialConfig(tutorialService.getDbTutorialCrudServiceHelper().readDbChild(1));
        DbConditionConfig dbConditionConfig1 = new DbConditionConfig();
        dbConditionConfig1.setConditionTrigger(ConditionTrigger.TUTORIAL);
        dbLevel1.setDbConditionConfig(dbConditionConfig1);
        dbAbstractLevels.add(dbLevel1);
        ////////////////////////////////////////////
        DbAbstractLevel dbLevel2 = new DbAbstractLevel();
        dbLevel2.setHtml("Html 2");
        dbLevel2.setName("Name 2");
        dbLevel2.setRealGame(true);
        dbLevel2.setDbTutorialConfig(tutorialService.getDbTutorialCrudServiceHelper().readDbChild(1));
        DbConditionConfig dbConditionConfig2 = new DbConditionConfig();
        dbConditionConfig2.setConditionTrigger(ConditionTrigger.SYNC_ITEM_BUILT);
        DbSyncItemTypeComparisonConfig dbSyncItemTypeComparisonConfig1 = new DbSyncItemTypeComparisonConfig();
        dbSyncItemTypeComparisonConfig1.setDbItemType((DbItemType) hibernateTemplate.get(DbItemType.class, 3));
        dbConditionConfig2.setDbAbstractComparisonConfig(dbSyncItemTypeComparisonConfig1);
        dbLevel2.setDbConditionConfig(dbConditionConfig2);
        DbScope dbScope2 = new DbScope();
        dbLevel2.setDbScope(dbScope2);
        dbScope2.setCreateRealBase(true);
        dbScope2.setStartItemFreeRange(100);
        dbScope2.setStartItemType((DbBaseItemType) hibernateTemplate.get(DbBaseItemType.class, 4));
        dbScope2.setStartRectangle(new Rectangle(0, 0, 4000, 3500));
        dbScope2.setHouseSpace(10);
        dbScope2.setItemSellFactor(0.5);
        dbScope2.setDeltaMoney(10000);
        dbAbstractLevels.add(dbLevel2);
        ////////////////////////////////////////////
        DbAbstractLevel dbLevel3 = new DbAbstractLevel();
        dbLevel3.setHtml("Html 3");
        dbLevel3.setName("Name 3");
        dbLevel3.setRealGame(true);
        dbLevel3.setDbTutorialConfig(tutorialService.getDbTutorialCrudServiceHelper().readDbChild(2));
        DbConditionConfig dbConditionConfig3 = new DbConditionConfig();
        dbConditionConfig3.setConditionTrigger(ConditionTrigger.TUTORIAL);
        dbLevel3.setDbConditionConfig(dbConditionConfig3);
        dbAbstractLevels.add(dbLevel3);*/

        /***************/
        dbAbstractLevels = (List<DbAbstractLevel>) crudServiceHelperHibernate.readDbChildren();
        tutorialService.activate();
    }
}
