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

package com.btxtech.game.jsre.mapeditor;

import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.client.terrain.MapWindow;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.PlaceablePreviewWidget;
import com.google.gwt.event.dom.client.MouseEvent;

/**
 * User: beat
 * Date: 10.01.2010
 * Time: 19:37:39
 */
public class PlaceablePreviewTerrainImagePoition extends PlaceablePreviewWidget {
    private int imageId;

    protected PlaceablePreviewTerrainImagePoition(int imageId, MouseEvent mouseEvent) {
        super(ImageHandler.getTerrainImage(imageId), mouseEvent);
        this.imageId = imageId;
    }

    @Override
    protected void execute(MouseEvent event) {
        int relX = event.getRelativeX(MapWindow.getAbsolutePanel().getElement());
        int relY = event.getRelativeY(MapWindow.getAbsolutePanel().getElement());
        if (relX < 0 || relY < 0) {
            return;
        }
        TerrainView.getInstance().addNewTerrainImage(relX, relY, imageId);
    }
}
