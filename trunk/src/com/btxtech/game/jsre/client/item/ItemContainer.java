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
import com.btxtech.game.jsre.client.ClientServices;
import com.btxtech.game.jsre.client.ClientSyncBaseItemView;
import com.btxtech.game.jsre.client.ClientSyncItemView;
import com.btxtech.game.jsre.client.ClientSyncResourceItemView;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.action.ActionHandler;
import com.btxtech.game.jsre.client.cockpit.SelectionHandler;
import com.btxtech.game.jsre.client.cockpit.radar.RadarPanel;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.effects.ExplosionHandler;
import com.btxtech.game.jsre.client.simulation.Simulation;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.client.utg.ClientUserGuidance;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.bot.PlayerSimulation;
import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.base.BaseService;
import com.btxtech.game.jsre.common.gameengine.services.collision.CommonCollisionService;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.services.items.impl.AbstractItemService;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncResourceItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.syncInfos.SyncItemInfo;
import com.btxtech.game.jsre.common.tutorial.ItemTypeAndPosition;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * User: beat
 * Date: Jul 4, 2009
 * Time: 12:26:56 PM
 */
public class ItemContainer extends AbstractItemService implements CommonCollisionService {
    public static final int CLEANUP_INTERVALL = 3000;
    private static final ItemContainer INSATNCE = new ItemContainer();
    private HashMap<Id, ClientSyncItemView> items = new HashMap<Id, ClientSyncItemView>();
    private HashSet<ClientSyncBaseItemView> specialItems = new HashSet<ClientSyncBaseItemView>();
    private HashMap<Id, ClientSyncItemView> orphanItems = new HashMap<Id, ClientSyncItemView>();
    private HashMap<Id, ClientSyncItemView> seeminglyDeadItems = new HashMap<Id, ClientSyncItemView>();

