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

/**
 * User: beat
 * Date: 15.08.2010
 * Time: 21:10:20
 */
package com.btxtech.game.jsre.client.item;

import com.btxtech.game.jsre.client.ClientSyncItem;
import com.btxtech.game.jsre.client.ClientSyncItemView;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class ItemViewContainer {
    private static final ItemViewContainer INSTANCE = new ItemViewContainer();
    private HashMap<ClientSyncItem, ClientSyncItemView> visibleItems = new HashMap<ClientSyncItem, ClientSyncItemView>();
    private ArrayList<ClientSyncItemView> unusedItems = new ArrayList<ClientSyncItemView>();

    public static ItemViewContainer getInstance() {
        return INSTANCE;
    }

    /**
     * Singleton
     */
    private ItemViewContainer() {
    }

    public void onSyncItemVisible(ClientSyncItem clientSyncItem) {
        ClientSyncItemView clientSyncItemView = getClientSyncItemView();
        visibleItems.put(clientSyncItem, clientSyncItemView);
        clientSyncItemView.transform(clientSyncItem);
    }

    public void onSyncItemInvisible(ClientSyncItem clientSyncItem) {
        ClientSyncItemView clientSyncItemView = visibleItems.remove(clientSyncItem);
        clientSyncItemView.transform(null);
        putClientSyncItemView(clientSyncItemView);
    }

    public void onSelectionChanged(ClientSyncItem clientSyncItem, boolean selected) {
        ClientSyncItemView clientSyncItemView = visibleItems.get(clientSyncItem);
        if (clientSyncItemView != null) {
            clientSyncItemView.setSelected(selected);
        }
    }

    public void updateSyncItemView(ClientSyncItem clientSyncItem) {
        ClientSyncItemView clientSyncItemView = visibleItems.get(clientSyncItem);
        if (clientSyncItemView != null) {
            clientSyncItemView.update();
        }
    }

    public void removeSyncItemView(ClientSyncItem clientSyncItem) {
        ClientSyncItemView clientSyncItemView = visibleItems.remove(clientSyncItem);
        if (clientSyncItemView != null) {
            putClientSyncItemView(clientSyncItemView);
        }
    }

    private ClientSyncItemView getClientSyncItemView() {
        if (unusedItems.isEmpty()) {
            return new ClientSyncItemView();
        } else {
            return unusedItems.remove(0);
        }
    }

    private void putClientSyncItemView(ClientSyncItemView clientSyncItemView) {
        clientSyncItemView.transform(null);
        unusedItems.add(clientSyncItemView);
    }

    public Collection<ClientSyncItemView> getVisibleItems() {
        return visibleItems.values();
    }
}
