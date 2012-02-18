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
import com.btxtech.game.jsre.client.ClientServices;
import com.btxtech.game.jsre.client.ClientSyncItem;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.GameEngineMode;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.action.ActionHandler;
import com.btxtech.game.jsre.client.cockpit.SelectionHandler;
import com.btxtech.game.jsre.client.cockpit.radar.RadarPanel;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.effects.ExplosionHandler;
import com.btxtech.game.jsre.client.simulation.SimulationConditionServiceImpl;
import com.btxtech.game.jsre.client.utg.SpeechBubbleHandler;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.ProjectileItemType;
import com.btxtech.game.jsre.common.gameengine.services.Services;
import com.btxtech.game.jsre.common.gameengine.services.base.AbstractBaseService;
import com.btxtech.game.jsre.common.gameengine.services.base.HouseSpaceExceededException;
import com.btxtech.game.jsre.common.gameengine.services.base.ItemLimitExceededException;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.services.items.impl.AbstractItemService;
import com.btxtech.game.jsre.common.gameengine.services.items.impl.ItemHandler;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItemListener;
import com.btxtech.game.jsre.common.gameengine.syncObjects.syncInfos.SyncItemInfo;
import com.btxtech.game.jsre.common.tutorial.ItemTypeAndPosition;
import com.google.gwt.user.client.Timer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * User: beat
 * Date: Jul 4, 2009
 * Time: 12:26:56 PM
 */
public class ItemContainer extends AbstractItemService {
    public static final int CLEANUP_INTERVALL = 3000;
    private static final ItemContainer INSATNCE = new ItemContainer();
    private HashMap<Id, ClientSyncItem> items = new HashMap<Id, ClientSyncItem>();
    private HashMap<Id, ClientSyncItem> orphanItems = new HashMap<Id, ClientSyncItem>();
    private HashMap<Id, ClientSyncItem> seeminglyDeadItems = new HashMap<Id, ClientSyncItem>();
    private int itemId = 1;

    /**
     * Singleton
     */
    private ItemContainer() {
        Timer timer = new Timer() {
            @Override
            public void run() {
                for (Iterator<Map.Entry<Id, ClientSyncItem>> it = orphanItems.entrySet().iterator(); it.hasNext();) {
                    Map.Entry<Id, ClientSyncItem> entry = it.next();
                    long insertTime = entry.getKey().getUserTimeStamp();
                    if (insertTime + CLEANUP_INTERVALL < System.currentTimeMillis()) {
                        it.remove();
                        items.remove(entry.getKey());
                        GwtCommon.sendLogToServer("Orphan item removed due timeout: " + entry.getValue().getSyncItem());
                    }
                }
                for (Iterator<Map.Entry<Id, ClientSyncItem>> it = seeminglyDeadItems.entrySet().iterator(); it.hasNext();) {
                    Map.Entry<Id, ClientSyncItem> entry = it.next();
                    long insertTime = entry.getKey().getUserTimeStamp();
                    if (insertTime + CLEANUP_INTERVALL < System.currentTimeMillis()) {
                        it.remove();
                        GwtCommon.sendLogToServer("Can not definitely kill item due to missing ack from server: " + entry.getKey() + " " + entry.getValue().getSyncItem());
                    }

                }
            }
        };
        timer.scheduleRepeating(CLEANUP_INTERVALL);
    }

