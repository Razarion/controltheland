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

import com.btxtech.game.jsre.client.action.ActionHandler;
import com.btxtech.game.jsre.client.common.Index;
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
}
