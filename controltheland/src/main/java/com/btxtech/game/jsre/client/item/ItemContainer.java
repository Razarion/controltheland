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

package com.btxtech.game.jsre.client.item;

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.ClientEnergyService;
import com.btxtech.game.jsre.client.ClientExceptionHandler;
import com.btxtech.game.jsre.client.ClientGlobalServices;
import com.btxtech.game.jsre.client.ClientPlanetServices;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.GameEngineMode;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.SoundHandler;
import com.btxtech.game.jsre.client.action.ActionHandler;
import com.btxtech.game.jsre.client.bot.ClientBotService;
import com.btxtech.game.jsre.client.cockpit.SelectionHandler;
import com.btxtech.game.jsre.client.cockpit.radar.RadarPanel;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.effects.AttackVisualization;
import com.btxtech.game.jsre.client.effects.ExplosionHandler;
import com.btxtech.game.jsre.client.simulation.SimulationConditionServiceImpl;
import com.btxtech.game.jsre.client.utg.ClientDeadEndProtection;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.GlobalServices;
import com.btxtech.game.jsre.common.gameengine.services.PlanetServices;
import com.btxtech.game.jsre.common.gameengine.services.base.HouseSpaceExceededException;
import com.btxtech.game.jsre.common.gameengine.services.base.ItemLimitExceededException;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.services.items.impl.AbstractItemService;
import com.btxtech.game.jsre.common.gameengine.services.items.impl.ItemHandler;
import com.btxtech.game.jsre.common.gameengine.syncObjects.ActiveProjectileGroup;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItemListener;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncResourceItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncTickItem;
import com.btxtech.game.jsre.common.packets.SyncItemInfo;
import com.btxtech.game.jsre.common.perfmon.PerfmonEnum;
import com.btxtech.game.jsre.common.perfmon.TimerPerfmon;
import com.btxtech.game.jsre.common.tutorial.ItemTypeAndPosition;
import com.google.gwt.user.client.Timer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: Jul 4, 2009
 * Time: 12:26:56 PM
 */
public class ItemContainer extends AbstractItemService implements SyncItemListener {
    public static final int CLEANUP_INTERVALL = 3000;
    private static final ItemContainer INSATNCE = new ItemContainer();
    private HashMap<Id, SyncItem> items = new HashMap<Id, SyncItem>();
    private HashMap<Id, SyncItem> seeminglyDeadItems = new HashMap<Id, SyncItem>();
    private int itemId = 1;
    private static Logger log = Logger.getLogger(ItemContainer.class.getName());

    /**
     * Singleton
     */
    private ItemContainer() {
        Timer timer = new TimerPerfmon(PerfmonEnum.ITEM_CONTAINER) {
            @Override
            public void runPerfmon() {
                for (Iterator<Map.Entry<Id, SyncItem>> it = seeminglyDeadItems.entrySet().iterator(); it.hasNext(); ) {
                    Map.Entry<Id, SyncItem> entry = it.next();
                    long insertTime = entry.getKey().getUserTimeStamp();
                    if (insertTime + CLEANUP_INTERVALL < System.currentTimeMillis()) {
                        it.remove();
                        GwtCommon.sendDebug(GwtCommon.DEBUG_CATEGORY_DEFINITELY_KILL, "Can not definitely kill item due to missing ack from server: " + entry.getKey() + " " + entry.getValue());
                    }

                }
            }
        };
        timer.scheduleRepeating(CLEANUP_INTERVALL);
    }


