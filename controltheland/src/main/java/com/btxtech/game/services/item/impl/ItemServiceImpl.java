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
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemTypeSpriteMap;
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
import com.btxtech.game.jsre.common.packets.SyncItemInfo;
import com.btxtech.game.jsre.itemtypeeditor.ItemTypeImageInfo;
import com.btxtech.game.services.action.ActionService;
import com.btxtech.game.services.base.Base;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.bot.BotService;
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
import com.btxtech.game.services.item.itemType.DbItemType;
import com.btxtech.game.services.item.itemType.DbItemTypeImage;
import com.btxtech.game.services.item.itemType.DbItemTypeImageData;
import com.btxtech.game.services.item.itemType.DbItemTypeSoundData;
import com.btxtech.game.services.item.itemType.DbProjectileItemType;
import com.btxtech.game.services.item.itemType.DbResourceItemType;
import com.btxtech.game.services.resource.ResourceService;
import com.btxtech.game.services.statistics.StatisticsService;
import com.btxtech.game.services.user.SecurityRoles;
import com.btxtech.game.services.utg.XpService;
import com.btxtech.game.services.utg.condition.ServerConditionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
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
    private InventoryService inventoryService;
    @Autowired
    private ServerConditionService serverConditionService;
    @Autowired
    private CrudRootServiceHelper<DbItemType> dbItemTypeCrud;
    @Autowired
    private StatisticsService statisticsService;
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private BotService botService;
    private int lastId = 0;
    private final HashMap<Id, SyncItem> items = new HashMap<>();
    private Log log = LogFactory.getLog(ItemServiceImpl.class);
    private HashMap<Integer, ImageHolder> itemTypeSpriteMaps = new HashMap<>();
    //private HashMap<Integer, HashMap<Integer, DbBuildupStep>> buildupStepsImages = new HashMap<>();
    private HashMap<Integer, DbItemTypeImageData> muzzleItemTypeImages = new HashMap<>();
    private HashMap<Integer, DbItemTypeSoundData> muzzleItemTypeSounds = new HashMap<>();

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
        HashSet<SyncItemInfo> result = new HashSet<>();
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

        killedItem.setExplode(explode);
        if (log.isDebugEnabled()) {
            log.debug("DELETED: " + killedItem);
        }

        if (killedItem instanceof SyncBaseItem) {
            SyncBaseItem killedBaseItem = (SyncBaseItem) killedItem;
            if (actor != null) {
                if (baseService.isBot(killedBaseItem.getBase())) {
                    botService.onBotItemKilled(killedBaseItem, actor);
                }
            }
            historyService.addItemDestroyedEntry(actor, (SyncBaseItem) killedItem);
        }

        synchronized (items) {
            if (items.remove(killedItem.getId()) == null) {
                throw new IllegalStateException("Id does not exist: " + killedItem);
            }
            if (killedItem instanceof SyncBaseItem) {
                baseService.onItemDeleted((SyncBaseItem) killedItem, actor);
            }
        }
        connectionService.sendSyncInfo(killedItem);

        if (killedItem instanceof SyncBaseItem) {
            SyncBaseItem killedBaseItem = (SyncBaseItem) killedItem;
            actionService.removeGuardingBaseItem(killedBaseItem);
            if (actor != null) {
                Base actorBase = baseService.getBase(actor);
                xpService.onItemKilled(actorBase, killedBaseItem);
                serverConditionService.onSyncItemKilled(actor, killedBaseItem);
                inventoryService.onSyncBaseItemKilled(killedBaseItem);
            }
            serverEnergyService.onBaseItemKilled(killedBaseItem);
            killContainedItems(killedBaseItem, actor);
        } else if (killedItem instanceof SyncResourceItem) {
            resourceService.resourceItemDeleted((SyncResourceItem) killedItem);
        }

    }

    @Override
    public void killSyncItemIds(Collection<Id> itemsToKill) {
        Collection<SyncItem> syncItems = new ArrayList<>();
        for (Id id : itemsToKill) {
            try {
                syncItems.add(getItem(id));
            } catch (ItemDoesNotExistException e) {
                log.error("", e);
            }
        }
        killSyncItems(syncItems);
    }

    @Override
    public List<SyncItem> getItemsCopy() {
        synchronized (items) {
            return new ArrayList<>(items.values());
        }
    }

    @Override
    public Collection<SyncItem> getItems4Backup() {
        Collection<SyncItem> result = new ArrayList<>();
        synchronized (items) {
            for (SyncItem syncItem : items.values()) {
                if (syncItem instanceof SyncBaseItem) {
                    SyncBaseItem syncBaseItem = (SyncBaseItem) syncItem;
                    if (!baseService.isBot(syncBaseItem.getBase())) {
                        result.add(syncItem);
                    }
                } else if (syncItem instanceof SyncBoxItem) {
                    // Do nothing
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
    protected <T> T iterateOverItems(boolean includeNoPosition, T defaultReturn, ItemHandler<T> itemHandler) {
        synchronized (items) {
            for (SyncItem syncItem : items.values()) {
                if (!syncItem.isAlive()) {
                    continue;
                }
                if (!includeNoPosition) {
                    if (!syncItem.getSyncItemArea().hasPosition()) {
                        continue;
                    }
                }
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
    @Transactional
    @Secured(SecurityRoles.ROLE_ADMINISTRATOR)
    public void saveItemTypeProperties(int itemTypeId, BoundingBox boundingBox, ItemTypeSpriteMap itemTypeSpriteMap, WeaponType weaponType, Collection<ItemTypeImageInfo> buildupImages, Collection<ItemTypeImageInfo> runtimeImages, Collection<ItemTypeImageInfo> demolitionImages) throws NoSuchItemTypeException {
        DbItemType dbItemType = getDbItemType(itemTypeId);
        if (dbItemType == null) {
            throw new NoSuchItemTypeException(itemTypeId);
        }
        dbItemType.setBounding(boundingBox);
        dbItemType.setTypeSpriteMap(itemTypeSpriteMap);
        if (dbItemType instanceof DbBaseItemType && ((DbBaseItemType) dbItemType).getDbWeaponType() != null) {
            saveWeaponType(dbItemType, weaponType);
        }
        dbItemType.saveImages(buildupImages, runtimeImages, demolitionImages);
        saveDbItemType(dbItemType);
    }

    private void saveWeaponType(DbItemType dbItemType, WeaponType weaponType) throws NoSuchItemTypeException {
        if (!(dbItemType instanceof DbBaseItemType)) {
            throw new IllegalArgumentException("Given item type is not instance of a DbBaseItemType: " + dbItemType);
        }

        DbBaseItemType dbBaseItemType = (DbBaseItemType) dbItemType;
        if (dbBaseItemType.getDbWeaponType() == null) {
            throw new IllegalArgumentException("Given item type has no DbWeaponType: " + dbItemType);
        }

        dbBaseItemType.getDbWeaponType().setMuzzleFlashPositions(weaponType.getMuzzleFlashPositions());
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
        ArrayList<ItemType> itemTypes = new ArrayList<>();
        itemTypeSpriteMaps.clear();
        //buildupStepsImages.clear();
        muzzleItemTypeImages.clear();
        muzzleItemTypeSounds.clear();
        for (DbItemType dbItemType : dbItemTypes) {
            try {
                ItemType itemType = dbItemType.createItemType();
                itemTypes.add(itemType);
                addItemTypeImages(dbItemType, itemType);
                if (dbItemType instanceof DbBaseItemType) {
                    addMuzzleEffect((DbBaseItemType) dbItemType);
                }
            } catch (RuntimeException e) {
                log.error("Can not activate item type: " + dbItemType.getName() + " id: " + dbItemType.getId());
                throw e;
            }
        }
        synchronize(itemTypes);
    }

    private void synchronize(Collection<ItemType> itemTypes) {
        ArrayList<ItemType> newItems = new ArrayList<>(itemTypes);
        newItems.removeAll(getItemTypes());
        ArrayList<ItemType> removedItems = new ArrayList<>(getItemTypes());
        removedItems.removeAll(itemTypes);
        ArrayList<ItemType> changingItems = new ArrayList<>(itemTypes);
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

    private void addItemTypeImages(DbItemType dbItemType, ItemType itemType) {
        try {
            List<DbItemTypeImage> allImages = new ArrayList<>(dbItemType.getItemTypeImageCrud().readDbChildren());
            List<DbItemTypeImage> buildup = new ArrayList<>();
            List<DbItemTypeImage> runtime = new ArrayList<>();
            List<DbItemTypeImage> demolition = new ArrayList<>();
            DbItemTypeImage exampleImage = null;

            for (DbItemTypeImage image : allImages) {
                if (exampleImage == null && image.getData() != null) {
                    exampleImage = image;
                }
                if (image.getType() == null) {
                    log.warn("ItemServiceImpl.addSpriteMapItemTypeImage() type in null: " + image.getType() + " id: " + image.getId());
                    continue;
                }
                switch (image.getType()) {
                    case BUILD_UP:
                        buildup.add(image);
                        break;
                    case RUN_TIME:
                        runtime.add(image);
                        break;
                    case DEMOLITION:
                        demolition.add(image);
                        break;
                    default:
                        log.warn("ItemServiceImpl.addSpriteMapItemTypeImage() unknown type in DbItemType: " + image.getType() + " id: " + image.getId());
                }
            }

            if (exampleImage == null) {
                log.warn("ItemServiceImpl.addSpriteMapItemTypeImage() not valid item type image for: " + dbItemType.getName() + " " + dbItemType.getId());
                return;
            }

            Collections.sort(buildup, new Comparator<DbItemTypeImage>() {
                @Override
                public int compare(DbItemTypeImage o1, DbItemTypeImage o2) {
                    if (o1.getStep() != o2.getStep()) {
                        return Integer.compare(o1.getStep(), o2.getStep());
                    } else {
                        return Integer.compare(o1.getFrame(), o2.getFrame());
                    }
                }
            });
            Collections.sort(runtime, new Comparator<DbItemTypeImage>() {
                @Override
                public int compare(DbItemTypeImage o1, DbItemTypeImage o2) {
                    if (o1.getAngelIndex() != o2.getAngelIndex()) {
                        return Integer.compare(o1.getAngelIndex(), o2.getAngelIndex());
                    } else {
                        return Integer.compare(o1.getFrame(), o2.getFrame());
                    }
                }
            });
            Collections.sort(demolition, new Comparator<DbItemTypeImage>() {
                @Override
                public int compare(DbItemTypeImage o1, DbItemTypeImage o2) {
                    if (o1.getAngelIndex() != o2.getAngelIndex()) {
                        return Integer.compare(o1.getAngelIndex(), o2.getAngelIndex());
                    } else {
                        if (o1.getStep() != o2.getStep()) {
                            return Integer.compare(o1.getStep(), o2.getStep());
                        } else {
                            return Integer.compare(o1.getFrame(), o2.getFrame());
                        }
                    }
                }
            });
            BufferedImage masterImage = ImageIO.read(new ByteArrayInputStream(exampleImage.getData()));

            // Get the format name
            Iterator<ImageReader> iter = ImageIO.getImageReaders(ImageIO.createImageInputStream(new ByteArrayInputStream(exampleImage.getData())));
            if (!iter.hasNext()) {
                throw new IllegalArgumentException("Can not find image reader: " + dbItemType);
            }
            String formatName = iter.next().getFormatName();

            ItemTypeSpriteMap itemTypeSpriteMap = itemType.getItemTypeSpriteMap();
            int totalImageCount = itemTypeSpriteMap.getBuildupSteps() * itemTypeSpriteMap.getBuildupAnimationFrames();
            totalImageCount += itemType.getBoundingBox().getAngelCount() * itemTypeSpriteMap.getRuntimeAnimationFrames();
            totalImageCount += itemType.getBoundingBox().getAngelCount() * itemTypeSpriteMap.getDemolitionSteps() * itemTypeSpriteMap.getDemolitionAnimationFrames();
            BufferedImage spriteMap = new BufferedImage(dbItemType.getImageWidth() * totalImageCount, dbItemType.getImageHeight(), masterImage.getType());
            int xPos = 0;
            String contentType = exampleImage.getContentType();
            for (DbItemTypeImage dbItemTypeImage : buildup) {
                BufferedImage image = ImageIO.read(new ByteArrayInputStream(dbItemTypeImage.getData()));
                boolean done = spriteMap.createGraphics().drawImage(image, xPos, 0, null);
                if (!done) {
                    throw new IllegalStateException("Buildup image could not be drawn: " + dbItemType + " image number: " + dbItemTypeImage.getId());
                }
                xPos += dbItemType.getImageWidth();
            }
            for (DbItemTypeImage dbItemTypeImage : runtime) {
                BufferedImage image = ImageIO.read(new ByteArrayInputStream(dbItemTypeImage.getData()));
                boolean done = spriteMap.createGraphics().drawImage(image, xPos, 0, null);
                if (!done) {
                    throw new IllegalStateException("Runtime image could not be drawn: " + dbItemType + " image number: " + dbItemTypeImage.getId());
                }
                xPos += dbItemType.getImageWidth();
            }
            for (DbItemTypeImage dbItemTypeImage : demolition) {
                BufferedImage image = ImageIO.read(new ByteArrayInputStream(dbItemTypeImage.getData()));
                boolean done = spriteMap.createGraphics().drawImage(image, xPos, 0, null);
                if (!done) {
                    throw new IllegalStateException("Demolition image could not be drawn: " + dbItemType + " image number: " + dbItemTypeImage.getId());
                }
                xPos += dbItemType.getImageWidth();
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(spriteMap, formatName, outputStream);
            itemTypeSpriteMaps.put(dbItemType.getId(), new ImageHolder(outputStream.toByteArray(), contentType));
        } catch (Exception e) {
            log.error("ItemServiceImpl.addSpriteMapItemTypeImage() error with DbItemType: " + dbItemType, e);
        }
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
        ItemType itemType;
        try {
            itemType = getItemType(itemTypeId);
        } catch (NoSuchItemTypeException e) {
            throw new RuntimeException(e);
        }
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbItemTypeImage.class);
        criteria.add(Restrictions.eq("itemType", dbItemType));
        criteria.add(Restrictions.eq("type", ItemTypeSpriteMap.SyncObjectState.RUN_TIME));
        criteria.add(Restrictions.eq("angelIndex", itemType.getBoundingBox().getCosmeticAngelIndex()));
        criteria.add(Restrictions.eq("frame", 0));
        criteria.add(Restrictions.eq("step", 0));
        List images = criteria.list();
        if (images.size() != 1) {
            throw new IllegalStateException("Wrong item type image count for: " + dbItemType + " received: " + images.size());
        }
        return (DbItemTypeImage) images.get(0);
    }

    @Override
    public ImageHolder getItemTypeSpriteMap(int itemTypeId) {
        ImageHolder imageHolder = itemTypeSpriteMaps.get(itemTypeId);
        if (imageHolder == null) {
            throw new IllegalArgumentException("Sprite map for item type id does not exist: " + itemTypeId);
        }
        return imageHolder;
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
