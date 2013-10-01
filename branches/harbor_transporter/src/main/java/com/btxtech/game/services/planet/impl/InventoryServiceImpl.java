package com.btxtech.game.services.planet.impl;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.dialogs.inventory.InventoryItemPlacerChecker;
import com.btxtech.game.jsre.common.MathHelper;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.BoxItemType;
import com.btxtech.game.jsre.common.gameengine.services.base.HouseSpaceExceededException;
import com.btxtech.game.jsre.common.gameengine.services.base.ItemLimitExceededException;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBoxItem;
import com.btxtech.game.services.common.ServerGlobalServices;
import com.btxtech.game.services.common.ServerPlanetServices;
import com.btxtech.game.services.planet.db.DbBoxRegion;
import com.btxtech.game.services.planet.db.DbBoxRegionCount;
import com.btxtech.game.services.inventory.DbInventoryItem;
import com.btxtech.game.services.inventory.impl.BoxRegion;
import com.btxtech.game.services.planet.InventoryService;
import com.btxtech.game.services.planet.db.DbPlanet;
import com.btxtech.game.services.user.UserState;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * User: beat
 * Date: 15.05.12
 * Time: 12:48
 */

public class InventoryServiceImpl implements InventoryService, Runnable {
    private static long SCHEDULE_RATE = 60 * 1000; // Overridden in tests
    private ServerPlanetServices serverPlanetServices;
    private ServerGlobalServices serverGlobalServices;
    private final Collection<SyncBoxItem> syncBoxItems = new ArrayList<>();
    private ScheduledThreadPoolExecutor boxRegionExecutor;
    private ScheduledFuture boxRegionFuture;
    private Log log = LogFactory.getLog(InventoryServiceImpl.class);
    private final Collection<BoxRegion> boxRegions = new ArrayList<>();

    public void init(ServerPlanetServices planetServices, ServerGlobalServices serverGlobalServices) {
        this.serverPlanetServices = planetServices;
        this.serverGlobalServices = serverGlobalServices;
    }

