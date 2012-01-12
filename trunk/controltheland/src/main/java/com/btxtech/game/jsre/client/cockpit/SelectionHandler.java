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

package com.btxtech.game.jsre.client.cockpit;

import com.btxtech.game.jsre.client.ClientSyncItem;
import com.btxtech.game.jsre.client.ClientSyncItemView;
import com.btxtech.game.jsre.client.action.ActionHandler;
import com.btxtech.game.jsre.client.cockpit.item.ItemCockpit;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.territory.ClientTerritoryService;
import com.btxtech.game.jsre.client.utg.SpeechBubbleHandler;
import com.btxtech.game.jsre.common.utg.tracking.SelectionTrackingItem;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * User: beat
 * Date: May 19, 2009
 * Time: 9:09:40 PM
 */
public class SelectionHandler {
    private final static SelectionHandler INSTANCE = new SelectionHandler();

    private Group selectedGroup; // Always my property
    private ClientSyncItem selectedTargetClientSyncItem; // Not my property
    private ArrayList<SelectionListener> listeners = new ArrayList<SelectionListener>();
    private boolean sellMode = false;

    public static SelectionHandler getInstance() {
        return INSTANCE;
    }

    /**
     * Singleton
     */
    private SelectionHandler() {

    }

    public void addSelectionListener(SelectionListener selectionListener) {
        if (!listeners.contains(selectionListener)) {
            listeners.add(selectionListener);
        }
    }

    public void removeSelectionListener(SelectionListener selectionListener) {
        listeners.remove(selectionListener);
    }

    public Group getOwnSelection() {
        return selectedGroup;
    }

    public boolean hasOwnSelection() {
        return selectedGroup != null && !selectedGroup.isEmpty();
    }

    public Collection<SurfaceType> getOwnSelectionSurfaceTypes() {
        if (selectedGroup != null) {
            return selectedGroup.getAllowedSurfaceTypes();
        } else {
            return new HashSet<SurfaceType>();
        }
    }

    public boolean atLeastOneAllowedOnTerrain4Selection() {
        return selectedGroup == null || ClientTerritoryService.getInstance().isAtLeastOneAllowed(selectedGroup.getSyncBaseItems());
    }

    public boolean atLeastOneAllowedOnTerritory4Selection(Index position) {
        return selectedGroup == null || ClientTerritoryService.getInstance().isAtLeastOneAllowed(position, selectedGroup.getSyncBaseItems());
    }

    public boolean atLeastOneItemTypeAllowed2Attack4Selection(SyncBaseItem syncBaseItem) {
        return selectedGroup == null || selectedGroup.atLeastOneItemTypeAllowed2Attack(syncBaseItem);
    }

    public boolean atLeastOneItemTypeAllowed2FinalizeBuild(SyncBaseItem tobeFinalized) {
        return selectedGroup == null || selectedGroup.atLeastOneItemTypeAllowed2FinalizeBuild(tobeFinalized);
    }

    public boolean atLeastOneAllowedToLaunch(Index position) {
        return selectedGroup == null || selectedGroup.atLeastOneAllowedToLaunch(position);
    }

    public void setTargetSelected(ClientSyncItemView selectedTargetClientSyncItem, MouseDownEvent event) {
        if (event.getNativeButton() == NativeEvent.BUTTON_LEFT) {
            if (selectedGroup != null) {
                if (selectedGroup.canAttack() && selectedTargetClientSyncItem.getClientSyncItem().isSyncBaseItem()) {
                    ActionHandler.getInstance().attack(selectedGroup.getItems(), selectedTargetClientSyncItem.getClientSyncItem().getSyncBaseItem());
                } else if (selectedGroup.canCollect() && selectedTargetClientSyncItem.getClientSyncItem().isSyncResourceItem()) {
                    ActionHandler.getInstance().collect(selectedGroup.getItems(), selectedTargetClientSyncItem.getClientSyncItem().getSyncResourceItem());
                }
            } else {
                this.selectedTargetClientSyncItem = selectedTargetClientSyncItem.getClientSyncItem();
                onTargetSelectionItemChanged(this.selectedTargetClientSyncItem);
            }
        }
    }