    /**
     * Singleton
     */
    private ItemContainer() {
        Timer timer = new Timer() {
            @Override
            public void run() {
                for (Iterator<Map.Entry<Id, ClientSyncItemView>> it = orphanItems.entrySet().iterator(); it.hasNext();) {
                    Map.Entry<Id, ClientSyncItemView> entry = it.next();
                    long insertTime = entry.getKey().getUserTimeStamp();
                    if (insertTime + CLEANUP_INTERVALL < System.currentTimeMillis()) {
                        it.remove();
                        items.remove(entry.getKey());
                        GwtCommon.sendLogToServer("Orphan item removed due timeout: " + entry.getValue().getSyncItem());
                        entry.getValue().dispose();
                    }
                }
                for (Iterator<Map.Entry<Id, ClientSyncItemView>> it = seeminglyDeadItems.entrySet().iterator(); it.hasNext();) {
                    Map.Entry<Id, ClientSyncItemView> entry = it.next();
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
        ClientSyncItemView clientSyncItemView = items.get(syncItemInfo.getId());

        boolean isCreated = false;
        if (syncItemInfo.isAlive()) {
            if (clientSyncItemView == null) {
                clientSyncItemView = createAndAddItem(syncItemInfo.getId(), syncItemInfo.getPosition(), syncItemInfo.getItemTypeId(), syncItemInfo.getBase());
                isCreated = true;
                checkSpecialAdded(clientSyncItemView);
            } else {
                // Check for  Teleportation effect
                Index localPos = clientSyncItemView.getSyncItem().getPosition();
                Index syncPos = syncItemInfo.getPosition();
                if (localPos != null && syncPos != null) {
                    int distance = localPos.getDistance(syncPos);
                    if (distance > 200) {
                        GwtCommon.sendLogToServer("Teleportation detected. Distance: " + distance + " Info:" + syncItemInfo + " | Item:" + clientSyncItemView.getSyncItem());
                    }
                }
                ClientSyncItemView orphanItem = orphanItems.remove(clientSyncItemView.getSyncItem().getId());
                if (orphanItem != null) {
                    orphanItem.setVisible(true);
                    isCreated = true;
                    checkSpecialAdded(clientSyncItemView);
                }
            }
            clientSyncItemView.getSyncItem().synchronize(syncItemInfo);
            if (isCreated) {
                ClientUserGuidance.getInstance().onItemCreated(clientSyncItemView);
                PlayerSimulation.getInstance().onItemCreated(clientSyncItemView);
            }
            clientSyncItemView.update();
            checkSpecialItem(clientSyncItemView);
            if (clientSyncItemView instanceof ClientSyncBaseItemView) {
                ActionHandler.getInstance().addActiveItem(((ClientSyncBaseItemView) clientSyncItemView).getSyncBaseItem());
            }
        } else {
            if (clientSyncItemView != null) {
                definitelyKillItem(clientSyncItemView);
            }

        }
    }

    @Override
    public SyncItem createSyncObject(ItemType toBeBuilt, Index position, SyncBaseItem creator, SimpleBase base, int createdChildCount) throws NoSuchItemTypeException {
        ClientSyncItemView itemView;
        if (Connection.getInstance().getGameInfo().hasServerCommunication()) {
            Id id = new Id(creator.getId().getId(), createdChildCount);
            itemView = items.get(id);
            if (itemView != null) {
                return itemView.getSyncItem();
            }
            itemView = createAndAddItem(id, position, toBeBuilt.getId(), base);
            id.setUserTimeStamp(System.currentTimeMillis());
            orphanItems.put(id, itemView);
            itemView.setVisible(false);
        } else {
            Id id = createSimulationId();
            itemView = createAndAddItem(id, position, toBeBuilt.getId(), base);
            itemView.setVisible(true);
            id.setUserTimeStamp(System.currentTimeMillis());
        }
        return itemView.getSyncItem();
    }

    public Id createSimulationId(int id) {
        return new Id(id, -1, -1);
    }

    private Id createSimulationId() {
        int intId = 1;
        for (Id id : items.keySet()) {
            if (id.getId() > intId) {
                intId = id.getId();
            }
        }
        intId++;
        return createSimulationId(intId);
    }

    public SyncItem createSimulationSyncObject(ItemTypeAndPosition itemTypeAndPosition) throws NoSuchItemTypeException {
        Id id = createSimulationId(itemTypeAndPosition.getId());
        if (items.containsKey(id)) {
            throw new IllegalStateException(this + " simulated id is already used: " + id);
        }
        ClientSyncItemView itemView = createAndAddItem(id, itemTypeAndPosition.getPosition(), itemTypeAndPosition.getItemTypeId(), itemTypeAndPosition.getBase());
        id.setUserTimeStamp(System.currentTimeMillis());
        if (itemView.getSyncItem() instanceof SyncBaseItem) {
            ((SyncBaseItem) itemView.getSyncItem()).setBuild(true);
            ((SyncBaseItem) itemView.getSyncItem()).setFullHealth();
        }
        return itemView.getSyncItem();
    }

    private ClientSyncItemView createAndAddItem(Id id, Index position, int itemTypeId, SimpleBase base) throws NoSuchItemTypeException {
        SyncItem syncItem = newSyncItem(id, position, itemTypeId, base, ClientServices.getInstance());
        ClientSyncItemView itemView;
        if (syncItem instanceof SyncBaseItem) {
            itemView = new ClientSyncBaseItemView((SyncBaseItem) syncItem);
        } else if (syncItem instanceof SyncResourceItem) {
            itemView = new ClientSyncResourceItemView((SyncResourceItem) syncItem);
        } else {
            throw new IllegalArgumentException(this + " unknown SyncItem: " + syncItem);
        }
        items.put(id, itemView);
        return itemView;
    }

    @Override
    public void killSyncItem(SyncItem killedItem, SyncBaseItem actor, boolean force) {
        ClientSyncItemView clientSyncItemView = items.get(killedItem.getId());
        if (Connection.getInstance().getGameInfo().hasServerCommunication()) {
            makeItemSeeminglyDead(killedItem, actor, clientSyncItemView);
        } else {
            definitelyKillItem(clientSyncItemView);
        }
        Simulation.getInstance().onSyncItemKilled(killedItem, actor);
    }

    private void makeItemSeeminglyDead(SyncItem syncItem, SyncBaseItem actor, ClientSyncItemView clientSyncItemView) {
        if (items.containsKey(syncItem.getId())) {
            syncItem.getId().setUserTimeStamp(System.currentTimeMillis());
            seeminglyDeadItems.put(syncItem.getId(), clientSyncItemView);
        } else {
            GwtCommon.sendLogToServer("This should never happen: ItemContainer.killSyncItem() syncItem:" + syncItem + " actor:" + actor);
        }
    }

    private void definitelyKillItem(ClientSyncItemView itemView) {
        items.remove(itemView.getSyncItem().getId());
        checkSpecialRemoved(itemView);
        seeminglyDeadItems.remove(itemView.getSyncItem().getId());
        SelectionHandler.getInstance().itemKilled(itemView);
        ClientUserGuidance.getInstance().onItemDeleted(itemView);

        if (itemView instanceof ClientSyncBaseItemView) {
            ClientSyncBaseItemView clientSyncBaseItemView = (ClientSyncBaseItemView) itemView;
            ActionHandler.getInstance().removeActiveItem(clientSyncBaseItemView.getSyncBaseItem());
            ExplosionHandler.getInstance().terminateWithExplosion(itemView);
        } else {
            itemView.dispose();
        }
    }

    public boolean baseObjectExists(SyncItem syncItem) {
        return items.containsKey(syncItem.getId());
    }

    @Override
    public SyncItem getItem(Id id) throws ItemDoesNotExistException {
        ClientSyncItemView clientSyncItemView = items.get(id);
        if (clientSyncItemView == null) {
            throw new ItemDoesNotExistException(id);
        }
        return clientSyncItemView.getSyncItem();
    }

    public static ItemContainer getInstance() {
        return INSATNCE;
    }

    public Collection<ClientSyncItemView> getItems() {
        return items.values();
    }

    public Collection<ClientSyncBaseItemView> getItemsInRect(Rectangle rectangle, boolean onlyOwnItems) {
        ArrayList<ClientSyncBaseItemView> clientBaseItems = new ArrayList<ClientSyncBaseItemView>();
        for (ClientSyncItemView clientBaseItem : items.values()) {
            if (clientBaseItem instanceof ClientSyncBaseItemView &&
                    !orphanItems.containsKey(clientBaseItem.getSyncItem().getId()) &&
                    !seeminglyDeadItems.containsKey(clientBaseItem.getSyncItem().getId()) &&
                    rectangle.contains(clientBaseItem.getSyncItem().getPosition())) {
                if (onlyOwnItems) {
                    if (((ClientSyncBaseItemView) clientBaseItem).isMyOwnProperty()) {
                        clientBaseItems.add((ClientSyncBaseItemView) clientBaseItem);
                    }
                } else {
                    clientBaseItems.add((ClientSyncBaseItemView) clientBaseItem);
                }
            }
        }
        return clientBaseItems;
    }

    public ClientSyncItemView getFirstItemInRange(ItemType itemType, Index origin, int maxRange) {
        for (ClientSyncItemView syncItemView : items.values()) {
            if (syncItemView.getSyncItem().getItemType().equals(itemType) &&
                    !orphanItems.containsKey(syncItemView.getSyncItem().getId()) &&
                    !seeminglyDeadItems.containsKey(syncItemView.getSyncItem().getId()) &&
                    syncItemView.getSyncItem().getPosition().getDistance(origin) <= maxRange) {
                return syncItemView;
            }
        }
        return null;
    }

    public boolean hasBuildingsInRect(Rectangle rectangle) {
        for (ClientSyncItemView syncItemView : items.values()) {
            if (syncItemView instanceof ClientSyncBaseItemView &&
                    !orphanItems.containsKey(syncItemView.getSyncItem().getId()) &&
                    !seeminglyDeadItems.containsKey(syncItemView.getSyncItem().getId()) &&
                    !((ClientSyncBaseItemView) syncItemView).getSyncBaseItem().hasSyncMovable() &&
                    rectangle.adjoins(syncItemView.getSyncItem().getRectangle())) {
                return false;
            }
        }
        return true;
    }


    public Collection<ClientSyncBaseItemView> getOwnItems() {
        ArrayList<ClientSyncBaseItemView> clientBaseItems = new ArrayList<ClientSyncBaseItemView>();
        for (ClientSyncItemView clientBaseItem : items.values()) {
            if (clientBaseItem instanceof ClientSyncBaseItemView &&
                    ((ClientSyncBaseItemView) clientBaseItem).isMyOwnProperty() &&
                    !orphanItems.containsKey(clientBaseItem.getSyncItem().getId()) &&
                    !seeminglyDeadItems.containsKey(clientBaseItem.getSyncItem().getId())) {
                clientBaseItems.add((ClientSyncBaseItemView) clientBaseItem);
            }
        }
        return clientBaseItems;
    }

    public List<SyncBaseItem> getEnemyItems(SimpleBase base) {
        ArrayList<SyncBaseItem> clientBaseItems = new ArrayList<SyncBaseItem>();
        for (ClientSyncItemView clientBaseItem : items.values()) {
            if (clientBaseItem instanceof ClientSyncBaseItemView &&
                    !((ClientSyncBaseItemView) clientBaseItem).getSyncBaseItem().getBase().equals(base) &&
                    !orphanItems.containsKey(clientBaseItem.getSyncItem().getId()) &&
                    !seeminglyDeadItems.containsKey(clientBaseItem.getSyncItem().getId())) {
                clientBaseItems.add(((ClientSyncBaseItemView) clientBaseItem).getSyncBaseItem());
            }
        }
        return clientBaseItems;
    }

    public List<? extends SyncItem> getItems(ItemType itemType, SimpleBase simpleBase) {
        ArrayList<SyncItem> syncItems = new ArrayList<SyncItem>();
        for (ClientSyncItemView clientBaseItem : items.values()) {
            SyncItem syncItem = clientBaseItem.getSyncItem();
            if (orphanItems.containsKey(syncItem.getId()) || seeminglyDeadItems.containsKey(syncItem.getId())) {
                continue;
            }

            if (!syncItem.getItemType().equals(itemType)) {
                continue;
            }

            if (simpleBase != null) {
                if (clientBaseItem instanceof ClientSyncBaseItemView && ((ClientSyncBaseItemView) clientBaseItem).getSyncBaseItem().getBase().equals(simpleBase)) {
                    syncItems.add(syncItem);
                }
            } else {
                syncItems.add(syncItem);
            }
        }
        return syncItems;

    }

    public Index getFreeRandomPosition(ItemType itemType, SyncItem origin, int targetMinRange, int targetMaxRange) {
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            double angel = Random.nextDouble() * 2.0 * Math.PI;
            int discance = targetMinRange + Random.nextInt(targetMaxRange - targetMinRange);
            Index point = origin.getPosition().getPointFromAngelToNord(angel, discance);

            if (point.getX() >= TerrainView.getInstance().getTerrainHandler().getTerrainSettings().getPlayFieldXSize()) {
                continue;
            }
            if (point.getY() >= TerrainView.getInstance().getTerrainHandler().getTerrainSettings().getPlayFieldYSize()) {
                continue;
            }

            if (!TerrainView.getInstance().getTerrainHandler().isFree(point, itemType)) {
                continue;
            }
            Rectangle itemRectangle = new Rectangle(point.getX() - itemType.getWidth() / 2,
                    point.getY() - itemType.getHeight() / 2,
                    itemType.getWidth(),
                    itemType.getHeight());

            if (!getItemsInRect(itemRectangle, false).isEmpty()) {
                continue;
            }
            return point;
        }
        throw new IllegalStateException("Can not find free position");
    }

    @Override
    protected BaseService getBaseService() {
        return ClientBase.getInstance();
    }

    private void checkSpecialItem(ClientSyncItemView clientSyncItemView) {
        if (isMySpecialItem(clientSyncItemView) != null) {
            checkForSpecialItems();
        }
    }

    private void checkSpecialAdded(ClientSyncItemView clientSyncItemView) {
        ClientSyncBaseItemView clientSyncBaseItemView = isMySpecialItem(clientSyncItemView);
        if (clientSyncBaseItemView == null) {
            return;
        }
        specialItems.add(clientSyncBaseItemView);
        checkForSpecialItems();
    }

    private void checkSpecialRemoved(ClientSyncItemView clientSyncItemView) {
        ClientSyncBaseItemView clientSyncBaseItemView = isMySpecialItem(clientSyncItemView);
        if (clientSyncBaseItemView == null) {
            return;
        }
        specialItems.remove(clientSyncBaseItemView);
        checkForSpecialItems();
    }

    private void checkForSpecialItems() {
        RadarPanel.getInstance().setRadarState1(checkForSpecialItem(RadarPanel.RADAR_1));
    }

    public void handleSpecial(ClientSyncBaseItemView clientSyncBaseItemView) {
        if (!clientSyncBaseItemView.isMyOwnProperty()) {
            return;
        }

        if (specialItems.contains(clientSyncBaseItemView)) {
            if (!clientSyncBaseItemView.getSyncBaseItem().hasSyncSpecial()) {
                checkSpecialRemoved(clientSyncBaseItemView);
            } else {
                checkForSpecialItems();
            }
        } else {
            if (clientSyncBaseItemView.getSyncBaseItem().hasSyncSpecial()) {
                checkSpecialAdded(clientSyncBaseItemView);
            }
        }
    }

    private ClientSyncBaseItemView isMySpecialItem(ClientSyncItemView clientSyncItemView) {
        if (!(clientSyncItemView instanceof ClientSyncBaseItemView)) {
            return null;
        }
        ClientSyncBaseItemView clientSyncBaseItemView = (ClientSyncBaseItemView) clientSyncItemView;
        if (!clientSyncBaseItemView.isMyOwnProperty()) {
            return null;
        }

        if (!clientSyncBaseItemView.getSyncBaseItem().hasSyncSpecial()) {
            return null;
        }
        return clientSyncBaseItemView;
    }

    private boolean checkForSpecialItem(String string) {
        for (ClientSyncBaseItemView specialItem : specialItems) {
            if (specialItem.getSyncBaseItem().getSyncSpecial().getString().equals(string) && specialItem.getSyncBaseItem().isReady()) {
                return true;
            }
        }
        return false;
    }

    public Map<BaseItemType, List<SyncBaseItem>> getItems4Base(SimpleBase simpleBase) {
        Map<BaseItemType, List<SyncBaseItem>> result = new HashMap<BaseItemType, List<SyncBaseItem>>();
        for (ClientSyncItemView clientSyncItemView : items.values()) {
            if (orphanItems.containsKey(clientSyncItemView.getSyncItem().getId()) ||
                    seeminglyDeadItems.containsKey(clientSyncItemView.getSyncItem().getId())) {
                continue;
            }

            if (!(clientSyncItemView instanceof ClientSyncBaseItemView)) {
                continue;
            }
            SyncBaseItem syncBaseItem = ((ClientSyncBaseItemView) clientSyncItemView).getSyncBaseItem();
            if (!syncBaseItem.getBase().equals(simpleBase)) {
                continue;
            }
            List<SyncBaseItem> syncBaseItems = result.get(syncBaseItem.getBaseItemType());
            if (syncBaseItems == null) {
                syncBaseItems = new ArrayList<SyncBaseItem>();
                result.put(syncBaseItem.getBaseItemType(), syncBaseItems);
            }
            syncBaseItems.add(syncBaseItem);
        }
        return result;
    }

    @Override
    public Index getRallyPoint(SyncBaseItem factory, Collection<SurfaceType> allowedSurfaces) {
        return factory.getPosition().add(factory.getItemType().getWidth() / 2 + 20, 0);
    }

    public void clear() {
        for (ClientSyncItemView clientSyncItemView : items.values()) {
            clientSyncItemView.dispose();
        }
        items.clear();
        specialItems.clear();
        orphanItems.clear();
        seeminglyDeadItems.clear();
    }
}
