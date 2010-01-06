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

import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.action.ActionHandler;
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.terrain.MapWindow;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * User: beat
 * Date: Jul 22, 2009
 * Time: 1:01:13 PM
 */
public class PlaceablePreviewWidget implements MouseMoveHandler, MouseUpHandler, MouseDownHandler {
    private Image image;
    private Group group;
    private BaseItemType itemTypeToBuilt;
    private boolean hasMoved = false;

    public PlaceablePreviewWidget(Image image, MouseEvent mouseEvent, Group group, BaseItemType itemTypeToBuilt) {
        this.image = image;
        this.group = group;
        this.itemTypeToBuilt = itemTypeToBuilt;
        DOM.setCapture(image.getElement());
        int x = mouseEvent.getClientX() - image.getWidth() / 2;
        int y = mouseEvent.getClientY() - image.getHeight() / 2;
        RootPanel.get().add(image, x, y);
        image.addMouseMoveHandler(this);
        image.addMouseUpHandler(this);
        image.addMouseDownHandler(this);
        image.getElement().getStyle().setZIndex(Constants.Z_INDEX_PLACEABLE_PREVIEW);
        if (!GwtCommon.isIe6()) {
            image.getElement().getStyle().setProperty("filter", "alpha(opacity=50)");
        }
        image.getElement().getStyle().setProperty("opacity", "0.5");
        image.getElement().getStyle().setProperty("cursor", "move");
    }

    public void close() {
        DOM.releaseCapture(image.getElement());
        RootPanel.get().remove(image);
    }

    @Override
    public void onMouseMove(MouseMoveEvent event) {
        int x = event.getClientX() - image.getOffsetWidth() / 2;
        int y = event.getClientY() - image.getOffsetHeight() / 2;
        RootPanel.get().setWidgetPosition(image, x, y);
        hasMoved = true;
        DOM.setCapture(image.getElement()); //IE6 need this to prevent losing of image
        image.getElement().getStyle().setProperty("cursor", "move");//IE6
        MapWindow.getInstance().onMouseMove(event);
    }

    @Override
    public void onMouseUp(MouseUpEvent event) {
        if (hasMoved) {
            close();
            execute(event);
        }
    }

    @Override
    public void onMouseDown(MouseDownEvent event) {
        close();
        execute(event);
    }

    private void execute(MouseEvent event) {
        int absX = TerrainView.getInstance().getViewOriginLeft() + event.getRelativeX(MapWindow.getAbsolutePanel().getElement());
        int absY = TerrainView.getInstance().getViewOriginTop() + event.getRelativeY(MapWindow.getAbsolutePanel().getElement());
        if (absX < 0 || absY < 0) {
            return;
        }
        Index positionToBuilt = new Index(absX, absY);
        ActionHandler.getInstance().buildFactory(group.getItems(), positionToBuilt, itemTypeToBuilt);
    }

}