    public void doSynchronize(Collection<SyncItemInfo> syncItemInfo) {
        for (SyncItemInfo itemInfo : syncItemInfo) {
            try {
                addSynchronize(itemInfo);
            } catch (Exception e) {
                ClientExceptionHandler.handleException("ItemContainer.doSynchronize() in addSynchronize", e);
            }
        }
        for (SyncItemInfo itemInfo : syncItemInfo) {
            try {
                synchronize(itemInfo);
            } catch (Exception e) {
                ClientExceptionHandler.handleException("ItemContainer.doSynchronize() in synchronize", e);
            }
        }
        for (SyncItemInfo itemInfo : syncItemInfo) {
            try {
                removeSynchronize(itemInfo);
            } catch (Exception e) {
                ClientExceptionHandler.handleException("ItemContainer.doSynchronize() in removeSynchronize", e);
            }
        }
    }

    private void addSynchronize(SyncItemInfo syncItemInfo) throws NoSuchItemTypeException {
        SyncItem syncItem = items.get(syncItemInfo.getId());
        if (syncItem != null) {
            return;
        }
        syncItem = createAndAddItem(syncItemInfo.getId(), syncItemInfo.getPosition(), syncItemInfo.getItemTypeId(), syncItemInfo.getBase());
        if (syncItem instanceof SyncBaseItem) {
            ClientBase.getInstance().onItemCreated((SyncBaseItem) syncItem);
            if (ClientBase.getInstance().isMyOwnProperty((SyncBaseItem) syncItem)) {
                ClientDeadEndProtection.getInstance().onSyncItemCreated((SyncBaseItem) syncItem);
            }
        }
    }

    private void removeSynchronize(SyncItemInfo syncItemInfo) throws NoSuchItemTypeException {
        SyncItem syncItem = items.get(syncItemInfo.getId());
        if (syncItem == null) {
            return;
        }
        if (!syncItemInfo.isAlive()) {
            definitelyKillItem(syncItem, true, syncItemInfo.isExplode(), syncItemInfo.getKilledBy());
        }
    }

    private void synchronize(SyncItemInfo syncItemInfo) throws NoSuchItemTypeException, ItemDoesNotExistException {
        SyncItem syncItem = items.get(syncItemInfo.getId());
        if (syncItem == null) {
            throw new IllegalStateException("SyncItem is null syncItemInfo: " + syncItemInfo);
        }

        // Check for  Teleportation effect
        Index localPos = syncItem.getSyncItemArea().getPosition();
        Index syncPos = syncItemInfo.getPosition();
        if (localPos != null && syncPos != null) {
            int distance = localPos.getDistance(syncPos);
            if (distance > 200) {
                GwtCommon.sendDebug(GwtCommon.DEBUG_CATEGORY_TELEPORTATION, "Teleportation detected. Distance: " + distance + " Info:" + syncItemInfo + " | Item:" + syncItem);
            }
        }

        syncItem.synchronize(syncItemInfo);
        checkSpecialChanged(syncItem);
        if (syncItem instanceof SyncTickItem) {
            ActionHandler.getInstance().syncItemActivated((SyncTickItem) syncItem);
        }
    }

    @Override
    public SyncItem createSyncObject(ItemType toBeBuilt, Index position, SyncBaseItem creator, SimpleBase base) throws NoSuchItemTypeException, ItemLimitExceededException, HouseSpaceExceededException {
        if (Connection.getInstance().getGameEngineMode() != GameEngineMode.MASTER) {
            throw new IllegalStateException("ItemContainer.createSyncObject() should not be called if game engine mode is not master: " + Connection.getInstance().getGameEngineMode());
        }

        if (toBeBuilt instanceof BaseItemType
                && ClientBase.getInstance().isMyOwnBase(base)
                && !ClientBase.getInstance().isBot(base)
                && (Connection.getInstance().getGameEngineMode() == GameEngineMode.SLAVE || Connection.getInstance().getGameEngineMode() == GameEngineMode.MASTER)) {
            ClientBase.getInstance().checkItemLimit4ItemAdding((BaseItemType) toBeBuilt);
        }
        SyncItem syncItem;
        int parentId = Id.NO_ID;
        if (creator != null) {
            parentId = creator.getId().getId();
        }
        Id id = createId(parentId);
        syncItem = createAndAddItem(id, position, toBeBuilt.getId(), base);
        id.setUserTimeStamp(System.currentTimeMillis());
        if (syncItem instanceof SyncBaseItem) {
            SyncBaseItem syncBaseItem = (SyncBaseItem) syncItem;
            ActionHandler.getInstance().addGuardingBaseItem(syncBaseItem);
            ActionHandler.getInstance().interactionGuardingItems(syncBaseItem);
            ClientBase.getInstance().onItemCreated(syncBaseItem);
            if (ClientBase.getInstance().isMyOwnProperty((SyncBaseItem) syncItem)) {
                ClientDeadEndProtection.getInstance().onSyncItemCreated((SyncBaseItem) syncItem);
            }
        }
        Connection.getInstance().sendSyncInfo(syncItem);
        return syncItem;
    }

