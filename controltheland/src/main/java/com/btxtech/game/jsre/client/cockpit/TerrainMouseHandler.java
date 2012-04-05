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
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.action.ActionHandler;
import com.btxtech.game.jsre.client.cockpit.item.ItemCockpit;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.terrain.TerrainMouseButtonListener;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.client.territory.ClientTerritoryService;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;

/**
 * User: beat
 * Date: May 19, 2009
 * Time: 9:40:11 PM
 */
public class TerrainMouseHandler implements TerrainMouseButtonListener {
    private final static TerrainMouseHandler INSTANCE = new TerrainMouseHandler();

    /**
     * Singleton
     */
    private TerrainMouseHandler() {
        TerrainView.getInstance().setTerrainMouseButtonListener(this);
    }

    @Override
    public void onMouseDown(int absoluteX, int absoluteY, MouseDownEvent mouseDownEvent) {
        ItemCockpit.getInstance().deActivate();
        if (SelectionHandler.getInstance().isSellMode()) {
            return;
        }
        if (mouseDownEvent.getNativeButton() == NativeEvent.BUTTON_LEFT) {
            new GroupSelectionFrame(mouseDownEvent.getX(), mouseDownEvent.getY(), this);
        } else if (mouseDownEvent.getNativeButton() == NativeEvent.BUTTON_RIGHT) {
            SelectionHandler.getInstance().clearSelection();
        }
        GwtCommon.preventDefault(mouseDownEvent);
        ChatCockpit.getInstance().blurFocus();
    }

    @Override
    public void onMouseUp(int absoluteX, int absoluteY, MouseUpEvent event) {
        if (event.getNativeButton() == NativeEvent.BUTTON_LEFT) {
            if (SideCockpit.getInstance().getCockpitMode().isUnloadMode()) {
                executeUnloadContainerCommand(absoluteX, absoluteY);
                SideCockpit.getInstance().getCockpitMode().clearUnloadMode();
                ItemCockpit.getInstance().deActivate();
            } else if (SideCockpit.getInstance().getCockpitMode().isLaunchMode()) {
                ActionHandler.getInstance().executeLaunchCommand(absoluteX, absoluteY);
            } else {
                executeMoveCommand(absoluteX, absoluteY);
            }
        }
    }

    private void executeUnloadContainerCommand(int absoluteX, int absoluteY) {
        Group selection = SelectionHandler.getInstance().getOwnSelection();
        if (selection == null) {
            return;
        }

        if (selection.getCount() != 1) {
            return;
        }

        ClientSyncItem clientSyncItem = selection.getFirst();
        SyncBaseItem syncBaseItem = clientSyncItem.getSyncBaseItem();
        Index position = new Index(absoluteX, absoluteY);
        if (!ClientTerritoryService.getInstance().isAllowed(position, syncBaseItem)) {
            return;
        }

        ActionHandler.getInstance().unloadContainer(clientSyncItem, position);
    }

    private void executeMoveCommand(int absoluteX, int absoluteY) {
        Group selection = SelectionHandler.getInstance().getOwnSelection();
        if (selection == null) {
            return;
        }

        if (!selection.canMove()) {
            return;
        }

        ActionHandler.getInstance().move(selection.getItems(), new Index(absoluteX, absoluteY));
    }


    public static TerrainMouseHandler getInstance() {
        return INSTANCE;
    }

}