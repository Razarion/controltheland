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

package com.btxtech.game.services.item.impl;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.NotYourBaseException;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.base.AbstractBaseService;
import com.btxtech.game.jsre.common.gameengine.services.base.HouseSpaceExceededException;
import com.btxtech.game.jsre.common.gameengine.services.base.ItemLimitExceededException;
import com.btxtech.game.jsre.common.gameengine.services.items.BaseDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.services.items.impl.AbstractItemService;
import com.btxtech.game.jsre.common.gameengine.services.items.impl.ItemHandler;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseObject;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncResourceItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncTickItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.syncInfos.SyncItemInfo;
import com.btxtech.game.services.action.ActionService;
import com.btxtech.game.services.base.Base;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.common.ServerServices;
import com.btxtech.game.services.connection.ConnectionService;
import com.btxtech.game.services.energy.ServerEnergyService;
import com.btxtech.game.services.history.HistoryService;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.item.itemType.DbItemType;
import com.btxtech.game.services.item.itemType.DbItemTypeData;
import com.btxtech.game.services.item.itemType.DbItemTypeImage;
import com.btxtech.game.services.item.itemType.DbProjectileItemType;
import com.btxtech.game.services.item.itemType.DbResourceItemType;
import com.btxtech.game.services.market.ServerMarketService;
import com.btxtech.game.services.mgmt.MgmtService;
import com.btxtech.game.services.resource.ResourceService;
import com.btxtech.game.services.statistics.StatisticsService;
import com.btxtech.game.services.user.SecurityRoles;
import com.btxtech.game.services.utg.ServerConditionService;
import com.btxtech.game.services.utg.UserGuidanceService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * User: beat
 * Date: Jun 3, 2009
 * Time: 12:59:07 PM
 */
@Component(value = "itemService")
public class ItemServiceImpl extends AbstractItemService implements ItemService {
    @Autowired
    private HistoryService historyService;
    @Autowired
    private ConnectionService connectionService;
    @Autowired
    private ActionService actionService;
    @Autowired
    private BaseService baseService;
    @Autowired
    private ServerMarketService serverMarketService;
    @Autowired
    private ServerServices services;
    @Autowired
    private ServerEnergyService serverEnergyService;
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private MgmtService mgmtService;
    @Autowired
    private ServerConditionService serverConditionService;
    @Autowired
    private CrudRootServiceHelper<DbItemType> dbItemTypeCrud;
    @Autowired
    private StatisticsService statisticsService;
    private HibernateTemplate hibernateTemplate;
    private int lastId = 0;
    private final HashMap<Id, SyncItem> items = new HashMap<Id, SyncItem>();
    private Log log = LogFactory.getLog(ItemServiceImpl.class);
    private HashMap<Integer, HashMap<Integer, DbItemTypeImage>> itemTypeImages = new HashMap<Integer, HashMap<Integer, DbItemTypeImage>>();
    private HashMap<Integer, DbItemTypeData> muzzleItemTypeImages = new HashMap<Integer, DbItemTypeData>();
    private HashMap<Integer, DbItemTypeData> muzzleItemTypeSounds = new HashMap<Integer, DbItemTypeData>();

