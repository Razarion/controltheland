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

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBoxItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItemContainer;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncResourceItem;
import com.google.gwt.dom.client.Style;

import java.util.Collection;

/**
 * User: beat
 * Date: Jun 27, 2009
 * Time: 9:43:48 AM
 * <p/>
 * Make an ico file with gimp and rename it to cur
 * Make cur file with RealWorld Cursor Editor 2012.1: http://www.rw-designer.com/cursor-maker
 */
public class CursorHandler {
    private static CursorHandler INSTANCE = new CursorHandler();

    /**
     * Singleton
     */
    private CursorHandler() {
    }

    public static CursorHandler getInstance() {
        return INSTANCE;
    }


    public void onSelectionCleared() {
        setTerrainCursor(null, false);
    }

    public void noCursor() {
        setTerrainCursor(null, false);
    }

    public void handleMouseMove(SyncItem syncItem, int absoluteX, int absoluteY) {
        if (syncItem != null) {
            handleItemCursor(syncItem);
        } else {
            handleTerrainCursor(absoluteX, absoluteY);
        }
    }

    private void handleTerrainCursor(int absoluteLeft, int absoluteTop) {
        Index position = new Index(absoluteLeft, absoluteTop);
        if (CockpitMode.getInstance().getMode() == CockpitMode.Mode.UNLOAD) {
            setTerrainCursor(CursorType.UNLOAD, atLeastOnAllowedForUnload(position));
        } else if (CockpitMode.getInstance().getMode() == CockpitMode.Mode.LAUNCH) {
            setTerrainCursor(CursorType.ATTACK, SelectionHandler.getInstance().atLeastOneAllowedToLaunch(position));
        } else if (CockpitMode.getInstance().isMovePossible()) {
            Collection<SurfaceType> allowedSurfaceTypes = SelectionHandler.getInstance().getOwnSelectionSurfaceTypes();
            SurfaceType surfaceType = TerrainView.getInstance().getTerrainHandler().getSurfaceTypeAbsolute(position);
            boolean tmpIsMoveAllowed = allowedSurfaceTypes.contains(surfaceType);
            setTerrainCursor(CursorType.GO, tmpIsMoveAllowed);
        } else {
            setCursor(Style.Cursor.DEFAULT);
        }
    }

    private void handleItemCursor(SyncItem syncItem) {
        Index position = syncItem.getSyncItemArea().getPosition();
        if (CockpitMode.getInstance().getMode() == CockpitMode.Mode.SELL) {
            if (syncItem instanceof SyncBaseItem) {
                setCursor(CursorType.SELL, ClientBase.getInstance().isMyOwnProperty((SyncBaseItem) syncItem));
            } else {
                setCursor(Style.Cursor.DEFAULT);
            }
            return;
        } else if (CockpitMode.getInstance().getMode() == CockpitMode.Mode.LAUNCH) {
            setTerrainCursor(CursorType.ATTACK, SelectionHandler.getInstance().atLeastOneAllowedToLaunch(position));
            return;
        }

        if (syncItem instanceof SyncBaseItem) {
            SyncBaseItem syncBaseItem = (SyncBaseItem) syncItem;
            if (ClientBase.getInstance().isMyOwnProperty(syncBaseItem)) {
                if (CockpitMode.getInstance().isLoadPossible() && syncBaseItem.hasSyncItemContainer() && isNotMyself(syncBaseItem)) {
                    SyncItemContainer syncItemContainer = syncBaseItem.getSyncItemContainer();
                    boolean allowed = syncItemContainer.atLeastOneAllowedToLoad(SelectionHandler.getInstance().getOwnSelection().getSyncBaseItems());
                    setCursor(CursorType.LOAD, allowed);
                } else if (CockpitMode.getInstance().isFinalizeBuildPossible() && !syncBaseItem.isReady() && isNotMyself(syncBaseItem)) {
                    setCursor(CursorType.FINALIZE_BUILD, SelectionHandler.getInstance().atLeastOneItemTypeAllowed2FinalizeBuild(syncBaseItem));
                } else {
                    setCursor(Style.Cursor.POINTER);
                }
            } else if (ClientBase.getInstance().isEnemy(syncBaseItem)) {
                if (CockpitMode.getInstance().isAttackPossible()) {
                    setCursor(CursorType.ATTACK, SelectionHandler.getInstance().atLeastOneItemTypeAllowed2Attack4Selection(syncBaseItem));
                } else {
                    setCursor(Style.Cursor.POINTER);
                }
            } else {
                if (CockpitMode.getInstance().isAttackPossible()) {
                    setCursor(CursorType.GUILD_MEMBER, false);
                } else {
                    setCursor(Style.Cursor.POINTER);
                }
            }
        } else if (CockpitMode.getInstance().isCollectPossible() && syncItem instanceof SyncResourceItem) {
            setCursor(CursorType.COLLECT, true);
        } else if (CockpitMode.getInstance().isMovePossible() && syncItem instanceof SyncBoxItem) {
            setCursor(CursorType.PICKUP, true);
        } else {
            setCursor(Style.Cursor.POINTER);
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

    private boolean isNotMyself(SyncBaseItem me) {
        for (SyncBaseItem syncBaseItem : SelectionHandler.getInstance().getOwnSelection().getSyncBaseItems()) {
            if (syncBaseItem.equals(me)) {
                return false;
            }
        }
        return true;
    }

    private void setTerrainCursor(CursorType cursorType, boolean allowed) {
        if (cursorType != null) {
            setCursor(cursorType, allowed);
        } else {
            setCursor(Style.Cursor.DEFAULT);
        }
    }

    private void setCursor(Style.Cursor cursor) {
        TerrainView.getInstance().getCanvas().getElement().getStyle().setCursor(cursor);
    }

    private void setCursor(CursorType cursorType, boolean allowed) {
        String url;
        Style.Cursor alternativeDefault;
        int hotSpotX;
        int hotSpotY;
        if (allowed) {
            url = cursorType.getUrl();
            alternativeDefault = cursorType.getAlternativeDefault();
            hotSpotX = cursorType.getHotSpotX();
            hotSpotY = cursorType.getHotSpotY();
        } else {
            url = cursorType.getNoUrl();
            alternativeDefault = cursorType.getNoAlternativeDefault();
            hotSpotX = cursorType.getHotSpotNoX();
            hotSpotY = cursorType.getHotSpotNoY();
        }

        if (GwtCommon.isOpera() || url == null) {
            TerrainView.getInstance().getCanvas().getElement().getStyle().setCursor(alternativeDefault);
        } else if (GwtCommon.isIE()) {
            TerrainView.getInstance().getCanvas().getElement().getStyle().setProperty("cursor", "url(" + url + "), " + alternativeDefault.getCssName());
        } else {
            TerrainView.getInstance().getCanvas().getElement().getStyle().setProperty("cursor", "url(" + url + ") " + hotSpotX + " " + hotSpotY + ", " + alternativeDefault.getCssName());
        }

    }
}