    private Id createId(int parentId) {
        itemId++;
        return new Id(itemId, parentId);
    }

    public SyncBaseItem getSimulationItem(int intId) {
        for (Map.Entry<Id, SyncItem> entry : items.entrySet()) {
            if (entry.getKey().getId() == intId) {
                return (SyncBaseItem) entry.getValue();
            }
        }
        throw new IllegalArgumentException(this + " getSimulationItem(): no SyncItem for id: " + intId);
    }

    public SyncItem createSimulationSyncObject(ItemTypeAndPosition itemTypeAndPosition) throws NoSuchItemTypeException {
        Id id = createId(Id.SIMULATION_ID);
        if (items.containsKey(id)) {
            throw new IllegalStateException(this + " simulated id is already used: " + id);
        }
        SimpleBase simpleBase = null;
        if (ItemTypeContainer.getInstance().getItemType(itemTypeAndPosition.getItemTypeId()) instanceof BaseItemType) {
            simpleBase = ClientBase.getInstance().getSimpleBase();
        }
        SyncItem syncItem = createAndAddItem(id, itemTypeAndPosition.getPosition(), itemTypeAndPosition.getItemTypeId(), simpleBase);
        id.setUserTimeStamp(System.currentTimeMillis());
        if (syncItem instanceof SyncBaseItem) {
            SyncBaseItem syncBaseItem = (SyncBaseItem) syncItem;
            syncBaseItem.setBuildup(1.0);
            syncBaseItem.getSyncItemArea().setAngel(itemTypeAndPosition.getAngel());
            syncBaseItem.fireItemChanged(SyncItemListener.Change.ANGEL, null);
            ClientBase.getInstance().onItemCreated(syncBaseItem);
        }
        Connection.getInstance().sendSyncInfo(syncItem);
        return syncItem;
    }

    public SyncItem createItemTypeEditorSyncObject(SimpleBase simpleBase, int itemTypeId, Index position) throws NoSuchItemTypeException {
        Id id = createId(Id.SIMULATION_ID);
        if (items.containsKey(id)) {
            throw new IllegalStateException(this + " simulated id is already used: " + id);
        }
        SyncItem syncItem = createAndAddItem(id, position, itemTypeId, simpleBase);
        id.setUserTimeStamp(System.currentTimeMillis());
        if (syncItem instanceof SyncBaseItem) {
            SyncBaseItem syncBaseItem = (SyncBaseItem) syncItem;
            syncBaseItem.setBuildup(1.0);
        }
        return syncItem;
    }

    private SyncItem createAndAddItem(Id id, Index position, int itemTypeId, SimpleBase base) throws NoSuchItemTypeException {
        SyncItem syncItem = newSyncItem(id, position, itemTypeId, base, ClientGlobalServices.getInstance(), ClientPlanetServices.getInstance());
        syncItem.addSyncItemListener(this);
        items.put(id, syncItem);
        return syncItem;
    }

