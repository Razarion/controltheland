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
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.action.ActionHandler;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.terrain.MapWindow;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.client.territory.ClientTerritoryService;
import com.btxtech.game.jsre.common.PlaceablePreviewWidget;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
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
        try {
            ActionHandler.getInstance().build(group.getItems(), positionToBuilt, itemTypeToBuilt);
        } catch (NoSuchItemTypeException e) {
            GwtCommon.handleException(e);
        }
    }

    @Override
    protected int specialMoveX(int x) {
        if (itemTypeToBuilt != null) {
            return x -= itemTypeToBuilt.getBoundingBox().getEffectiveWidth() / 2;
        } else {
            return x -= getImage().getWidth() / 2;
        }
    }

    @Override
    protected int specialMoveY(int y) {
        if (itemTypeToBuilt != null) {
            return y -= itemTypeToBuilt.getBoundingBox().getEffectiveHeight() / 2;
        } else {
            return y -= getImage().getHeight() / 2;
        }
    }

    @Override
    protected boolean allowedToPlace(int relX, int relY) {
        int offsetX = itemTypeToBuilt.getBoundingBox().getEffectiveWidth() / 2;
        int offsetY = itemTypeToBuilt.getBoundingBox().getEffectiveHeight() / 2;
        Index relative = Index.createSaveIndex(relX, relY).add(offsetX, offsetY);

        // Check if over cockpit
        if (itemTypeToBuilt.getBoundingBox().contains(relative, SideCockpit.getInstance().getArea())) {
            return false;
        }

        Index absolute = TerrainView.getInstance().toAbsoluteIndex(relative);

        // Check terrain
        if (!ClientServices.getInstance().getTerrainService().isFree(absolute, itemTypeToBuilt)) {
            return false;
        }

        // Check if Item allowed to play in territory
        if (!ClientTerritoryService.getInstance().isAllowed(absolute, itemTypeToBuilt)) {
            return false;
        }

        // Check if builder is allowed to build in territory
        if (!ClientTerritoryService.getInstance().isAtLeastOneAllowed(absolute, group.getSyncBaseItems())) {
            return false;
        }

        // Check items
        return !ItemContainer.getInstance().isUnmovableSyncItemOverlapping(itemTypeToBuilt.getBoundingBox(), absolute);
    }
}
