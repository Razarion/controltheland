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

package com.btxtech.game.jsre.common;

import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Image;
import com.btxtech.game.jsre.client.terrain.MapWindow;
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.GwtCommon;

/**
 * User: beat
 * Date: 10.01.2010
 * Time: 19:23:21
 */
public abstract class PlaceablePreviewWidget implements MouseMoveHandler, MouseUpHandler, MouseDownHandler {
    private boolean hasMoved = false;
    private Image image;

    protected PlaceablePreviewWidget(Image image, MouseEvent mouseEvent) {
        this.image = image;
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
        int x = event.getClientX()/* - image.getOffsetWidth() / 2*/;
        int y = event.getClientY()/* - image.getOffsetHeight() / 2*/;
        x = specialMoveX(x);
        y = specialMoveY(y);
        RootPanel.get().setWidgetPosition(image, x, y);
        hasMoved = true;
        DOM.setCapture(image.getElement()); //IE6 need this to prevent losing of image
        image.getElement().getStyle().setProperty("cursor", "move");//IE6
        MapWindow.getInstance().onMouseMove(event);
    }

    /**
     * Override in subclass if muve behaves special
     *
     * @param x original input
     * @return special x posisition
     */
    protected int specialMoveX(int x) {
        return x;
    }

    /**
     * Override in subclass if muve behaves special
     *
     * @param y original input
     * @return special y posisition
     */
    protected int specialMoveY(int y) {
        return y;
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

    protected abstract void execute(MouseEvent event);

}
