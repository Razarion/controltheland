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

import com.btxtech.game.jsre.client.ClientSyncItemView;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.terrain.MapWindow;
import com.btxtech.game.jsre.client.terrain.TerrainMouseMoveListener;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.client.territory.ClientTerritoryService;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItemContainer;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Widget;

import java.util.Collection;

// Make an ico file with gimp and rename it to cur

/**
 * User: beat
 * Date: Jun 27, 2009
 * Time: 9:43:48 AM
 */
public class CursorHandler implements TerrainMouseMoveListener {
    private static CursorHandler INSTANCE = new CursorHandler();
    private CursorState cursorState;
    private boolean sellMode = false;

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
        if (cursorState != null) {
            cursorState.setCanUnload(false);
        }
    }

    public void setLaunch() {
        cursorState.setCanLaunch(true);
    }

    public void clearLaunch() {
        if (cursorState != null) {
            cursorState.setCanLaunch(false);
        }
    }

    public void setSell(boolean sellMode) {
        this.sellMode = sellMode;
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

        if (selection.canFinalizeBuild()) {
            cursorState.setCanFinalizeBuild();
        }

        this.cursorState = cursorState;
    }

    public void onSelectionCleared() {
        cursorState = null;
        setTerrainCursor(null, false);
    }

    @Override
    public void onMove(int absoluteLeft, int absoluteTop, int relativeLeft, int relativeTop) {
        if (sellMode) {
            setTerrainCursor(CursorType.SELL, false);
            return;
        }

        if (cursorState == null) {
            setTerrainCursor(null, false);
            return;
        }
        Index position = new Index(absoluteLeft, absoluteTop);

        if (cursorState.isCanUnload()) {
            setTerrainCursor(CursorType.UNLOAD, atLeastOnAllowedForUnload(position));
        } else if (cursorState.isCanLaunch()) {
            setTerrainCursor(CursorType.ATTACK, SelectionHandler.getInstance().atLeastOneAllowedToLaunch(position));
        } else if (cursorState.isCanMove()) {
            Collection<SurfaceType> allowedSurfaceTypes = SelectionHandler.getInstance().getOwnSelectionSurfaceTypes();
            SurfaceType surfaceType = TerrainView.getInstance().getTerrainHandler().getSurfaceTypeAbsolute(position);
            boolean tmpIsMoveAllowed = allowedSurfaceTypes.contains(surfaceType);
            setTerrainCursor(CursorType.GO, tmpIsMoveAllowed);
        }
    }

    private boolean atLeastOnAllowedForUnload(Index position) {
        for (SyncBaseItem syncBaseItem : SelectionHandler.getInstance().getOwnSelection().getSyncBaseItems()) {
            if (syncBaseItem.hasSyncItemContainer()) {
                SyncItemContainer syncItemContainer = syncBaseItem.getSyncItemContainer();
                if (syncItemContainer.atLeastOneAllowedToUnload(position)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setItemCursor(ClientSyncItemView clientSyncItemView, CursorItemState cursorItemState) {
        if (sellMode) {
            setCursor(clientSyncItemView, CursorType.SELL, clientSyncItemView.getClientSyncItem().isMyOwnProperty());
            return;
        }

        if (cursorState == null) {
            setCursor(clientSyncItemView, Style.Cursor.POINTER);
            return;
        }
        Index position = clientSyncItemView.getClientSyncItem().getSyncItem().getSyncItemArea().getPosition();
        if (cursorItemState.isAlliance()) {
            setCursor(clientSyncItemView, CursorType.ALLIANCE, true);
        } else if (cursorState.isCanAttack() && cursorItemState.isAttackTarget()) {
            setCursor(clientSyncItemView, CursorType.ATTACK,
                    SelectionHandler.getInstance().atLeastOneAllowedOnTerrain4Selection()
                            && SelectionHandler.getInstance().atLeastOneAllowedOnTerritory4Selection(position)
                            && SelectionHandler.getInstance().atLeastOneItemTypeAllowed2Attack4Selection((clientSyncItemView).getClientSyncItem().getSyncBaseItem()));
        } else if (cursorState.isCanCollect() && cursorItemState.isCollectTarget()) {
            setCursor(clientSyncItemView, CursorType.COLLECT, SelectionHandler.getInstance().atLeastOneAllowedOnTerritory4Selection(position));
        } else if (cursorState.isCanLoad() && cursorItemState.isLoadTarget() && isNotMyself(clientSyncItemView)) {
            SyncItemContainer syncItemContainer = clientSyncItemView.getClientSyncItem().getSyncBaseItem().getSyncItemContainer();
            boolean allowed = ClientTerritoryService.getInstance().isAllowed(position, clientSyncItemView.getClientSyncItem().getSyncBaseItem())
                    && syncItemContainer.isAbleToLoad(SelectionHandler.getInstance().getOwnSelection().getSyncBaseItems());
            setCursor(clientSyncItemView, CursorType.LOAD, allowed);
        } else if (cursorState.isCanFinalizeBuild() && cursorItemState.isFinalizeBuild()) {
            setCursor(clientSyncItemView, CursorType.FINALIZE_BUILD, SelectionHandler.getInstance().atLeastOneItemTypeAllowed2FinalizeBuild(clientSyncItemView.getClientSyncItem().getSyncBaseItem()));
        } else if (cursorState.isCanLaunch() && cursorItemState.isAttackTarget()) {
            setCursor(clientSyncItemView, CursorType.ATTACK, SelectionHandler.getInstance().atLeastOneAllowedToLaunch(position));
        } else if (cursorState.isCanMove() && cursorItemState.isBoxTarget()) {
            setCursor(clientSyncItemView, CursorType.PICKUP, SelectionHandler.getInstance().atLeastOneAllowedOnTerritory4Selection(position));
        } else {
            setCursor(clientSyncItemView, Style.Cursor.POINTER);
        }
    }

    private boolean isNotMyself(ClientSyncItemView clientSyncItemView) {
        SyncBaseItem my = clientSyncItemView.getClientSyncItem().getSyncBaseItem();
        for (SyncBaseItem syncBaseItem : SelectionHandler.getInstance().getOwnSelection().getSyncBaseItems()) {
            if (syncBaseItem.equals(my)) {
                return false;
            }
        }
        return true;
    }

    private void setTerrainCursor(CursorType cursorType, boolean allowed) {
        if (cursorType != null) {
            setCursor(MapWindow.getAbsolutePanel(), cursorType, allowed);
        } else {
            setCursor(MapWindow.getAbsolutePanel(), Style.Cursor.DEFAULT);
        }
    }

    private void setCursor(Widget widget, Style.Cursor cursor) {
        widget.getElement().getStyle().setCursor(cursor);
    }

    private void setCursor(Widget widget, CursorType cursorType, boolean allowed) {
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