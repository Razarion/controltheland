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
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.terrain.MapWindow;
import com.btxtech.game.jsre.client.terrain.TerrainMouseMoveListener;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.client.territory.ClientTerritoryService;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceType;
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
    private static CursorHandler INSTANCE = new CursorHandler();
    private CursorState cursorState;

    /**
     * Singleton
     */
    private CursorHandler() {
        MapWindow.getInstance().setTerrainMouseMoveListener(this);
    }

    public static CursorHandler getInstance() {
        return INSTANCE;
    }

    public void setUnloadContainer() {
        cursorState.setCanUnload(true);
    }

    public void clearUnloadContainer() {
        cursorState.setCanUnload(false);
    }

    public void onOwnSelectionChanged(Group selection) {
        CursorState cursorState = new CursorState();
        if (selection.canMove()) {
            cursorState.setCanMove();
            cursorState.setCanLoad();
        }

        if (selection.canAttack()) {
            cursorState.setCanAttack();
        }

        if (selection.canCollect()) {
            cursorState.setCanCollect();
        }

        this.cursorState = cursorState;
    }

    public void onSelectionCleared() {
        cursorState = null;
    }

    @Override
    public void onMove(int absoluteLeft, int absoluteTop, int relativeLeft, int relativeTop) {
        if (cursorState == null) {
            setTerrainCursor(null, false);
            return;
        }
        Index position = new Index(absoluteLeft, absoluteTop);
        Collection<SurfaceType> allowedSurfaceTypes = SelectionHandler.getInstance().getOwnSelectionSurfaceTypes();
        SurfaceType surfaceType = TerrainView.getInstance().getTerrainHandler().getSurfaceTypeAbsolute(position);
        boolean tmpIsMoveAllowed = allowedSurfaceTypes.contains(surfaceType);

        if (cursorState.isCanUnload()) {
            // TODO unload cursor also depends on the loaded items surface type
            setTerrainCursor(CursorType.UNLOAD, SelectionHandler.getInstance().atLeastOneAllowedOnTerrain4Selection(position) && tmpIsMoveAllowed);
        } else if (cursorState.isCanMove()) {
            setTerrainCursor(CursorType.GO, tmpIsMoveAllowed);
        }
    }

    public void setItemCursor(ClientSyncItemView clientSyncItemView, CursorItemState cursorItemState, Index position) {
        if (cursorState == null) {
            setCursor(clientSyncItemView, null, false);
            return;
        }
        if (cursorState.isCanAttack() && cursorItemState.isAttackTarget()) {
            setCursor(clientSyncItemView, CursorType.ATTACK, SelectionHandler.getInstance().atLeastOneAllowedOnTerrain4Selection(position) && SelectionHandler.getInstance().atLeastOneItemTypeAllowed2Attack4Selection(((ClientSyncBaseItemView) clientSyncItemView).getSyncBaseItem()));
        } else if (cursorState.isCanCollect() && cursorItemState.isCollectTarget()) {
            setCursor(clientSyncItemView, CursorType.COLLECT, SelectionHandler.getInstance().atLeastOneAllowedOnTerrain4Selection(position));
        } else if (cursorState.isCanLoad() && cursorItemState.isLoadTarget()) {
            setCursor(clientSyncItemView, CursorType.LOAD, ClientTerritoryService.getInstance().isAllowed(position, ((ClientSyncBaseItemView) clientSyncItemView).getSyncBaseItem()));
        }
    }

    private void setTerrainCursor(CursorType cursorType, boolean allowed) {
        GWTCanvas terrain = TerrainView.getInstance().getCanvas();
        setCursor(terrain, cursorType, allowed);
    }

    private void setCursor(Widget widget, CursorType cursorType, boolean allowed) {
        if (cursorType == null) {
            widget.getElement().getStyle().setCursor(Style.Cursor.POINTER);
            return;
        }

        String url;
        Style.Cursor alternativeDefault;
        if (allowed) {
            url = cursorType.getUrl();
            alternativeDefault = cursorType.getAlternativeDefault();
        } else {
            url = cursorType.getNoUrl();
            alternativeDefault = cursorType.getNoAlternativeDefault();
        }

        if (GwtCommon.isOpera() || url == null) {
            widget.getElement().getStyle().setCursor(alternativeDefault);
        } else {
            widget.getElement().getStyle().setProperty("cursor", "url(" + url + "), " + alternativeDefault.getCssName());
        }

    }

}