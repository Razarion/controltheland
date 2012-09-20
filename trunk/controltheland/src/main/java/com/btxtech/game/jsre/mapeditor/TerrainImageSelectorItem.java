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
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
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
    private static final int MAX_EDGE_LENGTH = 100;
    private TerrainImage terrainImage;
    private Cockpit cockpit;
    private MapEditorModel mapEditorModel;

    public TerrainImageSelectorItem(TerrainImage terrainImage, Cockpit cockpit, MapEditorModel mapEditorModel) {
        this.terrainImage = terrainImage;
        this.cockpit = cockpit;
        this.mapEditorModel = mapEditorModel;
        Image image = new Image();
        image.addLoadHandler(new LoadHandler() {
            @Override
            public void onLoad(LoadEvent event) {
                Image image = (Image) event.getSource();
                if (image.getWidth() > image.getHeight()) {
                    if (image.getWidth() > MAX_EDGE_LENGTH) {
                        image.setWidth(Integer.toString(MAX_EDGE_LENGTH) + "px");
                    }
                } else {
                    if (image.getHeight() > MAX_EDGE_LENGTH) {
                        image.setHeight(Integer.toString(MAX_EDGE_LENGTH) + "px");
                    }
                }
            }
        });
        image.setUrl(ImageHandler.getTerrainImageUrl(terrainImage.getId()));
        add(image);
        image.addMouseDownHandler(this);
        setSelected(false);
        getElement().getStyle().setBackgroundColor("#888888");
        setPixelSize(MAX_EDGE_LENGTH, MAX_EDGE_LENGTH);
    }

    @Override
    public void onMouseDown(MouseDownEvent mouseDownEvent) {
        GwtCommon.preventDefault(mouseDownEvent);
        mapEditorModel.createTerrainImagePosition(terrainImage, mouseDownEvent);
    }

    public void setSelected(boolean selected) {
        if (selected) {
            setStyleName("tile-selector-item-selected");
        } else {
            setStyleName("tile-selector-item-unselected");
        }
    }
}
