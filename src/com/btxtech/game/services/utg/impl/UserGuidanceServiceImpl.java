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
import com.btxtech.game.services.utg.DbLevel;
import com.btxtech.game.services.utg.DbScope;
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
    private List<DbLevel> dbLevels = new ArrayList<DbLevel>();
    private CrudServiceHelper<DbLevel> crudServiceHelperHibernate;

    @PostConstruct
    public void init() {
        try {
            crudServiceHelperHibernate = new CrudServiceHelperHibernateImpl<DbLevel>(hibernateTemplate, DbLevel.class) {
                @Override
                protected void addAdditionalReadCriteria(Criteria criteria) {
                    criteria.addOrder(Order.asc("orderIndex"));
                }

                @Override
                protected void initChild(DbLevel dbLevel) {
                    int rowCount = (Integer) hibernateTemplate.execute(new HibernateCallback() {
                        @Override
                        public Object doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                            Criteria criteria = session.createCriteria(DbLevel.class);
                            criteria.setProjection(Projections.rowCount());
                            return criteria.list().get(0);
                        }
                    });
                    dbLevel.setOrderIndex(rowCount);
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
        DbLevel dbOldLevel = userLevelStatus.getCurrentLevel();
        DbLevel dbNextLevel = getNextDbLevel(dbOldLevel);

        // Prepare next level
        userLevelStatus.setCurrentLevel(dbNextLevel);
        DbScope dbScope = dbNextLevel.getDbScope();
        if (dbScope != null && dbScope.isCreateRealBase()) {
            try {
                baseService.createNewBase();
            } catch (Exception e) {
                log.error("Can not create base for user: " + user, e);
            }
        }
        activateCondition(user, dbNextLevel);

        // TODO save user
        // Send level update packet
        if (dbOldLevel.isRealGame()) {
            Base base = baseService.getBase(user);
            LevelPacket levelPacket = new LevelPacket();
            levelPacket.setLevel(dbNextLevel.getLevel());
            connectionService.sendPacket(base.getSimpleBase(), levelPacket);
            // TODO baseService.sendHouseSpacePacket(base);
        }
        // Tracking
        userTrackingService.levelPromotion(user, dbOldLevel);
        log.debug("User: " + user + " has been promeoted: " + dbOldLevel + " to " + dbNextLevel);
    }

    private DbLevel getNextDbLevel(DbLevel dbLevel) {
        int index = dbLevels.indexOf(dbLevel);
        if (index < 0) {
            throw new IllegalArgumentException("DbLevel not found: " + dbLevel);
        }
        index++;
        if (index >= dbLevels.size()) {
            throw new IllegalStateException("No next level for: " + dbLevel);
        }
        return dbLevels.get(index);
    }

    @Override
    public void setLevelForNewUser(User user) {
        DbLevel dbLevel = dbLevels.get(0);
        UserLevelStatus userLevelStatus = new UserLevelStatus();
        userLevelStatus.setCurrentLevel(dbLevel);
        user.setUserLevelStatus(userLevelStatus);
        activateCondition(user, dbLevel);
    }

    private void activateCondition(User user, DbLevel dbLevel) {
        if (dbLevel.isRealGame()) {
            serverConditionService.activateCondition(dbLevel.getDbConditionConfig().createConditionConfig(itemService), user);
        } else {
            serverConditionService.activateCondition(new ConditionConfig(ConditionTrigger.TUTORIAL, null), user);
        }
    }

    @Override
    public GameStartupSeq getColdStartupSeq() {
        if (getDbLevel().isRealGame()) {
            return GameStartupSeq.COLD_REAL;
        } else {
            return GameStartupSeq.COLD_SIMULATED;
        }
    }

    @Override
    public DbScope getDbScope() {
        return getDbLevel().getDbScope();
    }

    @Override
    public DbLevel getDbLevel() {
        return userService.getUser().getUserLevelStatus().getCurrentLevel();
    }

    @Override
    public DbLevel getDbLevel(SimpleBase simpleBase) {
        return baseService.getUser(simpleBase).getUserLevelStatus().getCurrentLevel();
    }

    @Override
    public DbLevel getDbLevel(String levelName) {
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
    public List<DbLevel> getDbLevels() {
        return dbLevels;
    }

    @Override
    @Transactional
    public void saveDbLevels(List<DbLevel> dbLevels) {
        int orderIndex = 0;
        for (DbLevel dbLevel : dbLevels) {
            dbLevel.setOrderIndex(orderIndex++);
        }
        crudServiceHelperHibernate.updateDbChildren(dbLevels);
    }

    @Override
    @Transactional
    public void saveDbLevel(DbLevel dbLevel) {
        crudServiceHelperHibernate.updateDbChild(dbLevel);
    }

    @Override
    @Transactional
    public void createDbLevel() {
        crudServiceHelperHibernate.createDbChild();
    }

    @Override
    @Transactional
    public void deleteDbLevel(DbLevel dbLevel) {
        crudServiceHelperHibernate.deleteDbChild(dbLevel);
    }

    @Override
    public CrudServiceHelper<DbLevel> getDbLevelCrudServiceHelper() {
        return crudServiceHelperHibernate;
    }

    @Override
    public void activateLevels() {
        /***************/
        /*dbLevels = new ArrayList<DbLevel>();
        DbLevel dbLevel1 = new DbLevel();
        dbLevel1.setHtml("Html 1");
        dbLevel1.setName("Name 1");
        dbLevel1.setRealGame(false);
        dbLevel1.setDbTutorialConfig(tutorialService.getDbTutorialCrudServiceHelper().readDbChild(1));
        DbConditionConfig dbConditionConfig1 = new DbConditionConfig();
        dbConditionConfig1.setConditionTrigger(ConditionTrigger.TUTORIAL);
        dbLevel1.setDbConditionConfig(dbConditionConfig1);
        dbLevels.add(dbLevel1);
        ////////////////////////////////////////////
        DbLevel dbLevel2 = new DbLevel();
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
        dbLevels.add(dbLevel2);
        ////////////////////////////////////////////
        DbLevel dbLevel3 = new DbLevel();
        dbLevel3.setHtml("Html 3");
        dbLevel3.setName("Name 3");
        dbLevel3.setRealGame(true);
        dbLevel3.setDbTutorialConfig(tutorialService.getDbTutorialCrudServiceHelper().readDbChild(2));
        DbConditionConfig dbConditionConfig3 = new DbConditionConfig();
        dbConditionConfig3.setConditionTrigger(ConditionTrigger.TUTORIAL);
        dbLevel3.setDbConditionConfig(dbConditionConfig3);
        dbLevels.add(dbLevel3);*/

        /***************/
        dbLevels = (List<DbLevel>) crudServiceHelperHibernate.readDbChildren();
        tutorialService.activate();
    }
}
