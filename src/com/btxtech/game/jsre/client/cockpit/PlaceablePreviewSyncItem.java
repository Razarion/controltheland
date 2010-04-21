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

import com.btxtech.game.jsre.client.ClientServices;
import com.btxtech.game.jsre.client.Game;
import com.btxtech.game.jsre.client.action.ActionHandler;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.terrain.MapWindow;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.PlaceablePreviewWidget;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.user.client.ui.Image;

/**
 * User: beat
 * Date: Jul 22, 2009
 * Time: 1:01:13 PM
 */
public class PlaceablePreviewSyncItem extends PlaceablePreviewWidget {
    private Group group;
    private BaseItemType itemTypeToBuilt;

    public PlaceablePreviewSyncItem(Image image, MouseEvent mouseEvent, Group group, BaseItemType itemTypeToBuilt) {
        super(image, mouseEvent);
        this.group = group;
        this.itemTypeToBuilt = itemTypeToBuilt;

    }

    protected void execute(MouseEvent event) {
        int absX = TerrainView.getInstance().getViewOriginLeft() + event.getRelativeX(MapWindow.getAbsolutePanel().getElement());
        int absY = TerrainView.getInstance().getViewOriginTop() + event.getRelativeY(MapWindow.getAbsolutePanel().getElement());
        if (absX < 0 || absY < 0) {
            return;
        }
        Index positionToBuilt = new Index(absX, absY);
        ActionHandler.getInstance().buildFactory(group.getItems(), positionToBuilt, itemTypeToBuilt);
    }

    @Override
    protected int specialMoveX(int x) {
        if (itemTypeToBuilt != null) {
            return x -= itemTypeToBuilt.getWidth() / 2;
        } else {
            return x -= getImage().getWidth() / 2;
        }
    }

    @Override
    protected int specialMoveY(int y) {
        if (itemTypeToBuilt != null) {
            return y -= itemTypeToBuilt.getHeight() / 2;
        } else {
            return y -= getImage().getHeight() / 2;
        }
    }

    @Override
    protected boolean allowedToPlace(int relX, int relY) {
        // Check if over cockpit
        if (Game.cockpitPanel.isInside(relX + itemTypeToBuilt.getHeight() / 2, relY + itemTypeToBuilt.getWidth() / 2)) {
            return false;
        }

        // Check terrain
        int absX = relX + TerrainView.getInstance().getViewOriginLeft();
        int absY = relY + TerrainView.getInstance().getViewOriginTop();
        if (absX < 0 || absY < 0) {
            return false;
        }
        int terrainX = absX + itemTypeToBuilt.getWidth() / 2;
        int terrainY = absY + itemTypeToBuilt.getHeight() / 2;
        if (!ClientServices.getInstance().getTerrainService().isFree(new Index(terrainX, terrainY), itemTypeToBuilt)) {
            return false;
        }

        // Check items
        Rectangle itemRect = new Rectangle(absX, absY, itemTypeToBuilt.getWidth(), itemTypeToBuilt.getHeight());
        return ItemContainer.getInstance().hasBuildingsInRect(itemRect);
    }
}
