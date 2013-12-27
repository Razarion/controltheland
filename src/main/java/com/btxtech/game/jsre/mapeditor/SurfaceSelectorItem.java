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
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceImage;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;

/**
 * User: beat
 * Date: 14.04.2010
 * Time: 11:20:39
 */
public class SurfaceSelectorItem extends FlowPanel implements MouseDownHandler {
    private SurfaceImage surfaceImage;
    private MapEditorModel mapEditorModel;

    public SurfaceSelectorItem(SurfaceImage surfaceImage, MapEditorModel mapEditorModel) {
        this.surfaceImage = surfaceImage;
        this.mapEditorModel = mapEditorModel;
        Image image = ImageHandler.getSurfaceImage(surfaceImage.getImageId());
        add(image);
        image.addMouseDownHandler(this);
        setSelected(false);
        image.setPixelSize(100,100);
    }

    @Override
    public void onMouseDown(MouseDownEvent mouseDownEvent) {
        GwtCommon.preventDefault(mouseDownEvent);
        mapEditorModel.createSurfaceRect(surfaceImage.getImageId(), mouseDownEvent);
    }

    public void setSelected(boolean selected) {
        if (selected) {
            setStyleName("tile-selector-item-selected");
        } else {
            setStyleName("tile-selector-item-unselected");
        }
    }

}
