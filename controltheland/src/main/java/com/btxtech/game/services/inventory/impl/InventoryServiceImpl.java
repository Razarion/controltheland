package com.btxtech.game.services.inventory.impl;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.dialogs.inventory.InventoryArtifactInfo;
import com.btxtech.game.jsre.client.dialogs.inventory.InventoryInfo;
import com.btxtech.game.jsre.client.dialogs.inventory.InventoryItemInfo;
import com.btxtech.game.jsre.common.packets.BoxPickedPacket;
import com.btxtech.game.jsre.common.MathHelper;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.BoxItemType;
import com.btxtech.game.jsre.common.gameengine.services.base.HouseSpaceExceededException;
import com.btxtech.game.jsre.common.gameengine.services.base.ItemLimitExceededException;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBoxItem;
import com.btxtech.game.services.base.Base;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.collision.CollisionService;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.connection.ConnectionService;
import com.btxtech.game.services.history.HistoryService;
import com.btxtech.game.services.inventory.DbBoxRegion;
import com.btxtech.game.services.inventory.DbBoxRegionCount;
import com.btxtech.game.services.inventory.DbInventoryArtifact;
import com.btxtech.game.services.inventory.DbInventoryArtifactCount;
import com.btxtech.game.services.inventory.DbInventoryItem;
import com.btxtech.game.services.inventory.InventoryService;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.item.itemType.DbBoxItemType;
import com.btxtech.game.services.item.itemType.DbBoxItemTypePossibility;
import com.btxtech.game.services.terrain.TerrainService;
import com.btxtech.game.services.territory.TerritoryService;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * User: beat
 * Date: 15.05.12
 * Time: 12:48
 */

@Component(value = "inventoryService")
public class InventoryServiceImpl implements InventoryService, Runnable {
    protected static long SCHEDULE_RATE = 60 * 1000;
    @Autowired
    private CrudRootServiceHelper<DbInventoryArtifact> artifactCrud;
    @Autowired
    private CrudRootServiceHelper<DbInventoryItem> itemCrud;
    @Autowired
    private CrudRootServiceHelper<DbBoxRegion> boxRegionCrud;
    @Autowired
    private CrudRootServiceHelper<DbInventoryNewUser> newUserCrud;
    @Autowired
    private ItemService itemService;
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private CollisionService collisionService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private BaseService baseService;
    @Autowired
    private UserService userService;
    @Autowired
    private ConnectionService connectionService;
    @Autowired
    private TerrainService terrainService;
    @Autowired
    private TerritoryService territoryService;
    private final Collection<SyncBoxItem> syncBoxItems = new ArrayList<>();
    private ScheduledThreadPoolExecutor boxRegionExecutor;
    private ScheduledFuture boxRegionFuture;
    private Log log = LogFactory.getLog(InventoryServiceImpl.class);
    private final Collection<BoxRegion> boxRegions = new ArrayList<>();

    @PostConstruct
    public void ini() {
        artifactCrud.init(DbInventoryArtifact.class);
        itemCrud.init(DbInventoryItem.class);
        boxRegionCrud.init(DbBoxRegion.class);
        newUserCrud.init(DbInventoryNewUser.class);
        runTimer();
        HibernateUtil.openSession4InternalCall(sessionFactory);
        try {
            activate();
        } catch (Exception e) {
            log.error("", e);
        } finally {
            HibernateUtil.closeSession4InternalCall(sessionFactory);
        }
    }

    private void runTimer() {
        stopTimer();
        boxRegionExecutor = new ScheduledThreadPoolExecutor(1, new CustomizableThreadFactory("InventoryServiceImpl thread "));
        boxRegionFuture = boxRegionExecutor.scheduleAtFixedRate(this, SCHEDULE_RATE, SCHEDULE_RATE, TimeUnit.MILLISECONDS);
    }

    @PreDestroy
    public void stopTimer() {
        if (boxRegionExecutor != null) {
            boxRegionExecutor.shutdown();
            boxRegionExecutor = null;
        }
        if (boxRegionFuture != null) {
            boxRegionFuture.cancel(false);
            boxRegionFuture = null;
        }
    }


    @Override
    public CrudRootServiceHelper<DbInventoryArtifact> getArtifactCrud() {
        return artifactCrud;
    }

    @Override
    public CrudRootServiceHelper<DbInventoryItem> getItemCrud() {
        return itemCrud;
    }

