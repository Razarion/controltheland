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

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.btxtech.game.jsre.client.ImageHandler;

/**
 * User: beat
 * Date: Sep 2, 2009
 * Time: 10:07:13 PM
 */
public class TileSelectorItem extends FlowPanel implements MouseDownHandler {
    private TileSelector tileSelector;
    private int imageId;
    private Image image;

    public TileSelectorItem(TileSelector tileSelector, int imageId) {
        this.tileSelector = tileSelector;
        this.imageId = imageId;
        image = ImageHandler.getTerrainImage(imageId);
        add(image);
        image.addMouseDownHandler(this);
        setSelected(false);
        image.setPixelSize(100,100);
    }

    @Override
    public void onMouseDown(MouseDownEvent mouseDownEvent) {
       tileSelector.onSelectionChanged(this); 
    }

    public void setSelected(boolean selected) {
        if(selected) {
            setStyleName("tile-selector-item-selected");
        } else {
            setStyleName("tile-selector-item-unselected");
        }
    }

    public int getImageId() {
        return imageId;
    }
}