    @Override
    public void killSyncItem(SyncItem killedItem, SimpleBase actor, boolean force, boolean explode) {
        if (!items.containsKey(killedItem.getId())) {
            throw new IllegalStateException("No SyncItem for: " + killedItem);
        }
        if (Connection.getInstance().getGameEngineMode() == GameEngineMode.MASTER) {
            if (killedItem instanceof SyncBaseItem) {
                ((SyncBaseItem) killedItem).setKilledBy(actor);
            }
            definitelyKillItem(killedItem, force, explode, actor);
            if (killedItem instanceof SyncBaseItem) {
                SyncBaseItem syncBaseItem = (SyncBaseItem) killedItem;
                ActionHandler.getInstance().removeGuardingBaseItem(syncBaseItem);
                ClientEnergyService.getInstance().onSyncItemKilled(syncBaseItem);
                SimulationConditionServiceImpl.getInstance().onSyncItemKilled(actor, (SyncBaseItem) killedItem);
            }
            Connection.getInstance().sendSyncInfo(killedItem);
            if (killedItem instanceof SyncBaseItem) {
                killContainedItems((SyncBaseItem) killedItem, actor);
            }
        } else {
            makeItemSeeminglyDead(killedItem, actor);
        }
    }

    private void makeItemSeeminglyDead(SyncItem syncItem, SimpleBase actor) {
        if (items.containsKey(syncItem.getId())) {
            syncItem.getId().setUserTimeStamp(System.currentTimeMillis());
            seeminglyDeadItems.put(syncItem.getId(), syncItem);
        } else {
            log.severe("This should never happen: ItemContainer.killSyncItem() syncItem:" + syncItem + " actor:" + actor);
        }
    }

    private void definitelyKillItem(SyncItem syncItem, boolean force, boolean explode, SimpleBase actor) {
        if (force) {
            if (syncItem instanceof SyncBaseItem) {
                ((SyncBaseItem) syncItem).setHealth(0);
            } else if (syncItem instanceof SyncResourceItem) {
                ((SyncResourceItem) syncItem).setAmount(0);
            }
        }
        if (explode) {
            ExplosionHandler.getInstance().onExplosion(syncItem);
        }
        items.remove(syncItem.getId());
        if (syncItem instanceof SyncBaseItem) {
            SyncBaseItem syncBaseItem = (SyncBaseItem) syncItem;
            boolean isMyOwnProperty = ClientBase.getInstance().isMyOwnProperty(syncBaseItem);
            if (isMyOwnProperty) {
                ClientBase.getInstance().recalculate4FakedHouseSpace(syncBaseItem);
            }
            ClientBase.getInstance().onItemDeleted(syncBaseItem, actor);
            if (isMyOwnProperty) {
                ClientDeadEndProtection.getInstance().onSyncItemLost(syncBaseItem);
            }
        }
        checkSpecialRemoved(syncItem);
        seeminglyDeadItems.remove(syncItem.getId());
        SelectionHandler.getInstance().itemKilled(syncItem);

        if (actor != null && syncItem instanceof SyncBaseItem) {
            SyncBaseItem target = (SyncBaseItem) syncItem;
            if (ClientBase.getInstance().isBot(target.getBase())) {
                ClientBotService.getInstance().onBotItemKilled(target, actor);
            }
            SoundHandler.getInstance().onItemKilled(target, actor);
        }
        if (syncItem instanceof SyncTickItem) {
            ActionHandler.getInstance().removeActiveItem((SyncTickItem) syncItem);
        }
    }

    @Override
    public boolean baseObjectExists(SyncItem syncItem) {
        return items.containsKey(syncItem.getId());
    }

    @Override
    public SyncItem getItem(Id id) throws ItemDoesNotExistException {
        SyncItem syncItem = items.get(id);
        if (syncItem == null) {
            throw new ItemDoesNotExistException(id);
        }
        return syncItem;
    }

    public static ItemContainer getInstance() {
        return INSATNCE;
    }

    public Collection<SyncItem> getItems() {
        return items.values();
    }

    @Override
    protected <T> T iterateOverItems(boolean includeNoPosition, boolean includeDead, T defaultReturn, ItemHandler<T> itemHandler) {
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

        return defaultReturn;
    }

