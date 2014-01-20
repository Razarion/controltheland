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

package com.btxtech.game.services.planet.impl;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.GlobalServices;
import com.btxtech.game.jsre.common.gameengine.services.PlanetServices;
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
import com.btxtech.game.services.common.ServerGlobalServices;
import com.btxtech.game.services.common.ServerPlanetServices;
import com.btxtech.game.services.planet.ServerItemService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: beat
 * Date: Jun 3, 2009
 * Time: 12:59:07 PM
 */
public class ServerItemServiceImpl extends AbstractItemService implements ServerItemService {
    private ServerPlanetServices serverPlanetServices;
    private ServerGlobalServices serverGlobalServices;
    private int lastId = 0;
    private final HashMap<Id, SyncItem> items = new HashMap<>();
    private Log log = LogFactory.getLog(ServerItemServiceImpl.class);

    public void init(ServerPlanetServices planetServices, ServerGlobalServices serverGlobalServices) {
        this.serverPlanetServices = planetServices;
        this.serverGlobalServices = serverGlobalServices;
    }

    @Override
    public SyncItem createSyncObject(ItemType toBeBuilt, Index position, SyncBaseItem creator, SimpleBase base) throws NoSuchItemTypeException, ItemLimitExceededException, HouseSpaceExceededException {
        if (base != null && !serverPlanetServices.getBaseService().isAlive(base)) {
            throw new BaseDoesNotExistException(base);
        }

        SyncItem syncItem;
        synchronized (items) {
            if (toBeBuilt instanceof BaseItemType && !serverPlanetServices.getBaseService().isBot(base) && !serverPlanetServices.getBaseService().isAbandoned(base)) {
                serverPlanetServices.getBaseService().checkItemLimit4ItemAdding((BaseItemType) toBeBuilt, base);
            }
            Id id = createId(creator);
            syncItem = newSyncItem(id, position, toBeBuilt.getId(), base, serverGlobalServices, serverPlanetServices);
            items.put(id, syncItem);
            if (syncItem instanceof SyncBaseItem) {
                serverPlanetServices.getBaseService().onItemCreated((SyncBaseItem) syncItem);
            }
        }

        if (syncItem instanceof SyncBaseObject) {
            serverPlanetServices.getBaseService().sendAccountBaseUpdate((SyncBaseObject) syncItem);
        }

        if (syncItem instanceof SyncTickItem) {
            serverPlanetServices.getActionService().syncItemActivated((SyncTickItem) syncItem);
        }

        if (syncItem instanceof SyncBaseItem) {
            SyncBaseItem syncBaseItem = (SyncBaseItem) syncItem;
            serverGlobalServices.getHistoryService().addItemCreatedEntry(syncBaseItem);
            serverPlanetServices.getActionService().addGuardingBaseItem(syncBaseItem);
            syncItem.addSyncItemListener(serverPlanetServices.getActionService());
            syncItem.addSyncItemListener(serverPlanetServices.getBaseService());
            serverPlanetServices.getActionService().interactionGuardingItems(syncBaseItem);
            serverGlobalServices.getStatisticsService().onItemCreated(syncBaseItem);
        }

        serverPlanetServices.getConnectionService().sendSyncInfo(syncItem);
        if (log.isDebugEnabled()) {
            log.debug("CREATED: " + syncItem);
        }
        return syncItem;
    }