    public void sychronize(SyncItemInfo syncItemInfo) throws NoSuchItemTypeException, ItemDoesNotExistException {
        ClientSyncItem clientSyncItem = items.get(syncItemInfo.getId());

        if (syncItemInfo.isAlive()) {
            if (clientSyncItem == null) {
                clientSyncItem = createAndAddItem(syncItemInfo.getId(), syncItemInfo.getPosition(), syncItemInfo.getItemTypeId(), syncItemInfo.getBase());
                if (clientSyncItem.isSyncBaseItem()) {
                    ClientBase.getInstance().onItemCreated(clientSyncItem.getSyncBaseItem());
                }
            } else {
                // Check for  Teleportation effect
                Index localPos = clientSyncItem.getSyncItem().getSyncItemArea().getPosition();
                Index syncPos = syncItemInfo.getPosition();
                if (localPos != null && syncPos != null) {
                    int distance = localPos.getDistance(syncPos);
                    if (distance > 200) {
                        GwtCommon.sendLogToServer("Teleportation detected. Distance: " + distance + " Info:" + syncItemInfo + " | Item:" + clientSyncItem.getSyncItem());
                    }
                }
                // It was a orphan until now
                ClientSyncItem orphanItem = orphanItems.remove(clientSyncItem.getSyncItem().getId());
                if (orphanItem != null) {
                    orphanItem.setHidden(false);
                    if (clientSyncItem.isSyncBaseItem()) {
                        ClientBase.getInstance().onItemCreated(clientSyncItem.getSyncBaseItem());
                    }
                }
            }
            clientSyncItem.getSyncItem().synchronize(syncItemInfo);
            checkSpecialChanged(clientSyncItem.getSyncItem());
            clientSyncItem.checkVisibility();
            clientSyncItem.update();
            if (clientSyncItem.isSyncTickItem()) {
                ActionHandler.getInstance().syncItemActivated(clientSyncItem.getSyncTickItem());
            }
        } else {
            if (clientSyncItem != null) {
                definitelyKillItem(clientSyncItem, true, syncItemInfo.isExplode(), null);
            }

        }
    }

    @Override
    public SyncItem createSyncObject(ItemType toBeBuilt, Index position, SyncBaseItem creator, SimpleBase base, int createdChildCount) throws NoSuchItemTypeException, ItemLimitExceededException, HouseSpaceExceededException {
        if (toBeBuilt instanceof BaseItemType
                && ClientBase.getInstance().isMyOwnBase(base)
                && !ClientBase.getInstance().isBot(base)
                && (Connection.getInstance().getGameEngineMode() == GameEngineMode.SLAVE || Connection.getInstance().getGameEngineMode() == GameEngineMode.MASTER)) {
            ClientBase.getInstance().checkItemLimit4ItemAdding((BaseItemType) toBeBuilt);
        }
        ClientSyncItem itemView;
        int parentId = Id.NO_ID;
        if (creator != null) {
            parentId = creator.getId().getId();
        }
        if (Connection.getInstance().getGameEngineMode() == GameEngineMode.MASTER) {
            Id id = createId(parentId, createdChildCount);
            itemView = createAndAddItem(id, position, toBeBuilt.getId(), base);
            itemView.setHidden(false);
            id.setUserTimeStamp(System.currentTimeMillis());
            ActionHandler.getInstance().addGuardingBaseItem(itemView.getSyncTickItem());
            if (itemView.isSyncBaseItem()) {
                ActionHandler.getInstance().interactionGuardingItems(itemView.getSyncBaseItem());
                ClientBase.getInstance().onItemCreated(itemView.getSyncBaseItem());
            }
            ClientServices.getInstance().getConnectionService().sendSyncInfo(itemView.getSyncItem());
        } else {
            Id id = new Id(parentId, createdChildCount);
            itemView = items.get(id);
            if (itemView != null) {
                return itemView.getSyncItem();
            }
            if (toBeBuilt instanceof ProjectileItemType) {
                // New idea, return null on the client. Create new items only on the server
                return null;
            }
            itemView = createAndAddItem(id, position, toBeBuilt.getId(), base);
            id.setUserTimeStamp(System.currentTimeMillis());
            orphanItems.put(id, itemView);
            itemView.setHidden(true);
        }
        itemView.checkVisibility();
        itemView.update();
        return itemView.getSyncItem();
    }

    private Id createId(int parentId, int createdChildCount) {
        itemId++;
        return new Id(itemId, parentId, createdChildCount);
    }

    public ClientSyncItem getSimulationItem(int intId) {
        for (Map.Entry<Id, ClientSyncItem> entry : items.entrySet()) {
            if (entry.getKey().getId() == intId) {
                return entry.getValue();
            }
        }
        throw new IllegalArgumentException(this + " getSimulationItem(): no ClientSyncItem for id: " + intId);
    }

