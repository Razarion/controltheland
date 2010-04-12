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

import com.btxtech.game.jsre.client.ClientSyncBaseItemView;
import com.btxtech.game.jsre.client.ClientSyncItemView;
import com.btxtech.game.jsre.client.ClientSyncResourceItemView;
import com.btxtech.game.jsre.client.action.ActionHandler;
import com.btxtech.game.jsre.client.utg.ClientUserTracker;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import java.util.ArrayList;

/**
 * User: beat
 * Date: May 19, 2009
 * Time: 9:09:40 PM
 */
public class SelectionHandler {
    private final static SelectionHandler INSTANCE = new SelectionHandler();

    private Group selectedGroup; // Always my property
    private ClientSyncItemView selectedTargetClientSyncItem; // Not my property
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
        listeners.add(selectionListener);
    }

    public Group getOwnSelection() {
        return selectedGroup;
    }

    public void setTargetSelected(ClientSyncItemView selectedTargetClientSyncItem, MouseDownEvent event) {
        if (event.getNativeButton() == NativeEvent.BUTTON_LEFT) {
            if (selectedGroup != null) {
                if (selectedGroup.canAttack() && selectedTargetClientSyncItem instanceof ClientSyncBaseItemView) {
                    ActionHandler.getInstance().attack(selectedGroup.getItems(), ((ClientSyncBaseItemView) selectedTargetClientSyncItem).getSyncBaseItem());
                } else if (selectedGroup.canCollect() && selectedTargetClientSyncItem instanceof ClientSyncResourceItemView) {
                    ActionHandler.getInstance().collect(selectedGroup.getItems(), ((ClientSyncResourceItemView) selectedTargetClientSyncItem).getSyncResourceItem());
                }
            } else {
                this.selectedTargetClientSyncItem = selectedTargetClientSyncItem;
                onTargetSelectionItemChanged(selectedTargetClientSyncItem);
            }
        }
    }

    public void setItemGroupSelected(Group selectedGroup) {
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
    }

    private void onTargetSelectionItemChanged(ClientSyncItemView selection) {
        for (SelectionListener listener : listeners) {
            listener.onTargetSelectionChanged(selection);
        }
        CursorHandler.getInstance().onSelectionCleared();
        ClientUserTracker.getInstance().onTargetSelectionItemChanged(selection);
    }

    private void onOwnItemSelectionChanged(Group selection) {
        for (SelectionListener listener : listeners) {
            listener.onOwnSelectionChanged(selection);
        }
        CursorHandler.getInstance().onOwnSelectionChanged(selection);
        ClientUserTracker.getInstance().onOwnItemSelectionChanged(selection);
    }


    public void itemKilled(ClientSyncItemView clientSyncItemView) {
        if (clientSyncItemView.equals(selectedTargetClientSyncItem)) {
            clearSelection();
        }

        if (selectedGroup != null && selectedGroup.contains(clientSyncItemView)) {
            selectedGroup.remove(clientSyncItemView);
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

}