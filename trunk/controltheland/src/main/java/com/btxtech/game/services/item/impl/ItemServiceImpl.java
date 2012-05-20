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
import com.btxtech.game.jsre.common.CommonJava;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.gameengine.itemType.BuildupStep;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.WeaponType;
import com.btxtech.game.jsre.common.gameengine.services.Services;
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
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBoxItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncResourceItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncTickItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.syncInfos.SyncItemInfo;
import com.btxtech.game.services.action.ActionService;
import com.btxtech.game.services.base.Base;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.common.ImageHolder;
import com.btxtech.game.services.common.ServerServices;
import com.btxtech.game.services.connection.ConnectionService;
import com.btxtech.game.services.energy.ServerEnergyService;
import com.btxtech.game.services.history.HistoryService;
import com.btxtech.game.services.inventory.InventoryService;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.item.itemType.DbBoxItemType;
import com.btxtech.game.services.item.itemType.DbBuildupStep;
import com.btxtech.game.services.item.itemType.DbItemType;
import com.btxtech.game.services.item.itemType.DbItemTypeImage;
import com.btxtech.game.services.item.itemType.DbItemTypeImageData;
import com.btxtech.game.services.item.itemType.DbItemTypeSoundData;
import com.btxtech.game.services.item.itemType.DbProjectileItemType;
import com.btxtech.game.services.item.itemType.DbResourceItemType;
import com.btxtech.game.services.resource.ResourceService;
import com.btxtech.game.services.statistics.StatisticsService;
import com.btxtech.game.services.user.SecurityRoles;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.XpService;
import com.btxtech.game.services.utg.condition.ServerConditionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

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
    private XpService xpService;
    @Autowired
    private ServerServices services;
    @Autowired
    private ServerEnergyService serverEnergyService;
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private ServerConditionService serverConditionService;
    @Autowired
    private CrudRootServiceHelper<DbItemType> dbItemTypeCrud;
    @Autowired
    private StatisticsService statisticsService;
    @Autowired
    private SessionFactory sessionFactory;
    private int lastId = 0;
    private final HashMap<Id, SyncItem> items = new HashMap<Id, SyncItem>();
    private Log log = LogFactory.getLog(ItemServiceImpl.class);
    private HashMap<Integer, ImageHolder> itemTypeSpriteMap = new HashMap<Integer, ImageHolder>();
    private HashMap<Integer, HashMap<Integer, DbBuildupStep>> buildupStepsImages = new HashMap<Integer, HashMap<Integer, DbBuildupStep>>();
    private HashMap<Integer, DbItemTypeImageData> muzzleItemTypeImages = new HashMap<Integer, DbItemTypeImageData>();
    private HashMap<Integer, DbItemTypeSoundData> muzzleItemTypeSounds = new HashMap<Integer, DbItemTypeSoundData>();

    @PostConstruct
    public void setup() {
        dbItemTypeCrud.init(DbItemType.class);
        try {
            HibernateUtil.openSession4InternalCall(sessionFactory);
            activate();
        } catch (Throwable t) {
            log.error("", t);
        } finally {
            HibernateUtil.closeSession4InternalCall(sessionFactory);
        }
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
                baseService.onItemCreated((SyncBaseItem) syncItem);
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
            xpService.onItemBuilt(syncBaseItem);
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
            } else if (killedItem instanceof SyncBoxItem) {
                ((SyncBoxItem) killedItem).kill();
            }
        }

        if (killedItem.isAlive()) {
            throw new IllegalStateException("SyncItem is still alive: " + killedItem);
        }

        if (killedItem instanceof SyncBaseItem) {
            // Call before base is deleted
            if (actor != null) {
                statisticsService.onItemKilled((SyncBaseItem) killedItem, actor);
            }
        }


        synchronized (items) {
            if (items.remove(killedItem.getId()) == null) {
                throw new IllegalStateException("Id does not exist: " + killedItem);
            }
            if (killedItem instanceof SyncBaseItem) {
                historyService.addItemDestroyedEntry(actor, (SyncBaseItem) killedItem);
                baseService.onItemDeleted((SyncBaseItem) killedItem, actor);
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
                xpService.onItemKilled(actorBase, (SyncBaseItem) killedItem);
                serverConditionService.onSyncItemKilled(actor, (SyncBaseItem) killedItem);
            }
            serverEnergyService.onBaseItemKilled((SyncBaseItem) killedItem);
            killContainedItems((SyncBaseItem) killedItem, actor);
            inventoryService.onSyncBaseItemKilled((SyncBaseItem) killedItem);
        } else if (killedItem instanceof SyncResourceItem) {
            resourceService.resourceItemDeleted((SyncResourceItem) killedItem);
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
    public List<SyncItem> getItemsCopy() {
        synchronized (items) {
            return new ArrayList<SyncItem>(items.values());
        }
    }

    @Override
    public Collection<SyncItem> getItemsCopyNoBot() {
        Collection<SyncItem> result = new ArrayList<SyncItem>();
        synchronized (items) {
            for (SyncItem syncItem : items.values()) {
                if (syncItem instanceof SyncBaseItem) {
                    SyncBaseItem syncBaseItem = (SyncBaseItem) syncItem;
                    if (!baseService.isBot(syncBaseItem.getBase())) {
                        result.add(syncItem);
                    }
                } else {
                    result.add(syncItem);
                }
            }
        }
        return result;
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
    protected Services getServices() {
        return services;
    }

    @Override
    protected AbstractBaseService getBaseService() {
        return baseService;
    }

    @Override
    @Transactional
    @Secured(SecurityRoles.ROLE_ADMINISTRATOR)
    public void saveDbItemTypes(Collection<DbItemType> itemTypes) {
        HibernateUtil.saveOrUpdateAll(sessionFactory, itemTypes);
    }

    @Override
    @Transactional
    @Secured(SecurityRoles.ROLE_ADMINISTRATOR)
    public void saveAttackMatrix(Collection<DbBaseItemType> weaponDbItemTypes) {
        for (DbBaseItemType weaponDbItemType : weaponDbItemTypes) {
            sessionFactory.getCurrentSession().merge(weaponDbItemType);
        }
    }

    @Override
    @Transactional
    @Secured(SecurityRoles.ROLE_ADMINISTRATOR)
    public void saveDbItemType(DbItemType dbItemType) {
        sessionFactory.getCurrentSession().saveOrUpdate(dbItemType);
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
    @Transactional
    @Secured(SecurityRoles.ROLE_ADMINISTRATOR)
    public void saveWeaponType(int itemTypeId, WeaponType weaponType) throws NoSuchItemTypeException {
        if (weaponType == null) {
            return;
        }
        DbItemType dbItemType = getDbItemType(itemTypeId);
        if (dbItemType == null) {
            throw new NoSuchItemTypeException(itemTypeId);
        }
        if (!(dbItemType instanceof DbBaseItemType)) {
            throw new IllegalArgumentException("Given item type is not instance of a DbBaseItemType: " + dbItemType);
        }

        DbBaseItemType dbBaseItemType = (DbBaseItemType) dbItemType;
        if (dbBaseItemType.getDbWeaponType() == null) {
            throw new IllegalArgumentException("Given item type has no DbWeaponType: " + dbItemType);
        }

        dbBaseItemType.getDbWeaponType().setMuzzleFlashPositions(weaponType.getMuzzleFlashPositions());
        saveDbItemType(dbItemType);
    }

    @Override
    @Transactional
    @Secured(SecurityRoles.ROLE_ADMINISTRATOR)
    public void saveBuildupStepData(int itemTypeId, List<BuildupStep> buildupSteps) throws NoSuchItemTypeException {
        if (buildupSteps == null) {
            return;
        }
        DbItemType dbItemType = getDbItemType(itemTypeId);
        if (dbItemType == null) {
            throw new NoSuchItemTypeException(itemTypeId);
        }
        if (!(dbItemType instanceof DbBaseItemType)) {
            throw new IllegalArgumentException("Given item type is not instance of a DbBaseItemType: " + dbItemType);
        }
        DbBaseItemType dbBaseItemType = (DbBaseItemType) dbItemType;
        Collection<DbBuildupStep> originalBuildupStep = dbBaseItemType.getBuildupStepCrud().readDbChildren();
        if (originalBuildupStep.isEmpty() && buildupSteps.isEmpty()) {
            return;
        } else if (originalBuildupStep.isEmpty() && !buildupSteps.isEmpty()) {
            for (BuildupStep step : buildupSteps) {
                DbBuildupStep dbBuildupStep = dbBaseItemType.getBuildupStepCrud().createDbChild();
                dbBuildupStep.setBuildupStep(step);
            }
        } else if (!originalBuildupStep.isEmpty() && buildupSteps.isEmpty()) {
            dbBaseItemType.getBuildupStepCrud().deleteAllChildren();
        } else {
            // Divide in toBeCreated, toBeDeleted and toBeChanged
            Collection<DbBuildupStep> toBeDeleted = new ArrayList<DbBuildupStep>(originalBuildupStep);
            Collection<BuildupStep> toBeCreated = new ArrayList<BuildupStep>();
            Collection<DbBuildupStep> toBeChanged = new ArrayList<DbBuildupStep>();
            for (BuildupStep buildupStep : buildupSteps) {
                if (buildupStep.getImageId() != null) {
                    moveList(toBeDeleted, toBeChanged, buildupStep.getImageId());
                } else {
                    toBeCreated.add(buildupStep);
                }
            }
            // create
            for (BuildupStep buildupStep : toBeCreated) {
                DbBuildupStep dbBuildupStep = dbBaseItemType.getBuildupStepCrud().createDbChild();
                dbBuildupStep.setBuildupStep(buildupStep);
            }
            // delete
            for (DbBuildupStep dbBuildupStep : toBeDeleted) {
                dbBaseItemType.getBuildupStepCrud().deleteDbChild(dbBuildupStep);
            }
            // change
            for (DbBuildupStep dbBuildupStep : toBeChanged) {
                BuildupStep newBuildupStep = getBuildStep4Id(buildupSteps, dbBuildupStep.getId());
                dbBuildupStep.setFrom(newBuildupStep.getFrom());
                dbBuildupStep.setToExclusive(newBuildupStep.getToExclusive());
            }
        }
        sessionFactory.getCurrentSession().saveOrUpdate(dbBaseItemType);
    }

    private BuildupStep getBuildStep4Id(List<BuildupStep> buildupSteps, int id) {
        for (BuildupStep buildupStep : buildupSteps) {
            if (buildupStep.getImageId() != null && buildupStep.getImageId() == id) {
                return buildupStep;
            }
        }
        throw new IllegalArgumentException("No BuildupStep for id: " + id);
    }

    private void moveList(Collection<DbBuildupStep> removeList, Collection<DbBuildupStep> addList, int imageId) {
        for (Iterator<DbBuildupStep> iterator = removeList.iterator(); iterator.hasNext(); ) {
            DbBuildupStep dbBuildupStep = iterator.next();
            if (dbBuildupStep.getId() == imageId) {
                iterator.remove();
                addList.add(dbBuildupStep);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<DbItemType> getDbItemTypes() {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbItemType.class);
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return criteria.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<DbBaseItemType> getDbBaseItemTypes() {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbBaseItemType.class);
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return criteria.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<DbProjectileItemType> getDbProjectileItemTypes() {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbProjectileItemType.class);
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return criteria.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<DbBaseItemType> getWeaponDbBaseItemTypes() {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbBaseItemType.class);
        criteria.add(Restrictions.isNotNull("dbWeaponType"));
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return criteria.list();
    }

    @Override
    public void activate() {
        Collection<DbItemType> dbItemTypes = getDbItemTypes();
        ArrayList<ItemType> itemTypes = new ArrayList<ItemType>();
        itemTypeSpriteMap.clear();
        buildupStepsImages.clear();
        muzzleItemTypeImages.clear();
        muzzleItemTypeSounds.clear();
        for (DbItemType dbItemType : dbItemTypes) {
            itemTypes.add(dbItemType.createItemType());
            addItemTypeImages(dbItemType);
            addBuildupSteps(dbItemType);
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
        if (itemTypeSpriteMap.containsKey(dbItemType.getId())) {
            throw new IllegalArgumentException("Item Type Images Sprite Map already exits: " + dbItemType);
        }

        if (dbItemType.getItemTypeImageCrud().readDbChildren().size() > 1) {
            addSpriteMapItemTypeImage(dbItemType);
        } else if (dbItemType.getItemTypeImageCrud().readDbChildren().size() == 1) {
            addSingleItemTypeImage(dbItemType);
        } else {
            log.warn("No item type images for: " + dbItemType);
        }
    }

    private void addSpriteMapItemTypeImage(DbItemType dbItemType) {
        try {
            List<DbItemTypeImage> sortedImages = new ArrayList<DbItemTypeImage>(dbItemType.getItemTypeImageCrud().readDbChildren());
            Collections.sort(sortedImages, new Comparator<DbItemTypeImage>() {
                @Override
                public int compare(DbItemTypeImage o1, DbItemTypeImage o2) {
                    return o1.getNumber() - o2.getNumber();
                }
            });
            if (sortedImages.get(0).getData() == null) {
                // Due to test cases
                return;
            }
            BufferedImage masterImage = ImageIO.read(new ByteArrayInputStream(sortedImages.get(0).getData()));

            // Get the format name
            Iterator<ImageReader> iter = ImageIO.getImageReaders(ImageIO.createImageInputStream(new ByteArrayInputStream(sortedImages.get(0).getData())));
            if (!iter.hasNext()) {
                throw new IllegalArgumentException("Can not find image reader: " + dbItemType);
            }
            String formatName = iter.next().getFormatName();

            BufferedImage spriteMap = new BufferedImage(dbItemType.getImageWidth() * sortedImages.size(), dbItemType.getImageHeight(), masterImage.getType());
            int xPos = 0;
            String contentType = sortedImages.get(0).getContentType();
            for (DbItemTypeImage itemTypeImage : sortedImages) {
                BufferedImage image = ImageIO.read(new ByteArrayInputStream(itemTypeImage.getData()));
                boolean done = spriteMap.createGraphics().drawImage(image, xPos, 0, null);
                if (!done) {
                    throw new IllegalStateException("Image could not be drawn: " + dbItemType + " image number: " + itemTypeImage.getNumber());
                }
                xPos += dbItemType.getImageWidth();
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(spriteMap, formatName, outputStream);
            itemTypeSpriteMap.put(dbItemType.getId(), new ImageHolder(outputStream.toByteArray(), contentType));
        } catch (IOException e) {
            log.error("ItemServiceImpl.addSpriteMapItemTypeImage() error with DbItemType: " + dbItemType, e);
        }
    }

    private void addSingleItemTypeImage(DbItemType dbItemType) {
        DbItemTypeImage dbItemTypeImage = CommonJava.getFirst(dbItemType.getItemTypeImageCrud().readDbChildren());
        itemTypeSpriteMap.put(dbItemType.getId(), new ImageHolder(dbItemTypeImage.getData(), dbItemTypeImage.getContentType()));
    }

    private void addBuildupSteps(DbItemType dbItemType) {
        if (buildupStepsImages.containsKey(dbItemType.getId())) {
            throw new IllegalArgumentException("BuildupStep Images already exits: " + dbItemType);
        }
        if (!(dbItemType instanceof DbBaseItemType)) {
            return;
        }
        DbBaseItemType dbBaseItemType = (DbBaseItemType) dbItemType;
        HashMap<Integer, DbBuildupStep> indexBuildupStepMap = new HashMap<Integer, DbBuildupStep>();
        for (DbBuildupStep dbBuildupStep : dbBaseItemType.getBuildupStepCrud().readDbChildren()) {
            if (indexBuildupStepMap.containsKey(dbBuildupStep.getId())) {
                throw new IllegalArgumentException("Buildup Step Image Index already exits: " + dbItemType + " index: " + dbBuildupStep.getId());
            }
            indexBuildupStepMap.put(dbBuildupStep.getId(), dbBuildupStep);
        }
        buildupStepsImages.put(dbItemType.getId(), indexBuildupStepMap);
    }

    private void addMuzzleEffect(DbBaseItemType dbItemType) {
        if (dbItemType.getDbWeaponType() == null) {
            return;
        }
        // Image
        if (muzzleItemTypeImages.containsKey(dbItemType.getId())) {
            throw new IllegalArgumentException("Item Type Images already exits: " + dbItemType);
        }
        Hibernate.initialize(dbItemType.getDbWeaponType().getMuzzleFlashImageData());
        muzzleItemTypeImages.put(dbItemType.getId(), dbItemType.getDbWeaponType().getMuzzleFlashImageData());
        // Sound
        if (muzzleItemTypeSounds.containsKey(dbItemType.getId())) {
            throw new IllegalArgumentException("Item Type sound already exits: " + dbItemType);
        }
        Hibernate.initialize(dbItemType.getDbWeaponType().getMuzzleFlashSoundData());
        muzzleItemTypeSounds.put(dbItemType.getId(), dbItemType.getDbWeaponType().getMuzzleFlashSoundData());
    }

    @Override
    public DbItemType getDbItemType(int itemTypeId) {
        return HibernateUtil.get(sessionFactory, DbItemType.class, itemTypeId);
    }

    @Override
    public DbBaseItemType getDbBaseItemType(int itemBaseTypeId) {
        return HibernateUtil.get(sessionFactory, DbBaseItemType.class, itemBaseTypeId);
    }

    @Override
    public DbResourceItemType getDbResourceItemType(int resourceItemType) {
        return HibernateUtil.get(sessionFactory, DbResourceItemType.class, resourceItemType);
    }

    @Override
    public DbBoxItemType getDbBoxItemType(int boxItemType) {
        return HibernateUtil.get(sessionFactory, DbBoxItemType.class, boxItemType);
    }

    @Override
    @Secured(SecurityRoles.ROLE_ADMINISTRATOR)
    @Transactional
    public void deleteItemType(DbItemType dbItemType) {
        sessionFactory.getCurrentSession().delete(dbItemType);
    }

    @Override
    public DbItemTypeImage getCmsDbItemTypeImage(int itemTypeId) {
        DbItemType dbItemType = getDbItemType(itemTypeId);
        if (dbItemType == null) {
            throw new IllegalArgumentException("DbItemType does not exist: " + itemTypeId);
        }
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbItemTypeImage.class);
        criteria.add(Restrictions.eq("itemType", dbItemType));
        criteria.setProjection(Projections.rowCount());
        int number = BoundingBox.getCosmeticImageIndex(((Number) criteria.list().get(0)).intValue());
        number++; // Number start with 1
        criteria = sessionFactory.getCurrentSession().createCriteria(DbItemTypeImage.class);
        criteria.add(Restrictions.eq("itemType", dbItemType));
        criteria.add(Restrictions.eq("number", number));
        List images = criteria.list();
        if (images.size() != 1) {
            throw new IllegalStateException("Wrong item type image count for: " + dbItemType + " number: " + number + " received: " + images.size());
        }
        return (DbItemTypeImage) images.get(0);
    }


    @Override
    public ImageHolder getItemTypeSpriteMap(int itemTypeId) {
        ImageHolder imageHolder = itemTypeSpriteMap.get(itemTypeId);
        if (imageHolder == null) {
            throw new IllegalArgumentException("Sprite map for item type id does not exist: " + itemTypeId);
        }
        return imageHolder;
    }

    @Override
    public DbBuildupStep getDbBuildupStep(int itemTypeId, int buildupStepId) {
        HashMap<Integer, DbBuildupStep> buildupSteps = this.buildupStepsImages.get(itemTypeId);
        if (buildupSteps == null) {
            throw new IllegalArgumentException("Item Type id does not exist in Buildup Steps: " + itemTypeId);
        }
        DbBuildupStep dbBuildupStep = buildupSteps.get(buildupStepId);
        if (dbBuildupStep == null) {
            throw new IllegalArgumentException("Buildup Step index does not exist: " + buildupStepId + ". ItemTypeId: " + itemTypeId);
        }
        return dbBuildupStep;

    }

    @Override
    public DbItemTypeImageData getMuzzleFlashImage(int itemTypeId) {
        DbItemTypeImageData dbItemTypeImageData = muzzleItemTypeImages.get(itemTypeId);
        if (dbItemTypeImageData == null) {
            throw new IllegalArgumentException("Muzzle image does not exist: " + itemTypeId);
        }
        return dbItemTypeImageData;
    }

    @Override
    public DbItemTypeSoundData getMuzzleFlashSound(int itemTypeId) {
        DbItemTypeSoundData sound = muzzleItemTypeSounds.get(itemTypeId);
        if (sound == null) {
            throw new IllegalArgumentException("Muzzle sound does not exist: " + itemTypeId);
        }
        return sound;
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
