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
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncResourceItem;
import com.google.gwt.user.client.DOM;
import com.google.gwt.widgetideas.graphics.client.GWTCanvas;

/**
 * User: beat
 * Date: Jun 27, 2009
 * Time: 9:43:48 AM
 */
public class CursorHandler {
    private static CursorHandler INSTANCE = new CursorHandler();
    private boolean hasAttackCursor = false;
    private boolean hasCollectCursor = false;

    /**
     * Singleton
     */
    private CursorHandler() {

    }

    public void setAttackCursor() {
        hasAttackCursor = true;
        for (ClientSyncItemView item : ItemContainer.getInstance().getItems()) {
            if (item instanceof ClientSyncBaseItemView && !((ClientSyncBaseItemView) item).isMyOwnProperty()) {
                DOM.setStyleAttribute(item.getElement(), "cursor", "url(/images/cursors/attack.cur), crosshair");
            }
        }
    }

    public void removeAttackCursor() {
        hasAttackCursor = false;
        for (ClientSyncItemView item : ItemContainer.getInstance().getItems()) {
            if (item instanceof ClientSyncBaseItemView && !((ClientSyncBaseItemView) item).isMyOwnProperty()) {
                DOM.setStyleAttribute(item.getElement(), "cursor", "pointer");
            }
        }
    }

    public void setCollectCursor() {
        hasCollectCursor = true;
        for (ClientSyncItemView item : ItemContainer.getInstance().getItems()) {
            if (item instanceof ClientSyncResourceItemView) {
                DOM.setStyleAttribute(item.getElement(), "cursor", "url(/images/cursors/collect.cur), crosshair");
            }
        }

    }

    public void removeCollectCursor() {
        hasCollectCursor = false;
        for (ClientSyncItemView item : ItemContainer.getInstance().getItems()) {
            if (item.getSyncItem() instanceof SyncResourceItem) {
                DOM.setStyleAttribute(item.getElement(), "cursor", "pointer");
            }
        }
    }

    public void handleCursorOnNewItems(ClientSyncItemView view) {
        if (view instanceof ClientSyncResourceItemView) {
            if (hasCollectCursor) {
                DOM.setStyleAttribute(view.getElement(), "cursor", "url(/images/cursors/collect.cur), crosshair");
            } else {
                DOM.setStyleAttribute(view.getElement(), "cursor", "pointer");
            }
        } else if (view instanceof ClientSyncBaseItemView && !((ClientSyncBaseItemView) view).isMyOwnProperty()) {
            if (hasAttackCursor) {
                DOM.setStyleAttribute(view.getElement(), "cursor", "url(/images/cursors/attack.cur), crosshair");
            } else {
                DOM.setStyleAttribute(view.getElement(), "cursor", "pointer");
            }
        } else {
            DOM.setStyleAttribute(view.getElement(), "cursor", "pointer");
        }
    }

    public void setMoveCursor() {
        GWTCanvas terrain = TerrainView.getInstance().getCanvas();
        DOM.setStyleAttribute(terrain.getElement(), "cursor", "url(/images/cursors/go.cur), crosshair");
    }

    public void removeMoveCursor() {
        GWTCanvas terrain = TerrainView.getInstance().getCanvas();
        DOM.setStyleAttribute(terrain.getElement(), "cursor", "default");
    }

    public static CursorHandler getInstance() {
        return INSTANCE;
    }

    public void onSelectionCleared() {
        removeMoveCursor();
        removeAttackCursor();
        removeCollectCursor();
    }

    public void onOwnSelectionChanged(Group selection) {
        if (selection.canMove()) {
            setMoveCursor();
        }

        if (selection.canAttack()) {
            setAttackCursor();
        }

        if (selection.canCollect()) {
            setCollectCursor();
        }
    }
}