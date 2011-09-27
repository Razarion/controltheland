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

import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Level;
import com.btxtech.game.jsre.common.LevelPacket;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncResourceItem;
import com.btxtech.game.services.base.Base;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.collision.CollisionService;
import com.btxtech.game.services.connection.ConnectionService;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.market.ServerMarketService;
import com.btxtech.game.services.utg.DbItemCount;
import com.btxtech.game.services.utg.DbLevel;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.UserTrackingService;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.annotation.PostConstruct;
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
    private HibernateTemplate hibernateTemplate;
    final private HashMap<SimpleBase, PendingPromotion> pendingPromotions = new HashMap<SimpleBase, PendingPromotion>();
    private HashMap<String, Level> levels = new HashMap<String, Level>();
    private Log log = LogFactory.getLog(UserGuidanceServiceImpl.class);

    @PostConstruct
    public void setup() {
        updateLevels();
    }

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    @Override
    public void createMissionTarget(Id attacker) throws NoSuchItemTypeException, ItemDoesNotExistException {
        ItemType targetItemType = itemService.getItemType(Constants.JEEP);
        SyncBaseItem attackerItem = (SyncBaseItem) itemService.getItem(attacker);
        Index targetPos = collisionService.getFreeRandomPosition(targetItemType, attackerItem, Constants.TARGET_MIN_RANGE, Constants.TARGET_MAX_RANGE);
        SyncBaseItem syncBaseItem = (SyncBaseItem) itemService.createSyncObject(targetItemType, targetPos, null, baseService.getDummyBase(), 0);
        syncBaseItem.setBuild(true);
        syncBaseItem.setFullHealth();
    }

    @Override
    public void createMissionMoney(Id harvester) throws NoSuchItemTypeException, ItemDoesNotExistException {
        ItemType moneyItemType = itemService.getItemType(Constants.MONEY);
        SyncBaseItem attackerItem = (SyncBaseItem) itemService.getItem(harvester);
        Index targetPos = collisionService.getFreeRandomPosition(moneyItemType, attackerItem, Constants.TARGET_MIN_RANGE, Constants.TARGET_MAX_RANGE);
        SyncResourceItem syncBaseItem = (SyncResourceItem) itemService.createSyncObject(moneyItemType, targetPos, null, null, 0);
        syncBaseItem.setAmount(Constants.MISSION_MONEY);
        syncBaseItem.setMissionMoney(true);
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

    @SuppressWarnings("unchecked")
    public DbLevel getDbLevel(final String level) {
        List<DbLevel> levels = hibernateTemplate.executeFind(new HibernateCallback() {
            @Override
            public Object doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(DbLevel.class);
                criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
                criteria.add(Restrictions.eq("name", level));
                return criteria.list();
            }
        });
        if (levels.isEmpty()) {
            throw new IllegalArgumentException("Unknown level: " + level);
        }
        return levels.get(0);
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
        hibernateTemplate.delete(dbLevel);
        updateLevels();
    }

    @Override
    public void addDbLevel() {
        DbLevel dbLevel = new DbLevel();
        dbLevel.setRank(getHighestDbLevel() + 1);
        hibernateTemplate.save(dbLevel);
        updateLevels();
    }

    @Override
    public void saveDbLevels(List<DbLevel> dbLevels) {
        hibernateTemplate.saveOrUpdateAll(dbLevels);
        updateLevels();
    }

    @Override
    public void saveDbLevel(DbLevel dbLevel) {
        hibernateTemplate.update(dbLevel);
        updateLevels();
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
            updateLevels();
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
            updateLevels();
        }
    }

    private void updateLevels() {
        levels.clear();
        for (Iterator<DbLevel> iterator = getDbLevels().iterator(); iterator.hasNext();) {
            DbLevel dbLevel = iterator.next();
            Level level = dbLevel.createLevel();
            if (!iterator.hasNext()) {
                level.setRunTutorial(true);
            }
            levels.put(level.getName(), level);
        }
    }

    private Level getLevel(DbLevel dbLevel) {
        return getLevel(dbLevel.getName());
    }

    private Level getLevel(String levelString) {
        Level level = levels.get(levelString);
        if (level == null) {
            throw new IllegalArgumentException("Level does no exists: " + levelString);
        }
        return level;
    }

    @Override
    public Level getLevel4Base() {
        return getLevel(baseService.getBase().getLevel());
    }

    @Override
    public String getMissionTarget4NextLevel(Base base) {
        DbLevel dbLevel = getNextDbLevel(getDbLevel(base.getLevel()));
        dbLevel = getUnskippable(dbLevel, base);
        return dbLevel.getMissionTarget();
    }

    @Override
    public void setupLevel4NewBase(Base base) {
        DbLevel dbLevel = getLowestDbLevel();
        dbLevel = getUnskippable(dbLevel, base);
        if (dbLevel == null) {
            return;
        }
        base.setLevel(dbLevel.getName());
        prepareForNextPromotion(dbLevel, base);
        userTrackingService.levelPromotion(base, null);
    }

    @Override
    public void tutorialTerminated() {
        Base base = baseService.getBase();
        PendingPromotion pendingPromotion = pendingPromotions.get(base.getSimpleBase());
        if (pendingPromotion == null || pendingPromotion.getDbLevel().isTutorialTermination() == null || !pendingPromotion.getDbLevel().isTutorialTermination()) {
            return;
        }
        pendingPromotion.setTutorialAchieved();
        userTrackingService.levelInterimPromotion(base, pendingPromotion.getDbLevel().getName(), PendingPromotion.INTERIM_PROMOTION_TUTORIAL);
        checkAndHandlePromotion(pendingPromotion, base);
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
        String oldLevel = base.getLevel();
        DbLevel achievedLevel = pendingPromotion.getDbLevel();
        base.setLevel(achievedLevel.getName());
        LevelPacket levelPacket = new LevelPacket();
        levelPacket.setLevel(getLevel(achievedLevel));
        connectionService.sendPacket(base.getSimpleBase(), levelPacket);
        userTrackingService.levelPromotion(base, oldLevel);
        // Cleanup
        pendingPromotions.remove(base.getSimpleBase());

        // Prepare next promotion
        prepareForNextPromotion(achievedLevel, base);
    }

    private void prepareForNextPromotion(DbLevel dbLevel, Base base) {
        DbLevel nextDbLevel = getNextDbLevel(dbLevel);
        nextDbLevel = getUnskippable(nextDbLevel, base);
        if (nextDbLevel == null) {
            return;
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
                    prepareForNextPromotion(getDbLevel(base.getLevel()), base);
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
}