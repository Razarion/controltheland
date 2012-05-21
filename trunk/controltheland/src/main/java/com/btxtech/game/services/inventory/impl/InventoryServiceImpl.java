package com.btxtech.game.services.inventory.impl;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.MathHelper;
import com.btxtech.game.jsre.common.gameengine.itemType.BoxItemType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBoxItem;
import com.btxtech.game.services.collision.CollisionService;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.history.HistoryService;
import com.btxtech.game.services.inventory.DbBoxRegion;
import com.btxtech.game.services.inventory.DbBoxRegionCount;
import com.btxtech.game.services.inventory.DbInventoryArtifact;
import com.btxtech.game.services.inventory.DbInventoryItem;
import com.btxtech.game.services.inventory.InventoryService;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
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

// TODO backup restore
// TODO history convert()

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
    private ItemService itemService;
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private CollisionService collisionService;
    @Autowired
    private HistoryService historyService;
    private final Collection<SyncBoxItem> syncBoxItems = new ArrayList<>();
    private final Map<UserState, Collection<Integer>> userArtifacts = new HashMap<>();
    private ScheduledThreadPoolExecutor boxRegionExecutor;
    private ScheduledFuture boxRegionFuture;
    private Log log = LogFactory.getLog(InventoryServiceImpl.class);
    private final Collection<BoxRegion> boxRegions = new ArrayList<>();

    @PostConstruct
    public void ini() {
        artifactCrud.init(DbInventoryArtifact.class);
        itemCrud.init(DbInventoryItem.class);
        boxRegionCrud.init(DbBoxRegion.class);
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
    public void onSyncBoxItemPicked(SyncBoxItem box, SyncBaseItem picker) {
        itemService.killSyncItem(box, picker.getBase(), true, false);
        synchronized (syncBoxItems) {
            syncBoxItems.remove(box);
        }
        HibernateUtil.openSession4InternalCall(sessionFactory);
        try {
            historyService.addBoxPicked(box, picker);
        } finally {
            HibernateUtil.closeSession4InternalCall(sessionFactory);
        }
        // TODO add artifacts to user
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

    // TODO call from GUI
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

    private void dropRegionBoxes(BoxRegion boxRegion) {
        HibernateUtil.openSession4InternalCall(sessionFactory);
        try {
            DbBoxRegion dbBoxRegion = boxRegionCrud.readDbChild(boxRegion.getDbBoxRegionId());
            for (DbBoxRegionCount dbBoxRegionCount : dbBoxRegion.getBoxRegionCountCrud().readDbChildren()) {
                BoxItemType boxItemType = (BoxItemType) itemService.getItemType(dbBoxRegionCount.getDbBoxItemType());
                Index position = collisionService.getFreeRandomPosition(boxItemType, dbBoxRegion.getRegion(), dbBoxRegion.getItemFreeRange(), true, false);
                for (int i = 0; i < dbBoxRegionCount.getCount(); i++) {
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
        HibernateUtil.openSession4InternalCall(sessionFactory);
        try {
            historyService.addBoxExpired(syncBoxItem);
        } finally {
            HibernateUtil.closeSession4InternalCall(sessionFactory);
        }
    }
}