    private Id createId(SyncItem parent) {
        int parentId;
        if (parent != null) {
            parentId = parent.getId().getId();
        } else {
            parentId = Id.NO_ID;
        }

        if (lastId == Integer.MAX_VALUE) {
            throw new IllegalStateException("MAJOR ERROR!!! Number of id exeeded!!!");
        }
        lastId++;
        return new Id(lastId, parentId);
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
                ((SyncBaseItem) killedItem).setKilledBy(actor);
                serverGlobalServices.getStatisticsService().onItemKilled((SyncBaseItem) killedItem, actor);
            }
        }

        killedItem.setExplode(explode);
        if (log.isDebugEnabled()) {
            log.debug("DELETED: " + killedItem);
        }

        if (killedItem instanceof SyncBaseItem) {
            SyncBaseItem killedBaseItem = (SyncBaseItem) killedItem;
            if (actor != null) {
                if (serverPlanetServices.getBaseService().isBot(killedBaseItem.getBase())) {
                    serverPlanetServices.getBotService().onBotItemKilled(killedBaseItem, actor);
                }
            }
            serverGlobalServices.getHistoryService().addItemDestroyedEntry(actor, (SyncBaseItem) killedItem);
        }

        // Send killed item before base is removed -> avoid no base for item error
        serverPlanetServices.getConnectionService().sendSyncInfo(killedItem);
        synchronized (items) {
            if (items.remove(killedItem.getId()) == null) {
                throw new IllegalStateException("Id does not exist: " + killedItem);
            }
            if (killedItem instanceof SyncBaseItem) {
                serverPlanetServices.getBaseService().onItemDeleted((SyncBaseItem) killedItem, actor);
            }
        }

        if (killedItem instanceof SyncBaseItem) {
            SyncBaseItem killedBaseItem = (SyncBaseItem) killedItem;
            serverPlanetServices.getActionService().removeGuardingBaseItem(killedBaseItem);
            if (actor != null) {
                serverGlobalServices.getXpService().onItemKilled(actor, killedBaseItem, serverPlanetServices);
                serverGlobalServices.getConditionService().onSyncItemKilled(actor, killedBaseItem);
                serverPlanetServices.getInventoryService().onSyncBaseItemKilled(killedBaseItem);
            }
            serverPlanetServices.getEnergyService().onBaseItemKilled(killedBaseItem);
            killContainedItems(killedBaseItem, actor);
        } else if (killedItem instanceof SyncResourceItem) {
            serverPlanetServices.getResourceService().resourceItemDeleted((SyncResourceItem) killedItem);
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
                    if (!serverPlanetServices.getBaseService().isBot(syncBaseItem.getBase())) {
                        result.add(syncItem);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public void restore(Collection<SyncBaseObject> syncBaseObjects) {
        int lastId = 0;
        synchronized (items) {
            items.clear();
            for (SyncBaseObject syncBaseObject : syncBaseObjects) {
                if (syncBaseObject instanceof SyncBaseItem) {
                    SyncBaseItem syncBaseItem = (SyncBaseItem) syncBaseObject;
                    syncBaseItem.addSyncItemListener(serverPlanetServices.getActionService());
                    syncBaseItem.addSyncItemListener(serverPlanetServices.getBaseService());
                } else {
                    log.warn("ServerItemServiceImpl.restore() can not restore syncBaseObject: " + syncBaseObject);
                    continue;
                }
                SyncItem syncItem = (SyncItem) syncBaseObject;
                items.put(syncItem.getId(), syncItem);
                lastId = Math.max(syncItem.getId().getId(), lastId);
            }
        }
        this.lastId = lastId + 1;
    }

    @Override
    protected <T> T iterateOverItems(boolean includeNoPosition, boolean includeDead, T defaultReturn, ItemHandler<T> itemHandler) {
        synchronized (items) {
            for (SyncItem syncItem : items.values()) {
                if (!includeDead && !syncItem.isAlive()) {
                    continue;
                }
                if (!includeNoPosition && !syncItem.getSyncItemArea().hasPosition()) {
                    continue;
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
    protected PlanetServices getPlanetServices() {
        return serverPlanetServices;
    }

    @Override
    protected GlobalServices getGlobalServices() {
        return serverGlobalServices;
    }

    @Override
    public void onGuildChanged(final Set<SimpleBase> simpleBases) {
        final Collection<SyncBaseItem> idleAttackItems = new ArrayList<>();
        iterateOverItems(false, false, null, new ItemHandler<Void>() {
            @Override
            public Void handleItem(SyncItem syncItem) {
                if (!(syncItem instanceof SyncBaseItem)) {
                    return null;
                }
                SyncBaseItem syncBaseItem = (SyncBaseItem) syncItem;

                if (!syncBaseItem.hasSyncWeapon()) {
                    return null;
                }

                if (!syncBaseItem.isIdle()) {
                    return null;
                }

                if (simpleBases.contains(syncBaseItem.getBase())) {
                    idleAttackItems.add(syncBaseItem);
                }

                return null;
            }
        });

        serverPlanetServices.getActionService().onGuildChanged(idleAttackItems);
    }

    @Override
    public boolean hasEnemyInRange(final Set<SimpleBase> friendlyBases, final Index middlePoint, final int range) {
        return iterateOverItems(false, false, false, new ItemHandler<Boolean>() {
            @Override
            public Boolean handleItem(SyncItem syncItem) {
                if (syncItem instanceof SyncBaseItem && !friendlyBases.contains(((SyncBaseItem) syncItem).getBase()) && syncItem.getSyncItemArea().getDistance(middlePoint) <= range) {
                    return true;
                }

                return null;
            }
        });
    }
}