    @Override
    protected GlobalServices getGlobalServices() {
        return ClientGlobalServices.getInstance();
    }

    @Override
    protected PlanetServices getPlanetServices() {
        return ClientPlanetServices.getInstance();
    }

    public void checkSpecialChanged(SyncItem syncItem) {
        if (!isSpecialItem(syncItem)) {
            return;
        }
        RadarPanel.getInstance().onRadarModeItemChanged((SyncBaseItem) syncItem);
    }

    private void checkSpecialRemoved(SyncItem syncItem) {
        if (!isSpecialItem(syncItem)) {
            return;
        }
        RadarPanel.getInstance().onRadarModeItemRemoved((SyncBaseItem) syncItem);
    }

    private boolean isSpecialItem(SyncItem syncItem) {
        if (!(syncItem instanceof SyncBaseItem)) {
            return false;
        }
        SyncBaseItem syncBaseItem = (SyncBaseItem) syncItem;
        return ClientBase.getInstance().isMyOwnProperty(syncBaseItem) && syncBaseItem.hasSyncSpecial();
    }

    public void clear() {
        items.clear();
        seeminglyDeadItems.clear();
    }

    @Override
    public void onItemChanged(Change change, SyncItem syncItem, Object additionalCustomInfo) {
        // TODO Remove if bug found
        switch (change) {
            case POSITION:
                try {
                    if (syncItem instanceof SyncBaseItem && Connection.getInstance().getGameEngineMode() == GameEngineMode.MASTER) {
                        ActionHandler.getInstance().interactionGuardingItems((SyncBaseItem) syncItem);
                    }
                } catch (Throwable t) {
                    log.log(Level.SEVERE, "SyncItem.onItemChanged() failed POSITION: " + syncItem, t);
                }
                break;
            case BUILD:
                try {
                    if (syncItem instanceof SyncBaseItem && ((SyncBaseItem) syncItem).isReady()) {
                        SyncBaseItem syncBaseItem = (SyncBaseItem) syncItem;
                        SimulationConditionServiceImpl.getInstance().onSyncItemBuilt(syncBaseItem);
                        ClientBase.getInstance().recalculate4FakedHouseSpace(syncBaseItem);
                        if (Connection.getInstance().getGameEngineMode() == GameEngineMode.MASTER) {
                            ActionHandler.getInstance().addGuardingBaseItem(syncBaseItem);
                            ItemContainer.getInstance().checkSpecialChanged(syncItem);
                        }
                        if (ClientBase.getInstance().isMyOwnProperty(syncBaseItem)) {
                            SoundHandler.getInstance().playOnBuiltSound(syncBaseItem);
                        }
                    }
                } catch (Throwable t) {
                    log.log(Level.SEVERE, "SyncItem.onItemChanged() failed BUILD: " + syncItem, t);
                }
                break;
            case ITEM_TYPE_CHANGED:
                try {
                    RadarPanel.getInstance().onItemTypeChanged(syncItem);
                } catch (Throwable t) {
                    log.log(Level.SEVERE, "SyncItem.onItemChanged() failed ITEM_TYPE_CHANGED: " + syncItem, t);
                }
                SelectionHandler.getInstance().refresh();
                break;
            case PROJECTILE_LAUNCHED:
                AttackVisualization.getInstance().onFiring((SyncBaseItem) syncItem, (ActiveProjectileGroup) additionalCustomInfo);
                break;
            case UNDER_ATTACK:
                if (ClientBase.getInstance().isMyOwnProperty((SyncBaseItem) syncItem)) {
                    RadarPanel.getInstance().onwItemUnderAttack((SyncBaseItem) syncItem);
                }
                break;
            case PROJECTILE_DETONATION:
                AttackVisualization.getInstance().onProjectileDetonation((SyncBaseItem) syncItem);
                break;
        }
    }
}
