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

package com.btxtech.game.jsre.client;

import com.btxtech.game.jsre.client.cockpit.SelectionHandler;
import com.btxtech.game.jsre.client.cockpit.CursorHandler;
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.utg.ClientUserTracker;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncResourceItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.google.gwt.event.dom.client.MouseDownEvent;

/**
 * User: beat
 * Date: 05.12.2009
 * Time: 16:42:17
 */
public class ClientSyncResourceItemView extends ClientSyncItemView {
    private SyncResourceItem syncResourceItem;

    public ClientSyncResourceItemView(SyncResourceItem syncResourceItem) {
        super(syncResourceItem);
        this.syncResourceItem = syncResourceItem;
        setZIndex();
        CursorHandler.getInstance().handleCursorOnNewItems(this);
    }

    private void setZIndex() {
        getElement().getStyle().setZIndex(Constants.Z_INDEX_MONEY);
    }

    @Override
    public void onMouseDown(MouseDownEvent mouseDownEvent) {
        SelectionHandler.getInstance().setTargetSelected(this, mouseDownEvent);
        ClientUserTracker.getInstance().clickResourceItem(syncResourceItem);

        // Just to prevent image dragging
        mouseDownEvent.stopPropagation();
        mouseDownEvent.preventDefault();

    }

    @Override
    public void onItemChanged(Change change, SyncItem syncItem) {
        switch (change) {
            case ANGEL:
                setupImage();
                break;
            case POSITION:
                setPosition();
                break;
        }
    }

    @Override
    public void update() {
        setupImage();
        setPosition();
    }

    public SyncResourceItem getSyncResourceItem() {
        return syncResourceItem;
    }
}