    public SyncItem createSimulationSyncObject(ItemTypeAndPosition itemTypeAndPosition) throws NoSuchItemTypeException {
        Id id = createId(Id.SIMULATION_ID, Id.SIMULATION_ID);
        if (items.containsKey(id)) {
            throw new IllegalStateException(this + " simulated id is already used: " + id);
        }
        SimpleBase simpleBase = null;
        if (getItemType(itemTypeAndPosition.getItemTypeId()) instanceof BaseItemType) {
            simpleBase = ClientBase.getInstance().getSimpleBase();
        }
        ClientSyncItem itemView = createAndAddItem(id, itemTypeAndPosition.getPosition(), itemTypeAndPosition.getItemTypeId(), simpleBase);
        id.setUserTimeStamp(System.currentTimeMillis());
        if (itemView.getSyncItem() instanceof SyncBaseItem) {
            SyncBaseItem syncBaseItem = (SyncBaseItem) itemView.getSyncItem();
            syncBaseItem.setBuildup(1.0);
            syncBaseItem.getSyncItemArea().setAngel(itemTypeAndPosition.getAngel());
            syncBaseItem.fireItemChanged(SyncItemListener.Change.ANGEL);
            ClientBase.getInstance().onItemCreated(syncBaseItem);
        }
        itemView.checkVisibility();
        itemView.update();
        ClientServices.getInstance().getConnectionService().sendSyncInfo(itemView.getSyncItem());
        return itemView.getSyncItem();
    }

    public SyncItem createItemTypeEditorSyncObject(SimpleBase simpleBase, int itemTypeId, Index position) throws NoSuchItemTypeException {
        Id id = createId(Id.SIMULATION_ID, Id.SIMULATION_ID);
        if (items.containsKey(id)) {
            throw new IllegalStateException(this + " simulated id is already used: " + id);
        }
        ClientSyncItem itemView = createAndAddItem(id, position, itemTypeId, simpleBase);
        id.setUserTimeStamp(System.currentTimeMillis());
        if (itemView.getSyncItem() instanceof SyncBaseItem) {
            SyncBaseItem syncBaseItem = (SyncBaseItem) itemView.getSyncItem();
            syncBaseItem.setBuildup(1.0);
        }
        itemView.checkVisibility();
        itemView.update();
        return itemView.getSyncItem();
    }

    private ClientSyncItem createAndAddItem(Id id, Index position, int itemTypeId, SimpleBase base) throws NoSuchItemTypeException {
        SyncItem syncItem = newSyncItem(id, position, itemTypeId, base, ClientServices.getInstance());
        ClientSyncItem itemView = new ClientSyncItem(syncItem);
        items.put(id, itemView);
        return itemView;
    }

    @Override
    public void killSyncItem(SyncItem killedItem, SimpleBase actor, boolean force, boolean explode) {
        ClientSyncItem clientSyncItem = items.get(killedItem.getId());
        if (clientSyncItem == null) {
            throw new IllegalStateException("No ClientSyncItem for: " + killedItem);
        }
        if (Connection.getInstance().getGameEngineMode() == GameEngineMode.MASTER) {
            definitelyKillItem(clientSyncItem, force, explode, actor);
            if (killedItem instanceof SyncBaseItem) {
                SyncBaseItem syncBaseItem = (SyncBaseItem) killedItem;
                ActionHandler.getInstance().removeGuardingBaseItem(syncBaseItem);
                ClientEnergyService.getInstance().onSyncItemKilled(syncBaseItem);
                SimulationConditionServiceImpl.getInstance().onSyncItemKilled(actor, (SyncBaseItem) killedItem);
            }
            ClientServices.getInstance().getConnectionService().sendSyncInfo(killedItem);
        } else {
            makeItemSeeminglyDead(killedItem, actor, clientSyncItem);
        }
    }

    private void makeItemSeeminglyDead(SyncItem syncItem, SimpleBase actor, ClientSyncItem ClientSyncItem) {
        if (items.containsKey(syncItem.getId())) {
            syncItem.getId().setUserTimeStamp(System.currentTimeMillis());
            seeminglyDeadItems.put(syncItem.getId(), ClientSyncItem);
        } else {
            GwtCommon.sendLogToServer("This should never happen: ItemContainer.killSyncItem() syncItem:" + syncItem + " actor:" + actor);
        }
    }

