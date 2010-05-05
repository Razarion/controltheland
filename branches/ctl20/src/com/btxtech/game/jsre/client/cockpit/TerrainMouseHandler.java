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

import com.btxtech.game.jsre.client.Game;
import com.btxtech.game.jsre.client.action.ActionHandler;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.terrain.TerrainMouseButtonListener;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.client.utg.ClientUserTracker;
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
        ClientUserTracker.getInstance().onMouseDownTerrain(absoluteX, absoluteY);
        if (mouseDownEvent.getNativeButton() == NativeEvent.BUTTON_LEFT) {
            new GroupSelectionFrame(mouseDownEvent.getX(), mouseDownEvent.getY(), this);
        } else if (mouseDownEvent.getNativeButton() == NativeEvent.BUTTON_RIGHT) {
            SelectionHandler.getInstance().clearSelection();
        }
    }

    @Override
    public void onMouseUp(int absoluteX, int absoluteY, MouseUpEvent event) {
        ClientUserTracker.getInstance().onMouseUpTerrain(absoluteX, absoluteY);
        if (event.getNativeButton() == NativeEvent.BUTTON_LEFT) {
            if (Game.cockpitPanel.isSelectUnloadMode()) {
                executeUnloadContainerCommand(absoluteX, absoluteY);
                Game.cockpitPanel.clearSelectUnloadMode();
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

        ActionHandler.getInstance().unloadContainer(selection.getFirst().getSyncBaseItem(), new Index(absoluteX, absoluteY));
    }

    private void executeMoveCommand(int absoluteX, int absoluteY) {
        Group selection = SelectionHandler.getInstance().getOwnSelection();
        if (selection == null) {
            return;
        }

        if (!selection.canMove()) {
            return;
        }

        ActionHandler.getInstance().move(selection.getMovableItems(), new Index(absoluteX, absoluteY));
    }

    public static TerrainMouseHandler getInstance() {
        return INSTANCE;
    }

}