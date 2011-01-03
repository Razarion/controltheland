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
import com.btxtech.game.jsre.common.level.config.ConditionConfig;
import com.btxtech.game.jsre.common.level.config.ConditionTrigger;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.PostConstruct;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

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
            };
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
    public void promote(User user) {
        // Prepare
        Base base = baseService.getBase(user);
        UserLevelStatus userLevelStatus = user.getUserLevelStatus();

        // Get next level
        DbLevel dbOldLevel = userLevelStatus.getCurrentLevel();
        DbLevel dbNextLevel = getNextDbLevel(dbOldLevel);

        // Prepare next level
        userLevelStatus.setCurrentLevel(dbNextLevel);
        baseService.sendHouseSpacePacket(base);
        DbScope dbScope = dbNextLevel.getDbScope();
        if (dbScope.isCreateRealBase()) {
            try {
                baseService.createNewBase();
            } catch (Exception e) {
                log.error("Can not create base for user: " + user, e);
            }
        }

        // TODO save user
        // Send level update packet
        if (dbNextLevel.isRealGame()) {
            LevelPacket levelPacket = new LevelPacket();
            levelPacket.setLevel(dbNextLevel.createLevel());
            connectionService.sendPacket(base.getSimpleBase(), levelPacket);
        }
        // Tracking
        userTrackingService.levelPromotion(user, dbOldLevel);
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
        if (dbLevel.isRealGame()) {
            serverConditionService.activateCondition(dbLevel.getDbConditionConfig().createConditionConfig(), user);
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
    public void saveDbLevels(List<DbLevel> dbLevels) {
        crudServiceHelperHibernate.updateDbChildren(dbLevels);
    }

    @Override
    public void activateLevels() {
        dbLevels = (List<DbLevel>) crudServiceHelperHibernate.readDbChildren();
        tutorialService.activate();
    }
}
