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
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.terrain.MapWindow;
import com.btxtech.game.jsre.client.terrain.TerrainMouseMoveListener;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncResourceItem;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.widgetideas.graphics.client.GWTCanvas;
import java.util.Collection;

/**
 * User: beat
 * Date: Jun 27, 2009
 * Time: 9:43:48 AM
 */
public class CursorHandler implements TerrainMouseMoveListener {
    public static final String CURSO_ATTACK = "/images/cursors/attack.cur";
    public static final String CURSO_COLLECT = "/images/cursors/collect.cur";
    public static final String CURSO_GO = "/images/cursors/go.cur";
    public static final String CURSO_NOGO = "/images/cursors/nogo.cur";
    private static CursorHandler INSTANCE = new CursorHandler();
    private boolean hasAttackCursor = false;
    private boolean hasCollectCursor = false;
    private boolean isMoveAllowed = true;
    private boolean hasMoveCursor = false;

    /**
     * Singleton
     */
    private CursorHandler() {
        MapWindow.getInstance().setTerrainMouseMoveListener(this);
    }

    public void setAttackCursor() {
        hasAttackCursor = true;
        for (ClientSyncItemView item : ItemContainer.getInstance().getItems()) {
            if (item instanceof ClientSyncBaseItemView && !((ClientSyncBaseItemView) item).isMyOwnProperty()) {
                setCursor(item, CURSO_ATTACK, Style.Cursor.CROSSHAIR);
            }
        }
    }

    public void removeAttackCursor() {
        hasAttackCursor = false;
        for (ClientSyncItemView item : ItemContainer.getInstance().getItems()) {
            if (item instanceof ClientSyncBaseItemView && !((ClientSyncBaseItemView) item).isMyOwnProperty()) {
                setCursor(item, null, Style.Cursor.POINTER);
            }
        }
    }

    public void setCollectCursor() {
        hasCollectCursor = true;
        for (ClientSyncItemView item : ItemContainer.getInstance().getItems()) {
            if (item instanceof ClientSyncResourceItemView) {
                setCursor(item, CURSO_COLLECT, Style.Cursor.CROSSHAIR);
            }
        }

    }

    public void removeCollectCursor() {
        hasCollectCursor = false;
        for (ClientSyncItemView item : ItemContainer.getInstance().getItems()) {
            if (item.getSyncItem() instanceof SyncResourceItem) {
                setCursor(item, null, Style.Cursor.POINTER);
            }
        }
    }

    public void handleCursorOnNewItems(ClientSyncItemView view) {
        if (view instanceof ClientSyncResourceItemView) {
            if (hasCollectCursor) {
                setCursor(view, CURSO_COLLECT, Style.Cursor.CROSSHAIR);
            } else {
                setCursor(view, null, Style.Cursor.POINTER);
            }
        } else if (view instanceof ClientSyncBaseItemView && !((ClientSyncBaseItemView) view).isMyOwnProperty()) {
            if (hasAttackCursor) {
                setCursor(view, CURSO_ATTACK, Style.Cursor.CROSSHAIR);
            } else {
                setCursor(view, null, Style.Cursor.POINTER);
            }
        } else {
            setCursor(view, null, Style.Cursor.POINTER);
        }
    }

    public void setMoveCursor() {
        GWTCanvas terrain = TerrainView.getInstance().getCanvas();
        setCursor(terrain, CURSO_GO, Style.Cursor.CROSSHAIR);
        hasMoveCursor = true;
    }

    private void setMoveForbiddenCursor() {
        GWTCanvas terrain = TerrainView.getInstance().getCanvas();
        setCursor(terrain, CURSO_NOGO, Style.Cursor.POINTER);
        hasMoveCursor = true;
    }

    public void removeMoveCursor() {
        GWTCanvas terrain = TerrainView.getInstance().getCanvas();
        setCursor(terrain, null, Style.Cursor.POINTER);
        hasMoveCursor = false;
    }

    private void setCursor(Widget widget, String url, Style.Cursor cursor) {
        if (GwtCommon.isOpera() || url == null) {
            widget.getElement().getStyle().setCursor(cursor);
        } else {
            widget.getElement().getStyle().setProperty("cursor", "url(" + url + "), " + cursor.getCssName());
        }
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

    @Override
    public void onMove(int absoluteLeft, int absoluteTop, int relativeLeft, int relativeTop) {
        Collection<SurfaceType> allowedSurfaceTypes = SelectionHandler.getInstance().getOwnSelectionSurfaceTypes();
        SurfaceType surfaceType = TerrainView.getInstance().getTerrainHandler().getSurfaceTypeAbsolute(new Index(absoluteLeft, absoluteTop));
        boolean tmpIsMoveAllowed = allowedSurfaceTypes.contains(surfaceType);
        if (hasMoveCursor && tmpIsMoveAllowed != isMoveAllowed) {
            if (tmpIsMoveAllowed) {
                setMoveCursor();
            } else {
                setMoveForbiddenCursor();
            }
        }
        isMoveAllowed = tmpIsMoveAllowed;
    }
}