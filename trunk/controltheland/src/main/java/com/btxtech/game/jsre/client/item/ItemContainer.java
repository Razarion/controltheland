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
import com.btxtech.game.jsre.client.ClientSyncItem;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.action.ActionHandler;
import com.btxtech.game.jsre.client.cockpit.Cockpit;
import com.btxtech.game.jsre.client.cockpit.SelectionHandler;
import com.btxtech.game.jsre.client.cockpit.radar.RadarPanel;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.effects.ExplosionHandler;
import com.btxtech.game.jsre.client.simulation.SimulationConditionServiceImpl;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.ProjectileItemType;
import com.btxtech.game.jsre.common.gameengine.services.base.AbstractBaseService;
import com.btxtech.game.jsre.common.gameengine.services.base.HouseSpaceExceededException;
import com.btxtech.game.jsre.common.gameengine.services.base.ItemLimitExceededException;
import com.btxtech.game.jsre.common.gameengine.services.collision.CommonCollisionService;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.services.items.impl.AbstractItemService;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItemListener;
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
    private HashMap<Id, ClientSyncItem> items = new HashMap<Id, ClientSyncItem>();
    private HashSet<ClientSyncItem> specialItems = new HashSet<ClientSyncItem>();
    private HashMap<Id, ClientSyncItem> orphanItems = new HashMap<Id, ClientSyncItem>();
    private HashMap<Id, ClientSyncItem> seeminglyDeadItems = new HashMap<Id, ClientSyncItem>();
    private int ownItemCount = 0;

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
                checkSpecialAdded(clientSyncItem);
            } else {
                // Check for  Teleportation effect
                Index localPos = clientSyncItem.getSyncItem().getPosition();
                Index syncPos = syncItemInfo.getPosition();
                if (localPos != null && syncPos != null) {
                    int distance = localPos.getDistance(syncPos);
                    if (distance > 200) {
                        GwtCommon.sendLogToServer("Teleportation detected. Distance: " + distance + " Info:" + syncItemInfo + " | Item:" + clientSyncItem.getSyncItem());
                    }
                }
                ClientSyncItem orphanItem = orphanItems.remove(clientSyncItem.getSyncItem().getId());
                if (orphanItem != null) {
                    orphanItem.setHidden(false);
                    checkSpecialAdded(clientSyncItem);
                }
            }
            clientSyncItem.getSyncItem().synchronize(syncItemInfo);
            clientSyncItem.checkVisibility();
            clientSyncItem.update();
            checkSpecialItem(clientSyncItem);
            if (clientSyncItem.isSyncTickItem()) {
                ActionHandler.getInstance().syncItemActivated(clientSyncItem.getSyncTickItem());
            }
        } else {
            if (clientSyncItem != null) {
                definitelyKillItem(clientSyncItem, syncItemInfo.isExplode());
            }

        }
    }

    @Override
    public SyncItem createSyncObject(ItemType toBeBuilt, Index position, SyncBaseItem creator, SimpleBase base, int createdChildCount) throws NoSuchItemTypeException, ItemLimitExceededException, HouseSpaceExceededException {
        if (toBeBuilt instanceof BaseItemType && ClientBase.getInstance().isMyOwnBase(base) && !ClientBase.getInstance().isBot(base) && Connection.getInstance().getGameInfo().hasServerCommunication()) {
            ClientBase.getInstance().checkItemLimit4ItemAdding((BaseItemType) toBeBuilt);
        }
        ClientSyncItem itemView;
        if (Connection.getInstance().getGameInfo().hasServerCommunication()) {
            Id id = new Id(creator.getId().getId(), createdChildCount);
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
        } else {
            Id id = createSimulationId();
            itemView = createAndAddItem(id, position, toBeBuilt.getId(), base);
            itemView.setHidden(false);
            id.setUserTimeStamp(System.currentTimeMillis());
        }
        itemView.checkVisibility();
        itemView.update();
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

    public ClientSyncItem getSimulationItem(int intId) {
        for (Map.Entry<Id, ClientSyncItem> entry : items.entrySet()) {
            if (entry.getKey().getId() == intId) {
                return entry.getValue();
            }
        }
        throw new IllegalArgumentException(this + " getSimulationItem(): no ClientSyncItem for id: " + intId);
    }

    public SyncItem createSimulationSyncObject(ItemTypeAndPosition itemTypeAndPosition) throws NoSuchItemTypeException {
        Id id = createSimulationId(itemTypeAndPosition.getId());
        if (items.containsKey(id)) {
            throw new IllegalStateException(this + " simulated id is already used: " + id);
        }
        ClientSyncItem itemView = createAndAddItem(id, itemTypeAndPosition.getPosition(), itemTypeAndPosition.getItemTypeId(), itemTypeAndPosition.getBase());
        id.setUserTimeStamp(System.currentTimeMillis());
        if (itemView.getSyncItem() instanceof SyncBaseItem) {
            SyncBaseItem syncBaseItem = (SyncBaseItem) itemView.getSyncItem();
            syncBaseItem.setBuildup(1.0);
            if (syncBaseItem.hasSyncTurnable()) {
                syncBaseItem.getSyncTurnable().setAngel(itemTypeAndPosition.getAngel());
                syncBaseItem.fireItemChanged(SyncItemListener.Change.ANGEL);
            }
        }
        itemView.checkVisibility();
        itemView.update();
        return itemView.getSyncItem();
    }

    private ClientSyncItem createAndAddItem(Id id, Index position, int itemTypeId, SimpleBase base) throws NoSuchItemTypeException {
        SyncItem syncItem = newSyncItem(id, position, itemTypeId, base, ClientServices.getInstance());
        ClientSyncItem itemView = new ClientSyncItem(syncItem);
        items.put(id, itemView);
        if (itemView.isMyOwnProperty()) {
            ownItemCount++;
            Cockpit.getInstance().updateItemLimit();
        }
        return itemView;
    }

    @Override
    public void killSyncItem(SyncItem killedItem, SimpleBase actor, boolean force, boolean explode) {
        ClientSyncItem ClientSyncItem = items.get(killedItem.getId());
        if (Connection.getInstance().getGameInfo().hasServerCommunication()) {
            makeItemSeeminglyDead(killedItem, actor, ClientSyncItem);
        } else {
            definitelyKillItem(ClientSyncItem, explode);
        }
        if (killedItem instanceof SyncBaseItem) {
            SimulationConditionServiceImpl.getInstance().onSyncItemKilled(actor, (SyncBaseItem) killedItem);
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

    private void definitelyKillItem(ClientSyncItem itemView, boolean explode) {
        items.remove(itemView.getSyncItem().getId());
        if (itemView.isMyOwnProperty()) {
            ownItemCount--;
            Cockpit.getInstance().updateItemLimit();
            ClientBase.getInstance().recalculate4FakedHouseSpace(itemView.getSyncBaseItem());
        }
        checkSpecialRemoved(itemView);
        seeminglyDeadItems.remove(itemView.getSyncItem().getId());
        SelectionHandler.getInstance().itemKilled(itemView);

        if (explode) {
            ActionHandler.getInstance().removeActiveItem(itemView.getSyncTickItem());
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

    public Collection<ClientSyncItem> getItemsInRect(Rectangle rectangle, boolean onlyOwnItems) {
        ArrayList<ClientSyncItem> clientBaseItems = new ArrayList<ClientSyncItem>();
        for (ClientSyncItem clientSyncItem : items.values()) {
            if (clientSyncItem.isSyncBaseItem() &&
                    !orphanItems.containsKey(clientSyncItem.getSyncItem().getId()) &&
                    !seeminglyDeadItems.containsKey(clientSyncItem.getSyncItem().getId()) &&
                    rectangle.contains(clientSyncItem.getSyncItem().getPosition())) {
                if (onlyOwnItems) {
                    if (clientSyncItem.isMyOwnProperty()) {
                        clientBaseItems.add(clientSyncItem);
                    }
                } else {
                    clientBaseItems.add(clientSyncItem);
                }
            }
        }
        return clientBaseItems;
    }

    public Collection<SyncBaseItem> getBaseItemsInRectangle(Rectangle rectangle, SimpleBase simpleBase, Collection<BaseItemType> baseItemTypeFilter) {
        ArrayList<SyncBaseItem> result = new ArrayList<SyncBaseItem>();
        for (ClientSyncItem clientSyncItem : items.values()) {
            if (clientSyncItem.isSyncBaseItem()
                    && !orphanItems.containsKey(clientSyncItem.getSyncItem().getId())
                    && !seeminglyDeadItems.containsKey(clientSyncItem.getSyncItem().getId())) {
                SyncBaseItem syncBaseItem = clientSyncItem.getSyncBaseItem();
                if (simpleBase != null && !(syncBaseItem.getBase().equals(simpleBase))) {
                    continue;
                }
                if (!rectangle.contains(syncBaseItem.getPosition())) {
                    continue;
                }
                if (baseItemTypeFilter != null && !baseItemTypeFilter.contains(syncBaseItem.getBaseItemType())) {
                    continue;
                }
                result.add(syncBaseItem);
            }
        }
        return result;
    }


    public ClientSyncItem getFirstItemInRange(ItemType itemType, Index origin, int maxRange) {
        for (ClientSyncItem syncItemView : items.values()) {
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
        for (ClientSyncItem clientSyncItem : items.values()) {
            if (clientSyncItem.isSyncBaseItem() &&
                    !orphanItems.containsKey(clientSyncItem.getSyncItem().getId()) &&
                    !seeminglyDeadItems.containsKey(clientSyncItem.getSyncItem().getId()) &&
                    !(clientSyncItem.getSyncBaseItem()).hasSyncMovable() &&
                    rectangle.adjoinsEclusive(clientSyncItem.getSyncItem().getRectangle())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasStandingItemsInRect(Rectangle rectangle, SyncItem exceptThat) {
        return false;
    }

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

    public List<SyncBaseItem> getEnemyItems(SimpleBase base, Rectangle region) {
        ArrayList<SyncBaseItem> clientBaseItems = new ArrayList<SyncBaseItem>();
        for (ClientSyncItem clientSyncItem : items.values()) {
            if (clientSyncItem.isSyncBaseItem() &&
                    !clientSyncItem.getSyncBaseItem().getBase().equals(base) &&
                    !orphanItems.containsKey(clientSyncItem.getSyncItem().getId()) &&
                    !seeminglyDeadItems.containsKey(clientSyncItem.getSyncItem().getId()) &&
                    region.contains(clientSyncItem.getSyncItem().getPosition())) {
                clientBaseItems.add(clientSyncItem.getSyncBaseItem());
            }
        }
        return clientBaseItems;
    }

    public boolean hasOwnAttackingMovable() {
        for (ClientSyncItem clientSyncItem : items.values()) {
            if (clientSyncItem.isSyncBaseItem()
                    && clientSyncItem.isMyOwnProperty()
                    && !orphanItems.containsKey(clientSyncItem.getSyncItem().getId())
                    && !seeminglyDeadItems.containsKey(clientSyncItem.getSyncItem().getId())
                    && clientSyncItem.getSyncBaseItem().hasSyncWeapon()
                    && clientSyncItem.getSyncBaseItem().hasSyncMovable()) {
                return true;
            }
        }
        return false;
    }

    public List<? extends SyncItem> getItems(ItemType itemType, SimpleBase simpleBase) {
        ArrayList<SyncItem> syncItems = new ArrayList<SyncItem>();
        for (ClientSyncItem clientSyncItem : items.values()) {
            SyncItem syncItem = clientSyncItem.getSyncItem();
            if (orphanItems.containsKey(syncItem.getId()) || seeminglyDeadItems.containsKey(syncItem.getId())) {
                continue;
            }

            if (!syncItem.getItemType().equals(itemType)) {
                continue;
            }

            if (simpleBase != null) {
                if (syncItem instanceof SyncBaseItem && clientSyncItem.getSyncBaseItem().getBase().equals(simpleBase)) {
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
    protected AbstractBaseService getBaseService() {
        return ClientBase.getInstance();
    }

    private void checkSpecialItem(ClientSyncItem ClientSyncItem) {
        if (isMySpecialItem(ClientSyncItem) != null) {
            checkForSpecialItems();
        }
    }

    private void checkSpecialAdded(ClientSyncItem clientSyncItem) {
        ClientSyncItem specialClientSyncItem = isMySpecialItem(clientSyncItem);
        if (specialClientSyncItem == null) {
            return;
        }
        specialItems.add(specialClientSyncItem);
        checkForSpecialItems();
    }

    private void checkSpecialRemoved(ClientSyncItem ClientSyncItem) {
        ClientSyncItem specialClientSyncItem = isMySpecialItem(ClientSyncItem);
        if (specialClientSyncItem == null) {
            return;
        }
        specialItems.remove(specialClientSyncItem);
        checkForSpecialItems();
    }

    private void checkForSpecialItems() {
        RadarPanel.getInstance().setRadarState1(checkForSpecialItem(RadarPanel.RADAR_1));
    }

    public void handleSpecial(ClientSyncItem clientSyncItem) {
        if (!clientSyncItem.isMyOwnProperty() || !clientSyncItem.isSyncBaseItem()) {
            return;
        }

        if (specialItems.contains(clientSyncItem)) {
            if (!clientSyncItem.getSyncBaseItem().hasSyncSpecial()) {
                checkSpecialRemoved(clientSyncItem);
            } else {
                checkForSpecialItems();
            }
        } else {
            if (clientSyncItem.getSyncBaseItem().hasSyncSpecial()) {
                checkSpecialAdded(clientSyncItem);
            }
        }
    }

    private ClientSyncItem isMySpecialItem(ClientSyncItem clientSyncItem) {
        if (!clientSyncItem.isMyOwnProperty()) {
            return null;
        }

        if (!clientSyncItem.isSyncBaseItem()) {
            return null;
        }

        if (!clientSyncItem.getSyncBaseItem().hasSyncSpecial()) {
            return null;
        }
        return clientSyncItem;
    }

    private boolean checkForSpecialItem(String string) {
        for (ClientSyncItem specialItem : specialItems) {
            SyncBaseItem syncBaseItem = specialItem.getSyncBaseItem();
            if (syncBaseItem.getSyncSpecial().getString().equals(string) && syncBaseItem.isReady()) {
                return true;
            }
        }
        return false;
    }

    public Map<BaseItemType, List<SyncBaseItem>> getItems4Base(SimpleBase simpleBase) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Index getRallyPoint(SyncBaseItem factory, Collection<SurfaceType> allowedSurfaces) {
        return factory.getPosition().add(factory.getItemType().getWidth() / 2 + 20, 0);
    }

    @Override
    public Index getDestinationHint(SyncBaseItem syncBaseItem, int range, SyncItem target, Index targetPosition) {
        return targetPosition;
    }

    public void clear() {
        for (ClientSyncItem ClientSyncItem : items.values()) {
            ClientSyncItem.dispose();
        }
        items.clear();
        specialItems.clear();
        orphanItems.clear();
        seeminglyDeadItems.clear();
        ownItemCount = 0;
    }

    public int getOwnItemCount() {
        return ownItemCount;
    }
}
