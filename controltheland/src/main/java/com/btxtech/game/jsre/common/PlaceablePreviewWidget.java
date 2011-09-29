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

import com.btxtech.game.jsre.client.ColorConstants;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.terrain.MapWindow;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * User: beat
 * Date: 10.01.2010
 * Time: 19:23:21
 */
public abstract class PlaceablePreviewWidget extends AbsolutePanel implements MouseMoveHandler, MouseUpHandler, MouseDownHandler {
    private boolean hasMoved = false;
    private Image image;
    private Canvas marker;
    private FocusPanel focusPanel;

    protected PlaceablePreviewWidget(Image image, MouseEvent mouseEvent) {
        this.image = image;
        DOM.setCapture(getElement());
        int x = mouseEvent.getRelativeX(MapWindow.getAbsolutePanel().getElement())/* - image.getWidth() / 2*/;
        int y = mouseEvent.getRelativeY(MapWindow.getAbsolutePanel().getElement())/* - image.getHeight() / 2*/;
        x = specialMoveX(x);
        y = specialMoveY(y);
        RootPanel.get().add(this, x, y);
        addMouseMoveHandler(this);
        addMouseUpHandler(this);
        addMouseDownHandler(this);
        image.getElement().getStyle().setZIndex(2);
        add(image);
        getElement().getStyle().setZIndex(Constants.Z_INDEX_PLACEABLE_PREVIEW);
        if (!GwtCommon.isIe6()) {
            image.getElement().getStyle().setProperty("filter", "alpha(opacity=50)");
        }
        image.getElement().getStyle().setProperty("opacity", "0.5");
        image.getElement().getStyle().setProperty("cursor", "move");
        setupMarker();
        addEscKeyHandler();
    }

    private void addEscKeyHandler() {
        focusPanel = new FocusPanel();
        focusPanel.setPixelSize(1, 1);
        focusPanel.getElement().getStyle().setBorderWidth(0, Style.Unit.PX);
        add(focusPanel);
        focusPanel.addKeyDownHandler(new KeyDownHandler() {
            @Override
            public void onKeyDown(KeyDownEvent keyDownEvent) {
                if (keyDownEvent.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
                    close();
                }
            }
        });
        focusPanel.setFocus(true);
    }

    private void setupMarker() {
        marker = Canvas.createIfSupported();
        if (marker == null) {
            throw new Html5NotSupportedException("PlaceablePreviewWidget: Canvas not supported.");
        }
        marker.setCoordinateSpaceWidth(image.getOffsetWidth());
        marker.setCoordinateSpaceHeight(image.getOffsetHeight());
        Context2d context2d = marker.getContext2d();
        marker.getElement().getStyle().setZIndex(1);
        add(marker, 0, 0);
        marker.setVisible(false);
        context2d.setStrokeStyle(ColorConstants.RED);
        context2d.setLineWidth(10);
        context2d.beginPath();
        context2d.moveTo(0, 0);
        context2d.lineTo(image.getOffsetWidth() - 1, image.getOffsetHeight() - 1);
        context2d.moveTo(0, image.getOffsetHeight() - 1);
        context2d.lineTo(image.getOffsetWidth() - 1, 0);
        context2d.stroke();
    }

    public void close() {
        focusPanel.setFocus(false);
        DOM.releaseCapture(getElement());
        RootPanel.get().remove(this);
    }

    @Override
    public void onMouseMove(MouseMoveEvent event) {
        int x = event.getRelativeX(MapWindow.getAbsolutePanel().getElement())/* - image.getOffsetWidth() / 2*/;
        int y = event.getRelativeY(MapWindow.getAbsolutePanel().getElement())/* - image.getOffsetHeight() / 2*/;
        x = specialMoveX(x);
        y = specialMoveY(y);
        RootPanel.get().setWidgetPosition(this, x, y);
        hasMoved = true;
        DOM.setCapture(this.getElement()); //IE6 need this to prevent losing of image
        getElement().getStyle().setProperty("cursor", "move");//IE6
        MapWindow.getInstance().onMouseMove(event);
        if (allowedToPlace(x, y)) {
            marker.setVisible(false);
        } else {
            marker.setVisible(true);
        }
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

    /**
     * Ovberride in subclass
     *
     * @param relX relative x Offset
     * @param relY relative y Offset
     * @return true of allowed to place
     */
    protected boolean allowedToPlace(int relX, int relY) {
        return true;
    }

    @Override
    public void onMouseUp(MouseUpEvent event) {
        int x = event.getRelativeX(MapWindow.getAbsolutePanel().getElement());
        int y = event.getRelativeY(MapWindow.getAbsolutePanel().getElement());
        x = specialMoveX(x);
        y = specialMoveY(y);
        if (hasMoved && allowedToPlace(x, y)) {
            close();
            execute(event);
        }
    }

    @Override
    public void onMouseDown(MouseDownEvent event) {
        GwtCommon.preventDefault(event);
    }

    protected abstract void execute(MouseEvent event);

    private HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
        return addDomHandler(handler, MouseMoveEvent.getType());
    }

    private HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
        return addDomHandler(handler, MouseDownEvent.getType());
    }

    private HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
        return addDomHandler(handler, MouseUpEvent.getType());
    }

    public Image getImage() {
        return image;
    }
}