    private void runTimer() {
        boxRegionExecutor = new ScheduledThreadPoolExecutor(1, new CustomizableThreadFactory("GlobalInventoryServiceImpl thread "));
        boxRegionFuture = boxRegionExecutor.scheduleAtFixedRate(this, SCHEDULE_RATE, SCHEDULE_RATE, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onSyncBoxItemPicked(SyncBoxItem box, SyncBaseItem picker) {
        serverPlanetServices.getItemService().killSyncItem(box, picker.getBase(), true, false);
        synchronized (syncBoxItems) {
            syncBoxItems.remove(box);
        }
        if (serverPlanetServices.getBaseService().isAbandoned(picker.getBase())) {
            return;
        }

        serverGlobalServices.getGlobalInventoryService().onSyncBoxItemPicked(box, picker);
    }

    @Override
    public void onSyncBaseItemKilled(SyncBaseItem syncBaseItem) {
        if (MathHelper.isRandomPossibility(syncBaseItem.getDropBoxPossibility())) {
            try {
                BoxItemType boxItemType = serverGlobalServices.getGlobalInventoryService().getDropBox4ItemType(syncBaseItem.getBaseItemType());
                if (boxItemType == null) {
                    throw new IllegalArgumentException("Drop boy possibility is > 0 but no drop box type configured: " + syncBaseItem.getBaseItemType());
                }
                dropBox(syncBaseItem.getSyncItemArea().getPosition(), boxItemType, syncBaseItem);
            } catch (Exception e) {
                log.error("", e);
            }
        }
    }

    @Override
    public void activate(DbPlanet dbPlanet) {
        deactivate();
        Collection<DbBoxRegion> dbBoxRegions = dbPlanet.getBoxRegionCrud().readDbChildren();
        synchronized (boxRegions) {
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
    public void deactivate() {
        if (boxRegionExecutor != null) {
            boxRegionExecutor.shutdown();
            boxRegionExecutor = null;
        }
        if (boxRegionFuture != null) {
            boxRegionFuture.cancel(false);
            boxRegionFuture = null;
        }
        synchronized (syncBoxItems) {
            for (SyncBoxItem syncBoxItem : syncBoxItems) {
                if (syncBoxItem.isAlive()) {
                    try {
                        expireBox(syncBoxItem);
                    } catch (Exception ignore) {
                        // Ignore
                    }
                }
            }
            syncBoxItems.clear();
        }
        synchronized (boxRegions) {
            boxRegions.clear();
        }
    }

    @Override
    public void reactivate(DbPlanet dbPlanet) {
        deactivate();
        activate(dbPlanet);
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
    public void useInventoryItem(UserState userState, SimpleBase simpleBase, DbInventoryItem dbInventoryItem, Collection<Index> positionToBePlaced) throws ItemLimitExceededException, HouseSpaceExceededException, NoSuchItemTypeException {
        if (dbInventoryItem.getDbBaseItemType() != null) {
            if (positionToBePlaced.size() != dbInventoryItem.getBaseItemTypeCount()) {
                throw new IllegalArgumentException("positionToBePlaced.size() != dbInventoryItem.getBaseItemTypeCount() " + positionToBePlaced.size() + " " + dbInventoryItem.getBaseItemTypeCount());
            }
            BaseItemType baseItemType = (BaseItemType) serverGlobalServices.getItemTypeService().getItemType(dbInventoryItem.getDbBaseItemType());
            InventoryItemPlacerChecker inventoryItemPlacerChecker = new InventoryItemPlacerChecker(baseItemType, dbInventoryItem.getBaseItemTypeCount(), dbInventoryItem.getItemFreeRange(), simpleBase, serverPlanetServices);
            inventoryItemPlacerChecker.check(positionToBePlaced);

            if (!inventoryItemPlacerChecker.isEnemiesOk()) {
                throw new IllegalArgumentException("Enemy items too near " + baseItemType + " " + userState);
            }
            if (!inventoryItemPlacerChecker.isItemsOk()) {
                throw new IllegalArgumentException("Can not place over other items " + baseItemType + " " + userState);
            }
            if (!inventoryItemPlacerChecker.isTerrainOk()) {
                throw new IllegalArgumentException("Terrain is not free " + baseItemType + " " + userState);
            }

            for (Index position : positionToBePlaced) {
                SyncBaseItem syncBaseItem = (SyncBaseItem) serverPlanetServices.getItemService().createSyncObject(baseItemType, position, null, simpleBase);
                syncBaseItem.setBuildup(1.0);
            }
        } else {
            serverPlanetServices.getBaseService().depositResource(dbInventoryItem.getGoldAmount(), simpleBase);
            serverPlanetServices.getBaseService().sendAccountBaseUpdate(simpleBase);
        }
    }

    private void dropRegionBoxes(BoxRegion boxRegion) {
        DbPlanet dbPlanet = serverGlobalServices.getPlanetSystemService().openDbSession(serverPlanetServices.getPlanetInfo());
        try {
            DbBoxRegion dbBoxRegion = dbPlanet.getBoxRegionCrud().readDbChild(boxRegion.getDbBoxRegionId());
            for (DbBoxRegionCount dbBoxRegionCount : dbBoxRegion.getBoxRegionCountCrud().readDbChildren()) {
                BoxItemType boxItemType = (BoxItemType) serverGlobalServices.getItemTypeService().getItemType(dbBoxRegionCount.getDbBoxItemType());
                for (int i = 0; i < dbBoxRegionCount.getCount(); i++) {
                    Index position = serverPlanetServices.getCollisionService().getFreeRandomPosition(boxItemType, dbBoxRegion.getRegion().createRegion(), dbBoxRegion.getItemFreeRange(), true, false);
                    dropBox(position, boxItemType, null);
                }
            }
        } finally {
            serverGlobalServices.getPlanetSystemService().closeDbSession();
        }
    }

    private void dropBox(Index position, BoxItemType boxItemType, SyncBaseItem dropper) {
        try {
            SyncBoxItem syncBoxItem = (SyncBoxItem) serverPlanetServices.getItemService().createSyncObject(boxItemType, position, null, null);
            synchronized (syncBoxItems) {
                syncBoxItems.add(syncBoxItem);
            }
            serverGlobalServices.getHistoryService().addBoxDropped(syncBoxItem, position, dropper);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    private void expireBox(SyncBoxItem syncBoxItem) {
        serverPlanetServices.getItemService().killSyncItem(syncBoxItem, null, true, false);
        serverGlobalServices.getHistoryService().addBoxExpired(syncBoxItem);
    }
}