    private void definitelyKillItem(ClientSyncItem itemView, boolean force, boolean explode, SimpleBase actor) {
        if (force) {
            if (itemView.isSyncBaseItem()) {
                itemView.getSyncBaseItem().setHealth(0);
            } else if (itemView.isSyncResourceItem()) {
                itemView.getSyncResourceItem().setAmount(0);
            }
        }

        items.remove(itemView.getSyncItem().getId());
        if (itemView.isMyOwnProperty()) {
            ClientBase.getInstance().recalculate4FakedHouseSpace(itemView.getSyncBaseItem());
            ClientBase.getInstance().onItemDeleted(itemView.getSyncBaseItem(), actor);
        }
        checkSpecialRemoved(itemView.getSyncItem());
        seeminglyDeadItems.remove(itemView.getSyncItem().getId());
        SelectionHandler.getInstance().itemKilled(itemView);
        SpeechBubbleHandler.getInstance().itemKilled(itemView.getSyncItem());

        if (explode) {
            ActionHandler.getInstance().removeActiveItem(itemView.getSyncTickItem());
            ExplosionHandler.getInstance().terminateWithExplosion(itemView);
        } else {
            itemView.dispose();
        }
    }

    @Override
    public boolean baseObjectExists(SyncItem syncItem) {
        return items.containsKey(syncItem.getId());
    }

    public ClientSyncItem getClientSyncItem(Id id) throws ItemDoesNotExistException {
        ClientSyncItem clientSyncItem = items.get(id);
        if (clientSyncItem == null) {
            throw new ItemDoesNotExistException(id);
        }
        return clientSyncItem;
    }

    public ClientSyncItem getClientSyncItem(SyncItem syncItem) throws ItemDoesNotExistException {
        ClientSyncItem clientSyncItem = items.get(syncItem.getId());
        if (clientSyncItem == null) {
            throw new ItemDoesNotExistException(syncItem.getId());
        }
        return clientSyncItem;
    }

    @Override
    public SyncItem getItem(Id id) throws ItemDoesNotExistException {
        ClientSyncItem ClientSyncItem = items.get(id);
        if (ClientSyncItem == null) {
            throw new ItemDoesNotExistException(id);
        }
        return ClientSyncItem.getSyncItem();
    }

    public static ItemContainer getInstance() {
        return INSATNCE;
    }

    public Collection<ClientSyncItem> getItems() {
        return items.values();
    }

    @Override
    protected <T> T iterateOverItems(ItemHandler<T> itemHandler, T defaultReturn) {
        for (ClientSyncItem clientSyncItem : items.values()) {
            if (orphanItems.containsKey(clientSyncItem.getSyncItem().getId()) || seeminglyDeadItems.containsKey(clientSyncItem.getSyncItem().getId())) {
                continue;
            }
            T result = itemHandler.handleItem(clientSyncItem.getSyncItem());
            if (result != null) {
                return result;
            }
        }
        return defaultReturn;
    }

    @Override
    protected Services getServices() {
        return ClientServices.getInstance();
    }

    // TODO move up

    public Collection<ClientSyncItem> getOwnItems() {
        ArrayList<ClientSyncItem> clientBaseItems = new ArrayList<ClientSyncItem>();
        for (ClientSyncItem clientSyncItem : items.values()) {
            if (clientSyncItem.isSyncBaseItem() &&
                    clientSyncItem.isMyOwnProperty() &&
                    !orphanItems.containsKey(clientSyncItem.getSyncItem().getId()) &&
                    !seeminglyDeadItems.containsKey(clientSyncItem.getSyncItem().getId())) {
                clientBaseItems.add(clientSyncItem);
            }
        }
        return clientBaseItems;
    }

    @Override
    protected AbstractBaseService getBaseService() {
        return ClientBase.getInstance();
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
        for (ClientSyncItem clientSyncItem : items.values()) {
            clientSyncItem.dispose();
        }
        items.clear();
        orphanItems.clear();
        seeminglyDeadItems.clear();
    }
}
