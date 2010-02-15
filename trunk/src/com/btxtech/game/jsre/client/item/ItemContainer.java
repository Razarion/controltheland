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
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.action.ActionHandler;
import com.btxtech.game.jsre.client.cockpit.SelectionHandler;
import com.btxtech.game.jsre.client.cockpit.radar.RadarPanel;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.effects.ExplosionHandler;
import com.btxtech.game.jsre.client.utg.ClientUserGuidance;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.base.BaseService;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.services.items.impl.AbstractItemService;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncResourceItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.syncInfos.SyncItemInfo;
import com.google.gwt.user.client.Timer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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
    private HashMap<Id, ClientSyncItemView> items = new HashMap<Id, ClientSyncItemView>();
    private HashSet<ClientSyncBaseItemView> specialItems = new HashSet<ClientSyncBaseItemView>();
    private HashMap<Id, ClientSyncItemView> orphanItems = new HashMap<Id, ClientSyncItemView>();
    private HashMap<Id, ClientSyncItemView> deadItems = new HashMap<Id, ClientSyncItemView>();

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
                for (Iterator<Map.Entry<Id, ClientSyncItemView>> it = deadItems.entrySet().iterator(); it.hasNext();) {
                    Map.Entry<Id, ClientSyncItemView> entry = it.next();
                    long insertTime = entry.getKey().getUserTimeStamp();
                    if (insertTime + CLEANUP_INTERVALL < System.currentTimeMillis()) {
                        it.remove();
                        GwtCommon.sendLogToServer("Can not definitely kill item due to missing ack from server: " + entry.getValue());
                    }

                }
            }
        };
        timer.scheduleRepeating(CLEANUP_INTERVALL);
    }

    public void sychronize(SyncItemInfo syncItemInfo) throws NoSuchItemTypeException {
        ClientSyncItemView clientSyncItemView = items.get(syncItemInfo.getId());

        if (syncItemInfo.isAlive()) {
            if (clientSyncItemView == null) {
                clientSyncItemView = createAndAddItem(syncItemInfo.getId(), syncItemInfo.getPosition(), syncItemInfo.getItemTypeId(), syncItemInfo.getBase());
                ClientUserGuidance.getInstance().onItemCreated(clientSyncItemView);
                checkSpecialAdded(clientSyncItemView);
            } else {
                // Check for  Teleportation effect
                int distance = clientSyncItemView.getSyncItem().getPosition().getDistance(syncItemInfo.getPosition());
                if (distance > 100) {
                    GwtCommon.sendLogToServer("Teleportation detected. Distance: " + distance + " Info:" + syncItemInfo + " | Item:" + clientSyncItemView.getSyncItem());
                }
                ClientSyncItemView orphanItem = orphanItems.remove(clientSyncItemView.getSyncItem().getId());
                if (orphanItem != null) {
                    orphanItem.setVisible(true);
                    ClientUserGuidance.getInstance().onItemCreated(orphanItem);
                    checkSpecialAdded(clientSyncItemView);
                }
            }
            clientSyncItemView.getSyncItem().synchronize(syncItemInfo);
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
        Id id = new Id(creator.getId().getId(), createdChildCount);
        ClientSyncItemView itemView = items.get(id);
        if (itemView != null) {
            return itemView.getSyncItem();
        }
        itemView = createAndAddItem(id, position, toBeBuilt.getId(), base);
        id.setUserTimeStamp(System.currentTimeMillis());
        orphanItems.put(id, itemView);
        itemView.setVisible(false);
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
            throw new IllegalArgumentException(this + " unknwon SyncItem: " + syncItem);
        }
        items.put(id, itemView);
        return itemView;
    }

    @Override
    public void killBaseSyncObject(SyncItem syncItem, SyncBaseItem actor) {
        ClientSyncItemView clientSyncItemView = items.get(syncItem.getId());
        if (items.containsKey(syncItem.getId())) {
            deadItems.put(syncItem.getId(), clientSyncItemView);
        } else {
            GwtCommon.sendLogToServer("This sould never happen: ItemContainer.killBaseSyncObject() syncItem:" + syncItem + " actor:" + actor);
        }
    }

    private void definitelyKillItem(ClientSyncItemView itemView) {
        items.remove(itemView.getSyncItem().getId());
        checkSpecialRemoved(itemView);
        deadItems.remove(itemView.getSyncItem().getId());
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
                    !deadItems.containsKey(clientBaseItem.getSyncItem().getId()) &&
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

    public Collection<ClientSyncBaseItemView> getOwnItems() {
        ArrayList<ClientSyncBaseItemView> clientBaseItems = new ArrayList<ClientSyncBaseItemView>();
        for (ClientSyncItemView clientBaseItem : items.values()) {
            if (clientBaseItem instanceof ClientSyncBaseItemView &&
                    ((ClientSyncBaseItemView) clientBaseItem).isMyOwnProperty() &&
                    !orphanItems.containsKey(clientBaseItem.getSyncItem().getId()) &&
                    !deadItems.containsKey(clientBaseItem.getSyncItem().getId())) {
                clientBaseItems.add((ClientSyncBaseItemView) clientBaseItem);
            }
        }
        return clientBaseItems;
    }


    @Override
    protected BaseService getBaseService() {
        return ClientBase.getInstance();
    }

    private void checkSpecialItem(ClientSyncItemView clientSyncItemView) {
        if (isMySpecialItem(clientSyncItemView) != null) {
            RadarPanel.getInstance().setRadarState(checkForSpecialItem(RadarPanel.RADAR_1));
        }
    }

    private void checkSpecialAdded(ClientSyncItemView clientSyncItemView) {
        ClientSyncBaseItemView clientSyncBaseItemView = isMySpecialItem(clientSyncItemView);
        if (clientSyncBaseItemView == null) {
            return;
        }

        specialItems.add(clientSyncBaseItemView);
        RadarPanel.getInstance().setRadarState(checkForSpecialItem(RadarPanel.RADAR_1));
    }

    private void checkSpecialRemoved(ClientSyncItemView clientSyncItemView) {
        ClientSyncBaseItemView clientSyncBaseItemView = isMySpecialItem(clientSyncItemView);
        if (clientSyncBaseItemView == null) {
            return;
        }
        specialItems.remove(clientSyncBaseItemView);
        RadarPanel.getInstance().setRadarState(checkForSpecialItem(RadarPanel.RADAR_1));
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


}
