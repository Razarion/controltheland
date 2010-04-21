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

import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImage;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;

/**
 * User: beat
 * Date: Sep 2, 2009
 * Time: 10:07:13 PM
 */
public class TerrainImageSelectorItem extends FlowPanel implements MouseDownHandler {
    private TerrainImage terrainImage;

    public TerrainImageSelectorItem(TerrainImage terrainImage) {
        this.terrainImage = terrainImage;
        Image image = ImageHandler.getTerrainImage(terrainImage.getId());
        add(image);
        image.addMouseDownHandler(this);
        setSelected(false);
        image.setPixelSize(100, 100);
    }

    @Override
    public void onMouseDown(MouseDownEvent mouseDownEvent) {
        GwtCommon.preventImageDragging(mouseDownEvent);
        new PlaceablePreviewTerrainImagePoition(terrainImage, mouseDownEvent);
    }

    public void setSelected(boolean selected) {
        if (selected) {
            setStyleName("tile-selector-item-selected");
        } else {
            setStyleName("tile-selector-item-unselected");
        }
    }
}
