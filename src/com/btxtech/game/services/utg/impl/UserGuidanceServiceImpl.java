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
import com.btxtech.game.jsre.common.LevelPacket;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.services.base.Base;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.collision.CollisionService;
import com.btxtech.game.services.connection.ConnectionService;
import com.btxtech.game.services.connection.Session;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.market.ServerMarketService;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.utg.BaseLevelStatus;
import com.btxtech.game.services.utg.DbItemCount;
import com.btxtech.game.services.utg.DbLevel;
import com.btxtech.game.services.utg.DbUserStage;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.UserTrackingService;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateCallback;
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
    private HibernateTemplate hibernateTemplate;
    final private HashMap<SimpleBase, PendingPromotion> pendingPromotions = new HashMap<SimpleBase, PendingPromotion>();
    private Log log = LogFactory.getLog(UserGuidanceServiceImpl.class);

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DbLevel> getDbLevels() {
        return (List<DbLevel>) hibernateTemplate.executeFind(new HibernateCallback() {
            @Override
            public Object doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(DbLevel.class);
                criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
                criteria.addOrder(Order.desc("rank"));
                return criteria.list();
            }
        });
    }

    private int getHighestDbLevel() {
        List result = (List) hibernateTemplate.execute(new HibernateCallback() {
            @Override
            public Object doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(DbLevel.class);
                criteria.setProjection(Projections.max("rank"));
                return criteria.list();
            }
        });
        if (result.isEmpty() || result.get(0) == null) {
            return 0;
        } else {
            return (Integer) result.get(0);
        }
    }

    @SuppressWarnings("unchecked")
    private DbLevel getLowestDbLevel() {
        List<DbLevel> result = hibernateTemplate.executeFind(new HibernateCallback() {
            @Override
            public Object doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(DbLevel.class);
                criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
                criteria.addOrder(Order.asc("rank"));
                criteria.setFetchSize(1);
                return criteria.list();
            }
        });
        if (result.isEmpty()) {
            throw new IllegalStateException("No levels found");
        } else {
            return result.get(0);
        }
    }

    @SuppressWarnings("unchecked")
    private DbLevel getNextDbLevel(final DbLevel dbLevel) {
        List<DbLevel> result = hibernateTemplate.executeFind(new HibernateCallback() {
            @Override
            public Object doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(DbLevel.class);
                criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
                criteria.add(Restrictions.gt("rank", dbLevel.getRank()));
                criteria.addOrder(Order.asc("rank"));
                criteria.setFetchSize(1);
                return criteria.list();
            }
        });
        if (result.isEmpty()) {
            return null;
        } else {
            return result.get(0);
        }

    }

    @Override
    public void deleteDbLevel(DbLevel dbLevel) {
        for (Base base : baseService.getBases()) {
            BaseLevelStatus baseLevelStatus = base.getBaseLevelStatus();
            if (baseLevelStatus.getCurrentLevel().equals(dbLevel)) {
                throw new IllegalStateException("Can not delete level. It is used in base: " + base);
            }
        }
        hibernateTemplate.delete(dbLevel);
    }

    @Override
    public void addDbLevel() {
        DbLevel dbLevel = new DbLevel();
        dbLevel.setRank(getHighestDbLevel() + 1);
        hibernateTemplate.save(dbLevel);
    }

    @Override
    public void saveDbLevels(List<DbLevel> dbLevels) {
        hibernateTemplate.saveOrUpdateAll(dbLevels);
    }

    @Override
    public void saveDbLevel(DbLevel dbLevel) {
        hibernateTemplate.update(dbLevel);
        // Update all bases
        for (Base base : baseService.getBases()) {
            BaseLevelStatus baseLevelStatus = base.getBaseLevelStatus();
            if (dbLevel.equals(baseLevelStatus.getCurrentLevel())) {
                baseLevelStatus.setCurrentLevel(dbLevel);
            }
        }
    }

    @Override
    public void moveUpDbLevel(DbLevel dbLevel) {
        List<DbLevel> levels = getDbLevels();
        int i = levels.indexOf(dbLevel);
        if (i > 0) {
            final DbLevel level1 = levels.get(i);
            final DbLevel level2 = levels.get(i - 1);
            int tmpRank = level1.getRank();
            level1.setRank(level2.getRank());
            level2.setRank(getHighestDbLevel() + 1); // Avoid unique constraint
            hibernateTemplate.update(level2);
            hibernateTemplate.update(level1);
            level2.setRank(tmpRank);
            hibernateTemplate.update(level2);
        }
    }

    @Override
    public void moveDownDbLevel(DbLevel dbLevel) {
        List<DbLevel> levels = getDbLevels();
        int i = levels.indexOf(dbLevel);
        if (levels.size() > i + 1) {
            DbLevel level1 = levels.get(i);
            DbLevel level2 = levels.get(i + 1);
            int tmpRank = level1.getRank();
            level1.setRank(level2.getRank());
            level2.setRank(getHighestDbLevel() + 1); // Avoid unique constraint
            hibernateTemplate.update(level2);
            hibernateTemplate.update(level1);
            level2.setRank(tmpRank);
            hibernateTemplate.update(level2);
        }
    }

    @Override
    public Level getLevel4Base() {
        return baseService.getBase().getBaseLevelStatus().getCurrentLevel().createLevel();
    }

    @Override
    public String getMissionTarget4NextLevel(Base base) {
        DbLevel dbLevel = getNextDbLevel(base.getBaseLevelStatus().getCurrentLevel());
        if (dbLevel == null) {
            return NO_MISSION_TARGET;
        }
        dbLevel = getUnskippable(dbLevel, base);
        if (dbLevel == null) {
            return NO_MISSION_TARGET;
        }
        return dbLevel.getMissionTarget();
    }

    @Override
    public void setupLevel4NewBase(Base base) {
        DbLevel dbLevel = getLowestDbLevel();
        dbLevel = getUnskippable(dbLevel, base);
        if (dbLevel == null) {
            return;
        }
        BaseLevelStatus baseLevelStatus = new BaseLevelStatus();
        baseLevelStatus.setCurrentLevel(dbLevel);
        base.setBaseLevelStatus(baseLevelStatus);
        prepareForNextPromotion(base, true);
        userTrackingService.levelPromotion(base, null);
    }

    @Override
    public Level getLevelToRunMissionTarget() {
        Base base = baseService.getBase();
        PendingPromotion pendingPromotion = pendingPromotions.get(base.getSimpleBase());
        if (pendingPromotion != null) {
            return getLevel4Base();
        } else {
            return null;
        }
    }

    @Override
    public void onIncreaseXp(Base base, int xp) {
        PendingPromotion pendingPromotion = pendingPromotions.get(base.getSimpleBase());
        if (pendingPromotion == null || pendingPromotion.getDbLevel().getMinXp() == null) {
            return;
        }
        if (xp >= pendingPromotion.getDbLevel().getMinXp()) {
            pendingPromotion.setXpAchieved();
            userTrackingService.levelInterimPromotion(base, pendingPromotion.getDbLevel().getName(), PendingPromotion.INTERIM_PROMOTION_XP);
            checkAndHandlePromotion(pendingPromotion, base);
        }
    }

    @Override
    public void onMoneyIncrease(Base base) {
        PendingPromotion pendingPromotion = pendingPromotions.get(base.getSimpleBase());
        if (pendingPromotion == null || (pendingPromotion.getDbLevel().getMinMoney() == null && pendingPromotion.getDbLevel().getDeltaMoney() == null)) {
            return;
        }

        if (pendingPromotion.getDbLevel().getMinMoney() != null) {
            int minMoney = pendingPromotion.getDbLevel().getMinMoney();
            if (minMoney >= base.getAccountBalance()) {
                pendingPromotion.setMinMoneyAchieved();
                userTrackingService.levelInterimPromotion(base, pendingPromotion.getDbLevel().getName(), PendingPromotion.INTERIM_PROMOTION_MIN_MONEY);
                checkAndHandlePromotion(pendingPromotion, base);
            }
        } else {
            int deltaMoney = pendingPromotion.getDbLevel().getDeltaMoney();
            Integer beginningMoney = base.getBaseLevelStatus().getBeginningMoney();
            if (beginningMoney == null) {
                throw new IllegalArgumentException("beginningMoney == null " + base);
            }
            if (base.getAccountBalance() - beginningMoney > deltaMoney) {
                pendingPromotion.setDeltaMoneyAchieved();
                userTrackingService.levelInterimPromotion(base, pendingPromotion.getDbLevel().getName(), PendingPromotion.INTERIM_PROMOTION_DELTA_MONEY);
                checkAndHandlePromotion(pendingPromotion, base);
            }
        }
    }

    @Override
    public void onSyncBaseItemCreated(SyncBaseItem syncBaseItem) {
        Base base = baseService.getBase(syncBaseItem);
        PendingPromotion pendingPromotion = pendingPromotions.get(base.getSimpleBase());
        if (pendingPromotion == null || pendingPromotion.getDbLevel().getDbItemCounts() == null || pendingPromotion.getDbLevel().getDbItemCounts().isEmpty()) {
            return;
        }
        if (checkForItemsCondition(pendingPromotion.getDbLevel(), base)) {
            pendingPromotion.setItemCountAchieved();
            userTrackingService.levelInterimPromotion(base, pendingPromotion.getDbLevel().getName(), PendingPromotion.INTERIM_PROMOTION_ITEMS);
            checkAndHandlePromotion(pendingPromotion, base);
        }
    }

    @Override
    public void onItemKilled(Base actorBase) {
        PendingPromotion pendingPromotion = pendingPromotions.get(actorBase.getSimpleBase());
        if (pendingPromotion == null || pendingPromotion.getDbLevel().getDeltaKills() == null) {
            return;
        }
        Integer beginningKills = actorBase.getBaseLevelStatus().getBeginningKills();
        if (beginningKills == null) {
            throw new IllegalArgumentException("beginningKills == null " + actorBase);
        }

        if (actorBase.getKills() - beginningKills >= pendingPromotion.getDbLevel().getDeltaKills()) {
            pendingPromotion.setDeltaKillsAchieved();
            userTrackingService.levelInterimPromotion(actorBase, pendingPromotion.getDbLevel().getName(), PendingPromotion.INTERIM_PROMOTION_DELTA_KILLS);
            checkAndHandlePromotion(pendingPromotion, actorBase);
        }
    }

    @Override
    public void onBaseDeleted(Base base) {
        pendingPromotions.remove(base.getSimpleBase());
    }

    private boolean checkForItemsCondition(DbLevel promotionLevel, Base base) {
        Collection<DbItemCount> dbItemCounts = promotionLevel.getDbItemCounts();
        if (dbItemCounts == null || dbItemCounts.isEmpty()) {
            return true;
        }

        for (DbItemCount dbItemCount : dbItemCounts) {
            int count = base.getItemCount(dbItemCount.getBaseItemType().createItemType());
            if (count < dbItemCount.getCount()) {
                return false;
            }
        }
        return true;
    }

    private void checkAndHandlePromotion(PendingPromotion pendingPromotion, Base base) {
        if (!pendingPromotion.achieved()) {
            return;
        }
        BaseLevelStatus baseLevelStatus = base.getBaseLevelStatus();
        DbLevel oldLevel = baseLevelStatus.getCurrentLevel();
        DbLevel achievedLevel = pendingPromotion.getDbLevel();
        baseLevelStatus.setCurrentLevel(achievedLevel);
        LevelPacket levelPacket = new LevelPacket();
        levelPacket.setLevel(achievedLevel.createLevel());
        connectionService.sendPacket(base.getSimpleBase(), levelPacket);
        baseService.sendHouseSpacePacket(base);
        userTrackingService.levelPromotion(base, oldLevel);
        // Cleanup
        pendingPromotions.remove(base.getSimpleBase());

        // Prepare next promotion
        prepareForNextPromotion(base, true);
    }

    private void prepareForNextPromotion(Base base, boolean setDeltas) {
        BaseLevelStatus baseLevelStatus = base.getBaseLevelStatus();
        DbLevel nextDbLevel = getNextDbLevel(baseLevelStatus.getCurrentLevel());
        if (nextDbLevel == null) {
            return;
        }
        nextDbLevel = getUnskippable(nextDbLevel, base);
        if (nextDbLevel == null) {
            return;
        }
        if (setDeltas) {
            baseLevelStatus.setDeltas(base, nextDbLevel);
        }
        PendingPromotion pendingPromotion = new PendingPromotion(nextDbLevel);
        pendingPromotions.put(base.getSimpleBase(), pendingPromotion);
    }

    @Override
    public void restore(Collection<Base> bases) {
        synchronized (pendingPromotions) {
            pendingPromotions.clear();
            for (Base base : bases) {
                try {
                    prepareForNextPromotion(base, false);
                } catch (Throwable throwable) {
                    log.error("", throwable);
                }
            }
        }
    }

    private DbLevel getUnskippable(DbLevel dbLevel, Base base) {
        while (canBeSkippedIfBought(dbLevel, base)) {
            dbLevel = getNextDbLevel(dbLevel);
            if (dbLevel == null) {
                return null;
            }
        }
        return dbLevel;
    }

    private boolean canBeSkippedIfBought(DbLevel dbLevel, Base base) {
        Collection<DbBaseItemType> skipIfBought = dbLevel.getSkipIfItemsBought();
        if (skipIfBought == null || skipIfBought.isEmpty()) {
            return false;
        }
        for (DbBaseItemType dbBaseItemType : skipIfBought) {
            if (!serverMarketService.isAllowed(dbBaseItemType.getId(), base)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public DbUserStage getDbUserStage() {
        User user = userService.getLoggedinUser();
        if (user != null) {
            return user.getUserStage();
        }
        return getDbUserStageFromSession();
    }

    @Override
    public void onUserCreated(User user) {
        user.setDbUserStage(getDbUserStageFromSession());
        session.setDbUserStage(null);
    }

    @Override
    public void onTutorialFinished() {
        DbUserStage dbUserStage = getDbUserStage();
        DbUserStage nextDbUserStage = getNextDbUserStage(dbUserStage);
        saveUserStageInUser(nextDbUserStage);
    }

    private void saveUserStageInUser(DbUserStage dbUserStage) {
        User user = userService.getLoggedinUser();
        if (user != null) {
            user.setDbUserStage(dbUserStage);
            userService.save(user);
        } else {
            session.setDbUserStage(dbUserStage);
        }
    }

    private DbUserStage getNextDbUserStage(final DbUserStage dbUserStage) {
        @SuppressWarnings("unchecked")
        List<DbUserStage> dbUserStages = hibernateTemplate.executeFind(new HibernateCallback() {
            @Override
            public Object doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(DbUserStage.class);
                // TODO: Assumption orderindex is contiguously
                criteria.add(Restrictions.eq("orderIndex", dbUserStage.getOrderIndex() + 1));
                criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
                return criteria.list();
            }
        });
        if (dbUserStages.isEmpty()) {
            throw new IllegalStateException("No next DbUserStage found for: " + dbUserStage);
        } else {
            return dbUserStages.get(0);
        }
    }

    private DbUserStage getDbUserStageFromSession() {
        DbUserStage dbUserStage = session.getUserStage();
        if (dbUserStage == null) {
            dbUserStage = getLowestDbUserStage();
            session.setDbUserStage(dbUserStage);
        }
        return dbUserStage;
    }

    @SuppressWarnings("unchecked")
    private DbUserStage getLowestDbUserStage() {
        return getAllDbUserStage().get(0);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DbUserStage> getAllDbUserStage() {
        return hibernateTemplate.executeFind(new HibernateCallback() {
            @Override
            public Object doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(DbUserStage.class);
                criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
                criteria.addOrder(Order.asc("orderIndex"));
                return criteria.list();
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public void saveAllDbUserStage(final List<DbUserStage> dbUserStages) {
        for (int i = 0, dbUserStagesSize = dbUserStages.size(); i < dbUserStagesSize; i++) {
            dbUserStages.get(i).setOrderIndex(i);
        }
        hibernateTemplate.execute(new HibernateCallback() {
            @Override
            public Object doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                hibernateTemplate.saveOrUpdateAll(dbUserStages);
                hibernateTemplate.flush();
                return null;
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public DbUserStage getDbUserStage(final String name) {
        List<DbUserStage> result = hibernateTemplate.executeFind(new HibernateCallback() {
            @Override
            public Object doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(DbUserStage.class);
                criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
                criteria.add(Restrictions.eq("name", name));
                criteria.setFetchSize(1);
                return criteria.list();
            }
        });
        if (result.isEmpty()) {
            throw new IllegalStateException("No DbUserStage found for name: " + name);
        } else {
            return result.get(0);
        }
    }
}