    @PostConstruct
    public void setup() {
        dbItemTypeCrud.init(DbItemType.class);
        try {
            SessionFactoryUtils.initDeferredClose(hibernateTemplate.getSessionFactory());
            activate();
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
    public SyncItem createSyncObject(ItemType toBeBuilt, Index position, SyncBaseItem creator, SimpleBase base, int createdChildCount) throws NoSuchItemTypeException, ItemLimitExceededException, HouseSpaceExceededException {
        if (base != null && !baseService.isAlive(base)) {
            throw new BaseDoesNotExistException(base);
        }

        SyncItem syncItem;
        synchronized (items) {
            if (toBeBuilt instanceof BaseItemType && !baseService.isBot(base) && !baseService.isAbandoned(base)) {
                baseService.checkItemLimit4ItemAdding((BaseItemType) toBeBuilt, base);
            }
            Id id = createId(creator, createdChildCount);
            syncItem = newSyncItem(id, position, toBeBuilt.getId(), base, services);
            items.put(id, syncItem);
            if (syncItem instanceof SyncBaseItem) {
                baseService.itemCreated((SyncBaseItem) syncItem);
            }
        }

        if (syncItem instanceof SyncBaseObject) {
            baseService.sendAccountBaseUpdate((SyncBaseObject) syncItem);
        }

        if (syncItem instanceof SyncTickItem) {
            actionService.syncItemActivated((SyncTickItem) syncItem);
        }

        if (syncItem instanceof SyncBaseItem) {
            SyncBaseItem syncBaseItem = (SyncBaseItem) syncItem;
            historyService.addItemCreatedEntry(syncBaseItem);
            actionService.addGuardingBaseItem(syncBaseItem);
            syncItem.addSyncItemListener(actionService);
            syncItem.addSyncItemListener(baseService);
            actionService.interactionGuardingItems(syncBaseItem);
            statisticsService.onItemCreated(syncBaseItem);
        }

        connectionService.sendSyncInfo(syncItem);
        if (log.isDebugEnabled()) {
            log.debug("CREATED: " + syncItem);
        }
        return syncItem;
    }

    private Id createId(SyncItem parent, int childIndex) {
        int parentId;
        if (parent != null) {
            parentId = parent.getId().getId();
        } else {
            parentId = Id.NO_ID;
            childIndex = Id.NO_ID;
        }

        if (lastId == Integer.MAX_VALUE) {
            throw new IllegalStateException("MAJOR ERROR!!! Number of id exeeded!!!");
        }
        lastId++;
        return new Id(lastId, parentId, childIndex);
    }

    @Override
    public SyncItem getItem(Id id) throws ItemDoesNotExistException {
        SyncItem syncItem = items.get(id);
        if (syncItem == null) {
            throw new ItemDoesNotExistException(id);
        }
        return syncItem;
    }

    @Override
    public Collection<SyncItemInfo> getSyncInfo() {
        HashSet<SyncItemInfo> result = new HashSet<SyncItemInfo>();
        synchronized (items) {
            for (SyncItem symcItem : items.values()) {
                result.add(symcItem.getSyncInfo());
            }
        }
        return result;
    }

    @Override
    public boolean baseObjectExists(SyncItem baseSyncItem) {
        return items.containsKey(baseSyncItem.getId());
    }

    @Override
    public void killSyncItem(SyncItem killedItem, SimpleBase actor, boolean force, boolean explode) {
        if (force) {
            if (killedItem instanceof SyncBaseItem) {
                ((SyncBaseItem) killedItem).setHealth(0);
            } else if (killedItem instanceof SyncResourceItem) {
                ((SyncResourceItem) killedItem).setAmount(0);
            }
        }

        if (killedItem.isAlive()) {
            throw new IllegalStateException("SyncItem is still alive: " + killedItem);
        }

        synchronized (items) {
            if (items.remove(killedItem.getId()) == null) {
                throw new IllegalStateException("Id does not exist: " + killedItem);
            }
            if (killedItem instanceof SyncBaseItem) {
                historyService.addItemDestroyedEntry(actor, (SyncBaseItem) killedItem);
                baseService.itemDeleted((SyncBaseItem) killedItem, actor);
            }
        }
        killedItem.setExplode(explode);
        if (log.isDebugEnabled()) {
            log.debug("DELETED: " + killedItem);
        }
        connectionService.sendSyncInfo(killedItem);

        if (killedItem instanceof SyncBaseItem) {
            actionService.removeGuardingBaseItem((SyncBaseItem) killedItem);
            if (actor != null) {
                Base actorBase = baseService.getBase(actor);
                statisticsService.onItemKilled((SyncBaseItem) killedItem, actorBase.getSimpleBase());
                serverMarketService.increaseXp(actorBase, (SyncBaseItem) killedItem);
                serverConditionService.onSyncItemKilled(actor, (SyncBaseItem) killedItem);

            }
            serverEnergyService.onBaseItemKilled((SyncBaseItem) killedItem);
            killContainedItems((SyncBaseItem) killedItem, actor);
        } else if (killedItem instanceof SyncResourceItem) {
            resourceService.resourceItemDeleted((SyncResourceItem) killedItem);
        }

    }

    @Override
    public void killSyncItems(Collection<SyncItem> itemsToKill) {
        for (SyncItem syncItem : itemsToKill) {
            try {
                killSyncItem(syncItem, null, true, false);

            } catch (Exception e) {
                log.error("", e);
            }
        }
    }

    @Override
    public void killSyncItemIds(Collection<Id> itemsToKill) {
        Collection<SyncItem> syncItems = new ArrayList<SyncItem>();
        for (Id id : itemsToKill) {
            try {
                syncItems.add(getItem(id));
            } catch (ItemDoesNotExistException e) {
                log.error("", e);
            }
        }
        killSyncItems(syncItems);
    }

    private void killContainedItems(SyncBaseItem syncBaseItem, SimpleBase actor) {
        if (!syncBaseItem.hasSyncItemContainer()) {
            return;
        }
        for (Id id : syncBaseItem.getSyncItemContainer().getContainedItems()) {
            try {
                SyncBaseItem baseItem = (SyncBaseItem) getItem(id);
                killSyncItem(baseItem, actor, true, false);
            } catch (ItemDoesNotExistException e) {
                log.error("", e);
            }
        }
    }

    @Override
    public SyncBaseItem getFirstEnemyItemInRange(SyncBaseItem baseSyncItem) {
        synchronized (items) {
            for (SyncItem syncItem : items.values()) {
                if (!syncItem.getSyncItemArea().hasPosition()) {
                    continue;
                }

                if (syncItem instanceof SyncBaseItem
                        && baseSyncItem.isEnemy((SyncBaseItem) syncItem)
                        && baseSyncItem.getSyncWeapon().isAttackAllowedWithoutMoving(syncItem))
                    return (SyncBaseItem) syncItem;
            }
        }
        return null;
    }


    @Override
    public List<SyncItem> getItemsCopy() {
        synchronized (items) {
            return new ArrayList<SyncItem>(items.values());
        }
    }

    @Override
    public void restoreItems(Collection<SyncItem> syncItems) {
        int lastId = 0;
        synchronized (items) {
            items.clear();
            for (SyncItem syncItem : syncItems) {
                items.put(syncItem.getId(), syncItem);
                if (syncItem instanceof SyncBaseItem) {
                    syncItem.addSyncItemListener(actionService);
                    syncItem.addSyncItemListener(baseService);
                }
                if (syncItem.getId().isSynchronized()) {
                    int tmpId = syncItem.getId().getId();
                    if (lastId < tmpId) {
                        lastId = tmpId;
                    }
                }
            }
        }
        this.lastId = lastId + 1;
        actionService.reload();
    }

    @Override
    protected <T> T iterateOverItems(ItemHandler<T> itemHandler, T defaultReturn) {
        synchronized (items) {
            for (SyncItem syncItem : items.values()) {
                T result = itemHandler.handleItem(syncItem);
                if (result != null) {
                    return result;
                }
            }
        }
        return defaultReturn;
    }

    @Override
    protected AbstractBaseService getBaseService() {
        return baseService;
    }

    @Override
    @Transactional
    @Secured(SecurityRoles.ROLE_ADMINISTRATOR)
    public void saveDbItemTypes(Collection<DbItemType> itemTypes) {
        hibernateTemplate.saveOrUpdateAll(itemTypes);
    }

    @Override
    @Transactional
    @Secured(SecurityRoles.ROLE_ADMINISTRATOR)
    public void saveAttackMatrix(Collection<DbBaseItemType> weaponDbItemTypes) {
        for (DbBaseItemType weaponDbItemType : weaponDbItemTypes) {
            hibernateTemplate.merge(weaponDbItemType);
        }
    }

    @Override
    @Transactional
    @Secured(SecurityRoles.ROLE_ADMINISTRATOR)
    public void saveDbItemType(DbItemType dbItemType) {
        hibernateTemplate.saveOrUpdate(dbItemType);
    }

    @Override
    @Secured(SecurityRoles.ROLE_ADMINISTRATOR)
    public BoundingBox getBoundingBox(int itemTypeId) throws NoSuchItemTypeException {
        DbItemType dbItemType = getDbItemType(itemTypeId);
        if (dbItemType == null) {
            throw new NoSuchItemTypeException(itemTypeId);
        }
        return dbItemType.getBoundingBox();
    }

    @Override
    @Transactional
    @Secured(SecurityRoles.ROLE_ADMINISTRATOR)
    public void saveBoundingBox(int itemTypeId, BoundingBox boundingBox) throws NoSuchItemTypeException {
        DbItemType dbItemType = getDbItemType(itemTypeId);
        if (dbItemType == null) {
            throw new NoSuchItemTypeException(itemTypeId);
        }
        dbItemType.setBounding(boundingBox);
        saveDbItemType(dbItemType);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<DbItemType> getDbItemTypes() {
        return hibernateTemplate.executeFind(new HibernateCallback() {
            @Override
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(DbItemType.class);
                criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
                return criteria.list();
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<DbBaseItemType> getDbBaseItemTypes() {
        return hibernateTemplate.executeFind(new HibernateCallback() {
            @Override
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(DbBaseItemType.class);
                criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
                return criteria.list();
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<DbProjectileItemType> getDbProjectileItemTypes() {
        return hibernateTemplate.executeFind(new HibernateCallback() {
            @Override
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(DbProjectileItemType.class);
                criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
                return criteria.list();
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<DbBaseItemType> getWeaponDbBaseItemTypes() {
        return hibernateTemplate.executeFind(new HibernateCallback() {
            @Override
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(DbBaseItemType.class);
                criteria.add(Restrictions.isNotNull("dbWeaponType"));
                criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
                return criteria.list();
            }
        });
    }

    @Override
    public void activate() {
        Collection<DbItemType> dbItemTypes = getDbItemTypes();
        ArrayList<ItemType> itemTypes = new ArrayList<ItemType>();
        itemTypeImages.clear();
        muzzleItemTypeImages.clear();
        muzzleItemTypeSounds.clear();
        for (DbItemType dbItemType : dbItemTypes) {
            itemTypes.add(dbItemType.createItemType());
            addItemTypeImages(dbItemType);
            if (dbItemType instanceof DbBaseItemType) {
                addMuzzleEffect((DbBaseItemType) dbItemType);
            }
        }
        synchronize(itemTypes);
    }

    private void synchronize(Collection<ItemType> itemTypes) {
        ArrayList<ItemType> newItems = new ArrayList<ItemType>(itemTypes);
        newItems.removeAll(getItemTypes());
        ArrayList<ItemType> removedItems = new ArrayList<ItemType>(getItemTypes());
        removedItems.removeAll(itemTypes);
        ArrayList<ItemType> changingItems = new ArrayList<ItemType>(itemTypes);
        changingItems.retainAll(getItemTypes());

        checkRemove(removedItems);
        putAll(newItems);
        removeAll(removedItems);
        changeAll(changingItems);
    }

    private void checkRemove(ArrayList<ItemType> removedItems) {
        synchronized (items) {
            for (SyncItem syncItem : items.values()) {
                if (removedItems.contains(syncItem.getItemType())) {
                    throw new IllegalArgumentException("Can not delete: " + syncItem.getItemType());
                }
            }
        }
    }


    private void addItemTypeImages(DbItemType dbItemType) {
        if (itemTypeImages.containsKey(dbItemType.getId())) {
            throw new IllegalArgumentException("Item Type Images already exits: " + dbItemType);
        }
        HashMap<Integer, DbItemTypeImage> indexImageHashMap = new HashMap<Integer, DbItemTypeImage>();
        for (DbItemTypeImage itemTypeImage : dbItemType.getItemTypeImages()) {
            if (indexImageHashMap.containsKey(itemTypeImage.getNumber())) {
                throw new IllegalArgumentException("Item Type Image Index already exits: " + dbItemType + " index: " + itemTypeImage.getNumber());
            }
            indexImageHashMap.put(itemTypeImage.getNumber(), itemTypeImage);
        }
        itemTypeImages.put(dbItemType.getId(), indexImageHashMap);
    }

    private void addMuzzleEffect(DbBaseItemType dbItemType) {
        if (dbItemType.getDbWeaponType() == null) {
            return;
        }
        // Image
        if (muzzleItemTypeImages.containsKey(dbItemType.getId())) {
            throw new IllegalArgumentException("Item Type Images already exits: " + dbItemType);
        }
        Hibernate.initialize(dbItemType.getDbWeaponType().getDbMuzzleImage());
        muzzleItemTypeImages.put(dbItemType.getId(), dbItemType.getDbWeaponType().getDbMuzzleImage());
        // Sound
        if (muzzleItemTypeSounds.containsKey(dbItemType.getId())) {
            throw new IllegalArgumentException("Item Type sound already exits: " + dbItemType);
        }
        Hibernate.initialize(dbItemType.getDbWeaponType().getDbSound());
        muzzleItemTypeSounds.put(dbItemType.getId(), dbItemType.getDbWeaponType().getDbSound());
    }

    @Override
    public DbItemType getDbItemType(int itemTypeId) {
        return hibernateTemplate.get(DbItemType.class, itemTypeId);
    }

    @Override
    public DbBaseItemType getDbBaseItemType(int itemBaseTypeId) {
        return hibernateTemplate.get(DbBaseItemType.class, itemBaseTypeId);
    }

    @Override
    public DbResourceItemType getDbResourceItemType(int resourceItemType) {
        return hibernateTemplate.get(DbResourceItemType.class, resourceItemType);
    }

    @Override
    @Secured(SecurityRoles.ROLE_ADMINISTRATOR)
    @Transactional
    public void deleteItemType(DbItemType dbItemType) {
        hibernateTemplate.delete(dbItemType);
    }

    @Override
    public DbItemTypeImage getItemTypeImage(int itemTypeId, int index) {
        HashMap<Integer, DbItemTypeImage> indexImages = itemTypeImages.get(itemTypeId);
        if (indexImages == null) {
            throw new IllegalArgumentException("Item Type id does not exist: " + itemTypeId);
        }
        DbItemTypeImage itemTypeImage = indexImages.get(index);
        if (itemTypeImage == null) {
            throw new IllegalArgumentException("Item Type index does not exist: " + index + ". ItemTypeId: " + itemTypeId);
        }
        return itemTypeImage;
    }

    @Override
    public DbItemTypeData getMuzzleFlashImage(int itemTypeId) {
        DbItemTypeData itemTypeImage = muzzleItemTypeImages.get(itemTypeId);
        if (itemTypeImage == null) {
            throw new IllegalArgumentException("Muzzle image does not exist: " + itemTypeId);
        }
        return itemTypeImage;
    }

    @Override
    public DbItemTypeData getMuzzleFlashSound(int itemTypeId) {
        DbItemTypeData sound = muzzleItemTypeSounds.get(itemTypeId);
        if (sound == null) {
            throw new IllegalArgumentException("Muzzle sound does not exist: " + itemTypeId);
        }
        return sound;
    }

    public List<? extends SyncItem> getItems(ItemType itemType, SimpleBase simpleBase) {
        ArrayList<SyncItem> syncItems = new ArrayList<SyncItem>();
        synchronized (items) {
            for (SyncItem syncItem : items.values()) {
                if (!syncItem.getItemType().equals(itemType)) {
                    continue;
                }
                if (simpleBase != null) {
                    if (syncItem instanceof SyncBaseItem && ((SyncBaseItem) syncItem).getBase().equals(simpleBase)) {
                        syncItems.add(syncItem);
                    }
                } else {
                    syncItems.add(syncItem);
                }

            }
        }
        return syncItems;
    }

    @Override
    public Map<BaseItemType, List<SyncBaseItem>> getItems4Base(SimpleBase simpleBase) {
        Map<BaseItemType, List<SyncBaseItem>> result = new HashMap<BaseItemType, List<SyncBaseItem>>();
        synchronized (items) {
            for (SyncItem syncItem : items.values()) {
                if (syncItem instanceof SyncBaseItem && ((SyncBaseItem) syncItem).getBase().equals(simpleBase)) {
                    SyncBaseItem syncBaseItem = (SyncBaseItem) syncItem;
                    List<SyncBaseItem> syncBaseItems = result.get(syncBaseItem.getBaseItemType());
                    if (syncBaseItems == null) {
                        syncBaseItems = new ArrayList<SyncBaseItem>();
                        result.put(syncBaseItem.getBaseItemType(), syncBaseItems);
                    }
                    syncBaseItems.add(syncBaseItem);
                }
            }
        }
        return result;
    }

    @Override
    public List<SyncBaseItem> getEnemyItems(SimpleBase simpleBase, Rectangle region, boolean ignoreBot) {
        ArrayList<SyncBaseItem> clientBaseItems = new ArrayList<SyncBaseItem>();
        synchronized (items) {
            for (SyncItem syncItem : items.values()) {
                if (!syncItem.getSyncItemArea().hasPosition()) {
                    continue;
                }
                if (syncItem instanceof SyncBaseItem
                        && !((SyncBaseItem) syncItem).getBase().equals(simpleBase)
                        && region.contains(syncItem.getSyncItemArea().getPosition())
                        && (!ignoreBot || !baseService.isBot(((SyncBaseItem) syncItem).getBase()))) {
                    clientBaseItems.add((SyncBaseItem) syncItem);
                }
            }
        }
        return clientBaseItems;
    }

    @Override
    public boolean isSyncItemOverlapping(SyncItem syncItem) {
        return isSyncItemOverlapping(syncItem, null, null);
    }

    @Override
    public void sellItem(Id id) throws ItemDoesNotExistException, NotYourBaseException {
        SyncBaseItem syncBaseItem = (SyncBaseItem) getItem(id);
        baseService.checkBaseAccess(syncBaseItem);
        double health = syncBaseItem.getHealth();
        double fullHealth = syncBaseItem.getBaseItemType().getHealth();
        double price = syncBaseItem.getBaseItemType().getPrice();
        double buildup = syncBaseItem.getBuildup();
        killSyncItem(syncBaseItem, null, true, false);
        SimpleBase simpleBase = syncBaseItem.getBase();
        // May last item sold
        if (baseService.isAlive(simpleBase)) {
            Base base = baseService.getBase(simpleBase);
            double money = health / fullHealth * buildup * price * userGuidanceService.getDbLevel().getItemSellFactor();
            baseService.depositResource(money, simpleBase);
            baseService.sendAccountBaseUpdate(base);
        }
    }

    @Override
    public Collection<SyncBaseItem> getBaseItemsInRectangle(Rectangle rectangle, SimpleBase simpleBase, Collection<BaseItemType> baseItemTypeFilter) {
        ArrayList<SyncBaseItem> result = new ArrayList<SyncBaseItem>();
        synchronized (items) {
            for (SyncItem syncItem : items.values()) {
                if (!syncItem.getSyncItemArea().hasPosition()) {
                    continue;
                }
                if (syncItem instanceof SyncBaseItem) {
                    SyncBaseItem syncBaseItem = (SyncBaseItem) syncItem;
                    if (simpleBase != null && !(syncBaseItem.getBase().equals(simpleBase))) {
                        continue;
                    }
                    if (!syncBaseItem.getSyncItemArea().contains(rectangle)) {
                        continue;
                    }
                    if (baseItemTypeFilter != null && !baseItemTypeFilter.contains(syncBaseItem.getBaseItemType())) {
                        continue;
                    }
                    result.add(syncBaseItem);
                }
            }
        }
        return result;
    }

    @Override
    public ItemType getItemType(DbItemType dbItemType) {
        ItemType itemType;
        try {
            itemType = getItemType(dbItemType.getId());
        } catch (NoSuchItemTypeException e) {
            throw new RuntimeException(e);
        }
        return itemType;
    }

    @Override
    public CrudRootServiceHelper<DbItemType> getDbItemTypeCrud() {
        return dbItemTypeCrud;
    }
}