    @Override
    public CrudRootServiceHelper<DbBoxRegion> getBoxRegionCrud() {
        return boxRegionCrud;
    }

    @Override
    public CrudRootServiceHelper<DbInventoryNewUser> getNewUserCrud() {
        return newUserCrud;
    }

    @Override
    public void onSyncBoxItemPicked(SyncBoxItem box, SyncBaseItem picker) {
        itemService.killSyncItem(box, picker.getBase(), true, false);
        synchronized (syncBoxItems) {
            syncBoxItems.remove(box);
        }
        if (baseService.isAbandoned(picker.getBase())) {
            return;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("You picked up a box! Items added to your Inventory:");
        builder.append("<ul>");
        boolean somethingAdded = false;
        HibernateUtil.openSession4InternalCall(sessionFactory);
        try {
            historyService.addBoxPicked(box, picker);
            DbBoxItemType dbBoxItemType = itemService.getDbBoxItemType(box.getItemType().getId());
            UserState userState = baseService.getUserState(picker.getBase());
            for (DbBoxItemTypePossibility dbBoxItemTypePossibility : dbBoxItemType.getBoxPossibilityCrud().readDbChildren()) {
                if (MathHelper.isRandomPossibility(dbBoxItemTypePossibility.getPossibility())) {
                    addBoxContentToUser(dbBoxItemTypePossibility, userState, builder);
                    somethingAdded = true;
                }
            }
        } finally {
            HibernateUtil.closeSession4InternalCall(sessionFactory);
        }
        if (!somethingAdded) {
            builder.append("<li>No luck. Empty box found</li>");
        }
        builder.append("</ul>");
        BoxPickedPacket boxPickedPacket = new BoxPickedPacket();
        boxPickedPacket.setHtml(builder.toString());
        connectionService.sendPacket(picker.getBase(), boxPickedPacket);
    }

    private void addBoxContentToUser(DbBoxItemTypePossibility dbBoxItemTypePossibility, UserState userState, StringBuilder builder) {
        builder.append("<li>");
        if (dbBoxItemTypePossibility.getDbInventoryItem() != null) {
            userState.addInventoryItem(dbBoxItemTypePossibility.getDbInventoryItem().getId());
            historyService.addInventoryItemFromBox(userState, dbBoxItemTypePossibility.getDbInventoryItem().getName());
            builder.append("Item: ").append(dbBoxItemTypePossibility.getDbInventoryItem().getName());
        } else if (dbBoxItemTypePossibility.getDbInventoryArtifact() != null) {
            userState.addInventoryArtifact(dbBoxItemTypePossibility.getDbInventoryArtifact().getId());
            historyService.addInventoryArtifactFromBox(userState, dbBoxItemTypePossibility.getDbInventoryArtifact().getName());
            builder.append("Artifact: ").append(dbBoxItemTypePossibility.getDbInventoryArtifact().getName());
        } else if (dbBoxItemTypePossibility.getRazarion() != null) {
            userState.addRazarion(dbBoxItemTypePossibility.getRazarion());
            historyService.addRazarionFromBox(userState, dbBoxItemTypePossibility.getRazarion());
            builder.append("Razarion: ").append(dbBoxItemTypePossibility.getRazarion());
        } else {
            log.warn("No content defined for box: " + dbBoxItemTypePossibility.getParent() + " " + dbBoxItemTypePossibility);
        }
        builder.append("</li>");
    }

    @Override
    public void onSyncBaseItemKilled(SyncBaseItem syncBaseItem) {
        if (MathHelper.isRandomPossibility(syncBaseItem.getDropBoxPossibility())) {
            HibernateUtil.openSession4InternalCall(sessionFactory);
            try {
                DbBaseItemType dbBaseItemType = itemService.getDbBaseItemType(syncBaseItem.getBaseItemType().getId());
                BoxItemType boxItemType = (BoxItemType) itemService.getItemType(dbBaseItemType.getDbBoxItemType());
                dropBox(syncBaseItem.getSyncItemArea().getPosition(), boxItemType, syncBaseItem);
            } catch (Exception e) {
                log.error("", e);
            } finally {
                HibernateUtil.closeSession4InternalCall(sessionFactory);
            }
        }
    }

    @Override
    public void activate() {
        stopTimer();
        Collection<DbBoxRegion> dbBoxRegions = boxRegionCrud.readDbChildren();
        synchronized (syncBoxItems) {
            for (SyncBoxItem syncBoxItem : syncBoxItems) {
                if (syncBoxItem.isAlive()) {
                    expireBox(syncBoxItem);
                }
            }
            syncBoxItems.clear();
        }

        synchronized (boxRegions) {
            boxRegions.clear();
            for (DbBoxRegion dbBoxRegion : dbBoxRegions) {
                try {
                    boxRegions.add(new BoxRegion(dbBoxRegion));
                } catch (IllegalArgumentException e) {
                    log.error(e.getMessage());
                }

            }
        }
        runTimer();
    }

    @Override
    public void restore() {
        stopTimer();
        synchronized (syncBoxItems) {
            syncBoxItems.clear();
        }
        activate();
    }

    @Override
    public void run() {
        try {
            synchronized (boxRegions) {
                for (BoxRegion boxRegion : boxRegions) {
                    if (boxRegion.isDropTimeReached()) {
                        dropRegionBoxes(boxRegion);
                        boxRegion.setupNextDropTime();
                    }
                }
            }
            synchronized (syncBoxItems) {
                for (Iterator<SyncBoxItem> iterator = syncBoxItems.iterator(); iterator.hasNext(); ) {
                    SyncBoxItem syncBoxItem = iterator.next();
                    if (!syncBoxItem.isInTTL()) {
                        expireBox(syncBoxItem);
                        iterator.remove();
                    }
                }
            }
        } catch (Throwable throwable) {
            log.error("", throwable);
        }
    }

    @Override
    public void assembleInventoryItem(int inventoryItemId) {
        UserState userState = userService.getUserState();
        DbInventoryItem dbInventoryItem = itemCrud.readDbChild(inventoryItemId);
        if (dbInventoryItem.getArtifactCountCrud().readDbChildren().isEmpty()) {
            throw new IllegalArgumentException("Can not assemble item with no artifacts: " + dbInventoryItem);
        }
        Collection<Integer> artifactIds = new ArrayList<>();
        for (DbInventoryArtifactCount dbInventoryArtifactCount : dbInventoryItem.getArtifactCountCrud().readDbChildren()) {
            for (int count = 0; count < dbInventoryArtifactCount.getCount(); count++) {
                artifactIds.add(dbInventoryArtifactCount.getDbInventoryArtifact().getId());
            }
        }
        if (!userState.removeArtifactIds(artifactIds)) {
            throw new IllegalArgumentException("Can not assemble inventory item: " + dbInventoryItem + " user: " + userState + ". Some inventory artifacts are mission");
        }
        userState.addInventoryItem(dbInventoryItem.getId());
    }

    @Override
    public void useInventoryItem(int inventoryItemId, Collection<Index> positionToBePlaced) throws ItemLimitExceededException, HouseSpaceExceededException, NoSuchItemTypeException {
        DbInventoryItem dbInventoryItem = itemCrud.readDbChild(inventoryItemId);
        UserState userState = userService.getUserState();
        if (!userState.hasInventoryItemId(inventoryItemId)) {
            throw new IllegalArgumentException("User does not have inventory item: " + dbInventoryItem + " user: " + userState);
        }
        Base base = baseService.getBase(userState);
        if (base == null) {
            throw new IllegalStateException("User does not have a base: " + userState);
        }
        if (dbInventoryItem.getDbBaseItemType() != null) {
            if (positionToBePlaced.size() != dbInventoryItem.getBaseItemTypeCount()) {
                throw new IllegalArgumentException("positionToBePlaced.size() != dbInventoryItem.getBaseItemTypeCount() " + positionToBePlaced.size() + " " + dbInventoryItem.getBaseItemTypeCount());
            }
            BaseItemType baseItemType = (BaseItemType) itemService.getItemType(dbInventoryItem.getDbBaseItemType());
            for (Index position : positionToBePlaced) {
                if (!terrainService.isFree(position, baseItemType)) {
                    throw new IllegalArgumentException("Terrain is not free " + position + " " + baseItemType + " " + userState);
                }
                if (!territoryService.isAllowed(position, baseItemType)) {
                    throw new IllegalArgumentException("Item not allowed on territory " + position + " " + baseItemType + " " + userState);
                }
                Rectangle itemRect = baseItemType.getBoundingBox().getRectangle(position);
                if (itemService.hasItemsInRectangle(itemRect)) {
                    throw new IllegalArgumentException("Can not place over other items " + position + " " + baseItemType + " " + userState);
                }
                if (itemService.hasEnemyInRange(base.getSimpleBase(), position, (int) baseItemType.getBoundingBox().getMaxRadiusDouble() + dbInventoryItem.getItemFreeRange())) {
                    throw new IllegalArgumentException("Enemy items too near " + position + " " + baseItemType + " " + userState);
                }
            }
            for (Index position : positionToBePlaced) {
                itemService.createSyncObject(baseItemType, position, null, base.getSimpleBase(), 0);
            }
        } else {
            baseService.depositResource(dbInventoryItem.getGoldAmount(), base.getSimpleBase());
            baseService.sendAccountBaseUpdate(base.getSimpleBase());
        }
        userState.removeInventoryItemId(inventoryItemId);
        historyService.addInventoryItemUsed(userState, dbInventoryItem.getName());
    }

    @Override
    public int buyInventoryItem(int inventoryItemId) {
        DbInventoryItem dbInventoryItem = itemCrud.readDbChild(inventoryItemId);
        UserState userState = userService.getUserState();
        if (dbInventoryItem.getRazarionCoast() == null) {
            throw new IllegalArgumentException("The InventoryItem can not be bought: " + userState + " dbInventoryItem: " + dbInventoryItem);
        }
        if (dbInventoryItem.getRazarionCoast() > userState.getRazarion()) {
            throw new IllegalArgumentException("The user does not have enough razarion to buy the inventory item. User: " + userState + " dbInventoryItem: " + dbInventoryItem + " Razarion: " + userState.getRazarion());
        }
        userState.subRazarion(dbInventoryItem.getRazarionCoast());
        userState.addInventoryItem(dbInventoryItem.getId());
        historyService.addInventoryItemBought(userState, dbInventoryItem.getName(), dbInventoryItem.getRazarionCoast());
        return userState.getRazarion();
    }

    @Override
    public int buyInventoryArtifact(int inventoryArtifactId) {
        DbInventoryArtifact dbInventoryArtifact = artifactCrud.readDbChild(inventoryArtifactId);
        UserState userState = userService.getUserState();
        if (dbInventoryArtifact.getRazarionCoast() == null) {
            throw new IllegalArgumentException("The InventoryArtifact can not be bought: " + userState + " dbInventoryItem: " + dbInventoryArtifact);
        }
        if (dbInventoryArtifact.getRazarionCoast() > userState.getRazarion()) {
            throw new IllegalArgumentException("The user does not have enough razarion to buy the inventory artifact. User: " + userState + " dbInventoryArtifact: " + dbInventoryArtifact + " Razarion: " + userState.getRazarion());
        }
        userState.subRazarion(dbInventoryArtifact.getRazarionCoast());
        userState.addInventoryArtifact(dbInventoryArtifact.getId());
        historyService.addInventoryArtifactBought(userState, dbInventoryArtifact.getName(), dbInventoryArtifact.getRazarionCoast());
        return userState.getRazarion();
    }

    @Override
    public InventoryInfo getInventory() {
        InventoryInfo inventoryInfo = new InventoryInfo();
        UserState userState = userService.getUserState();
        Map<Integer, InventoryArtifactInfo> allArtifacts = getAllInventoryArtifactInfoFromDb();
        Map<Integer, InventoryItemInfo> allItems = getAllInventoryItemsInfoFromDb(allArtifacts);

        // Set razarion
        inventoryInfo.setRazarion(userState.getRazarion());
        // Set all items
        inventoryInfo.setAllInventoryItemInfos(new ArrayList<>(allItems.values())); // new ArrayList() due to GWT problems
        // Set all artifacts
        inventoryInfo.setAllInventoryArtifactInfos(new ArrayList<>(allArtifacts.values())); // new ArrayList() due to GWT problems
        // Set own artifacts
        Map<InventoryArtifactInfo, Integer> ownInventoryArtifacts = new HashMap<>();
        for (Integer artifactId : userState.getInventoryArtifactIds()) {
            InventoryArtifactInfo inventoryArtifactInfo = allArtifacts.get(artifactId);
            if (inventoryArtifactInfo == null) {
                throw new IllegalStateException("InventoryArtifactInfo does not exist: " + artifactId);
            }
            if (ownInventoryArtifacts.containsKey(inventoryArtifactInfo)) {
                continue;
            }
            ownInventoryArtifacts.put(inventoryArtifactInfo, Collections.frequency(userState.getInventoryArtifactIds(), artifactId));
        }
        inventoryInfo.setOwnInventoryArtifacts(ownInventoryArtifacts);
        // Set own items
        Map<InventoryItemInfo, Integer> ownInventoryItems = new HashMap<>();
        for (Integer itemId : userState.getInventoryItemIds()) {
            InventoryItemInfo inventoryItemInfo = allItems.get(itemId);
            if (inventoryItemInfo == null) {
                throw new IllegalStateException("InventoryItemInfo does not exist: " + itemId);
            }
            if (ownInventoryItems.containsKey(inventoryItemInfo)) {
                continue;
            }
            ownInventoryItems.put(inventoryItemInfo, Collections.frequency(userState.getInventoryItemIds(), itemId));
        }
        inventoryInfo.setOwnInventoryItems(ownInventoryItems);

        return inventoryInfo;
    }

    @Override
    public void setupNewUserState(UserState userState) {
        for (DbInventoryNewUser dbInventoryNewUser : newUserCrud.readDbChildren()) {
            if (dbInventoryNewUser.getRazarion() != null) {
                userState.addRazarion(dbInventoryNewUser.getRazarion());
            }
            if (dbInventoryNewUser.getDbInventoryItem() != null) {
                for (int i = 0; i < dbInventoryNewUser.getCount(); i++) {
                    userState.addInventoryItem(dbInventoryNewUser.getDbInventoryItem().getId());
                }
            }
            if (dbInventoryNewUser.getDbInventoryArtifact() != null) {
                for (int i = 0; i < dbInventoryNewUser.getCount(); i++) {
                    userState.addInventoryArtifact(dbInventoryNewUser.getDbInventoryArtifact().getId());
                }
            }
        }
    }

    private void dropRegionBoxes(BoxRegion boxRegion) {
        HibernateUtil.openSession4InternalCall(sessionFactory);
        try {
            DbBoxRegion dbBoxRegion = boxRegionCrud.readDbChild(boxRegion.getDbBoxRegionId());
            for (DbBoxRegionCount dbBoxRegionCount : dbBoxRegion.getBoxRegionCountCrud().readDbChildren()) {
                BoxItemType boxItemType = (BoxItemType) itemService.getItemType(dbBoxRegionCount.getDbBoxItemType());
                for (int i = 0; i < dbBoxRegionCount.getCount(); i++) {
                    Index position = collisionService.getFreeRandomPosition(boxItemType, dbBoxRegion.getRegion(), dbBoxRegion.getItemFreeRange(), true, false);
                    dropBox(position, boxItemType, null);
                }
            }
        } finally {
            HibernateUtil.closeSession4InternalCall(sessionFactory);
        }
    }

    private void dropBox(Index position, BoxItemType boxItemType, SyncBaseItem dropper) {
        try {
            SyncBoxItem syncBoxItem = (SyncBoxItem) itemService.createSyncObject(boxItemType, position, null, null, 0);
            synchronized (syncBoxItems) {
                syncBoxItems.add(syncBoxItem);
            }
            historyService.addBoxDropped(syncBoxItem, position, dropper);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    private void expireBox(SyncBoxItem syncBoxItem) {
        itemService.killSyncItem(syncBoxItem, null, true, false);
        if (HibernateUtil.hasOpenSession(sessionFactory)) {
            historyService.addBoxExpired(syncBoxItem);
        } else {
            HibernateUtil.openSession4InternalCall(sessionFactory);
            try {
                historyService.addBoxExpired(syncBoxItem);
            } finally {
                HibernateUtil.closeSession4InternalCall(sessionFactory);
            }
        }
    }

    private Map<Integer, InventoryArtifactInfo> getAllInventoryArtifactInfoFromDb() {
        Map<Integer, InventoryArtifactInfo> result = new HashMap<>();
        for (DbInventoryArtifact dbInventoryArtifact : artifactCrud.readDbChildren()) {
            result.put(dbInventoryArtifact.getId(), dbInventoryArtifact.generateInventoryArtifactInfo());
        }
        return result;
    }

    private Map<Integer, InventoryItemInfo> getAllInventoryItemsInfoFromDb(Map<Integer, InventoryArtifactInfo> allArtifacts) {
        Map<Integer, InventoryItemInfo> result = new HashMap<>();
        for (DbInventoryItem dbInventoryItem : itemCrud.readDbChildren()) {
            result.put(dbInventoryItem.getId(), dbInventoryItem.generateInventoryItemInfo(allArtifacts));
        }
        return result;
    }
}
