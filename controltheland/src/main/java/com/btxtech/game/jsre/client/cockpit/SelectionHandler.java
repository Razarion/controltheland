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

import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.action.ActionHandler;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBoxItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncResourceItem;
import com.btxtech.game.jsre.common.utg.tracking.SelectionTrackingItem;
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
    private SyncItem selectedTargetSyncItem; // Not my property
    private ArrayList<SelectionListener> listeners = new ArrayList<SelectionListener>();

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

    public boolean atLeastOneItemTypeAllowed2Attack4Selection(SyncBaseItem syncBaseItem) {
        return selectedGroup == null || selectedGroup.atLeastOneItemTypeAllowed2Attack(syncBaseItem);
    }

    public boolean atLeastOneItemTypeAllowed2FinalizeBuild(SyncBaseItem tobeFinalized) {
        return selectedGroup == null || selectedGroup.atLeastOneItemTypeAllowed2FinalizeBuild(tobeFinalized);
    }

    public void setTargetSelected(SyncItem target, MouseDownEvent event) {
        if (event.getNativeButton() == NativeEvent.BUTTON_LEFT) {
            if (selectedGroup != null) {
                if (selectedGroup.canAttack() && target instanceof SyncBaseItem) {
                    ActionHandler.getInstance().attack(selectedGroup.getItems(), (SyncBaseItem) target);
                } else if (selectedGroup.canCollect() && target instanceof SyncResourceItem) {
                    ActionHandler.getInstance().collect(selectedGroup.getItems(), (SyncResourceItem) target);
                } else if (target instanceof SyncBoxItem) {
                    ActionHandler.getInstance().pickupBox(selectedGroup.getItems(), (SyncBoxItem) target);
                }
            } else {
                this.selectedTargetSyncItem = target;
                onTargetSelectionItemChanged(this.selectedTargetSyncItem);
            }
        }
    }

    public void setItemGroupSelected(Group selectedGroup) {
        if (hasOwnSelection() && selectedGroup.getCount() == 1) {
            if (selectedGroup.getFirst().hasSyncItemContainer()) {
                if (!this.selectedGroup.equals(selectedGroup) && this.selectedGroup.canMove()) {
                    ActionHandler.getInstance().loadContainer(selectedGroup.getFirst(), this.selectedGroup.getItems());
                    clearSelection();
                    return;
                }
            } else if (!selectedGroup.getFirst().isReady()) {
                ActionHandler.getInstance().finalizeBuild(this.selectedGroup.getItems(), selectedGroup.getFirst());
                return;
            }
        }
        clearSelection();
        this.selectedGroup = selectedGroup;
        onOwnItemSelectionChanged(selectedGroup);
    }

    public void keepOnlyOwnOfType(BaseItemType baseItemType) {
        selectedGroup.keepOnlyOwnOfType(baseItemType);
        onOwnItemSelectionChanged(selectedGroup);
    }
    
    public void clearSelection() {
        selectedTargetSyncItem = null;
        selectedGroup = null;

        for (SelectionListener listener : new ArrayList<SelectionListener>(listeners)) {
            listener.onSelectionCleared();
        }
        CursorHandler.getInstance().onSelectionCleared();
    }

    private void onTargetSelectionItemChanged(SyncItem selection) {
        for (SelectionListener listener : new ArrayList<SelectionListener>(listeners)) {
            listener.onTargetSelectionChanged(selection);
        }
        CursorHandler.getInstance().onSelectionCleared();
    }

    private void onOwnItemSelectionChanged(Group selection) {
        for (SelectionListener listener : new ArrayList<SelectionListener>(listeners)) {
            listener.onOwnSelectionChanged(selection);
        }
    }

    public void itemKilled(SyncItem syncItem) {
        if (syncItem.equals(selectedTargetSyncItem)) {
            clearSelection();
        }

        if (selectedGroup != null && syncItem instanceof SyncBaseItem && selectedGroup.contains((SyncBaseItem) syncItem)) {
            selectedGroup.remove((SyncBaseItem) syncItem);
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
        } else if (selectedTargetSyncItem != null) {
            onTargetSelectionItemChanged(selectedTargetSyncItem);
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
            selectedTargetSyncItem = ItemContainer.getInstance().getSimulationItem(id);
            onTargetSelectionItemChanged(selectedTargetSyncItem);
        }
    }

    public SyncItem getSelectedTargetSyncItem() {
        return selectedTargetSyncItem;
    }

}