    public void setItemGroupSelected(Group selectedGroup) {
        if (hasOwnSelection() && selectedGroup.getCount() == 1) {
            if (selectedGroup.getFirst().getSyncBaseItem().hasSyncItemContainer()) {
                if(this.selectedGroup.canMove()) {
                    ActionHandler.getInstance().loadContainer(selectedGroup.getFirst(), this.selectedGroup.getItems());
                    clearSelection();
                    return;
                }
            } else if (!selectedGroup.getFirst().getSyncBaseItem().isReady()) {
                ActionHandler.getInstance().finalizeBuild(this.selectedGroup.getItems(), selectedGroup.getFirst());
                return;
            }
        }
        clearSelection();
        selectedGroup.setSelected(true);
        this.selectedGroup = selectedGroup;
        onOwnItemSelectionChanged(selectedGroup);
    }

    public void clearSelection() {
        if (selectedTargetClientSyncItem != null) {
            selectedTargetClientSyncItem.setSelected(false);
            selectedTargetClientSyncItem = null;
        }

        if (selectedGroup != null) {
            selectedGroup.setSelected(false);
            selectedGroup = null;
        }

        for (SelectionListener listener : listeners) {
            listener.onSelectionCleared();
        }
        CursorHandler.getInstance().onSelectionCleared();
        ItemCockpit.getInstance().deActivate();
        SpeechBubbleHandler.getInstance().hide();
    }

    private void onTargetSelectionItemChanged(ClientSyncItem selection) {
        for (SelectionListener listener : listeners) {
            listener.onTargetSelectionChanged(selection);
        }
        CursorHandler.getInstance().onSelectionCleared();
    }

    private void onOwnItemSelectionChanged(Group selection) {
        for (SelectionListener listener : listeners) {
            listener.onOwnSelectionChanged(selection);
        }
        CursorHandler.getInstance().onOwnSelectionChanged(selection);
    }


    public void itemKilled(ClientSyncItem clientSyncItem) {
        if (clientSyncItem.equals(selectedTargetClientSyncItem)) {
            clearSelection();
        }

        if (selectedGroup != null && selectedGroup.contains(clientSyncItem)) {
            selectedGroup.remove(clientSyncItem);
            if (selectedGroup.isEmpty()) {
                clearSelection();
            } else {
                onOwnItemSelectionChanged(selectedGroup);
            }
        }
    }

    public void refresh() {
        if (selectedGroup != null) {
            setItemGroupSelected(selectedGroup);
        } else if (selectedTargetClientSyncItem != null) {
            onTargetSelectionItemChanged(selectedTargetClientSyncItem);
        }
    }

    public void inject(SelectionTrackingItem selectionTrackingItem) {
        Boolean own = selectionTrackingItem.isOwn();
        if (own == null) {
            clearSelection();
        } else if (own) {
            Group group = new Group();
            for (int id : selectionTrackingItem.getSelectedIds()) {
                group.addItem(ItemContainer.getInstance().getSimulationItem(id));
            }
            setItemGroupSelected(group);
        } else {
            int id = selectionTrackingItem.getSelectedIds().iterator().next();
            selectedTargetClientSyncItem = ItemContainer.getInstance().getSimulationItem(id);
            onTargetSelectionItemChanged(selectedTargetClientSyncItem);
        }
    }

    public void setSellMode(boolean sellMode) {
        if (this.sellMode == sellMode) {
            return;
        }
        this.sellMode = sellMode;
        if (sellMode) {
            clearSelection();
        }
        if(!sellMode) {
            SideCockpit.getInstance().clearSellMode();
        }
        CursorHandler.getInstance().setSell(sellMode);
    }

    public boolean isSellMode() {
        return sellMode;
    }
}