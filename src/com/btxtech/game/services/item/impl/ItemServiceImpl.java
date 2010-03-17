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
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.services.items.impl.AbstractItemService;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncResourceItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.syncInfos.SyncItemInfo;
import com.btxtech.game.services.action.ActionService;
import com.btxtech.game.services.base.Base;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.common.ServerServices;
import com.btxtech.game.services.connection.ConnectionService;
import com.btxtech.game.services.energy.ServerEnergyService;
import com.btxtech.game.services.history.CreatedElement;
import com.btxtech.game.services.history.DestroyedElement;
import com.btxtech.game.services.history.HistoryService;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.item.itemType.DbItemType;
import com.btxtech.game.services.item.itemType.DbItemTypeData;
import com.btxtech.game.services.item.itemType.DbItemTypeImage;
import com.btxtech.game.services.itemTypeAccess.ServerItemTypeAccessService;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

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
    private ServerItemTypeAccessService serverItemTypeAccessService;
    @Autowired
    private ServerServices services;
    @Autowired
    private ServerEnergyService serverEnergyService;
    private HibernateTemplate hibernateTemplate;
    private int lastId = 0;
    private final HashMap<Id, SyncItem> items = new HashMap<Id, SyncItem>();
    private Log log = LogFactory.getLog(ItemServiceImpl.class);
    private HashMap<Integer, HashMap<Integer, DbItemTypeImage>> itemTypeImages = new HashMap<Integer, HashMap<Integer, DbItemTypeImage>>();
    private HashMap<Integer, DbItemTypeData> muzzleItemTypeImages = new HashMap<Integer, DbItemTypeData>();
    private HashMap<Integer, DbItemTypeData> muzzleItemTypeSounds = new HashMap<Integer, DbItemTypeData>();


    @PostConstruct
    public void setup() {
        try {
            loadItemType();
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    @Override
    public SyncItem createSyncObject(ItemType toBeBuilt, Index position, SyncBaseItem creator, SimpleBase base, int createdChildCount) throws NoSuchItemTypeException {
        Id id = createId(creator, createdChildCount);
        SyncItem syncItem = newSyncItem(id, position, toBeBuilt.getId(), base, services);
        synchronized (items) {
            items.put(id, syncItem);
        }

        if (syncItem instanceof SyncBaseItem) {
            historyService.addHistoryElement(new CreatedElement((SyncBaseItem) syncItem));
            actionService.addGuardingBaseItem((SyncBaseItem) syncItem);
            syncItem.addSyncItemListener(actionService);
            baseService.itemCreated((SyncBaseItem) syncItem);
            baseService.sendAccountBaseUpdate((SyncBaseItem) syncItem);
            actionService.interactionGuardingItems((SyncBaseItem) syncItem);
        }
        connectionService.sendSyncInfo(syncItem);
        log.info("CREATED: " + syncItem);
        return syncItem;
    }

    private Id createId(SyncItem parent, int childIndex) {
        int parentId;
        if (parent != null) {
            parentId = parent.getId().getId();
        } else {
            parentId = 0;
            childIndex = 0;
        }

        synchronized (items) {
            if (lastId == Integer.MAX_VALUE) {
                throw new IllegalStateException("MAJOR ERROR!!! Number of id exeeded!!!");
            }
            lastId++;
            return new Id(lastId, parentId, childIndex);
        }
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
    public void killBaseSyncObject(SyncItem syncItem, SyncBaseItem actor, boolean force) {
        if (force) {
            if (syncItem instanceof SyncBaseItem) {
                ((SyncBaseItem) syncItem).setHealth(0);
            } else if (syncItem instanceof SyncResourceItem) {
                ((SyncResourceItem) syncItem).setAmount(0);
            }
        }

        if (syncItem.isAlive()) {
            throw new IllegalStateException("SyncItem is still alive: " + syncItem);
        }

        synchronized (items) {
            if (items.remove(syncItem.getId()) == null) {
                throw new IllegalStateException("Id does not exist: " + syncItem);
            }
        }
        log.info("DELETED: " + syncItem);
        connectionService.sendSyncInfo(syncItem);

        if (syncItem instanceof SyncBaseItem) {
            actionService.removeGuardingBaseItem((SyncBaseItem) syncItem);
            if (actor != null) {
                historyService.addHistoryElement(new DestroyedElement(actor, (SyncBaseItem) syncItem));
                Base actorBase = baseService.getBase(actor);
                actorBase.increaseKills();
                serverItemTypeAccessService.increaseXp(actorBase, (SyncBaseItem) syncItem);
            }
            baseService.itemDeleted((SyncBaseItem) syncItem, actor);
            serverEnergyService.onBaseItemKilled((SyncBaseItem) syncItem);
        }

        if (syncItem instanceof SyncResourceItem) {
            actionService.moneyItemDeleted((SyncResourceItem) syncItem);
        }
    }

    @Override
    public boolean hasItemsInRectangle(Rectangle rectangle) {
        synchronized (items) {
            for (SyncItem syncItem : items.values()) {
                if (rectangle.contains(syncItem.getPosition())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public SyncBaseItem getFirstEnemyItemInRange(SyncBaseItem baseSyncItem, int range) {
        int startX = baseSyncItem.getPosition().getX() - range;
        if (startX < 0) {
            startX = 0;
        }
        int startY = baseSyncItem.getPosition().getY() - range;
        if (startY < 0) {
            startY = 0;
        }
        Rectangle rectangle = new Rectangle(startX, startY, 2 * range, 2 * range);
        synchronized (items) {
            for (SyncItem syncItem : items.values()) {
                if (rectangle.contains(syncItem.getPosition())
                        && syncItem instanceof SyncBaseItem
                        && baseSyncItem.isEnemy((SyncBaseItem) syncItem)
                        && syncItem.getPosition().getDistance(baseSyncItem.getPosition()) <= range) {
                    return (SyncBaseItem) syncItem;
                }
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
    public List<SyncItem> getItemsCopyNoDummies() {
        List<SyncItem> syncItems = getItemsCopy();
        SimpleBase dummy = baseService.getDummyBase();
        for (Iterator<SyncItem> it = syncItems.iterator(); it.hasNext();) {
            SyncItem syncItem = it.next();
            if ((syncItem instanceof SyncBaseItem) && dummy.equals(((SyncBaseItem) syncItem).getBase())) {
                it.remove();
            }
        }
        return syncItems;
    }

    @Override
    public List<SyncItem> getItemsCopyNoDummiesNoBots() {
        List<SyncItem> syncItems = getItemsCopy();
        SimpleBase dummy = baseService.getDummyBase();
        for (Iterator<SyncItem> it = syncItems.iterator(); it.hasNext();) {
            SyncItem syncItem = it.next();
            if ((syncItem instanceof SyncBaseItem) && (dummy.equals(((SyncBaseItem) syncItem).getBase()) || ((SyncBaseItem) syncItem).getBase().isBot())) {
                it.remove();
            }
        }
        return syncItems;
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
    protected com.btxtech.game.jsre.common.gameengine.services.base.BaseService getBaseService() {
        return baseService;
    }

    @Override
    public void saveDbItemTypes(Collection<DbItemType> itemTypes) {
        hibernateTemplate.saveOrUpdateAll(itemTypes);
    }

    @Override
    public void saveDbItemType(DbItemType dbItemType) {
        hibernateTemplate.saveOrUpdate(dbItemType);
    }

    @Override
    public Collection<DbItemType> getDbItemTypes() {
        return (Collection<DbItemType>) hibernateTemplate.execute(new HibernateCallback() {
            @Override
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(DbItemType.class);
                criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
                return criteria.list();
            }
        });
    }

    @Override
    public void loadItemType() {
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
        if (dbItemType.getWeaponType() == null) {
            return;
        }
        // Image
        if (muzzleItemTypeImages.containsKey(dbItemType.getId())) {
            throw new IllegalArgumentException("Item Type Images already exits: " + dbItemType);
        }
        muzzleItemTypeImages.put(dbItemType.getId(), dbItemType.getWeaponType().getDbMuzzleImage());
        // Sound
        if (muzzleItemTypeSounds.containsKey(dbItemType.getId())) {
            throw new IllegalArgumentException("Item Type sound already exits: " + dbItemType);
        }
        muzzleItemTypeSounds.put(dbItemType.getId(), dbItemType.getWeaponType().getDbSound());
    }

    @Override
    public DbItemType getDbItemType(final int itemTypeId) {
        List<DbItemType> list = (List<DbItemType>) hibernateTemplate.execute(new HibernateCallback() {
            @Override
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(DbItemType.class);
                criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
                criteria.add(Restrictions.eq("id", itemTypeId));
                return criteria.list();
            }
        });
        if (list.isEmpty()) {
            throw new IllegalArgumentException("No DB entry for: " + itemTypeId);
        }
        if (list.size() > 1) {
            throw new IllegalArgumentException("More then one entry found: " + itemTypeId);
        }
        return list.get(0);
    }

    @Override
    public void delteItemType(DbItemType dbItemType) {
        hibernateTemplate.delete(dbItemType);
    }

    @Override
    public void removeItemTypeImages(DbItemType dbItemType) {
        hibernateTemplate.deleteAll(dbItemType.getItemTypeImages());
    }

    @Override
    public DbItemTypeImage getItemTypeImage(int itemTypeId, int index) {
        HashMap<Integer, DbItemTypeImage> indexImages = itemTypeImages.get(itemTypeId);
        if (indexImages == null) {
            throw new IllegalArgumentException("Item Type id does not exist: " + itemTypeId);
        }
        DbItemTypeImage itemTypeImage = indexImages.get(index);
        if (itemTypeImage == null) {
            throw new IllegalArgumentException("Item Type index does not exist: " + index);
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

    public List<SyncItem> getItems(ItemType itemType, SimpleBase simpleBase) {
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
    public List<SyncBaseItem> getEnemyItems(SimpleBase simpleBase) {
        ArrayList<SyncBaseItem> clientBaseItems = new ArrayList<SyncBaseItem>();
        synchronized (items) {
            for (SyncItem syncItem : items.values()) {
                if (syncItem instanceof SyncBaseItem && !((SyncBaseItem) syncItem).getBase().equals(simpleBase)) {
                    clientBaseItems.add((SyncBaseItem) syncItem);
                }
            }
        }
        return clientBaseItems;
    }
}
